<template>
    <div class="rule-detail-form" :class="{ edit: editMode !== 'display' }">
        <h6 v-if="listType === 'commonList'" class="wd-body-title">校验规则</h6>
        <BasicInfo v-if="listType !== 'listByTemplate'" ref="basicInfoRef" v-model:basicInfo="verifyRuleData" :lisType="lisType" prefixTitle="单表校验" />
        <FForm
            ref="verifyObjectFormRef"
            :layout="layout"
            :class="layout === 'inline' ? 'inline-form-item' : ''"
            :model="verifyRuleData"
            :rules="verifyRuleRules"
            :labelWidth="labelWidth"
            :labelPosition="editMode !== 'display' ? 'right' : 'left'"
        >
            <FFormItem :label="listType === 'listByTemplate' ? '规则模版' : $t('common.verificationTemplate')" prop="rule_template_id">
                <FSelect
                    v-model="verifyRuleData.rule_template_id"
                    class="form-edit-input"
                    filterable
                    placeholder="请选择校验模板"
                    @change="onTemplateChange"
                >
                    <FOption
                        v-for="item in checkTemplateList"
                        :key="item.template_id"
                        :value="item.template_id"
                        :label="item.template_name"
                    ></FOption>
                </FSelect>
                <div class="form-preview-label">{{getTemplateLabel}}</div>
            </FFormItem>
            <BasicInfo v-if="listType === 'listByTemplate'" ref="basicInfoRef" v-model:basicInfo="verifyRuleData" :lisType="lisType" prefixTitle="单表校验" />
            <div v-for="(w,k) in (verifyRuleData.ruleArgumentList || [])" :key="k">
                <!-- 需要下拉选择 -->
                <FFormItem v-if="w.flag" :prop="`ruleArgumentList[${k}].argument_value`" :label="w.argument_name" :rules="[{ required: true, message: $t('common.notEmpty') }]" class="form-item">
                    <FSelect
                        v-model="verifyRuleData.ruleArgumentList[k].argument_value"
                        labelField="key_name"
                        valueField="value"
                        :options="w.argsSelectList"
                        class="form-edit-input" @change="replaceParameter(k)">
                    </FSelect>
                    <div class="form-preview-label">{{getRuleArgumentName(w)}}</div>
                    <FTooltip v-if="ruleArgumentTips.textShow">
                        <ExclamationCircleOutlined class="tip edit hint" />
                        <template #content>{{ruleArgumentTips.regText.find(item => w.argument_type === item.input_type)?.description}}</template>
                    </FTooltip>
                </FFormItem>
                <!-- 标准值 -->
                <FFormItem v-else-if="w.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.STANDARD_VALUE" :prop="`ruleArgumentList[${k}].argument_value`" :label="w.argument_name" :rules="[{ required: true, message: $t('common.notEmpty') }]" class="form-item">
                    <FSelect
                        v-model="verifyRuleData.ruleArgumentList[k].argument_value"
                        labelField="label"
                        valueField="value"
                        filterable
                        :options="standardValueList"
                        class="form-edit-input" @change="replaceParameter(k)">
                    </FSelect>
                    <div class="form-preview-label">
                        {{(getEnumNameString(standardValueList, verifyRuleData.ruleArgumentList[k].argument_value) || verifyRuleData.standard_value_version_en_name)}}
                    </div>
                    <span
                        v-if="editMode !== 'create' && verifyRuleData.new_value_exists"
                        class="color-danger"
                        style="margin-left: 6px;width: 400px"
                    >
                        <ExclamationCircleOutlined />
                        &nbsp;规则校验不通过，已产生新的校验值，
                        <span class="btn-link" @click="showStandardListPanel('')">查看新值</span>
                    </span>
                    <FTooltip v-else-if="ruleArgumentTips.textShow">
                        <ExclamationCircleOutlined class="tip edit hint" />
                        <template #content>{{ruleArgumentTips.regText.find(item => w.argument_type === item.input_type)?.description}}</template>
                    </FTooltip>
                </FFormItem>
                <!-- 其他普通类型 -->
                <FFormItem v-else-if="w.argument_type !== TEMPLATE_ARGUMENT_INPUT_TYPE.FEILD" :prop="`ruleArgumentList[${k}].argument_value`" :label="w.argument_name" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                    <FInput v-model="verifyRuleData.ruleArgumentList[k].argument_value" class="form-edit-input" @input="replaceParameter(k)" />
                    <div class="form-preview-label">{{w.argument_value}}</div>
                    <!-- 枚举值和数值范围需要判断是否有新值 new_value_exists，有新值1 无新值0 -->
                    <span
                        v-if="(editMode !== 'create' && verifyRuleData.new_value_exists && w.whether_new_value)"
                        class="color-danger"
                        style="margin-left: 6px;width: 400px"
                    >
                        <ExclamationCircleOutlined />
                        &nbsp;规则校验不通过，已产生新的校验值，
                        <span class="btn-link" @click="showStandardListPanel(w.argument_type === 8 ? 'mjz' : 'szfw')">查看新值</span></span>
                    <FTooltip v-else-if="ruleArgumentTips.textShow">
                        <ExclamationCircleOutlined class="tip edit hint" />
                        <template #content>{{ruleArgumentTips.regText.find(item => w.argument_type === item.input_type)?.description}}</template>
                    </FTooltip>
                </FFormItem>
            </div>
            <FieldsSelect
                v-if="listType !== 'listByTemplate'"
                ref="feildRef"
                v-model:filterColNamesStrArr="verifyRuleData.filterColNamesStrArr"
                v-model:colNamesStrArr="verifyRuleData.colNamesStrArr"
                :lisType="lisType"
                :columns="columns"
                :showFields="!verifyRuleData.isFilterFields && fieldRuleArgument && fieldRuleArgument.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.FEILD"
                :showFilterFields="verifyRuleData.isFilterFields"
                :rules="verifyRuleRules"
                @onColumnChange="onColumnChange">
            </FieldsSelect>
            <FFormItem v-if="listType !== 'listByTemplate'" label="SQL预览">
                <div class="rule-sql-preview">
                    <p v-if="sqlPreviewString" v-html="sqlPreviewString"></p>
                </div>
            </FFormItem>
            <!-- about指标校验 -->
            <RuleConditionList
                ref="ruleconditionlistRef"
                v-model:ruleConditions="verifyRuleData.alarm_variable"
                :needUploadMetric="needUploadMetric"
                :lisType="lisType"
                :solidification="solidification"
            />
            <FFormItem label="执行参数" prop="execution_parameters_name">
                <FInput
                    v-model="verifyRuleData.execution_parameters_name"
                    class="form-edit-input excution-parameters-name-input"
                    readonly
                    :placeholder="$t('common.pleaseSelect')"
                    @focus="openExecuteParamsDrawer" />
                <div class="form-preview-label">{{verifyRuleData.execution_parameters_name || '--'}}</div>
            </FFormItem>
        </FForm>
        <!-- 选择参数模版 -->
        <FDrawer
            v-model:show="showExecuteParamsDrawer"
            :title="$t('myProject.template')"
            displayDirective="if"
            :footer="true"
            width="50%"
        >
            <template #footer>
                <FButton type="primary" @click="confirmSelect">{{$t('myProject.confirmSelect')}}</FButton>
            </template>
            <ProjectTemplate ref="templateRef" :showHeader="false" :isEmbed="true" class="project-template-style" :curExeName="verifyRuleData.execution_parameters_name"></ProjectTemplate>
        </FDrawer>
        <!-- 标准值管理 -->
        <DataRange v-model:show="showStandardList" v-model:data="verifyRuleData.ruleArgumentList" :actionType="templateActionType" :rule="currentRule" :DQMWorkflow="DQMWorkflow" @updateSqlPreview="updateSqlPreview" />
    </div>
