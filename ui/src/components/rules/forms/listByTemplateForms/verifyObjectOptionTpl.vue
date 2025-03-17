<template>
    <div class="rule-detail-form">
        <div class="wd-content-body tab-content obejct-group-content">
            <ul class="wd-body-menus">
                <li class="wd-body-menu-item">
                    <div class="deleteBtn" @click="copyVerifyObject">
                        <fes-icon type="copy" />
                        <span>{{$t('_.复制')}}</span>
                    </div>
                </li>
                <li v-if="isNeedDelete" class="wd-body-menu-item">
                    <div class="deleteBtn" @click="deleteVerifyObject">
                        <MinusCircleOutlined />
                        <span>{{$t('_.删除')}}</span>
                    </div>
                </li>
            </ul>
            <slot></slot>
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps, computed } from 'vue';
import { MinusCircleOutlined } from '@fesjs/fes-design/es/icon';
import { useI18n } from '@fesjs/fes';

const { t: $t } = useI18n();
const props = defineProps({
    index: {
        type: Number,
        default: 0,
    },
    len: {
        type: Number,
        default: 0,
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['delete', 'copy']);
const isNeedDelete = computed(() => !(props.len === 1 && props.index === 0));
const deleteVerifyObject = (type) => {
    emit('delete', props.index);
};

const copyVerifyObject = (type) => {
    emit('copy', props.index);
};
</script>

<style lang="less" scoped>
.obejct-group-content{
    background: rgba(15,18,34,.03);
    border-radius: 4px;
    padding: 24px;
    width: 498px;
    .deleteBtn {
        display: flex;
        height: 22px;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #93949B;
        letter-spacing: 0;
        align-items: center;
        font-weight: 400;
        cursor: pointer;
        & > span {
           padding-left: 6px;
        }
    }
}
</style>
