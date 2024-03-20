/* eslint-disable no-template-curly-in-string */
<template>
    <div class="rule-detail-form" :class="{ edit: ruleData.currentProject.editMode !== 'display' }">
        <h6 class="wd-body-title">校验规则</h6>
        <BasicInfo ref="basicInfoRef" v-model:basicInfo="verifyRuleData" />
        <FForm
            ref="verifyObjectFormRef"
            :layout="layout"
            :class="layout === 'inline' ? 'inline-form-item' : ''"
            :model="verifyRuleData"
            :rules="verifyRuleRules"
            :labelWidth="96"
            :labelPosition="ruleData.currentProject.editMode !== 'display' ? 'right' : 'left'"
        >
            <FForm
                ref="comparisonFormRef"
                style="margin-bottom: 0"
                :layout="'horizonal'"
                :class="layout === 'inline' ? 'inline-form-item' : ''"
                :model="verifyRuleData"
                :rules="comparisonFormRules"
                :labelWidth="96"
                :labelPosition="ruleData.currentProject.editMode !== 'display' ? 'right' : 'left'">
                <!-- 字段数据一致性比对--17 -->
                <!-- 行数据一致性比对--20 -->
                <FFormItem :label="$t('common.verificationTemplate')" prop="multi_source_rule_template_id">
                    <FSelect
                        v-model="verifyRuleData.multi_source_rule_template_id"
                        class="form-edit-input"
                        filterable
                        placeholder="请选择校验模板"
                        style="width:315px"
                        @change="onTemplateChange(verifyRuleData.multi_source_rule_template_id)"
                    >
                        <FOption
                            v-for="item in checkTemplateList"
                            :key="item.template_id"
                            :value="item.template_id"
                            :label="item.template_name"
                        ></FOption>
                    </FSelect>
                    <div class="form-preview-label">{{verifyRuleData.rule_template_name}}</div>
                </FFormItem>
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
                    <FFormItem v-else-if="w.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.CONNECT_FIELDS" :label="w.argument_name" prop="mappings">
                        <div v-show="verifyRuleData?.mappings?.length > 0 || ruleData.currentProject.editMode !== 'display'" class="rule-form-table">
                            <MappingRelationship v-model:mappings="verifyRuleData.mappings" :datasourceGroupData="datasourceGroupData" :title="w.argument_name" @mapSaved="mapSaved('mappings', k)" @valid="validate('mappings')" />
                        </div>
                        <div v-if="!(verifyRuleData?.mappings?.length > 0 || ruleData.currentProject.editMode !== 'display')">--</div>
                    </FFormItem>
                    <FFormItem v-else-if="w.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.COMPARISON_FIELD" :label="w.argument_name" prop="compare_cols">
                        <div v-show="verifyRuleData?.compare_cols?.length > 0 || ruleData.currentProject.editMode !== 'display'" class="rule-form-table">
                            <MappingRelationship v-model:mappings="verifyRuleData.compare_cols" :datasourceGroupData="datasourceGroupData" :title="w.argument_name" @mapSaved="mapSaved('compare_cols', k)" @valid="validate('compare_cols')" />
                        </div>
                        <div v-if="!(verifyRuleData?.compare_cols?.length > 0 || ruleData.currentProject.editMode !== 'display')">--</div>
                    </FFormItem>
                    <FFormItem
                        v-else-if="w.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.CONTRAST_TYPE"
                        :label="w.argument_name"
                        :prop="`ruleArgumentList[${k}].argument_value`"
                        :rules="[{ required: true, type: 'number', message: $t('common.notEmpty') }]">
                        <FSelect
                            v-model="verifyRuleData.ruleArgumentList[k].argument_value"
                            class="form-edit-input"
                            filterable
                            :placeholder="`请选择${w.argument_name}`"
                            style="width:315px"
                            @change="replaceParameter(k)"
                        >
                            <FOption
                                v-for="item in directionList"
                                :key="item.value"
                                :value="item.value"
                                :label="item.label"
                            ></FOption>
                        </FSelect>
                        <div class="form-preview-label">{{directionList.find(v => v.value === verifyRuleData.contrast_type)?.label || '--'}}</div>
                    </FFormItem>
                    <!-- 其他普通类型 -->
                    <FFormItem v-else :prop="`ruleArgumentList[${k}].argument_value`" :label="w.argument_name" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                        <FInput v-model="verifyRuleData.ruleArgumentList[k].argument_value" class="form-edit-input" @input="replaceParameter(k)" />
                        <div class="form-preview-label">{{w.argument_value}}</div>
                        <FTooltip v-if="ruleArgumentTips.textShow">
                            <ExclamationCircleOutlined class="tip edit hint" />
                            <template #content>{{ruleArgumentTips.regText.find(item => w.argument_type === item.input_type)?.description}}</template>
                        </FTooltip>
                    </FFormItem>
                </div>
            </FForm>
            <FForm
                ref="filterFormRef"
                style="margin-bottom: 0"
                :layout="'horizonal'"
                :class="layout === 'inline' ? 'inline-form-item' : ''"
                :model="verifyRuleData"
                :rules="filterFormRules"
                :labelWidth="96"
                :labelPosition="ruleData.currentProject.editMode !== 'display' ? 'right' : 'left'">
                <FFormItem v-if="verifyRuleData.isFilterFields" label="过滤字段">
                    <FSelect
                        v-model="verifyRuleData.filter_col_names"
                        class="form-edit-input"
                        filterable
                        multiple
                        placeholder="请选择"
                        style="width:315px"
                        collapseTags
                        :collapseTagsLimit="2"
                    >
                        <FOption
                            v-for="col in sourceTableColumns"
                            :key="col.column_name"
                            :value="col.column_name"
                            :label="`${col.column_name} (${col.data_type})`"
                        ></FOption>
                    </FSelect>
                    <div class="form-preview-label">
                        <FSpace v-if="verifyRuleData.filter_col_names?.length > 0">
                            <FTag v-for="(item, index) in verifyRuleData.filter_col_names" :key="index" type="info" size="small">
                                <FEllipsis style="max-width: 240px">{{item}}</FEllipsis>
                            </FTag>
                        </FSpace>
                        <span v-else>--</span>
                    </div>
                </FFormItem>
            </FForm>
            <FFormItem label="SQL预览">
                <div class="rule-sql-preview">
                    <p v-if="sqlPreviewString" v-html="sqlPreviewString"></p>
                </div>
            </FFormItem>
            <RuleConditionList
                ref="ruleconditionlistRef"
                :ruleConditions="verifyRuleData.alarm_variable"
                :needUploadMetric="needUploadMetric"
                :solidification="solidification"
            />
            <FFormItem label="执行参数" prop="execution_parameters_name">
                <FInput
                    v-model="verifyRuleData.execution_parameters_name"
                    class="form-edit-input execution-parameters-name-input"
                    readonly
                    style="width:315px"
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
            <ProjectTemplate ref="templateRef" :showHeader="false" :isEmbed="true" :curExeName="verifyRuleData.execution_parameters_name" class="project-template-style"></ProjectTemplate>
        </FDrawer>
    </div>
