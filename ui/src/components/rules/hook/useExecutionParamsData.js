import { ref } from 'vue';
import { FMessage } from '@fesjs/fes-design';
import { useI18n } from '@fesjs/fes';
import {
    fetchAlarmEvent, fetchDateSelectionMethod, fetchEliminateStrategy, fetchEngineParamsType,
    fetchEngineParamsNameAndInit, fetchExecutionVariableType, fetchExecutionVariableName,
} from '@/pages/projects/api.js';
import { fetchTemplates } from '@/pages/rules/template/api';
import { TEMPLATE_TYPES } from '@/common/constant.js';

export default function useExecutionParamsData() {
    const alarmEventList = ref([]);
    const dateSelectionMethodList = ref([]);
    const eliminateStrategyList = ref([]);
    const verifyTemplateList = ref([]);
    const engineParamsTypeList = ref([]);
    const engineParamsNameList = ref([]);
    const executionVariableTypeList = ref([]);
    const executionVariableNameList = ref([]);

    const loadEngineParamsType = async () => {
        try {
            const res = await fetchEngineParamsType();
            engineParamsTypeList.value = res;
        } catch (error) {
            console.log('获取动态引擎参数类型 - loadEngineParamsType', error);
        }
    };

    const loadEngineParamsNameAndInit = async (id) => {
        try {
            const res = await fetchEngineParamsNameAndInit(id);
            engineParamsNameList.value = res.filter((item) => {
                if (item.label !== '') {
                    return item;
                }
                return false;
            });
            return engineParamsNameList.value;
        } catch (error) {
            console.log('获取动态引擎参数名称与初始值 - loadEngineParamsNameAndInit', error);
        }
    };

    const loadExecutionVariableType = async () => {
        try {
            const res = await fetchExecutionVariableType();
            executionVariableTypeList.value = res;
        } catch (error) {
            console.log('获取执行变量类型 - loadExecutionVariableType', error);
        }
    };

    const loadExecutionVariableName = async () => {
        try {
            const res = await fetchExecutionVariableName();
            executionVariableNameList.value = res.map(item => ({ value: item, label: item }));
        } catch (error) {
            console.log('获取执行变量名称 - loadExecutionVariableName', error);
        }
    };

    const loadAlarmEvent = async () => {
        try {
            const res = await fetchAlarmEvent();
            alarmEventList.value = res;
        } catch (error) {
            console.log('获取告警事件 - loadAlarmEvent', error);
        }
    };

    const loadDateSelectionMethod = async () => {
        try {
            const res = await fetchDateSelectionMethod();
            dateSelectionMethodList.value = res;
        } catch (error) {
            console.log('获取日期选择方式 - loadDateSelectionMethod', error);
        }
    };

    const loadEliminateStrategy = async () => {
        try {
            const res = await fetchEliminateStrategy();
            eliminateStrategyList.value = res;
        } catch (error) {
            console.log('获取去噪策略 - loadEliminateStrategy', error);
        }
    };

    const loadVerifyTemplateList = async () => {
        try {
            const tempResp = await fetchTemplates({
                page: 0,
                size: 512,
            });
            verifyTemplateList.value = tempResp.data || [];
        } catch (error) {
            console.log('获取校验模板 - loadVerifyTemplateList', error);
        }
    };

    const computeInlineFormStyle = (mode) => {
        if (mode === 'edit') {
            return true;
        }
        return false;
    };
    return {
        alarmEventList,
        dateSelectionMethodList,
        eliminateStrategyList,
        verifyTemplateList,
        engineParamsTypeList,
        engineParamsNameList,
        executionVariableTypeList,
        executionVariableNameList,
        loadAlarmEvent,
        loadDateSelectionMethod,
        loadEliminateStrategy,
        loadVerifyTemplateList,
        computeInlineFormStyle,
        loadEngineParamsType,
        loadEngineParamsNameAndInit,
        loadExecutionVariableType,
        loadExecutionVariableName,
    };
}
