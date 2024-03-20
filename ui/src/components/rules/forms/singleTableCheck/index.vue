<template>
    <div>
        <verifyObject :ref="(e) => ele.verifyObjectRef = e" type="single" />
        <verifyRule :ref="(e) => ele.verifyRuleRef = e" />
    </div>
</template>
<script setup>
import {
    computed, ref, onMounted, inject, provide, unref,
} from 'vue';
import { useStore } from 'vuex';
import {
    request, useI18n, useRoute, useRouter,
} from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import eventbus from '@/common/useEvents';
import { FMessage } from '@fesjs/fes-design';
import { DWSMessage } from '@/common/utils';
import { TEMPLATE_ARGUMENT_INPUT_TYPE } from '@/common/constant';
import {
    saveEvent, useListener, delEvent, cancelEvent, columnData2Str, forceFormPageUpdateEvent, getConfiguredRuleMetric, replaceRouter, getRuleMetricAll,
} from '@/components/rules/utils';
import {
    getColumns,
} from '@/components/rules/utils/datasource';

import verifyObject from '@/components/rules/VerifyObject';
import verifyRule from './verifyRule';

const store = useStore();
const route = useRoute();
const router = useRouter();
const { t: $t } = useI18n();

const ruleData = computed(() => store.state.rule);

const upstreamParams = cloneDeep(unref(inject('upstreamParams')));

provide('groupType', 'newSingleTableRule');
provide('ruleType', '1-1');
const ruleMetricList = ref([]);
provide('ruleMetricList', computed(() => ruleMetricList.value));

// 组件ref
const ele = ref({
    verifyObjectRef: null,
    verifyRuleRef: null,
});