</template>
<script setup>
/* eslint-disable no-template-curly-in-string */
import {
    ref, computed, provide, nextTick, inject, onMounted, unref,
} from 'vue';
import { useStore } from 'vuex';
import { useI18n, request } from '@fesjs/fes';
import { TEMPLATE_TYPES, TEMPLATE_ARGUMENT_INPUT_TYPE } from '@/common/constant.js';
import { clone, cloneDeep, intersectionBy } from 'lodash-es';
import eventbus from '@/common/useEvents';
import BasicInfo from '@/components/rules/BasicInfo';
import ProjectTemplate from '@/pages/projects/template';
import {
    ExclamationCircleOutlined, FileOutlined, PlusOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    getSqlPreviewString, useListener, initRuleArgsList, placeholderPrompt, getReplacePlaceholder, columnData2Str, buildColumnData,
} from '@/components/rules/utils';
import { fetchTemplateDetail } from '@/pages/rules/template/api';
import { fetchTemplatesWithCache } from '@/components/rules/api/index';
import { getColumns, getTables } from '@/components/rules/utils/datasource';
import RuleConditionList from '@/components/rules/forms/RuleConditionList';
import useTemplateSelector from '@/components/rules/hook/useTemplateSelector';
import MappingRelationship from '@/components/rules/forms/MappingRelationship.vue';

const store = useStore();
const { t: $t } = useI18n();

const currentTemplateId = ref();
const ruleData = computed(() => store.state.rule);
// 加载模板
// 动态输入项的提示
const ruleArgumentTips = ref({});
// eslint-disable-next-line no-undef
const props = defineProps({
    layout: {
        type: String,
        default: 'horizontal',
    },
});

