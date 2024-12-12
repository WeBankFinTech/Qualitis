<template>
    <FForm ref="stdRef" labelWidth="94px" labelPosition="right" :model="stdForm" :rules="stdRules">
        <FFormItem :label="`标准值来源`" prop="source_value">
            <FSelect v-model="stdForm.source_value" :options="stdSourceList" filterable clearable
                     valueField="code" labelField="message" :placeholder="$t('common.pleaseEnter')"
                     @change="resetData('sourceValue')"
            />
        </FFormItem>
        <FFormItem v-if="stdForm.source_value === 1" :label="`自定义来源`" prop="source">
            <FInput v-model="stdForm.source" type="textarea" :placeholder="$t('common.pleaseEnter')" />
        </FFormItem>
        <template v-if="stdForm.source_value === 2">
            <FFormItem :label="`数据标准类别`" prop="std_sub_name">
                <FSelect
                    v-model="stdForm.std_sub_name"
                    filterable
                    clearable
                    :placeholder="$t('common.pleaseEnter')"
                    :options="stdTypeList"
                    labelField="stdSubName"
                    valueField="stdSubName"
                    remote
                    @change="resetData('type')"
                    @focus="loadStdType('')"
                    @search="loadStdType" />
            </FFormItem>
            <FFormItem :label="`数据标准大类`" prop="std_big_category_name">
                <FSelect
                    v-model="stdForm.std_big_category_name"
                    filterable
                    clearable
                    :placeholder="$t('common.pleaseEnter')"
                    :options="stdBigList"
                    labelField="stdBigCategoryName"
                    valueField="stdBigCategoryName"
                    remote
                    @change="resetData('big')"
                    @focus="handleStdBig"
                    @search="loadStdBig($event, stdForm.std_sub_name)" />
            </FFormItem>
            <FFormItem :label="`数据标准小类`" prop="small_category_name">
                <FSelect
                    v-model="stdForm.small_category_name"
                    filterable
                    clearable
                    :placeholder="$t('common.pleaseEnter')"
                    :options="stdSmallList"
                    labelField="smallCategoryName"
                    valueField="smallCategoryName"
                    remote
                    @change="changeSmallCategory"
                    @focus="handleStdSmall"
                    @search="loadStdSmall($event, stdForm.std_sub_name, stdForm.std_big_category_name)" />
            </FFormItem>
            <FFormItem :label="`数据标准名称`" prop="std_cn_name">
                <FSelect
                    v-model="stdForm.std_cn_name"
                    filterable
                    clearable
                    :placeholder="$t('common.pleaseEnter')"
                    :options="stdList"
                    labelField="stdCnName"
                    valueField="stdCnName"
                    remote
                    @focus="handleDataStd('')"
                    @search="handleDataStd"
                    @change="handleStdCode" />
            </FFormItem>
            <FFormItem :label="`标准代码`" prop="code_name">
                <FInput v-model="stdForm.code_name" :disabled="true" :placeholder="`标准代码名称`" />
            </FFormItem>
        </template>
    </FForm>
