<template>
    <FDrawer
        v-model:show="drawerShow"
        displayDirective="if"
        :title="drawerTitle"
        :footer="true"
        :width="960"
        @cancel="handleClose"
    >
        <template #footer>
            <div v-if="mode !== 'view'">
                <FButton
                    type="primary"
                    style="margin-right: 16px;"
                    @click="finishPublish"
                >
                    {{$t('_.确定')}}
                </FButton>
                <FButton @click="handleClose">{{$t('_.取消')}}</FButton>
            </div>
            <div v-else>
                <FButton
                    type="info"
                    style="margin-right: 16px;"
                    @click="aboutTimingTask('edit')"
                >
                    {{$t('_.编辑')}}
                </FButton>
                <FButton
                    type="danger"
                    @click="deleteTimingTask"
                >
                    {{$t('_.删除')}}
                </FButton>
            </div>
        </template>
        <FForm
            ref="MainFormRef"
            class="form"
            :labelWidth="69"
            labelPosition="right"
            :model="timingTask"
            :rules="mode !== 'view' ? taskRules : []"
        >
            <FFormItem
                :label="$t('_.调度系统')"
                prop="sys"
            >
                <FSelect
                    v-if="mode !== 'view'"
                    v-model="timingTask.sys"
                    class="form-select"
                    filterable
                    :options="sysTypeList"
                    @change="taskChange('sys')"
                ></FSelect>
                <span
                    v-else
                    class="view-font"
                >{{timingTask.sys}}</span>
            </FFormItem>
            <FFormItem
                :label="$t('_.集群')"
                prop="cluster"
            >
                <FSelect
                    v-if="mode !== 'view'"
                    v-model="timingTask.cluster"
                    class="form-select"
                    filterable
                    :options="editPageClusterList"
                    @change="taskChange('cluster')"
                ></FSelect>
                <span
                    v-else
                    class="view-font"
                >{{timingTask.cluster}}</span>
            </FFormItem>
            <FFormItem
                :label="$t('_.发布用户')"
                prop="publishUser"
            >
                <FSelect
                    v-if="mode !== 'view'"
                    v-model="timingTask.publishUser"
                    class="form-select"
                    filterable
                    :options="publishUserList"
                ></FSelect>
                <span
                    v-else
                    class="view-font"
                >{{timingTask.publishUser}}</span>
            </FFormItem>
            <FFormItem
                v-if="mode === 'create'"
                :label="$t('_.项目')"
                prop="scheduledProjectName"
            >
                <FInput
                    v-model="timingTask.scheduledProjectName"
                    class="form-select"
                    :placeholder="$t('_.请输入')"
                />
            </FFormItem>
            <FFormItem
                v-if="mode === 'edit'"
                :label="$t('_.项目')"
                prop="scheduledProjectID"
            >
                <FSelect
                    v-model="timingTask.scheduledProjectID"
                    class="form-select"
                    filterable
                    :options="editPageProjectList"
                    :placeholder="$t('_.请输入')"
                    @change="taskChange('project')"
                ></FSelect>
            </FFormItem>
            <FFormItem
                v-if="mode === 'view'"
                :label="$t('_.项目')"
                prop="scheduledProjectID"
            >
                <span class="view-font">{{timingTask.scheduledProjectName}}</span>
            </FFormItem>
            <div class="workflows-container">
                <div
                    v-for="(item, index) in timingTask.workflows"
                    :key="item"
                    class="workflows"
                >
                    <div class="workflows-title">
                        <span class="workflows-label">{{$t('_.工作流')}}{{index + 1}}</span>
                        <span
                            v-if="timingTask.workflows.length > 1 && mode !== 'view'"
                            class="workflows-delete"
                            @click="deleteWorkflow(index)"
                        >
                            <MinusCircleOutlined style="margin-right: 4.67px;" />{{$t('_.删除')}}</span>
                    </div>
                    <div class="workflows-form">
                        <FFormItem
                            :label="$t('_.工作流')"
                            :prop="`workflows[${index}].workflow`"
                            :rules="mode !== 'view' ? [
                                { required: true, message: '请输入工作流', type: 'string' },
                                {
                                    validator: (rule, value) => {
                                        const reg = new RegExp('^[a-zA-Z][a-zA-Z0-9_]*$');
                                        return reg.test(value) && value.length < 129;
                                    },
                                    trigger: ['blur', 'change'],
                                    message: '工作流名称需以字母开头，允许字母、数字、下划线，不超过 128 字符',
                                }] : []"
                        >
                            <FInput
                                v-if="mode !== 'view'"
                                v-model="item.workflow"
                                :placeholder="$t('_.请输入')"
                                class="workflows-input"
                            />
                            <span
                                v-else
                                class="view-font"
                            >{{timingTask.workflows[index].workflow}}</span>
                        </FFormItem>
                        <FFormItem
                            :label="$t('_.代理用户')"
                            :prop="`workflows[${index}].proxyUser`"
                            :rules="mode !== 'view' ? [{ required: true, trigger: ['change', 'blur'], message: '请选择代理用户', type: 'string' }] : []"
                        >
                            <FSelect
                                v-if="mode !== 'view'"
                                v-model="item.proxyUser"
                                class="workflows-select"
                                filterable
                                :options="proxyUserList"
                            ></FSelect>
                            <span
                                v-else
                                class="view-font"
                            >{{timingTask.workflows[index].proxyUser}}</span>
                        </FFormItem>
                        <FFormItem
                            :label="$t('_.规则组')"
                            :prop="`workflows[${index}].ruleGroup`"
                            :rules="mode !== 'view' ? [{ required: true, trigger: ['change', 'blur'], message: '请选择规则组', type: 'array' }] : []"
                        >
                            <selector
                                v-if="mode !== 'view'"
                                v-model:selected="item.ruleGroup"
                                v-model:selectedList="item.ruleGroupList"
                                class="workflows-select"
                                :options="ruleGroupList"
                            ></selector>
                            <span
                                v-else
                                class="view-font"
                            >
                                <FTag
                                    v-for="(item) in timingTask.workflows[index].ruleGroupList"
                                    :key="item"
                                    type="info"
                                    style="margin-right: 8px; margin-top: 8px;"
                                >{{item.label}}</FTag>
                            </span>
                        </FFormItem>
                        <FFormItem
                            :label="$t('_.信号设置')"
                            :prop="`workflows[${index}].signSettings`"
                        >
                            <div class="sign-wrap">
                                <SignSetting
                                    v-for="(item, cur) in timingTask.workflows[index].signSettings"
                                    :ref="el => { if (el) signRefs['' + index + '-' + cur] = el; }"
                                    :key="item"
                                    v-model:signData="timingTask.workflows[index].signSettings[cur]"
                                    :index="cur + 1"
                                    :mode="mode"
                                    :proxyUser="timingTask.workflows[index].proxyUser"
                                    :taskList="timingTask.workflows[index].ruleGroupList"
                                    @delete="deleteSign(index, cur)"
                                ></SignSetting>
                                <div
                                    v-if="mode !== 'view'"
                                    class="add-sign"
                                >
                                    <span
                                        class="add-sign-btn"
                                        @click="addSign(index)"
                                    >
                                        <PlusCircleOutlined style="padding-right: 4.58px;" />{{$t('_.添加信号')}}</span>
                                </div>
                            </div>
                        </FFormItem>
                        <FFormItem
                            :label="$t('_.调度类型')"
                            :prop="`workflows[${index}].scheduleType`"
                            :rules="mode !== 'view' ? [{ required: true, trigger: ['change', 'blur'], message: '请选择调度类型', type: 'string' }] : []"
                        >
                            <FSelect
                                v-if="mode !== 'view'"
                                v-model="item.scheduleType"
                                class="workflows-select"
                                filterable
                                :options="scheduleTypeList"
                            ></FSelect>
                            <span
                                v-else
                                class="view-font"
                            >{{getLabelByValue(scheduleTypeList, timingTask.workflows[index].scheduleType)}}</span>
                        </FFormItem>
                        <div v-if="item.scheduleType === 'interval'">
                            <FFormItem
                                :label="$t('_.执行频率')"
                                :prop="`workflows[${index}].executeInterval`"
                                :rules="mode !== 'view' ? [{ required: true, message: '请选择执行频率', type: 'string' }] : []"
                            >
                                <FSelect
                                    v-if="mode !== 'view'"
                                    v-model="item.executeInterval"
                                    class="workflows-select"
                                    :options="executeIntervalList"
                                    @change="generateExecutionTime(item)"
                                ></FSelect>
                                <span
                                    v-else
                                    class="view-font"
                                >{{executeIntervalDict[timingTask.workflows[index].executeInterval]}}</span>
                            </FFormItem>
                            <FFormItem
                                v-if="item.executeInterval"
                                :label="$t('_.执行时间')"
                                :prop="`workflows[${index}].executionTime`"
                                :rules="mode !== 'view' ? [{ required: true, message: '请选择执行时间', type: 'string' }] : []"
                            >
                                <div
                                    v-if="mode !== 'view'"
                                    class="execution-time-wrapper"
                                >
                                    <FInput
                                        v-if="item.executeInterval === 'hour'"
                                        v-model="item.executionTimeMM"
                                        class="mr-8px"
                                        @change="generateExecutionTime(item)"
                                    >
                                        <template #suffix>{{$t('_.分')}}</template>
                                    </FInput>
                                    <FInput
                                        v-if="item.executeInterval === 'hour'"
                                        v-model="item.executionTimeSS"
                                        @change="generateExecutionTime(item)"
                                    >
                                        <template #suffix>{{$t('_.秒')}}</template>
                                    </FInput>
                                    <FTimePicker
                                        v-if="item.executeInterval === 'day'"
                                        v-model="item.executionTimePicker"
                                        @change="generateExecutionTime(item)"
                                    ></FTimePicker>
                                    <FSelect
                                        v-if="item.executeInterval === 'week'"
                                        v-model="item.executionDateInInterval"
                                        class="mr-8px"
                                        :options="executionTimeWeekOptions"
                                        @change="generateExecutionTime(item)"
                                    ></FSelect>
                                    <FSelect
                                        v-if="item.executeInterval === 'month'"
                                        v-model="item.executionDateInInterval"
                                        class="mr-8px"
                                        :options="executionTimeMonthOptions"
                                        @change="generateExecutionTime(item)"
                                    ></FSelect>
                                    <FTimePicker
                                        v-if="item.executeInterval === 'week' || item.executeInterval === 'month'"
                                        v-model="item.executionTimePicker"
                                        @change="generateExecutionTime(item)"
                                    ></FTimePicker>
                                </div>
                                <span
                                    v-else
                                    class="view-font"
                                >{{timingTask.workflows[index].executionTime}}</span>
                            </FFormItem>
                        </div>
                        <div v-if="item.scheduleType === 'signal'">
                            <FFormItem
                                :label="$t('_.调度信号')"
                                :prop="`workflows[${index}].msgTopic`"
                                :rules="mode !== 'view' ? [{ required: true, message: '请完善调度信息', type: 'string' }] : []"
                            >
                                <div class="schedule-sign-box">
                                    <SchduleSignForm
                                        :ref="el => { if (el) schduleSignForms[index] = el; }"
                                        v-model:modelData="timingTask.workflows[index]"
                                        :mode="mode"
                                    />
                                </div>
                            </FFormItem>
                        </div>
                    </div>
                </div>
            </div>
        </FForm>
        <div
            v-if="mode !== 'view'"
            class="add-workflow"
        >
            <span
                class="add-workflow-btn"
                @click="addWorkflow"
            >
                <PlusCircleOutlined style="padding-right: 4.58px;" />{{$t('_.添加工作流')}}</span>
        </div>
    </FDrawer>
