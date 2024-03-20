import { ref } from 'vue';
import { fetchProjectUserData, fetchProjectUserList } from '../api';

export default function useProjectAuth(projectId, userName) {
    let permissionDataAll = [];
    const permissionData = ref([]);
    const roleList = ref([]);
    // 处理项目用户的权限数据, 便于表格显示
    const factoryProjectUserData = (data) => {
        // 判断是否为数组
        if (Array.isArray(data)) {
            // 存储权限
            let role = [];
            // 1为管理员，2为编辑，3为运行，4为查看
            // 如果data.permission []  包含1则所有权限都有
            const list = data.map((item) => {
                const obj = {};
                obj.name = userName === item.authorized_user ? `${item.authorized_user}(you)` : item.authorized_user;
                if (userName === item.authorized_user) role = item.permission || [];
                // 如果permission后台未给参数则默认[]
                obj.permission = item.permission || [];
                // 根据是否包含分别赋值
                if (obj.permission.includes(1)) {
                    obj.admin = true;
                    obj.edit = true;
                    obj.run = true;
                    obj.view = true;
                } else {
                    obj.admin = false;
                    obj.edit = obj.permission.includes(2);
                    obj.run = obj.permission.includes(3);
                    obj.view = obj.permission.includes(4);
                }
                obj.hidden = item.hidden;
                return obj;
            });
            return { role, list };
        }
        return new Error('data is not Array');
    };

    // qualitis注册用户列表
    const projectUserList = ref([]);

    // 在项目详情能够授权的用户列表
    const getProjectUserList = async () => {
        try {
            const res = await fetchProjectUserList();
            projectUserList.value = Array.isArray(res) ? res.filter(val => val) : [];
        } catch (err) {
            console.warn(err);
        }
    };

    // 在项目详情获取项目已经授权的用户信息列表
    const getProjectUserData = async () => {
        const res = await fetchProjectUserData(projectId, {});
        permissionDataAll = res || [];
        const factorData = factoryProjectUserData(permissionDataAll);
        permissionData.value = factorData.list
            .filter(item => !item.hidden);
        roleList.value = factorData.role;
    };

    // 用于判断是否包含已添加的账号
    const isProjectUser = (name) => {
        // 取出已经授权的用户名
        const list = permissionDataAll.map(item => item.authorized_user);
        return list.includes(name);
    };

    // 用于判断登录用户是否有导入导出该项目的权限
    const isPermissionCompute = async () => {
        const res = await fetchProjectUserData(projectId, {});
        permissionDataAll = res || [];
        const factorData = factoryProjectUserData(permissionDataAll);
        const pData = factorData.list;
        let isPermission = false;
        pData.forEach((pe) => {
            if (pe.name === `${userName}(you)` || pe.name === userName) {
                isPermission = pe.permission.includes(1) || pe.permission.includes(2);
            }
        });
        return isPermission;
    };
    return {
        permissionData,
        roleList,
        projectUserList,
        isProjectUser,
        getProjectUserList,
        getProjectUserData,
        isPermissionCompute,
    };
}
