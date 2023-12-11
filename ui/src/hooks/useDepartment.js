import { ref } from 'vue';

// eslint-disable-next-line camelcase
export default function useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions) {
    const selectDevDate = ref([]);
    const selectOpsDate = ref([]);
    const selectVisDate = ref([]);
    const handleDevId = (value) => {
        selectDevDate.value = devCurSubDepartData.value.find(item => item.value === value[1])?.code;
        console.log('开发', selectDevDate.value, devCurSubDepartData.value);
    };
    const handleOpsId = (value) => {
        selectOpsDate.value = opsCurSubDepartData.value.find(item => item.value === value[1])?.code;
        console.log('运维', selectOpsDate.value, opsCurSubDepartData.value);
    };
    const childDate = [];
    const visSelectChange = (value) => {
        childDate.push(...visCurSubDepartData.value);
        selectVisDate.value = value.map((item) => {
            let id = 0;
            if (item.length === 1) {
                id = visDivisions.value.find(v => v.value === item[0])?.code;
            } else {
                id = childDate.find(v => v.value === item[1])?.code;
            }
            const name = item.join('/');
            return { id, name };
        });
        console.log('可见范围', selectVisDate.value);
    };
    return {
        selectDevDate,
        selectOpsDate,
        selectVisDate,
        handleDevId,
        handleOpsId,
        visSelectChange,
    };
}
