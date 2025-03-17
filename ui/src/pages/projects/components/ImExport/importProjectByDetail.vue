<template>
    <FModal :show="show" :width="600"
            :title="modalTitle"
            :maskClosable="false"
            @ok="handleSave"
            @cancel="handleCancel"
    >
        <FForm
            ref="importFormRef"
            :model="importForm"
            :rules="importFormRule"
            labelPosition="right"
            :labelWidth="108"
        >
            <FFormItem v-if="type === 'table'" prop="update" :label="$t('_.操作类型')">
                <FRadioGroup v-model="importForm.update" :cancelable="false" @change="changeUpdate">
                    <FRadio :value="false">{{$t('_.新建项目')}}</FRadio>
                    <FRadio :value="true">{{$t('_.更新已有项目')}}</FRadio>
                </FRadioGroup>
            </FFormItem>
            <FFormItem v-if="importForm.update" prop="project_id" :label="$t('_.项目名称')">
                <FSelect v-model="importForm.project_id" :placeholder="$t('_.请选择项目')" :options="projectNameList" filterable @change="changeImportProject" />
            </FFormItem>
            <FFormItem prop="upload_type" :label="$t('_.导入方式')">
                <FSelect v-model="importForm.upload_type" :placeholder="$t('_.请选择导入方式')" :options="importTypeList" :disabled="props.uploadRule" @change="changeUploadType" />
            </FFormItem>
            <template v-if="importForm.upload_type === 1">
                <FFormItem prop="zip_path" :label="$t('_.上传文件')" style="align-items: center">
                    <div class="form-edit-input">
                        <FButton v-if="!importForm.zip_path && !uploading" @click="uploadFile"><UploadOutlined />{{$t('_.上传文件')}}</FButton>
                        <div v-else-if="uploading" class="uploading"><FSpin />{{$t('_.上传中')}}</div>
                        <FileBtn v-else :file="importForm.zip_path" @delFile="delFile" />
                    </div>
                    <div class="file-tooltip">{{$t('_.支持zip文件格式，不超过10M，请按照目录规范上传 excel 的压缩包。')}}</div>
                </FFormItem>
            </template>
            <template v-else>
                <GitBase ref="gitBaseRef" v-model:gitForm="importForm" mode="edit"></GitBase>
            </template>
            <FFormItem :label="$t('_.差异化变量')" class="diff-form-label">
                <DifVariables ref="difVariablesRef" parentType="import"></DifVariables>
            </FFormItem>
        </FForm>
        <template #footer>
            <FSpace :size="CONDITIONBUTTONSPACE" justify="end">
                <FButton @click="handleCancel">{{$t('common.cancel')}}</FButton>
                <FButton type="primary" :loading="submitLoading" @click="handleSave">{{$t('common.ok')}}</FButton>
            </FSpace>
        </template>
        <input
            v-show="false"
            :ref="el => { if (el) fileInputRefs = el }"
            type="file"
            class="btn-file-input"
            :accept="acceptType"
            @change="importFunc(file)"
        />
    </FModal>
</template>
<script setup>

import {
    toRaw, ref, defineProps, defineEmits, onMounted, computed, watchEffect,
} from 'vue';
import { useI18n, request, useModel } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
import GitBase from '@/pages/projects/components/ImExport/gitBase';
import DifVariables from '@/pages/projects/components/ImExport/difVariables';
import { UploadOutlined } from '@fesjs/fes-design/es/icon';
import FileBtn from '@/pages/rules/functionManagement/components/fileBtn';
import { useDataList } from '@/pages/projects/components/ImExport/hooks/useDataList';
import { uploadProject, gitModify } from '@/pages/projects/api';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import { cloneDeep } from 'lodash-es';

const { t: $t } = useI18n();
const {
    projectNameList,
    importTypeList,
    loadProjectNameList,
    updateGitForm,
} = useDataList();
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    // type有两种，detail-项目详情页的，table-项目表格页的
    type: {
        type: String,
        required: true,
    },
    project: {
        type: Object,
        required: false,
    },
    // 上传规则
    uploadRule: {
        type: Boolean,
        required: false,
        default: false,
    },
});

