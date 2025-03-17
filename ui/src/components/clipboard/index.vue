<template>
    <div class="copy-td">
        <template v-if="isLink">
            <FTooltip :content="val" placement="top">
                <div class="a-link row-content" href="javascript:void(0);" @click="handleClick">{{val || '--'}}</div>
            </FTooltip>
        </template>
        <template v-else>
            <FTooltip :content="val" placement="top">
                <div class="row-content">{{val}}</div>
            </FTooltip>
        </template>


        <clipboard class="clipboard" :injectVal="val" :uniqid="val" />
    </div>
</template>
<script setup>
import { defineProps, defineEmits } from 'vue';
import clipboard from './core.vue';

const props = defineProps({
    val: String,
    isLink: Boolean,
});
const emits = defineEmits(['clickLink']);
function handleClick() {
    emits('clickLink');
}
</script>
<style lang="less" scoped>
@import '@/style/varible.less';
.copy-td {
    display: flex;
    justify-content: start;
    .a-link {
        color: @blue-color;
        cursor: pointer;
    }
    .clipboard {
        display: none;
    }
    .row-content {
        max-width: 100%;
        margin-right: 5px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        position: relative;
    }
}
.fes-table-td {

    &:hover {
        .clipboard {
           display: inherit;
        }
    }
}
</style>
