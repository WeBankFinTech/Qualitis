<template>
    <FForm
        id="rule-condition-form-item-metricid"
        ref="ruleConditionFormRef"
        :labelWidth="labelWidth"
        :labelPosition="editMode !== 'display' ? 'right' : 'left'"
        :model="formData"
        :class="{ 'inline-metric': inlineMetric }"
    >
        <!-- 自定义sql和表文件时为必选 -->
        <FFormItem
            v-if="(!['sqlVerification', 'executionParams'].includes(groupType)) || inlineMetric"
            :rules="[{ required: inlineMetric || ruleType === '4-1', type: 'any', message: $t('common.notEmpty'), trigger: 'change' }]"
            :label="$t('common.VerificationMetric')"
            :labelWidth="inlineMetric ? 'auto' : undefined"
            prop="ruleMetricId"
        >
            <FSelect
                v-model="formData.ruleMetricId"
                class="form-edit-input"
                clearable
                filterable
                :options="ruleStandardList"
                tag
                popperClass="rule-metric-popper"
                :getContainer="getContainer"
                @change="onRuleMetricChange"
            ></FSelect>
            <div
                class="form-preview-label"
            >
                {{getLabelByValue(ruleStandardList, formData.ruleMetricId) || '--'}}
            </div>
            <FTooltip placement="right-start">
                <ExclamationCircleOutlined class="tip hint" />
                <template #content>
                    <div style="width: 490px;">{{$t('common.metricTip')}}</div>
                </template>
            </FTooltip>
            <div v-if="whetherVerify" class="verifyTip">指标未配置，上报开关无效</div>
        </FFormItem>
    </FForm>
</template>

<script setup>
import {
    ref, reactive, computed, provide, nextTick, watch, inject, unref, onMounted, defineProps, defineEmits,
} from 'vue';
import { useI18n, request as FRequest } from '@fesjs/fes';
import { useStore } from 'vuex';
import { MAX_PAGE_SIZE } from '@/assets/js/const';
import { getLabelByValue, ruleTypes } from '@/common/utils';
import { ExclamationCircleOutlined } from '@fesjs/fes-design/es/icon';

const { t: $t } = useI18n();
const store = useStore();
const configuredRuleMetric = inject('configuredRuleMetric');
const getContainer = () => document.getElementById('rule-condition-form-item-metricid');

const props = defineProps({
    // 页面的类型 普通规则组：commonList，表规则组：groupList, 基于规则模版创建listByTemplate
    listType: {
        type: String,
        default: 'commonList',
    },
    defaultMetricId: {
        type: Number || String,
        default: '',
    },
    // 是否需要上报指标
    needUploadMetric: {
        type: Boolean,
        default: false,
    },
    // 是否是放到校验条件中的校验指标，现在只有自定义SQL校验规则会用到，即多指标
    inlineMetric: {
        type: Boolean,
        default: false,
    },
});

const listType = computed(() => props.listType);
const ruleData = computed(() => store.state.rule);
const formData = reactive({
    ruleMetricId: null,
});
/**
 * 规则类型的处理
 * 1. 基于规则模版的ruletype是响应式，所以provide一个对象{ruleType: xx},否则监听不到ruleType的变更
 * 2. 其他情况provide一个值
 * */
const currentRuleTypeObject = inject('ruleType');
let ruleType = currentRuleTypeObject;
watch(listType.value, () => {
    if (listType.value === 'listByTemplate') ruleType = currentRuleTypeObject.value.ruleType;
}, { immediate: true });
const groupType = computed(() => ruleTypes.find(item => item.type === ruleType).value || '');

const defaultMetricId = computed(() => props.defaultMetricId);

watch(defaultMetricId, () => {
    if (defaultMetricId.value) {
        formData.ruleMetricId = defaultMetricId.value;
    }
}, { immediate: true });
// label宽度
const labelWidth = computed(() => {
    if (listType.value === 'groupList') {
        return 70;
    }
    return 96;
});

// 判断页面为普通规则组/表规则组，切换不同 editMode 变量
const editMode = computed(() => {
    if (listType.value === 'groupList') {
        return ruleData.value.currentProject.groupRuleEditMode;
    }
    if (listType.value === 'listByTemplate') {
        return 'edit';
    }
    return ruleData.value.currentProject.editMode;
});
const emit = defineEmits(['getRuleMetricData']);

