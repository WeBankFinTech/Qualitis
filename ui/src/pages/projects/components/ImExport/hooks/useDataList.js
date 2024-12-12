import { reactive, ref } from 'vue';
import { getDiffVariables, getProjectNames, fetchProjectDetail } from '@/pages/projects/api';

export const useDataList = () => {
    // 导入
    const importTypeList = ref([
        {
            value: 1,
            label: '从本地导入',
        },
    ]);
    // 导出
    const exportTypeList = ref([
        {
            value: 1,
            label: '导出到本地',
        },
    ]);
    // 变量类型
    const variableTypeList = ref([
        {
            value: 1,
            label: '内置',
        },
        {
            value: 2,
            label: '自定义',
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
    const loadProjectNameList = async () => {
        try {
            const result = await getProjectNames();
            projectNameList.value = result.data.map(item => ({
                value: item.project_id,
                label: item.project_name,
            }));
        } catch (error) {
            console.warn('loadProjectName', error);
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
    };
};
