<template>
    <FForm
        ref="formRef"
        :labelWidth="84"
        labelPosition="right"
        :model="formModel"
        :rules="formRule"
    >
        <FFormItem
            label="msg.topic"
            :prop="`msgTopic`"
        >
            <FInput
                v-if="mode !== 'view'"
                v-model="formModel.msgTopic"
                :placeholder="$t('_.请输入')"
                class="workflows-input"
            />
            <span
                v-else
                class="view-font"
            >{{formModel.msgTopic}}</span>
        </FFormItem>
        <FFormItem
            label="msg.name"
            :prop="`msgName`"
        >
            <FInput
                v-if="mode !== 'view'"
                v-model="formModel.msgName"
                :placeholder="$t('_.请输入')"
                class="workflows-input"
            />
            <span
                v-else
                class="view-font"
            >{{formModel.msgName}}</span>
        </FFormItem>
        <FFormItem
            label="msg.sender"
            :prop="`msgSender`"
        >
            <FInput
                v-if="mode !== 'view'"
                v-model="formModel.msgSender"
                :placeholder="$t('_.请输入')"
                class="workflows-input"
            />
            <span
                v-else
                class="view-font"
            >{{formModel.msgSender}}</span>
        </FFormItem>
        <FFormItem
            label="msg.key"
            :prop="`msgKey`"
        >
            <FInput
                v-if="mode !== 'view'"
                v-model="formModel.msgKey"
                :placeholder="$t('_.请输入')"
                class="workflows-input"
            />
            <span
                v-else
                class="view-font"
            >{{formModel.msgKey}}</span>
        </FFormItem>
    </FForm>
</template>
<script setup>
import { useI18n } from '@fesjs/fes';

import { computed, ref } from 'vue';


const { t: $t } = useI18n();

// eslint-disable-next-line no-undef
const props = defineProps({
    modelData: {
        type: Object,
        default: () => { },
    },
    mode: {
        type: String,
        required: true,
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:modelData', 'update:show', 'save']);

const formRef = ref(null);
const formModel = computed({
    get: () => props.modelData,
    set: (value) => {
        emit('update:modelData', value);
    },
});

const formRule = ref({
    msgTopic: [{ required: props.mode !== 'view', message: $t('_.请输入msgtopic'), type: 'string' }],
    msgName: [{ required: props.mode !== 'view', message: $t('_.请输入msgname'), type: 'string' }],
    msgSender: [{ required: props.mode !== 'view', message: $t('_.请输入msgsender'), type: 'string' }],
    msgKey: [{ required: props.mode !== 'view', message: $t('_.请输入msgkey'), type: 'string' }],
});

const reset = () => {
    formRef.value.resetFields();
};
const clearValid = () => {
    formRef.value.clearValidate();
};
const valid = async () => {
    try {
        if (!formRef.value) {
            return true;
        }
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
