<template>
    <div id="ruleFormPage" class="wd-content rule-detail">
        <div v-if="ruleData.isLoading" class="wd-content-body tab-content">
            <BPageLoading :loadingText="{ loading: '' }" />
        </div>
        <div v-else>
            <!-- 组名编辑 -->
            <FormDetailHeader />
            <!-- 头部tab -->
            <ProjectNavBar
                v-if="!(currentRuleType === '3-2')"
                v-model="currentRuleIndex"
                :data="ruleData.ruleList"
                :hint="false"
                :confirm="onNavbarConfirm">
                <!-- 独立运行的情况下
                工作流：只能预览
                普通项目：预览、编辑

                被嵌套使用的情况下
                工作流：预览、编辑、复制
                普通项目：预览、编辑 -->
                <template v-slot:suffix>
                    <FSpace v-if="!(ruleData.currentProject.isWorkflowProject && !ruleData.currentProject.isEmbedInFrame)" style="position:absolute; right:5px">
                        <FDropdown :options="options" @click="select">
                            <FButton type="primary"><PlusCircleOutlined />添加规则</FButton>
                        </FDropdown>
                        <!-- <FButton v-if="ruleData.currentProject.isWorkflowProject" type="info" @click="toggleCopyPanel"><fes-icon type="copy" />复制规则</FButton> -->
                    </FSpace>
                </template>
            </ProjectNavBar>
            <!-- 规则列表 -->
            <div v-if="ruleData.ruleList.length > 0" class="wd-content-body tab-content">
                <ul v-if="!(ruleData.currentProject.isWorkflowProject && !ruleData.currentProject.isEmbedInFrame)" class="wd-body-menus">
                    <li v-if="ruleData.currentProject.editMode === 'display' && !ruleData.currentProject.isEmbedInFrame" class="wd-body-menu-item" @click="runRule">执行规则</li>
                    <li v-if="ruleData.currentProject.editMode === 'display' && ruleData.currentProject.isEmbedInFrame" class="wd-body-menu-item" @click="toggleCopyPanel">复制规则</li>
                    <li v-if="ruleData.currentProject.editMode === 'display'" class="wd-body-menu-item" @click="editRule">编辑</li>
                    <li v-if="ruleData.currentProject.editMode !== 'create'" class="wd-body-menu-item" @click="delRule">删除</li>
                </ul>
                <!-- 单表对比 -->
                <SingleTableCheck v-if="currentRuleType === '1-1'" :key="ruleData.currentRule.rule_id + '1'" />
                <!-- 跨表对比 -->
                <CrossTableCheck v-else-if="currentRuleType === '3-1'" :key="ruleData.currentRule.rule_id + '2'" />
                <!-- 跨库对比 -->
                <CrossDbCheck v-else-if="currentRuleType === '3-2'" :key="ruleData.currentRule.rule_id + '3'" />
                <!-- 多指标 -->
                <SqlVerification v-else-if="currentRuleType === '2-2'" :key="ruleData.currentRule.rule_id + '5'" />
                <!-- 文件校验 -->
                <DocumentCheck v-else-if="currentRuleType === '4-1'" :key="ruleData.currentRule.rule_id + '6'" />
            </div>
            <FSpace v-if="ruleData.currentProject.editMode !== 'display'">
                <FButton size="large" :disabled="ruleData.isSaving" :loading="ruleData.isSaving" class="save" type="primary" @click="saveRule">保存</FButton>
                <FButton size="large" :disabled="ruleData.isSaving" class="cancel" @click="cancel">取消</FButton>
            </FSpace>
        </div>

        <!-- 任务执行 -->
        <!-- TODO: selectDisabled要传true,但是框的功能好像暂时不支持v-model传值 -->
        <ExecRulePanel
            v-model:show="showExecutation"
            v-model="executationConfig"
            :list="executationRules"
            :gid="ruleData.currentRule.rule_id"
            @ok="excuteRuleInProjectDetail"
        />
        <CopyRule v-model:show="showCopyPanel" :projectId="ruleData.currentProject.projectId" :groupId="ruleData.currentProject.groupId" :dssParams="query"></CopyRule>
    </div>
