/* eslint-disable camelcase */

import dayjs from 'dayjs';

// 放工具函数
export function dateFormat(fmt, date = new Date()) {
    const o = {
        'M+': date.getMonth() + 1, // 月份
        'd+': date.getDate(), // 日
        'H+': date.getHours(), // 小时
        'm+': date.getMinutes(), // 分
        's+': date.getSeconds(), // 秒
        'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
        S: date.getMilliseconds(), // 毫秒
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (`${date.getFullYear()}`).substr(4 - RegExp.$1.length));
    }
    // eslint-disable-next-line no-restricted-syntax
    for (const k in o) {
        if (new RegExp(`(${k})`).test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : ((`00${o[k]}`).substr((`${o[k]}`)
                .length)));
        }
    }
    return fmt;
}

// 通知dss
export function DWSMessage(key, ruleGroupId, action) {
    try {
        const jsonStr = {
            type: 'qualitis',
            nodeId: key,
            data: {
                ruleGroupId,
                action: action === 'modify' ? 'edit' : action,
            },
        };
        console.log('DWSMessage: ', jsonStr);
        window.parent.postMessage(JSON.stringify(jsonStr), '*');
    } catch (err) {
        console.warn(err);
    }
}

// 拼接URL参数
export function getURLQueryParams({ query, params }) {
    try {
        // 拼接现有和传入的参数
        const target = Object.assign({}, query, params);
        const targetKeys = Object.keys(target);
        const result = [];
        for (let i = 0; i < targetKeys.length; i++) {
            const key = targetKeys[i];
            result.push(`${key}=${target[key]}`);
        }
        return result.join('&');
    } catch (err) {
        console.warn(err);
        return '';
    }
}


const HIVE_NUMBER_TYPES = [
    'int',
    'double',
    'tinyint',
    'smallint',
    'bigint',
    'float',
    'decimal',
];

const MYSQL_NUMBER_TYPES = [
    'TINYINT',
    'SMALLINT',
    'MEDIUMINT',
    'INT',
    'INTEGER',
    'BIGINT',
    'FLOAT',
    'DOUBLE',
    'DECIMAL',
];

export const NUMBER_TYPES = [
    ...HIVE_NUMBER_TYPES,
    ...MYSQL_NUMBER_TYPES,
];

export const ruleTypes = [{
    type: '1-1',
    value: 'newSingleTableRule',
    label: '单表校验',
}, {
    type: '3-1',
    value: 'newMultiTableRule',
    // label: '跨表校验',
    label: '多表比对',
}, {
    type: '4-1',
    value: 'documentVerification',
    // label: '文件校验',
    label: '表文件校验',
}, {
    type: '3-2',
    value: 'crossDatabaseFullVerification',
    // label: '跨库校验',
    label: '库一致性比对',
    // }, {
    //     type: '2-1',
    //     value: 'newCustomRule',
    //     label: '单指标校验',
}, {
    type: '2-2',
    value: 'sqlVerification',
    // label: '多指标校验',
    label: '自定义SQL校验',
}, {
    type: '0-0',
    value: 'executionParams',
    label: '执行参数',
},
];
function deepCopy(p, c) {
    const newObj = c || {};
    // eslint-disable-next-line no-restricted-syntax
    for (const i in p) {
        if (typeof p[i] === 'object') {
            newObj[i] = (p[i].constructor === Array) ? [] : {};
            deepCopy(p[i], newObj[i]);
        } else {
            newObj[i] = '';
        }
    }
    return newObj;
}

const specialTplList = [4, 5, 6, 7, 8, 9, 10, 11, 12];

