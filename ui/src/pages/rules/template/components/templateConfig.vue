<template>
    <div class="rule-detail-form"
         :class="{ edit: mode !== 'view' }">
        <FForm
            ref="templateConfigForm"
            :labelWidth="96"
            :labelPosition="mode !== 'view' ? 'right' : 'left'"
            :model="templateData"
            :rules="mode !== 'view' ? templateRules : []"
        >
            <!-- 数据源类型 -->
            <FFormItem :label="$t('ruleTemplatelist.dataSourceType')" prop="datasource_type">
                <FSelect
                    v-model="templateData.datasource_type"
                    class="form-edit-input"
                    :options="datasourceTypeList"
                    multiple
                ></FSelect>
                <span
                    class="form-preview-label"
                >{{templateData.datasource_type.map(item => getLabelByValue(datasourceTypeList, item)).join('、') || '--'}}</span>
            </FFormItem>
            <!-- 校验级别 -->
            <FFormItem :label="$t('ruleTemplatelist.verificationLevel')" prop="verification_level">
                <FSelect
                    v-model="templateData.verification_level"
                    class="form-edit-input"
                    :options="verificationLevelList"
                ></FSelect>
                <span
                    class="form-preview-label"
                >{{getLabelByValue(verificationLevelList, templateData.verification_level) || '--'}}</span>
            </FFormItem>
            <!-- 校验类型 -->
            <FFormItem :label="$t('ruleTemplatelist.verificationType')" prop="verification_type">
                <FSelect
                    v-model="templateData.verification_type"
                    class="form-edit-input"
                    :options="verificationTypeList"
                ></FSelect>
                <span
                    class="form-preview-label"
                >{{getLabelByValue(verificationTypeList, templateData.verification_type) || '--'}}</span>
            </FFormItem>
            <!-- 是否保存异常数据 -->
            <FFormItem :label="$t('ruleTemplatelist.exceptionDatabase')" prop="save_mid_table">
                <FRadioGroup v-model="templateData.save_mid_table" class="form-edit-input" :cancelable="false">
                    <FRadio :value="true" :disabled="templateType === 4">是</FRadio>
                    <FRadio :value="false">否</FRadio>
                </FRadioGroup>
                <span class="form-preview-label">{{templateData.save_mid_table ? '是' : '否'}}</span>
            </FFormItem>
            <!-- 是否需要过滤字段 -->
            <FFormItem :label="$t('ruleTemplatelist.filterFields')" prop="filter_fields">
                <FRadioGroup v-model="templateData.filter_fields" class="form-edit-input" :cancelable="false">
                    <FRadio :value="true" :disabled="templateType === 4">是</FRadio>
                    <FRadio :value="false">否</FRadio>
                </FRadioGroup>
                <span class="form-preview-label">{{templateData.filter_fields ? '是' : '否'}}</span>
            </FFormItem>
            <!-- 校验值中文名 -->
            <FFormItem :label="$t('ruleTemplatelist.verificationCNName')" prop="verification_cn_name">
                <FInput
                    v-model="templateData.verification_cn_name"
                    class="form-edit-input"
                    :maxlength="16"
                    placeholder="请输入"
                />
                <span class="form-preview-label">{{templateData.verification_cn_name || '--'}}</span>
            </FFormItem>
            <!-- 校验值英文名 -->
            <FFormItem :label="$t('ruleTemplatelist.verificationENName')" prop="verification_en_name">
                <FInput
                    v-model="templateData.verification_en_name"
                    class="form-edit-input"
                    :maxlength="64"
                    placeholder="请输入"
                />
                <span class="form-preview-label">{{templateData.verification_en_name || '--'}}</span>
            </FFormItem>
            <!-- 校验值采样方式 -->
            <FFormItem :label="$t('ruleTemplatelist.verificationActionType')" prop="action_type">
                <FSelect
                    v-model="templateData.action_type"
                    class="form-edit-input"
                    :options="actionTypeList"
                ></FSelect>
                <span
                    class="form-preview-label"
                >{{getLabelByValue(actionTypeList, templateData.action_type) || '--'}}</span>
            </FFormItem>
            <!-- 采样方式选择为SQL -->
            <div v-if="templateData.action_type === 1">
                <!-- 采样SQL -->
                <FFormItem :label="$t('ruleTemplatelist.midTableAction')" prop="mid_table_action">
                    <FInput
                        v-model="templateData.mid_table_action"
                        class="form-edit-input"
                        type="textarea"
                        placeholder="请输入"
                    />
                    <span class="form-preview-label">{{templateData.mid_table_action || '--'}}</span>
                </FFormItem>
            </div>
            <!-- 采样方式选择为元数据接口 采样内容 -->
            <FFormItem v-if="templateData.action_type === 5" :label="$t('ruleTemplatelist.actionContent')" prop="sampling_content">
                <FSelect
                    v-model="templateData.sampling_content"
                    class="form-edit-input"
                    :options="actionContentList"
                ></FSelect>
                <span
                    class="form-preview-label"
                >{{templateData.sampling_content_name || '--'}}</span>
            </FFormItem>
            <!-- 统计函数 -->
            <FFormItem
                :label="$t('ruleTemplatelist.countFunction')"
                prop="count_function_name"
            >
                <FSelect
                    v-model="templateData.count_function_name"
                    class="form-edit-input"
                    :options="countFunctionList"
                    @change="handleCountFunctionChange"
                ></FSelect>
                <span class="form-preview-label">{{templateData.count_function_name || '--'}}</span>
            </FFormItem>
            <!-- 统计值 -->
            <FFormItem
                v-if="templateData.count_function_name !== 'count'"
                :label="$t('ruleTemplatelist.countFunctionAlias')"
                prop="count_function_alias"
            >
                <FInput
                    v-model="templateData.count_function_alias"
                    class="form-edit-input"
                    placeholder="请输入"
                />
                <span class="form-preview-label">{{templateData.count_function_alias || '--'}}</span>
            </FFormItem>
            <!-- 是否固化校验方式 -->
            <FFormItem :label="$t('ruleTemplatelist.whetherSetVerificationMethod')" prop="whether_solidification">
                <FRadioGroup v-model="templateData.whether_solidification" class="form-edit-input" :cancelable="false">
                    <FRadio :value="true">是</FRadio>
                    <FRadio :value="false">否</FRadio>
                </FRadioGroup>
                <span class="form-preview-label">{{templateData.whether_solidification ? '是' : '否'}}</span>
            </FFormItem>
            <!-- 校验方式 -->
            <FFormItem v-if="templateData.whether_solidification"
                       :label="$t('ruleTemplatelist.verificationMethod')"
                       prop="check_template">
                <FSelect
                    v-model="templateData.check_template"
                    class="form-edit-input"
                    :options="verificationMethodList"
                ></FSelect>
                <span
                    class="form-preview-label"
                >{{getLabelByValue(verificationMethodList, templateData.check_template) || '--'}}</span>
            </FFormItem>
        </FForm>
    </div>