const verifyRuleData = ref({
    datasource: [{}],
    alarm_variable: [{}],
    filter_col_names: [],
});
const ruleArgumentsList = ref([]);
// 加载模板
const checkTemplateList = ref([]);
const sqlDataSource = ref({});
// 比对方向
const directionList = ref([]);
// 校验条件
const ruleconditionlistRef = ref(null);
// sql模板
const checkFieldList = ref([]);
provide('checkFieldList', computed(() => checkFieldList.value));
// 模板
const sqlTpl = ref('');
// sql
const sql = ref();
const sqlGeneralTpl = ref();
const ruleConfig = ref({});
function subStr(str) {
    // 字符切割把最后一个and 去掉
    if (str.length > 0) {
        str = str.substr(0, str.length - 4);
    }
    return str;
}
const operationList = ['=', '!=', '>', '>=', '<', '<='];
function sqlMappingsReplace(str) {
    // 跨表准确定性校验sql预览第二条语句，需要把前缀反过来。
    return str.replace(/\b(tmp1.|tmp2.)\b/g, ($0, $1) => ({
        'tmp1.': 'tmp2.',
        'tmp2.': 'tmp1.',
    }[$1]));
}
function getMappingArgument(type, target) {
    // sql预览中的${mapping_argument}替换内容(映射关系表达式)
    // target 为指定mappings还是compare_cols
    let str = '';
    verifyRuleData.value[target]?.forEach((item) => {
        const oper = operationList[item.operation - 1];
        str += `(${item.left_statement} ${oper} ${item.right_statement}) and `;
    });
    if (type === 'secondSql') {
        str = sqlMappingsReplace(str);
    }
    return subStr(str);
}
// type决定是连接字段还是比对字段
function getMapField() {
    // 获取mappings里面的字段，sql预览展示
    const sourceColumnIsNull = [];
    const targetColumnIsNull = [];
    const target = verifyRuleData.value.mappings;
    if (target?.length > 0) {
        target.forEach((item) => {
            item.left.forEach((it) => {
                if (it.column_name.indexOf('tmp1.') > -1) {
                    sourceColumnIsNull.push(it.column_name);
                }
                if (it.column_name.indexOf('tmp2.') > -1) {
                    targetColumnIsNull.push(it.column_name);
                }
            });
            item.right.forEach((t) => {
                if (t.column_name.indexOf('tmp1.') > -1) {
                    sourceColumnIsNull.push(t.column_name);
                }
                if (t.column_name.indexOf('tmp2.') > -1) {
                    targetColumnIsNull.push(t.column_name);
                }
            });
        });
    }
    return {
        sourceColumnIsNull,
        targetColumnIsNull,
    };
}
function getSqlColumn(data, type) {
    const arr = Array.from(new Set(data));
    // sql预览中的${source_column_is_null} & ${target_column_is_null}替换内容
    let str = '';
    arr.forEach((item) => {
        str += `(${item} is null) and `;
    });
    if (type === 'secondSql') {
        str = sqlMappingsReplace(str);
    }
    return subStr(str);
}

const getSpecialSqlShowVal = (argumentType, argumentVal) => {
    let ruleItemSqlValue = null;
    // 用户修改比对类型后值为id，所以sql预览的值需要做转换
    if (argumentType === TEMPLATE_ARGUMENT_INPUT_TYPE.CONTRAST_TYPE) {
        ruleItemSqlValue = directionList.value.find(v => v.value === argumentVal)?.content;
        verifyRuleData.value.contrast_type = argumentVal;
    }
    // 连接字段sql预览需要特殊处理，且值没有存在verifyRuleData.value.ruleArgumentLis，在mapping变量里
    if (argumentType === TEMPLATE_ARGUMENT_INPUT_TYPE.CONNECT_FIELDS) {
        ruleItemSqlValue = getMappingArgument('firstSql', 'mappings').length > 0
            ? getMappingArgument('firstSql', 'mappings') : '';
    }

    // 比对字段sql预览需要特殊处理，且值没有存在verifyRuleData.value.ruleArgumentLis，在变量里
    if (argumentType === TEMPLATE_ARGUMENT_INPUT_TYPE.COMPARISON_FIELD) {
        ruleItemSqlValue = getMappingArgument('firstSql', 'compare_cols').length > 0
            ? getMappingArgument('firstSql', 'compare_cols') : '';
    }

    return ruleItemSqlValue;
};

