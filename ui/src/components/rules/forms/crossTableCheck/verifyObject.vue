<template>
    <div class="rule-detail-form" :class="{ edit: ruleData.currentProject.editMode !== 'display' }">
        <h6 class="wd-body-title">校验对象</h6>
        <FForm
            ref="verifyObjectFormRef"
            :layout="layout"
            :class="layout === 'inline' ? 'inline-form-item' : ''"
            :model="verifyObjectData"
            :rules="verifyObjectRules"
            :labelWidth="96"
            :labelPosition="ruleData.currentProject.editMode !== 'display' ? 'right' : 'left'"
        >
            <!-- rule_id -->
            <FFormItem v-if="verifyObjectData.rule_id" :label="$t('tableThead.ruleId')" prop="ruleId">
                <FInput
                    v-model="verifyObjectData.rule_id"
                    class="form-edit-input"
                    disabled
                />
                <div class="form-preview-label">{{verifyObjectData.rule_id}}</div>
            </FFormItem>
            <!-- 规则类型 -->
            <FFormItem :label="$t('common.ruleType')">
                <FSelect
                    v-model="ruleType"
                    class="form-edit-input"
                    filterable
                    :disabled="ruleData.currentProject.editMode !== 'create'"
                    placeholder="请选择规则类型"
                >
                    <FOption
                        v-for="(item, index) in showRuleTypes"
                        :key="index"
                        :value="item.value"
                        :label="item.label"
                    ></FOption>
                </FSelect>
                <div class="form-preview-label">{{ruleTypeLabel}}</div>
            </FFormItem>
            <FFormItem v-if="ruleData.currentProject.isEmbedInFrame" label="数据来源">
                <FRadioGroup v-if="ruleData.currentProject.editMode !== 'display'" v-model="verifyObjectData.isUpStream" class="form-edit-input" :cancelable="false" @change="onUpStreamChange">
                    <FRadio :value="true">上游表</FRadio>
                    <FRadio :value="false">正常表</FRadio>
                </FRadioGroup>
                <div class="form-preview-label">{{verifyObjectData.isUpStream ? '上游表' : '正常表'}}</div>
            </FFormItem>
            <!-- 任务执行集群 -->
            <FFormItem label="任务执行集群" prop="cluster_name">
                <FSelect
                    v-model="verifyObjectData.cluster_name"
                    filterable
                    class="form-edit-input"
                    @change="onClusterChange"
                >
                    <FOption
                        v-for="(item, index) in clusterList"
                        :key="index"
                        :disabled="item.disabled"
                        :value="item.cluster_name"
                        :label="item.cluster_name"
                    ></FOption>
                </FSelect>
                <div class="form-preview-label">{{verifyObjectData.cluster_name}}</div>
            </FFormItem>
            <div class="config-wrapper">
                <div class="config">
                    <verifyObject :ref="(e) => ele.sourceVerifyObjectRef = e" key="source" type="source" />
                </div>
                <div class="config">
                    <verifyObject :ref="(e) => ele.targetVerifyObjectRef = e" key="target" type="target" />
                </div>
            </div>
        </FForm>
    </div>
</template>
<script setup>
import {
    ref, computed, inject, nextTick,
} from 'vue';
import { useStore } from 'vuex';
import { ruleTypes } from '@/common/utils';
import { cloneDeep } from 'lodash-es';
import { useI18n } from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import {
    ExclamationCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    ruleTypeChangeEvent, dataSourceTypeList, getDataSourceType, buildColumnData, useListener,
} from '@/components/rules/utils';
import useDataSource from '@/components/rules/hook/useDataSource';
import verifyObject from '@/components/rules/VerifyObject';


const store = useStore();
const { t: $t } = useI18n();

const ruleData = computed(() => store.state.rule);

// eslint-disable-next-line no-undef
const props = defineProps({
    tips: {
        type: Object,
        default: {},
    },
    isNormalMode: {
        type: Boolean,
        default: true,
    },
    layout: {
        type: String,
        default: 'horizontal',
    },
});

const clusterList = inject('clusterList');

const verifyObjectData = ref({
    datasource: [{}],
    isUpStream: false,
});
// 组件ref
const ele = ref({
    sourceVerifyObjectRef: null,
    targetVerifyObjectRef: null,
});

const isFormInit = ref(true);
// 表单规则
const verifyObjectRules = ref({
    cluster_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
});


// 规则类型的处理
const currentRuleType = inject('currentRuleType');
const showRuleTypes = ruleTypes.filter(item => item.type !== '3-2' && item.type !== '0-0'); // 不展示库一致性比对和执行参数
const ruleType = computed({
    get() {
        return ruleTypes.find(({ type }) => type === currentRuleType.value)?.value || '';
    },
    set(v) {
        console.log('BaseInfo set ruleType', v);
        eventbus.emit(ruleTypeChangeEvent, ruleTypes.find(({ value }) => value === v).type);
    },
});
const ruleTypeLabel = computed(() => ruleTypes.find(({ type }) => type === currentRuleType.value)?.label || '');

// 初始化数据
useListener('IS_RULE_DETAIL_DATA_LOADED', async () => {
    try {
        const target = cloneDeep(ruleData.value.currentRuleDetail);
        const datasource = target;
        if (!datasource.type) {
            // 默认是hive
            datasource.type = 'hive';
        }
        verifyObjectData.value = target;
    } catch (err) {
        console.warn(err);
    }
});

const onClusterChange = () => {
    eventbus.emit('CLUSTER_NAME_CHANGE', verifyObjectData.value.cluster_name);
};
const onUpStreamChange = (data) => {
    eventbus.emit('UPSTREAM_CHANGE', data);
};
const verifyObjectFormRef = ref(null);
const valid = async () => {
    try {
        const result = await Promise.all([verifyObjectFormRef.value.validate(), ele.value.sourceVerifyObjectRef.valid(), ele.value.targetVerifyObjectRef.valid()]);
        if (result.includes(false)) {
            console.log('datasrouce表单验证失败: ', result);
            return false;
        }
        console.log('datasrouce表单验证成功: ', result);
        return true;
    } catch (err) {
        console.warn(err);
        return false;
    }
};

// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
<style scoped lang="less">
.config-wrapper {
    border-radius: 4px;
    padding-left: 112px;
    display: grid;
    gap: 0 24px;
    grid-template-columns: 498px 498px;
    .config {
        width: 498px;
        background: #FFFFFF;
        background: rgba(15,18,34,0.03);
        border-radius: 4px;
        padding: 24px 24px 0 24px;
        :deep(.wd-body-title) {
            display: none;
        }
        :deep(.rule-id) {
            display: none;
        }
        :deep(.rule-type) {
            display: none;
        }
        :deep(.upstream) {
            display: none;
        }
    }
}
</style>
