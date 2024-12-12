<template>
    <FForm ref="form" class="form" labelPosition="right" :model="{ clusterName,param,engineReuse }" :rules="rules" :labelWidth="71">
        <FFormItem prop="clusterName" :label="`${$t('common.switchCluster')}`">
            <FSelect v-model="clusterName" clearable>
                <FOption
                    v-for="item of clusterList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </FSelect>
        </FFormItem>
        <FFormItem class="item" prop="param" :label="`${$t('common.executeParams')}`">
            <ParamSetting v-model="param" :nameList="parameterNameList" />
        </FFormItem>
        <FFormItem :label="$t('common.engineReuse')" prop="engineReuse">
            <FSwitch v-model="engineReuse"></FSwitch>
        </FFormItem>
    </FForm>
</template>
<script setup>
import {
    defineProps, onMounted, ref, watchEffect, defineEmits, watch, defineExpose,
} from 'vue';
import { useI18n, request as FRequest } from '@fesjs/fes';
// import { request } from '@/app.js';
import ParamSetting from './SelectParam';
import { repeatValidator } from './utils';

const { t: $t } = useI18n();
const props = defineProps({ name: String, param: Array, engineReuse: Boolean });
const clusterName = ref('');
const param = ref([]);
const engineReuse = ref(true);
watchEffect(() => {
    console.log(props.param);
    clusterName.value = props.name;
    param.value = props.param;
    engineReuse.value = props.engineReuse;
});
const emit = defineEmits(['update:name', 'update:param']);
watch(clusterName, () => {
    emit('update:name', clusterName.value);
});
watch(param, () => {
    emit('update:param', param.value);
}, { deep: true });
watch(engineReuse, () => {
    emit('update:engineReuse', engineReuse.value);
}, { deep: true });
const clusterList = ref([]);
const form = ref();
const parameterNameList = ref([
    {
        label: 'YARN队列名',
        value: 'wds.linkis.rm.yarnqueue',
        default: '',
    },
    {
        label: 'YARN队列实例最大个数',
        value: 'wds.linkis.rm.yarnqueue.instance.max',
        default: '30',
    },
    {
        label: '队列CPU使用上限',
        value: 'wds.linkis.rm.yarnqueue.cores.max',
        default: '150',
    },
    {
        label: '队列内存使用上限',
        value: 'wds.linkis.rm.yarnqueue.memory.max',
        default: '300G',
    },
    {
        label: 'Driver内存使用上限',
        value: 'wds.linkis.rm.client.memory.max',
        default: '20G',
    },
    {
        label: 'Driver核心个数上限',
        value: 'wds.linkis.rm.client.core.max',
        default: '10',
    },
    {
        label: '引擎最大并发数',
        value: 'wds.linkis.rm.instance',
        default: '10',
    },
    {
        label: 'woker并发数',
        value: 'spark.executor.instances',
        default: '2',
    },
    {
        label: 'worker内存大小',
        value: 'spark.executor.memory',
        default: '3g',
    },
]);
onMounted(async () => {
    try {
        const { optional_clusters } = await FRequest(
            'api/v1/projector/meta_data/cluster',
            {},
            { method: 'POST' },
        );
        if (Array.isArray(optional_clusters)) {
            clusterList.value = optional_clusters.map(item => ({
                label: item,
                value: item,
            }));
        }
    } catch (error) {
        console.error(error);
    }
});
const rules = {
    param: [{ asyncValidator: (_, arr) => repeatValidator('动态引擎配置-执行参数', arr) }],
};
function valid() {
    return form.value.validate();
}
defineExpose({ valid });
</script>
<style lang="less" scoped>
.item{
    align-items:baseline
}
.form{
    padding:16px;
}
</style>
