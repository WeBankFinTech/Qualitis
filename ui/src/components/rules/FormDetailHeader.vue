<template>
    <div class="wd-project-header">
        <LeftOutlined v-if="!currentProject.isEmbedInFrame" class="back" @click="backToPreviousPage" />
        <div class="name-ctn">
            <FInput
                v-if="showEdit"
                ref="nameInput"
                v-model="groupName"
                class="name"
                placeholder="请输入规则组名称"
                @blur="updateGroupNameOnBlur"
            />
            <FEllipsis v-else class="name">{{currentProject.groupName || (currentRuleType === '3-2' ? '库一致性比对配置' : '规则组名称_未命名')}}</FEllipsis>
        </div>
        <EditOutlined v-if="!showEdit && (!currentProject.isWorkflowProject || currentProject.isEmbedInFrame)" class="edit" @click="editGroupName" />
        <FModal
            v-model:show="showBackConfirmModal"
            :title="$t('common.prompt')"
            displayDirective="if"
            @ok="confirmBackToPreviousPage"
        >
            <div>规则组未创建完成，退出后将不保存当前信息，是否确认退出？</div>
        </FModal>
    </div>
</template>
<script setup>
import {
    ref, inject, nextTick, computed, watch,
} from 'vue';
import {
    useI18n, useRouter, request as FRequest, useRoute,
} from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { useStore } from 'vuex';
import { LeftOutlined, EditOutlined } from '@fesjs/fes-design/es/icon';

const router = useRouter();
const route = useRoute();
const { t: $t } = useI18n();
const store = useStore();

const showBackConfirmModal = ref(false);

const currentProject = computed(() => store.state.rule.currentProject);
const currentRuleType = inject('currentRuleType');
const groupName = ref('');

watch(currentProject, (cur) => {
    console.log('++++', cur.groupName);
    if (cur.groupName) {
        groupName.value = cur.groupName;
    }
});

// 规则详情页返回都到项目详情页
const confirmBackToPreviousPage = () => {
    // 复制url直接进来 也跳转到项目详情页
    const params = route.query;
    const backUrl = window.history?.state?.back;
    if (backUrl && backUrl.includes('/projects/detail')) {
        // 从项目里正常跳转进来
        router.back();
    } else if (params.projectId) {
        router.push({
            path: '/projects/detail',
            query: {
                projectId: params.projectId,
            },
        });
    } else {
        router.replace('/projects');
    }
};

const backToPreviousPage = () => {
    confirmBackToPreviousPage();
};

const nameInput = ref(null);
const showEdit = ref(false);
const editGroupName = async () => {
    // if (!currentProject.value.groupId) return;
    showEdit.value = true;
    await nextTick();
    nameInput.value.focus();
};

const updateGroupNameOnBlur = async () => {
    try {
        if (!currentProject.value.groupId) {
            showEdit.value = false;
            store.commit('rule/setCurrentProject', {
                groupName: groupName.value,
            });
            return;
        }
        const result = await FRequest(`api/v1/projector/rule/group/${currentProject.value.groupId}`, {}, 'get');
        await FRequest('api/v1/projector/rule/group/modify', {
            project_id: currentProject.value.projectId,
            rule_group_id: currentProject.value.groupId,
            rule_group_name: groupName.value,
            datasource: result.datasource,
        }, 'post');
        FMessage.success('修改成功');
        showEdit.value = false;
        store.commit('rule/setCurrentProject', {
            groupName: groupName.value,
        });
    } catch (error) {
        console.warn(error);
    }
};
</script>
