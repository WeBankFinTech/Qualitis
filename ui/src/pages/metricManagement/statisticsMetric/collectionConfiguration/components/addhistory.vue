<template>
    <div class="wrap rule-detail-form edit">
        <div class="header">
            {{$t('_.发布组')}}{{index.toString().padStart(2, '0')}}
            <span v-if="removable" class="delete-btn" @click="deleteInputMeta">
                <MinusCircleOutlined style="margin-right: 4.67px;" />{{$t('_.删除')}}</span>
        </div>
        <FForm
            ref="scheduleFormRef"
            :labelWidth="66"
            :labelPosition="'right'"
            :model="inputHistoryConfigData"
            :rules="inputMetaRules">
            <FFormItem prop="cluster_name" :label="$t('_.集群')">
                <FSelect
                    v-model="inputHistoryConfigData.cluster_name"
                    filterable
                    clearable
                    :placeholder="$t('_.请选择')"
                    :options="clusterVivisions"
                    @change="clusterchange"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="db_name" :label="$t('_.数据库')">
                <FSelect
                    v-model="inputHistoryConfigData.db_name"
                    filterable
                    clearable
                    :placeholder="$t('_.请先选择集群')"
                    :options="dbLists"
                    @change="dbchange"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="table_name" :label="$t('_.数据表')">
                <FSelect
                    v-model="inputHistoryConfigData.table_name"
                    filterable
                    clearable
                    :placeholder="$t('_.请先选择数据库')"
                    :options="tableList"
                    @change="tableChange"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="column_name_list" :label="$t('_.字段')">
                <FSelect
                    v-model="inputHistoryConfigData.column_name_list"
                    filterable
                    clearable
                    multiple
                    :collapseTags="true"
                    :collapseTagsLimit="4"
                    :options="columnList"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="time" :label="$t('_.采集时间')">
                <FDatePicker
                    v-model="inputHistoryConfigData.time"
                    format="yyyy-MM-dd"
                    class="date-picker"
                    :shortcuts="rangeShortcuts"
                    :maxDate="maxDateValue"
                    type="daterange"
                    maxRange="1095D"
                />
            </FFormItem>
        </FForm>
    </div>
</template>
<script setup>

import {
    computed, inject, ref, watch, onMounted,
} from 'vue';
import { request, useI18n } from '@fesjs/fes';
import dayjs from 'dayjs';
import { FMessage, FModal, FTimePicker } from '@fesjs/fes-design';
import { MinusCircleOutlined } from '@fesjs/fes-design/es/icon';

const { t: $t } = useI18n();

// eslint-disable-next-line no-undef
const props = defineProps({
    inputHistoryConfigData: {
        type: Object,
        default: () => ({}),
        required: true,
    },
    index: {
        type: Number,
        requierd: true,
    },
    removable: {
        type: Boolean,
        requierd: true,
        default: false,
    },
    placeholders: {
        type: Array,
        requierd: true,
    },
});
const maxDateValue = dayjs().subtract(1, 'day').valueOf();
const rangeShortcuts = {
    最近一个月: [new Date().setDate(new Date().getDate() - 32), new Date().setDate(new Date().getDate() - 1)],
    最近三个月: [new Date().setDate(new Date().getDate() - 91), new Date().setDate(new Date().getDate() - 1)],
    最近六个月: [new Date().setDate(new Date().getDate() - 184), new Date().setDate(new Date().getDate() - 1)],
};
const getPartitionList = async ({ cluster_name, database, table }) => {
    try {
        const res = await request('api/v1/projector/imsmetric/collect/partition/list', { cluster_name, database, table });
        if (Array.isArray(res)) {
            return res.map(item => ({ value: item, label: item }));
        }
        return [];
    } catch (err) {
        console.warn(err);
        return [];
    }
};
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:inputHistoryConfigData', 'delete', 'update:placeholders']);
const inputHistoryConfigData = ref(props.inputHistoryConfigData);
const currentInputMetaData = computed({
    get: () => props.inputHistoryConfigData,
    set: (value) => {
        emit('update:inputHistoryConfigData', value);
    },
});
const currentPlaceholders = computed({
    get: () => props.placeholders,
    set: (value) => {
        emit('update:placeholders', value);
    },
});
const deleteInputMeta = () => {
    emit('delete');
};