</template>
<script setup>

import { FMessage, FModal, FTimePicker } from '@fesjs/fes-design';
import {
    PlusCircleOutlined,
    MinusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { request as FRequest, useI18n } from '@fesjs/fes';
import {
    computed,
    onMounted,
    reactive,
    ref,
    watch,
    defineEmits,
    defineProps,
} from 'vue';
import { cloneDeep } from 'lodash-es';
import { COMMON_REG } from '@/assets/js/const';
import { getLabelByValue } from '@/common/utils';
import selector from '../components/selector.vue';
import SignSetting from '../components/signSetting.vue';
import {
    fomatPreviewExecutionTime, executeIntervalList, executeIntervalDict, executionTimeWeekOptions, executionTimeMonthOptions,
} from './util';
import {
    loadRuleGroupList, loadSysTypeList, loadProxyUserList, loadPublishUserList, loadEditPageProjectList, loadClusterList, loadPublishClusterList,
} from './loadListData';
import SchduleSignForm from './schduleSignForm.vue';


const { t: $t } = useI18n();

const scheduleTypeList = [
    { value: 'interval', label: $t('_.定时调度') },
    { value: 'signal', label: $t('_.信号调度') },
];
const props = defineProps({
    show: {
        type: Boolean,
        default: false,
        required: true,
    },
    mode: {
        type: String,
        required: true,
    },
    projectId: {
        type: Number,
        required: true,
    },
    drawerTitle: {
        type: String,
        required: true,
    },
    scheduledProjectID: {
        type: Number,
    },
});
const emit = defineEmits(['update:show', 'update:mode', 'update:drawerTitle', 'deleteTask', 'publishTask', 'modifyTask']);
const drawerShow = computed({
    get: () => props.show,
    set: (value) => {
        emit('update:show', value);
    },
});
const sysTypeList = ref([]);

const publishUserList = ref([]);
const proxyUserList = ref([]);
const ruleGroupList = ref([]);
const emptyWorkflow = {
    workflow: '',
    proxyUser: '',
    ruleGroup: [],
    ruleGroupList: [],
    executionTime: '',
    executionTimePicker: '',
    executionTimeMM: '',
    executionTimeSS: '',
    executeInterval: '',
    executionDateInInterval: '',
    executionTimeInDate: '',
    signSettings: [],
};

const timingTask = reactive({
    sys: 'WTSS',
    cluster: '',
    scheduledProjectName: '',
    scheduledProjectID: '',
    publishUser: '',
    workflows: [cloneDeep(emptyWorkflow)],
});

// 调度任务初始化
const getInitialTask = () => ({
    sys: 'WTSS',
    cluster: '',
    scheduledProjectName: '',
    scheduledProjectID: '',
    publishUser: '',
    workflows: [cloneDeep(emptyWorkflow)],
});

const resetTask = () => {
    Object.assign(timingTask, getInitialTask());
};

const addWorkflow = () => {
    timingTask.workflows.push(cloneDeep(emptyWorkflow));
    // eslint-disable-next-line no-use-before-define
    WorkflowFormRefs.value.push(null);
};

const emptySign = {
    downstreamTask: [],
    upstreamTask: [],
};
const addSign = (index) => {
    console.log(timingTask.workflows[index]);
    timingTask.workflows[index].signSettings.push(cloneDeep(emptySign));
};
const deleteSign = (index, cur) => {
    timingTask.workflows[index].signSettings.splice(cur, 1);
};

const generateExecutionTime = (item) => {
    console.log(item);
    if (item.executeInterval === 'hour') {
        const mm = parseInt(item.executionTimeMM) || 0;
        const ss = parseInt(item.executionTimeSS) || 0;
        if (mm > 59) {
            item.executionTimeMM = '00';
        } else if (mm < 10) {
            item.executionTimeMM = `0${mm}`;
        } else {
            item.executionTimeMM = `${mm}`;
        }
        if (ss > 59) {
            item.executionTimeSS = '00';
        } else if (ss < 10) {
            item.executionTimeSS = `0${ss}`;
        } else {
            item.executionTimeSS = `${ss}`;
        }
        item.executionTimeInDate = `00:${item.executionTimeMM}:${item.executionTimeSS}`;
    } else {
        item.executionTimeInDate = item.executionTimePicker;
    }
    item.executionTime = fomatPreviewExecutionTime(item.executeInterval, item.executionDateInInterval, item.executionTimeInDate).time;
    console.log('item.executionTime', item.executionTime);
};

// 反显到drawer
const loadProjectDetailData = async (scheduledProjectID) => {
    console.log(scheduledProjectID);
    try {
        const result = await FRequest(`/api/v1/projector/scheduled/project/${scheduledProjectID}`, {}, { method: 'get', caches: true });
        timingTask.sys = result.dispatching_system_type;
        timingTask.cluster = result.cluster_name;
        timingTask.scheduledProjectID = result.scheduled_project_id;
        timingTask.scheduledProjectName = result.scheduled_project_name;
        timingTask.publishUser = result.publish_user;
        timingTask.workflows = result.workflow_list.map((item) => {
            const scheduledSignalJson = item.scheduled_signal_json ? JSON.parse(item.scheduled_signal_json) : {};
            return {
                workflowId: item.scheduled_workflow_id,
                workflow: item.scheduled_workflow_name,
                proxyUser: item.proxy_user,
                ruleGroup: item.rule_group_list.map(ruleGroup => ruleGroup.rule_group_id),
                ruleGroupList: item.rule_group_list.map(ruleGroup => ({
                    value: ruleGroup.rule_group_id,
                    label: ruleGroup.rule_group_name,
                })),
                executeInterval: item.execute_interval,
                executionDateInInterval: item.execute_date_in_interval,
                executionTimeInDate: item.execute_time_in_date,
                executionTime: fomatPreviewExecutionTime(item.execute_interval, item.execute_date_in_interval, item.execute_time_in_date).time,
                executionTimePicker: item.execute_interval === 'hour' ? '' : item.execute_time_in_date,
                executionTimeMM: item.execute_interval === 'hour' ? item.execute_time_in_date.split(':')[1] : '',
                executionTimeSS: item.execute_interval === 'hour' ? item.execute_time_in_date.split(':')[2] : '',
                signSettings: item.signal_parameter_list ? item.signal_parameter_list.map((sign) => {
                    const contentJsonObj = JSON.parse(sign.content_json);
                    const userToProxy = contentJsonObj['user.to.proxy'] || '';
                    if (sign.type === 7) {
                        const rmbTargetDcn = contentJsonObj['rmb.targetDcn'] || '';
                        const rmbServiceId = contentJsonObj['rmb.serviceId'] || '';
                        const rmbEnvironment = contentJsonObj['rmb.environment'] || '';
                        const rmbMessage = contentJsonObj['rmb.message'] || '';
                        const rmbMessageType = contentJsonObj['rmb.messageType'] || '';
                        return {
                            signType: sign.type,
                            name: sign.name,
                            streamTask: sign.rule_group_list.map(rule => rule.rule_group_id),
                            ruleGroupList: sign.rule_group_list,
                            userToProxy,
                            rmbTargetDcn,
                            rmbServiceId,
                            rmbEnvironment,
                            rmbMessage,
                            rmbMessageType,
                        };
                    }
                    const msgType = contentJsonObj['msg.type'] || '';
                    const msgSender = contentJsonObj['msg.sender'] || '';
                    const msgReceiver = contentJsonObj['msg.receiver'] || '';
                    const msgTopic = contentJsonObj['msg.topic'] || '';
                    const msgName = contentJsonObj['msg.name'] || '';
                    const msgBody = contentJsonObj['msg.body'] || '';
                    const msgKey = contentJsonObj['msg.key'] || '';
                    return {
                        signType: sign.type,
                        name: sign.name,
                        streamTask: sign.rule_group_list.map(rule => rule.rule_group_id),
                        ruleGroupList: sign.rule_group_list,
                        userToProxy,
                        msgType,
                        msgSender,
                        msgReceiver,
                        msgTopic,
                        msgName,
                        msgBody,
                        msgKey,
                    };
                }) : [],
                scheduleType: item.scheduled_type,
                msgTopic: scheduledSignalJson['msg.topic'],
                msgName: scheduledSignalJson['msg.name'],
                msgSender: scheduledSignalJson['msg.sender'],
                msgKey: scheduledSignalJson['msg.key'],
            };
        });
    } catch (error) {
        console.log('loadProjectDetailData Error:', error);
    }
};
const editPageClusterList = ref([]);
const editPageProjectList = ref([]);

// 表单校验
const MainFormRef = ref(null);
const WorkflowFormRefs = ref([]);
const taskRules = computed(() => ({
    sys: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择调度系统'),
    },
    cluster: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择集群'),
    },
    scheduledProjectID: {
        type: Number,
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择项目'),
    },
    scheduledProjectName: [
        {
            type: 'string',
            required: true,
            trigger: ['blur', 'change'],
            message: $t('_.请输入项目名称'),
        }, {
            validator: (rule, value) => {
                const reg = new RegExp('^[a-zA-Z][a-zA-Z0-9_]*$');
                return reg.test(value) && value.length < 129;
            },
            trigger: ['blur', 'change'],
            message: $t('_.项目名称需以字母开头，允许字母、数字、下划线，不超过 128 字符'),
        },
    ],
    publishUser: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
        message: $t('_.请选择发布用户'),
    },
}));
const aboutTimingTask = async () => {
    emit('update:mode', 'edit');
    emit('update:drawerTitle', $t('_.编辑调度任务'));
};

