<template>
    <div class="rule-detail-form">
        <div class="wd-content-body tab-content">
            <ul v-if="!(ruleData.currentProject.isWorkflowProject && !ruleData.currentProject.isEmbedInFrame)" class="wd-body-menus">
                <li v-if="ruleData.currentProject.groupObjectEditMode === 'display' && listReady" class="wd-body-menu-item" @click="run">{{$t('_.执行规则')}}</li>
                <li v-if="ruleData.currentProject.groupObjectEditMode === 'display'" class="wd-body-menu-item" @click="edit">{{$t('_.编辑')}}</li>
            </ul>
            <verifyObject ref="verifyObjectRef" type="single" lisType="groupList" />
            <FForm v-if="ruleData.currentProject.groupObjectEditMode !== 'display'" :labelWidth="96">
                <FFormItem label="&nbsp;&nbsp;">
                    <FSpace>
                        <FButton class="save" type="primary" @click="saveVerifyObjectData">{{$t('_.保存数据源')}}</FButton>
                        <FButton v-if="ruleData.currentProject.groupObjectEditMode !== 'create'" class="cancel" @click="cancel">{{$t('_.取消')}}</FButton>
                    </FSpace>
                </FFormItem>
            </FForm>
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
    ref,
    provide,
    computed,
    nextTick,
} from 'vue';
import dayjs from 'dayjs';
import { useStore } from 'vuex';
import {
    useRoute, useRouter, request as FRequest, useI18n,
} from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import eventbus from '@/common/useEvents';
import verifyObject from '@/components/rules/VerifyObject.vue';
import { DWSMessage, getURLQueryParams } from '@/common/utils';
import { cancelEvent, columnData2Str, useListener } from '@/components/rules/utils';
import { unLockRule, lockRule } from '@/components/rules/api/index';
import useExecutation from '@/pages/projects/hooks/useExecutation';
import ExecRulePanel from '@/components/ExecutionConfig/ExecRulePanel';


const { t: $t } = useI18n();

const executeRuleId = ref([]);
const listReady = ref(false);
// 执行规则相关useEffect
const {
    showExecutation,
    executationConfig,
    executationRules,
    executeTaskPre,
    excuteRuleInProjectDetail,
} = useExecutation();
const verifyObjectRef = ref();
const router = useRouter();
const route = useRoute();
const store = useStore();
const ruleData = computed(() => store.state.rule);

// 加载规则详情
const configuredRuleMetric = ref({});
provide('configuredRuleMetric', computed(() => configuredRuleMetric.value));

const edit = async () => {
    try {
        const datasourceRes = await lockRule({ rule_lock_id: ruleData.value.currentProject.groupId, rule_lock_range: 'table_group_datasource' });
        const rulesRes = await lockRule({ rule_lock_id: ruleData.value.currentProject.groupId, rule_lock_range: 'table_group_rules' });
        if (datasourceRes && rulesRes) {
            store.commit('rule/setCurrentProject', {
                groupObjectEditMode: 'edit',
                groupRuleEditMode: 'edit',
            });
        }
    } catch (error) {
        console.log(error);
    }
};

const cancel = async () => {
    try {
        await unLockRule({ rule_lock_id: ruleData.value.currentProject.groupId, rule_lock_range: 'table_group_datasource' });
    } catch (error) {
        console.log(error);
    }
    // 通知外层组件重新拉取校验对象数据
    eventbus.emit(cancelEvent);
    store.commit('rule/setCurrentProject', {
        groupObjectEditMode: 'display',
    });
};


const saveVerifyObjectData = async () => {
    try {
        const validResult = await verifyObjectRef.value.valid();

        // 校验不通过
        if (validResult === false) return;
        // 校验通过之后再拿数据
        const verifyObjectFormData = verifyObjectRef.value.verifyObjectData;

        const {
            projectId,
            groupName,
            groupId,
            groupObjectEditMode,
            isEmbedInFrame,
        } = ruleData.value.currentProject;

        const isCreate = groupObjectEditMode === 'create';
        const fpsColumns = verifyObjectFormData.datasource[0].file_table_desc;
        const params = {
            project_id: projectId,
            rule_group_name: groupName,
            datasource: verifyObjectFormData.datasource.map(item => ({
                ...item,
                file_table_desc: columnData2Str(item.file_table_desc),
            })),
            rule_group_id: groupId,
            cs_id: ruleData.value.currentRuleDetail.isUpStream ? route.query?.contextID || '{}' : null,
        };

        const action = isCreate ? 'add' : 'modify';

        const { rule_group_id: ruleGroupId, rule_group_name } = await FRequest(`api/v1/projector/rule/group/${action}`, params);
        if (isEmbedInFrame && ruleGroupId) {
            DWSMessage(route.query.nodeId, ruleGroupId, action);
        }
        if (isCreate) {
            // 创建的情况
            router.replace(`${route.path}?${getURLQueryParams({
                query: route.query,
                params: {
                    ruleGroupId,
                },
            })}`);
            store.commit('rule/setCurrentProject', {
                groupId: ruleGroupId,
                groupName: rule_group_name,
            });
        }

        await nextTick();
        store.commit('rule/setCurrentProject', {
            groupObjectEditMode: 'display',
        });
        eventbus.emit('UPDATE_COLUMN_LIST_ON_SAVE', fpsColumns);
        // 如果数据源是创建通知校验规则组件新建一个空白rule否则重新load rule数据
        !isCreate && eventbus.emit('IS_VERIFY_RULE_DATA_NEED_RELOAD');
        FMessage.success($t('_.保存成功'));
    } catch (error) {
        console.log(error);
    }
};
const ruleList = ref([]);
// batch execute group rules
const run = async () => {
    const result = await FRequest('api/v1/projector/rule/group/rules/query', {
        project_id: ruleData.value.currentProject.projectId,
        rule_group_id: ruleData.value.currentProject.groupId,
        page: 0,
        size: 99999,
    });
    const data = result.data;
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
    } else {
        ruleList.value = [];
    }
    executeRuleId.value = ruleList.value.map(rule => rule.rule_id);
    console.log(executeRuleId.value, 'executeRuleId');
    const ruleDetails = ruleList.value.map((rule) => {
        const { rule_id, rule_name, rule_group_id } = rule;
        return {
            rule_id,
            rule_name,
            rule_group_id,
            rule_enable: true,
            rule_group_name: ruleData.value.currentProject.groupName,
        };
    });

    const {
        project_id: projectId,
    } = ruleData.value.currentProject;
    executeTaskPre({
        project_id: projectId,
        rule_details: ruleDetails,
        disabled: false,
    });
};
useListener('RULE_LIST_READY', (list) => {
    // ruleList.value = list;
    listReady.value = true;
});

</script>

<style lang="less" scoped>
.wd-content-body {
    overflow: hidden;
    padding-bottom: 0;
}
</style>
