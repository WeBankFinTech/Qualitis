import {
    onMounted, onUnmounted, computed,
} from 'vue';
import { request, useRoute, useRouter } from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import { dateFormat } from '@/common/utils';
import { cloneDeep } from 'lodash-es';
import { TEMPLATE_ARGUMENT_INPUT_TYPE } from '@/common/constant';
import { MAX_PAGE_SIZE } from '@/assets/js/const';

export async function getTableList({
    cluster, user, id, db,
}) {
    let resp;
    if (id) {
        resp = await request('/api/v1/projector/meta_data/data_source/tables', {
            clusterName: cluster,
            proxyUser: user,
            dataSourceId: id,
            dbName: db,
        }, {
            method: 'get',
            cache: true,
        });
    } else {
        resp = await request('/api/v1/projector/meta_data/table', {
            cluster_name: cluster,
            proxy_user: user,
            db_name: db,
            start_index: 0,
            page_size: 50000,
        }, {
            cache: true,
        });
    }
    if (!Array.isArray(resp.data)) {
        return [];
    }
    // eslint-disable-next-line camelcase
    return resp.data.map(({ table_name }) => table_name);
}
export async function getDBlist({ id, user, cluster }) {
    let resp = {};
    if (id) {
        resp = await request('/api/v1/projector/meta_data/data_source/dbs', {
            dataSourceId: id,
            proxyUser: user,
            clusterName: cluster,
        }, {
            method: 'get',
            cache: true,
        });
    } else {
        resp = await request('/api/v1/projector/meta_data/db', {
            cluster_name: cluster,
            proxy_user: user,
            start_index: 0,
            page_size: 5000,
        }, {
            cache: true,
        });
    }
    if (!Array.isArray(resp.data)) return [];
    // eslint-disable-next-line camelcase
    return resp.data.map(({ db_name }) => db_name);
}

export const saveEvent = Symbol('ruleFormSave');
export const forceFormPageUpdateEvent = Symbol('updateFormPage');
export const delEvent = Symbol('ruleDelete');
export const cancelEvent = Symbol('ruleFormCancel');
export const groupChangeEvent = Symbol('RuleGroupChangeEvent');
export const checkTemplateChangeEvent = Symbol('ruleTemplateChangeEvent');
export const ruleTypeChangeEvent = Symbol('ruleTypeChangeEvent');

export function useListener(t, cb) {
    onMounted(() => {
        eventbus.on(t, cb);
    });
    onUnmounted(() => {
        eventbus.off(t, cb);
    });
}

export const dataSourceTypeList = [
    { value: 'hive', label: 'HIVE' },
    { value: 'mysql', label: 'MYSQL' },
    { value: 'tdsql', label: 'TDSQL' },
    { value: 'fps', label: 'FPS' },
];
export function getDataSourceType(isFps, type, lower) {
    let label = '';
    if (type) {
        label = dataSourceTypeList.find(v => v.value === type)?.label || '';
    } else if (isFps) {
        label = 'FPS';
    } else {
        label = 'HIVE';
    }
    if (lower) {
        return label.toLowerCase();
    }
    return label;
}

export const timeFormatList = ['yyyyMMdd', 'yyyy-MM-dd', 'yyyy.MM.dd', 'yyyy/MM/dd'];
export const buildColumnData = (columnsDescString = '', spliter = ',') => {
    if (!columnsDescString) return [];
    return columnsDescString.split(spliter).map((item) => {
        const data = item.split(':');
        const typeValue = data[1];
        let charType = typeValue;
        let type = typeValue;
        const dataType = typeValue;
        if (typeValue.startsWith('date_')) {
            const [datePrefix, dateType] = typeValue.split('_');
            charType = timeFormatList[Number.parseInt(dateType)];
            type = datePrefix;
        }
        return {
            column_name: data[0],
            charType,
            type,
            data_type: dataType,
        };
    });
};

export const columnData2Str = (columns) => {
    if (!Array.isArray(columns)) return '';
    return columns.reduce((t, c, i) => {
        if (i === 0) {
            return `${c.column_name}:${c.data_type}`;
        }
        return `${t},${c.column_name}:${c.data_type}`;
    }, '');
};

