<template>
    <FModal :show="show" :width="600"
            :title="modalTitle"
            :maskClosable="false"
            @ok="handleSave"
            @cancel="handleCancel"
    >
        <FForm
            ref="exportFormRef"
            :model="exportForm"
            :rules="exportFormRule"
            labelPosition="right"
            :labelWidth="108"
        >
            <FFormItem v-if="type === 'detail'" prop="project_name" :label="$t('_.项目名称')">
                <FInput v-model="exportForm.project_name" :disabled="true" />
            </FFormItem>
            <FFormItem v-else prop="project_id" :label="$t('_.项目名称')">
                <FSelect v-model="exportForm.project_id" :placeholder="$t('_.请选择需要导出的项目')" :options="projectNameList" filterable @change="changeExportProject" />
            </FFormItem>
            <FFormItem v-if="downloadRule" prop="rule_ids" :label="`规则列表`">
                <FSelectTree
                    v-model="exportForm.rule_ids"
                    multiple
                    filterable
                    :data="ruleList"
                    :clearable="false"
                    cascade
                    virtualList
                    checkStrictly="parent"
                    collapseTags
                    :collapseTagsLimit="2"
                >
                </FSelectTree>
                <!-- <FSelect v-model="exportForm.rule_ids" :placeholder="`请选择需要导出的规则`" :options="ruleList" filterable multiple clearable valueField="rule_id" labelField="rule_name" /> -->
            </FFormItem>
            <FFormItem prop="download_type" :label="$t('_.导出方式')">
                <FSelect v-model="exportForm.download_type" :placeholder="$t('_.请选择导出方式')" :options="exportTypeList" :disabled="props.downloadRule" @change="changeDownloadType" />
            </FFormItem>
            <template v-if="exportForm.download_type === 2">
                <GitBase ref="gitBaseRef" v-model:gitForm="exportForm" mode="edit" portmode="export"></GitBase>
            </template>
            <FFormItem :label="$t('_.差异化变量')" class="diff-form-label">
                <DifVariables ref="difVariablesRef" parentType="export"></DifVariables>
            </FFormItem>
        </FForm>
        <template #footer>
            <FSpace :size="CONDITIONBUTTONSPACE" justify="end">
                <FButton @click="handleCancel">{{$t('common.cancel')}}</FButton>
                <FButton type="primary" :loading="submitLoading" @click="handleSave">{{$t('common.ok')}}</FButton>
            </FSpace>
        </template>
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
import { useDataList } from '@/pages/projects/components/ImExport/hooks/useDataList';
import { downloadProjectByGit, fetchProjectAllRules } from '@/pages/projects/api.js';
import { isIE, forceDownload } from '@/assets/js/utils';
import { adaptDataToTreeSelect, getFlatDataFormTreeSelect } from '@/common/utils';
import useExport from '@/pages/projects/hooks/useExport';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import { cloneDeep } from 'lodash-es';

const { t: $t } = useI18n();
const {
    projectNameList,
    exportTypeList,
    loadProjectNameList,
    updateGitForm,
} = useDataList();
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    pName: {
        type: String,
        required: false,
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
    // 下载规则
    downloadRule: {
        type: Boolean,
        required: false,
        default: false,
    },
});
const ruleList = ref([]);
const modalTitle = computed(() => (props.downloadRule ? $t('myProject.downloadRule') : $t('myProject.downloadProject')));
const emit = defineEmits(['update:show']);
const initExportForm = () => {
    if (props.downloadRule) {
        exportTypeList.value = [
            {
                value: 1,
                label: $t('_.导出到本地'),
            },
        ];
        return {
            download_type: 1,
            dif_array: [],
            rule_ids: [],
        };
    }
    if (props.type === 'detail') {
        return {
            download_type: 1,
            git_branch: props.project.git_branch,
            git_type: props.project.git_type,
            git_repo: props.project.git_repo,
            git_root_dir: props.project.git_root_dir || `dqm/${props.project?.project_name || ''}`,
            dif_array: [],
        };
    }
    return {
        download_type: 1,
        git_branch: 'master',
        git_type: 1,
        git_repo: '',
        git_root_dir: 'dqm/',
        dif_array: [],
    };
};
const {
    exportFileByParams,
} = useExport('project');
const submitLoading = ref(false);
const exportForm = ref(initExportForm());
const gitBaseRef = ref(null);
const exportFormRef = ref(null);
const difVariablesRef = ref(null);
const initialState = useModel('@@initialState');
const isPermissionFunc = ref(null);
const curPid = computed(() => props.project?.project_id || exportForm.value.project_id);
const cacheProject = ref({});
watchEffect(async () => {
    console.log(`curId changed to ${curPid.value}`);
    const {
        isPermissionCompute,
    } = useProjectAuth(curPid.value, initialState.userName);
    isPermissionFunc.value = isPermissionCompute;
});
const changeExportProject = async () => {
    await updateGitForm(curPid.value, exportForm.value);
    cacheProject.value = cloneDeep(exportForm.value);
    difVariablesRef.value.clearData();
};
const changeDownloadType = (e) => {
    if (e === 2) {
        exportForm.value.git_type = cacheProject.value?.git_type || 1;
        exportForm.value.git_branch = cacheProject.value?.git_branch || 'master';
        exportForm.value.git_repo = cacheProject.value?.git_repo || '';
        exportForm.value.git_root_dir = cacheProject.value?.git_root_dir || `dqm/${cacheProject.value?.project_name || ''}`;
        exportForm.value.git_commit = cacheProject.value?.git_commit || '';
    }
    difVariablesRef.value.clearData();
};
const handleInitExportForm = () => {
    const curUploadType = exportForm.value.download_type;
    exportForm.value = initExportForm();
    exportForm.value.download_type = curUploadType;
    difVariablesRef.value.clearData();
};
const handleCancel = () => {
    emit('update:show', false);
    exportForm.value = initExportForm();
};
const handleParams = () => {
    const {
        // eslint-disable-next-line camelcase
        download_type, git_repo, git_type, git_branch, git_root_dir, rule_ids = [], git_commit,
    } = exportForm.value;
    let params = {};
    if (exportForm.value.download_type === 2) {
        params = {
            download_type, git_repo, git_type, git_branch, git_root_dir, git_commit,
        };
    } else {
        params = {
            download_type,
        };
    }
    // eslint-disable-next-line camelcase
    if (props.downloadRule) params.rule_ids = getFlatDataFormTreeSelect(rule_ids, ruleList.value);
    const curId = props.project?.project_id || exportForm.value.project_id;
    params.project_ids = [curId];
    params.diff_variables = toRaw(difVariablesRef.value.getDifData());
    return params;
};

