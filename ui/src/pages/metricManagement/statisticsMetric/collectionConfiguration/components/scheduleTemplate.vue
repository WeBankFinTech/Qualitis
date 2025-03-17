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
            :model="inputConfigData"
            :rules="inputMetaRules">
            <FFormItem prop="cluster_name" :label="$t('_.集群')">
                <FSelect
                    v-model="inputConfigData.cluster_name"
                    filterable
                    clearable
                    :placeholder="$t('_.请选择')"
                    :options="clusterVivisions"
                    @change="clusterchange"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="db_name" :label="$t('_.数据库')">
                <FSelect
                    v-model="inputConfigData.db_name"
                    filterable
                    clearable
                    :placeholder="$t('_.请先选择集群')"
                    :options="dbLists"
                    @change="dbchange"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="table_name" :label="$t('_.数据表')">
                <FSelect
                    v-model="inputConfigData.table_name"
                    filterable
                    clearable
                    :placeholder="$t('_.请先选择数据库')"
                    :options="tableList"
                    @change="tableChange"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="partition" :label="$t('_.采集分区')">
                <FSelect
                    v-model="inputConfigData.partition"
                    remote
                    :placeholder="$t('_.请先选择采集对象')"
                    filterable
                    clearable
                    :options="partitionList"
                ></FSelect>
            </FFormItem>
            <div :class="inputConfigData.execute_interval ? '' : 'time-bottom'">
                <FFormItem prop="execute_interval" :label="$t('_.采集频率')">
                    <FSelect
                        v-model="inputConfigData.execute_interval"
                        class="workflows-select"
                        remote
                        filterable
                        clearable
                        :options="intervalList"
                        @change="generateExecutionTime(inputConfigData)"
                    ></FSelect>
                </FFormItem>
            </div>
            <div v-if="inputConfigData.execute_interval" class="time-bottom">
                <FFormItem prop="executionTimePicker" :label="$t('_.采集时间')">
                    <div class="execution-time-wrapper">
                        <FFormItem prop="execute_date_in_interval">
                            <FInput
                                v-if="inputConfigData.execute_interval === 'hour'"
                                v-model="inputConfigData.executionTimeMM"
                                class="mr-8px"
                                @change="generateExecutionTime(inputConfigData)"
                            >
                                <template #suffix>{{$t('_.分')}}</template>
                            </FInput>
                            <FInput
                                v-if="inputConfigData.execute_interval === 'hour'"
                                v-model="inputConfigData.executionTimeSS"
                                @change="generateExecutionTime(inputConfigData)"
                            >
                                <template #suffix>{{$t('_.秒')}}</template>
                            </FInput>
                            <FTimePicker
                                v-if="inputConfigData.execute_interval === 'day'"
                                v-model="inputConfigData.executionTimePicker"
                                style="width:100%"
                                @change="generateExecutionTime(inputConfigData)"
                            ></FTimePicker>
                            <FSelect
                                v-if="inputConfigData.execute_interval === 'week'"
                                v-model="inputConfigData.executionDateInInterval"
                                class="mr-8px"
                                :options="executionTimeWeekOptions"
                                @change="generateExecutionTime(inputConfigData)"
                            ></FSelect>
                            <FSelect
                                v-if="inputConfigData.execute_interval === 'month'"
                                v-model="inputConfigData.executionDateInInterval"
                                class="mr-8px"
                                :options="executionTimeMonthOptions"
                                @change="generateExecutionTime(inputConfigData)"
                            ></FSelect>
                            <FTimePicker
                                v-if="inputConfigData.execute_interval === 'week' || inputConfigData.execute_interval === 'month'"
                                v-model="inputConfigData.executionTimePicker"
                                @change="generateExecutionTime(inputConfigData)"
                            ></FTimePicker>
                        </FFormItem>
                    </div>
                </FFormItem>
            </div>
        </FForm>
    </div>
</template>
<script setup>

import {
    computed, inject, ref, watch, onMounted,
} from 'vue';
import { request, useI18n } from '@fesjs/fes';
import { FMessage, FModal, FTimePicker } from '@fesjs/fes-design';
import { MinusCircleOutlined } from '@fesjs/fes-design/es/icon';
import {
    fomatPreviewExecutionTime, executeIntervalList, executeIntervalDict, executionTimeWeekOptions, executionTimeMonthOptions,
} from '../utils';


const { t: $t } = useI18n();

