<template>
    <div class="wrap">
        <div class="header">
            信号{{index}}
            <span v-if="mode !== 'view'" class="delete-btn" @click="deleteSign">
                <MinusCircleOutlined style="margin-right: 4.67px;" />删除
            </span>
        </div>
        <FForm
            ref="signForm"
            class="rule-detail-form"
            :class="{ edit: mode !== 'view' }"
            :labelWidth="signData.signType === 7 ? 112 : 86"
            labelPosition="right"
            :model="signData"
            :rules="mode !== 'view' ? signRules : []"
        >
            <FFormItem label="信号名称" :labelWidth="80" prop="name">
                <FInput v-model="signData.name" class="form-edit-input" placeholder="请输入" />
                <span class="form-preview-label">{{signData.name}}</span>
            </FFormItem>
            <FFormItem label="信号类型" :labelWidth="80" prop="signType">
                <FSelect
                    v-model="signData.signType"
                    class="form-edit-input"
                    :options="signTypeList"
                    @change="handleSignTypeChange"
                ></FSelect>
                <span
                    class="form-preview-label"
                >{{getLabelByValue(signTypeList, signData.signType)}}</span>
            </FFormItem>
            <div v-if="signData.signType" class="sign-detail">
                <div v-if="signData.signType === 7">
                    <FFormItem label="user.to.proxy" prop="userToProxy">
                        <FInput
                            v-model="signData.userToProxy"
                            class="form-edit-input"
                            placeholder="-"
                            disabled
                        />
                        <span class="form-preview-label">{{signData.userToProxy}}</span>
                    </FFormItem>
                    <FFormItem label="rmb.targetDcn" prop="rmbTargetDcn">
                        <FInput
                            v-model="signData.rmbTargetDcn"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.rmbTargetDcn}}</span>
                    </FFormItem>
                    <FFormItem label="rmb.serviceId" prop="rmbServiceId">
                        <FInput
                            v-model="signData.rmbServiceId"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.rmbServiceId}}</span>
                    </FFormItem>
                    <FFormItem label="rmb.environment" prop="rmbEnvironment">
                        <FInput
                            v-model="signData.rmbEnvironment"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.rmbEnvironment}}</span>
                    </FFormItem>
                    <FFormItem label="rmb.message" prop="rmbMessage">
                        <FInput
                            v-model="signData.rmbMessage"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.rmbMessage}}</span>
                    </FFormItem>
                    <FFormItem label="rmb.messageType" prop="rmbMessageType">
                        <FInput
                            v-model="signData.rmbMessageType"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.rmbMessageType}}</span>
                    </FFormItem>
                </div>
                <div v-else>
                    <FFormItem label="user.to.proxy" prop="userToProxy">
                        <FInput
                            v-model="signData.userToProxy"
                            class="form-edit-input"
                            placeholder="-"
                            disabled
                        />
                        <span class="form-preview-label">{{signData.userToProxy}}</span>
                    </FFormItem>
                    <FFormItem label="msg.type" prop="msgType">
                        <FInput
                            v-model="signData.msgType"
                            class="form-edit-input"
                            placeholder="-"
                            disabled
                        />
                        <span class="form-preview-label">{{signData.msgType}}</span>
                    </FFormItem>
                    <FFormItem v-if="signData.signType === 6" label="msg.sender" prop="msgSender">
                        <FInput
                            v-model="signData.msgSender"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.msgSender}}</span>
                    </FFormItem>
                    <FFormItem
                        v-if="signData.signType === 1"
                        label="msg.receiver"
                        prop="msgReceiver"
                    >
                        <FInput
                            v-model="signData.msgReceiver"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.msgReceiver}}</span>
                    </FFormItem>
                    <FFormItem label="msg.topic" prop="msgTopic">
                        <FInput
                            v-model="signData.msgTopic"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.msgTopic}}</span>
                    </FFormItem>
                    <FFormItem label="msg.name" prop="msgName">
                        <FInput
                            v-model="signData.msgName"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.msgName}}</span>
                    </FFormItem>
                    <FFormItem v-if="signData.signType === 1" label="msg.key" prop="msgKey">
                        <FInput
                            v-model="signData.msgKey"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.msgKey}}</span>
                    </FFormItem>
                    <FFormItem v-if="signData.signType === 6" label="msg.body" prop="msgBody">
                        <FInput
                            v-model="signData.msgBody"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <span class="form-preview-label">{{signData.msgBody}}</span>
                    </FFormItem>
                </div>
                <FFormItem class="last-element" :label="signData.signType === 1 ? '信号下游任务' : '信号上游任务'" prop="streamTask">
                    <FSelect
                        v-model="signData.streamTask"
                        class="form-edit-input"
                        :options="taskList"
                        placeholder="请选择，如不选择则默认关联当前工作流下所有任务"
                        multiple
                        @change="onTaskChange"
                    ></FSelect>
                    <span class="form-preview-label">
                        <FTag
                            v-for="(item) in signData.ruleGroupList"
                            :key="item"
                            type="info"
                            style="margin-right: 8px;"
                        >{{item.rule_group_name}}</FTag>
                    </span>
                </FFormItem>
            </div>
        </FForm>
    </div>