// 获取集群列表
const genClusterList = async () => {
    const result = await loadClusterList({
        project_id: props.projectId,
        schedule_system: timingTask.sys,
        task_type: 2,
    });
    editPageClusterList.value = result ? result.map(item => ({
        label: item,
        value: item,
    })) : [];
};

const genEditPageProjectList = async () => {
    const result = await loadEditPageProjectList({
        schedule_system: timingTask.sys,
        cluster: timingTask.cluster,
        project_id: props.projectId,
    });
    editPageProjectList.value = result ? result.map(item => ({
        value: item.scheduled_project_id,
        label: item.scheduled_project_name,
    })) : [];
};

// drawer编辑模式下的动态匹配
const taskChange = async (type) => {
    console.log(type, props.drawerTitle);
    if (props.mode === 'create') {
        switch (type) {
            case 'sys':
                timingTask.cluster = '';
                timingTask.publishUser = '';
                publishUserList.value = [];
                break;
            case 'cluster': {
                timingTask.publishUser = '';
                const result = await loadPublishUserList({ cluster: timingTask.cluster });
                publishUserList.value = result ? result.users.map(item => ({
                    label: item,
                    value: item,
                })) : [];
                break;
            }
            default:
                break;
        }
    } else if (props.mode === 'edit') {
        switch (type) {
            // 重置集群、项目、工作流
            // 工作流是项目下的，应该是重新填入
            case 'sys':
                timingTask.cluster = '';
                timingTask.publishUser = '';
                timingTask.scheduledProjectName = '';
                await genClusterList();
                editPageProjectList.value = [];
                publishUserList.value = [];
                timingTask.workflows = [cloneDeep(emptyWorkflow)];
                break;
            // 重置项目、工作流
            case 'cluster': {
                timingTask.scheduledProjectName = '';
                timingTask.publishUser = '';
                const result = await loadPublishUserList({ cluster: timingTask.cluster });
                publishUserList.value = result ? result.users.map(item => ({
                    label: item,
                    value: item,
                })) : [];
                timingTask.workflows = [cloneDeep(emptyWorkflow)];
                await genEditPageProjectList();
                break;
            }
            // 重置项目
            case 'project':
                await loadProjectDetailData(timingTask.scheduledProjectID);
                await genClusterList();
                await genEditPageProjectList();
                break;
            default:
                break;
        }
    }
};


