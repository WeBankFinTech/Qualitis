import { request as FRequest } from '@fesjs/fes';

// 获取普通项目
export function fetchProjects(params = {}) {
    return FRequest('/api/v1/projector/project/query', params);
}

// 获取工作流项目
export function fetchWorkflowProjects(params = {}) {
    return FRequest('/api/v1/projector/project/workflow/query', params);
}

// 获取项目详情
export function fetchProjectDetail(projectId, params = {}) {
    return FRequest(`/api/v1/projector/project/query/rules/${projectId}`, params);
}

// 获取项目下的所有规则
export function fetchProjectAllRules(projectId, params = {}) {
    return FRequest(`/api/v1/projector/project/all/rules/${projectId}`, params, { method: 'GET' });
}

export function fetchProjectHistory(projectId, params = {}) {
    return FRequest(`/api/v1/projector/project/event/${projectId}`, params);
}

export function fetchProjecTaskRecord(params = {}) {
    return FRequest('/api/v1/projector/application/filter/advance', params);
}

export function fetchProjectUserData(projectId, params = {}) {
    return FRequest(`/api/v1/projector/project_user/all/${projectId}`, params);
}

export function fetchProjectUserList() {
    return FRequest('/api/v1/projector/project_user/user', {}, { method: 'GET', cache: true });
}

// 新建普通项目
export function addProject(params = {}) {
    return FRequest('/api/v1/projector/project', params, { method: 'PUT' });
}

// 删除普通项目
export function deleteProject(params = {}) {
    return FRequest('/api/v1/projector/project/delete', params);
}

// 导入普通项目
export function uploadProject(params = {}) {
    return FRequest('/api/v1/projector/project/batch/upload', params);
}

// 导入规则
export function uploadRules(params = {}, projectId) {
    return FRequest(`/api/v1/projector/rule/batch/upload/${projectId}`, params);
}

// 导出普通项目（支持批量） -- 本地导出
export function downloadProject(params = {}) {
    return FRequest('/api/v1/projector/project/batch/download', params, {
        useResponse: true,
        responseType: 'blob',
        headers: {
            'Content-Language': localStorage.getItem('currentLanguage') || 'zh_CN',
        },
    });
}

// 导出普通项目 -- git导出
export function downloadProjectByGit(params = {}) {
    return FRequest('/api/v1/projector/project/batch/download_files_to_git', params);
}

// 导出普通项目规则（支持批量）
export function downloadProjectRules(params = {}) {
    return FRequest('/api/v1/projector/rule/batch/download', params, {
        useResponse: true,
        responseType: 'blob',
        headers: {
            'Content-Language': localStorage.getItem('currentLanguage') || 'zh_CN',
        },
    });
}

// 项目代码仓库配置修改
export function gitModify(params = {}) {
    return FRequest('/api/v1/projector/project/git/modify', params);
}

// 项目代码仓库配置删除
export function gitConfigDelete(params = {}) {
    return FRequest('/api/v1/projector/project/git/delete', params);
}

// 执行项目
export function executeProject(params = {}) {
    return FRequest('/api/v1/projector/execution/project', params);
}

// 项目详情页执行规则
export function executeRuleInProject(params = {}) {
    return FRequest('/api/v1/projector/execution/rule', params);
}

// 任务查询页执行任务
export function executeTaskInTaskQuery(params = {}) {
    return FRequest('/api/v1/projector/execution/group', params);
}

// 执行参数模版部分
// 获取静态执行参数类别 -- 弃用
export function fetchStaticParamsType() {
    return FRequest('/api/v1/projector/execution_parameters/static/type/all', {}, {
        cache: true,
    });
}

// 获取动态引擎参数类型
export function fetchEngineParamsType() {
    return FRequest('/api/v1/projector/execution_parameters/dynamic/engine/all', {}, {
        cache: true,
    });
}

// 获取动态引擎参数名称与默认值
export function fetchEngineParamsNameAndInit(id) {
    if (!id) {
        return;
    }
    return FRequest(`/api/v1/projector/execution_parameters/engine/${id}`, {}, {
        method: 'GET',
        cache: true,
    });
}

// 获取执行变量类型
export function fetchExecutionVariableType() {
    return FRequest('/api/v1/projector/execution_parameters/execution/type/all', {}, {
        cache: true,
    });
}

// 获取执行变量名称
export function fetchExecutionVariableName() {
    return FRequest('/api/v1/projector/execution_parameters/execution/variable/all', {}, {
        cache: true,
    });
}

// 获取告警事件
export function fetchAlarmEvent() {
    return FRequest('/api/v1/projector/execution_parameters/alarm/type/all', {}, {
        cache: true,
    });
}

// 获取日期选择方式
export function fetchDateSelectionMethod() {
    return FRequest('/api/v1/projector/execution_parameters/select/method/all', {}, {
        cache: true,
    });
}

// 去噪策略
export function fetchEliminateStrategy() {
    return FRequest('/api/v1/projector/execution_parameters/noise/strategy/all', {}, {
        cache: true,
    });
}

