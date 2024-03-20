<template>
    <FForm ref="form" labelPosition="right" :model="{ dynamic,args,partition }" :rules="rules" :labelWidth="71">
        <!-- 并发粒度 -->
        <FFormItem label="并发粒度" prop="batchType">
            <FRadioGroup v-model="batchType" @change="radioChange">
                <FRadio :value="true">库粒度</FRadio>
                <FRadio :value="false">表粒度</FRadio>
            </FRadioGroup>
        </FFormItem>
        <FFormItem :label="`${$t('common.isDynamicPartition')}`" prop="dynamic">
            <FSwitch v-model="dynamic"></FSwitch>
        </FFormItem>
        <FFormItem v-if="dynamic" :label="`${$t('common.topPartition')}`" prop="partition">
            <FInput v-model="partition" :placeholder="$t('common.pleaseEnter') + $t('common.topPartition')" clearable></FInput>
        </FFormItem>
        <FFormItem :label="`${$t('common.execution_param')}`" prop="args">
            <InputParam v-model="args" />
        </FFormItem>
    </FForm>
</template>
<script setup>
import {
    defineProps, watchEffect, ref, defineEmits, watch, defineExpose,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import InputParam from './InputParam';
import { repeatValidator } from './utils';

const { t: $t } = useI18n();
const props = defineProps({
    dynamic: Boolean,
    args: Array,
    partition: String,
});
const dynamic = ref(false);
const batchType = ref(true);
const args = ref([]);
const partition = ref('');
watchEffect(() => {
    dynamic.value = props.dynamic;
    args.value = props.args;
    partition.value = props.partition;
    batchType.value = !(props.args || []).some(arg => arg.name === 'split_by' && arg.value === 'table');
});
const rules = {
    // partition: [{
    //     asyncValidator: (rule, value) => new Promise((res, rej) => {
    //         if (value) {
    //             const reg = /^([a-zA-Z0-9_-]+\s*=\s*(([a-zA-Z0-9_-]+)|("[a-zA-Z0-9_-]+")|('[a-zA-Z0-9_-]+'))\s+and\s+)*([a-zA-Z0-9_-]+\s*=\s*(([a-zA-Z0-9_-]+)|("[a-zA-Z0-9_-]+")|('[a-zA-Z0-9_-]+')))$/ig;
    //             if (reg.test(value)) {
    //                 res();
    //             } else {
    //                 rej('顶层分区格式为大小写字母，数字，"_"，"-"组成');
    //             }
    //         } else {
    //             res();
    //         }
    //     }),
    // }],
    args: [{ asyncValidator: (_, arr) => repeatValidator('执行变量配置-变量替换', arr) }],
};
const form = ref();
const emit = defineEmits(['update:dynamic', 'update:args', 'update:partition']);
function radioChange(val) {
    const index = args.value.findIndex(arg => arg.name === 'split_by');
    if (index >= 0) {
        if (val) {
            args.value.splice(index, 1);
        } else {
            args.value.splice(index, 1, { value: 'table', name: 'split_by' });
        }
    } else if (!val) {
        args.value.push({ value: 'table', name: 'split_by' });
    }
}
watch(dynamic, () => {
    emit('update:dynamic', dynamic.value);
});
watch(partition, () => {
    emit('update:partition', partition.value);
});
watch(args, () => {
    emit('update:args', args.value);
}, { deep: true });
function valid() {
    return form.value.validate();
}
defineExpose({ valid });
</script>
