<template>
    <div>
        <verifyObject ref="verifyObjectRef" />
        <verifyRule ref="verifyRuleRef" />
    </div>
</template>
<script setup>
import {
    useI18n,
    request,
    useRouter,
} from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import {
    computed, ref, onMounted, inject, provide, unref, nextTick,
} from 'vue';
import { useStore } from 'vuex';
import eventbus from '@/common/useEvents';
import { FMessage } from '@fesjs/fes-design';
import {
    saveEvent, useListener, ruleTypeChangeEvent, delEvent, cancelEvent, forceFormPageUpdateEvent, dataSourceTypeList, getDataSourceType,
} from '@/components/rules/utils';
import verifyObject from './verifyObject';
import verifyRule from './verifyRule';

const store = useStore();
const router = useRouter();
const { t: $t } = useI18n();

const ruleData = computed(() => store.state.rule);
provide('groupType', 'crossDatabaseFullVerification');
provide('ruleType', '3-2');
const verifyObjectRef = ref(null);
const verifyRuleRef = ref(null);

// 加载规则详情
const loadRuleDetail = async () => {
    try {
        if (!unref(ruleData.value.currentRule.rule_id)) {
            return;
        }

        const currentRuleDetail = await request(`api/v1/projector/rule/${unref(ruleData.value.currentRule.rule_id)}`, {}, 'get');
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
        const results = await Promise.all([verifyObjectRef.value.valid(), verifyRuleRef.value.valid()]);

        console.log(results);
        if (results.includes(false)) {
            isSaving = false;
            store.commit('rule/setSaving', false);
            return;
        }

        const {
            projectId,
            groupId,
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
            black_list,
            white_list,
            cluster_name,
            source,
            target,
            contrast_type,
            filterSettingList,
            execution_parameters_name,
        } = cloneDeep(ruleData.value.currentRuleDetail);
        console.log('filterSettingList', filterSettingList);
        // eslint-disable-next-line camelcase
        const filter_list = filterSettingList.map(item => ({
            filter_column_list: Array.from(new Set([...item.source_filter_column_list, ...item.target_filter_column_list])),
            source_filter: item.source_filter,
            source_table: item.source_table,
            target_filter: item.target_filter,
            target_table: item.target_table,
        }));

        const body = {
            black_list: black_list.map(item => `${item.filterWay}:${item.filterContent}`),
            white_list: white_list.map(item => `${item.sourceName}:${item.targetName}`),
            contrast_type,
            cluster_name,
            execution_parameters_name,
            filter_list,
            project_id: projectId,
            proxy_user: source.proxy_user,
            rule_name,
            cn_name,
            rule_detail,
            source_db: source.db_name,
            source_linkis_datasource_type: source.type,
            target_db: target.db_name,
            target_linkis_datasource_type: target.type,
        };
        console.log('body', body);
        const action = editMode === 'create' ? 'add' : 'modify';
        console.log('handleExecuteParams-last-body', body);
        const resp = await request('api/v1/projector/meta_data/mul_db', body);
        if (editMode === 'create') {
            // 如果是新建项目，重新刷新页面，ruleId这些数据需要重新初始化，通信比较麻烦
            // workflowProject表示是否是工作流项目
            console.log();
            router.replace(`/projects/detail?projectId=${projectId}`);
            // 上面的url变更并不会通知组件强制刷新
            setTimeout(() => {
                // 通知组件重新渲染
                eventbus.emit(forceFormPageUpdateEvent);
            }, 0);
        } else {
            // 如果是编辑项目需要更新名字，不然tab显示的名字是旧的
            store.dispatch('rule/updateGroupDetailList', { rule_id, rule_name });
        }
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
</script>
<style lang='less' scoped>
</style>
