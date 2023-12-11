<template>
    <FTooltip
        mode="popover"
        :trigger="trigger"
        placement="bottom-start"
    >
        <div class="popover-text">
            <slot></slot>
        </div>
        <template #content>
            <div class="popover" :style="`width:${width};`">
                <div
                    v-for="item, index in content"
                    :key="index"
                    class="popover-item"
                >
                    <span>{{item.label}}</span>
                    <FEllipsis :line="3">{{item.value || '--'}}</FEllipsis>
                </div>
            </div>
        </template>
        <template
            v-if="title"
            #title
        >
            {{title}}
        </template>
    </FTooltip>
</template>
<script setup>
import { FTooltip } from '@fesjs/fes-design';
// eslint-disable-next-line no-undef
const props = defineProps({
    title: {
        type: String,
        required: true,
        default: '标题',
    },
    content: {
        tyep: Array,
        required: true,
    },
    trigger: {
        type: String,
        default: 'hover',
    },
    width: {
        type: String,
        default: '256px',
    },
});

</script>
<style lang='less' scoped>
.popover {
    font-size: 14px;

    &-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 16px;

        span {
            display: block;
            margin-right: 16px;
            min-width: 56px;
            flex-shrink: 0;
        }
    }

    :nth-last-child(1) {
        margin-bottom: 0;
    }

    &-text {}


}
</style>
