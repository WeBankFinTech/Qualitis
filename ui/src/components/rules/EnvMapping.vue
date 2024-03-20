<template>
    <div class="env-mapping">
        <div class="top">
            <div class="mapping-name">映射{{props.no + 1}}</div>
            <div v-if="deletable" class="del-btn" @click="del"><MinusCircleOutlined /><span class="del-text">删除</span></div>
        </div>
        <div class="env-map-form">
            <slot name="form"></slot>
        </div>
    </div>
</template>
<script setup>
import { defineProps, defineEmits } from 'vue';
import { MinusCircleOutlined } from '@fesjs/fes-design/es/icon';

const props = defineProps({
    no: {
        type: Number,
        required: true,
        default: 0,
    },
    deletable: {
        type: Boolean,
        required: true,
        default: false,
    },
});
const emit = defineEmits(['deleteMapping']);
const del = () => {
    emit('deleteMapping');
};
</script>
<style scoped lang="less">
.env-mapping {
    width: 488px;
    background-color: #f7f8f8;
    padding: 24px;
    margin-bottom: 16px;
    .top {
        width: 100%;
        display: flex;
        justify-content: space-between;
        margin-bottom: 16px;
        .mapping-name {
            font-family: PingFangSC-Medium;
            font-size: 14px;
            color: #0F1222;
            line-height: 22px;
            font-weight: 500;
        }
    }
}
</style>
