<template>
    <div>
        <verifyObject :ref="(e) => ele.verifyObjectRef = e" />
        <verifyRule :ref="(e) => ele.verifyRuleRef = e" />
    </div>
</template>
<script setup>
import {
    computed, ref, onMounted, inject, provide, unref, nextTick,
} from 'vue';
import { useStore } from 'vuex';
import {
    request, useI18n, useRoute, useRouter,
} from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import { FMessage } from '@fesjs/fes-design';
import { TEMPLATE_ARGUMENT_INPUT_TYPE } from '@/common/constant';
import { NUMBER_TYPES, DWSMessage, handleExecuteParams } from '@/common/utils';
import {
    saveEvent, useListener, ruleTypeChangeEvent, delEvent, cancelEvent, dataSourceTypeList, getDataSourceType, columnData2Str, forceFormPageUpdateEvent, getConfiguredRuleMetric, replaceRouter, getRuleMetricAll,
} from '@/components/rules/utils';
import { MAX_PAGE_SIZE } from '@/assets/js/const';
import {
    getColumns, getTables,
} from '@/components/rules/utils/datasource';

import { clone, cloneDeep } from 'lodash-es';
import verifyObject from './verifyObject';
import verifyRule from './verifyRule';

const store = useStore();
const route = useRoute();
const router = useRouter();
const { t: $t } = useI18n();

const ruleData = computed(() => store.state.rule);

const upstreamParams = cloneDeep(unref(inject('upstreamParams')));
provide('groupType', 'newMultiTableRule');
provide('ruleType', '3-1');
// 组件ref
const ele = ref({
    verifyObjectRef: null,
    verifyRuleRef: null,
});

