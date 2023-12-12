<template>
    <div class="rule-detail-form" :class="{ edit: editMode !== 'display' }">
        <h6 v-if="listType === 'commonList'" class="wd-body-title">校验规则</h6>
        <!-- 规则名称、规则描述 -->
        <BasicInfo v-if="listType !== 'listByTemplate'" ref="basicInfoRef" v-model:basicInfo="verifyRuleData" :lisType="lisType" prefixTitle="文件校验" />
        <FForm
            ref="verifyObjectFormRef"
            :layout="layout"
            :class="layout === 'inline' ? 'inline-form-item' : ''"
            :model="verifyRuleData"
            :rules="verifyRuleRules"
            :labelWidth="labelWidth"
            :labelPosition="editMode !== 'display' ? 'right' : 'left'"
        >
            <!-- 校验模板 -->
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
            <BasicInfo v-if="listType === 'listByTemplate'" ref="basicInfoRef" v-model:basicInfo="verifyRuleData" :lisType="lisType" prefixTitle="文件校验" />
            <!-- 校验条件 -->
            <RuleConditionList
                ref="ruleconditionlistRef"
                v-model:ruleConditions="verifyRuleData.alarm_variable"
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
            <ProjectTemplate ref="templateRef" :showHeader="false" :isEmbed="true" :curExeName="verifyRuleData.execution_parameters_name" class="project-template-style"></ProjectTemplate>
        </FDrawer>
    </div>
</template>
<script setup>
import {
    ref, computed, provide, nextTick, defineEmits, watch, onMounted,
} from 'vue';
import { useStore } from 'vuex';
import { useI18n, request } from '@fesjs/fes';
import { TEMPLATE_TYPES } from '@/common/constant.js';
import { cloneDeep } from 'lodash-es';
import eventbus from '@/common/useEvents';
import BasicInfo from '@/components/rules/BasicInfo';
import ProjectTemplate from '@/pages/projects/template';
import { NUMBER_TYPES } from '@/common/utils';
import {
    ExclamationCircleOutlined, FileOutlined, PlusOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    placeholderPrompt, initRuleArgsList, getColumnMapData, useListener,
} from '@/components/rules/utils';
import { fetchTemplateDetail } from '@/pages/rules/template/api';
import { fetchTemplatesWithCache, fetchTemplatesOfListByTemplate } from '@/components/rules/api/index';
import RuleConditionList from '@/components/rules/forms/RuleConditionList';
import useTemplateSelector from '@/components/rules/hook/useTemplateSelector';

const store = useStore();
const { t: $t } = useI18n();
const checkTemplateList = ref([]);
provide('ruleType', '4-1');
const ruleData = computed(() => store.state.rule);

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
});
const emit = defineEmits(['update:rule']);
const listType = computed(() => props.lisType);
const currentTemplateId = ref();
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
        if (listType.value !== 'groupList') {
            // 更新store，给save调用
            store.commit('rule/updateCurrentRuleDetail', cloneDeep(verifyRuleData.value));
        }
        return !result.includes(false);
    } catch (err) {
        console.warn(err);
        return false;
    }
};

// 执行参数模板相关
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

const getTemplateLabel = computed(() => {
    const result = checkTemplateList.value.filter(item => item.template_id === currentRule.value.rule_template_id);
    if (result) {
        return result[0]?.template_name;
    }
    return '';
});
provide('checkFieldList', computed(() => checkFieldList.value));

const solidification = ref(false); // 规则模板是否固化了校验条件
const handleTemplateChange = async (id) => {
    try {
        if (!id) {
            return;
        }
        const result = await request(`api/v1/projector/rule_template/meta_input/${id}`, {}, 'get');
        checkFieldList.value = result.template_output || [];
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
    } catch (error) {
        console.warn(error);
    }
};

const onTemplateChange = (id) => {
    currentTemplateId.value = id;
    handleTemplateChange(id);
    verifyRuleData.value.rule_template_name = checkTemplateList.value.find(v => v.template_id === id)?.template_name || '';
    // 清空校验参数
    ruleconditionlistRef.value.reset();
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

});

const loadVerifyTpl = async () => {
    try {
        let resp = null;
        // 基于规则模版时，由于权限需要切换另一个接口
        if (listType.value === 'listByTemplate') {
            resp = await fetchTemplatesOfListByTemplate({
                template_type: TEMPLATE_TYPES.DOCUMENT_TABLE_CHECK_TYPE,
            });
        } else {
            resp = await fetchTemplatesWithCache({
                page: 0,
                size: 512,
                template_type: TEMPLATE_TYPES.DOCUMENT_TABLE_CHECK_TYPE,
            });
        }
        checkTemplateList.value = (listType.value === 'listByTemplate' ? resp : resp.data) || [];
        store.commit('rule/setDocumentCheckTemplateList', cloneDeep(checkTemplateList.value));
    } catch (error) {
        console.log(error);
    }
};

// isInitStep差异化初始和重新覆盖数据的逻辑
const init = async (isInitStep = true) => {
    const target = cloneDeep(currentRule.value);
    target.alarm_variable = target.alarm_variable || [{}];
    target.colNamesStrArr = [];
    verifyRuleData.value = target;
    console.log('documentCheck-init', verifyRuleData);
    // 如果是数据源修改，且用户修改过模版id，则当前模版id赋值之前用户修改选择的id
    if (!isInitStep && currentTemplateId.value) {
        verifyRuleData.value.rule_template_id = currentTemplateId.value;
        verifyRuleData.value.alarm_variable = [{}];
    }
    handleTemplateChange(verifyRuleData.value.rule_template_id, isInitStep);
    await loadVerifyTpl();
};

// 当数据库、数据表、过滤条件、字段等发生变化的时候，需要调用updateSqlDataSource进行预览SQL更新
useListener('SHOULD_UPDATE_NECESSARY_DATA', () => {
    if (listType.value === 'listByTemplate') return;
    init(false);
});

// 初始化数据
useListener('IS_RULE_DETAIL_DATA_LOADED', () => {
    if (listType.value === 'groupList' || listType.value === 'listByTemplate') return;
    init();
});
// eslint-disable-next-line no-undef
defineExpose({ valid, init });

onMounted(() => {
    if (listType.value === 'listByTemplate') init();
});

watch(() => verifyRuleData, () => {
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