const handleSave = async () => {
    try {
        let validR = [];
        if (exportForm.value.download_type === 1) {
            validR = await Promise.all([exportFormRef.value.validate(), difVariablesRef.value.valid()]);
        } else {
            validR = await Promise.all([gitBaseRef.value.valid(), exportFormRef.value.validate(), difVariablesRef.value.valid()]);
        }
        let isExport = true;
        if (props.type === 'table') { // type = detail,项目详情中在打开前已做权限校验
            isExport = await isPermissionFunc.value();
        }
        if (!validR.includes(false) && isExport) {
            submitLoading.value = true;
            const params = handleParams();
            console.log('params', params);
            // 本地导出
            if (exportForm.value.download_type === 1) {
                await exportFileByParams(params);
            } else {
                // git导出
                await downloadProjectByGit(params);
                FMessage.success($t('_.导出成功'));
            }
            handleCancel();
            submitLoading.value = false;
        }
        if (!isExport) {
            FMessage.warning($t('_.导出权限不足'));
        }
    } catch (e) {
        FMessage.error($t('toastError.actionFail'));
        console.log(e);
        submitLoading.value = false;
    }
};

const exportFormRule = ref({
    project_name: [
        {
            required: true, message: $t('_.不能为空'),
        },
    ],
    project_id: [
        {
            type: 'number', trigger: ['change', 'blur'], required: true, message: $t('_.不能为空'),
        },
    ],
    download_type: [
        {
            type: 'number', required: true, message: $t('_.不能为空'),
        },
    ],
    rule_ids: [
        {
            type: 'array', trigger: ['change', 'blur'], required: true, message: $t('_.不能为空'),
        },
    ],
});
const handleRuleDetails = (data) => {
    if (!Array.isArray(data)) return {};
    const temp = {};
    data.forEach((item) => {
        if (Object.keys(temp).includes(item.rule_group_name)) {
            temp[item.rule_group_name].push(item);
        } else {
            temp[item.rule_group_name] = [item];
        }
    });
    return temp;
};
const loadRuleList = async () => {
    try {
        const res = await fetchProjectAllRules(props.project.project_id);
        const originAllRules = handleRuleDetails(res.rule_details || []);
        // projectDetail中的规则为当前页的规则数据，替换为全部的规则数据
        let index = 0;
        Object.keys(originAllRules).forEach((key) => {
            originAllRules[key].forEach((subkey) => {
                ruleList.value[index++] = toRaw(subkey);
            });
        });
        ruleList.value = adaptDataToTreeSelect(ruleList.value);
        console.log('rules', ruleList.value);
    } catch (err) {
        console.warn(err);
    }
};
onMounted(async () => {
    if (props.type === 'detail') {
        exportForm.value.project_name = props.project.project_name;
    }
    await loadProjectNameList(1);
    if (props.downloadRule) {
        await loadRuleList();
    }
});

</script>
<style lang="less" scoped>
:deep(.diff-form-label){
    .fes-form-item-content{
        display: flow-root;
    }
}
</style>
