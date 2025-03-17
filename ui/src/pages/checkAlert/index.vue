<template>
    <div class="wd-content rule-detail">
        <div class="wd-project-header">
            <div class="name">{{$t('_.数据告警规则配置')}}</div>
        </div>
        <div class="wd-content-body">
            <div v-if="ruleExist" class="rule-detail-form" :class="{ edit: mode !== 'display' }">
                <ul v-if="mode === 'display'" class="wd-body-menus">
                    <li class="wd-body-menu-item" @click="edit">{{$t('_.编辑')}}</li>
                    <li class="wd-body-menu-item" @click="del">{{$t('_.删除')}}</li>
                </ul>
                <FForm ref="alarmFormRef" :model="alarm" :rules="alarmFormRules" labelWidth="110px" :labelPosition="mode === 'edit' ? 'right' : 'left'">
                    <FFormItem :label="$t('_.告警主题')" prop="topic">
                        <FInput
                            v-model="alarm.topic"
                            class="form-edit-input"
                            :placeholder="$t('_.请输入告警主题')"
                            :maxlength="100"
                        >
                            <template #suffix>
                                <span style="color: #93949B">{{alarm.topic.length}}/100</span>
                            </template>
                        </FInput>
                        <div class="form-preview-label">{{alarm.topic}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.默认告警人')" prop="default_receiver">
                        <FInput
                            v-model="alarm.default_receiver"
                            class="form-edit-input"
                            :placeholder="$t('_.请输入')"
                        />
                        <FTooltip v-if=" mode !== 'display'" placement="right">
                            <ExclamationCircleOutlined class="tip edit hint" />
                            <template #content>
                                <div>{{$t('_.请输入 RTX 英文名，多个用户请使用英文逗号分隔')}}</div>
                            </template>
                        </FTooltip>
                        <div class="form-preview-label">{{alarm.default_receiver}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.默认告警级别')" prop="default_alert_level">
                        <FSelect
                            v-model="alarm.default_alert_level"
                            clearable
                            filterable
                            class="form-edit-input"
                            :options="levelList"
                        >
                        </FSelect>
                        <div class="form-preview-label">{{levelList.find(item => item.value === alarm.default_alert_level)?.label || ''}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.默认告警方式')" prop="default_alert_ways">
                        <FCheckboxGroup v-model="alarm.default_alert_ways" class="form-edit-input">
                            <FCheckbox v-for="way in wayList" :key="way" :value="way.value">
                                {{way.label}}
                            </FCheckbox>
                        </FCheckboxGroup>
                        <div class="form-preview-label">{{getWayLabel(alarm.default_alert_ways)}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.高等级告警人')" prop="advanced_receiver">
                        <FInput
                            v-model="alarm.advanced_receiver"
                            class="form-edit-input"
                            :placeholder="$t('_.请输入')"
                        />
                        <FTooltip v-if=" mode !== 'display'" placement="right">
                            <ExclamationCircleOutlined class="tip edit hint" />
                            <template #content>
                                <div>{{$t('_.请输入 RTX 英文名，多个用户请使用英文逗号分隔')}}</div>
                            </template>
                        </FTooltip>
                        <div class="form-preview-label">{{alarm.advanced_receiver}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.高级告警级别')" prop="advanced_alert_level">
                        <FSelect
                            v-model="alarm.advanced_alert_level"
                            clearable
                            filterable
                            class="form-edit-input"
                            :options="levelList"
                        >
                        </FSelect>
                        <div class="form-preview-label">{{levelList.find(item => item.value === alarm.advanced_alert_level)?.label || ''}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.高级告警方式')" prop="advanced_alert_ways">
                        <FCheckboxGroup v-model="alarm.advanced_alert_ways" class="form-edit-input">
                            <FCheckbox v-for="way in wayList" :key="way.value" :value="way.value">
                                {{way.label}}
                            </FCheckbox>
                        </FCheckboxGroup>
                        <div class="form-preview-label">{{getWayLabel(alarm.advanced_alert_ways)}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.告警表')" prop="alert_table">
                        <FInput
                            v-model="alarm.alert_table"
                            class="form-edit-input"
                            :placeholder="$t('_.请输入，格式如：dbtable')"
                        />
                        <div class="form-preview-label">{{alarm.alert_table}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.过滤条件')" prop="filter">
                        <FInput
                            v-model="alarm.filter"
                            class="form-edit-input"
                            :placeholder="`${$t('common.format')} + $ + {run_date}`"
                        />
                        <div class="form-preview-label">{{alarm.filter}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.默认告警筛选列')" prop="alert_col">
                        <FInput
                            v-model="alarm.alert_col"
                            class="form-edit-input"
                            :placeholder="$t('_.请输入')"
                        />
                        <div class="form-preview-label">{{alarm.alert_col}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.高等级告警列')" prop="advanced_alert_col">
                        <FInput
                            v-model="alarm.advanced_alert_col"
                            class="form-edit-input"
                            :placeholder="$t('_.请输入')"
                        />
                        <div class="form-preview-label">{{alarm.advanced_alert_col}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.内容展示列')" prop="content_cols">
                        <FInput
                            v-model="alarm.content_cols"
                            class="form-edit-input"
                            :placeholder="$t('_.请输入，格式如xx;yy; 这里xx指的是数据表的真实字段名，yy指字段别名')"
                            type="textarea"
                        />
                        <div class="form-preview-label">{{alarm.content_cols}}</div>
                    </FFormItem>
                </FForm>
            </div>
        </div>
        <FModal v-model:show="showPreview" :title="$t('_.告警内容预览')" :oktext="$t('_.保存')" @ok="save">
            <div class="rule-detail-form">
                <FForm labelWidth="110px" labelPosition="left">
                    <FFormItem :label="$t('_.告警主题')">
                        <div class="form-preview-label">{{alarm.topic}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.告警时间')">
                        <div class="form-preview-label">yyyy-MM-dd hh:mm:sss</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.告警内容')">
                        <div class="form-preview-label line-feed">{{formatContent(alarm.content_cols)}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.告警节点')">
                        <div class="form-preview-label">{{alarm.topic}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.告警人')">
                        <div class="form-preview-label">{{alarm.default_receiver}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.默认告警级别')">
                        <div class="form-preview-label">{{levelList.find(item => item.value === alarm.default_alert_level)?.label || ''}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.默认告警方式')">
                        <div class="form-preview-label">{{getWayLabel(alarm.default_alert_ways)}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.高级告警人')">
                        <div class="form-preview-label">{{alarm.advanced_receiver}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.高级告警级别')">
                        <div class="form-preview-label">{{levelList.find(item => item.value === alarm.advanced_alert_level)?.label || ''}}</div>
                    </FFormItem>
                    <FFormItem :label="$t('_.高级告警方式')">
                        <div class="form-preview-label">{{getWayLabel(alarm.advanced_alert_ways)}}</div>
                    </FFormItem>
                </FForm>
            </div>
        </FModal>
        <div v-if="mode !== 'display'" style="padding: 0 16px">
            <FSpace :size="16">
                <FButton type="primary" class="button" @click="preview">{{$t('_.预览')}}</FButton>
                <FButton type="primary" class="button" @click="save">{{$t('_.保存')}}</FButton>
                <FButton v-if="ruleGroupId" type="default" class="button" @click="cancel">{{$t('_.取消')}}</FButton>
            </FSpace>
        </div>
    </div>
</template>
<script setup>

import { ref, computed, onMounted } from 'vue';
import {
    request, useI18n, useRoute, useRouter,
} from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import {
    ExclamationCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { DWSMessage, wayList, levelList } from '@/common/utils';
import { FMessage, FModal } from '@fesjs/fes-design';

const route = useRoute();
const router = useRouter();
const query = computed(() => route.query);
const ruleGroupId = computed(() => ((query.value?.ruleGroupId === 'undefined') ? '' : query.value.ruleGroupId));
const { t: $t } = useI18n();
const showPreview = ref(false);
const alarm = ref({
    topic: '',
    default_alert_level: 5,
    default_alert_ways: [1, 2, 3],
    advanced_alert_level: 2,
    advanced_alert_ways: [1, 2, 3],
});
// 请输入，格式如：ds=${run_date}
const ff = `${$t('common.format')} + $ + {run_date}`;
const getWayLabel = ways => ways.map(way => wayList.find(item => item.value === way)?.label || '').join(', ');
const alarmFormRef = ref(null);
const mode = ref('display');
const alarmFormRulesArr = ['default_receiver', 'alert_table', 'alert_col', 'advanced_alert_col'].map(item => ({
    [item]: {
        trigger: ['change', 'blur'],
        required: true,
        message: $t('common.notEmpty'),

    },
}));
alarmFormRulesArr.push({
    topic: {
        required: true,
        trigger: ['change', 'blur'],
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (value.length > 0) {
                if (/^[\u4e00-\u9fa5a-zA-Z0-9_]{1,100}$/.test(value)) {
                    resolve();
                } else {
                    reject($t('_.最大长度为100，且只支持且只支持中文，英文，数字，下划线'));
                }
            } else {
                reject($t('common.notEmpty'));
            }
        }),
    },

});
alarmFormRulesArr.push({
    content_cols: {
        trigger: ['change', 'blur'],
        validator: (rule, value) => new Promise((resolve, reject) => {
            if (/[\u4E00-\u9FA5]/g.test(value)) {
                reject($t('_.不允许输入中文'));
            }
            resolve();
        }),
    },

});
alarmFormRulesArr.push({
    default_alert_ways: {
        trigger: ['change', 'blur'],
        required: true,
        type: 'array',
        message: $t('common.notEmpty'),
    },

});
alarmFormRulesArr.push({
    default_alert_level: {
        trigger: ['change', 'blur'],
        required: true,
        type: 'number',
        message: $t('common.notEmpty'),
    },

});
const alarmFormRules = ref({});
for (let i = 0; i < alarmFormRulesArr.length; i++) {
    alarmFormRules.value = Object.assign({}, alarmFormRules.value, alarmFormRulesArr[i]);
}

const formatContent = content => content?.split(';')?.map(item => item?.split(':')[1])?.join('|') || '';
const preview = async () => {
    try {
        await alarmFormRef.value.validate();
        showPreview.value = true;
        const {
            nodeName, workflowName, workflowVersion, projectId,
        } = query.value;
        const urlParam = {
            node_name: nodeName, work_flow_name: workflowName, work_flow_version: workflowVersion, project_id: projectId, rule_group_id: ruleGroupId.vaule || null,
        };
        const body = Object.assign({}, alarm.value, urlParam);
        await request('/api/v1/projector/checkAlert/check', body);
    } catch (err) {
        console.warn(err);
    }
};
// eslint-disable-next-line no-restricted-globals
const isEmbedInFrame = computed(() => top !== self);
const save = async () => {
    try {
        await alarmFormRef.value.validate();
        const {
            nodeName, workflowName, workflowVersion, projectId,
        } = query.value;
        const contextID = JSON.parse(query.value?.contextID || '{}');
        const workflowInfo = JSON.parse(contextID?.value || '{}');
        const urlParam = {
            node_name: nodeName, work_flow_name: workflowName, work_flow_version: workflowVersion, project_id: projectId, rule_group_id: ruleGroupId.value || null, work_flow_space: workflowInfo.workspace || '',
        };
        const body = Object.assign({}, alarm.value, urlParam);
        // await request('/api/v1/projector/checkAlert/check', body);
        console.log(alarm.value, '保存');
        let action;
        if (ruleGroupId.value) {
            // edit
            action = 'edit';
            const res = await request('/api/v1/projector/checkAlert/modify', body);
            if (!res.advanced_alert_ways) res.advanced_alert_ways = [];
            alarm.value = res;
            // eslint-disable-next-line no-use-before-define
            copyDetail.value = cloneDeep(res);
            FMessage.success($t('_.编辑节点成功'));
            // eslint-disable-next-line no-use-before-define
            checkAlertId.value = res.id;
        } else {
            // add
            action = 'add';
            const res = await request('/api/v1/projector/checkAlert/add', body);
            const { rule_group_id: targetGroudId } = res || {};
            if (isEmbedInFrame.value && targetGroudId) {
                DWSMessage(route.query.nodeId, targetGroudId, action);
            }

            if (targetGroudId) {
                // 重写路由
                const params = Object.assign({}, route.query, { ruleGroupId: targetGroudId });
                const paramsRules = [];
                const paramsKeys = Object.keys(params);
                for (let index = 0; index < paramsKeys.length; index++) {
                    const key = paramsKeys[index];
                    paramsRules.push(`${key}=${params[key]}`);
                }
                await router.replace(`/checkAlert?${paramsRules.join('&')}`);
            }
            if (!res.advanced_alert_ways) res.advanced_alert_ways = [];
            alarm.value = res;
            // eslint-disable-next-line no-use-before-define
            copyDetail.value = cloneDeep(res);
            // eslint-disable-next-line no-use-before-define
            checkAlertId.value = res.id;
            FMessage.success($t('_.新建节点成功'));
        }
        mode.value = 'display';
        showPreview.value = false;
    } catch (err) {
        console.warn(err);
    }
};
const cancel = () => {
    FModal.confirm({
        title: $t('_.提示'),
        content: $t('_.您处于编辑状态，离开后数据将不会被保存'),
        okText: $t('_.确认离开'),
        cancelText: $t('_.取消'),
        onOk() {
            // eslint-disable-next-line no-use-before-define
            alarm.value = cloneDeep(copyDetail.value);
            mode.value = 'display';
        },
    });
};
const checkAlertId = ref('');
const copyDetail = ref({});
const edit = () => {
    mode.value = 'edit';
};
const ruleExist = ref(true);
const del = () => {
    FModal.confirm({
        title: $t('_.提示'),
        content: $t('_.确认删除当前批量告警规则配置'),
        okText: $t('_.删除'),
        cancelText: $t('_.取消'),
        async onOk() {
            try {
                await request(`/api/v1/projector/checkAlert/delete/${checkAlertId.value}`, {});
                DWSMessage(route.query.nodeId, ruleGroupId.value, 'delete');
                ruleExist.value = false;
            } catch (err) {
                console.warn(err);
            }
        },
        onCancel() {
            console.log('取消');
        },
    });
};
onMounted(async () => {
    try {
        if (ruleGroupId.value) {
            const firRes = await request(`/api/v1/projector/rule/group/${ruleGroupId.value}`, {}, 'get');
            checkAlertId.value = firRes.check_alert_id_list[0];
            const secRes = await request(`/api/v1/projector/checkAlert/get/${checkAlertId.value}`, {}, 'get');
            if (!secRes.advanced_alert_ways) secRes.advanced_alert_ways = [];
            alarm.value = secRes;
            copyDetail.value = cloneDeep(secRes);
        } else {
            mode.value = 'edit';
        }
    } catch (err) {
        console.warn(err);
    }
});

</script>
<config>
{
    "name": "checkAlert",
    "title": "checkAlert"
}
</config>
<style lang="less" scoped>
.wd-content .wd-project-header .name {
    flex: 1;
    width: 0; //通过设置宽度为0让整个name容器的宽度由flex自己分配控制
    min-width: 0;
    margin: 0 12px 0 0;
}
.rule-detail-form {
    .line-feed {
        white-space: pre;
    }

.hint {
    display: inline-block;
    margin-left: 8px;
    color: #646670;
}
}
</style>
