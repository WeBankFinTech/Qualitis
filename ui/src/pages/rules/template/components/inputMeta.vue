<template>
    <div class="wrap rule-detail-form"
         :class="{ edit: mode !== 'view', view: mode === 'view' }">
        <div v-if="mode !== 'view'" class="header">
            占位符{{index}}
            <span v-if="removable" class="delete-btn" @click="deleteInputMeta">
                <MinusCircleOutlined style="margin-right: 4.67px;" />删除
            </span>
        </div>
        <div v-else class="header">
            {{inputMetaData.input_meta_name || '--'}}
        </div>
        <FForm
            ref="inputMetaForm"
            :labelWidth="96"
            :labelPosition="mode !== 'view' ? 'right' : 'left'"
            :model="inputMetaData"
            :rules="mode !== 'view' ? inputMetaRules : []"
        >
            <!-- 占位符名称 -->
            <FFormItem v-if="mode !== 'view'" :label="$t('ruleTemplatelist.replaceName')" prop="input_type">
                <FSelect
                    v-model="inputMetaData.input_type"
                    class="form-edit-input"
                    :options="placeholders"
                    valueField="input_type"
                    labelField="input_cn_name"
                    clearable
                    filterable
                    @change="handleInputTypeChange"
                ></FSelect>
            </FFormItem>
            <!-- 字段是否多选 (占位符类型：字段)-->
            <FFormItem
                v-if="inputMetaData.input_type === 4"
                :label="$t('ruleTemplatelist.fieldMultipleChoice')"
                prop="field_multiple_choice"
            >
                <FRadioGroup
                    v-model="inputMetaData.field_multiple_choice"
                    class="form-edit-input"
                    :cancelable="false"
                >
                    <FRadio :value="true">是</FRadio>
                    <FRadio :value="false">否</FRadio>
                </FRadioGroup>
                <span class="form-preview-label">{{inputMetaData.field_multiple_choice ? '是' : '否'}}</span>
            </FFormItem>
            <!-- 是否发现新值 (占位符类型：枚举值8、数值范围28、标准值39) -->
            <FFormItem
                v-if="[8,28,39].includes(inputMetaData.input_type)"
                :label="$t('ruleTemplatelist.whetherNewValue')"
                prop="whether_new_value"
            >
                <FRadioGroup
                    v-model="inputMetaData.whether_new_value"
                    class="form-edit-input"
                    :cancelable="false"
                >
                    <FRadio :value="true">是</FRadio>
                    <FRadio :value="false">否</FRadio>
                </FRadioGroup>
                <span class="form-preview-label">{{inputMetaData.whether_new_value ? '是' : '否'}}</span>
            </FFormItem>
            <!-- 表达式29-该类型大部分字段可编辑 -->
            <div v-show="expanded || inputMetaData.input_type === 29 || mode === 'view'">
                <!-- 占位符类型 -->
                <FFormItem :label="$t('ruleTemplatelist.replaceType')" prop="input_type_name">
                    <FInput
                        v-model="inputMetaData.input_type_name"
                        class="form-edit-input"
                        :maxlength="16"
                        :disabled="true"
                    />
                    <span class="form-preview-label">{{inputMetaData.input_type_name || '--'}}</span>
                </FFormItem>
                <!-- 替换内容 -->
                <FFormItem
                    :label="$t('ruleTemplatelist.replaceContent')"
                    prop="input_meta_placeholder"
                >
                    <FInput
                        v-model="inputMetaData.input_meta_placeholder"
                        class="form-edit-input"
                        :maxlength="16"
                        :disabled="inputMetaData.input_type !== 29"
                    />
                    <span class="form-preview-label">{{inputMetaData.input_meta_placeholder || '--'}}</span>
                </FFormItem>
                <!-- 中文名称 -->
                <FFormItem :label="$t('ruleTemplatelist.CNName')" prop="input_meta_cn_name">
                    <FInput
                        v-model="inputMetaData.input_meta_cn_name"
                        class="form-edit-input"
                        :maxlength="16"
                        :disabled="inputMetaData.input_type !== 29"
                    />
                    <span class="form-preview-label">{{inputMetaData.input_meta_cn_name || '--'}}</span>
                </FFormItem>
                <!-- 英文名称 -->
                <FFormItem :label="$t('ruleTemplatelist.ENName')" prop="input_meta_en_name">
                    <FInput
                        v-model="inputMetaData.input_meta_en_name"
                        class="form-edit-input"
                        :maxlength="16"
                        :disabled="inputMetaData.input_type !== 29"
                    />
                    <span class="form-preview-label">{{inputMetaData.input_meta_en_name || '--'}}</span>
                </FFormItem>
                <!-- 中文描述 -->
                <FFormItem
                    :label="$t('ruleTemplatelist.CNDescription')"
                    prop="input_meta_cn_description"
                >
                    <FInput
                        v-model="inputMetaData.input_meta_cn_description"
                        class="form-edit-input"
                        :maxlength="16"
                        :disabled="inputMetaData.input_type !== 29"
                    />
                    <span class="form-preview-label">{{inputMetaData.input_meta_cn_description || '--'}}</span>
                </FFormItem>
                <!-- 英文描述 -->
                <FFormItem
                    :label="$t('ruleTemplatelist.ENDescription')"
                    prop="input_meta_en_description"
                >
                    <FInput
                        v-model="inputMetaData.input_meta_en_description"
                        class="form-edit-input"
                        :maxlength="16"
                        :disabled="inputMetaData.input_type !== 29"
                    />
                    <span class="form-preview-label">{{inputMetaData.input_meta_en_description || '--'}}</span>
                </FFormItem>
            </div>
        </FForm>
        <div v-if="inputMetaData.input_type !== 29 && mode !== 'view'" class="more">
            <div @click="() => (expanded = !expanded)">
                <span>{{expanded ? '收起' : '展开'}}</span>
                <UpOutlined v-if="expanded" />
                <DownOutlined v-else />
            </div>
        </div>
    </div>