</template>
<script setup>
import {
    ref, computed, provide, nextTick, watch, inject, unref, onMounted,
} from 'vue';
import { useStore } from 'vuex';
import { useI18n, request } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import eventbus from '@/common/useEvents';
import BasicInfo from '@/components/rules/BasicInfo';
import ProjectTemplate from '@/pages/projects/template';
import { NUMBER_TYPES } from '@/common/utils';
import { TEMPLATE_TYPES, TEMPLATE_ARGUMENT_INPUT_TYPE } from '@/common/constant';
import {
    ExclamationCircleOutlined, FileOutlined, PlusOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    placeholderPrompt, parseSqlTpl, initRuleArgsList, getColumnMapData, getReplacePlaceholder, getSqlPreviewString, getEnumNameString, useListener,
} from '@/components/rules/utils';
import { fetchTemplateDetail } from '@/pages/rules/template/api';
import { fetchTemplatesWithCache, fetchTemplatesOfListByTemplate } from '@/components/rules/api/index';
import { getColumns, getTables } from '@/components/rules/utils/datasource';
import RuleConditionList from '@/components/rules/forms/RuleConditionList';
import FieldsSelect from '@/components/rules/forms/FieldsSelect';
import useTemplateSelector from '@/components/rules/hook/useTemplateSelector';
import { useNewFormModel } from '@/common/useModel';
import DataRange from './dataRange.vue';

