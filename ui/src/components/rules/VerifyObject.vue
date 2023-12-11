<template>
    <div class="rule-detail-form" :class="{ edit: editMode !== 'display' }">
        <p v-if="listType === 'listByTemplate'" class="wd-body-title-template">{{`校验对象${titleIndex + 1}`}}</p>
        <h6 v-else class="wd-body-title">校验对象</h6>
        <FForm
            ref="verifyObjectFormRef"
            :model="formDataSource"
            :rules="verifyObjectRules"
            :labelWidth="96"
            :labelPosition="editMode !== 'display' ? 'right' : 'left'"
            :class="{ listByTemplateForm: listType === 'listByTemplate' }"
        >
            <!-- rule_id -->
            <FFormItem v-if="verifyObjectData.rule_id" class="rule-id" :label="$t('tableThead.ruleId')" prop="ruleId">
                <FInput
                    v-model="verifyObjectData.rule_id"
                    class="form-edit-input"
                    disabled
                />
                <div class="form-preview-label">{{verifyObjectData.rule_id}}</div>
            </FFormItem>
            <!-- 规则类型 -->
            <FFormItem v-if="lisType === 'commonList'" class="rule-type" :label="$t('common.ruleType')">
                <FSelect
                    v-model="ruleType"
                    class="form-edit-input"
                    filterable
                    :disabled="editMode !== 'create'"
                    placeholder="请选择规则类型"
                >
                    <FOption
                        v-for="(item, index) in showRuleTypes"
                        :key="index"
                        :value="item.value"
                        :label="item.label"
                    ></FOption>
                </FSelect>
                <div class="form-preview-label">{{ruleTypeLabel}}</div>
            </FFormItem>
            <!-- 上游表 -->
            <FFormItem v-if="ruleData.currentProject.isWorkflowProject && (['sqlVerification', 'newSingleTableRule'].includes(ruleType) || listType === 'groupList')" class="upstream" label="数据来源">
                <FRadioGroup v-if="editMode !== 'display'" v-model="verifyObjectData.isUpStream" class="form-edit-input" :cancelable="false" @change="onUpStreamChange">
                    <FRadio :value="true">上游表</FRadio>
                    <FRadio :value="false">正常表</FRadio>
                </FRadioGroup>
                <div class="form-preview-label">{{verifyObjectData.isUpStream ? '上游表' : '正常表'}}</div>
            </FFormItem>
            <!-- 任务执行集群 -->
            <FFormItem v-if="!['crossDatabaseFullVerification', 'newMultiTableRule'].includes(ruleType)" label="任务执行集群" prop="cluster_name">
                <FSelect
                    v-model="formDataSource.cluster_name"
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
                <div class="form-preview-label">{{formDataSource.cluster_name}}</div>
            </FFormItem>
            <!-- 任务执行用户 -->
            <FFormItem label="任务执行用户" prop="proxy_user">
                <FSelect
                    v-model="formDataSource.proxy_user"
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
                <div class="form-preview-label">{{formDataSource.proxy_user || '--'}}</div>
            </FFormItem>
            <!-- 数据源类型 -->
            <FFormItem label="数据源类型" prop="type">
                <FSelect
                    v-model="formDataSource.type"
                    class="form-edit-input"
                    filterable
                    @change="onSourceTypeChange"
                >
                    <FOption v-for="(item,index) in currentDataSourceTypeList" :key="index" :value="item.value" :label="item.label" :disabled="item.value === 'fps' && verifyObjectData.isUpStream">
                    </FOption>
                </FSelect>
                <div class="form-preview-label">{{dataSourceType}}</div>
            </FFormItem>
            <!-- 自定义SQL支持多环境映射 -->
            <template v-if="!['HIVE', 'FPS'].includes(dataSourceType)">
                <!-- 数据源名称 -->
                <FFormItem label="数据源名称" prop="linkis_datasource_id">
                    <FSelect
                        v-model="formDataSource.linkis_datasource_id"
                        :options="connections"
                        filterable
                        :fetchData="(v) => getDataSources(v)"
                        class="form-edit-input"
                        clearable
                        @change="onSourceNameChange"
                    ></FSelect>
                    <div class="form-preview-label">{{connection?.label}}</div>
                </FFormItem>
                <FFormItem v-if="['sqlVerification'].includes(ruleType)" label="是否启用环境库表映射" prop="isMappingMode">
                    <FRadioGroup v-model="formDataSource.isMappingMode" class="form-edit-input" @change="radioChange">
                        <FRadio :value="true">是</FRadio>
                        <FRadio :value="false">否</FRadio>
                    </FRadioGroup>
                    <div class="form-preview-label">{{formDataSource.isMappingMode ? '是' : '否'}}</div>
                </FFormItem>
                <!-- 数据源环境 -->
                <FFormItem v-if="!formDataSource.isMappingMode" label="数据源环境" prop="linkis_datasource_envs">
                    <FSelect
                        v-model="formDataSource.linkis_datasource_envs"
                        :options="envs"
                        class="form-edit-input"
                        filterable
                        clearable
                        multiple
                        labelField="env_name"
                        collapseTags
                        :collapseTagsLimit="2"
                        @change="onEnvChange($event, -1)"
                    ></FSelect>
                    <div class="form-preview-label project-tags">
                        <div v-for="(item, index) in formDataSource.linkis_datasource_envs" :key="index" class="tags-item">{{item.env_name}}</div>
                    </div>
                </FFormItem>
                <FFormItem v-else label="环境库表映射" prop="linkis_datasource_envs_mappings" class="multi-components">
                    <EnvMapping v-for="(item, index) in formDataSource.linkis_datasource_envs_mappings" :key="item" :no="index" :deletable="editMode !== 'display'" @deleteMapping="deleteMapping(index)">
                        <template v-slot:form>
                            <FForm :ref="el => { if (el) envFormRefs[index] = el; }"
                                   :model="item"
                                   :rules="getEnvFormRule(index)">
                                <FFormItem label="环境名" prop="datasource_envs">
                                    <FSelect
                                        v-model="item.datasource_envs"
                                        :options="envs"
                                        class="form-edit-input"
                                        filterable
                                        clearable
                                        multiple
                                        labelField="env_name"
                                        valueField="value"
                                        @change="onEnvChange($event, index)"
                                    ></FSelect>
                                    <div class="form-preview-label project-tags">
                                        <div v-for="(env, index) in item.datasource_envs" :key="index" class="tags-item">{{env.env_name}}</div>
                                    </div>
                                </FFormItem>
                                <FFormItem label="数据库" prop="db_name">
                                    <FSelect
                                        v-model="item.db_name"
                                        :options="dbsList[index]"
                                        class="form-edit-input"
                                        filterable
                                        clearable
                                        labelField="db_name"
                                        valueField="db_name"
                                        @change="onDBChange($event, index)"
                                    ></FSelect>
                                    <div class="form-preview-label">{{item.db_name}}</div>
                                </FFormItem>
                                <FFormItem label="库别名" prop="db_alias_name">
                                    <FInput
                                        v-model="item.db_alias_name"
                                        class="form-edit-input"
                                        clearable
                                        @change="onTableChange($event, index)"
                                    />
                                    <div class="form-preview-label">{{item.db_alias_name}}</div>
                                </FFormItem>
                            </FForm>
                        </template>
                    </EnvMapping>
                    <div v-if="editMode !== 'display'" class="add-btn" @click="addMapping"><PlusCircleOutlined /><a style="display: inline-block; margin-left: 4.58px" href="javascript:;">新增</a></div>
                </FFormItem>
            </template>
            <!-- 数据库 -->
            <!-- 数据表 -->
            <template v-if="dataSourceType !== 'FPS' && !['sqlVerification'].includes(ruleType)">
                <FFormItem v-if="!verifyObjectData.isUpStream" :label="$t('crossTableCheck.Database')" prop="db_name">
                    <FSelect
                        v-model="formDataSource.db_name"
                        filterable
                        class="form-edit-input"
                        labelField="db_name"
                        valueField="db_name"
                        :options="dbs"
                        @change="onDBChange($event, -1)" />
                    <div class="form-preview-label">{{formDataSource.db_name || '--'}}</div>
                    <!-- <Tip v-if="props.tips.textShow && editMode !== 'display'" :content="props.tips.dbText"></Tip> -->
                </FFormItem>
                <FFormItem v-if="!['crossDatabaseFullVerification'].includes(ruleType)" label="数据表" prop="table_name" class="tabel">
                    <FSelect
                        v-model="formDataSource.table_name"
                        filterable
                        class="form-edit-input"
                        labelField="table_name"
                        valueField="table_name"
                        :options="tables"
                        @change="onTableChange($event, -1)" />
                    <div class="form-preview-label">{{formDataSource.table_name}}</div>
                    <!-- <Tip v-if="props.tips.textShow && editMode !== 'display'" :content="props.tips.tableText"></Tip> -->
                </FFormItem>
            </template>
            <!-- FPS模块 -->
            <template v-else-if="dataSourceType === 'FPS'">
                <template v-if="!formDataSource.file_id">
                    <FFormItem label="数据库/表" prop="file_id">
                        <TableStructureButton @click="() => tableStructrueShow = true" />
                    </FFormItem>
                </template>
                <template v-else>
                    <FFormItem v-if="!verifyObjectData.isUpStream" :label="$t('crossTableCheck.Database')" prop="db_name">
                        <FSelect
                            v-model="formDataSource.db_name"
                            filterable
                            class="form-edit-input"
                            labelField="db_name"
                            valueField="db_name"
                            :options="dbs"
                            @change="onDBChange($event, -1)" />
                        <div class="form-preview-label">{{formDataSource.db_name}}</div>
                    </FFormItem>
                    <FFormItem v-if="!['crossDatabaseFullVerification'].includes(ruleType)" label="数据表" prop="table_name">
                        <FInput
                            v-model="formDataSource.table_name"
                            class="form-edit-input"
                            @change="onTableChange($event, -1)" />
                        <div class="form-preview-label">{{formDataSource.table_name}}</div>
                        <span v-if="editMode !== 'display'" class="edit-table-icon" @click="() => tableStructrueShow = true">
                            <img src="@/assets/images/icons/edit.svg" alt="">
                        </span>
                    </FFormItem>
                </template>
            </template>
            <FieldsSelect
                ref="feildRef"
                v-model:filterColNamesStrArr="feildsData.filterColNamesStrArr"
                v-model:colNamesStrArr="feildsData.colNamesStrArr"
                :showFields="aboutColumnsSelectData.isColumnsShow"
                :showFilterFields="aboutColumnsSelectData.isFilterShow"
                :lisType="lisType"
                :columns="columns"
                :rules="colNamesRules"
            ></FieldsSelect>
            <RuleMetricSelect
                v-if="listType === 'listByTemplate'"
                ref="ruleMetricRef"
                :listType="listType"
                :defaultMetricId="verifyObjectData?.ruleMetricData?.rule_metric_id || ''"
                @getRuleMetricData="getRuleMetricData"
            ></RuleMetricSelect>
            <!-- 过滤条件 -->
            <FFormItem v-if="!['sqlVerification', 'crossDatabaseFullVerification'].includes(ruleType)" label="过滤条件" prop="filter">
                <FInput
                    v-model="formDataSource.filter"
                    class="form-edit-input"
                    type="textarea"
                    :placeholder="dataSourceType === 'FPS' ? '' : 'ds=${run_date}或ds=${run_date_std}'"
                    @change="onFilterChange"
                />
                <div class="form-preview-label">{{formDataSource.filter}}</div>
                <FTooltip v-if="editMode !== 'display'" mode="popover">
                    <ExclamationCircleOutlined class="tip edit hint" />
                    <template #title>过滤条件说明</template>
                    <template #content>
                        <div style="width: 300px;">可在过滤条件中添加分区变量表达式,用于指定运行时的分区。 例子：1. ds=${run_date},表示程序运行时的实际日期前一天，格式为ds=yyyyMMdd；ds=${run_date_std},表示程序运行时的实际日期前一天，格式为ds=yyyy-MM-dd。2.ds=${run_date-N}，表示程序运行时的实际日期前N + 1天；若今天是20210707，填写了ds=${run_date-2},运行时替换为ds=20210704。</div>
                    </template>
                </FTooltip>
            </FFormItem>
        </FForm>
        <TableStructure v-if="tableStructrueShow" v-model:show="tableStructrueShow" :index="titleIndex" :form="formDataSource" :target="props.type" />
    </div>
