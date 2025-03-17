<template>
    <div class="wd-content rule-detail">
        <div class="rule-detail-form group-rule-form">
            <div class="wd-project-header">
                <LeftOutlined class="back" @click="backToProjectList" />
                <div class="name">{{$t('_.基于规则模版创建')}}</div>
            </div>
            <div class="wd-content-body rule-detail-form edit">
                <h6 class="wd-body-title">{{$t('_.校验规则')}}</h6>
                <FForm
                    :labelWidth="96"
                    labelPosition="right"
                    :model="ruleTypeData"
                >
                    <!-- 规则类型 -->
                    <FFormItem
                        prop="ruleType"
                        :rules="[{ required: true }]"
                        :label="$t('common.ruleType')"
                    >
                        <FSelect
                            v-model="ruleTypeData.ruleType"
                            filterable
                            :placeholder="$t('_.请选择规则类型')"
                            @change="changeRuleType"
                        >
                            <FOption
                                v-for="(item, index) in showRuleTypes"
                                :key="index"
                                :value="item.type"
                                :label="item.label"
                            ></FOption>
                        </FSelect>
                    </FFormItem>
                </FForm>
                <singleTableCheckVerifyRule v-if="ruleTypeData.ruleType === '1-1'" :ref="(e) => verifyRuleRef = e" lisType="listByTemplate" />
                <!-- 文件校验 -->
                <documentCheckVerifyRule v-else-if="ruleTypeData.ruleType === '4-1'" :ref="(e) => verifyRuleRef = e" lisType="listByTemplate" />
                <h6 class="wd-body-title">{{$t('_.校验对象')}}</h6>
                <advanceSetting ref="settingFormRef" v-model:isShowSettting="isShowSettting" :isDisable="isDisable"></advanceSetting>
                <div v-if="!(currentRule?.rule_template_id?.toString().length || 0)" class="temp-class">{{$t('_.校验对象1')}}</div>
                <template v-else>
                    <div v-if="verfiyObjectList.length > 0" v-bind:key="ruleTypeData.ruleType">
                        <div v-for="(verfiyObjectData, index) in verfiyObjectList" v-bind:key="verfiyObjectData.timestamp">
                            <verifyObjectOptionTpl
                                :key="verfiyObjectData.timestamp"
                                :index="index"
                                :len="verfiyObjectList.length"
                                @delete="deleteVerifyObject"
                                @copy="copyVerifyObject"
                            >
                                <verifyObject
                                    :ref="el => { if (el) verfiyObjectListForm[index] = el; }"
                                    :aboutColumnsSelectData="aboutColumnsSelectData"
                                    :objectData="verfiyObjectData"
                                    type="single"
                                    lisType="listByTemplate"
                                    :titleIndex="index"
                                />
                            </verifyObjectOptionTpl>
                        </div>
                    </div>
                </template>
                <div class="addBox" @click="() => clickAddVerifyObject()">
                    <PlusCircleOutlined :class="(currentRule?.rule_template_id?.toString().length || 0) ? 'addIcon' : 'gray'" />
                    <span :class="(currentRule?.rule_template_id?.toString().length || 0) ? 'addBtn' : 'gray'">{{$t('_.新增校验对象')}}</span>
                </div>
            </div>
        </div>
        <FSpace>
            <FButton size="large" class="save" :disabled="isSaveLoading" :loading="isSaveLoading" type="primary" @click="save">{{$t('_.保存')}}</FButton>
            <FButton size="large" class="cancel" :disabled="isSaveLoading" @click="backToProjectList">{{$t('_.取消')}}</FButton>
        </FSpace>
    </div>
</template>

<script setup>

import {
    ref,
    computed,
    onUnmounted,
    provide,
    watch,
    nextTick,
    onMounted,
} from 'vue';
import { useStore } from 'vuex';
import {
    useI18n, useRouter, useRoute, request as FRequest,
} from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import {
    useListener,
    getRuleMetricAll,
    columnData2Str,
} from '@/components/rules/utils';
import useRuleBasicData from '@/hooks/useRuleBasicData';
import { LeftOutlined, PlusCircleOutlined } from '@fesjs/fes-design/es/icon';
import { ruleTypes } from '@/common/utils';
import { batchAddRule } from '@/components/rules/api/index';
import singleTableCheckVerifyRule from '@/components/rules/forms/singleTableCheck/verifyRule.vue';
import verifyObject from '@/components/rules/VerifyObject.vue';
import verifyObjectOptionTpl from '@/components/rules/forms/listByTemplateForms/verifyObjectOptionTpl.vue';
import advanceSetting from '@/components/rules/forms/listByTemplateForms/advanceSetting.vue';
import documentCheckVerifyRule from '@/components/rules/forms/documentCheck/verifyRule.vue';
import eventbus from '@/common/useEvents';
import { FMessage, FModal } from '@fesjs/fes-design';


