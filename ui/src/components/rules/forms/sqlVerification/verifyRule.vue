<template>
    <div class="rule-detail-form" :class="{ edit: ruleData.currentProject.editMode !== 'display' }">
        <h6 class="wd-body-title">{{$t('_.校验规则')}}</h6>
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
            <FFormItem :label="$t('common.verificationTemplate')" prop="rule_template_id">
                <FSelect
                    :modelValue="'自定义'"
                    class="form-edit-input"
                    filterable
                    disabled
                >
                </FSelect>
                <div class="form-preview-label">{{$t('_.自定义')}}</div>
            </FFormItem>
            <FFormItem style="flex: 1" prop="standard_params" :label="$t('_.标准值')">
                <FSelect
                    v-model="verifyRuleData.standard_params"
                    labelField="label"
                    valueField="value"
                    filterable
                    multiple
                    collapseTags
                    :collapseTagsLimit="3"
                    :placeholder="$t('_.请选择-自定义sql')"
                    :options="standardValueList"
                    class="form-edit-input"
                    @change="handleChangeStandardValue">
                </FSelect>
                <div class="form-preview-label">
                    {{getEnumNameString(verifyRuleData.standard_params)}}
                </div>
                <FTooltip>
                    <ExclamationCircleOutlined class="tip edit hint" />
                    <template #content>{{`${$t('common.sqlplaceholder')}$\{${$t('common.standardName')} }`}}</template>
                </FTooltip>
            </FFormItem>
            <FFormItem :label="$t('_.UDF设置')" prop="linkis_udf_names">
                <FSelect
                    v-model="verifyRuleData.linkis_udf_names"
                    multiple
                    class="form-edit-input"
                    style="width:315px"
                    filterable
                    collapseTags
                    :collapseTagsLimit="2"
                    :placeholder="$t('_.请选择')">
                    <FOption
                        v-for="(item, index) in udfList"
                        :key="index"
                        :value="item.value"
                        :label="item.label"
                        :disabled="!item.status"
                    ></FOption>
                </FSelect>
                <div class="form-preview-label">
                    <FSpace v-if="verifyRuleData.linkis_udf_names?.length > 0">
                        <FTag v-for="(item, index) in verifyRuleData.linkis_udf_names" :key="index" type="info" size="small">
                            <FEllipsis style="max-width: 240px">{{udfList.find(udf => udf.value === item)?.value || item}}</FEllipsis>
                        </FTag>
                    </FSpace>
                    <span v-else>--</span>
                </div>
            </FFormItem>
            <FFormItem :label="$t('_.采样SQL')" prop="sql_check_area">
                <!-- <FInput
                    v-model="verifyRuleData.sql_check_area"
                    class="form-edit-input"
                    type="textarea"
                    style="max-width: 100%"
                    :autosize="{ minRows: 10 }"
                />
                <div class="form-preview-label">{{verifyRuleData.sql_check_area}}</div> -->
                <div style="flex: 1">
                    <div style="width: 100%; display:flex; justify-content: end; margin-bottom: 16px"><FButton class="format-btn" type="link" @click="formatSql">{{$t('_.格式化')}}</FButton></div>

                    <SqlEditor
                        ref="sqlEditorRef"
                        v-model:sql="verifyRuleData.sql_check_area"
                        :readOnly="ruleData.currentProject.editMode === 'display'"
                        @valid="validateSql"
                    />
                </div>
            </FFormItem>
            <RuleConditionList ref="ruleconditionlistRef" v-model:ruleConditions="verifyRuleData.alarm_variable" />
            <FFormItem :label="$t('_.执行参数')" prop="execution_parameters_name">
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
    </div>
</template>
<script setup>

import {
    ref, computed, provide, onMounted,
} from 'vue';
import { format } from 'sql-formatter';
import { useStore } from 'vuex';
import {
    useI18n, request, useRoute, useRouter,
} from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import eventbus from '@/common/useEvents';
import BasicInfo from '@/components/rules/BasicInfo';
import ProjectTemplate from '@/pages/projects/template';
import {
    ExclamationCircleOutlined, FileOutlined, PlusOutlined,
} from '@fesjs/fes-design/es/icon';
import RuleConditionList from '@/components/rules/forms/RuleConditionList';
import useTemplateSelector from '@/components/rules/hook/useTemplateSelector';
import SqlEditor from '@/components/rules/forms/sqlEditor';
import { useListener } from '../../utils';

const store = useStore();
const { t: $t } = useI18n();

const ruleData = computed(() => store.state.rule);

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
    standard_params: [],
    standard_value_variables: [{}],
});
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
const handleChangeStandardValue = (value) => {
    verifyRuleData.value.standard_value_variables = verifyRuleData.value.standard_params.map(item => ({
        standard_value_variables_id: item,
    })) || [{}];
};

const getEnumNameString = (ids) => {
    const getEnumLabel = id => standardValueList.value.find(item => item.value === id)?.label || '';
    return ids?.map(getEnumLabel)?.join(';') || '--';
};
// 加载模板
// 动态输入项的提示