</template>
<script setup>
import {
    ref, computed, onMounted, provide, watch, unref, onUnmounted,
} from 'vue';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusCircleOutlined } from '@fesjs/fes-design/es/icon';
import { useStore } from 'vuex';
import { useRouter, useRoute, request as FRequest } from '@fesjs/fes';
import {
    ruleTypes, DWSMessage, getURLQueryParams,
} from '@/common/utils';
import { cloneDeep } from 'lodash-es';
import eventbus from '@/common/useEvents';
import ProjectNavBar from '@/pages/projects/components/NavBar';
import {
    saveEvent, useListener, ruleTypeChangeEvent, delEvent, cancelEvent,
} from '@/components/rules/utils';
import useExecutation from '@/pages/projects/hooks/useExecutation';
import ExecRulePanel from '@/components/ExecutionConfig/ExecRulePanel';
import { useFormEditIntercept } from '@/components/rules/hook/useFormEditIntercept';
import useRuleBasicData from '@/hooks/useRuleBasicData';
import { lockRule, unLockRule } from '@/components/rules/api/index';
import { BPageLoading } from '@fesjs/traction-widget';
import CopyRule from './CopyRule.vue';
import FormDetailHeader from './FormDetailHeader.vue';
import CrossTableCheck from './forms/crossTableCheck/index.vue';
import CrossDbCheck from './forms/crossDbCheck/index.vue';
import DocumentCheck from './forms/documentCheck/index.vue';
import SingleTableCheck from './forms/singleTableCheck/index.vue';
import SqlVerification from './forms/sqlVerification/index.vue';

const route = useRoute();
const query = computed(() => route.query);
const tpl = () => query.value.tpl;
const router = useRouter();

const store = useStore();

const ruleData = computed(() => store.state.rule);

// 用于拦截用户编辑中途退出
useFormEditIntercept(ruleData.value);

provide('upstreamParams', computed(() => route.query));

const currentRuleType = computed(() => `${ruleData.value.currentRule?.rule_type}-${ruleData.value.currentRule?.table_type || '1'}`);
provide('currentRuleType', currentRuleType);
useListener(ruleTypeChangeEvent, (typeId) => {
    console.log('ruleTypeChangeEvent', typeId);
    const [rule, table] = typeId.split('-').map(v => parseInt(v));
    store.dispatch('rule/updateCurrentRule', {
        rule_type: rule,
        table_type: table,
    });
});

const currentRuleIndex = ref(-1);

// 可选规则类型
const options = ref([{
    value: 'newSingleTableRule',
    label: '单表校验',
}, {
    value: 'newMultiTableRule',
    label: '多表比对',
}, {
    value: 'documentVerification',
    label: '表文件校验',
}, {
    value: 'sqlVerification',
    label: '自定义SQL校验',
}]);

async function saveRule() {
    console.log('普通保存', (ruleData.value.currentRule.rule_type === 1 || ruleData.value.currentRule.rule_type === 2) && ruleData.value.currentRuleDetail?.whetherVerify);
    if (ruleData.value.currentRuleDetail?.whetherVerify) return FMessage.error('指标未配置，上报开关无效');

    if (ruleData.value.isSaving) {
        return;
    }
    store.commit('rule/setSaving', true);
    console.log('保存');
    eventbus.emit(saveEvent, async () => {
        if (currentRuleType.value === '3-2') {
            // 跨库对比的话直接将新建的tab删除
            store.dispatch('rule/deleteDataFromGroupDetailList');
        }
        store.commit('rule/setCurrentProject', {
            editMode: 'display',
        });
        store.commit('rule/setSaving', false);
    });
}

function reset() {
    // 当没有规则的时候需要增加兜底，不然会报错
    const targetRule = cloneDeep(unref(ruleData.value.ruleList[currentRuleIndex.value])) || {};
    store.commit('rule/setCurrentRule', targetRule);
}