const { t: $t } = useI18n();
const store = useStore();
const router = useRouter();
const route = useRoute();
const formDetailPageKey = ref(new Date().getTime());

const verifyRuleRef = ref(null);
const verfiyObjectListForm = ref([]);
const ruleData = computed(() => store.state.rule);
const currentRule = computed(() => ruleData.value.currentRuleDetail);
const isDisable = computed(() => (!!currentRule.value?.rule_template_id?.toString().length));
const query = computed(() => route.query);
const isSaveLoading = ref(false);
const isShowSettting = ref(false);
const curAdvanceSettingData = ref({});

// ruleType相关
console.log('ruleType相关', ruleTypes);
const deafaultSelectRuleType = '1-1';
const defaultRuleType = ruleTypes.find(({ type }) => type === deafaultSelectRuleType)?.value || '';
const ruleTypeData = ref({
    ruleType: deafaultSelectRuleType,
});
const showRuleTypes = ruleTypes.filter(item => item.type === '4-1' || item.type === '1-1');
const groupType = computed(() => ruleTypes.find(item => item.type === ruleTypeData.value.ruleType).value || '');
// ruleType是变化的，需要注入一个对象
provide('ruleType', ruleTypeData);

const configuredRuleMetric = ref({});
provide('configuredRuleMetric', computed(() => configuredRuleMetric.value));
provide('upstreamParams', computed(() => route.query));

// const ruleMetricList = ref([]);
// provide('ruleMetricList', computed(() => ruleMetricList.value));

const verfiyObjectList = ref([{
    timestamp: 1,
}]);

const {
    proxyUserList,
    updateProxyUserList,
    clusterList,
    loadClusterList,
} = useRuleBasicData();

provide('proxyUserList', computed(() => proxyUserList.value || []));
provide('clusterList', computed(() => clusterList.value || []));


const changeRuleType = () => {
    // 清空store
    store.commit('rule/clearCurrentRuleData');
    store.commit('rule/setVerifyColumns', []);
};

const backToProjectList = () => {
    if (window.history?.state?.back) {
        // 从项目里正常跳转进来
        router.back();
    } else {
        // 复制url直接进来
        router.replace('/projects');
    }
    // 清空store
    store.commit('rule/setCurrentRuleDetail', {});
};

const addVerifyObject = async (verfiyObject = null) => {
    const newVerifyObject = {
        timestamp: Date.now(),
    };

    // 如果高级设置打开，需要传入设置的数据源数据
    if (isShowSettting.value) newVerifyObject.datasource = [cloneDeep(curAdvanceSettingData.value)];
    verfiyObjectList.value.push(verfiyObject || newVerifyObject);

    await nextTick();
    // 复制或者高级设置下新增后，需要通知verifyObject初始化选项
    if (verfiyObject || isShowSettting.value) eventbus.emit('IS_RULE_DETAIL_DATA_LOADED');
};
const clickAddVerifyObject = () => {
    if (currentRule.value?.rule_template_id?.toString()?.length || 0) {
        if (isShowSettting.value) {
            for (let i = 0; i < verfiyObjectList.value.length; i++) {
                verfiyObjectList.value[i] = cloneDeep(verfiyObjectListForm.value[i].verifyObjectData);
            }
        }
        addVerifyObject();
    }
};

const copyVerifyObject = (index) => {
    const target = cloneDeep(verfiyObjectListForm.value[index].verifyObjectData);
    for (let i = 0; i < verfiyObjectList.value.length; i++) {
        verfiyObjectList.value[i] = cloneDeep(verfiyObjectListForm.value[i].verifyObjectData);
    }
    target.timestamp = Date.now();
    addVerifyObject(target);
};

const deleteVerifyObject = (index) => {
    verfiyObjectListForm.value.splice(index, 1);
    verfiyObjectList.value.splice(index, 1);
};

const valid = async () => {
    try {
        const arr = [verifyRuleRef.value.valid()];
        for (let i = 0; i < verfiyObjectListForm.value.length; i++) {
            arr.push(verfiyObjectListForm.value[i].valid());
        }
        const result = await Promise.all(arr);
        return !result.includes(false);
    } catch (err) {
        console.warn(err);
        return false;
    }
};