const store = useStore();
const { t: $t } = useI18n();
provide('ruleType', '1-1');
const ruleData = computed(() => store.state.rule);
const currentTemplateId = ref();
// eslint-disable-next-line no-undef
const props = defineProps({
    layout: {
        type: String,
        default: 'horizontal',
    },
    // 表规则组需要传入默认的rule数据
    rule: {
        type: Object,
        default: null,
    },
    // 页面的类型 普通规则组：commonList，表规则组：groupList，基于规则模版创建listByTemplate
    lisType: {
        type: String,
        default: 'commonList',
    },
    fpsColumns: {
        type: Array,
        default: [],
    },
});

// eslint-disable-next-line no-undef
const emit = defineEmits(['update:rule']);
const listType = computed(() => props.lisType);
// label宽度
const labelWidth = computed(() => {
    if (listType.value === 'groupList') {
        return 70;
    }
    return 96;
});
// 表规则组时使用 props 传入的数据，普通规则组则使用 store的 数据
const currentRule = computed(() => {
    if (listType.value === 'groupList') {
        return props.rule;
    }
    return ruleData.value.currentRuleDetail;
});

// 判断页面为普通规则组/表规则组，切换不同 editMode 变量
const editMode = computed(() => {
    if (listType.value === 'groupList') {
        return ruleData.value.currentProject.groupRuleEditMode;
    }
    if (listType.value === 'listByTemplate') {
        return 'edit';
    }
    return ruleData.value.currentProject.editMode;
});

const verifyRuleData = ref({
    datasource: [{}],
    alarm_variable: [{}],
});

// 规则对应模版的配置
const ruleConfig = ref({});
// 校验字段相关
const fieldRuleArgument = ref();
// 加载模板
// 动态输入项的提示
const ruleArgumentTips = ref({});
// 动态参数
const checkTemplateList = ref([]);
const sqlDataSource = ref({});
const upstreamParams = cloneDeep(unref(inject('upstreamParams')));

const handleCloumnsAndSQL = async () => {
    if (listType.value === 'listByTemplate') return;

    const datasource = verifyRuleData.value.datasource[0];
    // 主要用于数据匹配
    const colNamesStrArr = getColumnMapData(datasource.col_names);
    verifyRuleData.value.colNamesStrArr = colNamesStrArr;
    // 需要过滤字段
    if (verifyRuleData.value.isFilterFields && colNamesStrArr.length) {
        const allColumns = await getColumns({
            clusterName: datasource.cluster_name,
            proxyUser: datasource.proxy_user,
            dataSourceType: datasource.type,
            dataSourceId: datasource.linkis_datasource_id,
            dbName: datasource.db_name,
            tableName: datasource.table_name,
            upstreamParams,
        });
        const filterColNamesStrArr = allColumns.filter(item => !colNamesStrArr.includes(item.column_name))
            .filter(item => item && item.column_name && item.data_type)
            .map(item => ({ column_name: item.column_name, data_type: item.data_type }));
        // 更新数据
        verifyRuleData.value.filterColNamesStrArr = getColumnMapData(filterColNamesStrArr);
    }

    const { template_variable: templateVariable } = verifyRuleData.value;

    // 下面字段必须要{}包裹，不然可能死循环
    sqlDataSource.value = {
        // eslint-disable-next-line
        '${database}': datasource.db_name,
        // eslint-disable-next-line
        '${table}': datasource.table_name,
        // eslint-disable-next-line
        '${fields}': colNamesStrArr,
        // eslint-disable-next-line
        '${field_replace_null_concat}': colNamesStrArr,
        // eslint-disable-next-line
        '${filter}': datasource.filter,
    };

    if (Array.isArray(templateVariable)) {
        const sqlKeys = Object.keys(sqlDataSource.value);
        templateVariable.forEach((item) => {
            const key = `$\{${item.alias}}`;
            const hasSqlKey = sqlKeys.includes(key);
            if (!hasSqlKey || (hasSqlKey && !sqlDataSource.value[key])) {
                sqlDataSource.value[key] = item.value;
            }
        });
    }
};

