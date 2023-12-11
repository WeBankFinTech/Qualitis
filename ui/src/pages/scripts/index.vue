<template>
    <div class="wd-content rule-detail">
        <div class="wd-project-header">
            <div class="name">
                <FInput
                    v-if="showEdit"
                    ref="nameInput"
                    v-model="groupName"
                    class="input"
                    placeholder="请输入规则组名称"
                    @blur="updateGroupNameOnBlur"
                />
                <FEllipsis v-if="!showEdit" class="display">{{groupName || '默认规则组名称'}}</FEllipsis>
            </div>
            <EditOutlined
                v-if="!showEdit && (query.ruleGroupId && query.ruleGroupId !== 'undefined')"
                class="edit"
                @click="editGroupName"
            />
        </div>
        <FForm ref="scriptsFormRef" :model="script" :rules="scriptFormRules">
            <FFormItem>
                <FButton class="save" type="primary" :loading="isLoading" @click="saveScripts">保存</FButton>
            </FFormItem>
            <FFormItem prop="detail">
                <FInput
                    v-model="script.detail"
                    class="form-edit-input scirpt-area"
                    type="textarea"
                />
            </FFormItem>
        </FForm>
    </div>
</template>
<script setup>
import {
    ref, computed, onMounted, nextTick,
} from 'vue';
import {
    request, useI18n, useRoute, useRouter,
} from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { EditOutlined } from '@fesjs/fes-design/es/icon';
import { DWSMessage } from '@/common/utils';

const { t: $t } = useI18n();
const router = useRouter();
const route = useRoute();
const query = computed(() => route.query);

// 嵌入url样本数据
// /scripts?projectId=xx&projectName=xx&ruleGroupId=xx

const showEdit = ref(false);
const nameInput = ref(null);
const groupName = ref('');

const updateGroupNameOnBlur = async () => {
    try {
        const { projectId, ruleGroupId } = query.value;
        if (!ruleGroupId || !projectId || projectId === 'undefined' || ruleGroupId === 'undefined') {
            showEdit.value = false;
            return;
        }
        await request('api/v1/projector/rule/group/modify', {
            project_id: projectId,
            rule_group_id: ruleGroupId,
            rule_group_name: groupName.value,
        }, 'post');
        FMessage.success('修改成功');
        showEdit.value = false;
    } catch (error) {
        console.warn(error);
    }
};

async function getGroupName() {
    try {
        const { ruleGroupId } = query.value;
        if (!ruleGroupId || ruleGroupId === 'undefined') {
            return;
        }
        const result = await request(`api/v1/projector/rule/group/${ruleGroupId}`, {}, 'get');
        groupName.value = result?.rule_group_name;
    } catch (error) {
        console.warn(error);
    }
}

const editGroupName = async () => {
    showEdit.value = true;
    await nextTick();
    nameInput.value.focus();
};

const script = ref({
    detail: '',
});

const scriptFormRules = ref({
    detail: [{
        required: true,
        trigger: 'input',
        message: $t('common.notEmpty'),
    }],
});

const getRuleDetail = async () => {
    const { projectId, projectName, ruleGroupId } = query.value;
    try {
        if (!projectId || !ruleGroupId || projectId === 'undefined' || ruleGroupId === 'undefined') {
            return;
        }
        const detail = await request('api/v1/projector/rule/bash/get', {
            project_id: projectId,
            project_name: projectName,
            rule_group_id: ruleGroupId === 'undefined' ? null : ruleGroupId,
        });
        if (detail) {
            script.value.detail = detail.bash_content;
        }
    } catch (err) {
        console.warn(err);
    }
};

// eslint-disable-next-line no-restricted-globals
const isEmbedInFrame = computed(() => top !== self);
const isLoading = ref(false);
const scriptsFormRef = ref(null);
const saveScripts = async () => {
    try {
        const {
            // eslint-disable-next-line camelcase
            projectId, projectName, ruleGroupId, workflowName = '', workflowVersion = '', contextID = '{}', nodeName = '',
        } = query.value;
        const action = ruleGroupId && ruleGroupId !== 'undefined' ? 'edit' : 'add';
        const contextObj = JSON.parse(JSON.parse(contextID)?.value || '{}');
        if (!projectId || projectId === 'undefined') {
            FMessage.error('URL参数必须包含projectId');
            return;
        }
        await scriptsFormRef.value.validate();
        if (isLoading.value) {
            return;
        }
        isLoading.value = true;
        const result = await request('api/v1/projector/rule/bash/add', {
            project_id: projectId,
            project_name: projectName,
            rule_group_id: ruleGroupId === 'undefined' ? null : ruleGroupId,
            template_functions: script.value.detail,
            work_flow_name: workflowName,
            work_flow_version: workflowVersion,
            work_flow_space: contextObj?.workspace,
            node_name: nodeName,
        }, {
            closeResDataCheck: true,
            dataField: false,
        });

        const { bash_content: detail, rule_group_id: targetGroudId } = result.data || {};

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
            await router.replace(`/scripts?${paramsRules.join('&')}`);
        }
        if (detail) {
            script.value.detail = detail;
        }
        await getGroupName();
        if (result.code !== '0') {
            FMessage.error(result.message);
        } else {
            FMessage.success($t('toastSuccess.editSuccess'));
        }
        isLoading.value = false;
    } catch (err) {
        console.warn(err);
        isLoading.value = false;
        const detail = err.data?.data?.bash_content;
        if (detail) {
            // 失败的时候也要回写信息
            script.value.detail = detail;
        }
    }
};

onMounted(async () => {
    await getGroupName();
    await getRuleDetail();
});
</script>
<config>
{
    "name": "scripts",
    "title": "scripts"
}
</config>
<style lang="less" scoped>
:deep(.scirpt-area) {
    textarea {
        min-height: 500px !important;
    }
}

.wd-content .wd-project-header .name {
    flex: 1;
    width: 0; //通过设置宽度为0让整个name容器的宽度由flex自己分配控制
    min-width: 0;
    margin: 0 12px 0 0;
}
</style>
