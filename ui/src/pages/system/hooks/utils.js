// department_name: "基础科技产品部/移动科技产品室"
export default function getDepId(devDivisions, departmentName) {
    if (!departmentName) {
        return;
    }
    const parentDepName = departmentName.split('/')[0];
    const id = devDivisions.find(item => parentDepName === item.label).id;
    return id;
}