// 加载规则详情
const configuredRuleMetric = ref({});
provide('configuredRuleMetric', computed(() => configuredRuleMetric.value));
const ruleMetricList = ref([]);
provide('ruleMetricList', computed(() => ruleMetricList.value));
const loadRuleDetail = async () => {
    try {
        ruleMetricList.value = await getRuleMetricAll();
        if (!unref(ruleData.value.currentRule.rule_id)) {
            return;
        }
        // 后台返回数据不一致，做差异化处理
        // source和target分开处理
        const currentRuleDetail = await request(`api/v1/projector/mul_source_rule/${unref(ruleData.value.currentRule.rule_id)}`, {}, 'get');
        // 处理上游表逻辑
        currentRuleDetail.isUpStream = currentRuleDetail.target.context_service || currentRuleDetail.source.context_service;
        if (currentRuleDetail.isUpStream && currentRuleDetail.cs_id) {
            // upstreamParams.nodeName = currentRuleDetail.rule_name;
            upstreamParams.contextID = currentRuleDetail.cs_id;
        }

        configuredRuleMetric.value = getConfiguredRuleMetric(currentRuleDetail);
        currentRuleDetail.source.cluster_name = currentRuleDetail.cluster_name;
        // 更新store
        store.commit('rule/setCurrentRuleDetail', currentRuleDetail);
        // 发出初始化数据事件
        eventbus.emit('IS_RULE_DETAIL_DATA_LOADED');
    } catch (err) {
        console.warn(err);
    }
};
// 处理FPS数据
function handleFPS(target) {
    if (target.type.toLowerCase() !== 'fps') {
        target.fps_file = false;
        target.file_id = '';
        target.file_hash_values = '';
        target.file_delimiter = '';
        target.file_header = null;
        target.file_db = '';
        target.file_table = '';
        target.file_table_desc = '';
        target.file_type = '';
    } else {
        target.file_table_desc = columnData2Str(target.file_table_desc) || target.file_table_desc;
    }
}
// FPS时要保存源的columns
const fpsCols = ref([]);
useListener('UPDATE_COLUMN_LIST', ({ data, target }) => {
    if (target === 'source') {
        fpsCols.value = data;
    }
});
// 保存事件
// 防重复
let isSaving = false;
// eslint-disable-next-line complexity
useListener(saveEvent, async (cb) => {
    try {
        if (isSaving) {
            return;
        }
        isSaving = true;
        const refs = ele.value;
        eventbus.emit('SHOULD_VALIDATE', true);
        const results = await Promise.all(Object.keys(refs).map(r => refs[r].valid()));
        eventbus.emit('SHOULD_VALIDATE', false);
        console.log(results);
        if (results.includes(false)) {
            isSaving = false;
            store.commit('rule/setSaving', false);
            return;
        }

        const {
            projectId,
            groupId,
            groupName,
            editMode,
            isEmbedInFrame,
            isWorkflowProject,
        } = ruleData.value.currentProject;
        const {
            rule_id,
            rule_name,
            cn_name,
            rule_detail,
            multi_source_rule_template_id,
            rule_template_name,
            source,
            target,
            alarm_variable,
            filter,
            mappings,
            compare_cols,
            contrast_type,
            filter_col_names,
            execution_parameters_name,
            isUpStream,
            isFilterFields,
            ruleArgumentList,
        } = cloneDeep(ruleData.value.currentRuleDetail);
        // eslint-disable-next-line camelcase
        const cluster_name = source.cluster_name;
        // delete dataSourceValue.fpsData;
        const body = {
            work_flow_name: upstreamParams.workflowName,
            work_flow_version: upstreamParams.workflowVersion,
            project_id: projectId,
            rule_group_id: groupId,
            rule_group_name: groupName,
            rule_id,
            rule_name,
            cn_name,
            rule_detail,
            multi_source_rule_template_id,
            rule_template_name,
            source,
            target,
            cluster_name,
            execution_parameters_name,
            alarm: true,
            alarm_variable: alarm_variable.map(v => ({
                rule_metric_id: v.rule_metric_id,
                rule_metric_en_code: v.rule_metric_en_code,
                rule_metric_name: v.rule_metric_id ? null : v.rule_metric_name,
                output_meta_id: v.output_meta_id,
                check_template: v.check_template,
                compare_type: v.compare_type,
                threshold: v.threshold,
                upload_abnormal_value: v.upload_abnormal_value,
                upload_rule_metric_value: v.upload_rule_metric_value,
            })),
            filter,
            contrast_type,
            filter_col_names,
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
                // 连接字段和比对字段需要转换成json字符串
                // eslint-disable-next-line camelcase
                if (argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.CONNECT_FIELDS) {
                    // eslint-disable-next-line camelcase
                    argument_value = JSON.stringify(mappings);
                }

                // eslint-disable-next-line camelcase
                if (argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.COMPARISON_FIELD) {
                    // eslint-disable-next-line camelcase
                    argument_value = JSON.stringify(compare_cols);
                }
                return {
                    argument_step,
                    argument_id,
                    argument_value,
                    argument_type,
                };
            }),
        };
        handleFPS(body.source);
        handleFPS(body.target);
        // filter_col_names兜底必须是空数组，不能是null
        body.filter_col_names = body.filter_col_names || [];
        upstreamParams.isUpStream = isUpStream;
        if (isFilterFields) {
            // 如果有过滤字段控件，col_names需要过滤掉源表字段中列过滤已选中的字段
            if (isUpStream) {
                // 注入contextKey
                const allTables = await getTables({
                    clusterName: body.source.cluster_name,
                    proxyUser: body.source.proxy_user,
                    dataSourceType: body.source.type,
                    dataSourceId: body.source.linkis_datasource_id,
                    dbName: body.source.db_name,
                    upstreamParams,
                });
                const targetTable = allTables.filter(item => item.table_name === body.source.table_name);
                if (targetTable.length > 0) {
                    upstreamParams.contextKey = targetTable[0].context_Key;
                }
            }
            let allColumns;
            if (body.source.type === 'fps') {
                allColumns = cloneDeep(fpsCols.value);
            } else {
                allColumns = await getColumns({
                    clusterName: body.source.cluster_name,
                    proxyUser: body.source.proxy_user,
                    dataSourceType: body.source.type,
                    dataSourceId: body.source.linkis_datasource_id,
                    dbName: body.source.db_name,
                    tableName: body.source.table_name,
                    upstreamParams,
                });
            }
            body.filter_col_names = allColumns.filter(item => !body.filter_col_names.find(v => v === item.column_name))
                .filter(item => item && item.column_name && item.data_type)
                .map(item => ({ column_name: item.column_name, data_type: item.data_type }));
            // mappings.length = 0;
            // compare_cols.length = 0;
            // eslint-disable-next-line camelcase
        } else if (filter_col_names) {
            // eslint-disable-next-line camelcase
            filter_col_names.length = 0;
        }
        delete body.source.cluster_name;
        delete body.target.cluster_name;
        if (isUpStream) {
            // 开启上游表
            body.cs_id = upstreamParams.contextID;
            body.node_name = upstreamParams.nodeName;
            body.nodeId = upstreamParams.nodeId;
        }
        // 在dss里时的额外参数
        if (isWorkflowProject) {
            const contextID = JSON.parse(route.query?.contextID || '{}');
            const workflowInfo = JSON.parse(contextID?.value || '{}');
            const nodeName = route.query?.nodeName || '';
            body.work_flow_name = body.work_flow_name || workflowInfo.flow || '';
            body.work_flow_space = workflowInfo.workspace || '';
            body.node_name = body.node_name || nodeName || '';
        }
        const action = editMode === 'create' ? 'add' : 'modify';
        console.log('handleExecuteParams-last-body', body);
        const resp = await request(`api/v1/projector/mul_source_rule/${action}`, body);
        console.log('saved!', resp);
        if (unref(isEmbedInFrame) && resp.rule_group_id) {
            DWSMessage(upstreamParams.nodeId, resp.rule_group_id, action);
        }
        if (editMode === 'create') {
            // 如果是新建项目，重新刷新页面，ruleId这些数据需要重新初始化，通信比较麻烦
            // workflowProject表示是否是工作流项目
            replaceRouter(resp, isWorkflowProject, route, router);
            // 上面的url变更并不会通知组件强制刷新
            setTimeout(() => {
                // 通知组件重新渲染
                eventbus.emit(forceFormPageUpdateEvent);
            }, 0);
        } else {
            // 如果是编辑项目需要更新名字，不然tab显示的名字是旧的
            store.dispatch('rule/updateGroupDetailList', { rule_id, rule_name });
        }
        // 重新加载数据
        loadRuleDetail();
        const mMessage = unref(editMode === 'create') ? $t('toastSuccess.addSuccess') : $t('toastSuccess.editSuccess');
        FMessage.success(mMessage);
        if (cb) {
            cb();
        }
        isSaving = false;
    } catch (err) {
        isSaving = false;
        console.warn(err);
        store.commit('rule/setSaving', false);
    }
});

// 取消事件
useListener(cancelEvent, () => {
    loadRuleDetail();
});
// 删除规则
let isDeleting = false;
useListener(delEvent, async (cb) => {
    try {
        if (isDeleting) {
            return;
        }
        isDeleting = true;
        const resp = await request('api/v1/projector/mul_source_rule/delete', {
            rule_id: ruleData.value.currentRuleDetail.rule_id,
        }, 'post');
        console.log('del!', resp);
        cb();
    } catch (err) {
        console.warn(err);
        isDeleting = false;
    }
});
onMounted(() => {
    loadRuleDetail();
});
</script>
