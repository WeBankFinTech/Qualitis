// 规则类型字典
const RULE_TYPE_MAP = {
    // 单表规则
    SINGLE_TABLE_RULE: '1',
    // 跨表全量校验规则
    CROSS_TABLE_VERIFICATION_FULLY_RULE: '3',
    // 自定义规则
    CUSTOMIZATION_RULE: '2',
    // 文件校验规则
    FILE_VERIFICATION_RULE: '4'
};

const FORM_MODE = {
    ADD: 'add',
    EDIT: 'edit',
    REVIEW: 'review'
};

const HIVE_NUMBER_TYPES = [
    'int',
    'double',
    'tinyint',
    'smallint',
    'bigint',
    'float',
    'decimal'
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
    'DECIMAL'
];

const NUMBER_TYPES = [
    ...HIVE_NUMBER_TYPES,
    ...MYSQL_NUMBER_TYPES
];

const COMMON_REG = {
    EN_NAME: /^[a-zA-Z0-9_]{0,64}$/ig,
    CN_NAME: /^[\u4e00-\u9fa5a-zA-Z0-9_]{0,128}$/ig
};

export {
    RULE_TYPE_MAP,
    FORM_MODE,
    NUMBER_TYPES,
    COMMON_REG
};
