import { ref, onMounted } from 'vue';
import { fetchDepartmentsDivisionsByCode } from '@/pages/metricManagement/api';
import { getDepartment } from '@/pages/system/api';
import { INTMAXVALUE } from '@/assets/js/const';

export default function useDivisions() {
    const divisions = ref([]);
    const curSubDepartData = ref([]);

    const buildDivisionOption = data => ({
        label: data.department_name,
        value: data.department_name,
        code: data.department_code,
        id: data.department_id,
        isLeaf: false,
    });

    const getDivisionsByDepartmentName = async (option) => {
        let temp = null;
        try {
            if (option.code) {
                temp = await fetchDepartmentsDivisionsByCode(option.code);
            } else {
                console.log('部门code为空，无法查询科室数据。');
            }
        } catch (e) {
            console.error(e.message);
        }
        if (!Array.isArray(temp)) return [];
        return temp.map(item => ({
            label: item.department_sub_name,
            value: `${option.value}/${item.department_sub_name}`,
            code: item.department_sub_id,
            isLeaf: true,
        }));
    };

    // 手动触发下一级节点列表，返回列表的形式
    const loadDivisions = async (option) => {
        if (!option) {
            return;
        }
        let temp = null;
        try {
            const findIndex = curSubDepartData.value.findIndex(item => item.code === option.code);
            // 若该子节点数据已缓存，不需要再请求接口，直接返回缓存数据
            if (findIndex === -1) {
                console.log('curSubDepartData中未缓存', option.label);
                temp = await getDivisionsByDepartmentName(option);
                curSubDepartData.value.push(
                    {
                        code: option.code,
                        id: option.id,
                        label: option.label,
                        subDep: temp,
                    },
                );
                console.log('curSubDepartData.value', curSubDepartData.value);
            } else {
                console.log('curSubDepartData中已缓存', option.label);
                temp = curSubDepartData.value[findIndex];
            }
            console.log('loadDivisions || option:', option, ' || children:', temp);
            // 初始化兜底，确保子级数据与父级数据关联上
            if (!option.children) {
                option.children = temp;
            }
        } catch (e) {
            console.warn(e.message);
        }
        return temp;
    };

    const initGetDepartments = async () => {
        try {
            const r = await getDepartment({
                page: 0,
                size: 100000000,
            });
            const resp = r.data;
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