async function loadVerifyTpl(dataSourceType = 'hive') {
    try {
        let resp = null;
        // 基于规则模版时，由于权限需要切换另一个接口
        if (listType.value === 'listByTemplate') {
            resp = await fetchTemplatesOfListByTemplate({
                template_type: TEMPLATE_TYPES.SINGLE_TABLE_CHECK_TYPE,
            });
        } else {
            resp = await fetchTemplatesWithCache({
                data_source_type: dataSourceType || 'hive',
                page: 0,
                size: 512,
                template_type: TEMPLATE_TYPES.SINGLE_TABLE_CHECK_TYPE,
            });
        }
        checkTemplateList.value = (listType.value === 'listByTemplate' ? resp : resp.data) || [];
        store.commit('rule/setSingleCheckTemplateList', cloneDeep(checkTemplateList.value));

        handleCloumnsAndSQL();

        const { template_variable: templateVariable } = verifyRuleData.value;

        if (Array.isArray(templateVariable)) {
            verifyRuleData.value.ruleArgumentList = verifyRuleData.value.ruleArgumentList?.map((arg) => {
                const args = { ...arg };
                templateVariable.forEach((tv) => {
                    if (arg.argument_id === tv.input_meta_id) {
                        let selected;
                        if (arg.argsSelectList && arg.argsSelectList.length > 0) {
                            selected = arg.argsSelectList.find(it => it.value === tv.value);
                        }
                        args.argument_value = selected ? selected.value : tv.value;
                    }
                });
                return args;
            });
        }
    } catch (err) {
        console.log(err);
    }
}

const getTemplateLabel = computed(() => {
    const result = checkTemplateList.value.filter(item => item.template_id === currentRule.value.rule_template_id);
    if (result) {
        return result[0]?.template_name;
    }
    return '';
});

const basicInfoRef = ref(null);
const verifyObjectFormRef = ref(null);
const ruleconditionlistRef = ref(null);
const feildRef = ref(null);
const valid = async () => {
    try {
        await verifyObjectFormRef.value.validate();
        const result = await Promise.all([
            basicInfoRef.value.valid(),
            ruleconditionlistRef.value.valid(),
        ]);
        let res = true;
        if (listType.value !== 'listByTemplate') {
            res = await feildRef.value.valid();
        }
        if (listType.value !== 'groupList') {
            // 更新store，给save调用
            store.commit('rule/updateCurrentRuleDetail', cloneDeep(verifyRuleData.value));
        }

        return res && !result.includes(false);
    } catch (err) {
        console.warn(err);
        return false;
    }
};
// 是否需要上报指标
const needUploadMetric = computed(() => verifyRuleData.value.upload_abnormal_value || verifyRuleData.value.upload_rule_metric_value);
// 选择模板
const templateRef = ref(null);
const datasourceComFormRef = ref(null);
const {
    showExecuteParamsDrawer,
    openExecuteParamsDrawer,
    confirmSelect,
} = useTemplateSelector(templateRef, (value) => {
    console.log('选择的参数模板', value);
    verifyRuleData.value.execution_parameters_name = value.execution_parameters_name;
    verifyRuleData.value.upload_abnormal_value = value.upload_abnormal_value;
    verifyRuleData.value.upload_rule_metric_value = value.upload_rule_metric_value;
    // 是否需要上报指标（因为默认后台返回的数据如果有上报指标，肯定有指标值，所以这里只监听用户修改参数模版后的情况）
    needUploadMetric.value = value.upload_abnormal_value || value.upload_rule_metric_value;
    // 表规则需要特殊处理，carolinali todo
    if (listType.value !== 'groupList') {
        store.commit('rule/updateCurrentRuleDetail', cloneDeep(verifyRuleData.value));
    }
    // 需要手动触发更新
    verifyObjectFormRef.value.validate('execution_parameters_name');
    console.log('页面数据', verifyRuleData.value);
});