export function getExceptionObj(rule, type) {
    let exception = {};
    exception = deepCopy(rule.exception);
    if (type === 'newSingleTableRule') {
        if (Array.isArray(rule.validation.qualities)) {
            exception.qualities = rule.validation.qualities.map((ele) => {
                const qua = {
                    output_meta_id: '',
                    check_template: '',
                    threshold: '',
                };
                if (specialTplList.includes(ele.check_template)) {
                    qua.compareValue = '';
                }
                return qua;
            });
        }
        if (rule.executionParams.specify_static_startup_param) {
            exception.static_startup_param = '';
        }
        if (rule.executionParams.alert) {
            exception.alert_receiver = '';
        }
    }
    return exception;
}

export function canSave(exception) {
    const ruleKeys = Object.keys(exception);
    for (let index = 0; index < ruleKeys.length; index++) {
        const element = ruleKeys[index];
        if (typeof exception[element] === 'object') return canSave(exception[element]);
        if (exception[element] === '') continue;
        else return console.log('Ooh,', exception[element], 'is not ready...');
    }
    return true;
}
export function handleExecuteParams(data = {}) {
    const {
        rule_enable = true,
        specify_filter = false,
        filter = '',
        delete_fail_check_result = false,
        upload_abnormal_value = false,
        upload_rule_metric_value = false,
        abnormal_data_storage = false,
        cluster = '',
        abnormal_proxy_user = '',
        abnormal_database = '',
        specify_static_startup_param = false,
        static_startup_param = '',
        abort_on_failure = false,
        alert = false,
        alarm_level = '',
        alarm_receiver = '',
        execution_parameters_name = '',
        execution_parameters_id = '',
        union_all = false,
    } = data;
    return {
        rule_enable,
        specify_filter,
        filter,
        delete_fail_check_result,
        upload_abnormal_value,
        upload_rule_metric_value,
        abnormal_data_storage,
        cluster,
        abnormal_proxy_user,
        abnormal_database,
        specify_static_startup_param,
        static_startup_param,
        abort_on_failure,
        alert,
        alarm_level,
        alarm_receiver,
        execution_parameters_name,
        execution_parameters_id,
        union_all,
    };
}