const modalTitle = computed(() => (props.uploadRule ? $t('myProject.importRule') : $t('myProject.importProject')));
const emit = defineEmits(['update:show', 'uploadSucess']);
const initImportForm = () => {
    const initForm = {
        upload_type: 1,
        zip_path: '',
        git_branch: props.project?.git_branch || 'master',
        git_type: props.project?.git_type || 1,
        git_repo: props.project?.git_repo || '',
        git_root_dir: props.project?.git_root_dir || `dqm/${props.project?.project_name || ''}`,
        dif_array: [],
    };
    if (props.type === 'table') {
        initForm.update = false;
        initForm.project_id = '';
        initForm.git_root_dir = '';
    }
    if (props.uploadRule) {
        initForm.upload_type = 1;
        importTypeList.value = [
            {
                value: 1,
                label: $t('_.从本地导入'),
            },
        ];
        delete initForm.git_branch;
        delete initForm.git_type;
        delete initForm.git_repo;
        delete initForm.git_root_dir;
    }
    return initForm;
};
const submitLoading = ref(false);
const importForm = ref(initImportForm());
const gitBaseRef = ref(null);
const importFormRef = ref(null);
const difVariablesRef = ref(null);
const isfirstOpen = ref(true);
const initialState = useModel('@@initialState');
const isPermissionFunc = ref(null);
const curPid = computed(() => props.project?.project_id || importForm.value.project_id);
const cacheProject = ref({});
watchEffect(async () => {
    console.log(`curId changed to ${curPid.value}`);
    const {
        isPermissionCompute,
    } = useProjectAuth(curPid.value, initialState.userName);
    isPermissionFunc.value = isPermissionCompute;
});
// 上传文件
const fileInputRefs = ref(null);
const acceptType = ref('.zip');
const uploading = ref(false);
const uploadFile = () => {
    console.log('uploadFile', uploadFile);
    fileInputRefs.value?.click();
};
const importFunc = async () => {
    console.log('importFunc', importFunc);
    try {
        const fileInputRef = fileInputRefs.value;
        if (!fileInputRef) return;
        const maxSize = 10 * 1024 * 1024; // 10M
        const file = fileInputRef.files[0];
        console.log('file size:', file.size);
        if (file.size > maxSize) {
            FMessage.warning($t('_.文件大小超过10M,请重新上传。'));
            return;
        }
        const formData = new FormData();
        formData.append('file', file);
        uploading.value = true;
        const res = await request('/api/v1/projector/project/batch/upload_zip_from_local', formData, 'post');
        uploading.value = false;
        importForm.value.zip_path = res;
        importFormRef.value.clearValidate();
    } catch (err) {
        console.log('importFunc-error');
        console.warn(err);
        uploading.value = false;
    }
};

const delFile = () => {
    importForm.value.zip_path = '';
    fileInputRefs.value.value = '';
};