</template>
<script setup>
import {
    ref, computed, inject, nextTick, watch, provide, unref, onMounted,
} from 'vue';
import { useStore } from 'vuex';
import { ruleTypes, NUMBER_TYPES } from '@/common/utils';
import { cloneDeep } from 'lodash-es';
import { useI18n, request } from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import {
    ExclamationCircleOutlined, PlusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    ruleTypeChangeEvent, getDataSourceType, buildColumnData, useListener, getColumnMapData, columnData2Str,
} from '@/components/rules/utils';
import { getColumns, getTables } from '@/components/rules/utils/datasource';
import useDataSource from '@/components/rules/hook/useDataSource';
import FieldsSelect from '@/components/rules/forms/FieldsSelect';
import RuleMetricSelect from '@/components/rules/forms/RuleMetricSelect';
import EnvMapping from '@/components/rules/EnvMapping.vue';
import TableStructureButton from './forms/TableStructureButton.vue';
import TableStructure from './forms/TableStructure.vue';

// import Tip from './tip.vue';

const store = useStore();
const { t: $t } = useI18n();
const envFormRefs = ref([]);
const ruleData = computed(() => store.state.rule);


// 环境库表映射不能重复
// const duplicateDetection = (mappings, index) => {
//     if (mappings.length < 1) {
//         console.log('没有映射');
//         return false;
//     }
//     const stringfyMappings = mappings.map(item => `${item.env_id}/${item.env_name}/${item.db_name}/${item.table_name}`);
//     const targetMapping = mappings[index];
//     const stringfyMapping = `${targetMapping.env_id}/${targetMapping.env_name}/${targetMapping.db_name}/${targetMapping.table_name}`;
//     for (let i = 0; i < stringfyMappings.length; i++) {
//         if (stringfyMappings[i] === stringfyMapping && i !== index) {
//             return true;
//         }
//     }
//     return false;
// };
const getEnvFormRule = index => ({
    datasource_envs: [{
        required: true,
        trigger: 'change',
        type: 'array',
        message: $t('common.notEmpty'),
    }],
    db_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
    db_alias_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
});
// eslint-disable-next-line no-undef
const props = defineProps({
    titleIndex: {
        type: Number,
        default: 0,
    },
    tips: {
        type: Object,
        default: {},
    },
    isNormalMode: {
        type: Boolean,
        default: true,
    },
    /**
     * 当前datasource涵盖了所有规则的表单，为了满足多个数据源的情况，这里需要做差异化调整
     * single-代表只有一个数据源的情况，对应currentRuleDetail.datasource
     * source-代表两个数据源的情况下的左侧部分currentRuleDetail.source
     * target-代表两个数据源的情况下的右侧部分currentRuleDetail.target
     */
    type: {
        type: String,
        default: 'single',
    },
    // 页面的类型 普通规则组：commonList，表规则组：groupList
    lisType: {
        type: String,
        default: 'commonList',
    },
    aboutColumnsSelectData: {
        type: Object,
        default: {},
    },
    // 基于规则模版创建时有复制功能所以需要传入复制的数据
    objectData: {
        type: Object,
        default: null,
    },
});
const listType = computed(() => props.lisType);
/**
 * 规则类型的处理
 * 1. 基于规则模版的ruletype是响应式，所以provide一个对象{ruleType: xx},否则监听不到ruleType的变更
 * 2. 其他情况provide一个值
 * */