// 在调用创建发布调度任务的接口之前，调用该接口判断新建的调度项目是否已在WTSS创建过，如果已创建，应在页面提示用户，是否覆盖现有调度项目的内容
const checkProjectIfExists = async () => {
    try {
        const result = await FRequest('/api/v1/projector/scheduled/project/checkIfExists', {
            scheduled_project_name: timingTask.scheduledProjectName,
            publish_user: timingTask.publishUser,
            cluster_name: timingTask.cluster,
        });
        return result;
    } catch (error) {
        console.log('checkProjectIfExists', error);
    }
};

const doPublishTask = async () => {
    try {
        await FRequest('/api/v1/projector/scheduled/project/add', {
            project_id: props.projectId,
            dispatching_system_type: timingTask.sys,
            scheduled_project_name: timingTask.scheduledProjectName,
            cluster_name: timingTask.cluster,
            publish_user: timingTask.publishUser,
            workflow_list: timingTask.workflows.map(workflow => ({
                scheduled_workflow_name: workflow.workflow,
                proxy_user: workflow.proxyUser,
                execute_interval: workflow.executeInterval,
                execute_date_in_interval: workflow.executionDateInInterval,
                execute_time_in_date: workflow.executionTimeInDate,
                rule_group_list: workflow.ruleGroupList.map(ruleGroup => ({
                    rule_group_id: ruleGroup.value,
                    rule_group_name: ruleGroup.label,
                })),
                signal_parameter_list: workflow.signSettings.map(sign => ({
                    type: sign.signType,
                    name: sign.name,
                    rule_group_list: sign.ruleGroupList,
                    content_json: sign.contentJson,
                })),
                scheduled_type: workflow.scheduleType,
                scheduled_signal_json: JSON.stringify({
                    'msg.topic': workflow.msgTopic,
                    'msg.name': workflow.msgName,
                    'msg.sender': workflow.msgSender,
                    'msg.key': workflow.msgKey,
                }),
            })),
        });
        emit('publishTask');
        drawerShow.value = false;
        resetTask();
        FMessage.success($t('_.发布成功'));
    } catch (error) {
        console.log('doPublishTask', error);
    }
};