// 模板参数替换
const replaceParameter = (index) => {
    const ruleItem = verifyRuleData.value.ruleArgumentList[index] || {};
    const ruleItemSqlValue = getSpecialSqlShowVal(ruleItem.argument_type, ruleItem.argument_value) || ruleItem.argument_value || '';
    const pStr = getReplacePlaceholder({
        placeholders: ruleConfig.value.placeholders,
        condition: {
            key: 'placeholder_id',
            value: ruleItem.argument_id,
        },
    });
    sqlDataSource.value[pStr] = ruleItemSqlValue;
};

// SQL预览
const sqlPreviewString = computed(() => getSqlPreviewString({
    sqlTpl: sqlTpl.value,
    clusterRule: sqlDataSource.value,
    selectedTemplateId: verifyRuleData.value.multi_source_rule_template_id,
}));

const getJSONParsedResult = (jsonString) => {
    try {
        const string = JSON.parse(jsonString) || null;
        return string;
    } catch (error) {
        console.log(error);
    }
};

// eslint-disable-next-line complexity
async function buildSql(isInit = false) {
    console.log('buildSql', sqlTpl.value);
    await nextTick();
    const sourceDB = verifyRuleData.value.source.db_name;
    const sourceTable = verifyRuleData.value.source.table_name;
    const filterLeft = verifyRuleData.value.source.filter;
    const targetDB = verifyRuleData.value.target.db_name;
    const targetTable = verifyRuleData.value.target.table_name;
    const filterRight = verifyRuleData.value.target.filter;

    sqlDataSource.value = {
        // '${source_db}': sourceDB,
        // '${source_table}': sourceTable,
        // '${filter_left}': filterLeft,
        // '${target_db}': targetDB,
        // '${target_table}': targetTable,
        // '${filter_right}': filterRight,
        '${left_database}': sourceDB,
        '${left_table}': sourceTable,
        '${left_filter}': filterLeft,
        '${right_database}': targetDB,
        '${right_table}': targetTable,
        '${right_filter}': filterRight,
        '${compare_argument}': '',
        '${mapping_argument}': '',
    };

    if (!verifyRuleData.value?.template_arguments?.length && !isInit) return;

    // 给占位符赋值
    if (Array.isArray(verifyRuleData.value?.template_arguments)) { // 模版参数
        // 从后台拿的数据给规则参数列表的value赋值
        verifyRuleData.value.ruleArgumentList = verifyRuleData.value.ruleArgumentList?.map((arg) => {
            const args = { ...arg };
            verifyRuleData.value?.template_arguments.forEach((tv) => {
                if (arg.argument_id === tv.argument_id) {
                    let selected;
                    if (arg.argsSelectList && arg.argsSelectList.length > 0) {
                        selected = arg.argsSelectList.find(it => it.value === tv.argument_value);
                    }
                    if (tv.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.CONTRAST_TYPE) {
                        // 比对类型比较特殊，需要从contrast_type拿到id，argument_value返回的是sql展示的字符串
                        tv.argument_value = verifyRuleData.value?.contrast_type;
                    }

                    args.argument_value = selected ? selected.value : tv.argument_value;

                    // 连接字段和比对字段需要对拿到的json字符串做反序列化，且赋值给对应变量
                    if (tv.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.CONNECT_FIELDS) {
                        verifyRuleData.value.mappings = getJSONParsedResult(tv.argument_value) || [];
                    }
                    if (tv.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.COMPARISON_FIELD) {
                        verifyRuleData.value.compare_cols = getJSONParsedResult(tv.argument_value) || [];
                    }
                }
            });
            return args;
        });

        const sqlKeys = Object.keys(sqlDataSource.value);
        verifyRuleData.value?.template_arguments.forEach((item) => {
            const key = item.argument_placeholder;
            const hasSqlKey = sqlKeys.includes(key);
            // 初始化的比对类型argument_value返回的是sql展示的字符串，不需要特殊处理，所以提前赋值
            if (item.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.CONTRAST_TYPE) sqlDataSource.value[key] = item.argument_value;
            if (!hasSqlKey || (hasSqlKey && !sqlDataSource.value[key])) {
                sqlDataSource.value[key] = getSpecialSqlShowVal(item.argument_type, item.argument_value) || item.argument_value || '';
            }
        });
    }
}