const currentRuleTypeObject = inject('ruleType');
let currentRuleType = currentRuleTypeObject;
watch(listType.value, () => {
    if (listType.value === 'listByTemplate') currentRuleType = currentRuleTypeObject.value.ruleType;
}, { immediate: true });

const showRuleTypes = ruleTypes.filter(item => item.type !== '3-2' && item.type !== '0-0'); // 不展示库一致性比对和执行参数
const ruleType = computed({
    get() {
        return ruleTypes.find(({ type }) => type === currentRuleType)?.value || '';
    },
    set(v) {
        console.log('BaseInfo set ruleType', v);
        eventbus.emit(ruleTypeChangeEvent, ruleTypes.find(({ value }) => value === v).type);
    },
});
const ruleTypeLabel = computed(() => ruleTypes.find(({ type }) => type === currentRuleType)?.label || '');
const aboutColumnsSelectData = computed(() => props.aboutColumnsSelectData);
const titleIndex = computed(() => props.titleIndex);
const clusterList = inject('clusterList');
const proxyUserList = inject('proxyUserList');
const upstreamParams = cloneDeep(unref(inject('upstreamParams')));

// 判断页面为普通规则组/表规则组，切换不同 editMode 变量
const editMode = computed(() => {
    if (listType.value === 'groupList') {
        return ruleData.value.currentProject.groupObjectEditMode;
    }
    if (listType.value === 'listByTemplate') {
        return 'edit';
    }
    return ruleData.value.currentProject.editMode;
});

