<template>
    <img :id="validUniqId" class="clip-btn" src="@/assets/images/icons/clipboard.svg" @click="clip" />
</template>
<script setup>
import { defineProps, computed } from 'vue';
import Clipboard from 'clipboard';
import { FMessage } from '@fesjs/fes-design';

const props = defineProps({
    uniqid: String,
    injectVal: String,
});

const validUniqId = computed(() => `UNIQUE-${props.uniqid}`);
const clip = () => {
    const clipboard = new Clipboard(`#${validUniqId.value}`, {
        text: () => props.injectVal,
    });
    clipboard.on('success', () => {
        FMessage.success('复制成功');
        clipboard.destroy();
    });
    clipboard.on('error', (val) => {
        console.error(val);
        FMessage.error('复制失败');
        clipboard.destroy();
    });
};
</script>
<style lang="less" scoped>
.clip-btn {
    filter: invert(60%) sepia(2%) saturate(868%) hue-rotate(195deg) brightness(99%) contrast(86%);
    &:hover {
        cursor: pointer;
        filter: invert(55%) sepia(52%) saturate(6477%) hue-rotate(211deg) brightness(108%) contrast(101%);
    }
}
</style>
