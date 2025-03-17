<template>
    <div class="wrap rule-detail-form edit">
        <div class="header">
            {{$t('_.采集组')}}{{index.toString().padStart(2, '0')}}
            <span v-if="removable" class="delete-btn" @click="deleteInputMeta">
                <MinusCircleOutlined style="margin-right: 4.67px;" />{{$t('_.删除')}}</span>
        </div>
        <FForm
            ref="inputMetaForm"
            :labelWidth="68"
            labelPosition="right"
            :model="inputCollectData"
            :rules="inputMetaRules"
        >
            <FFormItem prop="proxy_user" :label="$t('_.代理用户')">
                <FSelect
                    v-model="inputCollectData.proxy_user"
                    style="width:830px"
                    filterable
                    clearable
                    :options="proxyUserList"
                    @change="userchange"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="cluster_name" :label="$t('_.集群')">
                <FSelect
                    v-model="inputCollectData.cluster_name"
                    style="width:830px"
                    filterable
                    clearable
                    :placeholder="$t('_.请先选择代理用户')"
                    :options="clusterList"
                    @change="clusterchange"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="db_name" :label="$t('_.数据库')">
                <FSelect
                    v-model="inputCollectData.db_name"
                    style="width:830px"
                    filterable
                    clearable
                    :placeholder="$t('_.请先选择集群')"
                    :options="dbLists"
                    @change="dbchange"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="table_name" :label="$t('_.数据表')">
                <FSelect
                    v-model="inputCollectData.table_name"
                    style="width:830px"
                    filterable
                    clearable
                    :placeholder="$t('_.请先选择数据库')"
                    :options="tableList"
                    @change="tableChange"
                ></FSelect>
            </FFormItem>
            <!-- <FFormItem prop="collect_obj" label="采集对象">
                <FSelectCascader
                    v-model="inputCollectData.collect_obj"
                    :data="clusterList"
                    :loadData="loadData"
                    clearable
                    filterable
                    remote
                    emitPath
                    showPath
                    @change="handleChange"
                />
            </FFormItem> -->
            <FFormItem prop="partition" :label="$t('_.采集分区')">
                <FSelect
                    v-model="inputCollectData.partition"
                    style="width:830px"
                    tag
                    filterable
                    clearable
                    :options="partitionList"
                ></FSelect>
            </FFormItem>
            <div class="selection">
                <ConfigGroup
                    v-for="(item, configIndex) in inputCollectData.collect_configs"
                    :ref="el => { if (el) configRefs[configIndex] = el }"
                    :key="item"
                    v-model:inputConfigData="inputCollectData.collect_configs[configIndex]"
                    :removable="inputCollectData.collect_configs.length > 1"
                    :configIndex="configIndex + 1"
                    :templateList="templateList"
                    :proxy_user="inputCollectData.proxy_user"
                    :collect_obj="inputCollectData.collect_obj"
                    :columnList="columnList"
                    :clusterVivisions="clusterVivisions"
                    @delete="deleteConfig(configIndex)"
                />
                <div class="add-sign">
                    <span class="add-sign-btn" @click="addConfig">
                        <PlusCircleOutlined style="padding-right: 4.58px;" />{{$t('_.添加配置组')}}</span>
                </div>
            </div>
        </FForm>
    </div>
</template>
<script setup>