// 加载规则详情
const configuredRuleMetric = ref({});
provide('configuredRuleMetric', computed(() => configuredRuleMetric.value));
const loadRuleDetail = async () => {
    try {
        ruleMetricList.value = await getRuleMetricAll();
        if (!unref(ruleData.value.currentRule.rule_id)) {
            return;
        }

        const currentRuleDetail = await request(`api/v1/projector/rule/${unref(ruleData.value.currentRule.rule_id)}`, {}, 'get');
        // 处理上游表逻辑
        currentRuleDetail.isUpStream = currentRuleDetail.context_service;
        if (currentRuleDetail.context_service && currentRuleDetail.cs_id) {
            // upstreamParams.nodeName = currentRuleDetail.rule_name;
            upstreamParams.contextID = currentRuleDetail.cs_id;
        }

        configuredRuleMetric.value = getConfiguredRuleMetric(currentRuleDetail);

        // 更新store
        store.commit('rule/setCurrentRuleDetail', currentRuleDetail);

        // 发出初始化数据事件
        eventbus.emit('IS_RULE_DETAIL_DATA_LOADED');
    } catch (err) {
        console.warn(err);
    }
};

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
        const results = await Promise.all(Object.keys(refs).map(r => refs[r].valid()));
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

        // 用store的最新数提交
        const {
            rule_id,
            rule_name,
            cn_name,
            rule_detail,
            rule_template_id,
            rule_template_name,
            datasource,
            alarm_variable,
            ruleArgumentList,
            execution_parameters_name,
            standard_value: standardValue,
            colNamesStrArr,
            isUpStream,
            isFilterFields,
            filterColNamesStrArr,
        } = cloneDeep(ruleData.value.currentRuleDetail);

        // delete dataSourceValue.fpsData;
        let body = {
            work_flow_name: upstreamParams.workflowName,
            work_flow_version: upstreamParams.workflowVersion,
            project_id: projectId,
            rule_group_id: groupId,
            rule_group_name: groupName,
            rule_id,
            rule_name,
            cn_name,
            rule_detail,
            rule_template_id,
            rule_template_name,
            datasource,
            alarm: true,
            alarm_variable: alarm_variable.map(v => ({
                rule_metric_id: alarm_variable[0].rule_metric_id,
                rule_metric_en_code: alarm_variable[0].rule_metric_en_code,
                rule_metric_name: alarm_variable[0].rule_metric_id ? null : alarm_variable[0].rule_metric_name,
                output_meta_id: v.output_meta_id,
                check_template: v.check_template,
                compare_type: v.compare_type,
                threshold: v.threshold,
            })),
            execution_parameters_name,
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
        };

        // 在dss里时的额外参数
        if (isWorkflowProject) {
            const contextID = JSON.parse(route.query?.contextID || '{}');
            const workflowInfo = JSON.parse(contextID?.value || '{}');
            const nodeName = route.query?.nodeName || '';
            body.work_flow_name = body.work_flow_name || workflowInfo.flow || '';
            body.work_flow_space = workflowInfo.workspace || '';
            body.node_name = body.node_name || nodeName || '';
        }

        if (body.datasource[0].type.toLowerCase() === 'fps') {
            // 将表结构拼接成后台需要的结构(由于利用了对象引用值类型的特性，需要进行一次深拷贝)
            // TODO
            const fpsData = JSON.parse(JSON.stringify(datasource || {}))[0];
            const fpsParams = {
                file_id: fpsData.file_id,
                file_hash_values: fpsData.file_hash_values,
                file_db: fpsData.db_name,
                file_table: fpsData.table_name,
                file_table_desc: columnData2Str(fpsData.file_table_desc),
                file_delimiter: fpsData.file_delimiter,
                file_type: fpsData.file_type,
                file_header: fpsData.file_header,
            };
            body.datasource[0].db_name = fpsData.db_name;
            body.datasource[0].table_name = fpsData.table_name;
            body.datasource[0] = Object.assign({}, body.datasource[0], fpsParams);
            body = Object.assign({}, body, fpsParams);
        } else {
            body.datasource[0].fps_file = false;
            body.datasource[0].file_id = '';
            body.datasource[0].file_hash_values = '';
            body.datasource[0].file_delimiter = '';
            body.datasource[0].file_header = null;
            body.datasource[0].file_db = '';
            body.datasource[0].file_table = '';
            body.datasource[0].file_table_desc = '';
            body.datasource[0].file_type = '';
        }
        // col_names兜底必须是空数组，不能是null
        body.datasource[0].col_names = body.datasource[0].col_names || [];
        upstreamParams.isUpStream = isUpStream;

        // 如果有过滤字段，需要处理
        if (isFilterFields) {
            const allColumns = await getColumns({
                clusterName: datasource[0].cluster_name,
                proxyUser: datasource[0].proxy_user,
                dataSourceType: datasource[0].type,
                dataSourceId: datasource[0].linkis_datasource_id,
                dbName: datasource[0].db_name,
                tableName: datasource[0].table_name,
                upstreamParams,
            });
            body.datasource[0].col_names = allColumns.filter(item => !filterColNamesStrArr.includes(item.column_name))
                .filter(item => item && item.column_name && item.data_type)
                .map(item => ({ column_name: item.column_name, data_type: item.data_type }));
        }
        // 模版包含标准值
        if (standardValue) {
            const standardVal = ruleArgumentList.find(item => item.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.STANDARD_VALUE)?.argument_value || '';
            body.standard_value_version_id = +standardVal || '';
        }
        if (isUpStream) {
            // 开启上游表
            body.cs_id = upstreamParams.contextID;
            body.node_name = upstreamParams.nodeName;
            body.nodeId = upstreamParams.nodeId;
        }
        const action = editMode === 'create' ? 'add' : 'modify';
        console.log('handleExecuteParams-last-body', body);
        const resp = await request(`api/v1/projector/rule/${action}`, body);
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
        isSaving = false;
        if (cb) {
            cb();
        }
    } catch (err) {
        isSaving = false;
        console.warn(err);
        store.commit('rule/setSaving', false);
    }
});

// 删除规则
let isDeleting = false;
useListener(delEvent, async (cb) => {
    try {
        if (isDeleting) {
            return;
        }
        isDeleting = true;
        const resp = await request('api/v1/projector/rule/delete', {
            rule_id: ruleData.value.currentRuleDetail.rule_id,
        }, 'post');
        console.log('del!', resp);
        cb();
    } catch (err) {
        console.warn(err);
        isDeleting = false;
    }
});

// 取消事件
useListener(cancelEvent, () => {
    loadRuleDetail();
});

onMounted(() => {
    loadRuleDetail();
});
</script>
