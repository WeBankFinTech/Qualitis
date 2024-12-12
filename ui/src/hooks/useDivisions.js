import { ref, onMounted } from 'vue';
import { fetchDepartments, fetchDepartmentsDivisionsByCode } from '@/pages/metricManagement/api';

export default function useDivisions(rulesTemplate) {
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
            temp = await fetchDepartmentsDivisionsByCode(params);
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
        console.log(rulesTemplate, option, 'rulesTemplate');
        if (rulesTemplate && +option.code === 1) {
            temp = [
                {
                    label: '全行部门', value: '全行部门', code: '0', isLeaf: true,
                },
            ];
        } else {
            try {
                temp = await getDivisionsByDepartmentName(option.code);
            } catch (e) {
                console.warn(e.message);
            }
        }
        console.log('loadDivisions || option:', option, ' || children:', temp);
        curSubDepartData.value = temp;
        if (!option.children) option.children = temp;
        return temp;
    };
    // 给department兜底
    const initDepartment = async (val) => {
        for (let i = 0; i < val?.length; i++) {
            // eslint-disable-next-line no-use-before-define
            await loadDivisions(divisions.value.find(item => val[i].name.split('/')[0] === item.label));
        }
    };
    const initGetDepartments = async () => {
        try {
            const resp = await fetchDepartments();
            if (!Array.isArray(resp)) return;
            const allDep = {
                disable: '0',
                department_code: '1',
                department_name: '全行',
            };
            if (rulesTemplate && +resp[0].department_code !== 1) { resp.splice(0, 0, allDep); } // 规则模板、标准值可见范围选择需要全行
            if (!rulesTemplate && +resp[0].department_code === 1) {
                resp.splice(0, 1);
            } // 其他情况不需要
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
        initDepartment,
    };
}