const checkFieldList = ref([]);
provide('checkFieldList', computed(() => checkFieldList.value));

// 标准值
const standardValueList = ref([]);
let isLoadingStandard = false;
const getStandValueList = async () => {
    try {
        if (isLoadingStandard) {
            return;
        }
        isLoadingStandard = true;
        const { data = [] } = await request('/api/v1/projector/standardValue/query', {
            page: 0,
            size: 2147483647,
        }, { mergeRequest: true });
        // value 需要是字符串，不然没法拼接
        standardValueList.value = data.map(item => ({
            label: item.en_name,
            value: `${item.edition_id}`,
        }));
        isLoadingStandard = false;
    } catch (err) {
        console.warn(err);
        isLoadingStandard = false;
    }
};

const onStandardChange = () => {
    if ((verifyRuleData.value.standard_value) && standardValueList.value.length === 0) {
        getStandValueList();
    }
};

// 规则模板相关的逻辑
// 在基于规则模版创建时，需要通知verfiyObject关于校验字段/过滤字段展示和校验的相关信息
const notifyColumnsData = () => {
    if (listType.value !== 'listByTemplate') return;
    const aboutColumnsSelectData = {
        isColumnsShow: !verifyRuleData.value.isFilterFields && fieldRuleArgument.value && fieldRuleArgument.value.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.FEILD,
        isFilterShow: verifyRuleData.value.isFilterFields,
        field_type: ruleConfig.value.field_type,
        isMultipleChoice: verifyRuleData.value.isFilterFields ? true : fieldRuleArgument.value?.field_multiple_choice,
    };
    eventbus.emit('notifyColumnsData', aboutColumnsSelectData);
    // 通知多个数据源重置校验字段数据
    eventbus.emit('RULE_TEMPLATE_CHNAGE');
};
// sql模板
const sqlTpl = ref('');
const solidification = ref(false); // 规则模板是否固化了校验条件
const handleTemplateChange = async (id) => {
    try {
        if (!id) {
            return;
        }
        // 模板变化清空字段
        verifyRuleData.value.colNamesStrArr = [];
        if (listType.value !== 'listByTemplate') verifyRuleData.value.datasource.col_names = [];
        const metaData = await request(`api/v1/projector/rule_template/meta_input/${id}`, {}, {
            cache: true, // 防止重复提交出错
            method: 'get',
        });
        const result = cloneDeep(metaData);
        checkFieldList.value = result.template_output || [];
        ruleConfig.value = {
            field_type: result.field_type,
            placeholders: result.sql_display_response.placeholders,
            field_num: result.field_num,
        };
        verifyRuleData.value.isFilterFields = !!result.filter_fields;

        if (verifyRuleData.value.isFilterFields) verifyRuleData.value.filterColNamesStrArr = [];
        // 清空字段
        if (result.field_num === 0) {
            verifyRuleData.value.colNamesStrArr = [];
            // verifyRuleData.value.datasource[0].col_names = [];
            verifyRuleData.value.filterColNamesStrArr = [];
        }
        // 提示的生成
        ruleArgumentTips.value = placeholderPrompt({
            config: ruleConfig.value,
            id,
        });

        if (listType.value !== 'listByTemplate') {
            // 解释sql模板
            sqlTpl.value = result.sql_display_response?.show_sql;
            parseSqlTpl({
                placeholders: result.sql_display_response?.placeholders,
                isEnum: verifyRuleData.value.datasource[0]?.isEnum,
            });
        }

        // standard_value为了保存时区分是否有标准值的标识位
        verifyRuleData.value.standard_value = false;
        result.rule_arguments.forEach((item) => {
            if (item.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.STANDARD_VALUE) {
                verifyRuleData.value.standard_value = true;
            }
        });
        onStandardChange();

        fieldRuleArgument.value = null;
        if (result.rule_arguments && result.rule_arguments.length) {
            const allRuleArgumentList = initRuleArgsList({
                list: result.rule_arguments,
                placeholders: result.sql_display_response.placeholders,
            });
            // 把校验字段单独过滤出来
            fieldRuleArgument.value = allRuleArgumentList.find(item => item.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.FEILD);
            verifyRuleData.value.ruleArgumentList = allRuleArgumentList;
        } else {
            verifyRuleData.value.ruleArgumentList = [];
        }

        // 数值范围占位符控件顺序
        const order = [
            TEMPLATE_ARGUMENT_INPUT_TYPE.MIN,
            TEMPLATE_ARGUMENT_INPUT_TYPE.EXPRESSION,
            TEMPLATE_ARGUMENT_INPUT_TYPE.MAX,
        ];
        // 控件数（已除去校验字段）为3或以上才可能是数值范围，排序
        if (verifyRuleData.value.ruleArgumentList.length > 2) {
            verifyRuleData.value.ruleArgumentList.sort((a, b) => order.indexOf(a.argument_type) - order.indexOf(b.argument_type));
        }
        // 是否固化校验条件相关逻辑
        const template = await fetchTemplateDetail({ template_id: id });
        if (listType.value === 'listByTemplate') {
            verifyRuleData.value.cn_name = template.template_name;
            verifyRuleData.value.rule_name = template.en_name;
        }
        console.log('rule template:', template);
        if (template.whether_solidification) {
            solidification.value = true;
            verifyRuleData.value.alarm_variable[0].check_template = template.check_template;
        } else {
            solidification.value = false;
        }

        notifyColumnsData();
    } catch (err) {
        console.warn(err);
    }
};

