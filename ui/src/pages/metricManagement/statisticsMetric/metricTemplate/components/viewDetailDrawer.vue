<template>
    <FDrawer v-model:show="showModal" :title="title" width="50%" :maskClosable="false" displayDirective="if" contentClass="metricTemplateDrawer">
        {{content}}
    </FDrawer>
</template>
<script setup>
import {
    ref, defineProps, computed, defineEmits,
} from 'vue';
import { useI18n } from '@fesjs/fes';

const { t: $t } = useI18n();

const emits = defineEmits(['update:show']);
const props = defineProps({
    show: {
        type: Boolean,
        default: false,
    },
    content: {
        type: String,
        default: '',
    },
    name: {
        type: String,
        default: '',
    },
});
const title = computed(() => `指标模板(${props.name})`);
const showModal = computed({
    get() {
        return props.show;
    },
    set(val) {
        emits('update:show', val);
    },
});

const content = computed(() => (props.content ? props.content : {}));
</script>
<style lang="less">
.metricTemplateDrawer {
    .fes-scrollbar-content {
        border: 1px solid #F1F1F2;
        border-radius: 4px;
        padding: 16px 16px;
        min-height: 100%;
        word-break: break-all;
    }
}
</style>