// 判断是否是基于模版创建里，复制的数据源(不是复制的只传入一个timestamp)
const judgeCopyObject = () => listType.value === 'listByTemplate' && props.objectData.datasource;

const verifyObjectData = ref({
    datasource: [{
        linkis_datasource_envs_mappings: [],
        isMappingMode: false,
    }],
    target: {},
    source: {},
    isUpStream: false,
});

const feildsData = ref({
    colNamesStrArr: [],
    filterColNamesStrArr: [],
});

// 判断是否为dss中iframe
const formDataSource = ref({});
switch (props.type) {
    case 'single':
        formDataSource.value = verifyObjectData.value.datasource[0];
        break;
    case 'target':
        formDataSource.value = verifyObjectData.value.target;
        break;
    case 'source':
        formDataSource.value = verifyObjectData.value.source;
        break;
    default:
        break;
}
const {
    connections,
    dbs,
    tables,
    columns,
    envs,
    handleDbChange,
    handleTableChange,
    handleDbsDependenciesChange,
    handleDataSourcesDependenciesChange,
    handleEnvChange,
    updateDataSource,
    updateUpstreamParams,
    dbsList,
} = useDataSource(upstreamParams);

provide('dbs', dbs);

// 表单规则
const verifyObjectRules = ref({
    ruleType: [{
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
    // 库一致性比对时，数据表并不是必填项
    table_name: [{
        // eslint-disable-next-line no-use-before-define
        required: ruleType.value !== 'crossDatabaseFullVerification',
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    // FPS表单完成时file_id必有值，取file_id来判断FPS表单是否完成
    file_id: [{
        required: true,
        trigger: ['change', 'blur'],
        message: '数据库/表未完成',
    }],
    filter: [{
        required: currentRuleType !== '4-1',
        trigger: ['input'],
        message: $t('common.notEmpty'),
    }],
    isMappingMode: [{
        required: true,
        type: 'boolean',
        trigger: ['change', 'blur'],
        message: '请选择是否启用环境库表映射',
    }],
});

// eslint-disable-next-line complexity
const resetFormItems = (type = '') => {
    try {
        // 任意变化都会导致数据表置空，所级联的映射关系数据也要置空
        eventbus.emit('crossTableCheck:resetMappings');
        // 集群变化之后需要清空1、数据源类型、名称、环境、数据库、数据表
        switch (type) {
            case 'cluster':
                formDataSource.value.type = '';
                formDataSource.value.linkis_datasource_id = '';
                connections.value = [];
                formDataSource.value.linkis_datasource_envs = [];
                envs.value = [];
                if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(formDataSource.value.type)) {
                    formDataSource.value.db_name = '';
                    dbs.value = [];
                    formDataSource.value.table_name = '';
                    tables.value = [];
                }
                break;
            case 'proxy_user':
                formDataSource.value.linkis_datasource_id = '';
                formDataSource.value.linkis_datasource_envs = [];
                envs.value = [];
                if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(formDataSource.value.type)) {
                    formDataSource.value.db_name = '';
                    dbs.value = [];
                    formDataSource.value.table_name = '';
                    tables.value = [];
                }
                break;
            case 'type':
                // 为关系型数据库时补充linkis_datasource_type
                if (['tdsql', 'mysql'].includes(formDataSource.value.type)) {
                    formDataSource.value.linkis_datasource_type = formDataSource.value.type;
                } else {
                    formDataSource.value.linkis_datasource_type = null;
                }
                formDataSource.value.linkis_datasource_id = '';
                connections.value = [];
                formDataSource.value.linkis_datasource_envs = [];
                envs.value = [];
                if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(formDataSource.value.type)) {
                    formDataSource.value.db_name = '';
                    dbs.value = [];
                    formDataSource.value.table_name = '';
                    tables.value = [];
                }
                break;
            case 'source_name':
                formDataSource.value.linkis_datasource_envs = [];
                envs.value = [];
                if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(formDataSource.value.type)) {
                    formDataSource.value.db_name = '';
                    dbs.value = [];
                    formDataSource.value.table_name = '';
                    tables.value = [];
                }
                break;
            case 'db':
                if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(formDataSource.value.type)) {
                    formDataSource.value.table_name = '';
                    tables.value = [];
                }
                break;
            case 'mappingMode':
                if (['sqlVerification'].includes(ruleType.value)) {
                    formDataSource.value.linkis_datasource_envs = [];
                    dbs.value = [];
                    tables.value = [];
                    formDataSource.value.db_name = '';
                    formDataSource.value.table_name = '';
                    formDataSource.value.linkis_datasource_envs_mappings = [];
                    dbsList.value = [];
                }
                break;
            default:
                break;
        }
        // important需要更新hook的数据
        console.log(verifyObjectData.value, 'verifyObjectData.value');
        updateDataSource(verifyObjectData.value, props.type);
        // 更新store
        const tempData = cloneDeep(formDataSource.value);
        const targetTempData = {};
        const target = cloneDeep(ruleData.value.currentRuleDetail);
        switch (props.type) {
            case 'single':
                targetTempData.datasource = [tempData];
                targetTempData.datasource[0].col_names = [];
                targetTempData.isUpStream = verifyObjectData.value.isUpStream;
                break;
            case 'source':
                targetTempData.source = tempData;
                targetTempData.isUpStream = target.isUpStream || verifyObjectData.value.isUpStream;
                targetTempData.filter_col_names = [];
                break;
            case 'target':
                targetTempData.target = tempData;
                break;
            default:
                break;
        }
        targetTempData.isUpStream = verifyObjectData.value.isUpStream;
        store.commit('rule/updateCurrentRuleDetail', targetTempData);
        eventbus.emit('SHOULD_UPDATE_NECESSARY_DATA', type);
    } catch (err) {
        console.warn(err);
    }
};
// 集群/代理用户/数据源类型/数据源名称变化且ruleType为sqlVerification时，迭代重置tbsList，dbsList中元素，迭代重置linkis_datasource_envs_mappings中元素
const clearList = () => {
    if (['sqlVerification'].includes(ruleType.value) && formDataSource.value.isMappingMode) {
        for (let i = 0; i < formDataSource.value.linkis_datasource_envs_mappings.length; i++) {
            dbsList.value[i] = [];
            formDataSource.value.linkis_datasource_envs_mappings[i] = {
                env_id: '',
                env_name: '',
                db_name: '',
                db_alias_name: '',
            };
        }
    }
};
const radioChange = async (val) => {
    resetFormItems('mappingMode');
    if (val) {
        // eslint-disable-next-line no-use-before-define
        await addMapping();
    }
};
// 集群选择变化
const onClusterChange = async () => {
    clearList();
    resetFormItems('cluster');
};