export const getConfiguredRuleMetric = (dataSource) => {
    if (!dataSource || !dataSource.alarm_variable) {
        return;
    }
    const firstAlarmVariable = dataSource.alarm_variable[0];
    return {
        value: firstAlarmVariable ? firstAlarmVariable.rule_metric_id || -1 : -1,
        label: firstAlarmVariable ? firstAlarmVariable.rule_metric_name || '' : '',
        en_code: firstAlarmVariable ? firstAlarmVariable.rule_metric_en_code || '' : '',
    };
};

// 支持文件校验
export const formatAlarmVariable = (datas = [], isFileType = false) => {
    try {
        const result = datas.map((item) => {
            const alarm = isFileType ? {
                ruleStandard: item.rule_metric_id,
                ruleMetricEnCode: item.rule_metric_en_code,
                ruleStandardName: item.output_meta_name,
                output_meta_id: item.file_output_name,
                file_output_unit: null,
                check_template: item.check_template,
                compare_type: '',
                threshold: item.threshold,
                delete_fail_check_result: item.delete_fail_check_result,
                upload_abnormal_value: item.upload_abnormal_value,
                upload_rule_metric_value: item.upload_rule_metric_value,
            } : {
                ruleStandard: item.rule_metric_id,
                ruleMetricEnCode: item.rule_metric_en_code,
                ruleStandardName: item.output_meta_name,
                output_meta_id: item.output_meta_id,
                check_template: item.check_template,
                compare_type: '',
                threshold: item.threshold,
            };
            // 带有比较方式的校验模板
            const specialCheckTemplateValueList = [
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                12,
            ];
            if (specialCheckTemplateValueList.includes(item.check_template)) {
                alarm.compare_type = item.compare_type;
                // 校验字段 不为 文件数
                if (item.file_output_name !== 1 && isFileType) {
                    alarm.file_output_unit = item.file_output_unit;
                }
            }
            return alarm;
        });
        return result;
    } catch (err) {
        console.warn(err);
        return [];
    }
};

export const getEnumNameString = (standardValueList = [], id) => {
    const target = standardValueList.find(item => item.value === id);
    return target?.label || '';
};

export const getSqlPreviewString = ({
    sqlTpl = '', clusterRule = {}, selectedTemplateId = '', standardValue = false, standardValueList = [],
}) => {
    if (!sqlTpl) {
        return '';
    }
    let sqlTplResult = sqlTpl;
    console.log('clusterRule: ', clusterRule, sqlTpl, selectedTemplateId);
    Object.keys(clusterRule).forEach((key) => {
        const placeholderKey = `${key}`;
        let value = clusterRule[key];
        console.log('clusterRuleKey: ', key, value);
        // 对应字段有值则进行替换
        if (value && value.length) {
            // 过滤字段有日期类型模版表达式则转换时间
            const dateReg = /\$\{(yyyy(?:-|\/|\s)?MM(?:-|\/|\s)?dd(?:(?:-|\/|\s)?HH)?(?:(?:-|\/|\s|:)?mm)?(?:(?:-|\/|\s|:)?ss)?)\}(?:-(\d+))?/;
            const dateMatchResult = dateReg.test(value) && value.match(dateReg);
            if (dateMatchResult) {
                let dateObj = new Date();
                if (dateMatchResult[2]) {
                    // ${yyyyMMdd}-n  n:减去多少天
                    dateObj = new Date(new Date() - 24 * 60 * 60 * 1000 * dateMatchResult[2]);
                }
                const formated = dateFormat(dateMatchResult[1], dateObj);
                value = value.replace(/^dateMatchResult[0]$/g, formated);
            }
            // 可能有多个结果
            // 这里因为key都是$开始的，正则表达式中必须用\\做转义才能匹配
            const regExp = new RegExp(`\\${placeholderKey}`, 'g');
            sqlTplResult = sqlTplResult.replace(regExp, (m) => {
                // 枚举值预览的时候，一个name对应多个versionid&&key=${list}&&选择的是标准值，需要专门处理
                // eslint-disable-next-line no-template-curly-in-string
                const valueStr = (placeholderKey === '${standard_value}' && standardValue) ? getEnumNameString(standardValueList, value) : value;
                return `<font color=#d20909>${valueStr}</font>`;
            });
        }
    });
    return sqlTplResult;
};

