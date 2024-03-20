import { request as FRequest } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
// initial state
const state = () => ({
    // 普通规则组的规则列表
    ruleList: [],
    // 加载中
    isLoading: true,
    // 保存中
    isSaving: false,
    currentProject: {
        groupId: '',
        groupName: '',
        projectId: '',
        // dss传入的url参数
        dssParams: '',
        // 普通规则组项目
        isNormalProject: true,
        // 当前是否在iframe中使用
        isEmbedInFrame: window.top !== window.self,
        // 工作流项目
        isWorkflowProject: false,
        // 当前工作模式edit,display,create，一次只能编辑一个规则，所以除去display之外，都是编辑模式，只是create的时候tab需要处理的操作不同
        editMode: 'display',
        // 表规则组的校验对象的edit标志位
        groupObjectEditMode: 'display',
        // 表规则组的校验规则的edit标志位
        groupRuleEditMode: 'display',
    },
    // 当前tab编辑的具体规则信息，因为信息不全，所以没办法在详情页里面用
    currentRule: {
        rule_id: '',
        rule_type: '',
        table_type: '',
    },
    // 校验字段，根据数据源的数据表而变化
    verifyColumns: [],
    // 单表校验模版
    singleCheckTemplateList: [],
    // 文件校验模版
    documentCheckTemplateList: [],
    // 通过详情页接口返回来的原始具体规则信息
    currentRuleDetail: {},
});

// getters
const getters = {
    getCurrentProject: cstate => cstate.currentProject,
};

// actions
const actions = {
    // 获取普通规则组的规则列表
    async getGroupDetailList({
        commit, state: cstate,
    }) {
        try {
            commit('setLoading', true);
            const result = await FRequest(`api/v1/projector/rule/group/${cstate.currentProject.groupId}`, { t: Date.now() }, 'get');
            if (Array.isArray(result.rule_list) && result.rule_list.length > 0) {
                const mapRuleList = result.rule_list.map((item, index) => {
                    item.label = item.rule_name;
                    item.value = index;
                    return item;
                });
                // 更新普通规则组的规则列表
                commit('setRuleList', mapRuleList);
                // 更新规则组名
                commit('setCurrentProject', {
                    groupName: result.rule_group_name,
                });
            } else {
                commit('setRuleList', []);
            }
            commit('setLoading', false);
        } catch (err) {
            console.warn(err);
            commit('setLoading', false);
        }
    },
    addDataToGroupDetailList({
        commit, state: cstate,
    }, data) {
        const currentRuleList = cstate.ruleList.slice(0);
        currentRuleList.push(data);
        commit('setRuleList', currentRuleList);
    },
    deleteDataFromGroupDetailList({
        commit, state: cstate,
    }) {
        const currentRuleList = cstate.ruleList.slice(0);
        currentRuleList.splice(-1);
        commit('setRuleList', currentRuleList);
    },
    deleteDataFromGroupDetailListByIndex({
        commit, state: cstate,
    }, index) {
        const currentRuleList = cstate.ruleList.slice(0);
        currentRuleList.splice(index, 1);
        commit('setRuleList', currentRuleList);
    },
    updateGroupDetailList({
        commit, state: cstate,
    }, { rule_id, rule_name }) {
        // 用store中数组执行map操作vuex会报错
        const tempList = cloneDeep(cstate.ruleList);
        const currentRuleList = tempList.map((item) => {
            // eslint-disable-next-line camelcase
            if (item.rule_id === rule_id) {
                // eslint-disable-next-line camelcase
                item.rule_name = rule_name;
                // eslint-disable-next-line camelcase
                item.label = rule_name;
            }
            return item;
        });
        commit('setRuleList', currentRuleList);
    },
    // 获取编辑的具体规则信息
    async getRuleDetail({
        commit,
    }) {
        const currentRule = await FRequest.getProducts();
        commit('setRuleList', currentRule);
    },
    updateCurrentRule({
        commit, state: cstate,
    }, data) {
        commit('setCurrentRule', Object.assign({}, cstate.currentRule, data));
    },
};

// mutations
const mutations = {
    // 更新普通规则组的规则列表
    setRuleList(cstate, ruleList = []) {
        cstate.ruleList = ruleList;
    },
    // 更新当前编辑的具体规则信息
    setCurrentProject(cstate, currentProject = {}) {
        cstate.currentProject = Object.assign({}, cstate.currentProject, currentProject);
    },
    // 更新tab当前编辑的具体规则信息
    setCurrentRule(cstate, currentRule = {}) {
        cstate.currentRule = currentRule;
    },
    // 更新loading
    setLoading(cstate, status = false) {
        cstate.isLoading = status;
    },
    // 更新saving
    setSaving(cstate, status = false) {
        cstate.isSaving = status;
    },
    // 更新具体编辑规则信息
    setCurrentRuleDetail(cstate, currentRuleDetail = {}) {
        cstate.currentRuleDetail = currentRuleDetail;
    },
    // 更新具体编辑规则信息
    updateCurrentRuleDetail(cstate, currentRuleDetail = {}) {
        cstate.currentRuleDetail = Object.assign({}, cstate.currentRuleDetail, currentRuleDetail);
    },
    setVerifyColumns(cstate, verifyColumns = []) {
        cstate.verifyColumns = verifyColumns;
    },
    setSingleCheckTemplateList(cstate, checkTemplateList = []) {
        cstate.singleCheckTemplateList = checkTemplateList;
    },
    setDocumentCheckTemplateList(cstate, checkTemplateList = []) {
        cstate.documentCheckTemplateList = checkTemplateList;
    },
    // 清空所有信息
    clearCurrentRuleData(cstate) {
        cstate.currentRuleDetail = {};
        cstate.currentRule = {};
        cstate.currentProject = {
            groupId: '',
            groupName: '',
            projectId: '',
            // dss传入的url参数
            dssParams: '',
            // 普通规则组项目
            isNormalProject: true,
            // 当前是否在iframe中使用
            isEmbedInFrame: window.top !== window.self,
            // 工作流项目
            isWorkflowProject: false,
            // 当前工作模式edit,display,create，一次只能编辑一个规则，所以除去display之外，都是编辑模式，只是create的时候tab需要处理的操作不同
            editMode: 'display',
            // 表规则组的校验对象的edit标志位
            groupObjectEditMode: 'display',
            // 表规则组的校验规则的edit标志位
            groupRuleEditMode: 'display',
        };
        cstate.ruleList = [];
    },
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations,
};