const basicInfoRef = ref(null);
const verifyObjectFormRef = ref(null);
const ruleconditionlistRef = ref(null);
const valid = async () => {
    try {
        await verifyObjectFormRef.value.validate();
        const result = await Promise.all([
            basicInfoRef.value.valid(),
            ruleconditionlistRef.value.valid(),
        ]);
        store.commit('rule/updateCurrentRuleDetail', cloneDeep(verifyRuleData.value));
        return !result.includes(false);
    } catch (err) {
        console.warn(err);
        return false;
    }
};

// 选择模板
const templateRef = ref(null);
const datasourceComFormRef = ref(null);
const {
    showExecuteParamsDrawer,
    openExecuteParamsDrawer,
    confirmSelect,
} = useTemplateSelector(templateRef, (value) => {
    console.log(value);
    verifyRuleData.value.execution_parameters_name = value.execution_parameters_name;
    // 需要手动触发更新
    verifyObjectFormRef.value.validate('execution_parameters_name');
});

const checkFieldList = ref([]);
provide('checkFieldList', computed(() => checkFieldList.value));

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
    sql_check_area: [
        {
            required: true,
            trigger: 'blur',
            message: $t('common.notEmpty'),
        },
        // 不允许sql的单行注释
        {
            required: true,
            validator: (rule, value) => new Promise((resolve, reject) => {
                if (/(?:--.*?[\r\n]|--.*?$)/g.test(verifyRuleData.value.sql_check_area)) {
                    reject($t('_.禁用单行注释'));
                }
                resolve();
            }),
        },
    ],
});
const validateSql = () => {
    verifyObjectFormRef.value.validate('sql_check_area');
};
// udf设置
const udfList = ref([]);
// function formatSql(sql) {
//     return format(props.sql);
// }
const init = async (type = '') => {
    try {
        await getStandValueList();
        const target = cloneDeep(ruleData.value.currentRuleDetail);
        // if (target.sql_check_area) {
        //     try {
        //         target.sql_check_area = format(target.sql_check_area, { language: 'sql', tabWidth: 4 });
        //     } catch (err) {
        //         console.warn(err);
        //     }
        // }

        verifyRuleData.value = target;
        console.log('target=> ', target);
        verifyRuleData.value.standard_value_variables = target.standard_value_variables ? target.standard_value_variables : [];
        verifyRuleData.value.standard_params = verifyRuleData.value.standard_value_variables.length ? verifyRuleData.value.standard_value_variables.map(item => `${item?.standard_value_variables_id ?? ''}`) : [];
        // 没有模板值，满足样式
        verifyRuleData.value.rule_template_id = 99999;
        if (!verifyRuleData.value?.alarm_variable?.length > 0) {
            verifyRuleData.value.alarm_variable = [{}];
        }
        if (!verifyRuleData.value?.linkis_udf_names?.length > 0) {
            verifyRuleData.value.linkis_udf_names = [];
        }
        if (type === 'cluster') {
            const res = await request('/api/v1/projector/meta_data/udf/all', Object.assign({
                page: 0,
                size: 999,
            }, { enable_cluster: verifyRuleData.value.datasource[0].cluster_name ? [verifyRuleData.value.datasource[0].cluster_name] : [] }));
            udfList.value = res.content?.map(item => ({ value: item.name, label: item.name, status: item.status }));
        }
        eventbus.emit('INIT_FINISHED');
    } catch (err) {
        console.warn(err);
    }
};
const sqlEditorRef = ref(null);
const formatSql = () => {
    sqlEditorRef.value.formatSql();
};
// 当数据库、数据表、过滤条件、字段等发生变化的时候，需要调用updateSqlDataSource进行预览SQL更新
useListener('SHOULD_UPDATE_NECESSARY_DATA', type => init(type));
const route = useRoute();
const router = useRouter();
// 初始化数据
useListener('IS_RULE_DETAIL_DATA_LOADED', () => init('cluster'));
// 切换复制模式，规则名称改成xxx_副本
useListener('COPY_MODE', () => {
    verifyRuleData.value.rule_name = `${verifyRuleData.value.rule_name}_copy`;
    if (verifyRuleData.value.cn_name) verifyRuleData.value.cn_name = `${verifyRuleData.value.cn_name}_副本`;
    // ruleconditionlistRef.value.reset();
    delete verifyRuleData.value.alarm_variable[0].rule_metric_id;
    delete verifyRuleData.value.alarm_variable[0].rule_metric_en_code;
    delete verifyRuleData.value.alarm_variable[0].rule_metric_name;
    delete verifyRuleData.value.alarm_variable[0].rule_metric_en_code;
    delete verifyRuleData.value.alarm_variable[0].rule_metric_name;
    // const tempQuery = { ...route.query };
    // delete tempQuery.id;
    // const queryString = Object.keys(tempQuery)
    //     .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(tempQuery[key])}`)
    //     .join('&');

    // // 构建新的 URL
    // const newUrl = `${route.path}?${queryString}`;
    // router.replace(newUrl);
});
// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
<style lang="less" scoped>
.format-btn {
    padding: 0px;
    color: #5384ff;
    &:hover {
        color: #5384ff
    }
}
.hint {
    display: inline-block;
    margin-left: 8px;
    color: #646670;
}
</style>