export const placeholderPrompt = ({ config }) => {
    const result = {
        textShow: true,
        dbText: config.placeholders.find(item => item.input_type === TEMPLATE_ARGUMENT_INPUT_TYPE.DB)?.placeholder_description || '',
        tableText: config.placeholders.find(item => item.input_type === TEMPLATE_ARGUMENT_INPUT_TYPE.DB_TABLE)?.placeholder_description || '',
    };

    const regText = [];
    config.placeholders.forEach((item) => {
        if (item.input_type !== TEMPLATE_ARGUMENT_INPUT_TYPE.DB && item.input_type !== TEMPLATE_ARGUMENT_INPUT_TYPE.DB_TABLE) {
            regText.push({
                input_type: item.input_type,
                description: item.placeholder_description,
            });
        }
    });

    result.regText = regText;
    return result;
};
/**
 *  placeholders: Array<{}>
 *  type: input_type数组
 *  key: false 返回符合条件列表项, string 返回对应值
 *  condition: 条件
 */
export const getReplacePlaceholder = ({
    placeholders, type = [], key = 'placeholder', condition = {},
}) => {
    if (Array.isArray(placeholders)) {
        const placeholderItem = placeholders.find((p) => {
            const match = {
                matchKeyValue: !condition.key,
                macthType: type.length < 1,
            };
            if (condition.key) {
                match.matchKeyValue = p[condition.key] === condition.value;
            }
            if (type.length) {
                match.macthType = type.indexOf(p.input_type) > -1;
            }
            return match.macthType && match.matchKeyValue;
        }) || {};
        return key === false ? placeholderItem : (placeholderItem[key] || '');
    }
};

export const parseSqlTpl = ({ placeholders, isEnum, sqlDataSource }) => {
    // 切换规则，sql模板会变化
    // 先遍历看placeholder有没有枚举值，如果有且只有一个则直接替换预览
    const sourceItemRules = {};
    const additionalMap = {};
    placeholders.forEach((p) => {
        const placeholderEnumValue = getReplacePlaceholder({
            placeholders,
            key: 'enum_value',
            condition: {
                key: 'placeholder',
                value: p.placeholder,
            },
        });
        if (Array.isArray(placeholderEnumValue) && placeholderEnumValue.length === 1) {
            additionalMap[p.placeholder] = placeholderEnumValue[0] && placeholderEnumValue[0].value;
        } else {
            additionalMap[p.placeholder] = '';
        }
    });

    Object.keys(sourceItemRules).forEach((key) => {
        const isEnumMap = Object.assign({}, isEnum);
        // 如果数据项已有枚举值，切换规则时优先使用枚举值，如果有上个规则的值且其不是枚举值则保留
        if (sourceItemRules[key]) {
            isEnumMap[key] = 1;
        } else {
            isEnumMap[key] = 0;
        }
        const lastData = isEnumMap[key] === 1 ? '' : sqlDataSource[key];
        additionalMap[key] = sourceItemRules[key] || lastData;
    });

    return additionalMap;
};

// 如果规则有模版参数，则组织列表数据
export const initRuleArgsList = ({ list = [], placeholders }) => {
    if (Array.isArray(list)) {
        list.forEach((rule) => {
            const placeholder = getReplacePlaceholder({
                placeholders,
                key: false,
                condition: {
                    key: 'placeholder_id',
                    value: rule.argument_id,
                },
            });
            rule.argsSelectList = placeholder ? placeholder.enum_value : [];
            rule.whether_new_value = placeholder ? placeholder.whether_new_value : false;
            rule.flag = !!((rule.argsSelectList && rule.argsSelectList.length > 0));
        });
    }
    return list;
};

export const getColumnMapData = function (arr = []) {
    try {
        const result = (arr || []).map(item => item.column_name);
        return result;
    } catch (err) {
        console.warn(err);
    }
};

export const replaceRouter = function (resp, isWorkflowProject, route, router) {
    const query = computed(() => route.query);
    const curQuery = cloneDeep(query.value);
    const params = {
        ruleGroupId: resp.rule_group_id,
        id: resp.rule_id,
        workflowProject: isWorkflowProject,
        projectId: resp.project_id,
    };
    const mixinQuery = Object.assign(curQuery, params);
    router.replace({
        query: mixinQuery,
    });
};

// 获取校验指标list
export const getRuleMetricAll = async () => {
    console.log('getRuleMetricAll');
    try {
        const result = await request('api/v1/projector/rule_metric/all', {
            page: 0,
            size: MAX_PAGE_SIZE,
            t: new Date(),
        }, {
            method: 'post',
        });
        return result?.data;
    } catch (e) {
        console.log('getRuleMetricAll error', e);
        return [];
    }
};
