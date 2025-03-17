<template>
    <div class="rule-detail-form group-rule-form" :class="{ edit: editMode === 'edit' }">
        <div class="wd-content-body tab-content">
            <ul v-if="(!currentProject.isWorkflowProject || (currentProject.isEmbedInFrame && !isEmbedInDMS))" class="wd-body-menus">
                <li class="wd-body-menu-item">
                    <FDropdown :options="options" @click="selectAddRule">
                        <span>{{$t('_.添加规则')}}</span>
                    </FDropdown>
                </li>
                <li
                    class="wd-body-menu-item"
                    @click="onfilterRule"
                >
                    {{$t('common.filter')}}{{filtrateCount > 0 ? `（已选${filtrateCount}项）` : ''}}
                </li>
                <li v-if="editMode !== 'edit'" class="wd-body-menu-item" @click="editRule">{{$t('_.编辑')}}</li>
            </ul>
            <h6 class="wd-body-title">{{$t('_.校验规则')}}</h6>
            <div v-if="isLoading">
                <BPageLoading :loadingText="{ loading: '' }" />
            </div>
            <div v-else-if="ruleList.length > 0">
                <div v-for="(rule,index) in ruleList" v-bind:key="rule.timestamp">
                    <VerifyRuleOptionTpl
                        :key="rule.timestamp"
                        :rule="ruleList[index]"
                        :index="index"
                        @copyRule="copyRule"
                        @deleteRule="confirmDeleteRule"
                        @executeRule="executeRule"
                    >
                        <singleTableCheckVerifyRule
                            v-if="rule['rule_type'] === 1"
                            :ref="el => { if (el) ruleListForm[index] = el; }"
                            v-model:rule="ruleList[index]"
                            lisType="groupList"
                            :fpsColumns="fpsColumns"
                        />
                        <documentCheckVerifyRule
                            v-else-if="rule['rule_type'] === 4"
                            :key="rule.timestamp"
                            :ref="el => { if (el) ruleListForm[index] = el; }"
                            v-model:rule="ruleList[index]"
                            lisType="groupList"
                        />
                    </VerifyRuleOptionTpl>
                </div>
            </div>
            <div v-else class="empty-block">
                <BPageLoading actionType="emptyInitResult" />
            </div>
            <div class="table-pagination-container">
                <FPagination
                    v-model:currentPage="pagination.page"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="onPageChange"
                    @pageSizeChange="onPageChange"
                ></FPagination>
            </div>
            <FSpace v-if="editMode === 'edit'">
                <FButton class="save" :disabled="isSaveLoading" :loading="isSaveLoading" type="primary" @click="saveRules">{{$t('_.保存规则')}}</FButton>
                <FButton class="cancel" :disabled="isSaveLoading" @click="cancel">{{$t('_.取消')}}</FButton>
            </FSpace>
            <!-- 筛选弹框 -->
            <FilterModel
                v-model:filterQueryData="filterQueryData"
                v-model:showFilterSearchModal="showFilterSearchModal"
                @onSearchFilterData="onSearchFilterData"
                @cancelSearchFilterData="cancelSearchFilterData"
                @resetFilterQueryForm="resetFilterQueryForm"
            />
        </div>
    </div>
    <ExecRulePanel
        v-model:show="showExecutation"
        v-model="executationConfig"
        :list="executationRules"
        :gid="executeRuleId"
        @ok="excuteRuleInProjectDetail"
    />
</template>
<script setup>

import {
    watch,
    ref,
    onMounted,
    provide,
    computed,
    nextTick,
    onUnmounted,
} from 'vue';
import dayjs from 'dayjs';
import { useStore } from 'vuex';
import { request as FRequest, useI18n, useRoute } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { cloneDeep } from 'lodash-es';
import eventbus from '@/common/useEvents';
import {
    formatAlarmVariable, useListener, getRuleMetricAll, columnData2Str,
} from '@/components/rules/utils';
import {
    DOCUMENT_CHECK_RULE_TYPE, handleExecuteParams, ruleTypeList, SINGLE_CHECK_RULE_TYPE,
} from '@/common/utils';
import singleTableCheckVerifyRule from '@/components/rules/forms/singleTableCheck/verifyRule.vue';
import documentCheckVerifyRule from '@/components/rules/forms/documentCheck/verifyRule.vue';
import VerifyRuleOptionTpl from '@/components/rules/forms/groupListForms/verifyRuleOptionTpl.vue';
import FilterModel from '@/components/rules/forms/groupListForms/filterModel.vue';
import { unLockRule, lockRule } from '@/components/rules/api/index';
import { BPageLoading } from '@fesjs/traction-widget';
import useExecutation from '@/pages/projects/hooks/useExecutation';
import ExecRulePanel from '@/components/ExecutionConfig/ExecRulePanel';