const handleCancel = () => {
    emit('update:show', false);
    importForm.value = initImportForm();
    difVariablesRef.value.clearData();
    isfirstOpen.value = true;
};
const handleParams = () => {
    const {
        upload_type, git_repo, git_type, git_branch, git_root_dir, zip_path,
    } = importForm.value;
    let params = {};
    if (importForm.value.upload_type === 2) {
        params = {
            upload_type, git_repo, git_type, git_branch, git_root_dir,
        };
    } else {
        params = {
            upload_type, zip_path,
        };
    }
    if (props.type === 'detail' || importForm.value.update) {
        params.project_id = props.project?.project_id || importForm.value.project_id;
    }
    params.diff_variables = toRaw(difVariablesRef.value.getDifData());
    // 上传规则时，为增量上传，increment为true；为项目时则为false；
    params.increment = props.uploadRule;
    if (params.increment) {
        params.project_id = +params.project_id;
    }
    return params;
};
const afterConfirmSave = async () => {
    const params = handleParams();
    console.log('params', params);
    try {
        submitLoading.value = true;
        await uploadProject(params);
        FMessage.success($t('engineConfig.saveSuccess'));
        emit('uploadSucess');
        submitLoading.value = false;
    } catch (e) {
        console.error(e);
        submitLoading.value = false;
    }
    handleCancel();
};
const submitCodeConfig = async () => {
    const {
        git_repo, git_type, git_branch, git_root_dir,
    } = importForm.value;
    const params = {
        project_id: props.project?.project_id || importForm.value.project_id,
        git_repo,
        git_type,
        git_branch,
        git_root_dir,
    };
    try {
        const r = await gitModify(params);
    } catch (e) {
        console.log(e);
    }
};
// 判断代码配置信息是否有修改
const isModifyGit = () => {
    let isModify = false;
    ['git_branch', 'git_repo', 'git_root_dir', 'git_type'].forEach((e) => {
        if (cacheProject.value[e] !== importForm.value[e]) {
            isModify = true;
        }
    });
    return isModify;
};
// 项目详情页，git方式导入时，是否做代码存储配置
const isReplaceConfig = ref(false);
const handleSave = async () => {
    try {
        let r = [];
        if (importForm.value.upload_type === 1) {
            r = await Promise.all([importFormRef.value.validate(), difVariablesRef.value.valid()]);
        } else {
            r = await Promise.all([gitBaseRef.value.valid(), importFormRef.value.validate(), difVariablesRef.value.valid()]);
        }
        let isImport = true;
        if (props.type === 'table' && importForm.value.update) { // 只对已有项目做权限校验
            isImport = await isPermissionFunc.value();
        }
        if (!r.includes(false) && isImport) {
            importForm.value.dif_array = difVariablesRef.value.getDifData();
            const isModify = isModifyGit();
            // 项目详情页，git方式做导入且git配置信息有修改，第一次点击保存先提示是否替换地址配置信息
            if (isModify && isfirstOpen.value && importForm.value.upload_type === 2 && props.type === 'detail') {
                FModal.confirm({
                    title: $t('_.温馨提示'),
                    content: $t('_.是否将导入项目的Git地址配置信息保存为代码存储配置信息'),
                    okText: $t('_.保存'),
                    cancelText: $t('_.不保存'),
                    async onOk() {
                        isfirstOpen.value = false;
                        isReplaceConfig.value = true;
                    },
                    async onCancel() {
                        isfirstOpen.value = false;
                        isReplaceConfig.value = false;
                    },
                });
            // 项目列表页，导入且为更新已有项目时，1：本地导入方式 2：git导入方式且git配置信息有修改-提示导入会完全替换项目信息
            } else if ((importForm.value.upload_type === 1 || isModify) && importForm.value.update && props.type === 'table') {
                FModal.confirm({
                    title: $t('_.系统提示'),
                    content: $t('_.导入的项目信息将完全替换现有项目的信息，是否确认导入并替换'),
                    okText: $t('_.确认导入'),
                    cancelText: $t('_.取消'),
                    async onOk() {
                        console.log('确认导入');
                        afterConfirmSave();
                    },
                    async onCancel() {
                        console.log('取消导入');
                        handleCancel();
                    },
                });
            } else {
                if (isReplaceConfig.value) {
                    submitCodeConfig();
                    isReplaceConfig.value = false;
                }
                afterConfirmSave();
            }
        }
        if (!isImport) {
            FMessage.warning($t('_.导入权限不足'));
        }
    } catch (e) {
        console.log(e);
    }
};
const importFormRule = ref({
    project_id: [
        {
            type: 'number', trigger: ['change', 'blur'], required: true, message: $t('_.不能为空'),
        },
    ],
    update: [
        {
            type: 'boolean', required: true, message: $t('_.不能为空'),
        },
    ],
    upload_type: [
        {
            type: 'number', required: true, message: $t('_.不能为空'),
        },
    ],
    zip_path: [
        {
            required: true, message: $t('_.不能为空'),
        },
    ],
});
const changeUpdate = (v) => {
    importForm.value = initImportForm();
    importForm.value.update = v;
    cacheProject.value = cloneDeep(importForm.value);
};
const changeImportProject = async () => {
    await updateGitForm(curPid.value, importForm.value);
    cacheProject.value = cloneDeep(importForm.value);
    difVariablesRef.value.clearData();
};
const changeUploadType = (e) => {
    // 1-本地，2-git
    if (e === 2) {
        importForm.value.git_type = cacheProject.value?.git_type || 1;
        importForm.value.git_branch = cacheProject.value?.git_branch || 'master';
        importForm.value.git_repo = cacheProject.value?.git_repo || '';
        importForm.value.git_root_dir = cacheProject.value?.git_root_dir || `dqm/${cacheProject.value?.project_name || ''}`;
    }
    importForm.value.zip_path = '';
    difVariablesRef.value.clearData();
};
onMounted(async () => {
    await loadProjectNameList();
    if (props.project) {
        cacheProject.value = props.project;
    }
});

</script>
<style lang="less" scoped>
:deep(.diff-form-label){
    .fes-form-item-content{
        display: flow-root;
    }
}
.file-tooltip {
    margin-left:16px;
    color: #93949B;
}
</style>