// 代理用户变化
const onProxyUserChange = async () => {
    clearList();
    resetFormItems('proxy_user');
    await nextTick();
    // eslint-disable-next-line no-use-before-define
    handleDataSourcesDependenciesChange(map.value);
    if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(formDataSource.value.type)) {
        handleDbsDependenciesChange();
        handleDbChange();
    }
};

// 数据源类型变化
const onSourceTypeChange = async () => {
    clearList();
    resetFormItems('type');
    await nextTick();
    // eslint-disable-next-line no-use-before-define
    handleDataSourcesDependenciesChange(map.value);
    if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(formDataSource.value.type)) {
        handleDbsDependenciesChange();
        handleDbChange();
    }
};

// 数据源名称变化
// 数据源名称会影响库、表和环境，环境与库表之间没影响关系
const connection = computed(() => connections.value.find(v => v.value === formDataSource.value.linkis_datasource_id?.toString()));
const onSourceNameChange = async (value) => {
    clearList();
    resetFormItems('source_name');
    await nextTick();
    if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(formDataSource.value.type)) {
        handleDbsDependenciesChange();
    }
    handleEnvChange();
};

// 设置数据源名称
watch(connection, (val) => {
    if (val) {
        formDataSource.value.linkis_datasource_name = val?.label;
        formDataSource.value.linkis_datasource_version_id = val?.versionId;
    }
}, { deep: true });

