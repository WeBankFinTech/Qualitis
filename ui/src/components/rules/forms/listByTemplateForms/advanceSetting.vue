<template>
    <div class="rule-detail-form">
        <div class="wd-content-body tab-content obejct-group-content">
            <p class="wd-body-title-template">{{$t('_.高级设置')}}</p>
            <ul class="wd-body-menus">
                <li class="wd-body-menu-item">
                    <FTooltip mode="popover" :disabled="isDisable">
                        <FSwitch v-model="isShowSettting" :disabled="!isDisable">
                            <template #active>
                                <CheckOutlined />
                            </template>
                            <template #inactive>
                                <CloseOutlined />
                            </template>
                        </FSwitch>
                        <template #content>
                            <div>{{$t('_.请先选择模板')}}</div>
                        </template>
                    </FTooltip>
                </li>
            </ul>
            <div v-if="isShowSettting">
                <FForm
                    ref="settingFormRef"
                    :model="settingFormData"
                    :labelWidth="96"
                    :rules="rules"
                    labelPosition="right"
                    class="listByTemplateForm"
                >
                    <FFormItem :label="$t('_.任务集群是否相同')" prop="isSameCluster">
                        <FRadioGroup v-model="settingFormData.isSameCluster">
                            <FRadio :value="true">{{$t('_.是')}}</FRadio>
                            <FRadio :value="false">{{$t('_.否')}}</FRadio>
                        </FRadioGroup>
                    </FFormItem>
                    <template v-if="settingFormData.isSameCluster">
                        <!-- 任务执行集群 -->
                        <FFormItem :label="$t('_.任务执行集群')" prop="cluster_name">
                            <FSelect
                                v-model="settingFormData.cluster_name"
                                filterable
                                class="form-edit-input"
                                @change="onClusterChange"
                            >
                                <FOption
                                    v-for="(item, index) in clusterList"
                                    :key="index"
                                    :disabled="item.disabled"
                                    :value="item.cluster_name"
                                    :label="item.cluster_name"
                                ></FOption>
                            </FSelect>
                            <div class="form-preview-label">{{settingFormData.cluster_name}}</div>
                        </FFormItem>
                        <FFormItem :label="$t('_.任务执行用户是否相同')" prop="isSameProxyUser">
                            <FRadioGroup v-model="settingFormData.isSameProxyUser">
                                <FRadio :value="true">{{$t('_.是')}}</FRadio>
                                <FRadio :value="false">{{$t('_.否')}}</FRadio>
                            </FRadioGroup>
                        </FFormItem>
                    </template>
                    <template v-if="settingFormData.isSameCluster && settingFormData.isSameProxyUser">
                        <!-- 任务执行用户 -->
                        <FFormItem :label="$t('_.任务执行用户')" prop="proxy_user">
                            <FSelect
                                v-model="settingFormData.proxy_user"
                                class="form-edit-input"
                                filterable
                                clearable
                                @change="onProxyUserChange"
                            >
                                <FOption
                                    v-for="(item, index) in proxyUserList"
                                    :key="index"
                                    :value="item"
                                    :label="item"
                                ></FOption>
                            </FSelect>
                            <div class="form-preview-label">{{settingFormData.proxy_user || '--'}}</div>
                        </FFormItem>
                        <FFormItem :label="$t('_.数据源类型是否相同')" prop="isSameType">
                            <FRadioGroup v-model="settingFormData.isSameType">
                                <FRadio :value="true">{{$t('_.是')}}</FRadio>
                                <FRadio :value="false">{{$t('_.否')}}</FRadio>
                            </FRadioGroup>
                        </FFormItem>
                    </template>
                    <template v-if="settingFormData.isSameCluster && settingFormData.isSameProxyUser && settingFormData.isSameType">
                        <!-- 数据源类型 -->
                        <FFormItem v-if="settingFormData.isSameType" :label="$t('_.数据源类型')" prop="type">
                            <FSelect
                                v-model="settingFormData.type"
                                class="form-edit-input"
                                filterable
                                @change="onSourceTypeChange"
                            >
                                <FOption v-for="(item,index) in currentDataSourceTypeList" :key="index" :value="item.value" :label="item.label">
                                </FOption>
                            </FSelect>
                            <div class="form-preview-label">{{dataSourceType}}</div>
                        </FFormItem>
                        <template v-if="!['HIVE', 'FPS'].includes(dataSourceType) && settingFormData.isSameType">
                            <!-- 数据源名称 -->
                            <FFormItem :label="$t('_.数据源名称')" prop="linkis_datasource_id">
                                <FSelect
                                    v-model="settingFormData.linkis_datasource_id"
                                    :options="connections"
                                    filterable
                                    :fetchData="(v) => getDataSources(v)"
                                    class="form-edit-input"
                                    clearable
                                    @change="onSourceNameChange"
                                ></FSelect>
                                <div class="form-preview-label">{{connection?.label}}</div>
                            </FFormItem>
                            <!-- 数据源环境选择方式 -->
                            <FFormItem :label="$t('_.数据源环境选择方式')" prop="dcn_range_type">
                                <FRadioGroup v-model="settingFormData.dcn_range_type" class="form-edit-input" :cancelable="false" @change="onRangeDcnTypeChange">
                                    <FRadio value="all">{{$t('_.直接选择')}}</FRadio>
                                    <FRadio value="dcn_num">{{$t('_.按环境编号选择')}}</FRadio>
                                    <FRadio value="logic_area">{{$t('_.按逻辑区域选择')}}</FRadio>
                                </FRadioGroup>
                                <div class="form-preview-label">{{DCNTypeMap[settingFormData.dcn_range_type]}}</div>
                            </FFormItem>
                            <FFormItem v-if="['dcn_num', 'logic_area'].includes(settingFormData?.dcn_range_type)" :label="settingFormData.dcn_range_type === 'dcn_num' ? '环境编号' : '逻辑区域'" prop="linkis_datasource_dcn_range_values">
                                <FSelect
                                    v-model="settingFormData.linkis_datasource_dcn_range_values"
                                    :options="dcnValueOptions"
                                    class="form-edit-input"
                                    filterable
                                    clearable
                                    multiple
                                    collapseTags
                                    :collapseTagsLimit="2"
                                    @change="onDcnChange"
                                ></FSelect>
                                <div class="form-preview-label project-tags">
                                    <div v-for="(item, index) in settingFormData.linkis_datasource_dcn_range_values" :key="index" class="tags-item">{{item}}</div>
                                </div>
                            </FFormItem>
                            <!-- 数据源环境 -->
                            <FFormItem :label="$t('_.数据源环境')" prop="linkis_datasource_envs">
                                <FSelect
                                    v-model="settingFormData.linkis_datasource_envs"
                                    :options="envs"
                                    class="form-edit-input"
                                    filterable
                                    clearable
                                    multiple
                                    labelField="env_name"
                                    :disabled="['dcn_num', 'logic_area'].includes(settingFormData?.dcn_range_type)"
                                    collapseTags
                                    :collapseTagsLimit="2"
                                ></FSelect>
                                <div class="form-preview-label project-tags">
                                    <div v-for="(item, index) in settingFormData.linkis_datasource_envs" :key="index" class="tags-item">{{item.env_name}}</div>
                                </div>
                            </FFormItem>
                        </template>
                        <FFormItem :label="$t('_.数据库是否相同')" prop="isSameDb">
                            <FRadioGroup v-model="settingFormData.isSameDb">
                                <FRadio :value="true">{{$t('_.是')}}</FRadio>
                                <FRadio :value="false">{{$t('_.否')}}</FRadio>
                            </FRadioGroup>
                        </FFormItem>
                    </template>
                    <template v-if="settingFormData.isSameCluster
                        && settingFormData.isSameProxyUser
                        && settingFormData.isSameType
                        && settingFormData.isSameDb">
                        <FFormItem :label="$t('crossTableCheck.Database')" prop="db_name">
                            <FSelect
                                v-model="settingFormData.db_name"
                                filterable
                                class="form-edit-input"
                                labelField="db_name"
                                valueField="db_name"
                                :options="dbs"
                                @change="onDBChange($event, -1)" />
                            <div class="form-preview-label">{{settingFormData.db_name}}</div>
                        </FFormItem>
                        <FFormItem v-if="!['FPS'].includes(dataSourceType)" :label="$t('_.数据表是否相同')" prop="isSameTable">
                            <FRadioGroup v-model="settingFormData.isSameTable">
                                <FRadio :value="true">{{$t('_.是')}}</FRadio>
                                <FRadio :value="false">{{$t('_.否')}}</FRadio>
                            </FRadioGroup>
                        </FFormItem>
                        <FFormItem v-if="['FPS'].includes(dataSourceType)" :label="$t('_.数据库/表是否相同')" prop="isSameDbTable">
                            <FRadioGroup v-model="settingFormData.isSameDbTable">
                                <FRadio :value="true">{{$t('_.是')}}</FRadio>
                                <FRadio :value="false">{{$t('_.否')}}</FRadio>
                            </FRadioGroup>
                        </FFormItem>
                    </template>
                    <FFormItem v-if="settingFormData.isSameCluster
                                   && settingFormData.isSameProxyUser
                                   && settingFormData.isSameType
                                   && settingFormData.isSameDb
                                   && settingFormData.isSameTable
                                   && !['FPS'].includes(dataSourceType)"
                               :label="'数据表'" prop="table_name">
                        <FSelect
                            v-model="settingFormData.table_name"
                            filterable
                            class="form-edit-input"
                            labelField="table_name"
                            valueField="table_name"
                            :options="tables"
                            @change="onTableChange($event, -1)" />
                        <div class="form-preview-label">{{settingFormData.table_name}}</div>
                    </FFormItem>
                    <!-- FPS模块 -->
                    <template v-if="settingFormData.isSameDb && settingFormData.isSameDbTable && ['FPS'].includes(dataSourceType)">
                        <template v-if="!settingFormData.file_type">
                            <FFormItem :label="$t('_.数据库/表')" prop="file_type">
                                <TableStructureButton @click="() => adTableStructrueShow = true" />
                            </FFormItem>
                        </template>
                        <template v-else>
                            <FFormItem v-if="!['crossDatabaseFullVerification'].includes(ruleType)" :label="$t('_.数据表')" prop="table_name">
                                <FInput
                                    v-model="settingFormData.table_name"
                                    class="form-edit-input"
                                    @change="onTableChange($event, -1)" />
                                <div class="form-preview-label">{{settingFormData.table_name}}</div>
                                <span v-if="editMode !== 'display'" class="edit-table-icon" @click="() => adTableStructrueShow = true">
                                    <img src="@/assets/images/icons/edit.svg" alt="">
                                </span>
                            </FFormItem>
                        </template>
                    </template>
                </FForm>
                {{props.type}}
                <TableStructure v-if="adTableStructrueShow" v-model:show="adTableStructrueShow" :index="1" :form="settingFormData" :target="'single'" />
            </div>
        </div>
    </div>