const executeRuleId = ref(null);
// 执行规则相关useEffect
const {
    showExecutation,
    executationConfig,
    executationRules,
    executeTaskPre,
    excuteRuleInProjectDetail,
} = useExecutation();

// 默认每页数量
const DEFAULT_PAGE_SIZE = 10;
// 默认页数
const DEFAULT_PAGE_NUM = 1;

const { t: $t } = useI18n();
const store = useStore();
const ruleData = computed(() => store.state.rule);
const currentProject = computed(() => ruleData.value.currentProject);
const editMode = computed(() => ruleData.value.currentProject.groupRuleEditMode);
const isUpStream = computed(() => ruleData.value.currentRuleDetail.isUpStream);
const route = useRoute();

//
const options = computed(() => [
    {
        value: SINGLE_CHECK_RULE_TYPE,
        label: $t('_.单表校验'),
    },
    {
        value: DOCUMENT_CHECK_RULE_TYPE,
        label: $t('_.文件校验'),
        disabled: isUpStream.value || ['fps', 'mysql', 'tdsql'].includes(ruleData.value.currentRuleDetail?.datasource?.[0].type),
    },
]);

// 新建的规则类型
const selectRuleType = ref(SINGLE_CHECK_RULE_TYPE);
const isLoading = ref(false);
const isSaveLoading = ref(false);

const configuredRuleMetric = ref({});
provide('configuredRuleMetric', computed(() => configuredRuleMetric.value));
// const ruleMetricList = ref([]);
// provide('ruleMetricList', computed(() => ruleMetricList.value));

// 规则列表
const ruleList = ref([]);
const ruleListForm = ref([]);

// 分页
const pagination = ref({
    page: DEFAULT_PAGE_NUM,
    size: DEFAULT_PAGE_SIZE,
    total: 0,
});
const paginationCache = ref({
    page: DEFAULT_PAGE_NUM,
    size: DEFAULT_PAGE_SIZE,
});

// 筛选
const showFilterSearchModal = ref(false);
const filtrateCount = ref(0);
const filterQueryDataCopy = ref({});
const filterQueryData = ref({
    rule_name: '',
    rule_cn_name: '',
    template_id: '',
    col_names: [], // 数据字段
});

// 判断校验对象是否被创建
const judgeIsVerifyObjectCreated = () => {
    if (!ruleData.value.currentProject.groupId) {
        FMessage.warning($t('_.请保存校验对象后再进行该操作'));
        return false;
    }
    return true;
};

