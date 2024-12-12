<template>
    <div class="root">
        <div v-for="(value, i) in paramList" :key="value.name" class="row">
            <FSelect
                v-model="value.name"
                placeholder="请选择参数名称"
                class="max-w-40"
                clearable
                @change="onSelectChange(i)"
            >
                <FOption
                    v-for="name in nameList"
                    :key="name.value"
                    :value="name.value"
                    :label="name.label"
                ></FOption>
            </FSelect>
            <FInput
                class="value"
                :modelValue="value.value"
                placeholder="请输入参数值"
                clearable
                @input="
                    (e) => {
                        onInputChange(i, e);
                    }
                "
            ></FInput>
            <PlusCircleFilled
                v-if="i === paramList.length - 1"
                class="add"
                @click="add"
            />
            <MinusCircleOutlined v-else @click="remove(i)" />
        </div>
    </div>
</template>
<script setup>
import {
    defineProps, watchEffect, ref, defineEmits, watch,
} from 'vue';
import { PlusCircleFilled, MinusCircleOutlined } from '@fesjs/fes-design/icon';

const props = defineProps({
    modelValue: Array,
    nameList: Array,
});
const paramList = ref([]);
watchEffect(() => {
    const temp = props.modelValue;
    if (temp.length === 0) {
        temp.push({ value: '', name: '' });
    }
    paramList.value = temp;
});
function onSelectChange(e) {
    const name = paramList.value[e].name;
    paramList.value[e].value = props.nameList.find(
        v => v.value === name,
    ).default;
}
function onInputChange(i, e) {
    paramList.value[i].value = e;
}
function add(e) {
    paramList.value.push({ value: '', name: '' });
}
function remove(e) {
    paramList.value.splice(e, 1);
}
const emit = defineEmits(['update:modelValue']);
watch(paramList, () => {
    emit('update:modelValue', paramList.value);
}, { deep: true });
</script>
<style lang="less" scoped>
.root {
    font-size: 14px;
    width: 100%;
    .row {
        display: flex;
        align-items: center;
        .value {
            margin-left: 8px;
            margin-right: 16px;
        }
        &:not(:last-child){
            margin-bottom: 10px;
        }
    }
    .add {
        color: #5384ff;
    }
}
.max-w-40{
    max-width: 160px;
}
</style>