const signRefs = ref({});
const schduleSignForms = ref([]);
// 提交进行表单验证
const finishPublish = async () => {
    try {
        await MainFormRef.value.validate();
        // console.log(signRefs.value)
        const result = await Promise.all([...Object.keys(signRefs.value).map(key => signRefs.value[key].valid()), ...schduleSignForms.value.map(form => form.valid())]);
        if (result.includes(false)) {
            return;
        }
        console.log('finish', timingTask, props.drawerTitle);
        // 发布新的调度任务
        if (props.mode === 'create') {
            const projectExists = await checkProjectIfExists();
            if (!projectExists) {
                await doPublishTask();
            } else {
                FModal.confirm({
                    title: $t('_.提示'),
                    content: $t('_.该调度任务已存在，是否覆盖？'),
                    onOk() {
                        doPublishTask();
                    },
                });
            }
        }
        if (props.mode === 'edit') {
            await FRequest('/api/v1/projector/scheduled/project/modify', {
                scheduled_project_id: timingTask.scheduledProjectID,
                publish_user: timingTask.publishUser,
                workflow_list: timingTask.workflows.map(workflow => ({
                    scheduled_workflow_id: workflow.workflowId || '',
                    scheduled_workflow_name: workflow.workflow,
                    proxy_user: workflow.proxyUser,
                    execute_interval: workflow.executeInterval,
                    execute_date_in_interval: workflow.executionDateInInterval,
                    execute_time_in_date: workflow.executionTimeInDate,
                    rule_group_list: workflow.ruleGroupList.map(ruleGroup => ({
                        rule_group_id: ruleGroup.value,
                        rule_group_name: ruleGroup.label,
                    })),
                    signal_parameter_list: workflow.signSettings.map(sign => ({
                        type: sign.signType,
                        name: sign.name,
                        rule_group_list: sign.ruleGroupList,
                        content_json: sign.contentJson,
                    })),
                    scheduled_type: workflow.scheduleType,
                    scheduled_signal_json: JSON.stringify({
                        'msg.topic': workflow.msgTopic,
                        'msg.name': workflow.msgName,
                        'msg.sender': workflow.msgSender,
                        'msg.key': workflow.msgKey,
                    }),
                })),
            });
            emit('modifyTask');
            FMessage.success($t('_.修改成功'));
            drawerShow.value = false;
            resetTask();
        }
    } catch (err) {
        console.log(err);
    }
};

