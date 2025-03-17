<template>
    <FModal
        :show="show"
        :title="type === 1 ? '发布关联调度' : '发布调度任务'"
        width="600px"
        :maskClosable="false"
        :okText="$t('common.confirm')"
        :cancelText="$t('common.cancel')"
        @ok="handleOk"
        @cancel="handleCancel"
    >
        <FForm
            ref="formRef"
            :labelWidth="66"
            labelPosition="right"
            :model="formModel"
            :rules="formRule"
        >
            <FFormItem
                :label="$t('_.调度系统')"
                prop="sys"
            >
                <FSelect
                    v-model="formModel.sys"
                    class="form-select"
                    filterable
                    :options="sysTypeList"
                ></FSelect>
            </FFormItem>
            <FFormItem
                :label="$t('_.集群')"
                prop="cluster"
            >
                <FSelect
                    v-model="formModel.cluster"
                    class="form-select"
                    filterable
                    clearable
                    :options="clusterList"
                    @change="clusterChange"
                ></FSelect>
            </FFormItem>
            <FFormItem
                :label="$t('_.项目')"
                prop="project"
            >
                <FSelect
                    v-model="formModel.project"
                    class="form-select"
                    filterable
                    clearable
                    :options="projectList"
                    @change="projectChange"
                ></FSelect>
            </FFormItem>
            <FFormItem
                :label="$t('_.工作流')"
                prop="workflow"
            >
                <FSelect
                    v-model="formModel.workflow"
                    class="form-select"
                    filterable
                    clearable
                    :options="workflowList"
                    @change="workflowChange"
                ></FSelect>
            </FFormItem>
            <FFormItem
                v-if="type === 2"
                :label="$t('_.应用信息')"
                prop="workflow_business_id"
            >
                <FSelect
                    v-model="formModel.workflow_business_id"
                    class="form-select"
                    filterable
                    clearable
                    :options="businessList"
                ></FSelect>
            </FFormItem>
            <FFormItem
                v-if="type === 1"
                :label="$t('_.任务')"
                prop="task_name"
            >
                <FSelect
                    v-model="formModel.task_name"
                    class="form-select"
                    filterable
                    clearable
                    :options="taskList"
                ></FSelect>
            </FFormItem>
            <FFormItem
                :label="$t('_.变更单号')"
                prop="approve_number"
            >
                <FInput
                    v-model="formModel.approve_number"
                    class="form-select"
                    :maxlength="64"
                    :placeholder="$t('_.请输入变更单号')"
                />
            </FFormItem>
        </FForm>
    </FModal>
</template>
<script setup>

import {
    computed, ref, reactive, onMounted, onUpdated,
} from 'vue';
import { useI18n, request, useRoute } from '@fesjs/fes';
import { COMMON_REG } from '@/assets/js/const';
import { FMessage } from '@fesjs/fes-design';
import getFormatOptions from './hooks/useOption';
import { releaseScheduledTask } from '../api';

const { t: $t } = useI18n();
const route = useRoute();

// eslint-disable-next-line no-undef
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    projectId: {
        type: Number,
        required: true,
    },
    sysTypeList: {
        type: Array,
        required: true,
    },
    type: {
        type: Number,
        default: 1, // 1.associate schedule 2. publish schedule
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:show', 'finishPub']);
const businessList = ref([]);
const formRef = ref(null);
const formModel = reactive({});
const formRule = ref({
    sys: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
    },
    cluster: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择集群'),
    },
    project: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择项目'),
    },
    workflow_business_id: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择应用信息'),
    },
    approve_number: [{
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请输入变更单号'),
    }, {
        pattern: COMMON_REG.ONLY_EN_NUM,
        message: $t('_.只允许输入数字英文,最大30位'),
    }],
});

const {
    clusterList,
    projectList,
    workflowList,
    taskList,
    getQualitisCluster,
    getQualitisProject,
    getQualitisWorkflow,
    getQualitisTask,
} = getFormatOptions();

function clusterChange(value) {
    formModel.project = '';
    formModel.workflow = '';
    formModel.task_name = '';
    workflowList.value = [];
    taskList.value = [];
    if (value) {
        const { cluster } = formModel;
        getQualitisProject(props.projectId, props.type, cluster);
    }
}
function projectChange(value) {
    formModel.workflow = '';
    formModel.task_name = '';
    workflowList.value = [];
    taskList.value = [];
    if (value) {
        const { cluster, project } = formModel;
        getQualitisWorkflow(props.type, cluster, project);
    }
}
// 根据项目和工作流来获取任务列表
function workflowChange(value) {
    formModel.task_name = '';
    taskList.value = [];
    if (value) {
        const { cluster, project, workflow } = formModel;
        getQualitisTask(props.type, cluster, project, workflow);
    }
}

const handleCancel = () => {
    emit('update:show', false);
    formRef.value.clearValidate();
    formRef.value.resetFields();
};
const handleOk = async () => {
    try {
        await formRef.value.validate();
        // TODO submit featch
        const params = {
            project_id: props.projectId,
            task_type: props.type,
            approve_number: formModel.approve_number,
            schedule_system: formModel.sys,
            cluster: formModel.cluster,
            scheduled_project_name: formModel.project,
            scheduled_work_flow: formModel.workflow,
            workflow_business_id: formModel.workflow_business_id,
            scheduled_task_name: formModel.task_name,
        };
        console.log(['', '1.associate schedule ', '2. publish schedule'][props.type], params);
        await releaseScheduledTask(params);
        emit('update:show', false);
        emit('finishPub');
        FMessage.success($t('_.发布成功'));
        formRef.value.resetFields();
    } catch (e) {
        console.log('EdirModal submit error:', e);
    }
};
onUpdated(async () => {
    if (props.show) {
        await getQualitisCluster(props.projectId, props.type);
    }
    const data = await request(`/api/v1/projector/scheduled/workflow_business/list/${route.query.projectId}`, {}, 'get');
    businessList.value = data.map(item => ({ label: item.name, value: String(item.id) }));
});

</script>
<style lang='less' scoped></style>
