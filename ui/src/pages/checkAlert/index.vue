<template>
    <div class="wd-content rule-detail">
        <div class="wd-project-header">
            <div class="name">
                数据告警规则配置
            </div>
        </div>
        <div class="wd-content-body">
            <div v-if="ruleExist" class="rule-detail-form" :class="{ edit: mode !== 'display' }">
                <ul v-if="mode === 'display'" class="wd-body-menus">
                    <li class="wd-body-menu-item" @click="edit">编辑</li>
                    <li class="wd-body-menu-item" @click="del">删除</li>
                </ul>
                <FForm ref="alarmFormRef" :model="alarm" :rules="alarmFormRules" labelWidth="110px" :labelPosition="mode === 'edit' ? 'right' : 'left'">
                    <FFormItem label="告警主题" prop="topic">
                        <FInput
                            v-model="alarm.topic"
                            class="form-edit-input"
                            placeholder="请输入告警主题"
                            :maxlength="100"
                        >
                            <template #suffix>
                                <span style="color: #93949B">{{alarm.topic.length}}/100</span>
                            </template>
                        </FInput>
                        <div class="form-preview-label">{{alarm.topic}}</div>
                    </FFormItem>
                    <FFormItem label="默认告警人" prop="info_receiver">
                        <FInput
                            v-model="alarm.info_receiver"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <FTooltip v-if=" mode !== 'display'" placement="right">
                            <ExclamationCircleOutlined class="tip edit hint" />
                            <template #content>
                                <div>请输入 RTX 英文名，多个用户请使用英文逗号分隔;</div>
                                <div>仅进行IMS告警即可，告警等级为info</div>
                            </template>
                        </FTooltip>
                        <div class="form-preview-label">{{alarm.info_receiver}}</div>
                    </FFormItem>
                    <FFormItem label="高等级告警人" prop="major_receiver">
                        <FInput
                            v-model="alarm.major_receiver"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <FTooltip v-if=" mode !== 'display'" placement="right">
                            <ExclamationCircleOutlined class="tip edit hint" />
                            <template #content>
                                <div>请输入 RTX 英文名，多个用户请使用英文逗号分隔;</div>
                                <div>均采用电话告警，告警等级为major</div>
                            </template>
                        </FTooltip>
                        <div class="form-preview-label">{{alarm.major_receiver}}</div>
                    </FFormItem>
                    <FFormItem label="告警表" prop="alert_table">
                        <FInput
                            v-model="alarm.alert_table"
                            class="form-edit-input"
                            placeholder="请输入，格式如：db.table"
                        />
                        <div class="form-preview-label">{{alarm.alert_table}}</div>
                    </FFormItem>
                    <FFormItem label="过滤条件" prop="filter">
                        <FInput
                            v-model="alarm.filter"
                            class="form-edit-input"
                            placeholder="请输入，格式如：ds=${run_date}"
                        />
                        <div class="form-preview-label">{{alarm.filter}}</div>
                    </FFormItem>
                    <FFormItem label="默认告警筛选列" prop="alert_col">
                        <FInput
                            v-model="alarm.alert_col"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <div class="form-preview-label">{{alarm.alert_col}}</div>
                    </FFormItem>
                    <FFormItem label="高等级告警列" prop="major_alert_col">
                        <FInput
                            v-model="alarm.major_alert_col"
                            class="form-edit-input"
                            placeholder="请输入"
                        />
                        <div class="form-preview-label">{{alarm.major_alert_col}}</div>
                    </FFormItem>
                    <FFormItem label="内容展示列" prop="content_cols">
                        <FInput
                            v-model="alarm.content_cols"
                            class="form-edit-input"
                            placeholder="请输入，格式如xx;yy; 这里xx指的是数据表的真实字段名，yy指字段别名"
                            type="textarea"
                        />
                        <div class="form-preview-label">{{alarm.content_cols}}</div>
                    </FFormItem>
                </FForm>
            </div>
        </div>
        <FModal v-model:show="showPreview" title="告警内容预览" okText="保存" @ok="save">
            <div class="rule-detail-form">
                <FForm labelWidth="110px" labelPosition="left">
                    <FFormItem label="告警主题">
                        <div class="form-preview-label">{{alarm.topic}}</div>
                    </FFormItem>
                    <FFormItem label="告警时间">
                        <div class="form-preview-label">yyyy-MM-dd hh:mm:sss</div>
                    </FFormItem>
                    <FFormItem label="告警内容">
                        <div class="form-preview-label line-feed">{{formatContent(alarm.content_cols)}}</div>
                    </FFormItem>
                    <FFormItem label="告警节点">
                        <div class="form-preview-label">{{alarm.topic}}</div>
                    </FFormItem>
                    <FFormItem label="告警人">
                        <div class="form-preview-label">{{alarm.info_receiver}}</div>
                    </FFormItem>
                    <FFormItem label="高级告警人">
                        <div class="form-preview-label">{{alarm.major_receiver}}</div>
                    </FFormItem>
                </FForm>
            </div>
        </FModal>
        <div v-if="mode !== 'display'" style="padding: 0 16px">
            <FSpace :size="16">
                <FButton type="primary" class="button" @click="preview">预览</FButton>
                <FButton type="primary" class="button" @click="save">保存</FButton>
                <FButton v-if="ruleGroupId" type="default" class="button" @click="cancel">取消</FButton>
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
import { DWSMessage } from '@/common/utils';
import { FMessage, FModal } from '@fesjs/fes-design';

const route = useRoute();
const router = useRouter();
const query = computed(() => route.query);
const ruleGroupId = computed(() => ((query.value?.ruleGroupId === 'undefined') ? '' : query.value.ruleGroupId));
const { t: $t } = useI18n();
const showPreview = ref(false);
const alarm = ref({
    topic: '',
});
const alarmFormRef = ref(null);
const mode = ref('display');
const alarmFormRulesArr = ['info_receiver', 'alert_table', 'alert_col', 'major_alert_col'].map(item => ({
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
                    reject('最大长度为100，且只支持且只支持中文，英文，数字，下划线');
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
                reject('不允许输入中文');
            }
            resolve();
        }),
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
            alarm.value = res;
            // eslint-disable-next-line no-use-before-define
            copyDetail.value = cloneDeep(res);
            FMessage.success('编辑节点成功');
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
            alarm.value = res;
            // eslint-disable-next-line no-use-before-define
            copyDetail.value = cloneDeep(res);
            // eslint-disable-next-line no-use-before-define
            checkAlertId.value = res.id;
            FMessage.success('新建节点成功');
        }
        mode.value = 'display';
        showPreview.value = false;
    } catch (err) {
        console.warn(err);
    }
};
const cancel = () => {
    FModal.confirm({
        title: '提示',
        content: '您处于编辑状态，离开后数据将不会被保存',
        okText: '确认离开',
        cancelText: '取消',
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
        title: '提示',
        content: '确认删除当前批量告警规则配置',
        okText: '删除',
        cancelText: '取消',
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
            alarm.value = secRes;
            copyDetail.value = cloneDeep(secRes);
        } else {
            mode.value = 'edit';
        }
        console.log(query.value, 'query');
        console.log(alarm.value, 'alert');
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
