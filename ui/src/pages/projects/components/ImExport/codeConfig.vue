<template>
    <FModal :show="show" :width="600"
            :title="$t('myProject.codeAddress')"
            @ok="handleSave"
            @cancel="handleCancel"
    >
        <GitBase ref="gitBaseRef" v-model:gitForm="configForm" :mode="curMode"></GitBase>
        <template #footer>
            <FSpace :size="CONDITIONBUTTONSPACE" justify="end">
                <template v-if="curMode === 'preview'">
                    <FButton type="danger" @click="delConfig">{{$t('myProject.delConfig')}}</FButton>
                    <FButton type="info" @click="editConfig">{{$t('common.edit')}}</FButton>
                </template>
                <template v-else>
                    <FButton @click="handleCancel">{{$t('common.cancel')}}</FButton>
                    <FButton type="primary" @click="handleSave">{{$t('common.save')}}</FButton>
                </template>
            </FSpace>
        </template>
    </FModal>
</template>
<script setup>

import {
    ref, defineProps, defineEmits, onMounted,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
import GitBase from '@/pages/projects/components/ImExport/gitBase';
import { gitModify, gitConfigDelete } from '@/pages/projects/api.js';

const { t: $t } = useI18n();
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    project: {
        type: Object,
        required: true,
    },
});
const emit = defineEmits(['update:show', 'updateGit']);
const curMode = ref('preview');
const initConfigForm = () => ({
    git_branch: props.project?.git_branch || 'master',
    git_type: props.project?.git_type || 1,
    git_repo: props.project?.git_repo,
    git_root_dir: props.project?.git_root_dir || `dqm/${props.project?.project_name || ''}`,

});
const configForm = ref(initConfigForm());
const gitBaseRef = ref(null);
const editConfig = () => {
    curMode.value = 'edit';
};
const handleCancel = () => {
    curMode.value = 'preview';
    configForm.value = initConfigForm();
    emit('update:show', false);
};
const delConfig = () => {
    FModal.confirm({
        title: $t('common.prompt'),
        content: $t('_.确认删除该代码存储配置？'),
        async onOk() {
            await gitConfigDelete({
                project_id: props.project.project_id,
            });
            FMessage.success($t('toastSuccess.deleteSuccess'));
            emit('updateGit');
            handleCancel();
        },
    });
};
const handleSave = async () => {
    const r = await gitBaseRef.value.valid();
    if (r) {
        const params = { ...configForm.value, project_id: props.project.project_id };
        console.log('params', params);
        try {
            const modifyResult = await gitModify(params);
            emit('updateGit');
        } catch (e) {
            console.log(e);
        }
        FMessage.success($t('engineConfig.saveSuccess'));
        handleCancel();
    }
};
const isComplete = () => {
    let flag = true;
    const vArray = Object.values(configForm.value);
    vArray.forEach((v) => {
        if (!v) {
            flag = false;
        }
    });
    return flag;
};
onMounted(() => {
    const completeFlag = isComplete();
    if (!completeFlag) {
        curMode.value = 'edit';
    }
});

</script>
