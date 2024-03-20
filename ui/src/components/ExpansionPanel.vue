<template>
    <div style="border-radius: 4px">
        <div class="header" @click="() => (expanded = !expanded)">
            <div>
                <slot name="header"></slot>
            </div>
            <DownOutlined v-if="!expanded" />
            <UpOutlined v-else />
        </div>
        <div class="body" :style="{ height: height }">
            <div ref="body">
                <slot name="body"></slot>
            </div>
        </div>
    </div>
</template>
<script setup>
import { DownOutlined, UpOutlined } from '@fesjs/fes-design/icon';
import {
    defineProps, watchEffect, ref, defineEmits, onMounted,
} from 'vue';
import { useTransition } from '@/hooks/useTransition';

const props = defineProps({ modelValue: Boolean });
const emit = defineEmits(['update:modelValue']);
const { expanded, body, height } = useTransition();
watchEffect(() => {
    expanded.value = props.modelValue;
});
watchEffect(() => {
    emit('update:modelValue', expanded.value);
});
</script>
<style lang="less" scoped>
.header {
    cursor: pointer;
    display: flex;
    background-color: #f7f7f8;
    border-radius: 4px;
    height: 54px;
    overflow: hidden;
    align-items: center;
    padding: 16px;
    font-family: PingFangSC-Medium;
    font-size: 16px;
    color: #0F1222;
    letter-spacing: 0;
    line-height: 22px;
    font-weight: 500;
    > div {
        flex-grow: 1;
    }
}
.body {
    background-color: #f7f7f8;
    overflow: hidden;
    transition: height 300ms cubic-bezier(0.4, 0, 0.2, 1) 0ms;
}
</style>