const solidification = ref(false); // 规则模板是否固化了校验条件
const handleTemplateChange = async (id, isInit = false) => {
    try {
        if (!id) {
            return;
        }

        const result = await request(`api/v1/projector/rule_template/meta_input/${id}`, {}, 'get');
        checkFieldList.value = result.template_output || [];
        verifyRuleData.value.ruleArgumentList = initRuleArgsList({
            list: result.rule_arguments,
            placeholders: cloneDeep(result.sql_display_response.placeholders),
        });
        // 占位符控件顺序
        const order = [
            TEMPLATE_ARGUMENT_INPUT_TYPE.CONNECT_FIELDS,
            TEMPLATE_ARGUMENT_INPUT_TYPE.COMPARISON_FIELD,
            TEMPLATE_ARGUMENT_INPUT_TYPE.CONTRAST_TYPE,
        ];
        verifyRuleData.value.ruleArgumentList.sort((a, b) => order.indexOf(a.argument_type) - order.indexOf(b.argument_type));

        ruleConfig.value = {
            field_type: result.field_type,
            placeholders: result.sql_display_response.placeholders,
        };
        verifyRuleData.value.isFilterFields = !!result.filter_fields;
        ruleArgumentTips.value = placeholderPrompt({
            config: ruleConfig.value,
            id,
        });
        sqlTpl.value = result.sql_display_response.show_sql;
        buildSql(isInit);
        // 是否固化校验条件相关逻辑
        const template = await fetchTemplateDetail({ template_id: id });
        console.log('rule template:', template);
        if (template.whether_solidification) {
            solidification.value = true;
            verifyRuleData.value.alarm_variable[0].check_template = template.check_template;
        } else {
            solidification.value = false;
        }
    } catch (err) {
        console.warn(err);
    }
};

const onTemplateChange = async (id) => {
    currentTemplateId.value = id;
    handleTemplateChange(id);
    verifyRuleData.value.rule_template_name = checkTemplateList.value.find(v => v.template_id === id)?.template_name || '';
    // 清空校验参数
    ruleconditionlistRef.value.reset();
    verifyRuleData.value.mappings = [];
    verifyRuleData.value.compare_cols = [];
    verifyRuleData.value.filter_col_names = [];
};

