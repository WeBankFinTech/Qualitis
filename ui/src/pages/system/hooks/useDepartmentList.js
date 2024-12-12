import { ref, onMounted } from 'vue';
import { fetchDepartmentsDivisionsByCodeCustom, getDepartment } from '../api';

export default function useDepartmentList() {
    const departmentList = ref([]);
    const roomList = ref([]);

    const getDepartmentList = async () => {
        try {
            const result = await getDepartment();
            departmentList.value = result.data.map(item => ({
                ...item,
                value: item.department_code,
                label: item.department_name,
            }));
        } catch (error) {
            console.log('getDepartmentList error:', error);
        }
    };

    const getRoomList = async (departmentCode) => {
        try {
            const result = await fetchDepartmentsDivisionsByCodeCustom(departmentCode);
            roomList.value = result.map(item => ({
                ...item,
                value: item.department_sub_id,
                label: item.department_sub_name,
            }));
        } catch (error) {
            console.log('getDepartmentList error:', error);
        }
    };

    onMounted(() => {
        getDepartmentList();
    });
    return {
        departmentList,
        roomList,
        getDepartmentList,
        getRoomList,
    };
}
