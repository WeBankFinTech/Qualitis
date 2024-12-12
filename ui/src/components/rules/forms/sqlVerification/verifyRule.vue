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
            <FFormItem :label="$t('common.verificationTemplate')" prop="rule_template_id">
                <FSelect
                    :modelValue="'自定义'"
                    class="form-edit-input"
                    filterable
                    disabled
                >
                </FSelect>
                <div class="form-preview-label">自定义</div>
            </FFormItem>
            <FFormItem label="校验SQL" prop="sql_check_area">
                <FInput
                    v-model="verifyRuleData.sql_check_area"
                    class="form-edit-input"
                    type="textarea"
                    style="max-width: 100%"
                    :autosize="{ minRows: 10 }"
                />
                <div class="form-preview-label">{{verifyRuleData.sql_check_area}}</div>
            </FFormItem>
            <RuleConditionList ref="ruleconditionlistRef" v-model:ruleConditions="verifyRuleData.alarm_variable" />
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
    </div>
</template>
<script setup>
import {
    ref, computed, provide,
} from 'vue';
import { useStore } from 'vuex';
import { useI18n, request } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import eventbus from '@/common/useEvents';
import BasicInfo from '@/components/rules/BasicInfo';
import ProjectTemplate from '@/pages/projects/template';
import {
    ExclamationCircleOutlined, FileOutlined, PlusOutlined,
} from '@fesjs/fes-design/es/icon';
import RuleConditionList from '@/components/rules/forms/RuleConditionList';
import useTemplateSelector from '@/components/rules/hook/useTemplateSelector';
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
});

// 加载模板
// 动态输入项的提示
const ruleArgumentTips = ref({});
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
    sql_check_area: [{
        required: true,
        trigger: 'blur',
        message: $t('common.notEmpty'),
    }],
});

const init = async (type = '') => {
    try {
        const target = cloneDeep(ruleData.value.currentRuleDetail);
        verifyRuleData.value = target;
        // 没有模板值，满足样式
        verifyRuleData.value.rule_template_id = 99999;
        if (!verifyRuleData.value?.alarm_variable?.length > 0) {
            verifyRuleData.value.alarm_variable = [{}];
        }
    } catch (err) {
        console.warn(err);
    }
};

// 当数据库、数据表、过滤条件、字段等发生变化的时候，需要调用updateSqlDataSource进行预览SQL更新
useListener('SHOULD_UPDATE_NECESSARY_DATA', type => init(type));

// 初始化数据
useListener('IS_RULE_DETAIL_DATA_LOADED', () => init('cluster'));

// eslint-disable-next-line no-undef
defineExpose({ valid });

</script>