</template>
<script setup>
import {
    ref, defineProps, defineEmits, defineExpose, computed, onMounted,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { useDataList } from '../hooks/useDataList';
import { utils } from '../hooks/utils';

const { t: $t } = useI18n();
const {
    findValue,
} = utils();
const {
    stdSourceList,
    stdTypeList,
    stdBigList,
    stdSmallList,
    stdList,
    stdCodeObj,
    loadStdSource,
    loadStdType,
    loadStdBig,
    loadStdSmall,
    loadDataStd,
    loadStdCode,
} = useDataList();
const stdRef = ref(null);
const props = defineProps({
    form: {
        type: Object,
        required: true,
        default: {},
    },
    mode: {
        type: String,
        required: true,
        default: 'add',
    },
});
const emit = defineEmits(['update:form', 'updateStdContent']);
const stdForm = computed({
    get: () => props.form,
    set: (value) => {
        emit('update:form', value);
    },
});
const stdRules = {
    source_value: [
        {
            required: true, type: 'number', message: $t('common.notEmpty'), trigger: ['change', 'blur'],
        },
    ],
    source: [
        {
            required: true, message: $t('common.notEmpty'), trigger: ['change', 'blur'],
        },
    ],
    std_sub_name: [
        {
            required: true, message: $t('common.notEmpty'), trigger: ['change', 'blur'],
        },
    ],
    std_big_category_name: [
        {
            required: true, message: $t('common.notEmpty'), trigger: ['change', 'blur'],
        },
    ],
    small_category_name: [
        {
            required: true, message: $t('common.notEmpty'), trigger: ['change', 'blur'],
        },
    ],
    std_cn_name: [
        {
            required: true, message: $t('common.notEmpty'), trigger: ['change', 'blur'],
        },
    ],
    code_name: [
        {
            required: true, message: $t('common.notEmpty'), trigger: ['change', 'blur'],
        },
    ],
};
const valid = async () => {
    try {
        const result = await stdRef.value.validate();
        return true;
    } catch (e) {
        console.warn(e);
        return false;
    }
};

const resetData = (e) => {
    switch (e) {
        case 'sourceValue':
            if (stdForm.value.source_value === 2) {
                stdForm.value.content = '';
            }
            stdForm.value.source = '';
            stdForm.value.std_sub_name = '';
            stdForm.value.std_big_category_name = '';
            stdForm.value.small_category_name = '';
            stdForm.value.std_cn_name = '';
            stdForm.value.code_name = '';
            break;
        case 'type':
            stdForm.value.std_big_category_name = '';
            stdForm.value.small_category_name = '';
            stdForm.value.std_cn_name = '';
            stdForm.value.code_name = '';
            break;
        case 'big':
            stdForm.value.small_category_name = '';
            stdForm.value.std_cn_name = '';
            stdForm.value.code_name = '';
            break;
        case 'small':
            stdForm.value.std_cn_name = '';
            stdForm.value.code_name = '';
            break;
        case 'std':
            stdForm.value.code_name = '';
            break;
        default:
            break;
    }
};
const handleStdBig = () => {
    if (stdForm.value.std_sub_name) {
        loadStdBig('', stdForm.value.std_sub_name);
    } else {
        FMessage.warn('请先选择数据标准类别');
    }
};
const handleStdSmall = () => {
    if (stdForm.value.std_big_category_name) {
        loadStdSmall('', stdForm.value.std_sub_name, stdForm.value.std_big_category_name);
    } else {
        FMessage.warn('请先选择数据标准大类');
    }
};
const handleDataStd = async (key) => {
    console.log('handleDataStd', key);
    if (stdForm.value.small_category_name) {
        if (!stdForm.value.std_small_category_urn) {
            stdForm.value.std_small_category_urn = findValue(stdSmallList.value, stdForm.value.small_category_name, 'smallCategoryName', 'smallCategoryUrn');
        } else {
            loadDataStd(key, stdForm.value.std_small_category_urn);
        }
    } else {
        FMessage.warn('请先选择数据标准小类');
    }
};
const handleStdCode = async () => {
    resetData('std');
    const stdUrn = findValue(stdList.value, stdForm.value.std_cn_name, 'stdCnName', 'stdUrn');
    const result = await loadStdCode(stdUrn);
    if (result.length > 0) {
        stdForm.value.code = result[0]?.code;
        stdForm.value.code_name = result[0]?.codeName;
        stdRef.value.clearValidate();
        emit('updateStdContent', stdForm.value.code);
    } else {
        FMessage.error('该数据标准暂无对应标准代码，请确认后重新选择。');
        stdForm.value.code = '';
        stdForm.value.code_name = '';
        emit('updateStdContent', '');
    }
};

const completionStd = () => {
    if (stdForm.value.small_category_name) {
        stdForm.value.small_category_code = findValue(stdSmallList.value, stdForm.value.small_category_name, 'smallCategoryName', 'smallCategoryCode');
        stdForm.value.std_small_category_urn = findValue(stdSmallList.value, stdForm.value.small_category_name, 'smallCategoryName', 'smallCategoryUrn');
    } if (stdForm.value.std_big_category_name) {
        stdForm.value.std_big_category_code = findValue(stdBigList.value, stdForm.value.std_big_category_name, 'stdBigCategoryName', 'stdBigCategoryCode');
    } if (stdForm.value.std_sub_name) {
        stdForm.value.std_sub_code = findValue(stdTypeList.value, stdForm.value.std_sub_name, 'stdSubName', 'stdSubCode');
    }
};

const changeSmallCategory = () => {
    stdForm.value.std_small_category_urn = findValue(stdSmallList.value, stdForm.value.small_category_name, 'smallCategoryName', 'smallCategoryUrn');
    resetData('small');
};
// eslint-disable-next-line no-undef
defineExpose({ valid, completionStd });

onMounted(() => {
    loadStdSource();
});

</script>