// 数据库变化
const onDBChange = async (val, index = -1) => {
    // 非环境库表的映射中，DBChange不会传入参数，取默认值-1；环境库表映射中，传入index为环境库表映射列表的序号
    if (index !== -1) {
        formDataSource.value.linkis_datasource_envs_mappings[index].db_alias_name = '';
    }
    resetFormItems('db');
    await nextTick();
    handleDbChange(index);
};

// 数据表变化
const onTableChange = async (val, index = -1) => {
    // 非环境库表的映射中，TableChange不会传入参数，取默认值-1；环境库表映射中，传入index为环境库表映射列表的序号
    resetFormItems();
    await nextTick();
    if (index === -1) {
        handleTableChange(props.type);
    }
};

// 过滤条件变化
const onFilterChange = async () => {
    resetFormItems();
};
// 数据源环境变化
const onEnvChange = async (val, index = -1) => {
    // 非环境库表的映射中，EnvChange不会传入参数，取默认值-1；环境库表映射中，传入index为环境库表映射列表的序号
    // 环境库表映射中，环境变化需要清空对应映射的相关内容
    if (index !== -1) {
        formDataSource.value.linkis_datasource_envs_mappings[index].db_alias_name = '';
        formDataSource.value.linkis_datasource_envs_mappings[index].db_name = '';
    }
    // 自定义sql中不为环境映射的时候不对数据源环境的变化做处理
    if (formDataSource.value.isMappingMode || !['sqlVerification'].includes(ruleType.value) || ['fps'].includes(formDataSource.value.type)) {
        handleDbsDependenciesChange(index);
    }
    resetFormItems();
    await nextTick();
};
const dataSourceTypeList = ref([]);
// 数据源类型的相关处理
// 表文件校验可选数据源类型仅有Hive
// const handleCurDataSourceTypeList = () => {
//     if (ruleType.value === 'documentVerification') {
//         return dataSourceTypeList.filter(item => item.value === 'hive');
//     }
//     if (props.isNormalMode) {
//         return dataSourceTypeList;
//     }
//     return dataSourceTypeList.filter(item => item.value !== 'fps');
// };
const currentDataSourceTypeList = computed(() => {
    if (ruleType.value === 'documentVerification') {
        return dataSourceTypeList.value.filter(item => item.value === 'hive');
    }
    if (props.isNormalMode) {
        return dataSourceTypeList.value;
    }
    return dataSourceTypeList.value.filter(item => item.value !== 'fps');
});
const dataSourceType = computed(() => getDataSourceType(formDataSource.value.type === 'fps', formDataSource.value.type, false));

