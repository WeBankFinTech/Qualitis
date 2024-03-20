<template>
    <div>
        <verifyObject :ref="(e) => ele.verifyObjectRef = e" type="single" :isCustomSql="true" />
        <verifyRule :ref="(e) => ele.verifyRuleRef = e" />
    </div>
</template>
<script setup>
import {
    computed, ref, onMounted, inject, provide, unref,
} from 'vue';
import { useStore } from 'vuex';
import {
    request, useRouter, useI18n, useRoute,
} from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import { FMessage } from '@fesjs/fes-design';
import { NUMBER_TYPES, DWSMessage, handleExecuteParams } from '@/common/utils';
import {
    saveEvent, useListener, delEvent, cancelEvent, columnData2Str, forceFormPageUpdateEvent, getConfiguredRuleMetric, replaceRouter, getRuleMetricAll,
} from '@/components/rules/utils';
import { MAX_PAGE_SIZE } from '@/assets/js/const';
import {
    getColumns,
} from '@/components/rules/utils/datasource';

import verifyObject from '@/components/rules/VerifyObject';
import { cloneDeep } from 'lodash-es';
import verifyRule from './verifyRule';

const store = useStore();
const route = useRoute();
const router = useRouter();
const { t: $t } = useI18n();

const ruleData = computed(() => store.state.rule);

const upstreamParams = cloneDeep(unref(inject('upstreamParams')));
provide('groupType', 'sqlVerification');
provide('ruleType', '2-2');
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

        const currentRuleDetail = await request(`api/v1/projector/rule/custom/${unref(ruleData.value.currentRule.rule_id)}`, {}, 'get');
        // 处理上游表逻辑
        currentRuleDetail.isUpStream = currentRuleDetail.context_service;
        if (currentRuleDetail.context_service && currentRuleDetail.cs_id) {
            // upstreamParams.nodeName = currentRuleDetail.rule_name;
            upstreamParams.contextID = currentRuleDetail.cs_id;
        }

        configuredRuleMetric.value = getConfiguredRuleMetric(currentRuleDetail);
        console.log(configuredRuleMetric.value, '已经配置的指标');
        // 将数据源相关数据载入datasource[0]中
        const {
            cluster_name,
            file_db,
            file_delimiter,
            file_hash_values,
            file_header,
            file_id,
            file_table_desc,
            file_type,
            fps_file,
            proxy_user,
            file_table,
            type,
            isUpStream,
            linkis_datasource_envs,
            linkis_datasource_version_id,
            linkis_datasource_id,
            linkis_datasource_name,
            linkis_datasource_type,
            linkis_datasource_envs_mappings,
        } = currentRuleDetail;
        currentRuleDetail.datasource = [{
            cluster_name,
            db_name: file_db,
            file_delimiter,
            file_hash_values,
            file_header,
            file_id,
            file_table_desc,
            file_type,
            fps_file,
            proxy_user,
            table_name: file_table,
            isUpStream,
            type,
            linkis_datasource_envs,
            linkis_datasource_version_id,
            linkis_datasource_id,
            linkis_datasource_name,
            linkis_datasource_type,
            linkis_datasource_envs_mappings,
        }];
        // 更新store
        console.log('init currentRuleDetail', currentRuleDetail);
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

        const {
            rule_id,
            rule_name,
            cn_name,
            rule_detail,
            datasource,
            alarm_variable,
            execution_parameters_name,
            sql_check_area,
            isUpStream,
            linkis_udf_names,
        } = cloneDeep(ruleData.value.currentRuleDetail);
        // 展开datasource
        const {
            cluster_name,
            db_name,
            file_delimiter,
            file_hash_values,
            file_header,
            file_id,
            file_table_desc,
            file_type,
            fps_file,
            proxy_user,
            table_name,
            type,
            linkis_datasource_type,
            linkis_datasource_id,
            linkis_datasource_envs,
            linkis_datasource_name,
            linkis_datasource_version_id,
            linkis_datasource_envs_mappings,
        } = datasource[0];
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
            alarm: true,
            execution_parameters_name,
            cluster_name,
            file_db: db_name,
            file_delimiter,
            file_hash_values,
            file_header,
            file_id,
            file_table_desc,
            file_type,
            fps_file,
            proxy_user,
            file_table: table_name,
            type,
            linkis_datasource_type,
            linkis_datasource_id,
            linkis_datasource_envs,
            linkis_datasource_name,
            linkis_datasource_version_id,
            linkis_datasource_envs_mappings,
            sql_check_area,
            linkis_udf_names,
            save_mid_table: false,
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
            })), //
            // TODO
            template_arguments: (datasource.ruleArgumentList || []).map(({
                argument_step,
                argument_id,
                argument_value,
                argsSelectList,
                flag,
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
                };
            }),
            // ...handleExecuteParams(executionParams.value),
        };
        if (body.type.toLowerCase() !== 'fps') {
            body.fps_file = false;
            body.file_id = '';
            body.file_hash_values = '';
            body.file_delimiter = '';
            body.file_header = null;
            body.file_db = '';
            body.file_table = '';
            body.file_table_desc = '';
            body.file_type = '';
        } else {
            body.file_table_desc = columnData2Str(body.file_table_desc);
        }
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
        const resp = await request(`api/v1/projector/rule/custom/${action}`, body);
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
        store.commit('rule/setSaving', false);
        console.warn(err);
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
        const resp = await request('api/v1/projector/rule/custom/delete', {
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
