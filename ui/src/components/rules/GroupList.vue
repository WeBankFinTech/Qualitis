<template>
    <div class="wd-content rule-detail">
        <!-- 组名编辑 -->
        <FormDetailHeader />
        <!-- 校验对象模块 -->
        <GroupVerifyObject />
        <!-- 校验规则模块 -->
        <GroupVerifyRules />
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
import { cloneDeep } from 'lodash-es';
import { useRoute, request as FRequest } from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import { useListener, cancelEvent } from '@/components/rules/utils';
import { useFormEditIntercept } from '@/components/rules/hook/useFormEditIntercept';
import useRuleBasicData from '@/hooks/useRuleBasicData';
import FormDetailHeader from '@/components/rules/FormDetailHeader.vue';
import GroupVerifyObject from '@/components/rules/forms/groupListForms/groupVerifyObject.vue';
import GroupVerifyRules from '@/components/rules/forms/groupListForms/groupVerifyRules.vue';

const route = useRoute();
const query = computed(() => route.query);

const store = useStore();
const ruleData = computed(() => store.state.rule);


// 用于拦截用户编辑中途退出
useFormEditIntercept(ruleData.value);

provide('upstreamParams', computed(() => route.query));

const {
    proxyUserList,
    updateProxyUserList,
    clusterList,
    loadClusterList,
} = useRuleBasicData();

provide('proxyUserList', computed(() => proxyUserList.value || []));
provide('clusterList', computed(() => clusterList.value || []));

/**
 * 如果 url 中没有 ruleGroupId ，说明是创建入口
 * 根据是否是创建状态修改 groupObjectEditMode
 */
watch(
    () => query,
    () => {
        if (!query.value.ruleGroupId || query.value.ruleGroupId === 'undefined') {
            // 没有ruleGroupId的时候才设置成create模式
            store.commit('rule/setCurrentProject', {
                groupObjectEditMode: 'create',
            });
        }
    }, { immediate: true },
);

async function init() {
    // 初始化填入数据逻辑
    // 取路由参数逻辑
    // 填入数据时exception特殊处理逻辑
    const {
        ruleGroupId, id: ruleId, projectId: id, workflowProject,
    } = query.value;
    store.commit('rule/setCurrentProject', {
        projectId: id,
        isWorkflowProject: workflowProject === 'true',
        isNormalProject: !(workflowProject === 'true'),
    });
    // dss的url参数没有ruleid，url参数里面给的可能是字符串undefined
    if (ruleGroupId && ruleGroupId !== 'undefined') {
        store.commit('rule/setCurrentProject', {
            groupId: ruleGroupId,
            groupObjectEditMode: 'display',
            groupRuleEditMode: 'display',
        });
    }
}

// 获取校验对象的数据
const loadVerifyObjectData = async () => {
    try {
        // 创建状态
        if (!query.value.ruleGroupId || query.value.ruleGroupId === 'undefined') {
            return;
        }

        const {
            datasource: datasourceList = [], rule_group_name, context_service: isUpStream = false, cs_id: contextId = '', node_name: nodeName = '',
        } = await FRequest(`api/v1/projector/rule/group/${route.query.ruleGroupId}`, {}, {
            method: 'get',
        });

        await nextTick();

        const targetTempData = {};
        const tempData = cloneDeep(datasourceList || null);
        targetTempData.datasource = tempData;
        targetTempData.isUpStream = isUpStream;
        targetTempData.cs_id = contextId;
        targetTempData.rule_name = nodeName;
        store.commit('rule/setCurrentProject', {
            groupName: rule_group_name,
        });
        store.commit('rule/updateCurrentRuleDetail', targetTempData);
        // 发出初始化数据事件，通知 verifyObject 组件重新渲染数据
        eventbus.emit('IS_RULE_DETAIL_DATA_LOADED');
        // 通知verifyRule渲染数据
        eventbus.emit('IS_VERIFY_RULE_DATA_NEED_RELOAD');
    } catch (e) {
        console.warn(e);
    }
};


// 监听校验对象取消编辑事件，重新拉取数据
useListener(cancelEvent, () => {
    loadVerifyObjectData();
});


onMounted(() => {
    init();
    loadVerifyObjectData();
    updateProxyUserList();
    loadClusterList();
});

onUnmounted(() => {
    // 清空store
    store.commit('rule/clearCurrentRuleData');
    store.commit('rule/setVerifyColumns', []);
});
</script>
