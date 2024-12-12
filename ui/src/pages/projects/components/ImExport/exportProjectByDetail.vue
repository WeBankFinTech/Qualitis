<template>
    <FModal :show="show" :width="600"
            :title="$t('myProject.downloadProject')"
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
            <FFormItem v-if="type === 'detail'" prop="project_name" :label="`项目名称`">
                <FInput v-model="exportForm.project_name" :disabled="true" />
            </FFormItem>
            <FFormItem v-else prop="project_id" :label="`项目名称`">
                <FSelect v-model="exportForm.project_id" :placeholder="`请选择需要导出的项目`" :options="projectNameList" filterable @change="changeExportProject" />
            </FFormItem>
            <FFormItem prop="download_type" :label="`导出方式`">
                <FSelect v-model="exportForm.download_type" :placeholder="`请选择导出方式`" :options="exportTypeList" @change="changeDownloadType" />
            </FFormItem>
            <FFormItem :label="`差异化变量`" class="diff-form-label">
                <DifVariables ref="difVariablesRef" parentType="export"></DifVariables>
            </FFormItem>
        </FForm>
        <template #footer>
            <FSpace :size="CONDITIONBUTTONSPACE" justify="end">
                <FButton @click="handleCancel">{{$t('common.cancel')}}</FButton>
                <FButton type="primary" :loading="submitLoading" @click="handleSave">{{$t('common.save')}}</FButton>
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
import DifVariables from '@/pages/projects/components/ImExport/difVariables';
import { useDataList } from '@/pages/projects/components/ImExport/hooks/useDataList';
import { isIE, forceDownload } from '@/assets/js/utils';
import useExport from '@/pages/projects/hooks/useExport';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import { cloneDeep } from 'lodash-es';

const { t: $t } = useI18n();
const {
    projectNameList,
    exportTypeList,
    loadProjectNameList,
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
});
const emit = defineEmits(['update:show']);
const initExportForm = () => ({
    download_type: 1,
    dif_array: [],
});
const {
    exportFileByParams,
} = useExport('project');
const submitLoading = ref(false);
const exportForm = ref(initExportForm());
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
    cacheProject.value = cloneDeep(exportForm.value);
    difVariablesRef.value.clearData();
};
const changeDownloadType = (e) => {
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
        download_type,
    } = exportForm.value;
    const params = { download_type };
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
            }
            handleCancel();
            submitLoading.value = false;
        }
        if (!isExport) {
            FMessage.warning('导出权限不足');
        }
    } catch (e) {
        console.log(e);
        submitLoading.value = false;
    }
};

const exportFormRule = ref({
    project_name: [
        {
            required: true, message: '不能为空',
        },
    ],
    project_id: [
        {
            type: 'number', trigger: ['change', 'blur'], required: true, message: '不能为空',
        },
    ],
    download_type: [
        {
            type: 'number', required: true, message: '不能为空',
        },
    ],
});
onMounted(async () => {
    if (props.type === 'detail') {
        exportForm.value.project_name = props.project.project_name;
    }
    await loadProjectNameList();
});
</script>
<style lang="less" scoped>
:deep(.diff-form-label){
    .fes-form-item-content{
        display: flow-root;
    }
}
</style>
