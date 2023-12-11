//  规则模版对应的id
export const TEMPLATE_TYPES = {
    SINGLE_TABLE_CHECK_TYPE: 1,
    SQLVERIFICATION_TYPE: 2,
    CROSS_TABLE_CHECK_TYPE: 3,
    DOCUMENT_TABLE_CHECK_TYPE: 4,
};

// 规则模版占位符对应的input_type（部分）
export const TEMPLATE_ARGUMENT_INPUT_TYPE = {
    // 校验字段
    FEILD: 4,
    // 标准值
    STANDARD_VALUE: 39,
    // 枚举值
    LIST: 8,
    // 中间表达式
    EXPRESSION: 37,
    // 最大值
    MAX: 36,
    // 最小值
    MIN: 38,
    // 数据库
    DB: 5,
    // 数据表
    DB_TABLE: 3,
    // 多表
    // 比对字段设置
    COMPARISON_FIELD: 33,
    // 对比类型
    CONTRAST_TYPE: 25,
    // 比对结果过滤条件
    RESULTS_FOR_FILTER: 34,
    // 连接字段设置
    CONNECT_FIELDS: 32,
};
