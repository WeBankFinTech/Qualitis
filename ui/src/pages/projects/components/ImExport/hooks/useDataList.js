import { reactive, ref } from 'vue';
import { getDiffVariables, getProjectNames, fetchProjectDetail } from '@/pages/projects/api';
import { locale } from '@fesjs/fes';

const $t = locale.t;


export const useDataList = () => {
    const overseasVersion = sessionStorage.getItem('overseas_external_version');
    // 导入
    const importTypeList = ref(overseasVersion === 'true' ? [
        {
            value: 1,
            label: $t('_.从本地导入'),
        },
    ] : [
        {
            value: 2,
            label: $t('_.从Git导入'),
        },
        {
            value: 1,
            label: $t('_.从本地导入'),
        },
    ]);

    // 导出
    const exportTypeList = ref(overseasVersion === 'true' ? [
        {
            value: 1,
            label: $t('_.导出到本地'),
        },
    ] : [
        {
            value: 2,
            label: $t('_.导出到Git'),
        },
        {
            value: 1,
            label: $t('_.导出到本地'),
        },
    ]);
    // 变量类型
    const variableTypeList = ref([
        {
            value: 1,
            label: $t('_.内置'),
        },
        {
            value: 2,
            label: $t('_.自定义'),
        },
        {
            value: 3,
            label: $t('_.自定义Sql'),
        },
        {
            value: 4,
            label: $t('_.自定义Json'),
        },
    ]);
    // 变量名称
    const variableNameList = ref([]);
    const loadVariableNameList = async () => {
        try {
            const result = await getDiffVariables();
            variableNameList.value = result;
        } catch (error) {
            console.warn('loadVariableName', error);
        }
    };
    // 项目名称
    const projectNameList = ref([]);
    const loadProjectNameList = async (type) => {
        try {
            const params = { project_type: null };
            params.project_type = type || null;
            const result = await getProjectNames(params);
            projectNameList.value = result.data.map(item => ({
                value: item.project_id,
                label: item.project_name,
            }));
        } catch (error) {
            console.warn('loadProjectName', error);
        }
    };
    // 切换项目名称时，获取对应git配置信息
    const updateGitForm = async (pid, form) => {
        try {
            const res = await fetchProjectDetail(pid, {
                page: 0,
                size: 10,
            });
            form.git_type = res.project_detail?.git_type || 1;
            form.git_repo = res.project_detail?.git_repo;
            form.git_branch = res.project_detail?.git_branch || 'master';
            form.git_root_dir = res.project_detail?.git_root_dir || `dqm/${res.project_detail?.project_name || ''}`;
        } catch (e) {
            console.log(e);
        }
    };
    return {
        importTypeList,
        projectNameList,
        exportTypeList,
        variableTypeList,
        variableNameList,
        loadVariableNameList,
        loadProjectNameList,
        updateGitForm,
    };
};