export function getPostData({
    projectId, rule, refValidation, refDataSource,
}) {
    console.log('projectId, rule, refValidation, refDataSource', projectId, rule, refValidation, refDataSource);
    let data = {};
    if (rule.groupType === 'newSingleTableRule') {
        const rule_metric = refValidation.ruleStandardList.find(item => item.id === rule.validation.ruleStandard);
        let rule_metric_en_code = '';
        if (rule_metric) {
            rule_metric_en_code = rule_metric.en_code;
        }
        const executeParamsObj = handleExecuteParams(rule.executionParams);
        const {
            ruleName,
            ruleDesc,
            checkTemplate,
        } = rule.baseInfo;
        data = {
            ...data,
            ...executeParamsObj,
            project_id: projectId,
            rule_name: ruleName,
            rule_detail: ruleDesc,
            rule_metric_en_code,
            // delete_fail_check_result: rule.validation.noFailFlag,
            alarm: true,
            rule_template_id: checkTemplate,
        };
        data.alarm_variable = rule.validation.qualities.map(({
            output_meta_id,
            // upload_abnormal_value,
            // upload_rule_metric_value,
            check_template,
            threshold,
            compareValue,
        }) => {
            const alarmRule = {
                rule_metric_en_code,
                rule_metric_id: rule.validation.ruleStandard,
                output_meta_id,
                // upload_abnormal_value,
                // upload_rule_metric_value,
                check_template,
                threshold,
            };
            if (refValidation.specialTplList.includes(check_template)) {
                alarmRule.compare_type = compareValue;
            }
            return alarmRule;
        });
        // 获取用户填写的数据源数据
        const singleData = refDataSource.refNewSingleTableRule;
        const dataItem = rule.dataSource;
        const selectedCluster = singleData.clusterList.find(cluster => cluster.cluster_name === dataItem.cluster);
        const db = singleData.databaseList.find(e => e.db_name === dataItem.database);
        const table = singleData.tableList.find(t => t.table_name === dataItem.table);
        const selectColumn = [];
        if (Array.isArray(dataItem.column)) {
            dataItem.column.forEach((name) => {
                const col = singleData.columnList.find(e => e.column_name === name);
                if (col.column_name || col.data_type) {
                    selectColumn.push({ column_name: col.column_name, data_type: col.data_type });
                }
            });
        }
        const db_name = db ? db.db_name : '';
        const dataSource = singleData.connectionList.find(item => String(item.id) === String(dataItem.connection)) || {};
        const dataSourceType = dataSource.dataSourceType || {};
        const isFPS = dataItem.sourceType === 'FPS';
        const datasource = {
            db_name,
            table_name: (table ? table.table_name : isFPS ? dataItem.table : ''),
            col_names: selectColumn,
            filter: dataItem.condition,
            cluster_name: selectedCluster ? selectedCluster.cluster_name : '',
            proxy_user: dataItem.proxyUser,
            linkis_datasource_id: dataItem.connection,
            linkis_datasource_name: dataSource.dataSourceName,
            linkis_datasource_type: dataSourceType.name,
        };
        if (isFPS) {
            const { tableStructure } = dataItem;
            data.datasource = [{
                ...datasource,
                file_id: tableStructure.fileID, // 文件id
                file_hash_values: tableStructure.hash, // 文件哈希值
                file_delimiter: tableStructure.separator, // 分割符号
                file_header: tableStructure.firstLineHeader, // 首行为表头
                file_type: tableStructure.fileType,
                file_table_desc: tableStructure.charList.reduce((t, c, i) => {
                    if (i === 0) return `${c.column_name}:${c.data_type}`;
                    return `${t},${c.column_name}:${c.data_type}`;
                }, ''), // 表结构
            }];
        } else {
            data.datasource = [datasource];
        }
        data.template_arguments = rule.ruleArgumentList.map(({
            argument_step,
            argument_id,
            argument_value,
            argsSelectList,
            flag,
        }) => {
            if (flag) {
                const item = argsSelectList.find(k => k.value === argument_value);
                argument_value = item ? item.key_name : argument_value;
            }
            return {
                argument_step,
                argument_id,
                argument_value,
            };
        });
    }
    return data;
}

// 将数据适配给treeselect组件
export function adaptDataToTreeSelect(list = []) {
    const ruleTree = {};
    list.forEach((rule) => {
        if (!ruleTree[rule.rule_group_id]) {
            ruleTree[rule.rule_group_id] = {
                children: [],
                label: rule.rule_group_name,
                value: `g${rule.rule_group_id}`,
            };
        }
        ruleTree[rule.rule_group_id].children.push({
            label: rule.rule_name,
            value: rule.rule_id,
            disabled: !rule.rule_enable,
        });
    });
    const ruleGroupNames = Object.keys(ruleTree).map(k => ruleTree[k]);
    return ruleGroupNames;
}

// 打平从treeselect组件拿出来的数据
// data-选中的数据，originList-完整数据
export function getFlatDataFormTreeSelect(data = [], originList = []) {
    try {
        const result = data.map((rule) => {
            if (rule.startsWith && rule.startsWith('g')) {
                return originList.find(v => v.value === rule).children.map(({ value }) => value);
            }
            console.log(rule);
            return [rule];
        }).flat();
        return result;
    } catch (err) {
        console.warn(err);
    }
}
// 可选范围显示
export const getMoreActionRangeString = arr => arr.map(item => item.join('/')).join('、');

// select组件获取label，根据value值获取对应的label
export const getLabelByValue = (options, value, { labelFieldName = 'label', valueFieldName = 'value' } = {}) => {
    if (!Array.isArray(options)) return '';
    const target = options.find(item => item[valueFieldName] === value);
    return target ? target[labelFieldName] || '' : '';
};

export const isEqual = (cur, target) => cur === target;

