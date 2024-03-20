<!-- 规则组，校验规则-校验条件输入及展示框 -->
<template>
    <FForm
        ref="ruleConditionFormRef"
        :rules="ruleConditionFormRules"
        :model="currentCondition.ruleCondition"
        layout="inline"
        :class="{ edit: ruleData.currentProject.editMode !== 'display' }"
    >
        <!-- 校验指标 当规则为自定义SQL规则时展示该下拉选择 -->
        <RuleMetricSelect
            v-if="groupType === 'sqlVerification'"
            ref="ruleMetricSelectRef"
            :defaultMetricId="currentCondition.ruleCondition.rule_metric_id"
            :inlineMetric="true"
            @getRuleMetricData="getRuleMetricData"
        ></RuleMetricSelect>
        <!-- <FFormItem
            v-if="groupType === 'sqlVerification'"
            class="item-min-width"
            label="校验指标"
            prop="rule_metric_id"
        >
            <FSelect
                v-model="currentCondition.ruleCondition.rule_metric_id"
                class="form-edit-input"
                :options="ruleStandardList"
                filterable
                @change="onRuleMetricChange"
            />
            <div
                class="form-preview-label"
            >
                {{getLabelByValue(ruleStandardList, currentCondition.ruleCondition.rule_metric_id)}}
            </div>
            <FTooltip placement="right-start">
                <ExclamationCircleOutlined class="tip hint" />
                <template #content>
                    <div style="width: 490px;">{{$t('common.metricTip')}}</div>
                </template>
            </FTooltip>
        </FFormItem>-->
        <!-- 校验值 -->
        <FFormItem
            v-if="!['newCustomRule', 'sqlVerification'].includes(groupType)"
            class="item-min-width"
            label="校验值"
            prop="output_meta_id"
        >
            <FSelect
                v-model="currentCondition.ruleCondition.output_meta_id"
                valueField="output_id"
                labelField="output_name"
                :options="checkFieldList"
                class="form-edit-input"
                @change="onOutputMetaChange($event)"
            ></FSelect>
            <FTooltip
                :content="getLabelByValue(checkFieldList, currentCondition.ruleCondition.output_meta_id, { labelFieldName: 'output_name', valueFieldName: 'output_id' })"
                placement="top-start"
            >
                <div
                    class="form-preview-label item-ellipsis"
                >
                    {{getLabelByValue(checkFieldList, currentCondition.ruleCondition.output_meta_id, { labelFieldName: 'output_name', valueFieldName: 'output_id' })}}
                </div>
            </FTooltip>
        </FFormItem>
        <!-- 校验方式 -->
        <FFormItem
            class="item-min-width"
            :label="$t('common.verificationMethod')"
            prop="check_template"
        >
            <FSelect
                v-model="currentCondition.ruleCondition.check_template"
                :options="tplList"
                class="form-edit-input"
                :disabled="solidification"
                @change="tplChange"
            ></FSelect>
            <FTooltip
                :content="getLabelByValue(tplList, currentCondition.ruleCondition.check_template)"
                placement="top-start"
            >
                <div
                    class="form-preview-label item-ellipsis"
                >
                    {{getLabelByValue(tplList, currentCondition.ruleCondition.check_template)}}
                </div>
            </FTooltip>
        </FFormItem>
        <!-- 比较方式 -->
        <FFormItem
            v-if="specialTplList.includes(currentCondition.ruleCondition.check_template)"
            class="item-min-width"
            :label="$t('label.comparisonMethod')"
            prop="compare_type"
        >
            <FSelect
                v-model="currentCondition.ruleCondition.compare_type"
                :options="compareList"
                class="form-edit-input"
            ></FSelect>
            <FTooltip
                :content="getLabelByValue(compareList, currentCondition.ruleCondition.compare_type)"
                placement="top-start"
            >
                <div
                    class="form-preview-label item-ellipsis"
                >
                    {{getLabelByValue(compareList, currentCondition.ruleCondition.compare_type)}}
                </div>
            </FTooltip>
        </FFormItem>
        <!-- 校验阈值 -->
        <FFormItem class="item-min-width" :label="$t('common.thresholdValue')" prop="threshold">
            <FInput
                v-model="currentCondition.ruleCondition.threshold"
                class="form-edit-input number-input"
                :class="isUnitShow() ? 'has-file-unit' : ''"
                placeholder="请输入"
            >
                <template #suffix>
                    <span v-if="isPercentShow()">%</span>
                </template>
                <template v-if="isUnitShow()" #append>
                    <!-- 文件校验 检验模板为 目录容量，且 校验模板为包含比较方式的校验模板时，有单位选项 -->
                    <FSelect
                        v-model="currentCondition.ruleCondition.file_output_unit"
                        :options="unitList"
                        class="form-edit-input"
                        style="width: 63px;"
                    ></FSelect>
                </template>
            </FInput>
            <div class="form-preview-label">
                {{currentCondition.ruleCondition.threshold}}
                <span v-if="isPercentShow()">%</span>
                <span
                    v-if="isUnitShow()"
                >{{getLabelByValue(unitList, currentCondition.ruleCondition.file_output_unit)}}</span>
            </div>
        </FFormItem>
        <slot></slot>
    </FForm>
