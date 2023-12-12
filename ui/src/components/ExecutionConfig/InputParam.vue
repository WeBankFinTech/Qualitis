<template>
    <div class="root">
        <div v-for="(value,i) in tempList" :key="i" class="row-wrap">
            <div class="row">
                <FInput v-model="value.name" placeholder="请输入参数名" clearable></FInput>
                <FInput v-model="value.value" class="value" placeholder="请输入参数值" clearable></FInput>
                <PlusCircleFilled v-if="i === tempList.length - 1" class="add" @click="add" />
                <MinusCircleOutlined v-else @click="remove(i)" />
            </div>
        </div>
    </div>
</template>
<script setup>
import {
    defineProps, watchEffect, ref, defineEmits, computed,
} from 'vue';
import { PlusCircleFilled, MinusCircleOutlined } from '@fesjs/fes-design/icon';

function configRepeatValidator(value) {
    const set = new Set();
    value.forEach((v) => {
        if (set.has(v)) {
            return false;
        }
        set.add(v);
    });
    return true;
}
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
const tempList = computed(() => {
    const index = paramList.value.findIndex(arg => arg.name === 'split_by');
    const list = paramList.value.slice();
    console.log('list', JSON.stringify(list));
    if (index >= 0) {
        list.splice(index, 1);
    }
    console.log('list', JSON.stringify(list), index);
    return list;
});
function add(e) {
    paramList.value.push({ value: '', name: '' });
}
function remove(e) {
    let index = e;
    if (paramList.value.findIndex(arg => arg.name === 'split_by') <= e) {
        index = e + 1;
    }
    paramList.value.splice(index, 1);
}
const emit = defineEmits(['update:modelValue']);
watchEffect(() => {
    emit('update:modelValue', paramList.value);
});
</script>
<style lang="less" scoped>
.root{
    font-size:14px;
    .row-wrap:not(:last-child){
        margin-bottom: 10px;
    }
    .row{
        display: flex;
        align-items: center;
    }
    .add{
        color:#5384ff;
    }
    .value{
        margin-left: 8px;
        margin-right: 16px;
    }
}
</style>