// FPS表单校验完成后发送信号，监听并将数据同步到formDataSource，区别触发来源，两个verifyobject都在监听这个事件，会导致同步更新。
useListener('TABLE_STRUCTURE_CHANGE', ([datasource, target, index]) => {
    if (!(index === props.titleIndex)) return;
    // 更新store
    if (target === props.type) {
        const datasourceKeys = Object.keys(datasource);
        for (let i = 0; i < datasourceKeys.length; i++) {
            formDataSource.value[datasourceKeys[i]] = datasource[datasourceKeys[i]];
        }
        const tempData = cloneDeep(verifyObjectData.value);
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
        store.commit('rule/updateCurrentRuleDetail', targetTempData);
        eventbus.emit('SHOULD_UPDATE_NECESSARY_DATA');
    }
});
// eslint-disable-next-line complexity
useListener('IS_RULE_DETAIL_DATA_LOADED', async () => {
    try {
        let target = null;
        // 基于规则模版创建时，如果是复制的数据源也需要做初始化处理
        if (judgeCopyObject()) {
            target = cloneDeep(props.objectData);
            // 处理校验字段反显
            const colNamesStrArr = getColumnMapData(target.colNamesStrArr);
            feildsData.value.colNamesStrArr = colNamesStrArr;

            // 需要过滤字段
            if (aboutColumnsSelectData.value.isFilterShow && colNamesStrArr.length) {
                const allColumns = await getColumns({
                    clusterName: target.datasource[0].cluster_name,
                    proxyUser: target.datasource[0].proxy_user,
                    dataSourceType: target.datasource[0].type,
                    dataSourceId: target.datasource[0].linkis_datasource_id,
                    dbName: target.datasource[0].db_name,
                    tableName: target.datasource[0].table_name,
                    upstreamParams,
                });
                const filterColNamesStrArr = allColumns.filter(item => !colNamesStrArr.includes(item.column_name))
                    .filter(item => item && item.column_name && item.data_type)
                    .map(item => ({ column_name: item.column_name, data_type: item.data_type }));
                // 更新数据
                feildsData.value.filterColNamesStrArr = getColumnMapData(filterColNamesStrArr);
            }
        } else if (listType.value === 'listByTemplate') {
            // 如果不是复制的，不需要初始化
            return;
        } else {
            target = cloneDeep(ruleData.value.currentRuleDetail);
        }
        // props.type决定获取的数据
        const datasource = props.type === 'single' ? target.datasource[0] : target[props.type];

        // 跨表的情况下，数据取自target和source，target和source对象中没有cluster_name属性，从对象上层字段取出进行补充。
        if (!datasource?.cluster_name) {
            datasource.cluster_name = target.cluster_name;
        }
        // 基于规则模版创建 如果没有选择数据源，不需要初始化选项了
        if (!datasource.type && listType.value !== 'listByTemplate') {
            // 默认是hive
            datasource.type = 'hive';
        }
        updateUpstreamParams(target.isUpStream, target.rule_name, target.cs_id);
        updateDataSource(cloneDeep(target), props.type);
        // eslint-disable-next-line no-use-before-define
        await handleDataSourcesDependenciesChange(map.value);
        datasource.isMappingMode = datasource?.linkis_datasource_envs_mappings?.length > 0 && ruleType.value === 'sqlVerification';
        // 非自定义SQL做的初始化
        if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(datasource.type)) {
            await handleDbsDependenciesChange();
            await handleDbChange();
        } else if (datasource.isMappingMode) {
            // 自定义SQL 有环境库表映射需要初始化二维数组
            dbsList.value = [];
            for (let i = 0; i < datasource.linkis_datasource_envs_mappings.length; i++) {
                dbsList.value.push([]);
                await handleDbsDependenciesChange(i);
                await handleDbChange(i);
            }
        }
        // 数据源环境只有在mysql，tdsql的情况下请求
        if (['mysql', 'tdsql'].includes(datasource.type)) {
            // 基于规则模版创建-高级设置，传进数据可能没有linkis_datasource_id
            if (datasource.linkis_datasource_id) await handleEnvChange();
        }

        if (datasource?.type?.toLowerCase() === 'fps') {
            // 基于规则创建的情况，file_table_desc是个数组
            const fileTableDesc = Array.isArray(datasource.file_table_desc) ? columnData2Str(datasource.file_table_desc) : datasource.file_table_desc;
            datasource.file_table_desc = buildColumnData(fileTableDesc);
            if (Array.isArray(datasource.file_table_desc)) {
                columns.value = datasource.file_table_desc.map(item => ({ column_name: item.column_name, data_type: item.data_type }));
                eventbus.emit('UPDATE_COLUMN_LIST', { data: columns.value, target: props.type });
            }
        } else if (!['sqlVerification'].includes(ruleType.value) || ['fps'].includes(datasource.type)) {
            // 自定义Sql不需要表的相关信息
            await handleTableChange(props.type);
        }
        // 数据展示相关
        verifyObjectData.value = target;
        // 数据操作相关
        formDataSource.value = datasource;
        // datasource的相关下拉框value都为string
        formDataSource.value.linkis_datasource_id = String(formDataSource.value.linkis_datasource_id);
        // 替换引用值，value为对象时，select组件不会做isEqual，而是去比对地址
        if (!datasource.isMappingMode) {
            formDataSource.value.linkis_datasource_envs = formDataSource.value.linkis_datasource_envs?.map(v => envs.value.find(i => i.env_id === v.env_id)?.value || '') || null;
        } else {
            for (let i = 0; i < formDataSource.value.linkis_datasource_envs_mappings.length; i++) {
                formDataSource.value.linkis_datasource_envs_mappings[i].datasource_envs = formDataSource.value.linkis_datasource_envs_mappings[i].datasource_envs?.map(v => envs.value.find(env => env.env_id === v.env_id)?.value || '') || null;
            }
        }
    } catch (err) {
        console.warn(err);
    }
});


// 监听父级变化，主要用于多表联动
useListener('CLUSTER_NAME_CHANGE', (clusterName) => {
    formDataSource.value.cluster_name = clusterName;
    resetFormItems('cluster');
});
// 监听上游表开启情况
useListener('UPSTREAM_CHANGE', (data) => {
    verifyObjectData.value.isUpStream = data;
    // eslint-disable-next-line no-use-before-define
    onUpStreamChange(data);
});

/** 校验字段相关逻辑 * */
// 规则模版修改时，需要重置校验字段
useListener('RULE_TEMPLATE_CHNAGE', (val) => {
    if (listType.value === 'listByTemplate') {
        feildsData.value.colNamesStrArr = [];
        feildsData.value.filterColNamesStrArr = [];
    }
});

