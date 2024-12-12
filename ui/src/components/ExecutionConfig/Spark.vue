<template>
    <FForm ref="form" labelPosition="right" :model="{ param }" :rules="rules" :labelWidth="71">
        <FFormItem class="item" prop="param" :label="`${$t('common.configParams')}`">
            <ParamSetting v-model="param" :nameList="parameterNameList" />
        </FFormItem>
    </FForm>
</template>
<script setup>
import {
    defineProps, onMounted, ref, watchEffect, defineEmits, watch, defineExpose,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import ParamSetting from './SelectParam.vue';
import { repeatValidator } from './utils';

const { t: $t } = useI18n();
const props = defineProps({ modelValue: Array });
const param = ref([]);
const parameterNameList = [
    {
        label: 'spark.sql.adaptive.skewJoin.enabled',
        value: 'spark.sql.adaptive.skewJoin.enabled',
    },
    {
        label: 'spark.sql.adaptive.skewJoin.skewedPartitionFactor',
        value: 'spark.sql.adaptive.skewJoin.skewedPartitionFactor',
    },
    {
        label: 'spark.sql.shuffle.partitions',
        value: 'spark.sql.shuffle.partitions',
    },
];
watchEffect(() => {
    param.value = props.modelValue;
});
const emit = defineEmits(['update:modelValue']);
const rules = {
    param: [{ asyncValidator: (_, arr) => repeatValidator('SPARK配置-配置参数', arr) }],
};
watch(param, () => {
    emit('update:modelValue', param.value);
}, { deep: true });
const form = ref();
function valid() {
    return form.value.validate();
}
defineExpose({ valid });
</script>
<style lang="less">
.item{
    align-items:baseline
}
</style>