const basicInfoRef = ref(null);
const verifyObjectFormRef = ref(null);
const comparisonFormRef = ref(null);
const filterFormRef = ref(null);
const valid = async () => {
    try {
        await verifyObjectFormRef.value.validate();
        const validList = [filterFormRef.value.validate(), basicInfoRef.value.valid(), ruleconditionlistRef.value.valid(), comparisonFormRef.value.validate()];
        const result = await Promise.all(validList);
        store.commit('rule/updateCurrentRuleDetail', cloneDeep(verifyRuleData.value));
        return !result.includes(false);
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
    verifyRuleData.value.upload_abnormal_value = value.upload_abnormal_value;
    verifyRuleData.value.upload_rule_metric_value = value.upload_rule_metric_value;
    // 是否需要上报指标（因为默认后台返回的数据如果有上报指标，肯定有指标值，所以这里只监听用户修改参数模版后的情况）
    needUploadMetric.value = value.upload_abnormal_value || value.upload_rule_metric_value;
    verifyRuleData.value.execution_parameters_name = value.execution_parameters_name;
    // 需要手动触发更新
    verifyObjectFormRef.value.validate('execution_parameters_name');
});
// 控制校验时机，在输入框/选择框触发校验时屏蔽校验，直接通过。在点击确定/删除/保存时执行校验。
const shouldValidate = ref(false);
useListener('SHOULD_VALIDATE', (val) => {
    shouldValidate.value = val;
});
// 表单规则
const verifyRuleRules = ref({
    execution_parameters_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
});
const mappingsValidator = (rule, value) => new Promise((resolve, reject) => {
    if (shouldValidate.value && value?.length === 0) {
        reject($t('common.notEmpty'));
    } else {
        resolve();
    }
});
const comparisonFormRules = ref({
    multi_source_rule_template_id: [{
        required: true,
        trigger: 'change',
        type: 'number',
        message: $t('common.notEmpty'),
    }],
    mappings: [{
        required: true,
        trigger: ['input'],
        validator: mappingsValidator,
    }],
    compare_cols: [{
        required: true,
        trigger: ['input'],
        validator: mappingsValidator,
    }],
});
const filterFormRules = ref({
    filter_col_names: [{
        required: false,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
});


// 列过滤下拉数据为源表字段
const sourceTableColumns = ref([]);
const upstreamParams = cloneDeep(unref(inject('upstreamParams')));
const init = async (isLoad = true) => {
    const target = cloneDeep(ruleData.value.currentRuleDetail);
    target.alarm_variable = target.alarm_variable || [{}];
    upstreamParams.isUpStream = target.isUpStream;

    if (upstreamParams.isUpStream) {
        // 注入contextKey
        const allTables = await getTables({
            clusterName: target.source.cluster_name,
            proxyUser: target.source.proxy_user,
            dataSourceType: target.source.type,
            dataSourceId: target.source.linkis_datasource_id,
            dbName: target.source.db_name,
            upstreamParams,
        });
        const targetTable = allTables.filter(item => item.table_name === target.source.table_name);
        if (targetTable.length > 0) {
            upstreamParams.contextKey = targetTable[0].context_Key;
        }
    }
    if (!isLoad || (!target.filter_col_names && target?.source.type !== 'fps')) {
        // 数据依赖更新的时候filter_col_names清空，filter_col_names为null的时候给个初始值兜底
        target.filter_col_names = [];
    } else {
        let allColumns = [];
        if (target.type !== 'fps' && target?.source.type !== 'fps') {
            allColumns = await getColumns({
                clusterName: target.source.cluster_name,
                proxyUser: target.source.proxy_user,
                dataSourceType: target.source.type,
                dataSourceId: target.source.linkis_datasource_id,
                dbName: target.source.db_name,
                tableName: target.source.table_name,
                upstreamParams,
            });
        } else {
            allColumns = buildColumnData(target.source.file_table_desc);
        }
        target.filter_col_names = allColumns.filter(v => !target.filter_col_names?.find(i => i.column_name === v.column_name)).map(i => i.column_name);
    }
    verifyRuleData.value = target;
    // 如果是数据源修改，且用户修改过模版id，则当前模版id赋值之前用户修改选择的id
    if (!isLoad && currentTemplateId.value) {
        verifyRuleData.value.multi_source_rule_template_id = currentTemplateId.value;
        verifyRuleData.value.alarm_variable = [{}];
    }
    handleTemplateChange(verifyRuleData.value.multi_source_rule_template_id, isLoad);
};
// mappings/cols_compare 校验触发
const validate = async (type) => {
    try {
        shouldValidate.value = true;
        comparisonFormRef.value.validate(type);
        shouldValidate.value = false;
    } catch (err) {
        console.log(err);
    }
};
const mapSaved = async (type, index) => {
    // 组件内部输入框会触发校验，必须要点击确定后，手动触发校验，避免校验时机错误
    try {
        await validate(type);
        replaceParameter(index);
    } catch (err) {
        console.log(err);
    }
};

// 当数据库、数据表、过滤条件、字段等发生变化的时候，需要调用updateSqlDataSource进行预览SQL更新
useListener('SHOULD_UPDATE_NECESSARY_DATA', () => init(false));

// 初始化数据
useListener('IS_RULE_DETAIL_DATA_LOADED', () => init(true));

// mappding组件需要拿到表的信息
const datasourceGroupData = ref({});
useListener('UPDATE_COLUMN_LIST', ({ data, target }) => {
    if (target === 'source') {
        sourceTableColumns.value = data;
        datasourceGroupData.value.leftGroup = data;
    } else if (target === 'target') {
        datasourceGroupData.value.rightGroup = data;
    }
});

const loadVerifyTpl = async () => {
    try {
        const tempResp = await fetchTemplatesWithCache({
            page: 0,
            size: 512,
            template_type: TEMPLATE_TYPES.CROSS_TABLE_CHECK_TYPE,
        });
        checkTemplateList.value = tempResp.data || [];
    } catch (error) {
        console.log(error);
    }
};

onMounted(async () => {
    try {
        const constrastResp = await request('api/v1/projector/mul_source_rule/constrast/all', {}, 'post');
        directionList.value = constrastResp.map(v => ({ label: v.message, value: v.code, content: v.join_type }));
        loadVerifyTpl();
    } catch (err) {
        console.log(err);
    }
});
// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>

<style lang="less" scoped>
.hint {
    display: inline-block;
    margin-left: 8px;
    color: #646670;
}
</style>
