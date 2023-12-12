<template>
    <!-- 校验指标 -->
    <!-- 除去文件校验、多指标校验，其他都是公用指标 -->
    <!-- 单指标这个地方是必填的 -->
    <FForm
        :model="ruleStandardData"
        :labelWidth="labelWidth"
        :labelPosition="editMode !== 'display' ? 'right' : 'left'"
        class="rule-condition-form"
        :class="{ 'rule-condition-form-preview': editMode === 'display' }"
    >
        <RuleMetricSelect
            v-if="listType !== 'listByTemplate'"
            ref="ruleMetricSelectRef"
            :defaultMetricId="ruleStandardData.rule_metric_id"
            :listType="listType"
            :needUploadMetric="needUploadMetric"
            @getRuleMetricData="getRuleMetricData"
        ></RuleMetricSelect>
        <!-- VerificationMetric: '校验指标' -->
        <!-- <FFormItem
            v-if="!['documentVerification', 'sqlVerification'].includes(groupType)"
            :rules="[{ required: groupType === 'newCustomRule', type: 'number', message: $t('common.notEmpty'), trigger: 'change' }]"
            :label="$t('common.VerificationMetric')"
            prop="rule_metric_id"
        >
            <FSelect
                v-model="ruleStandardData.rule_metric_id"
                class="form-edit-input"
                clearable
                filterable
                :options="ruleStandardList"
                @change="onRuleMetricChange"
            ></FSelect>
            <div
                class="form-preview-label"
            >
                {{getLabelByValue(ruleStandardList, ruleStandardData.rule_metric_id) || '--'}}
            </div>
            <FTooltip :content="$t('common.metricTip')" placement="right-start">
                <ExclamationCircleOutlined class="tip hint" />
            </FTooltip>
            <div v-if="whetherVerify" class="verifyTip">指标未配置，上报开关无效</div>
        </FFormItem> -->
        <!-- 校验条件 -->
        <FFormItem
            :label="title ? $t(title) : ''"
            labelClass="label-form-item"
            :class="editMode !== 'display' ? 'required-form-item' : ''"
        >
            <div class="validation-list">
                <div
                    v-for="(ruleCondition,index) in ruleStandardData.ruleConditionList"
                    v-bind:key="index"
                    class="validation-item"
                >
                    <!-- {{ruleCondition}} -->
                    <RuleConditionItem
                        :ref="el => { if (el) ruleConditionForms[index] = el; }"
                        v-model:ruleCondition="ruleStandardData.ruleConditionList[index]"
                        class="table-validation-check"
                        layout="inline"
                        isEllipsis
                        :solidification="solidification"
                    >
                        <FFormItem v-if="editMode !== 'display' && ruleStandardData.ruleConditionList.length > 1" class="delete-btn">
                            <FButton
                                type="text"
                                @click="deleteRuleCondition(index)"
                            >
                                <template #icon>
                                    <MinusCircleOutlined />
                                </template>删除
                            </FButton>
                        </FFormItem>
                    </RuleConditionItem>
                    <LinkOutlined class="link-icon" />
                </div>
                <FButton
                    v-if="editMode !== 'display' && !disableAddBtn"
                    type="link"
                    style="margin-left: -23px;"
                    @click="addRuleCondition"
                >
                    <template #icon>
                        <PlusCircleOutlined />
                    </template>新增
                </FButton>
            </div>
        </FFormItem>
    </FForm>
