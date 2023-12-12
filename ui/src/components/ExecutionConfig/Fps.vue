<template>
    <FForm labelPosition="right" :labelWidth="71">
        <FFormItem label="FPS ID">
            <FInput v-model="id" :placeholder="$t('common.pleaseEnter')" clearable></FInput>
        </FFormItem>
        <FFormItem label="FPS hash">
            <FInput v-model="hash" :placeholder="$t('common.pleaseEnter')" clearable></FInput>
        </FFormItem>
    </FForm>
</template>
<script setup>
import {
    defineProps, watchEffect, ref, defineEmits, watch,
} from 'vue';
import { useI18n } from '@fesjs/fes';

const { t: $t } = useI18n();
const props = defineProps({ modelValue: { id: String, hash: String } });
const id = ref('');
const hash = ref('');
watchEffect(() => {
    id.value = props.modelValue.id;
    hash.value = props.modelValue.hash;
});
const emit = defineEmits(['update:modelValue']);
watch([id, hash], () => {
    emit('update:modelValue', { id: id.value, hash: hash.value });
});
</script>
<style scoped>
</style>
