<template>
    <div class="engine-param">
        <div class="top">
            <div class="param-name">引擎参数{{props.index + 1}}</div>
            <div class="del-btn" @click="del"><MinusCircleOutlined /><span class="del-text">删除</span></div>
        </div>
        <div class="param-form">
            <FForm ref="paramFormRef"
                   :model="param"
                   :rules="paramFormRules"
                   labelPosition="right"
                   labelWidth="66px">
                <FFormItem label="参数类型" prop="param_type">
                    <FSelect
                        v-model="param.param_type"
                        :options="props.paramTypeList"
                        class="form-edit-input"
                        filterable
                        clearable
                        @change="onTypeChange"
                    ></FSelect>
                </FFormItem>
                <template v-if="isCustom">
                    <FFormItem label="参数名称" prop="param_name">
                        <FInput
                            v-model="param.param_name"
                            class="form-edit-input"
                            placeholder="请输入自定义参数名称"
                            clearable
                        />
                    </FFormItem>
                    <FFormItem label="参数值" prop="param_value">
                        <FInput
                            v-model="param.param_value"
                            class="form-edit-input"
                            placeholder="请输入参数值"
                            clearable
                        />
                    </FFormItem>
                </template>
                <template v-else>
                    <FFormItem label="参数名称" prop="param_name">
                        <FSelect
                            v-model="param.param_name"
                            :options="paramNameList"
                            class="form-edit-input"
                            filterable
                            clearable
                            @change="onNameChange"
                        ></FSelect>
                    </FFormItem>
                    <FFormItem label="参数值" prop="param_value">
                        <FInput
                            v-model="param.param_value"
                            class="form-edit-input"
                            clearable
                            filterable
                            placeholder="请输入参数值"
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
    param: {
        param_type: String,
        param_name: String,
        param_value: String,
    },
    paramTypeList: {
        type: Array,
        required: true,
        default: () => [],
    },
    params: {
        type: Array,
        required: true,
        default: () => [],
    },
});
const emit = defineEmits(['update:param', 'removeEngineParam']);
const param = computed({
    get() {
        return props.param;
    },
    set: (val) => {
        emit('update:param', val);
    },
});
const paramFormRef = ref(null);
const paramNameList = ref([]);
const isCustom = ref(false);
const onTypeChange = async (val) => {
    try {
        param.value.param_name = '';
        param.value.param_value = '';
        if (!val) {
            paramNameList.value = [];
            return;
        }
        if (val !== 4) {
            isCustom.value = false;
            const res = await request(`/api/v1/projector/execution_parameters/engine/${val}`, {}, 'get');
            paramNameList.value = res;
        } else {
            paramNameList.value = [];
            isCustom.value = true;
        }
    } catch (err) {
        console.warn(err);
    }
};
// 执行参数名称变化时有对应的默认值
const onNameChange = (val) => {
    param.value.param_value = paramNameList.value.find(item => item.value === val)?.initial || '';
};
const paramFormRules = {
    param_type: {
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
        type: 'number',
    },
    param_name: {
        required: true,
        trigger: ['blur'],
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (!value) {
                reject($t('common.notEmpty'));
            }
            if (props.params.findIndex(item => item === value) !== props.index) {
                reject('该参数已存在');
            }

            resolve();
        }),
    },
    param_value: {
        required: true,
        trigger: ['blur'],
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (!value) {
                reject($t('common.notEmpty'));
            }
            if (props.params.findIndex(item => item === param.value.param_name) !== props.index) {
                reject('该参数已存在');
            }

            resolve();
        }),
    },
};
const del = () => {
    emit('removeEngineParam');
};
function valid() {
    return paramFormRef.value.validate();
}
watch(() => props.params, (newVal, oldVal) => {
    if (newVal.length === oldVal.length && param.value.param_name !== '' && param.value.param_value !== '') {
        valid();
    }
}, { deep: true });
defineExpose({ valid });
</script>

<style scoped lang="less">
.engine-param {
    width: 100%;
    background-color: #fff;
    padding: 24px;
    margin-bottom: 16px;
    border-radius: 4px;
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
