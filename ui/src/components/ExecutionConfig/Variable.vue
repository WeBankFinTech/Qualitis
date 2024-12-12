<template>
    <div class="engine-param">
        <div class="top">
            <div class="param-name">变量{{props.index + 1}}</div>
            <div class="del-btn" @click="del"><MinusCircleOutlined /><span class="del-text">删除</span></div>
        </div>
        <div class="param-form">
            <FForm ref="variableFormRef"
                   :model="variable"
                   :rules="variableFormRules"
                   labelPosition="right"
                   labelWidth="66px">
                <FFormItem label="变量类型" prop="var_type">
                    <FSelect
                        v-model="variable.var_type"
                        :options="props.variableTypeList"
                        class="form-edit-input"
                        filterable
                        clearable
                        @change="onTypeChange"
                    ></FSelect>
                </FFormItem>
                <template v-if="isCustom">
                    <FFormItem label="变量名称" prop="var_name">
                        <FInput
                            v-model="variable.var_name"
                            class="form-edit-input"
                            placeholder="请输入自定义变量名称"
                            clearable
                        />
                    </FFormItem>
                    <FFormItem label="变量值" prop="var_value">
                        <FInput
                            v-model="variable.var_value"
                            class="form-edit-input"
                            placeholder="请输入变量值"
                            clearable
                        />
                    </FFormItem>
                </template>
                <template v-else>
                    <FFormItem label="变量名称" prop="var_name">
                        <FSelect
                            v-model="variable.var_name"
                            :options="variableNameList"
                            class="form-edit-input"
                            filterable
                            clearable
                            @change="onNameChange"
                        ></FSelect>
                    </FFormItem>
                    <FFormItem label="变量值" prop="var_value">
                        <FInput
                            v-model="variable.var_value"
                            class="form-edit-input"
                            placeholder="请输入变量值"
                            clearable
                        />
                    </FFormItem>
                </template>
            </FForm>
        </div>
    </div>
</template>
<script setup>
import {
    defineProps, computed, ref, defineEmits, defineExpose, watch,
} from 'vue';
import {
    useI18n, request,
} from '@fesjs/fes';
import { MinusCircleOutlined } from '@fesjs/fes-design/icon';

const { t: $t } = useI18n();

const props = defineProps({
    index: {
        type: Number,
        required: true,
        default: 0,
    },
    variable: {
        var_type: String,
        var_name: String,
        var_value: String,
    },
    variableTypeList: {
        type: Array,
        required: true,
        default: () => [],
    },
    variables: {
        type: Array,
        required: true,
        default: () => [],
    },
});

const emit = defineEmits(['update:variable', 'removeVariable']);
const variable = computed({
    get() {
        return props.variable;
    },
    set: (val) => {
        emit('update:variable', val);
    },
});
const del = () => {
    emit('removeVariable');
};
const variableNameList = ref([]);
const variableFormRef = ref(null);
const variableFormRules = {
    var_type: {
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
        type: 'number',
    },
    var_name: {
        required: true,
        trigger: ['blur'],
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (!value) {
                reject($t('common.notEmpty'));
            }
            if (props.variables.findIndex(item => item === value) !== props.index) {
                reject('该参数已存在');
            }

            resolve();
        }),
    },
    var_value: {
        required: true,
        trigger: ['blur'],
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (!value) {
                reject($t('common.notEmpty'));
            }
            if (props.variables.findIndex(item => item === variable.value.var_name) !== props.index) {
                reject('该参数已存在');
            }

            resolve();
        }),
    },
};
function valid() {
    return variableFormRef.value.validate();
}
watch(() => props.variables, (newVal, oldVal) => {
    if (newVal.length === oldVal.length && variable.value.var_name !== '' && variable.value.var_value !== '') {
        valid();
    }
}, { deep: true });
const isCustom = ref(false);
const onTypeChange = async (val) => {
    try {
        variable.value.var_name = '';
        variable.value.var_value = '';
        if (!val) {
            variableNameList.value = [];
            return;
        }
        if (val === 1) {
            isCustom.value = false;
            const res = await request('/api/v1/projector/execution_parameters/execution/variable/all', {});
            variableNameList.value = res.map(item => ({
                value: item,
                label: item,
            }));
        } else {
            variableNameList.value = [];
            isCustom.value = true;
        }
    } catch (err) {
        console.warn(err);
    }
};
const onNameChange = () => {
    variable.value.var_value = '';
};
defineExpose({ valid });
</script>

<style scoped lang="less">
.engine-param {
    width: 100%;
    background-color: #fff;
    padding: 24px;
    margin-bottom: 16px;
    .top {
        width: 100%;
        display: flex;
        justify-content: space-between;
        margin-bottom: 16px;
        .param-name {
            font-family: PingFangSC-Medium;
            font-size: 14px;
            color: #0F1222;
            line-height: 22px;
            font-weight: 500;
        }
    }
}
</style>