</template>
<script setup>
import {
    useI18n,
} from '@fesjs/fes';
import {
    computed, inject, ref, watch,
} from 'vue';
import useDivisions from '@/hooks/useDivisions';
import useDepartment from '@/hooks/useDepartment';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { getLabelByValue } from '@/common/utils';
import { MinusCircleOutlined, UpOutlined, DownOutlined } from '@fesjs/fes-design/es/icon';

const { t: $t } = useI18n();

// eslint-disable-next-line no-undef
const props = defineProps({
    mode: {
        type: String,
        default: 'add',
    },
    inputMetaData: {
        type: Object,
        default: () => ({}),
        required: true,
    },
    index: {
        type: Number,
        requierd: true,
    },
    removable: {
        type: Boolean,
        requierd: true,
        default: false,
    },
    placeholders: {
        type: Array,
        requierd: true,
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:inputMetaData', 'delete', 'update:placeholders']);
const currentInputMetaData = computed({
    get: () => props.inputMetaData,
    set: (value) => {
        emit('update:inputMetaData', value);
    },
});
const currentPlaceholders = computed({
    get: () => props.placeholders,
    set: (value) => {
        emit('update:placeholders', value);
    },
});
const deleteInputMeta = () => {
    emit('delete');
};

const expanded = ref(false);

const handleInputTypeChange = () => {
    // console.log(props.placeholders);
    const placeholder = props.placeholders.find(item => item.input_type === currentInputMetaData.value.input_type);
    currentInputMetaData.value.input_meta_name = placeholder.input_cn_name || '';
    currentInputMetaData.value.input_type_name = placeholder.input_cn_name || '';
    currentInputMetaData.value.input_meta_placeholder = placeholder.placeholder || '';
    currentInputMetaData.value.input_meta_cn_name = placeholder.cn_name || '';
    currentInputMetaData.value.input_meta_en_name = placeholder.en_name || '';
    currentInputMetaData.value.input_meta_cn_description = placeholder.cn_description || '';
    currentInputMetaData.value.input_meta_en_description = placeholder.en_description || '';
    currentInputMetaData.value.field_multiple_choice = placeholder.field_multiple_choice || false;
    currentInputMetaData.value.whether_standard_value = placeholder.whether_standard_value || false;
    currentInputMetaData.value.whether_new_value = placeholder.whether_new_value || false;
};
watch(() => currentInputMetaData.value.input_type, (cur, pre) => {
    currentPlaceholders.value.forEach((item) => {
        if (pre && item.input_type === pre) {
            item.disabled = false;
        }
        if (cur && item.input_type === cur) {
            item.disabled = true;
        }
    });
});

const inputMetaRules = {
    input_type: [
        {
            required: true,
            type: 'number',
            trigger: ['blur', 'change'],
            message: '请选择占位符名称',
        },
    ],
};

const inputMetaForm = ref(null);
const valid = async () => {
    try {
        await inputMetaForm.value?.validate();
        return true;
    } catch (e) {
        return false;
    }
};
// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
<style lang='less' scoped>
.wrap {
    padding: 16px;
    border: 1px solid #cfd0d3;
    border-radius: 4px;
    margin-bottom: 16px;
    margin-right: 8px;
}
.wrap.view{
    background-color: #f7f7f8;
    border: none;

    :deep(.fes-form-item){
        margin-bottom: 8px;
    }
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    color: #0f1222;
    font-weight: bold;
    .delete-btn {
        cursor: pointer;
        display: flex;
        align-items: center;
        color:#646670;
        font-weight: 400;
    }
}
.more {
    cursor: pointer;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    div {
        padding: 2px;
        display: flex;
        justify-content: center;
        align-items: center;
        & span {
            color: #5384ff;
            margin-left: 4px;
            font-size: 14px;
            line-height: 20px;
        }
    }
}
.rule-detail-form {
    :deep(.fes-input) {
        max-width: 100%;
    }
    :deep(.fes-select) {
        max-width: 100%;
    }
    :deep(.fes-textarea) {
        max-width: 100%;
    }
}
</style>
