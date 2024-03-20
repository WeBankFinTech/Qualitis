import { ref } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import eventbus from '@/common/useEvents';
import { adaptDataToTreeSelect } from '@/common/utils';
import { MAX_PAGE_SIZE } from '@/assets/js/const';
import {
    fetchProjectDetail, executeProject, executeRuleInProject, executeTaskInTaskQuery as _executeTaskInTaskQuery,
} from '../api';

export default function useExecutation(showLoading) {
    const { t: $t } = useI18n();
    const showExecutation = ref(false);
    // 处理
    const executationConfig = ref({
        rules: [],
        actor: '',
        engineConfig: {
            cluster_name: '',
            params: [],
        },
        variables: [],
        // fpsConfig: { id: '', hash: '' },
        // argsConfig: { dynamic: false, args: [], partition: '' },
        advancedConfig: {
            engineReuse: true,
            granularity: 2,
            dynamicPartitionBool: false,
        },
    });
    const clearConfig = () => {
        executationConfig.value = {
            rules: [],
            actor: '',
            engineConfig: {
                cluster_name: '',
                params: [],
            },
            variables: [],
            // fpsConfig: { id: '', hash: '' },
            // argsConfig: { dynamic: false, args: [], partition: '' },
            advancedConfig: {
                engineReuse: true,
                granularity: 2,
                dynamicPartitionBool: false,
            },
        };
    };
    const executationRules = ref({
        show: true, // 是否展示规则下拉框，默认展示，执行项目时，不展示规则下拉框
        flag: '', // 规则，代表 规则，并且是tree选择框，任务，代表 任务，并且是普通Select
        label: '', // 控件label
        disabled: false, // 控件是否有效
        list: [],
    });
    let payload = null;

    const params2String = (params, labelFieldName = 'name', valueFieldName = 'value', operator = '=', spliter = ';') => params.filter((item) => {
        if (item.param_name || item.var_name) {
            return true;
        }
        return false;
    }).map(item => `${item[labelFieldName]}${operator}${item[valueFieldName]}`).join(spliter);
    const executeTaskPre = async (data, show = true) => {
        // 多次使用的时候需要先清空
        executationConfig.value.rules = [];
        payload = data;
        showExecutation.value = true;
        executationRules.value = {
            show, flag: '规则', label: $t('projects.rule'), list: adaptDataToTreeSelect(data.rule_details), disabled: data.disabled,
        };
    };
    // const getVarVal = key => executationConfig.value.variables.find(item => item.var_name === key)?.var_value || '';
    const buildCommonParams = () => {
        // 处理
        const {
            actor,
            engineConfig,
            variables,
            advancedConfig,
        } = executationConfig.value;
        const startupParamName = params2String(engineConfig.params, 'param_name', 'param_value');
        // const sparkConfigName = params2String(sparkConfig, 'label');
        // const executationParamName = params2String(argsConfig.args.concat({ name: 'engine_reuse', value: engineConfig.engine_reuse }), 'name', 'value', ':');
        // eslint-disable-next-line camelcase
        // const fps_file_id = getVarVal('fps_id');
        // eslint-disable-next-line camelcase
        // const fps_hash = getVarVal('fps_hash');
        // const filterVariables = variables.filter(item => !['fps_id', 'fps_hash'].includes(item.var_name));
        // eslint-disable-next-line camelcase
        const execution_param = params2String(variables, 'var_name', 'var_value', ':');
        const granularityMap = ['db', 'table', 'merge'];
        // eslint-disable-next-line camelcase
        const split_by = granularityMap[advancedConfig.granularity];
        return {
            create_user: sessionStorage.getItem('firstUserName'),
            cluster_name: engineConfig.cluster_name,
            startup_param_name: startupParamName,
            dynamic_partition_bool: advancedConfig.dynamicPartitionBool,
            dynamic_partition_prefix: advancedConfig.dynamicPartitionPrefix,
            execution_param,
            execution_user: actor,
            // fps_file_id,
            // fps_hash,
            split_by,
            engine_reuse: advancedConfig.engineReuse,
        };
    };
    const executeTask = async () => {
        showLoading.value = true;
        try {
            const params = {
                ...buildCommonParams(),
                project_id: payload.project_id,
            };
            await executeProject(params);
            clearConfig();
            FMessage.success($t('toastSuccess.submitSuccess'));
            showExecutation.value = false;
            showLoading.value = false;
        } catch (err) {
            showLoading.value = false;
            console.warn(err);
            FMessage.warn(err?.data?.message || $t('toastError.submitFail'));
        }
    };

    const executeTaskPreInTaskQuery = (selectedList, list = []) => {
        const newList = list.map(item => ({ ...item, label: item.task_group_name, value: item.group_id }));
        console.log(list);
        payload = [];
        for (let i = 0; i < list.length; i++) {
            const item = list[i];
            if (selectedList.indexOf(item.application_id) > -1) {
                payload.push(item);
            }
        }
        executationConfig.value.rules = [];
        selectedList.forEach(item => executationConfig.value.rules.push(item.group_id));
        showExecutation.value = true;
        executationRules.value = { flag: '任务', label: $t('label.task'), list: newList };
    };
    const executeTaskInTaskQuery = async () => {
        const ruleGroupList = payload.map(item => ({
            group_id: item.group_id,
            cluster_name: item.cluster_name,
            execution_user: item.execute_user,
            execution_param: item.execution_param,
            create_user: item.create_user,
            partition: item.partition,
            fps_file_id: item.fps_file_id,
            fps_hash: item.fps_hash,
            startup_param_name: item.startup_param,
            set_flag: item.set_flag,
            run_date: item.run_date,
        }));
        try {
            const params = {
                ...buildCommonParams(),
                rule_group_list: ruleGroupList,
            };
            await _executeTaskInTaskQuery(params);
            clearConfig();
            FMessage.success($t('toastSuccess.submitSuccess'));
            showExecutation.value = false;
            eventbus.emit('closeTaskLoading');
        } catch (err) {
            console.warn(err);
            FMessage.warn(err?.data?.message || $t('toastError.excuteFail'));
            eventbus.emit('closeTaskLoading');
        }
    };

    const executeTaskPreInRuleQuery = (list = [], selectedList) => {
        const newList = list.map(item => ({ ...item, label: item.rule_name, value: item.rule_id }));
        executationConfig.value.rules = [];
        if (Array.isArray(selectedList)) {
            selectedList.forEach(item => executationConfig.value.rules.push(item.rule_id));
        }
        showExecutation.value = true;
        executationRules.value = { flag: '任务', label: $t('projects.rule'), list: newList };
    };

    // 项目详情页的执行任务
    const excuteRuleInProjectDetail = async () => {
        console.log('excuteRuleInProjectDetail');
        try {
            let {
                rules,
            } = executationConfig.value;
            console.log(rules);
            if (executationRules.value.flag === '规则') {
                rules = rules.map((rule) => {
                    if (rule.startsWith && rule.startsWith('g')) {
                        return executationRules.value.list.find(v => v.value === rule).children.map(({ value }) => value);
                    }
                    return [rule];
                }).flat();
            }
            const params = {
                ...buildCommonParams(),
                rule_list: rules,
            };
            await executeRuleInProject(params);
            clearConfig();
            FMessage.success($t('toastSuccess.submitSuccess'));
            showExecutation.value = false;
            eventbus.emit('closeTaskLoading');
        } catch (err) {
            console.warn(err);
            FMessage.warn(err?.data?.message || $t('toastError.excuteFail'));
            eventbus.emit('closeTaskLoading');
        }
    };

    return {
        showExecutation,
        executationConfig,
        executationRules,
        executeTaskPre,
        executeTask,
        excuteRuleInProjectDetail,
        executeTaskPreInTaskQuery,
        executeTaskPreInRuleQuery,
        executeTaskInTaskQuery,
    };
}