const ruleStandardListCache = ref([]);
// 从各规则index文件inject eg. src\components\rules\forms\singleTableCheck\index.vue
const ruleMetricList = inject('ruleMetricList', []);
async function getRuleMetricAll() {
    console.log('ruleMetricList:', ruleMetricList.value);
    try {
        if (Array.isArray(ruleMetricList.value)) {
            ruleStandardListCache.value = ruleMetricList.value.map(({
                id, name, en_code, multi_env, used,
            }) => ({
                value: id, label: name, en_code, multi_env, disabled: used,
            }));
        } else {
            ruleStandardListCache.value = [];
        }
        if (configuredRuleMetric.value > -1 && ruleStandardListCache.value) {
            ruleStandardListCache.value.push(configuredRuleMetric.value);
        }
        console.log('ruleStandards', ruleStandardListCache.value);
    } catch (error) {
        console.warn(error);
    }
}

watch(ruleMetricList, () => {
    if (ruleMetricList.value.length) {
        getRuleMetricAll();
    }
}, { immediate: true });

const ruleStandardList = computed(() => {
    // TODO-根据规则类型做差异化，和verifyObject差不多
    let data1; let data2; let isMultiEnvsFlag;
    console.log('ruleData.value', ruleData.value.currentRule.rule_type);
    if (ruleData.value.currentRule.rule_type === 3) {
        // 1. 多数据源（多表对比）
        data1 = ruleData.value.currentRuleDetail.source ? ruleData.value.currentRuleDetail.source : {};
        data2 = ruleData.value.currentRuleDetail.target ? ruleData.value.currentRuleDetail.target : {};
        isMultiEnvsFlag = data1?.linkis_datasource_envs?.length > 1 || data2?.linkis_datasource_envs?.length;
    } else {
        // 2. 单数据源（单表、文件）
        data1 = ruleData.value.currentRuleDetail.datasource ? ruleData.value.currentRuleDetail.datasource[0] : {};
        isMultiEnvsFlag = data1?.linkis_datasource_envs?.length > 1;
        // 3. 自定义sql
        data1?.linkis_datasource_envs_mappings?.forEach((element) => {
            if (element?.datasource_envs?.length > 1) {
                isMultiEnvsFlag = true;
            }
        });
    }
    // console.log('data1',data1,'data2',data2)
    // console.log('isMultiEnvsFlag',isMultiEnvsFlag)
    if (isMultiEnvsFlag) {
        return ruleStandardListCache.value.filter(item => item.multi_env);
    }
    return ruleStandardListCache.value;
});

const onRuleMetricChange = () => {
    const target = ruleStandardList.value.find(item => item.value === formData.ruleMetricId);
    let ruleMetricObj = {};
    if (target) {
        ruleMetricObj = {
            rule_metric_id: formData.ruleMetricId,
            rule_metric_name: target?.label || '',
            rule_metric_en_code: target?.en_code || '',
        };
    } else {
        ruleMetricObj = {
            rule_metric_id: null,
            rule_metric_name: formData.ruleMetricId,
            rule_metric_en_code: null,
        };
    }
    emit('getRuleMetricData', ruleMetricObj);
    console.log('指标校验');
    console.log(ruleMetricObj);
};

const currentRule = computed(() => ruleData.value.currentRuleDetail);
const needUploadMetric = computed(() => props.needUploadMetric);
const whetherVerify = computed(() => {
    let isNeed = needUploadMetric.value;
    // 如果是基于规则模版创建时，校验指标在数据源模版，可以直接从store拿到是否选择需要指标上报
    if (listType.value === 'listByTemplate') {
        isNeed = currentRule.value.upload_abnormal_value || currentRule.value.upload_rule_metric_value;
    }
    return isNeed && !formData.ruleMetricId;
});
watch(whetherVerify, () => {
    if (listType.value !== 'groupList') {
        store.commit('rule/updateCurrentRuleDetail', {
            whetherVerify: whetherVerify.value,
        });
    }
});
onMounted(() => {
    getRuleMetricAll();
});
const ruleConditionFormRef = ref(null);
const valid = async () => {
    try {
        await ruleConditionFormRef.value?.validate();
        return !whetherVerify.value;
    } catch (e) {
        console.log('rulemetricselect error:', e);
        return false;
    }
};
// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>

<style lang='less' scoped>
.verifyTip {
    margin-left: 6px;
    color: #f0484a;
}
.hint {
    color: #646670;
    display: inline-block;
    margin-left: 8px;
}
// 并入到校验条件里的时候的样式
.inline-metric{
    width: 262px;
}

:deep(.rule-metric-popper){
    width: 400px;
}
</style>