</template>
<script setup>
import {
    useI18n,
} from '@fesjs/fes';
import { computed, ref, inject } from 'vue';
import useDivisions from '@/hooks/useDivisions';
import useDepartment from '@/hooks/useDepartment';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { getLabelByValue } from '@/common/utils';

const { t: $t } = useI18n();

// eslint-disable-next-line no-undef
const props = defineProps({
    mode: {
        type: String,
        default: 'add',
    },
    templateData: {
        type: Object,
        default: () => ({}),
        required: true,
    },
    templateType: {
        type: Number,
        default: 1,
    },
});

// 从 ../index.js inject数据
const datasourceTypeList = inject('datasourceTypeList', []);
const verificationLevelList = inject('verificationLevelList', []);
const verificationTypeList = inject('verificationTypeList', []);
const actionTypeList = props.templateType === 4 ? [{ value: 5, label: '元数据接口' }] : inject('actionTypeList', []);
const actionContentList = inject('actionContentList', []);
const countFunctionList = inject('countFunctionList', []);


// eslint-disable-next-line no-undef
const emit = defineEmits(['update:templateData']);
const currentTemplateData = computed({
    get: () => props.templateData,
    set: (value) => {
        emit('update:templateData', value);
    },
});
const {
    divisions: devDivisions,
    loadDivisions: loadDevDivisions,
    curSubDepartData: devCurSubDepartData,
} = usePermissionDivisions();
const {
    divisions: opsDivisions,
    loadDivisions: loadOpsDivisions,
    curSubDepartData: opsCurSubDepartData,
} = usePermissionDivisions();
const {
    divisions: visDivisions,
    loadDivisions: loadVisDivisions,
    curSubDepartData: visCurSubDepartData,
} = useDivisions(true);
const {
    selectDevDate,
    selectOpsDate,
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);