const deleteTimingTask = async () => {
    try {
        await FRequest(`/api/v1/projector/scheduled/project/${timingTask.scheduledProjectID}`, {}, 'delete');
        drawerShow.value = false;
        resetTask();
        FMessage.success($t('_.删除成功'));
        emit('deleteTask');
    } catch (error) {
        console.log('deleteTimingTask Error:', error);
    }
};

const deleteWorkflow = (index) => {
    timingTask.workflows.splice(index, 1);
};

watch(() => props.show, async () => {
    if (props.show && props.mode === 'view' && props.scheduledProjectID) {
        await loadProjectDetailData(props.scheduledProjectID);
        await genClusterList();
        await genEditPageProjectList();
    } else if (props.mode === 'create') {
        const result = await loadPublishClusterList();
        editPageClusterList.value = result ? result.optional_clusters.map(item => ({
            label: item,
            value: item,
        })) : [];
    } else if (timingTask.sys) {
        await genClusterList();
    } else {
        editPageClusterList.value = [];
    }
});

onMounted(async () => {
    const proxyRes = await loadProxyUserList();
    proxyUserList.value = proxyRes ? proxyRes.map(item => ({
        label: item,
        value: item,
    })) : [];
    const ruleRes = await loadRuleGroupList({ projectId: props.projectId });
    ruleGroupList.value = ruleRes ? ruleRes.map(item => ({
        label: item.rule_group_name,
        value: item.rule_group_id,
    })) : [];
    const sysRes = await loadSysTypeList();
    sysTypeList.value = sysRes ? sysRes.map(item => ({
        label: item.message,
        value: item.message,
    })) : [];
});

