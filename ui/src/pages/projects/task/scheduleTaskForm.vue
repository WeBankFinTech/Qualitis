<template>
    <FForm
        ref="formRef"
        :labelWidth="69"
        labelPosition="right"
        :model="modelValue"
        :rules="rules"
        class="drawer-form"
    >
        <FFormItem
            :label="$t('_.工作流')"
            prop="workflow"
        >
            <FSelect
                v-model="modelValue.workflow"
                class="form-select"
                filterable
                clearable
                :options="workflowList"
                @change="workflowChange"
            ></FSelect>
        </FFormItem>
        <FFormItem
            :label="$t('_.任务')"
            prop="task"
        >
            <FSelect
                v-model="modelValue.task"
                class="form-select"
                filterable
                clearable
                :options="taskList"
                @change="taskChange"
            ></FSelect>
        </FFormItem>
        <FFormItem
            :label="$t('_.校验位置')"
            prop="checkPosition"
        >
            <FCheckboxGroup v-model="modelValue.checkPosition">
                <FCheckbox
                    value="pre"
                    :label="$t('_.前置校验规则组')"
                />
                <FCheckbox
                    value="post"
                    :label="$t('_.后置校验规则组')"
                />
            </FCheckboxGroup>
        </FFormItem>
        <FFormItem
            v-if="modelValue.checkPosition.includes('pre')"
            :label="$t('_.前置校验规则组')"
            labelClass="label-text"
            prop="preCheckRuleGroup"
        >
            <selector
                v-model:selected="modelValue.preCheckRuleGroup"
                v-model:selectedList="preCheckRuleGroupList"
                class="rules-select"
                :options="ruleGroupList"
            >
            </selector>
        </FFormItem>
        <FFormItem
            v-if="modelValue.checkPosition.includes('post')"
            :label="$t('_.后置校验规则组')"
            labelClass="label-text"
            prop="postCheckRuleGroup"
        >
            <selector
                v-model:selected="modelValue.postCheckRuleGroup"
                v-model:selectedList="postCheckRuleGroupList"
                class="rules-select"
                :options="ruleGroupList"
            >
            </selector>
        </FFormItem>
    </FForm>
</template>
<script setup>
import { useI18n } from '@fesjs/fes';

import {
    computed, ref, reactive, onMounted,
} from 'vue';
import getFormatOptions from './hooks/useOption';
import selector from '../components/selector.vue';
import {
    getScheduledTaskRules,
} from '../api';

const { t: $t } = useI18n();
// eslint-disable-next-line no-undef
const props = defineProps({
    modelValue: {
        type: Object,
        required: true,
    },
    workflowList: {
        type: Array,
        required: true,
    },
    ruleGroupList: {
        type: Array,
        required: true,
    },
    sys: {
        type: String,
        required: true,
    },
    project: {
        type: String,
        required: true,
    },
    releaseUser: {
        type: String,
        required: true,
    },
    cluster: {
        type: String,
        required: true,
    },
    formType: {
        type: String,
        default: 'create',
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:modelValue']);
const formRef = ref(null);
const formModel = computed({
    get: () => props.modelValue,
    set: (value) => {
        emit('update:modelValue', value);
    },
});
const rules = computed(() => ({
    workflow: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择工作流'),
    },
    task: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择任务'),
    },
    checkPosition: {
        type: 'array',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.至少选择一个校验位置'),
    },
    preCheckRuleGroup: {
        type: 'array',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.校验规则组不能为空'),
    },
    postCheckRuleGroup: {
        type: 'array',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.校验规则组不能为空'),
    },
}));


const {
    taskList,
    getQualitisTask,
    getTaskList,
} = getFormatOptions();


// 拉规则组
const preCheckRuleGroupList = ref([]);
const postCheckRuleGroupList = ref([]);
// 重置规则组
function resetRuleGroup() {
    preCheckRuleGroupList.value = [];
    postCheckRuleGroupList.value = [];
    formModel.value.preCheckRuleGroup = [];
    formModel.value.postCheckRuleGroup = [];
}

// 根据集群、发布用户、项目和工作流来获取任务列表
function workflowChange(value) {
    formModel.value.task = '';
    taskList.value = [];
    console.log('props', props);
    if (value) {
        const {
            workflow,
        } = formModel.value;
        if (props.formType === 'edit') {
            resetRuleGroup();
            getQualitisTask(1, props.cluster, props.project, workflow);
        } else {
            getTaskList(
                props.project,
                workflow,
                props.releaseUser,
                props.cluster,
            );
        }
    }
}

// 编辑模式下根据参数来获取具体任务
async function taskChange() {
    console.log(formModel.value.task, props.formType);
    if (formModel.value.task && props.formType === 'edit') {
        const params = {
            schedule_system: props.sys,
            cluster: props.cluster,
            project_name: props.project,
            work_flow: formModel.value.workflow,
            task_name: formModel.value.task,
        };
        const res = await getScheduledTaskRules(params);
        formModel.value.scheduledTaskId = res.scheduled_task_id;
        formModel.value.releaseUser = res.release_user;
        formModel.value.preCheckRuleGroup = [];
        formModel.value.postCheckRuleGroup = [];
        res.rule_group_type.forEach((item) => {
            if (item.trigger_type === 1) {
                // 前置
                formModel.value.preCheckRuleGroup.push(item.rule_group_id);
            } else {
                // 后置
                formModel.value.postCheckRuleGroup.push(item.rule_group_id);
            }
        });
    } else if (!formModel.value.task && props.formType === 'edit') {
        formModel.value.preCheckRuleGroup = [];
        formModel.value.postCheckRuleGroup = [];
    }
}

const reset = () => {
    formRef.value.resetFields();
};
const clearValid = () => {
    formRef.value.clearValidate();
};
const valid = async () => {
    try {
        await formRef.value.validate();
        return true;
    } catch (err) {
        console.warn(err);
        return false;
    }
};
// eslint-disable-next-line no-undef
defineExpose({ valid, reset, clearValid });

</script>
<style lang='less' scoped></style>