const templateRules = {
    datasource_type: [
        {
            required: true,
            type: 'array',
            trigger: ['blur', 'change'],
            message: '请选择数据源类型',
        },
    ],
    verification_level: [
        {
            required: true,
            type: 'number',
            trigger: ['blur', 'change'],
            message: '请选择校验级别',
        },
    ],
    verification_type: [
        {
            required: true,
            type: 'number',
            trigger: ['blur', 'change'],
            message: '请选择校验类型',
        },
    ],
    save_mid_table: [
        {
            required: true,
            type: 'bool',
            trigger: ['blur', 'change'],
            message: '请选择是否保存异常数据',
        },
    ],
    whether_solidification: [
        {
            required: true,
            type: 'bool',
            trigger: ['blur', 'change'],
            message: '请选择是否固定校验方式',
        },
    ],
    verification_cn_name: [
        {
            required: true,
            trigger: ['blur', 'change'],
            message: '请输入模板中文名',
        },
    ],
    verification_en_name: [
        {
            required: true,
            trigger: ['blur', 'change'],
            message: '请输入模板英文名',
        },
    ],
    filter_fields: [
        {
            required: true,
            type: 'bool',
            trigger: ['blur', 'change'],
            message: '请选择是否需要过滤字段',
        },
    ],
    action_type: [
        {
            required: true,
            type: 'number',
            trigger: ['blur', 'change'],
            message: '请选择采样方式',
        },
    ],
    sampling_content: [
        {
            required: true,
            type: 'string',
            trigger: ['blur', 'change'],
            message: '请选择采样内容',
        },
    ],
    mid_table_action: [
        {
            required: true,
            trigger: ['blur', 'change'],
            message: '请输入采样SQL',
        },
    ],
    count_function_name: [
        {
            required: true,
            type: 'string',
            trigger: ['blur', 'change'],
            message: '请选择统计函数',
        },
    ],
    count_function_alias: [
        {
            required: true,
            type: 'string',
            trigger: ['blur', 'change'],
            message: '请输入统计值',
        },
    ],
    check_template: [
        {
            required: true,
            type: 'number',
            trigger: ['blur', 'change'],
            message: '请选择校验方式',
        },
    ],
};
const handleCountFunctionChange = () => {
    currentTemplateData.value.count_function_alias = '';
};

const templateConfigForm = ref(null);
const valid = async () => {
    // console.log('case in valid')
    try {
        await templateConfigForm.value?.validate();
        return true;
    } catch (e) {
        console.log(e);
        return false;
    }
};
// 校验方式 options
const verificationMethodList = ref([
    {
        label: $t('common.monthlyFluctuation'),
        value: 1,
    },
    {
        label: $t('common.weeklyFluctuation'),
        value: 2,
    },
    {
        label: $t('common.daillyFluctuation'),
        value: 3,
    },
    {
        label: $t('common.fixedValue'),
        value: 4,
    },
    {
        label: $t('common.yearCircleCompare'),
        value: 5,
    },
    {
        label: $t('common.halfYearCircleCompare'),
        value: 6,
    },
    {
        label: $t('common.seasonCircleCompare'),
        value: 7,
    },
    {
        label: $t('common.monthCircleCompare'),
        value: 8,
    },
    {
        label: $t('common.weekCircleCompare'),
        value: 9,
    },
    {
        label: $t('common.dayCircleCompare'),
        value: 10,
    },
    {
        label: $t('common.hourCircleCompare'),
        value: 11,
    },
    {
        label: $t('common.monthSameCompare'),
        value: 12,
    },
]);
// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
<style lang='less' scoped>
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