// eslint-disable-next-line no-undef
const props = defineProps({
    inputConfigData: {
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
    intervalList: {
        type: Array,
        default: () => [],
    },
});
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
const emit = defineEmits(['update:inputConfigData', 'delete', 'update:placeholders']);
const inputConfigData = ref(props.inputConfigData);
const currentInputMetaData = computed({
    get: () => props.inputConfigData,
    set: (value) => {
        emit('update:inputConfigData', value);
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
    partition: [
        {
            required: true,
            trigger: ['change'],
            message: $t('_.不能为空'),
        },
    ],
    execute_interval: [
        {
            required: true,
            type: 'string',
            trigger: 'change',
            message: $t('_.不能为空'),
        },
    ],
    executionTimePicker: [{
        required: true,
        message: $t('_.不能为空'),
    }],
};
const generateExecutionTime = (item) => {
    console.log(inputConfigData.value);
    if (inputConfigData.value.execute_interval === 'hour') {
        const mm = parseInt(inputConfigData.value.executionTimeMM) || 0;
        const ss = parseInt(inputConfigData.value.executionTimeSS) || 0;
        if (mm > 59) {
            inputConfigData.value.executionTimeMM = '00';
        } else if (mm < 10) {
            inputConfigData.value.executionTimeMM = `0${mm}`;
        } else {
            inputConfigData.value.executionTimeMM = `${mm}`;
        }
        if (ss > 59) {
            inputConfigData.value.executionTimeSS = '00';
        } else if (ss < 10) {
            inputConfigData.value.executionTimeSS = `0${ss}`;
        } else {
            inputConfigData.value.executionTimeSS = `${ss}`;
        }
        inputConfigData.value.executionTimeInDate = `00:${inputConfigData.value.executionTimeMM}:${inputConfigData.value.executionTimeSS}`;
    } else {
        inputConfigData.value.executionTimeInDate = inputConfigData.value.executionTimePicker;
    }
    inputConfigData.value.executionTime = fomatPreviewExecutionTime(inputConfigData.value.execute_interval, inputConfigData.value.executionDateInInterval, inputConfigData.value.executionTimeInDate).time;
    console.log('inputConfigData.value.executionTime', inputConfigData.value.executionTime);
};
// 采集对象
const buildDivisionOption = data => ({
    type: 'cluster',
    label: data,
    value: data,
    isLeaf: false,
    children: [],
});
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
const clusterchange = async (value) => {
    inputConfigData.value.db_name = '';
    inputConfigData.value.table_name = '';
    dbLists.value = [];
    tableList.value = [];
    if (value) {
        const clusterRes = await request('api/v1/projector/imsmetric/data_source/conditions', { cluster: value }, { method: 'get' });
        dbLists.value = (clusterRes.dbs || []).map(item => ({
            label: item,
            value: item,
        }));
    }
};
const dbchange = async (value) => {
    inputConfigData.value.table_name = '';
    tableList.value = [];
    if (value) {
        const dbsRes = await request('api/v1/projector/imsmetric/data_source/conditions', { cluster: inputConfigData.value.table_name, db: inputConfigData.value.db_name }, { method: 'get' });
        // eslint-disable-next-line no-case-declarations
        tableList.value = (dbsRes.tables || []).map(item => ({
            label: item,
            value: item,
        }));
    }
};
const tableChange = async (value) => {
    if (value) {
        inputConfigData.value.collect_obj = [inputConfigData.value.cluster_name, inputConfigData.value.db_name, inputConfigData.value.table_name];
        // eslint-disable-next-line no-use-before-define
        handleChange(inputConfigData.value.collect_obj);
    } else {
        inputConfigData.value.collect_obj = [];
    }
};
// 分区
const partitionList = ref([]);
const handleChange = async (value) => {
    inputConfigData.value.partition = '';
    if (value) {
        partitionList.value = await getPartitionList({ cluster_name: value[0], database: value[1], table: value[2] });
    } else {
        partitionList.value = [];
    }
};
// 查询调度详情
const schedulerDetailParams = computed(() => {
    if (inputConfigData.value?.collect_obj?.length > 0 && inputConfigData.value.partition) {
        return true;
    }
    return false;
});
const getScheduleDetail = async () => {
    if (schedulerDetailParams.value) {
        const res = await request('api/v1/projector/imsmetric/collect/scheduler/detail', { database: inputConfigData.value.collect_obj[1], table: inputConfigData.value.collect_obj[2], partition: inputConfigData.value.partition });
        if (res) {
            inputConfigData.value.executionTimePicker = res.execute_interval === 'hour' ? '' : res.execute_time_in_date;
            inputConfigData.value.execute_interval = res.execute_interval;
            inputConfigData.value.executionDateInInterval = res.execute_date_in_interval;
            inputConfigData.value.executionTimeInDate = res.execute_date_in_interval;
            inputConfigData.value.executionTimeMM = res.execute_interval === 'hour' ? res.execute_time_in_date.split(':')[1] : '';
            inputConfigData.value.executionTimeSS = res.execute_interval === 'hour' ? res.execute_time_in_date.split(':')[2] : '';
            inputConfigData.value.execute_time_in_date = res.execute_time_in_date;
            inputConfigData.value.execute_date_in_interval = res.execute_date_in_interval;
        }
        generateExecutionTime();
    }
};
watch(
    schedulerDetailParams,
    getScheduleDetail,
    { deep: true },
);

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