export const DOCUMENT_CHECK_RULE_TYPE = 4;
export const SINGLE_CHECK_RULE_TYPE = 1;
export const ruleTypeList = [
    {
        value: DOCUMENT_CHECK_RULE_TYPE,
        label: '文件校验',
    },
    {
        value: SINGLE_CHECK_RULE_TYPE,
        label: '单表校验',
    },
];

// 可见范围详情显示
export const actionRangeDetail = arr => (Array.isArray(arr) ? arr.join('、') : '--');
// 运维科室、开发科室详情显示
export const departmentDetail = arr => (Array.isArray(arr) ? arr.join('/') : arr) || '--';

// 可见范围列表显示
export const getvisibilityDepartment = val => val?.map(item => item.name).join('、') || '--';

// 表格格式化函数，将空白格式化为'--'
export const formatterEmptyValue = ({ cellValue }) => {
    if (cellValue === 0) return 0;
    return cellValue || '--';
};

// echart 生成tooltip函数
export const genTooltipStr = (tempData) => {
    const showList = tempData.map(item => ({ value: item.value, name: item.seriesName }));
    const dateStr = showList.length ? `时间：${tempData[0].name} <br />` : '';
    const total = showList.reduce((pre, cur) => pre + cur.value, 0);
    const totalStr = showList.length ? `总计：${total} <br />` : '';
    const showListStr = showList.reduce((pre, cur) => `${pre}${cur.name}: ${cur.value}<br />`, '');
    return dateStr + totalStr + showListStr;
};

// 获取url参数
export function getUrlParams(childLocationHref = '') {
    let search;
    if (childLocationHref) {
        search = childLocationHref;
    } else {
        search = window.location.search || window.location.href;
    }
    const index = search.indexOf('?');
    if (index === -1) {
        return {};
    }
    search = search.substring(index + 1);
    const searchArr = search.split('&');
    const param = {};
    searchArr.forEach((item) => {
        const paramKey = item.substring(0, item.indexOf('='));
        const paramVal = item.substring(item.indexOf('=') + 1);
        param[paramKey] = paramVal;
    });
    return param;
}


// 获取baseurl
export function getBaseUrlParam() {
    let param = getUrlParams();
    // 如果嵌套在microapp里面，通过数据通信拿到baseurl
    if (window.__MICRO_APP_ENVIRONMENT__) {
        const baseUrlData = window.microApp.getData();
        if (baseUrlData) param = baseUrlData;
    }
    let baseUrl = '';
    if (param.baseUrl) {
        baseUrl = decodeURIComponent(param.baseUrl);
        sessionStorage.setItem('dqmBaseUrl', baseUrl);
    } else {
        baseUrl = sessionStorage.getItem('dqmBaseUrl') ? sessionStorage.getItem('dqmBaseUrl') : '';
    }
    return baseUrl;
}

// 将一个可能含有children的对象转化为没有children的数组
const treeToArray = (data) => {
    if (data.children && data.children.length > 0) {
        return data.children.map(item => treeToArray(item));
    }
    return data;
};

/**
 * 当树形选择器使用 parent模式作为回显模式时，有时需要传递给后台的却是最下层叶节点的数据，改方法返回实际选中的叶节点数据对象
 * @param {*} data 树形选择器组件model的value list
 * @param {*} originList 树形选择器使用的tree data打平后的数组，注意必须包含所有节点，包括根节点 [{value,label,childer}]
 * @returns
 */
export function getDataFormTreeSelect(data = [], originList = []) {
    // console.log(data, originList)
    return data.map((item) => {
        const value = originList.find(v => v.value === item);
        return treeToArray(value);
    }).flat(Infinity);
}

/**
 * 因为dayjs在接受undefined值等非真值时会直接使用当前时间，所以包一层
 * @param {*} time 需要处理的时间
 * @param {*} format 处理的格式，参考dayjs说明
 * @returns 处理后的时间字符串
 */
export const formatDatetime = (time, format = 'YYYY-MM-DD HH:mm:ss') => {
    if (time) {
        return dayjs(time).format(format);
    }
    return '';
};