// ruleargumentList是数组的时候需要单独处理反显的逻辑
const getRuleArgumentName = (arg) => {
    const result = arg.argsSelectList.filter(item => item.value === arg.argument_value);
    if (result && result.length > 0) {
        return result[0].key_name;
    }
    return '';
};

const onTemplateChange = async (id) => {
    currentTemplateId.value = id;
    handleTemplateChange(id);
    verifyRuleData.value.rule_template_name = checkTemplateList.value.find(v => v.template_id === id)?.template_name || '';
    // 清空校验参数
    ruleconditionlistRef.value.reset();
};

// 枚举值列表
const enumTypeList = ref([{
    label: '枚举值',
    value: false,
}, {
    label: '标准值',
    value: true,
}]);

const enumTypeString = computed(() => {
    const target = enumTypeList.value.find(item => item.value === verifyRuleData.value.standard_value);
    return target ? target.label : '';
});

const checkDataRangeValidator = (rule, value) => /^\d*$/.test(value);

const getSzfwPreviewText = (data) => {
    const result = [];
    for (let i = 0; i < data.length; i++) {
        result.push(data[i].argument_value);
    }
    return result.join('&le;');
};

// 模板参数替换
const replaceParameter = (index) => {
    const ruleItem = verifyRuleData.value.ruleArgumentList[index] || {};
    const pStr = getReplacePlaceholder({
        placeholders: ruleConfig.value.placeholders,
        condition: {
            key: 'placeholder_id',
            value: ruleItem.argument_id,
        },
    });
    sqlDataSource.value[pStr] = ruleItem.argument_value || '';
};