const handleClose = () => {
    drawerShow.value = false;
    resetTask();
};


</script>
<style lang='less' scoped>
@import "@/style/varible";

.workflows-container {
    &>div:nth-of-type(n + 2) {
        margin-top: 16px;
    }

    .workflows {
        border: 1px solid #f1f1f2;
        border-radius: 4px;
        padding: 16px;

        .workflows-title {
            padding: 16px;
            overflow: hidden;

            .workflows-delete {
                font-family: PingFangSC-Regular;
                font-size: 14px;
                color: #93949b;
                letter-spacing: 0;
                line-height: 22px;
                font-weight: 400;
                float: right;
                cursor: pointer;
            }

            .workflows-label {
                font-family: PingFangSC-Regular;
                font-size: 14px;
                color: #0f1222;
                letter-spacing: 0;
                line-height: 22px;
                font-weight: 400;
                float: left;
            }
        }

        .workflows-form {
            margin-left: 16px;

            .workflows-input {
                width: 100%;
            }

            .workflows-select {
                width: 100%;
            }

            .execution-time-wrapper {
                width: 100%;
                display: flex;
                align-items: center;

                .mr-8px {
                    margin-right: 8px;
                }
            }
        }
    }
}

.add-workflow {
    color: @blue-color;
    margin-top: 8px;

    .add-workflow-btn {
        display: flex;
        align-items: center;
        justify-content: flex-start;
        cursor: pointer;
    }
}

.sign-wrap {
    width: 100%;
}

.schedule-sign-box {
    width: 100%;
    border: 1px solid #CFD0D3;
    border-radius: 4px;
    padding: 16px 16px 0 16px;
}

.add-sign {
    color: @blue-color;

    .add-sign-btn {
        display: flex;
        align-items: center;
        justify-content: flex-start;
        cursor: pointer;
    }
}

.view-font {
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #0f1222;
    line-height: 22px;
    font-weight: 400;
}
</style>
