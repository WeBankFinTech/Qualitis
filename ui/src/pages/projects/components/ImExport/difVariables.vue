<template>
    <div v-for="(difData, index) in difList" :key="index" class="dif-variable">
        <FForm
            :ref="el => { if (el) difListRef[index] = el; }"
            label-width="120px"
            :model="difList[index]"
            :labelPosition="`right`"
            :labelWidth="84"
            :rules="difRuleValidate"
            layout="inline"

        >
            <!-- 变量类型-->
            <FFormItem prop="variable_type" :labelWidth="90">
                <FSelect
                    v-model="difList[index].variable_type"
                    filterable
                    :placeholder="'变量类型'"
                    :options="variableTypeList"
                    :class="parentType === 'import' ? 'var-type-import' : 'var-type-export'"
                    @change="clearVariable(difList[index], 'variable_type')"
                >
                </FSelect>
            </FFormItem>
            <FFormItem prop="name" :class="parentType === 'import' ? 'var-name-form-item' : ''">
                <FInput v-if="difList[index].variable_type === 2" v-model="difList[index].name" :placeholder="'变量名'"
                        :class="parentType === 'import' ? 'var-name-import' : 'var-name-export'" />
                <FSelect
                    v-else
                    v-model="difList[index].name"
                    filterable
                    :placeholder="'变量名'"
                    :options="variableNameList"
                    labelField="name"
                    valueField="name"
                    :class="parentType === 'import' ? 'var-name-import' : 'var-name-export'"
                    @change="changeVariableName(index)"
                >
                </FSelect>
            </FFormItem>
            <FFormItem v-if="parentType === 'import'" prop="value" :labelWidth="151" class="var-value-form-item">
                <FInput v-model="difList[index].value" :placeholder="`变量值`" class="var-value" />
            </FFormItem>
            <FFormItem props="icon" class="minus-icon-formItem">
                <MinusCircleOutlined :class="parentType === 'import' ? 'var-icon-import' : 'var-icon-export'" @click="deleteVariableConfig(difList[index])" />
            </FFormItem>
        </FForm>
    </div>
    <FButton
        type="link"
        class="init-button"
        @click="addVariableConfig"
    >
        <template #icon>
            <PlusCircleOutlined />
        </template>添加
    </FButton>
</template>
<script setup>
import {
    computed, ref, inject, defineProps, defineExpose, defineEmits, onMounted, watch, provide,
} from 'vue';
import { useI18n, request } from '@fesjs/fes';
import useExecutionParamsData from '@/components/rules/hook/useExecutionParamsData';
import { PlusCircleOutlined, MinusCircleOutlined } from '@fesjs/fes-design/es/icon';
import { useDataList } from '@/pages/projects/components/ImExport/hooks/useDataList';
import { cloneDeep } from 'lodash-es';

const {
    variableTypeList,
    variableNameList,
    loadVariableNameList,
} = useDataList();

const { t: $t } = useI18n();
const difList = ref([]);
const difListRef = ref([]);
const props = defineProps({
    parentType: {
        type: String,
        required: true,
    },
});
const getInitVariableConfig = () => {
    const item = {
        timestamp: Date.now(),
        variable_type: 1,
        name: '',
        value: '',
    };
    return item;
};
const deleteVariableConfig = (data) => {
    const index = difList.value.findIndex(item => item.timestamp === data.timestamp);
    difList.value.splice(index, 1);
};
const addVariableConfig = () => {
    difList.value.push(getInitVariableConfig());
};
const validateVariableNameRepeat = (rule, value) => {
    let count = 0;
    difList.value.forEach((item) => {
        if (item.name === value) {
            count++;
        }
    });
    if (count > 1) {
        return false;
    }
    return true;
};
const difRuleValidate = ref({
    variable_type: [
        { type: 'number', required: true, message: $t('common.notEmpty') },
    ],
    name: [
        { trigger: ['change', 'blur'], required: true, message: $t('common.notEmpty') },
        {
            validator: validateVariableNameRepeat,
            message: '变量名称不可重复设置',
        },
    ],
    value: [
        { required: true, message: $t('common.notEmpty') },
    ],
});
const clearVariable = (data, type) => {
    if (type === 'variable_type') {
        data.name = '';
        data.value = '';
    }
};
const valid = async () => {
    try {
        const formValidArray = difListRef.value.map(item => item.validate());
        const result = await Promise.all(formValidArray);
        console.log('差异化变量表单验证成功: ', result);
        return true;
    } catch (error) {
        console.log('差异化变量表单验证失败: ', error);
        return false;
    }
};
const getDifData = () => {
    const returnDifList = cloneDeep(difList);
    returnDifList.value.forEach((item) => {
        delete item.timestamp;
        delete item.variable_type;
    });
    return returnDifList.value;
};
const clearData = () => {
    difList.value = [];
};
const changeVariableName = (index) => {
    const curData = difList.value[index];
    variableNameList.value.forEach((e) => {
        if (e.name === curData.name) {
            curData.value = e.value;
        }
    });
    difListRef.value[index].clearValidate();
};
onMounted(() => {
    loadVariableNameList();
});
defineExpose({ valid, getDifData, clearData });
</script>
<style lang="less" scoped>
.var-type-import {
    width: 90px;
    z-index: 10;
}
.var-name-form-item {
    .var-name-import {
        width: 151px;
        margin-left: -12px;
        z-index: 9;
    }
    :deep(.fes-form-item-error) {
            margin-left: -12px
    }
}
.var-value-form-item {
    .var-value {
        width: 151px;
        margin-left: 38px;
        z-index: 8;
    }
    :deep(.fes-form-item-error) {
        margin-left: 38px
    }
}
.var-type-export {
    width: 100px;
    z-index: 10;

}
.var-name-export {
    width: 290px;
    margin-left: 2px;
    z-index: 9;
}
.init-button {
    margin-left: -18px;
}
:deep(.minus-icon-formItem) {
    .fes-form-item-content{
        padding-top: 6px;
        .var-icon-import {
            margin-left: 85px;
        }
        .var-icon-export {
            margin-left: 194px;
        }
    }
}
.dif-variable {
    &:last-of-type {
        margin-bottom: -16px;
    }
}

</style>