useListener('UPDATE_COLUMN_LIST', ({ data, target }) => {
    // 基于规则模版创建时，如果是数据源为fps，创建表数据后需要更新校验字段列表
    if (listType.value === 'listByTemplate' && formDataSource.value.type?.toLowerCase() === 'fps') {
        console.log('UPDATE_COLUMN_LIST:', data);
        columns.value = data;
    }
});
// 校验字段/过滤字段校验规则
const colNamesRules = ref({
    colNamesStrArr: [{
        required: true,
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (aboutColumnsSelectData.value.field_type === '') {
                reject('请选择校验模板');
            }

            // 需要更新字段，组件支持的数组和提交给接口的数据不相同，是在麻烦
            const colNames = value.map((name) => {
                // eslint-disable-next-line camelcase
                const c = columns.value.find(({ column_name }) => column_name === name);
                return { column_name: c.column_name, data_type: c.data_type };
            });
            // verifyRuleData.value.datasource[0].col_names = colNames;
            verifyObjectData.value.colNamesStrArr = colNames;
            if (aboutColumnsSelectData.value.field_type === 1) {
                colNames.forEach((item) => {
                    const colItem = columns.value.find(citem => citem.column_name === item.column_name);
                    if (NUMBER_TYPES.every(nitem => !(colItem.data_type || '').startsWith(nitem))) {
                        reject($t('addTechniqueRule.notConformRules'));
                    }
                });
            }

            if (colNames?.length === 0) {
                reject($t('common.notEmpty'));
            }

            // 单选提示，否则默认多选
            if (!aboutColumnsSelectData.value.isMultipleChoice && colNames.length > 1) {
                if (colNames.length > 1) {
                    reject($t('toastWarn.atMost') + 1 + $t('addTechniqueRule.fields'));
                }
            }

            resolve();
        }),
    }],
    filterColNamesStrArr: [{
        required: false,
        trigger: 'change',
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (aboutColumnsSelectData.value.field_type === '') {
                reject('请选择校验模板');
            }
            if (!value || value?.length === 0) {
                value = [];
            }

            // 表规则组时需要从store拿数据
            if (!(columns.value && columns.value.length)) {
                columns.value = ruleData.value.verifyColumns;
            }
            // 需要更新字段，组件支持的数组和提交给接口的数据不相同，是在麻烦
            // 获取过滤后的数据
            const colNames = columns.value.filter(item => !value?.includes(item.column_name))
                .map(item => ({ column_name: item.column_name, data_type: item.data_type }));
            // verifyRuleData.value.datasource[0].col_names = colNames;
            // updateSqlDataSource();

            verifyObjectData.value.colNamesStrArr = colNames;

            if (aboutColumnsSelectData.value.field_type === 1) {
                colNames.forEach((item) => {
                    const colItem = columns.value.find(citem => citem.column_name === item.column_name);
                    if (NUMBER_TYPES.every(nitem => !(colItem.data_type || '').startsWith(nitem))) {
                        reject($t('addTechniqueRule.notConformRules'));
                    }
                });
            }

            resolve();
        }),
    }],
});


const verifyObjectFormRef = ref(null);
const feildRef = ref(null);
const ruleMetricRef = ref(null);
const valid = async () => {
    try {
        await verifyObjectFormRef.value.validate();
        let result = [];
        if (listType.value === 'listByTemplate') {
            result = await Promise.all([
                feildRef.value.valid(),
                ruleMetricRef.value.valid(),
            ]);
        }
        for (let i = 0; i < envFormRefs.value.length; i++) {
            await envFormRefs.value[i].validate();
        }
        return !result.includes(false);
    } catch (err) {
        console.warn(err);
        return false;
    }
};
const addMapping = async () => {
    formDataSource.value.linkis_datasource_envs_mappings.push({
        env_id: '',
        env_name: '',
        db_name: '',
        table_name: '',
    });
    dbsList.value.push([]);
    resetFormItems();
    await nextTick();
};
const deleteMapping = async (index) => {
    formDataSource.value.linkis_datasource_envs_mappings.splice(index, 1);
    dbsList.value.splice(index, 1);
    resetFormItems();
    await nextTick();
};
const tableStructrueShow = ref(false);
// 切换上下游表开关
const onUpStreamChange = (data) => {
    // 如果选择了上游表，一定是hive，但是选择hive的时候上游表和正常表都可以选择
    // fps和上游表开关不能同时开启
    updateUpstreamParams(data);
    if (data) {
        formDataSource.value.type = 'hive';
        onSourceTypeChange();
    }
};

const getRuleMetricData = (ruleMetricData) => {
    console.log('指标校验');
    verifyObjectData.value.ruleMetricData = ruleMetricData;
    console.log(ruleMetricData);
};
const map = ref({});
onMounted(async () => {
    try {
        const res = await request('/api/v1/projector/meta_data/data_source/types/custom', {}, { method: 'get', cache: true });
        dataSourceTypeList.value = res.typeList.map(item => ({
            label: item?.name?.toUpperCase() || '',
            value: item?.name,
        }));
        for (let i = 0; i < res.typeList.length; i++) {
            map.value[res.typeList[i].name] = res.typeList[i].id;
        }
        console.log('数据源类型map-', map.value);
    } catch (err) {
        console.error(err);
    }
});

// eslint-disable-next-line no-undef
defineExpose({ valid, verifyObjectData });
</script>
<style scoped lang="less">
.edit-table-icon {
    display: inline-block;
    margin-left: 10px
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
.add-btn {
    display: flex;
    align-items: center;
    cursor: pointer;
    text-align: center;
    height: 22px;
}
.multi-components {
    /deep/ .fes-form-item-content {
        display: block;
    }
}

.hint {
    color: #646670;
    display: inline-block;
    margin-left: 8px;
}
</style>
