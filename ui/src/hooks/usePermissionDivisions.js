import { ref, onMounted } from 'vue';
import { fetchRuleDepartments, fetchRuleDepartmentsDivisionsByCode } from '@/pages/rules/template/api';

// 与useDivision区别是 此部门选择 仅有自己管理的部门科室 普通用户无数据
export default function usePermissionDivisions() {
    const divisions = ref([]);
    const curSubDepartData = ref([]);
    const buildDivisionOption = data => ({
        label: data.department_name,
        value: data.department_name,
        code: data.department_code,
        disabled: data.disable === '1',
        id: data.department_id,
        isLeaf: false,
    });

    const getDivisionsByDepartmentName = async (params) => {
        let temp = null;
        try {
            temp = await fetchRuleDepartmentsDivisionsByCode(params);
        } catch (e) {
            console.error(e.message);
        }
        if (!Array.isArray(temp)) return [];
        return temp.map(item => ({
            label: item.department_sub_name,
            value: item.department_sub_name,
            code: item.department_sub_id,
            isLeaf: true,
        }));
    };

    // 手动触发下一级节点列表，返回列表的形式
    const loadDivisions = async (option) => {
        if (!option || option.disabled) {
            return;
        }
        let temp = null;
        try {
            temp = await getDivisionsByDepartmentName(option.code);
        } catch (e) {
            console.warn(e.message);
        }
        console.log('loadDivisions || option:', option, ' || children:', temp);
        curSubDepartData.value = temp;
        if (!option.children) option.children = temp;
        return temp;
    };

    const initGetDepartments = async () => {
        try {
            const resp = await fetchRuleDepartments();
            if (!Array.isArray(resp)) return;
            divisions.value = resp.map(buildDivisionOption);
        } catch (e) {
            console.warn(e.message);
        }
    };

    onMounted(() => {
        initGetDepartments();
    });

    return {
        divisions,
        loadDivisions,
        curSubDepartData,
    };
}