const showStandardList = ref(false);
const templateActionType = ref('');
const showStandardListPanel = async (type = '') => {
    if (type) {
        // 预览模式下点击查看新值，数值范围和枚举值的情况下需要把编辑模式打开
        // 改1：普通项目下打开编辑模式，工作流项目下不打开编辑模式,因此工作流项目下的枚举值保存后直接向后端返回修改数据。
        // 改2：修改为工作流项目下不能对新值做任何操作
        const {
            isEmbedInFrame,
            isWorkflowProject,
        } = ruleData.value.currentProject;
        if (!isWorkflowProject || (isWorkflowProject && isEmbedInFrame)) {
            eventbus.emit('openFormEditEvent');
        }
        await nextTick();
    }
    showStandardList.value = true;
    templateActionType.value = type;
};
// eslint-disable-next-line no-restricted-globals
const DQMWorkflow = computed(() => (top === self || window.__MICRO_APP_ENVIRONMENT__) && upstreamParams.workflowProject === 'true');
// SQL预览
const sqlPreviewString = computed(() => getSqlPreviewString({
    // SQL模板
    sqlTpl: sqlTpl.value,
    // SQL相关插值
    clusterRule: sqlDataSource.value,
    // 模板id
    selectedTemplateId: verifyRuleData.value.rule_template_id,
    standardValue: verifyRuleData.value.standard_value,
    standardValueList: standardValueList.value,
}));

// 获取字段列表
const columns = ref([]);

const COLUMN_INPUT_TYPES = [4, 6, 24];
const updateSqlDataSource = () => {
    if (sqlDataSource.value) {
        const dbstr = getReplacePlaceholder({ placeholders: ruleConfig.value.placeholders, type: [5] });
        const tableStr = getReplacePlaceholder({ placeholders: ruleConfig.value.placeholders, type: [3] });
        const columnStr = getReplacePlaceholder({ placeholders: ruleConfig.value.placeholders, type: COLUMN_INPUT_TYPES });
        dbstr && (sqlDataSource.value[dbstr] = verifyRuleData.value.datasource[0].db_name);
        tableStr && (sqlDataSource.value[tableStr] = verifyRuleData.value.datasource[0].table_name);
        if (columnStr && Array.isArray(verifyRuleData.value.datasource[0].col_names)) {
            sqlDataSource.value[columnStr] = verifyRuleData.value.datasource[0].col_names.map(item => item.column_name);
        }
        if (verifyRuleData.value.datasource[0].filter) {
            // eslint-disable-next-line no-template-curly-in-string
            sqlDataSource.value['${filter}'] = verifyRuleData.value.datasource[0].filter;
        }
    }
};

// 字段变化
const onColumnChange = (selectedCol = []) => {
    updateSqlDataSource();
};