const inputMetaRules = {
    cluster_name: [
        {
            required: true,
            message: $t('_.不能为空'),
        },
    ],
    db_name: [
        {
            required: true,
            message: $t('_.不能为空'),
        },
    ],
    table_name: [
        {
            required: true,
            message: $t('_.不能为空'),
        },
    ],
    executionTimePicker: [{
        required: true,
        message: $t('_.不能为空'),
    }],
};

const clusterVivisions = ref([]);
const handleChangeCluster = async (value) => {
    const resp = await request('api/v1/projector/imsmetric/data_source/conditions', {}, { method: 'get' });
    clusterVivisions.value = resp?.clusters.map(item => ({
        label: item,
        value: item,
    }));
};
const dbLists = ref([]);
const tableList = ref([]);
const columnList = ref([]);
const clusterchange = async (value) => {
    inputHistoryConfigData.value.db_name = '';
    inputHistoryConfigData.value.table_name = '';
    inputHistoryConfigData.value.column_name_list = [];
    dbLists.value = [];
    tableList.value = [];
    columnList.value = [];
    if (value) {
        const clusterRes = await request('api/v1/projector/imsmetric/data_source/conditions', { cluster: value }, { method: 'get' });
        dbLists.value = (clusterRes.dbs || []).map(item => ({
            label: item,
            value: item,
        }));
    }
};
const dbchange = async (value) => {
    inputHistoryConfigData.value.table_name = '';
    tableList.value = [];
    inputHistoryConfigData.value.column_name_list = [];
    columnList.value = [];
    if (value) {
        const dbsRes = await request('api/v1/projector/imsmetric/data_source/conditions', { cluster: inputHistoryConfigData.value.cluster_name, db: inputHistoryConfigData.value.db_name }, { method: 'get' });
        // eslint-disable-next-line no-case-declarations
        tableList.value = (dbsRes.tables || []).map(item => ({
            label: item,
            value: item,
        }));
    }
};
const tableChange = async (value) => {
    inputHistoryConfigData.value.column_name_list = [];
    columnList.value = [];
    if (value) {
        const dbsRes = await request('api/v1/projector/imsmetric/data_source/conditions', { cluster: inputHistoryConfigData.value.cluster_name, db: inputHistoryConfigData.value.db_name, table: value }, { method: 'get' });
        // eslint-disable-next-line no-use-before-define
        columnList.value = (dbsRes.columns || []).map(item => ({
            label: item,
            value: item,
        }));
    }
};


const scheduleFormRef = ref(null);
const valid = async () => {
    try {
        await scheduleFormRef.value?.validate();
        return true;
    } catch (e) {
        return false;
    }
};
// eslint-disable-next-line no-undef
defineExpose({ valid });
onMounted(() => {
    handleChangeCluster();
});

</script>
<style lang='less' scoped>
.wrap {
    padding: 16px;
    border: 1px solid #cfd0d3;
    border-radius: 4px;
    margin-bottom: 16px;
    margin-right: 8px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #0F1222;
    line-height: 22px;
    font-weight: 400;
    .delete-btn {
        cursor: pointer;
        display: flex;
        align-items: center;
        color:#93949B;
        font-weight: 400;
    }
}
.rule-detail-form {
    :deep(.fes-input) {
        max-width: 100%;
    }
    :deep(.fes-select) {
        max-width: 100%;
    }
    :deep(.fes-textarea) {
        max-width: 100%;
    }
}
.execution-time-wrapper {
    width: 100%;
    align-items: center;
    display: flex;
    .mr-8px {
        margin-right: 8px;
    }
    :deep(.fes-form-item ){
        margin-bottom: 0 !important;
        width: 100%;
    }
}
.time-bottom {
    :deep(.fes-form-item){
        margin-bottom: 0px !important;
        width: 100%;
    }
}
</style>