import {
    useI18n, request,
} from '@fesjs/fes';
import {
    computed, inject, ref, watch, defineEmits,
} from 'vue';
import { FMessage, FModal } from '@fesjs/fes-design';
import { cloneDeep } from 'lodash-es';
import {
    MinusCircleOutlined, QuestionCircleOutlined, PlusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import ConfigGroup from './configGroup.vue';

const { t: $t } = useI18n();

// eslint-disable-next-line no-undef
const props = defineProps({
    inputCollectData: {
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
    templateList: {
        required: true,
        type: Array,
    },
    proxyUserList: {
        type: Array,
        default: () => [],
    },
    clusterVivisions: {
        type: Array,
        default: () => [],
    },
});
const emit = defineEmits(['update:inputCollectData', 'delete', 'update:placeholders']);
const inputCollectData = ref(props.inputCollectData);
const currentInputMetaData = computed({
    get: () => props.inputCollectData,
    set: (value) => {
        emit('update:inputCollectData', value);
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
// 配置组相关
const initConfigInfo = {
    template_id: null,
    columns: null,
};
const configRefs = ref([]);
const deleteConfig = (index) => {
    inputCollectData.value.collect_configs.splice(index, 1);
};
const addConfig = () => {
    console.log('data.value.collect_configs', inputCollectData.value, inputCollectData.value.collect_configs);

    inputCollectData.value.collect_configs.push({
        collect_type: true,
        calcu_unit_configs: [cloneDeep(initConfigInfo)],
    });
};

const partitionList = ref([]);
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
const handleChange = async (value) => {
    console.log('采集对象', value);
    inputCollectData.value.partition = '';
    if (value) {
        partitionList.value = await getPartitionList({ cluster_name: value[0], database: value[1], table: value[2] });
    } else {
        partitionList.value = [];
    }
};
const clusterList = computed(() => props.clusterVivisions.map(item => ({
    value: item.label,
    label: item.label,
})));
const dbLists = ref([]);
const tableList = ref([]);
const userchange = async (value) => {
    inputCollectData.value.cluster_name = '';
    inputCollectData.value.db_name = '';
    inputCollectData.value.table_name = '';
    dbLists.value = [];
    tableList.value = [];
};

const clusterchange = async (value) => {
    inputCollectData.value.db_name = '';
    inputCollectData.value.table_name = '';
    dbLists.value = [];
    tableList.value = [];
    if (value) {
        const { data = [] } = await request('/api/v1/projector/meta_data/db', {
            proxy_user: inputCollectData.value.proxy_user,
            cluster_name: value,
            page_size: 2147483647,
            source_type: '',
            start_index: 0,
        });
        dbLists.value = (data || []).map(item => ({
            label: item.db_name,
            value: item.db_name,
        }));
    }
};
const dbchange = async (value) => {
    inputCollectData.value.table_name = '';
    tableList.value = [];
    if (value) {
        const resss = await request('/api/v1/projector/meta_data/table', {
            cluster_name: inputCollectData.value.cluster_name,
            db_name: inputCollectData.value.db_name,
            page_size: 2147483647,
            proxy_user: inputCollectData.value.proxy_user,
            source_type: '',
            start_index: 0,
        });
        tableList.value = (resss.data || []).map(item => ({
            label: item.table_name,
            value: item.table_name,
        }));
    }
};
const tableChange = async (value) => {
    if (value) {
        inputCollectData.value.collect_obj = [inputCollectData.value.cluster_name, inputCollectData.value.db_name, inputCollectData.value.table_name];
        handleChange(inputCollectData.value.collect_obj);
    } else {
        inputCollectData.value.collect_obj = [];
    }
};
const loadData = async (node) => {
    if (node) {
        switch (node.type) {
            case 'cluster':
                // eslint-disable-next-line no-case-declarations
                const { data = [] } = await request('/api/v1/projector/meta_data/db', {
                    proxy_user: inputCollectData.value.proxy_user,
                    cluster_name: node.value,
                    page_size: 2147483647,
                    source_type: '',
                    start_index: 0,
                });
                // eslint-disable-next-line no-case-declarations
                const dbsListmiddle = (data || []).map(item => ({
                    type: 'dbs',
                    label: item.db_name,
                    value: item.db_name,
                    isLeaf: false,
                    children: [],
                    cut: `${node.value}&&${item.db_name}`,
                }));
                node.children = dbsListmiddle;
                return node.children;
            case 'dbs':
                // eslint-disable-next-line no-case-declarations
                const paramss = node.cut.split('&&');
                // eslint-disable-next-line no-case-declarations
                const resss = await request('/api/v1/projector/meta_data/table', {
                    cluster_name: paramss[0],
                    db_name: paramss[1],
                    page_size: 2147483647,
                    proxy_user: inputCollectData.value.proxy_user,
                    source_type: '',
                    start_index: 0,
                });
                // eslint-disable-next-line no-case-declarations
                const tablelast = (resss.data || []).map(item => ({
                    type: 'table',
                    label: item.table_name,
                    value: item.table_name,
                    isLeaf: true,
                }));
                node.children = tablelast;
                return node.children;
            default:
                break;
        }
    }
};

const columnList = ref([]);
const columnParams = computed(() => {
    if (inputCollectData.value?.cluster_name?.length > 0 && inputCollectData.value?.db_name?.length > 0 && inputCollectData.value?.table_name?.length > 0 && inputCollectData.value.proxy_user) {
        return true;
    }
    return false;
});
const getcolumnList = async () => {
    if (columnParams.value) {
        try {
            const params = {
                cluster_name: inputCollectData.value?.cluster_name ?? '',
                db_name: inputCollectData.value?.db_name ?? '',
                table_name: inputCollectData.value?.table_name ?? '',
                proxy_user: inputCollectData.value.proxy_user,
                start_index: 0,
                page_size: 2147483647,

            };
            const res = await request('/api/v1/projector/meta_data/column', params);
            columnList.value = res.data.map(item => ({
                ...item,
                label: `${item.column_name} (${item.data_type})`,
            }));
        } catch (err) {
            columnList.value = [];
        }
    }
};
watch(
    columnParams,
    getcolumnList,
    { deep: true },
);
const inputMetaRules = {
    proxy_user: [
        {
            required: true,
            message: $t('_.不能为空'),
        },
    ],
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
            message: $t('_.不能为空'),
        },
    ],
};

const inputMetaForm = ref(null);
const valid = async () => {
    try {
        await Promise.all([inputMetaForm.value?.validate(), ...configRefs.value.map(item => item.valid())]);
        return true;
    } catch (e) {
        return false;
    }
};

// eslint-disable-next-line no-undef
defineExpose({ valid });

</script>
<style lang='less' scoped>
.wrap {
    padding: 16px;
    border: 1px solid #cfd0d3;
    border-radius: 4px;
    margin-bottom: 16px;
    margin-right: 8px;
    width: 948px;
}
.wrap.view{
    background-color: #f7f7f8;
    border: none;

    :deep(.fes-form-item){
        margin-bottom: 8px;
    }
}
.tip-icon {
    display: flex;
    margin-left: -13px;
}
.tip-content {
    width: 300px;
    font-family: PingFangSC-Regular;
    font-size: 12px;
    color: #FFFFFF;
    letter-spacing: 0;
    line-height: 20px;
    font-weight: 400;
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
    font-weight: 600;
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
.add-box {
    width: 100%;
}
.add {
    display: flex;
}
.add-icon {
    color: #5384FF;
}
.add-body {
    background-color: #fff;
    padding: 24px;
}
.add-sign {
    color: #5384FF;
    .add-sign-btn {
        display: flex;
        align-items: center;
        justify-content: flex-start;
        cursor: pointer;
    }
}
</style>