// 新增 执行参数
export function fetchAddTemplate(params = {}) {
    return FRequest('/api/v1/projector/execution_parameters/add', params);
}

// 修改 执行参数
export function fetchModifyTemplate(params = {}) {
    return FRequest('/api/v1/projector/execution_parameters/modify', params);
}

// 删除 执行参数
export function fetchDeleteTemplate(params = {}) {
    return FRequest('/api/v1/projector/execution_parameters/delete', params);
}

// 执行参数 列表
export function fetchTemplateList(params = {}) {
    return FRequest('/api/v1/projector/execution_parameters/all', params);
}

// 执行参数 详情
export function fetchTemplateDetail(templateId, params = {}) {
    return FRequest(`/api/v1/projector/execution_parameters/${templateId}`, params, 'get');
}
// 单表修改
export function fetchModifyRule(params = {}) {
    return FRequest('/api/v1/projector/rule/modify', params);
}

// 获取集群列表
export function fetchClusterList(params = {}) {
    return FRequest('/api/v1/projector/meta_data/cluster', params);
}

// 获取数据库列表
export function fetchDB(params = {}) {
    return FRequest('/api/v1/projector/meta_data/db', params);
}

// 获取子系统列表
export function fetchSubSystemInfo(params = {}) {
    return Promise.resolve([]);
}

// 获取规则查询数据源等信息
export function fetchOptions() {
    return FRequest('/api/v1/projector/query/conditions', {}, 'get');
}

// 获取项目检验模板
export function fetchRuleTemplate() {
    return FRequest('/api/v1/projector/rule_template/option/list', {}, 'get');
}

// 获取调度任务列表
export function queryScheduledTaskList(params = {}) {
    return FRequest('/api/v1/projector/scheduledTask/query', params);
}

// 获取调度任务详情
export function queryScheduledTask(id) {
    return FRequest(`/api/v1/projector/scheduledTask/${id}`, {}, 'get');
}

// 修改调度任务(表单)
export function modifyScheduledTask(params = {}) {
    return FRequest('/api/v1/projector/scheduledTask/modify', params);
}
// 修改调度任务(列表)
export function modifyTableScheduledTask(params = {}) {
    return FRequest('/api/v1/projector/scheduledTask/rule/group/modify', params);
}
// 新增调度任务
export function addScheduledTask(params = {}) {
    return FRequest('/api/v1/projector/scheduledTask/add', params);
}

// 删除调度任务
export function deleteScheduledTask(params = {}) {
    return FRequest('/api/v1/projector/scheduledTask/delete', params);
}

// 获取调度系统枚举值
export function getAllScheduledTaskSystem() {
    return FRequest('/api/v1/projector/scheduledTask/system/all', {});
}

// 获取项目下拉框数据
export function getAllWtssProject(params = {}) {
    return FRequest('/api/v1/projector/scheduled/config/get/All/project', params);
}

// 获取项目下的工作流
export function getAllWtssWorkFlow(params = {}) {
    return FRequest('/api/v1/projector/scheduled/config/getWorkFlow', params);
}
// 获取任务下拉框数据
export function getAllWtssTask(params = {}) {
    return FRequest('/api/v1/projector/scheduled/config/getTask', params);
}
// 获取项目下的规则组(会过滤在定时任务的规则组)
export function getScheduledTaskRuleGroup(projectId) {
    return FRequest(`/api/v1/projector/scheduledTask/rule/group/${projectId}`, {}, 'get');
}
// 获取查询组件的规则组列表
export function getQueryOptionsRuleGroup(params = {}) {
    return FRequest('/api/v1/projector/scheduledTask/table/rule/group', params);
}
// 获取指定调度任务下的规则组
export function getScheduledTaskRules(params = {}) {
    return FRequest('/api/v1/projector/scheduledTask/get/rule/group', params);
}

// 根据集群获取发布用户
export function getReleaseUser(params = {}) {
    return FRequest('/api/v1/projector/scheduled/config/get/All/publish/user', params);
}

// 变量名称列表
export function getDiffVariables(params = {}) {
    return FRequest('/api/v1/projector/project/batch/diff_variables', params, 'get');
}
// 项目名称列表
export function getProjectNames(params = {}) {
    return FRequest('/api/v1/projector/project/util/all', params);
}

// 发布调度任务 任务类型（1-关联调度，2-发布调度）
export function releaseScheduledTask(params = {}) {
    return FRequest('/api/v1/projector/scheduledTask/release', params, 'post');
}

// 获取工作流项目相关筛选条件下拉
export function fetchWorkflowFilterList(params = {}) {
    return FRequest('/api/v1/projector/checkAlert/condition/list', params, {
        cache: true,
        method: 'post',
    });
}

// 获取告警规则表格数据
export function fetchAlarmRuleTableData(params = {}) {
    return FRequest('/api/v1/projector/checkAlert/query', params, 'post');
}
