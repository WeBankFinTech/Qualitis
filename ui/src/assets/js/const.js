export const MAX_PAGE_SIZE = 2147483647;

// 除了项目英文名称长度是64，其他都是128
export const COMMON_REG = {
    EN_NAME_64: /^[a-zA-Z0-9_]{0,64}$/ig,
    EN_NAME: /^[a-zA-Z0-9_]{0,128}$/ig,
    ONLY_EN_NAME: /^[a-zA-Z0-9]{0,128}$/ig,
    CN_NAME: /^[\u4e00-\u9fa5a-zA-Z0-9_]{0,128}$/ig,
    CN_NAME2: /^[\u4e00-\u9fa5a-zA-Z0-9]*$/, // 字母数字汉字
    ONLY_EN_NUM: /^[a-zA-Z0-9]{0,30}$/ig,
    NUMBER_CONTAIN_FLOAT: /^-?(?:\d+)(?:.\d+)?$/, // 包含小数的数字正则
    NUMBER_CONTAIN_FLOAT_PLUS: /^(?:\d+)(?:.\d+)?$/, // 包含小数的数字正则,只允许正数
};

export const FORM_MODE = {
    ADD: 'ADD',
    EDIT: 'EDIT',
    REVIEW: 'REVIEW',
};

export const FORM_MODES = [FORM_MODE.ADD, FORM_MODE.EDIT, FORM_MODE.REVIEW];

export const BOOLEANS = [
    { label: 'common.yes', value: true },
    { label: 'common.no', value: false },
];

// 规则类型字典
export const RULE_TYPE_MAP = {
    // 单表规则
    SINGLE_TABLE_RULE: '1',
    // 跨表全量校验规则
    CROSS_TABLE_VERIFICATION_FULLY_RULE: '3',
    // 自定义规则
    CUSTOMIZATION_RULE: '2',
    // 文件校验规则
    FILE_VERIFICATION_RULE: '4',
};

export const TEMPLATE_FORMAT_LIST = [{
    value: 'SQL',
    key: 1,
}, {
    value: 'JAVA',
    key: 2,
}, {
    value: 'SCALA',
    key: 3,
}, {
    value: '元数据接口',
    key: 5,
}];

export const DATASOURCE_TYPE_LIST = [{
    label: 'HIVE',
    value: 1,
}, {
    label: 'MYSQL',
    value: 2,
}, {
    label: 'TDSQL',
    value: 3,
}, {
    label: 'KAFKA',
    value: 4,
}, {
    label: 'FPS',
    value: 5,
}];
export const DATASOURCE_TYPE_MAP = {
    1: 'hive',
    2: 'mysql',
    3: 'tdsql',
    4: 'kafka',
    5: 'fps',
};

export const CONDITIONBUTTONSPACE = 16;
export const INTMAXVALUE = 2147483647;

export const ACCEPT_TYPE_MAP = {
    JAR: 1,
    SCALA: 2,
    PYTHON: 3,
};