</template>
<script setup>
import {
    ref, computed, inject, onMounted, watchEffect, watch, provide,
} from 'vue';
import { useStore } from 'vuex';
import { cloneDeep } from 'lodash-es';
import { useI18n, request as FRequest } from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import {
    PlusCircleOutlined, LinkOutlined, MinusCircleOutlined, ExclamationCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { MAX_PAGE_SIZE } from '@/assets/js/const';
import { getLabelByValue, ruleTypes } from '@/common/utils';
import {
    useListener, formatAlarmVariable,
} from '@/components/rules/utils';
import RuleMetricSelect from '@/components/rules/forms/RuleMetricSelect';
import RuleConditionItem from './RuleConditionItem.vue';

const store = useStore();
const ruleData = computed(() => store.state.rule);
const ruleType = inject('ruleType');
const groupType = computed(() => ruleTypes.find(item => item.type === ruleType).value || '');
const { t: $t } = useI18n();

const configuredRuleMetric = inject('configuredRuleMetric');
// eslint-disable-next-line no-undef
const props = defineProps({
    // 校验条件list，一个规则组可能有多个校验条件 一般存在store.state.rule.currentRuleDetail.alarm_variable
    ruleConditions: {
        type: Object,
        default: () => { },
    },
    // 页面的类型 普通规则组：commonList，表规则组：groupList
    lisType: {
        type: String,
        default: 'commonList',
    },
    // 是否需要上报指标
    needUploadMetric: {
        type: Boolean,
        default: false,
    },
    // 执行参数调用组件时使用
    mode: {
        type: String,
        default: '',
    },
    // 执行参数调用组件时使用
    templateId: {
        type: Number,
        default: -1,
    },
    // 组件名称可传进来自定义
    title: {
        type: String,
        default: 'common.ruleCondition',
    },
    // 校验模板是否固化校验条件
    solidification: {
        type: Boolean,
        default: false,
    },
});
const mode = computed(() => props.mode);
const listType = computed(() => props.lisType);
// label宽度
const labelWidth = computed(() => {
    if (listType.value === 'groupList') {
        return 70;
    }
    return 96;
});
// 判断页面为普通规则组/表规则组，切换不同 editMode 变量
const editMode = computed(() => {
    if (!mode.value) {
        if (listType.value === 'groupList') {
            return ruleData.value.currentProject.groupRuleEditMode;
        }
        if (listType.value === 'listByTemplate') {
            return 'edit';
        }
        return ruleData.value.currentProject.editMode;
    }
    return mode.value;
});

// eslint-disable-next-line no-undef
const emit = defineEmits(['update:ruleConditions']);
const ruleMetricSelectRef = ref(null);
const ruleStandardData = ref({});
const cacheTemplateId = ref(0);
const reset = () => {
    ruleStandardData.value.ruleConditionList = [{}];
    ruleStandardData.value.rule_metric_id = '';
    // console.log(ruleStandardData.value)
};
// checkField注入
const checkFieldList = ref([]);
const handleTemplateChange = async (id) => {
    try {
        if (!id) {
            return;
        }
        const result = await FRequest(`api/v1/projector/rule_template/meta_input/${id}`, {}, {
            cache: true, // 防止重复提交出错
            method: 'get',
        });
        checkFieldList.value = result.template_output || [];
        console.log('checkFieldList', checkFieldList.value);
    } catch (error) {
        console.warn(error);
    }
};
const initFlag = ref(true);
const onTemplateChange = (id) => {
    handleTemplateChange(id);
    if (initFlag.value) {
        initFlag.value = false;
    } else {
        reset();
    }
};
ruleStandardData.value.ruleConditionList = computed({
    get: () => {
        // 当校验指标为空时，后台返回的id为-1，在这里转化成'',校验指标数据无论是否使用公共指标后台都会直接放到alarm_variable数组里，不直接放到ruledetail下
        const alarmVariable = props.ruleConditions;
        console.log('alarmVariable', alarmVariable);
        if (alarmVariable?.length) {
            ruleStandardData.value.rule_metric_id = alarmVariable[0]?.rule_metric_id === -1 ? '' : alarmVariable[0].rule_metric_id;
        }
        if (cacheTemplateId.value !== props.templateId && groupType.value === 'executionParams') {
            cacheTemplateId.value = props.templateId;
            onTemplateChange(props.templateId);
        }
        return alarmVariable;
    },
    set: (value) => {
        // console.log('set',value)
        emit('update:ruleConditions', value);
    },
});

// 判断是否展示新增校验条件按钮
const disableAddBtn = computed(() => props.solidification && [1, 2, 3].includes(ruleStandardData.value.ruleConditionList[0].check_template));

const ruleConditionForms = ref([]);
const addRuleCondition = () => {
    ruleStandardData.value.ruleConditionList.push({
        output_meta_id: '',
        output_meta_name: '',
        check_template: props.solidification ? ruleStandardData.value.ruleConditionList[0].check_template : '',
        compare_type: '',
        threshold: '',
        file_output_unit: null,
        delete_fail_check_result: true,
        upload_abnormal_value: false,
        upload_rule_metric_value: false,
    });
};

const deleteFormRefs = (index) => {
    ruleConditionForms.value.splice(index, 1);
};

const deleteRuleCondition = (index) => {
    if (ruleStandardData.value.ruleConditionList.length === 1) return;
    ruleStandardData.value.ruleConditionList.splice(index, 1);
    deleteFormRefs(index);
};

const getRuleMetricData = (ruleMetricData) => {
    console.log('指标校验');
    ruleStandardData.value.ruleConditionList.forEach((item) => {
        item.rule_metric_id = ruleMetricData.rule_metric_id;
        item.rule_metric_name = ruleMetricData.rule_metric_name;
        item.rule_metric_en_code = ruleMetricData.rule_metric_en_code;
    });
    console.log(ruleStandardData.value.ruleConditionList);
};

const valid = async () => {
    try {
        const formArrays = [];
        if (ruleMetricSelectRef.value) {
            formArrays.push(ruleMetricSelectRef.value.valid());
        }
        ruleConditionForms.value.forEach((form) => {
            formArrays.push(form.valid());
        });
        const result = await Promise.all(formArrays);
        return !result.includes(false);
    } catch (e) {
        console.warn(e);
        return false;
    }
};
if (groupType.value === 'executionParams') {
    provide('checkFieldList', computed(() => checkFieldList.value));
}
// eslint-disable-next-line no-undef
defineExpose({ valid, reset });
</script>
<style lang='less' scoped>
.hint {
    color: #646670;
    display: inline-block;
    margin-left: 8px;
}
.required-form-item {
    :deep(.fes-form-item-label::before) {
        display: inline-block;
        margin-top: 2px;
        margin-right: 4px;
        color: var(--f-danger-color);
        content: "*";
    }
    :deep(.label-form-item){
        text-align-last: right
    }
}

.validation-list {
    width: 100%;
    .validation-item {
        position: relative;
        padding-left: 8px;
        margin-bottom: 22px;
        .table-validation-check {
            display: flex;
            flex-wrap: wrap;
            grid-gap: 0; // 取消flex布局时的grid-gap 解决浏览器版本不同时的样式差异
            .delete-btn{
                width: 80px;
            }
        }
        &:nth-last-of-type(1) {
            margin-bottom: 0;
            .fes-form {
                margin-bottom: 0;
            }
            .link-icon {
                display: none;
            }
        }
        &::after {
            content: "";
            display: block;
            position: absolute;
            top: 0;
            left: 0;
            width: 1px;
            height: 100%;
            background: #cfd0d3;
        }
        .link-icon {
            position: absolute;
            left: -8px;
            bottom: -16px;
            padding: 1px;
            color: #5384ff;
            background: rgba(83, 132, 255, 0.06);
            border-radius: 1px;
        }
        .fes-form-inline {
            :deep(.fes-form-item) {
                margin-bottom: 22px;
                margin-right: 24px;
            }
        }
    }
}
.rule-condition-form-preview{
    .validation-list .validation-item .fes-form-inline {
        :deep(.fes-form-item) {
            margin-bottom: 16px;
            margin-right: 24px;
        }
    }
    :deep(.item-min-width) {
        width: auto;
    }
}
.verifyTip {
    margin-left: 6px;
    color: #f0484a;
}
</style>