</template>

<script setup>

import {
    ref, inject, defineProps, unref, computed, nextTick, watch, defineEmits,
} from 'vue';
import { CheckOutlined, CloseOutlined } from '@fesjs/fes-design/es/icon';
import { cloneDeep } from 'lodash-es';
import { useI18n } from '@fesjs/fes';
import useDataSource from '@/components/rules/hook/useDataSource';
import eventbus from '@/common/useEvents';
import { dataSourceTypeList, getDataSourceType, useListener } from '@/components/rules/utils';
import TableStructureButton from '../TableStructureButton.vue';
import TableStructure from '../TableStructure.vue';

const props = defineProps({ isShowSettting: Boolean, isDisable: Boolean });
const emit = defineEmits(['update:isShowSettting']);

const { t: $t } = useI18n();

const proxyUserList = inject('proxyUserList');
const clusterList = inject('clusterList');
const upstreamParams = cloneDeep(unref(inject('upstreamParams')));
const currentRuleTypeObject = inject('ruleType');
const currentRuleType = currentRuleTypeObject;

const DCNTypeMap = {
    all: $t('_.直接选择'),
    dcn_num: $t('_.按环境编号选择'),
    logic_area: $t('_.按逻辑区域选择'),
};

const defaultAdvanceFormData = () => ({
    cluster_name: '',
    db_name: '',
    table_name: '',
    linkis_datasource_dcn_range_values: [],
    linkis_datasource_envs: [],
    type: '',
    proxy_user: '',
    isSameCluster: true,
    isSameProxyUser: true,
    isSameType: true,
    isSameDb: true,
    isSameTable: true,
    isSameDbTable: true,
    file_table_desc: [],
    file_id: '',
    file_hash_values: '',
    file_delimiter: '',
    file_type: '',
});
const defaultFormData = () => ({
    cluster_name: '',
    db_name: '',
    table_name: '',
    linkis_datasource_envs: [],
    type: '',
    proxy_user: '',
    file_table_desc: [],
    file_id: '',
    file_hash_values: '',
    file_delimiter: '',
    file_type: '',
    isSameCluster: false,
    isSameProxyUser: false,
    isSameType: false,
    isSameDb: false,
    isSameTable: false,
    isSameDbTable: false,
});
const settingFormData = ref(defaultFormData());