// 更新当前的编辑规则
watch(currentRuleIndex, () => {
    reset();
});


const resetTabs = () => {
    store.dispatch('rule/deleteDataFromGroupDetailListByIndex', currentRuleIndex.value);
    const newIndex = currentRuleIndex.value + 1;
    currentRuleIndex.value = ruleData.value.ruleList.length > 0 ? (newIndex > ruleData.value.ruleList.length ? ruleData.value.ruleList.length - 1 : newIndex) : 0;
};

const delRule = () => {
    const {
        groupId,
        isEmbedInFrame,
    } = ruleData.value.currentProject;
    FModal.confirm({
        title: '警告',
        content: `确认删除规则${ruleData.value.currentRule.rule_name}?`,
        okText: '确认',
        onOk() {
            console.log('modal ok');
            eventbus.emit(delEvent, () => {
                store.commit('rule/setCurrentProject', {
                    editMode: 'display',
                });
                if (isEmbedInFrame) {
                    if (ruleData.value.ruleList.length - 1 <= 0) {
                        DWSMessage(route.query.nodeId, groupId, 'delete');
                    }
                }
                // 已经请求过删除接口，如果能成功执行这里的话，前端直接删除dom，节省一个请求
                resetTabs();
            });
        },
    });
};

const cancel = (leaveFlag) => {
    console.log('取消');
    return new Promise((resolve, reject) => {
        FModal.confirm({
            title: '警告',
            content: leaveFlag === 'leave' ? '即将退出正在编辑的规则，是否保存规则' : '您处于编辑状态，离开后数据将不会被保存',
            okText: leaveFlag === 'leave' ? '保存' : '确认离开',
            cancelText: leaveFlag === 'leave' ? '不保存' : '取消',
            closable: true,
            async onOk() {
                try {
                    await unLockRule({ rule_lock_id: ruleData.value.currentRule.rule_id, rule_lock_range: 'rule' });
                } catch (error) {
                    console.log(error);
                }
                if (leaveFlag === 'leave') {
                    saveRule();
                    return reject();
                }
                if (ruleData.value.currentProject.editMode === 'create') {
                    store.dispatch('rule/deleteDataFromGroupDetailList');
                    store.commit('rule/setCurrentProject', {
                        editMode: 'display',
                    });
                    currentRuleIndex.value = ruleData.value.ruleList.length - 1;
                }
                store.commit('rule/setCurrentProject', {
                    editMode: 'display',
                });
                if (currentRuleType.value === '3-2') {
                    router.back();
                }
                eventbus.emit(cancelEvent);
                reset();
                return resolve();
            },
            onCancel() {
                if (leaveFlag === 'leave') {
                    if (ruleData.value.currentProject.editMode === 'create') {
                        store.dispatch('rule/deleteDataFromGroupDetailList');
                    }
                    store.commit('rule/setCurrentProject', {
                        editMode: 'display',
                    });
                    return resolve();
                }
                return reject();
            },
        });
    });
};

async function select(type, skip = false) {
    if (ruleData.value.currentProject.editMode !== 'display' && !skip) {
        await cancel('leave');
    }
    // 清空store
    store.commit('rule/setCurrentRuleDetail', {});
    console.log('select', type);
    const t = ruleTypes.find(v => v.value === type)?.type;
    if (!t) {
        return;
    }
    const [rule, table] = t.split('-');
    store.dispatch('rule/addDataToGroupDetailList', {
        rule_type: rule, table_type: table, rule_name: '新建规则', label: '规则名称_未命名', value: ruleData.value.ruleList.length,
    });
    store.dispatch('rule/updateCurrentRule', {
        rule_type: rule,
        table_type: table,
    });
    store.commit('rule/setCurrentProject', {
        editMode: 'create',
    });
    currentRuleIndex.value = ruleData.value.ruleList.length - 1;
    // currentRule.value.table_type = table;
}