</template>
<script setup>
import { computed, ref, watch } from 'vue';
import { getLabelByValue } from '@/common/utils';
import { MinusCircleOutlined } from '@fesjs/fes-design/es/icon';


// eslint-disable-next-line no-undef
const props = defineProps({
    index: {
        type: Number,
        requierd: true,
    },
    signData: {
        type: Object,
        requierd: true,
        default: () => { },
    },
    mode: {
        type: String,
        requierd: true,
        default: 'view',
    },
    proxyUser: {
        type: String,
        requierd: true,
    },
    taskList: {
        type: Array,
        requierd: true,
        default: () => [],
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['delete', 'update:signData']);
const deleteSign = () => {
    emit('delete');
};
const currentSignData = computed({
    get: () => props.signData,
    set: (value) => {
        emit('update:signData', value);
    },
});
const signTypeList = [
    { label: '接收信号', value: 1 },
    { label: '发送WTSS内部信号', value: 6 },
    { label: '发送RMB信号', value: 7 },
];
const signForm = ref(null);
const signRules = {
    signType: [
        {
            required: true,
            type: 'number',
            trigger: ['blur', 'change'],
            message: '请选择信号类型',
        },
    ],
    name: [
        {
            required: true,
            trigger: ['blur', 'change'],
            message: '请输入信号名称',
        },
    ],

};
watch(() => props.proxyUser, (cur) => {
    currentSignData.value.userToProxy = cur;
}, { immediate: true });
// 已选规则组发生变化时清空已选的上、下游任务
watch(() => props.taskList, () => {
    currentSignData.value.streamTask = [];
    currentSignData.value.ruleGroupList = [];
});
const handleSignTypeChange = () => {
    switch (currentSignData.value.signType) {
        case 1:
            currentSignData.value.msgType = 'RECEIVE';
            break;
        case 6:
            currentSignData.value.msgType = 'SEND';
            break;
        default:
            currentSignData.value.msgType = '';
            break;
    }
};
const onTaskChange = () => {
    console.log(currentSignData.value.streamTask);
    currentSignData.value.ruleGroupList = props.taskList
        .filter(item => currentSignData.value.streamTask.includes(item.value))
        .map(item => ({ rule_group_id: item.value, rule_group_name: item.label }));
    console.log(currentSignData.value.ruleGroupList);
};
const genContentJson = () => {
    if (currentSignData.value.signType === 7) {
        currentSignData.value.contentJson = JSON.stringify({
            'user.to.proxy': currentSignData.value.userToProxy,
            'rmb.targetDcn': currentSignData.value.rmbTargetDcn,
            'rmb.serviceId': currentSignData.value.rmbServiceId,
            'rmb.environment': currentSignData.value.rmbEnvironment,
            'rmb.message': currentSignData.value.rmbMessage,
            'rmb.messageType': currentSignData.value.rmbMessageType,
        });
    } else if (currentSignData.value.signType === 6) {
        currentSignData.value.contentJson = JSON.stringify({
            'user.to.proxy': currentSignData.value.userToProxy,
            'msg.type': currentSignData.value.msgType,
            'msg.sender': currentSignData.value.msgSender,
            'msg.topic': currentSignData.value.msgTopic,
            'msg.name': currentSignData.value.msgName,
            'msg.body': currentSignData.value.msgBody,
        });
    } else {
        currentSignData.value.contentJson = JSON.stringify({
            'user.to.proxy': currentSignData.value.userToProxy,
            'msg.type': currentSignData.value.msgType,
            'msg.receiver': currentSignData.value.msgReceiver,
            'msg.topic': currentSignData.value.msgTopic,
            'msg.name': currentSignData.value.msgName,
            'msg.key': currentSignData.value.msgKey,
        });
    }
};
const valid = async () => {
    try {
        await signForm.value?.validate();
        genContentJson();
        // console.log('sign '+props.index,currentSignData.value)
        return true;
    } catch (e) {
        return false;
    }
};

// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
<style lang='less' scoped>
.wrap {
    padding: 16px;
    border: 1px solid #cfd0d3;
    border-radius: 4px;
    margin-bottom: 16px;
    margin-right: 8px;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    .delete-btn {
        cursor: pointer;
        display: flex;
        align-items: center;
    }
}
.sign-detail {
    border-radius: 4px;
    padding: 16px;
    background-color: #f7f7f8;
    .last-element {
        margin-bottom: 0px;
    }
}
.rule-detail-form {
    margin-bottom: 0px;
    :deep(.fes-input) {
        max-width: 100%;
    }
    :deep(.fes-select) {
        max-width: 100%;
    }
    :deep(.fes-textarea) {
        max-width: 100%;
    }
}
</style>