const isShowSettting = computed({
    get() {
        return props.isShowSettting;
    },
    set(value) {
        emit('update:isShowSettting', value);
    },
});
const dataSourceType = computed(() => getDataSourceType(settingFormData.value.type === 'fps', settingFormData.value.type, false));

const {
    connections,
    dbs,
    tables,
    envs,
    handleDbChange,
    handleTableChange,
    handleDbsDependenciesChange,
    handleDataSourcesDependenciesChange,
    handleEnvChange,
    updateDataSource,
    updateUpstreamParams,
    dbsList,
    dcnValueOptions,
} = useDataSource(upstreamParams);

// 表单规则
const rules = ref({
    isSameCluster: [{
        type: 'boolean',
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    isSameProxyUser: [{
        type: 'boolean',
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    isSameType: [{
        type: 'boolean',
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    isSameDb: [{
        type: 'boolean',
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    isSameTable: [{
        type: 'boolean',
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    isSameDbTable: [{
        type: 'boolean',
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    file_type: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('_.数据库/表未完成'),
    }],
    proxy_user: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    cluster_name: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    type: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    linkis_datasource_id: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    dcn_range_type: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    linkis_datasource_dcn_range_values: [{
        required: true,
        type: 'array',
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    linkis_datasource_envs: [{
        required: true,
        trigger: ['change', 'blur'],
        type: 'array',
        message: $t('common.notEmpty'),
    }],
    db_name: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    table_name: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
});
const adTableStructrueShow = ref(false);
const resetFormItems = (type = '') => {
    try {
        // 集群变化之后需要清空1、数据源类型、名称、环境、数据库、数据表
        switch (type) {
            case 'cluster':
                settingFormData.value.type = '';
                settingFormData.value.linkis_datasource_id = '';
                settingFormData.value.dcn_range_type = '';
                connections.value = [];
                settingFormData.value.linkis_datasource_envs = [];
                envs.value = [];
                settingFormData.value.db_name = '';
                settingFormData.value.table_name = '';
                dbs.value = [];
                tables.value = [];
                dcnValueOptions.value = [];
                break;
            case 'db':
                settingFormData.value.table_name = '';
                tables.value = [];
                break;
            case 'proxy_user':
                settingFormData.value.linkis_datasource_id = '';
                settingFormData.value.dcn_range_type = '';
                settingFormData.value.linkis_datasource_envs = [];
                envs.value = [];
                settingFormData.value.db_name = '';
                settingFormData.value.table_name = '';
                dbs.value = [];
                tables.value = [];
                dcnValueOptions.value = [];
                settingFormData.value.linkis_datasource_dcn_range_values = [];
                break;
            case 'type':
                // 为关系型数据库时补充linkis_datasource_type
                if (['tdsql', 'mysql'].includes(settingFormData.value.type)) {
                    settingFormData.value.linkis_datasource_type = settingFormData.value.type;
                } else {
                    settingFormData.value.linkis_datasource_type = null;
                }
                if (!['fps'].includes(settingFormData.value.type)) {
                    settingFormData.value.file_table_desc = [];
                    settingFormData.value.file_id = '';
                    settingFormData.value.file_hash_values = '';
                    settingFormData.value.file_delimiter = '';
                    settingFormData.value.file_type = '';
                }
                settingFormData.value.linkis_datasource_id = '';
                settingFormData.value.dcn_range_type = '';
                connections.value = [];
                settingFormData.value.linkis_datasource_envs = [];
                envs.value = [];
                settingFormData.value.db_name = '';
                settingFormData.value.table_name = '';
                dbs.value = [];
                tables.value = [];
                dcnValueOptions.value = [];
                settingFormData.value.linkis_datasource_dcn_range_values = [];
                break;
            case 'source_name':
                settingFormData.value.dcn_range_type = '';
                settingFormData.value.linkis_datasource_envs = [];
                envs.value = [];
                settingFormData.value.db_name = '';
                settingFormData.value.table_name = '';
                dbs.value = [];
                tables.value = [];
                dcnValueOptions.value = [];
                settingFormData.value.linkis_datasource_dcn_range_values = [];
                break;
            case 'dcn_range_type':
                settingFormData.value.linkis_datasource_envs = [];
                envs.value = [];
                settingFormData.value.db_name = '';
                settingFormData.value.table_name = '';
                dcnValueOptions.value = [];
                settingFormData.value.linkis_datasource_dcn_range_values = [];
                dbs.value = [];
                tables.value = [];
                break;
            case 'linkis_datasource_dcn_range_values':
                settingFormData.value.db_name = '';
                settingFormData.value.table_name = '';
                dbs.value = [];
                tables.value = [];
                break;
            default:
                break;
        }
        updateDataSource(settingFormData.value, 'single', 'advanceSetting');
    } catch (err) {
        console.warn(err);
    }
};

watch(isShowSettting, () => {
    if (!isShowSettting.value) {
        resetFormItems('cluster');
        // settingFormData.value = cloneDeep(defaultFormData());
        settingFormData.value = {};
        // eslint-disable-next-line no-use-before-define
        eventbus.emit('ADVANCE_SETTING_CHANGE', {});
        // eventbus.emit('ADVANCE_SETTING_CHANGE', defaultFormData());
        console.log('着的问题', settingFormData.value);
    } else {
        resetFormItems('cluster');
        settingFormData.value = defaultAdvanceFormData();
    }
});

// 集群选择变化
const onClusterChange = async () => {
    resetFormItems('cluster');
};

// 代理用户变化
const onProxyUserChange = async () => {
    resetFormItems('proxy_user');
    await nextTick();
    handleDataSourcesDependenciesChange();
    handleDbsDependenciesChange();
    handleDbChange();
};

// 数据源类型变化
const onSourceTypeChange = async () => {
    resetFormItems('type');
    await nextTick();
    handleDataSourcesDependenciesChange();
    handleDbsDependenciesChange();
    handleDbChange();
};

// 数据库变化
const onDBChange = async (val, index = -1) => {
    resetFormItems('db');
    handleDbChange(index);
};
// 数据表变化
const onTableChange = async (val, index = -1) => {
    handleTableChange(props.type);
};
// 数据源名称变化
// 数据源名称会影响库、表和环境，环境与库表之间没影响关系
const connection = computed(() => connections.value.find(v => v.value === settingFormData.value.linkis_datasource_id?.toString()));
const onSourceNameChange = async (value) => {
    resetFormItems('source_name');
    await nextTick();
    handleDbsDependenciesChange();
    handleDbChange();
    handleEnvChange();
};
const onRangeDcnTypeChange = async () => {
    resetFormItems('dcn_range_type');
    await nextTick();
    handleDbsDependenciesChange();
    handleDbChange();
    handleEnvChange();
};
const rebuildEnvs = (dcnList) => {
    const tempEnvs = [];
    for (let i = 0; i < dcnList.length; i++) {
        const tempDcn = dcnValueOptions.value.find(item => item.value === dcnList[i]);
        if (tempDcn) {
            for (let j = 0; j < tempDcn.envs.length; j++) {
                const tempEnv = envs.value.find(item => item.env_id === tempDcn.envs[j].env_id).value;
                tempEnvs.push(tempEnv);
            }
        }
    }
    return tempEnvs;
};
const onDcnChange = async (val, index) => {
    settingFormData.value.linkis_datasource_envs = rebuildEnvs(settingFormData.value.linkis_datasource_dcn_range_values);
    // eslint-disable-next-line no-use-before-define
    await settingFormRef.value.validate(['linkis_datasource_envs']);
    resetFormItems('linkis_datasource_dcn_range_values');
    await nextTick();
    handleDbsDependenciesChange(index);
};
const columns = ref([]);
useListener('UPDATE_COLUMN_LIST', ({ data, target }) => {
    // 基于规则模版创建时，如果是数据源为fps，创建表数据后需要更新校验字段列表
    if (dataSourceType.value?.toLowerCase() === 'fps') {
        console.log('UPDATE_COLUMN_LIST:', data);
        columns.value = data;
    }
});
// FPS表单校验完成后发送信号，监听并将数据同步到formDataSource，区别触发来源，两个verifyobject都在监听这个事件，会导致同步更新。
useListener('TABLE_STRUCTURE_CHANGE', ([datasource, target, index]) => {
    // 更新store
    if (target === 'single' && settingFormData.value.isSameDbTable) {
        settingFormData.value = cloneDeep(datasource);
        console.log('settingFormData:', settingFormData.value);
        const tempData = cloneDeep(settingFormData.value);
        const targetTempData = {};
        switch (props.type) {
            case 'single':
                targetTempData.datasource = tempData.datasource;
                break;
            case 'source':
                targetTempData.source = tempData.source;
                break;
            case 'target':
                targetTempData.target = tempData.target;
                break;
            default:
                break;
        }
        console.log(targetTempData, 'data');
        // store.commit('rule/updateCurrentRuleDetail', targetTempData);
        // eventbus.emit('SHOULD_UPDATE_NECESSARY_DATA');
    }
});

// 表文件数据源只有hive
const currentDataSourceTypeList = computed(() => {
    if (currentRuleType.value.ruleType === '4-1') {
        return dataSourceTypeList.filter(item => item.value === 'hive');
    }
    return dataSourceTypeList;
});

const emitSettingData = () => {
    if (!isShowSettting.value) {
        eventbus.emit('ADVANCE_SETTING_CHANGE', defaultFormData());
        return;
    }

    const settingData = {};
    const {
        cluster_name: clusterName,
        db_name: dbName,
        table_name: tableName,
        linkis_datasource_envs: datasourceEnvs,
        linkis_datasource_dcn_range_values: linkisDatasourceDcnRangeValues,
        linkis_datasource_id: datasourceId,
        linkis_datasource_type: linkisDatasourceType,
        dcn_range_type: dcnRangeType,
        type,
        proxy_user: proxyUser,
        isSameCluster,
        isSameProxyUser,
        isSameType,
        isSameDb,
        isSameTable,
        isSameDbTable,
        file_table_desc: fileTableDesc,
        file_id: fileId,
        file_hash_values: fileHashValues,
        file_delimiter: fileDelimiter,
        file_type: fileType,
    } = settingFormData.value;
    settingData.cluster_name = isSameCluster ? clusterName : '';
    settingData.isSameCluster = isSameCluster;
    settingData.isSameProxyUser = isSameProxyUser;
    settingData.isSameType = isSameType;
    settingData.isSameDb = isSameDb;
    settingData.isSameTable = isSameTable;
    settingData.isSameDbTable = isSameDbTable;
    settingData.proxy_user = isSameProxyUser ? proxyUser : '';
    if (isSameType) {
        settingData.type = type;
        settingData.linkis_datasource_dcn_range_values = linkisDatasourceDcnRangeValues;
        settingData.linkis_datasource_envs = datasourceEnvs;
        settingData.linkis_datasource_id = datasourceId;
        settingData.dcn_range_type = dcnRangeType;
    } else {
        settingData.type = '';
    }
    if (isSameType && ['tdsql', 'mysql'].includes(type)) {
        settingData.linkis_datasource_type = linkisDatasourceType;
    }
    if (isSameDbTable && type === 'fps') {
        settingData.file_table_desc = fileTableDesc;
        settingData.file_id = fileId;
        settingData.file_hash_values = fileHashValues;
        settingData.file_delimiter = fileDelimiter;
        settingData.file_type = fileType;
    }
    settingData.db_name = isSameDb ? dbName : '';
    settingData.table_name = isSameTable ? tableName : '';
    console.log('执行次数');
    eventbus.emit('ADVANCE_SETTING_CHANGE', { ...settingData });
};

// 需要重置的公共字段
const resetCommonProperties = () => {
    settingFormData.value.type = '';
    settingFormData.value.db_name = '';
    settingFormData.value.table_name = '';
    settingFormData.value.proxy_user = '';
};
watch(
    [
        () => settingFormData.value.isSameType,
        () => settingFormData.value.isSameProxyUser,
        () => settingFormData.value.isSameCluster,
        () => settingFormData.value.isSameDb,
        () => settingFormData.value.isSameTable,
        () => settingFormData.value.isSameDbTable,
    ],
    (
        [isSameType, isSameProxyUser, isSameCluster, isSameDb, isSameTable, isSameDbTable],
        [oldIsSameType, oldIsSameProxyUser, oldIsSameCluster, oldIsSameDb, oldIsSameTable, oldIsSameDbTable],
    ) => {
        if (isShowSettting.value) {
            if (!isSameType) {
                console.log('执行排查 isSameType');
                resetCommonProperties();
                settingFormData.value.linkis_datasource_id = '';
                settingFormData.value.linkis_datasource_envs = [];
                settingFormData.value.dcn_range_type = '';
            }
            if (!isSameProxyUser) {
                console.log('执行排查 isSameProxyUser');
                settingFormData.value.proxy_user = '';
                resetCommonProperties();
            }
            if (!isSameCluster) {
                console.log('执行排查 isSameCluster');
                settingFormData.value.cluster_name = '';
                resetCommonProperties();
            }
            if (!isSameDb) {
                console.log('执行排查 isSameDb');
                settingFormData.value.db_name = '';
                settingFormData.value.table_name = '';
            }
            if (!isSameTable) {
                console.log('执行排查 isSameTable');
                settingFormData.value.table_name = '';
            }
            if (!isSameDbTable) {
                console.log('执行排查 isSameDbTable');
                settingFormData.value.file_table_desc = [];
                settingFormData.value.file_id = '';
                settingFormData.value.file_hash_values = '';
                settingFormData.value.file_delimiter = '';
                settingFormData.value.file_type = '';
                settingFormData.value.table_name = '';
            }
            // 假设 emitSettingData 是一个已定义的函数
            emitSettingData();
        }
    },
);

const settingFormRef = ref();
const valid = async () => {
    try {
        if (isShowSettting.value) await settingFormRef.value.validate();
        return true;
    } catch (err) {
        console.warn(err);
        return false;
    }
};

// eslint-disable-next-line no-undef
defineExpose({ valid });

</script>

<style lang="less" scoped>
.obejct-group-content{
    background: rgba(15,18,34,.03);
    border-radius: 4px;
    padding: 24px;
    width: 498px;
    :deep(.fes-switch-inner) {
        margin-top: 2px;
    }
    .wd-body-title-template {
        width: 116px;
        height: 22px;
        font-family: PingFangSC-Medium;
        font-size: 14px;
        color: #0F1222;
        line-height: 22px;
        font-weight: 500;
    }
    .listByTemplateForm {
        padding-top: 20px;
    }
}
</style>
