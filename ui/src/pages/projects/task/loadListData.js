import {
    request as FRequest,
} from '@fesjs/fes';

// 加载调度系统枚举值
export const loadSysTypeList = async () => {
    try {
        const result = await FRequest('/api/v1/projector/scheduledTask/system/all', {}, { method: 'post', cache: true });
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadSysTypeList Error:', error);
        return false;
    }
};

// 加载发布用户列表
export const loadPublishUserList = async (param = { cluster: 'BDP-DEV' }) => {
    try {
        const result = await FRequest('/api/v1/projector/scheduled/config/get/All/publish/user', param, {
            method: 'POST',
            caches: 'true',
        });
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadPublishUserList Error:', error);
        return false;
    }
};

// 加载代理人列表
export const loadProxyUserList = async () => {
    try {
        const result = await FRequest('/api/v1/projector/proxy_user', {}, {
            method: 'GET',
            mergeRequest: true,
        });
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadProxyUserList Error:', error);
        return false;
    }
};

// 加载规则组列表
export const loadRuleGroupList = async (param = { projectId: null }) => {
    try {
        const result = await FRequest(`/api/v1/projector/scheduled/project/rule/group/${param.projectId}`, {}, { method: 'get', cache: true });
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadRuleGroupList Error:', error);
        return false;
    }
};

// 加载集群列表
export const loadClusterList = async (param = {
    project_id: null,
    schedule_system: '',
    task_type: 2,
}) => {
    try {
        const result = await FRequest('/api/v1/projector/scheduledTask/option/cluster/list', param);
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadClusterList Error:', error);
        return false;
    }
};

// 加载抽屉页的项目列表
export const loadEditPageProjectList = async (param = {
    schedule_system: '',
    cluster: '',
    project_id: null,
}) => {
    try {
        const result = await FRequest('/api/v1/projector/scheduled/project/option/list', param);
        return result;
    } catch (error) {
        console.log('loadEditPageProjectList Error:', error);
        return false;
    }
};

// 加载查询组件的项目列表
export const loadProjectList = async (param = {
    project_id: null,
    schedule_system: '',
    task_type: 2,
    cluster: '',
}) => {
    try {
        const result = await FRequest('/api/v1/projector/scheduledTask/option/project/list', param);
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadProjectList Error:', error);
        return false;
    }
};

// 加载工作流列表
export const loadWorkflowList = async (param = {
    schedule_system: '',
    task_type: 2,
    cluster: '',
    project_name: '',
}) => {
    try {
        const result = await FRequest('/api/v1/projector/scheduledTask/option/workflow/list', param);
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadWorkflowList Error:', error);
        return false;
    }
};

// 加载任务列表
export const loadTaskList = async (param = {
    schedule_system: '',
    task_type: 2,
    cluster: '',
    project_name: '',
    work_flow: '',
}) => {
    try {
        const result = await FRequest('/api/v1/projector/scheduledTask/option/task/list', param);
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadTaskList Error:', error);
        return false;
    }
};

// 当查询条件为数据时，加载整个数据源，包含数据库、数据表的数据
export const loadDatasource = async (param = { project_id: null }) => {
    try {
        const result = await FRequest('/api/v1/projector/scheduledTask/option/publish/dbs', param, { caches: true, method: 'GET' });
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadDatasource Error:', error);
        return false;
    }
};

// 加载发布调度任务时的集群列表

export const loadPublishClusterList = async () => {
    try {
        const result = await FRequest('/api/v1/projector/meta_data/cluster', {});
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadPublishClusterList Error:', error);
        return false;
    }
};

// 当查询条件为数据时，加载表格数据库、表的数据
export const loadDBTable = async (params = {
    project_id: null,
    task_type: 1,
}) => {
    try {
        const result = await FRequest('/api/v1/projector/scheduledTask/data/set/query', params);
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadDBTable Error:', error);
        return false;
    }
};

// 获取下拉框初始数据
export const loadInitOptions = async (params = {
    task_type: 1,
    project_id: null,
}) => {
    try {
        const result = await FRequest('/api/v1/projector/scheduledTask/drop/down/list', params);
        console.log(result);
        return result;
    } catch (error) {
        console.log('loadDBTable Error:', error);
        return false;
    }
};