async function editRule() {
    try {
        const res = await lockRule({ rule_lock_id: ruleData.value.currentRule.rule_id, rule_lock_range: 'rule' });
        if (res) {
            store.commit('rule/setCurrentProject', {
                editMode: 'edit',
            });
        }
    } catch (error) {
        console.log(error);
    }
}
useListener('openFormEditEvent', editRule);
// tab的切换操作
const onNavbarConfirm = async () => {
    try {
        if (ruleData.value.currentProject.editMode !== 'display') {
            await cancel('leave');
        }
        // 清空store
        store.commit('rule/setCurrentRuleDetail', {});
        return Promise.resolve();
    } catch (error) {
        return Promise.reject();
    }
};

async function init() {
    // 清空tab数据
    store.commit('rule/setRuleList', []);
    // 初始化填入数据逻辑
    // 取路由参数逻辑
    // 填入数据时exception特殊处理逻辑
    const {
        ruleGroupId, id: ruleId, projectId: id, workflowProject,
    } = query.value;
    console.log('ruleGroupId ruleId', ruleGroupId, ruleId, id);
    store.commit('rule/setCurrentProject', {
        projectId: id,
        isWorkflowProject: workflowProject === 'true',
        isNormalProject: !(workflowProject === 'true'),
    });
    // dss的url参数没有ruleid，url参数里面给的可能是字符串undefined
    if (ruleGroupId && ruleGroupId !== 'undefined') {
        store.commit('rule/setCurrentProject', {
            groupId: ruleGroupId,
            editMode: 'display',
        });
        await store.dispatch('rule/getGroupDetailList');
        // 需要根据url的id切换到指定的tab上
        if (ruleId) {
            store.commit('rule/setCurrentProject', {
                ruleId,
            });
            currentRuleIndex.value = ruleData.value.ruleList.findIndex(item => item.rule_id === +ruleId);
            // 最后需要用0做兜底&有规则的时候
            if (currentRuleIndex.value === -1 && ruleData.value.ruleList.length > 0) {
                currentRuleIndex.value = 0;
                // 需要改变当前页面的url，不然在页面上用户看到的ID和url的id不同
                // 这个地方只是重写url，并不会重新加载
                router.replace(`${route.path}?${getURLQueryParams({
                    query: route.query,
                    params: { id: ruleData.value.ruleList[0].rule_id },
                })}`);
            }
        } else {
            currentRuleIndex.value = 0;
        }
    } else {
        // 新增的情况
        select(tpl(), true);
        // 新建需要关闭loading
        store.commit('rule/setLoading', false);
    }
    console.log(store.state.rule);
}

const {
    proxyUserList,
    updateProxyUserList,
    clusterList,
    loadClusterList,
} = useRuleBasicData();
provide('proxyUserList', computed(() => proxyUserList.value || []));
provide('clusterList', computed(() => clusterList.value || []));

onMounted(() => {
    init();
    updateProxyUserList();
    loadClusterList();
});

onUnmounted(() => {
    // 清空store
    store.commit('rule/clearCurrentRuleData');
    store.commit('rule/setVerifyColumns', []);
});

// 执行规则相关useEffect
const {
    showExecutation,
    executationConfig,
    executationRules,
    executeTaskPre,
    excuteRuleInProjectDetail,
} = useExecutation();

function runRule() {
    console.log('执行规则', ruleData.value);
    const {
        project_id: projectId,
        rule_id: ruleId,
        rule_name: ruleName,
        rule_group_id: ruleGroupId,
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
}
const showCopyPanel = ref(false);
const toggleCopyPanel = () => {
    showCopyPanel.value = !showCopyPanel.value;
};
</script>
<style lang="less" scoped>
.wd-content {
    .tab-content{
        position: relative;
        margin-top: 8px;
        background: #fff;
    }
    :deep(.fes-form-item-label) {
        height: 22px;
    }
    :deep(.fes-form-item-content){
        min-height: 22px;
    }
}
</style>