</template>
<script setup>
import {
    ref, inject, computed,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { getLabelByValue, ruleTypes } from '@/common/utils';
import { MinusCircleOutlined, LinkOutlined } from '@fesjs/fes-design/icon';
import { useStore } from 'vuex';
import { useFormModel } from '@/components/rules/hook/useModel';
import { ExclamationCircleOutlined } from '@fesjs/fes-design/es/icon';
import RuleMetricSelect from '@/components/rules/forms/RuleMetricSelect';
import { COMMON_REG } from '@/assets/js/const';

const { t: $t } = useI18n();
const ruleType = inject('ruleType');
// 校验字段 options
const checkFieldList = inject('checkFieldList');
const store = useStore();
const ruleData = computed(() => store.state.rule);
const groupType = computed(() => ruleTypes.find(item => item.type === ruleType).value || '');

// eslint-disable-next-line no-undef
const props = defineProps({
    // 校验指标 options
    ruleStandardList: {
        type: Array,
        default: [],
    },
    // 校验条件
    ruleCondition: {
        type: Object,
        default: {},
    },
    // 校验模板是否固化校验条件
    solidification: {
        type: Boolean,
        default: false,
    },
});

// eslint-disable-next-line no-undef
const emit = defineEmits(['update:ruleCondition']);
const currentCondition = useFormModel(props, emit, ['ruleCondition']);

const specialTplList = [4, 5, 6, 7, 8, 9, 10, 11, 12];

const onlyPlusTplList = [1, 2, 3];
const ruleConditionFormRef = ref(null);
const ruleConditionFormRules = ref({
    output_meta_id: [{
        required: true,
        trigger: ['change', 'blur'],
        type: 'number',
        message: $t('common.notEmpty'),
    }],
    check_template: [{
        required: true,
        trigger: ['change', 'blur'],
        type: 'number',
        message: $t('common.notEmpty'),
    }],
    compare_type: [{
        required: true,
        trigger: ['change', 'blur'],
        type: 'number',
        message: $t('common.notEmpty'),
    }],
    threshold: [
        {
            required: true,
            trigger: 'input',
            validator: (rule, value) => new Promise((resolve, reject) => {
                // 这个字段可能多个类型，只能自定义
                if (value !== 0 && !value) {
                    reject($t('common.notEmpty'));
                }
                if (onlyPlusTplList.includes(currentCondition.ruleCondition.check_template)
                    ? COMMON_REG.NUMBER_CONTAIN_FLOAT_PLUS.test(value)
                    : COMMON_REG.NUMBER_CONTAIN_FLOAT.test(value)) {
                    resolve();
                } else {
                    reject('不合法的非数值输入');
                }
            }),
        },
    ],
    file_output_unit: [{
        required: true,
        trigger: ['change', 'blur'],
        type: 'number',
        message: $t('common.notEmpty'),
    }],
    rule_metric_id: [{
        required: true,
        trigger: ['change', 'blur'],
        type: 'number',
        message: $t('common.notEmpty'),
    }],
});
const ruleMetricSelectRef = ref(null);
const valid = async () => {
    try {
        // 当校验指标组件不展示的时候跳过校验指标组件的valid
        const result = ruleMetricSelectRef.value ? await ruleMetricSelectRef.value.valid() : true;
        if (result) {
            await ruleConditionFormRef.value?.validate();
            return true;
        }
        return false;
    } catch (e) {
        console.warn('rulecondition item valid error:', e);
        return false;
    }
};

function isUnitShow() {
    return ['documentVerification'].includes(groupType.value)
        && currentCondition.ruleCondition.output_meta_name === '目录容量'
        && specialTplList.includes(currentCondition.ruleCondition.check_template);
}

const isPercentShow = () => (currentCondition.ruleCondition.check_template !== 4) && !isUnitShow();

// 文件校验规则 校验字段、校验模板 改变时，清空 已选择的file_output_unit
function clearUnitValue() {
    if (!['documentVerification'].includes(groupType.value)) return;
    currentCondition.ruleCondition.file_output_unit = 4;
}

function onOutputMetaChange(value) {
    if (!Array.isArray(checkFieldList.value)) return;
    const target = checkFieldList.value.find(v => v.output_id === value);
    const ruleCondition = currentCondition.ruleCondition;
    if (target && ruleCondition) {
        ruleCondition.output_meta_name = target.output_name || '';
    }
    clearUnitValue();
}

const onRuleMetricChange = () => {
    const target = props.ruleStandardList.find(item => item.value === currentCondition.ruleCondition.rule_metric_id);
    currentCondition.ruleCondition.rule_metric_name = target?.label || '';
    currentCondition.ruleCondition.rule_metric_en_code = target?.en_code || '';
};

const getRuleMetricData = (ruleMetricData) => {
    currentCondition.ruleCondition.rule_metric_id = ruleMetricData.rule_metric_id;
    currentCondition.ruleCondition.rule_metric_name = ruleMetricData.rule_metric_name;
    currentCondition.ruleCondition.rule_metric_en_code = ruleMetricData.rule_metric_en_code;
};

// 校验方式 options
const tplList = ref([
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

// 比较方式 options
const compareList = ref([
    {
        value: 1,
        label: $t('common.equal'),
    },

    {
        value: 2,
        label: $t('common.greaterThan'),
    },

    {
        value: 3,
        label: $t('common.lessThan'),
    },
    {
        value: 4,
        label: $t('common.greatThanOrEqualTo'),
    },
    {
        value: 5,
        label: $t('common.lessThanOrEqualTo'),
    },
    {
        value: 6,
        label: $t('common.unequalTo'),
    },

]);

// 文件校验 options
const unitList = ref([
    { label: 'B', value: 5 },
    { label: 'KB', value: 4 },
    { label: 'MB', value: 3 },
    { label: 'GB', value: 2 },
    { label: 'TB', value: 1 },
]);

function tplChange() {
    currentCondition.ruleCondition.compare_type = '';
    currentCondition.ruleCondition.threshold = '';
    clearUnitValue();
}

// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
<style lang='less' scoped>
.item-min-width {
    width: 216px;
}
.number-input {
    :deep(.fes-input-number-actions) {
        display: none;
    }
}
.has-file-unit {
    :deep(.fes-input-inner) {
        width: 57px;
    }
}
</style>
