<template>
    <div class="selector">
        <div class="query-input">
            <FSelect
                v-model="currentSelected"
                class="workflows-select"
                filterable
                clearable
                multiple
                :options="curOptions"
                @change="change"
            ></FSelect>
        </div>
        <div class="tag-label">
            <span>已选：</span>
            <span class="clear-btn" @click="clearAll">全部清除</span>
        </div>
        <div class="tag-container">
            <FTag
                v-for="(item, index) in currentSelectedList"
                :key="item"
                type="info"
                style="margin-right: 8px; margin-top: 8px;"
                closable
                @close="deleteTag(item, index)"
            >
                {{item.label}}
            </FTag>
        </div>
    </div>
</template>
<script setup>
import { unionWith } from 'lodash-es';
import {
    defineProps, ref, defineEmits, computed, unref, watch, watchEffect,
} from 'vue';

const emit = defineEmits(['update:selected', 'update:selectedList']);
const props = defineProps({
    selected: {
        type: Array,
        required: true,
    },
    options: {
        type: Array,
        required: true,
    },
    selectedList: {
        type: Array,
        required: true,
    },
});
const currentSelected = ref(unref(props.selected));
watch(() => props.selected, (cur) => {
    console.log(cur);
    currentSelected.value = cur;
});
const currentSelectedList = computed(() => props.options.filter(item => currentSelected.value.includes(item.value)));
const curOptions = ref(props.options);
watchEffect(() => {
    if (props.options.length > 0 && 'used' in props.options[0]) {
        curOptions.value = props.options.filter(item => item.used === false).concat(currentSelectedList.value);
        curOptions.value = unionWith(curOptions.value, (item1, item2) => item1.value === item2.value);
    }
});

const change = () => {
    console.log(currentSelected.value);
    console.log(currentSelectedList.value);
    emit('update:selected', currentSelected.value);
    emit('update:selectedList', currentSelectedList.value);
};
const deleteTag = (item) => {
    for (let i = 0; i < currentSelected.value.length; i++) {
        if (currentSelected.value[i] === item.value) {
            currentSelected.value.splice(i, 1);
        }
    }
    change();
};
const clearAll = () => {
    currentSelected.value = [];
};
</script>
<style lang="less" scoped>
.selector {
    border: 1px solid #cfd0d3;
    border-radius: 4px;
    padding: 16px;
    width: 100%;
    min-height: 240px;
    max-height: 320px;
    overflow: auto;
    .query-input {
        :deep(.fes-select-trigger .fes-select-trigger-label-item) {
            display: none;
        }
    }
    .tag-label {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 16px;
        .clear-btn{
            cursor: pointer;
            color: #93949B;
        }
    }
}
</style>