const loadRuleList = async () => {
    try {
        // ruleMetricList.value = await getRuleMetricAll();
        // 如果还未创建，直接返回
        if (!route.query.ruleGroupId) {
            return;
        }
        if (isLoading.value) {
            return;
        }
        isLoading.value = true;
        const result = await FRequest('api/v1/projector/rule/group/rules/query', {
            project_id: ruleData.value.currentProject.projectId,
            rule_group_id: ruleData.value.currentProject.groupId,
            name: filterQueryData.value.rule_name,
            cn_name: filterQueryData.value.rule_cn_name,
            template_id: filterQueryData.value.template_id,
            rule_type: filterQueryData.value.rule_type,
            columns: filterQueryData.value.col_names,
            page: pagination.value.page - 1,
            size: pagination.value.size,
        });
        // 把ref列表清空，防止ref渲染出错
        ruleListForm.value = [];
        if (result) {
            const { total, data = [] } = result;
            pagination.value.total = total;
            if (data && data.length) {
                ruleList.value = data.map((item) => {
                    // 如果是文件校验需要特殊处理
                    if (item.rule_type === 4) {
                        item.alarm_variable = item.alarm_variable.length ? item.alarm_variable : item.file_alarm_variable;
                        // item.alarm_variable.map((i) => {
                        //     i.output_meta_id = i.file_output_name;
                        //     return i;
                        // });
                    }
                    item.timestamp = `${item.rule_group_id}${item.rule_id}${Date.now()}`;
                    item.ruleStandard = item.alarm_variable[0]?.rule_metric_id;
                    item.ruleMetricEnCode = item.alarm_variable[0]?.rule_metric_en_code;
                    item.ruleStandardName = item.alarm_variable[0]?.rule_metric_name;
                    item.create_time_unix = dayjs(item.create_time).unix();
                    return item;
                });
                // 返回的数据按照创建时间排序
                ruleList.value.sort((a, b) => b.create_time_unix - a.create_time_unix);
                await nextTick();
                // 等ruleListForm更新，init才能执行
                setTimeout(() => {
                    ruleListForm.value.forEach((item) => {
                        item.init();
                    });
                }, 0);
                eventbus.emit('RULE_LIST_READY', ruleList.value);
            } else {
                ruleList.value = [];
            }
        } else {
            ruleList.value = [];
        }
        isLoading.value = false;
    } catch (err) {
        isLoading.value = false;
        console.warn(err);
    }
};
// eslint-disable-next-line complexity
const convertData = (data) => {
    const { execution_parameters_name } = handleExecuteParams(data);
    const datasourceInfo = cloneDeep(ruleData.value.currentRuleDetail.datasource[0]);
    // datasource只传递接口所需要的字段
    const postDatasource = {
        db_name: datasourceInfo.db_name,
        table_name: datasourceInfo.table_name,
        cluster_name: datasourceInfo.cluster_name,
        linkis_datasource_envs: datasourceInfo.linkis_datasource_envs,
        linkis_datasource_name: datasourceInfo?.linkis_datasource_name,
        linkis_datasource_type: datasourceInfo.linkis_datasource_type,
        linkis_datasource_version_id: datasourceInfo?.linkis_datasource_version_id,
        linkis_datasource_id: datasourceInfo.linkis_datasource_id,
        type: datasourceInfo.type,
        datasourceIndex: datasourceInfo?.datasourceIndex,
        proxy_user: datasourceInfo.proxy_user,
        filter: datasourceInfo.filter,
        file_id: data.datasource[0]?.file_id,
        file_table_desc: data.datasource[0]?.file_table_desc,
        file_hash_values: data.datasource[0]?.file_hash_values,
        file_delimiter: data.datasource[0]?.file_delimiter,
        file_header: data.datasource[0]?.file_header,
        file_type: data.datasource[0]?.file_type,
        black_list: data.datasource[0]?.black_list,
        col_names: data.datasource[0]?.col_names,
    };
    if (datasourceInfo.type === 'fps') {
        postDatasource.file_table_desc = columnData2Str(postDatasource.file_table_desc);
    }
    const body = {
        alarm: true,
        execution_parameters_name,
        rule_type: data.rule_type,
        rule_id: data.rule_id,
        rule_name: data.rule_name || '',
        cn_name: data.cn_name || '',
        reg_rule_code: data.reg_rule_code || '',
        rule_detail: data.rule_detail || '', // 增加详情
        rule_template_id: data.rule_template_id,
        datasource: [postDatasource],
        template_arguments: (data?.ruleArgumentList || []).map(({
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
    };
    // 表行数检测，数值范围检测，逻辑类检测，数值范围新值检测时不需要校验字段，传递空校验字段数组
    if ([3, 12, 14, 2860].indexOf(data.rule_template_id) > -1) {
        body.datasource[0].col_names = [];
    }

    if (data.rule_type === 4) {
        // 文件校验
        body.file_alarm_variable = (data.alarm_variable || []).map(v => ({
            rule_metric_id: v.rule_metric_id,
            rule_metric_en_code: v.rule_metric_en_code,
            rule_metric_name: v.rule_metric_id ? null : v.rule_metric_name,
            output_meta_name: v.output_meta_name,
            output_meta_id: v.output_meta_id,
            file_output_unit: v.file_output_unit,
            check_template: v.check_template,
            compare_type: v.compare_type,
            threshold: v.threshold,
            delete_fail_check_result: v.delete_fail_check_result,
            upload_abnormal_value: v.upload_abnormal_value,
            upload_rule_metric_value: v.upload_rule_metric_value,
        }));
    } else {
        // 单表校验
        body.alarm_variable = (data.alarm_variable || []).map(v => ({
            output_meta_id: v.output_meta_id,
            check_template: v.check_template,
            compare_type: v.compare_type,
            threshold: v.threshold,
            rule_metric_id: data.alarm_variable[0].rule_metric_id,
            rule_metric_en_code: data.alarm_variable[0].rule_metric_en_code,
            rule_metric_name: data.alarm_variable[0].rule_metric_id ? null : data.alarm_variable[0].rule_metric_name,
        }));
    }

    // 枚举值的情况下选择了标准值
    body.standard_value = data.standard_value;
    // body.standard_value_version_id = data.standard_value ? +data.ruleArgumentList[0]?.argument_value : null;
    // 枚举新值监控->argument_type 为 39，此时argument_value为标准值的edition_id，赋值给standard_value_version_id
    body.standard_value_version_id = data.standard_value ? data.ruleArgumentList.find(e => e?.argument_type === 39)?.argument_value : null;
    // 在dss里时的额外参数
    if (currentProject.value.isWorkflowProject) {
        const contextID = JSON.parse(route.query?.contextID || '{}');
        const workflowInfo = JSON.parse(contextID?.value || '{}');
        const nodeName = route.query?.nodeName || '';
        body.work_flow_name = body.work_flow_name || workflowInfo.flow || '';
        body.work_flow_space = workflowInfo.workspace || '';
        body.work_flow_version = workflowInfo.version;
        body.node_name = body.node_name || nodeName || '';
        body.cs_id = isUpStream.value ? route.query?.contextID || '{}' : null;
    }
    return body;
};

// 编辑规则
const editRule = async () => {
    try {
        const res = await lockRule({ rule_lock_id: ruleData.value.currentProject.groupId, rule_lock_range: 'table_group_rules' });
        if (res) {
            if (!judgeIsVerifyObjectCreated()) return;

            store.commit('rule/setCurrentProject', {
                groupRuleEditMode: 'edit',
            });
        }
    } catch (error) {
        console.log(error);
    }
};

const getNewRuleItem = () => {
    const timestamp = Date.now();
    return {
        timestamp,
        datasource: ruleData.value.currentRuleDetail.datasource,
        alarm_variable: [{}],
        rule_type: selectRuleType.value,
    };
};

// 添加规则
const addRule = async (index = 0, rule = null) => {
    if (!judgeIsVerifyObjectCreated()) return;

    store.commit('rule/setCurrentProject', {
        groupRuleEditMode: 'edit',
    });
    const newRule = rule || getNewRuleItem();
    ruleList.value.splice(index, 0, newRule);
    await nextTick();
    ruleListForm.value[index].init();
};

const selectAddRule = (type) => {
    selectRuleType.value = type;
    addRule();
};

const addFileRule = async (index = 0, rule = null) => {
    if (!judgeIsVerifyObjectCreated()) return;

    store.commit('rule/setCurrentProject', {
        groupRuleEditMode: 'edit',
    });
    const newRule = rule || getNewRuleItem('file');
    ruleList.value.splice(index, 0, newRule);
    await nextTick();
    ruleListForm.value.forEach((item) => {
        item.init();
    });
};

// 保存规则
const saveRules = async () => {
    try {
        if (isSaveLoading.value) return;
        const result = await Promise.all(ruleListForm.value.map(item => item.valid()));
        if (result.includes(false)) {
            FMessage.warning($t('_.规则校验未通过，请检查是否有字段不符合要求或为空'));
            return;
        }
        ruleList.value.forEach((rule) => {
            rule.whetherVerify = (rule.upload_abnormal_value || rule.upload_rule_metric_value) && !rule.alarm_variable[0]?.rule_metric_name;
            console.log(rule.whetherVerify, '校验结果保存');
        });
        if (ruleList.value.find(item => item.whetherVerify === true)) return FMessage.error($t('_.指标未配置，上报开关无效'));
        isSaveLoading.value = true;
        const res = await FRequest('api/v1/projector/rule/group/rules/modify', {
            project_id: ruleData.value.currentProject.projectId,
            rule_group_id: ruleData.value.currentProject.groupId,
            rule_list: ruleList.value.map(item => convertData(item)),
            page: pagination.value.page - 1,
            size: pagination.value.size,
        });
        FMessage.success($t('_.保存成功'));

        isSaveLoading.value = false;

        store.commit('rule/setCurrentProject', {
            groupRuleEditMode: 'display',
        });

        loadRuleList();
    } catch (err) {
        isSaveLoading.value = false;
        console.warn(err);
    }
};

// 取消保存
const cancel = async () => {
    try {
        await unLockRule({ rule_lock_id: ruleData.value.currentProject.groupId, rule_lock_range: 'table_group_rules' });
        await loadRuleList();
        store.commit('rule/setCurrentProject', {
            groupRuleEditMode: 'display',
        });
    } catch (error) {
        console.log(error);
    }
};

// 复制规则
const copyRule = async (index, rule) => {
    const ruleID = rule?.rule_id;
    store.commit('rule/setCurrentProject', {
        groupRuleEditMode: 'edit',
    });
    const targetRule = cloneDeep(ruleList.value.find(item => item.rule_id === ruleID));
    targetRule.timestamp = Date.now();
    targetRule.rule_name = `${targetRule.rule_name}_copy`;
    targetRule.cn_name = (targetRule.cn_name && targetRule.cn_name !== '') ? `${targetRule.cn_name}_copy` : '';
    targetRule.rule_id = '';
    addRule(0, targetRule);
};

// 删除规则
const deleteRule = async (index, rule, isFileRule = false) => {
    try {
        const ruleID = rule?.rule_id;
        if (ruleID) {
            await FRequest(`api/v1/projector/rule${isFileRule ? '/file' : ''}/delete`, {
                rule_id: ruleID,
            });
        }
        ruleList.value.splice(index, 1);
        ruleListForm.value.splice(index, 1);
        await nextTick();

        if (ruleID) {
            FMessage.success($t('_.删除成功'));
        }
    } catch (e) {
        console.warn(e);
    }
};
// 执行单个规则(wip)
const executeRule = async (index, rule, isFileRule = false) => {
    executeRuleId.value = rule.rule_id;
    const { rule_id: ruleId, rule_group_id: ruleGroupId, rule_name: ruleName } = rule;
    const {
        project_id: projectId,
    } = ruleData.value.currentRule;
    executeTaskPre({
        project_id: projectId,
        rule_details: [{
            rule_id: ruleId,
            rule_group_id: ruleGroupId,
            rule_name: ruleName,
        }],
        disabled: true,
    });
};

const confirmDeleteRule = (index, rule, isFileRule = true) => {
    if (!rule.rule_id) {
        deleteRule(index, rule, isFileRule);
        return;
    }
    FModal.confirm({
        title: $t('_.警告'),
        content: `确认删除规则${rule?.rule_name}?`,
        okText: $t('_.确认'),
        onOk() {
            deleteRule(index, rule, isFileRule);
        },
    });
};

// 分页操作
const onPageChange = () => {
    if (editMode.value === 'edit') {
        FMessage.warning($t('_.请先保存当前数据再进行页面跳转操作'));
        pagination.value.page = paginationCache.value.page;
        pagination.value.size = paginationCache.value.size;
        return;
    }
    paginationCache.value.page = pagination.value.page;
    paginationCache.value.size = pagination.value.size;
    loadRuleList();
};

// 重置筛选条件
const resetFilterQueryForm = () => {
    eventbus.emit('RESET_FILTER_FORM');
};
// eslint-disable-next-line no-restricted-globals
const isEmbedInDMS = computed(() => self.location.href.includes('microApp/dqm'));

watch(filterQueryData.value, () => {
    filtrateCount.value = Object.values(filterQueryData.value).filter(v => v?.toString().length > 0).length;
}, { immediate: true });

// 点击筛选，弹出筛选信息框
const onfilterRule = () => {
    if (!ruleData.value.currentProject.groupId) {
        FMessage.warning($t('_.请保存校验对象后再进行该操作'));
        return;
    }

    if (editMode.value === 'edit') {
        FMessage.warning($t('_.请先保存当前数据'));
        return;
    }
    filterQueryDataCopy.value = cloneDeep(filterQueryData.value);
    showFilterSearchModal.value = true;
};

// 搜索筛选数据
const onSearchFilterData = () => {
    console.log(filterQueryData.value);
    showFilterSearchModal.value = false;
    // 重制参数
    pagination.value.page = DEFAULT_PAGE_NUM;
    pagination.value.size = DEFAULT_PAGE_SIZE;
    loadRuleList();
};

// 取消筛选
const cancelSearchFilterData = () => {
    Object.assign(filterQueryData.value, filterQueryDataCopy.value);
    showFilterSearchModal.value = false;
};


// 数据源修改，需要更新 rule
useListener('IS_VERIFY_RULE_DATA_NEED_RELOAD', () => {
    loadRuleList();
});

// 查看新的校验值后需要切换规则为编辑模式
useListener('openFormEditEvent', editRule);
onMounted(async () => {
    // ruleMetricList.value = await getRuleMetricAll();
});
const fpsColumns = ref([]);
useListener('UPDATE_COLUMN_LIST_ON_SAVE', (data) => {
    console.log('UPDATE_COLUMN_LIST_ON_SAVE:', data);
    fpsColumns.value = data;
});

</script>
