<template>
    <div class="btn">
        <span class="file">{{props.file.split('/')[props.file.split('/').length - 1]}}</span><CloseCircleOutlined class="close" @click.stop="delFile" />
    </div>
</template>

<script setup>
import {
    defineProps, defineEmits, computed, ref,
} from 'vue';
import {
    CloseCircleOutlined,
} from '@fesjs/fes-design/es/icon';

const props = defineProps({
    file: {
        type: String,
        required: true,
    },
});
const emit = defineEmits(['delFile']);

const delFile = () => {
    emit('delFile');
};
</script>
<style lang="less" scoped>
.btn {
    cursor: pointer;
    display: flex;
    align-items: center;
    padding:5px;
    min-width: 81px;
    .close {
        visibility: hidden;
        display: inline-block;
    }
    .file {
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #93949B;
        letter-spacing: 0;
        font-weight: 400;
    }
    &:hover {
        background: #F6F9FF;
        border-radius: 4px;
        .close {
            visibility: visible;
            margin-left: 10px;
        }
        .file {
            font-family: PingFangSC-Regular;
            font-size: 14px;
            color: #5384FF;
            letter-spacing: 0;
            font-weight: 400;
        }
    }
}
</style>