const settingFormRef = ref();
const save = async () => {
    try {
        if (isSaveLoading.value) return;
        const advanceSettingValidRes = await settingFormRef.value.valid();
        if (!advanceSettingValidRes) {
            FMessage.warning($t('_.高级设置校验未通过，请关闭或者检查是否有字段不符合要求或为空'));
            return;
        }
        const result = await valid();
        if (!result) {
            FMessage.warning($t('_.校验未通过，请检查是否有字段不符合要求或为空'));
            return;
        }

        const projectId = route.query.projectId;
        const {
            rule_template_id,
            rule_template_name,
            rule_name,
            cn_name,
            rule_detail,
            reg_rule_code,
            execution_parameters_name,
            ruleArgumentList,
            alarm_variable,
        } = cloneDeep(currentRule.value);

        const ruleMetricIds = [];

        const checkObjectList = verfiyObjectListForm.value.map((item) => {
            const verifyObjectData = item.verifyObjectData;
            const datasource = verifyObjectData.datasource[0];

            verifyObjectData.ruleMetricData?.rule_metric_id && ruleMetricIds.push(verifyObjectData.ruleMetricData?.rule_metric_id);
            const tempData = {
                col_names: verifyObjectData.colNamesStrArr,
                datasource_type: datasource.type,
                file_table_desc: datasource.type.toLowerCase() === 'fps' ? columnData2Str(datasource.file_table_desc) : '',
                rule_metric_id: verifyObjectData.ruleMetricData?.rule_metric_id,
                rule_metric_name: verifyObjectData.ruleMetricData?.rule_metric_name,
                rule_metric_en_code: verifyObjectData.ruleMetricData?.rule_metric_en_code,
            };

            const res = Object.assign({}, datasource, tempData);
            return res;
        });
        if (ruleMetricIds.length && (Array.from(new Set(ruleMetricIds)).length < ruleMetricIds.length)) {
            FMessage.warning($t('_.校验指标不能重复，一个校验对象对应一个校验指标！'));
            return;
        }

        const params = {
            project_id: projectId,
            rule_template_id,
            rule_type: ruleTypeData.value.ruleType === '1-1' ? 1 : 4,
            rule_template_name,
            rule_name,
            rule_cn_name: cn_name,
            rule_detail,
            reg_rule_code,
            execution_parameters_name,
            check_variable_list: alarm_variable.map(v => ({
                output_meta_id: v.output_meta_id,
                check_template: v.check_template,
                compare_type: v.compare_type,
                threshold: v.threshold,
                file_output_unit: v.file_output_unit,
            })),
            template_arguments: (ruleArgumentList || []).map(({
                argument_step,
                argument_id,
                argument_value,
                argsSelectList,
                flag,
                argument_type,
            }) => {
                if (flag) {
                    // eslint-disable-next-line camelcase
                    const item = argsSelectList.find(k => k.value === argument_value);
                    // eslint-disable-next-line camelcase
                    argument_value = item ? item.key_name : argument_value;
                }
                return {
                    argument_step,
                    argument_id,
                    argument_value,
                    argument_type,
                };
            }),
            check_object_list: checkObjectList,
        };
        isSaveLoading.value = true;
        await batchAddRule(params);
        isSaveLoading.value = false;
        FMessage.success($t('_.批量创建成功'));
        setTimeout(() => {
            backToProjectList();
        }, 1000);
        console.log(params);
    } catch (error) {
        isSaveLoading.value = false;
        FMessage.error($t('_.批量创建失败'));
        console.log(error);
    }
};

// 在基于规则模版创建时，需要拿到关于校验字段/过滤字段展示和校验的相关信息
const aboutColumnsSelectData = ref({});
useListener('notifyColumnsData', (val) => {
    aboutColumnsSelectData.value = val;
});

watch(() => ruleTypeData.value.ruleType, () => {
    isShowSettting.value = false;
    verfiyObjectList.value = [];
    addVerifyObject();
    aboutColumnsSelectData.value = {};
});

const handleAdvanceSetting = () => {
    verfiyObjectList.value = verfiyObjectList.value.map((item) => {
        item.colNamesStrArr = [];
        item.ruleMetricData = [];
        if (!item.datasource) {
            item.datasource = [cloneDeep(curAdvanceSettingData.value)];
        } else {
            item.datasource = [cloneDeep(curAdvanceSettingData.value)];
            // Object.assign(item.datasource[0], cloneDeep(curAdvanceSettingData.value));
        }
        return item;
    });
    eventbus.emit('IS_RULE_DETAIL_DATA_LOADED');
};
useListener('ADVANCE_SETTING_CHANGE', (val) => {
    curAdvanceSettingData.value = cloneDeep(val);
    handleAdvanceSetting();
});

onMounted(async () => {
    updateProxyUserList();
    loadClusterList();
    // ruleMetricList.value = await getRuleMetricAll();
});


</script>

<style lang="less" scoped>
.name {
    font-size: 24px;
    color: #0F1222;
    margin: 0 12px;
    font-weight: 500;
}
.addBox {
    display: flex;
    align-items: center;
    .addBtn {
    width: 90px;
    height: 22px;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #5384FF;
    letter-spacing: 0;
    line-height: 22px;
    font-weight: 400;
    cursor: pointer;
    padding-left: 2px;
    }
    .addIcon {
        color: #5384FF;
        cursor: pointer;
    }
    .gray {
        color: rgba(15,18,34,0.30);
        cursor: pointer;

    }
}
.temp-class {
    background: rgba(15, 18, 34, 0.03);
    border-radius: 4px;
    padding: 24px;
    width: 498px;
    margin-bottom: 8px;
}
</style>