// 表单规则
const verifyRuleRules = ref({
    rule_template_id: [{
        required: true,
        trigger: 'change',
        type: 'number',
        message: $t('common.notEmpty'),
    }],
    execution_parameters_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
    colNamesStrArr: [{
        required: true,
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (ruleConfig.value.field_type === '') {
                reject('请选择校验模板');
            }

            // 表规则组时需要从store拿数据
            if (!(columns.value && columns.value.length)) {
                columns.value = ruleData.value.verifyColumns;
            }

            // 需要更新字段，组件支持的数组和提交给接口的数据不相同，是在麻烦
            const colNames = value.map((name) => {
                // eslint-disable-next-line camelcase
                const c = columns.value.find(({ column_name }) => column_name === name);
                return { column_name: c.column_name, data_type: c.data_type };
            });
            verifyRuleData.value.datasource[0].col_names = colNames;
            updateSqlDataSource();


            if (ruleConfig.value.field_type === 1) {
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
            if (!fieldRuleArgument.value.field_multiple_choice && colNames.length > 1) {
                if (colNames.length > 1) {
                    reject($t('toastWarn.atMost') + 1 + $t('addTechniqueRule.fields'));
                }
            }

            resolve();
        }),
    }],
    filterColNamesStrArr: [{
        required: false,
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (ruleConfig.value.field_type === '') {
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
            verifyRuleData.value.datasource[0].col_names = colNames;
            updateSqlDataSource();


            if (ruleConfig.value.field_type === 1) {
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

// 获取可选字段
// const isDataLoaded = ref(false);
useListener('UPDATE_COLUMN_LIST', ({ data, target }) => {
    if (listType.value !== 'groupList') {
        // 普通规则组columns的通过事件传进来
        console.log('UPDATE_COLUMN_LIST:', data);
        columns.value = data;
    }
    // if (isDataLoaded.value) {
    //     // 初始化的时候不能清空，变更的时候在做清空
    //     // 清空数据
    //     verifyRuleData.value.colNamesStrArr = [];
    //     verifyRuleData.value.datasource[0].col_names = [];
    // }
    // isDataLoaded.value = true;
});
watch(() => props.fpsColumns, () => {
    if (listType.value === 'groupList') {
        // 表规则组columns的通过prop传进来
        columns.value = props.fpsColumns;
    }
}, { immediate: true });
const handleTemplateChangePromise = () => Promise.resolve(handleTemplateChange(verifyRuleData.value.rule_template_id));
// isInitStep差异化初始和重新覆盖数据的逻辑
const init = async (isInitStep = true) => {
    const target = cloneDeep(currentRule.value);
    // 当多次调用的时候，select组件原本绑定了colNamesStrArr，变成空的时候报错，无解，只能兜底
    target.colNamesStrArr = [];
    // 避免数据覆盖
    target.alarm_variable = target.alarm_variable || [{}];
    verifyRuleData.value = target;
    // 是否需要过滤字段置空
    verifyRuleData.value.isFilterFields = false;
    // 如果是数据源修改，且用户修改过模版id，则当前模版id赋值之前用户修改选择的id
    if (!isInitStep && currentTemplateId.value) {
        verifyRuleData.value.rule_template_id = currentTemplateId.value;
        verifyRuleData.value.alarm_variable = [{}];
    }
    handleTemplateChangePromise().then(() => loadVerifyTpl(currentRule.value.datasource ? currentRule.value.datasource[0]?.type : ''));
    if (isInitStep) {
        onStandardChange();
    } else {
        updateSqlDataSource();
    }
};

// 当数据库、数据表、过滤条件、字段等发生变化的时候，需要调用updateSqlDataSource进行预览SQL更新
useListener('SHOULD_UPDATE_NECESSARY_DATA', () => {
    if (listType.value === 'groupList' || listType.value === 'listByTemplate') return;
    init(false);
});

// 初始化数据
useListener('IS_RULE_DETAIL_DATA_LOADED', () => {
    if (listType.value === 'groupList' || listType.value === 'listByTemplate') return;
    init();
});


// eslint-disable-next-line no-undef
defineExpose({ valid, verifyRuleData, init });


// 从标准值组件接收通知之后更新mysql
const updateSqlPreview = (type = 'szfw') => {
    switch (type) {
        case 'szfw':
            replaceParameter(0);
            replaceParameter(1);
            replaceParameter(2);
            break;
        case 'mjz':
            replaceParameter(0);
            break;
        default:
            break;
    }
};

onMounted(() => {
    if (listType.value === 'listByTemplate') init();
});

watch(() => verifyRuleData, () => {
    console.log('页面初始数据', verifyRuleData.value);
    // 规则修改时，如果是表规则组，则 emit 更新，用于双向绑定数据
    if (listType.value === 'groupList') {
        emit('update:rule', verifyRuleData.value);
    }
}, { deep: true });

</script>
<style lang="less">
.data-range{
    display: flex;
    align-content: center;
    .fes-form-item{
        text-align: center;
        padding: 0;
    }
}
.sperator{
    font-style: normal;
    font-family: math;
    line-height: 30px;
    padding: 0 6px;
}
</style>
<style lang="less" scoped>
@import "@/style/varible";

.inline-form-item{
    grid-template-columns: repeat(auto-fit, 268px);
    :deep(.form-edit-input){
        width: 160px;
    }
}

.data-range{
    width: 292px;
    margin-bottom: 0;
    .fes-form-item {
        margin-bottom: 0 !important;
    }
}
.hint {
    display: inline-block;
    margin-left: 8px;
    color: #646670;
}
.btn-link {
    color: @blue-color;
    cursor: pointer;
}
.color-danger {
    color: @red-color;
}
</style>
