// 告警等级列表
export const ALARM_LEVELS = [
    { label: 'Critical', value: '1', classNames: ['red'] },
    { label: 'Major', value: '2', classNames: ['blue'] },
    { label: 'Minor', value: '3', classNames: ['orange'] },
    { label: 'Warning', value: '4', classNames: ['yellow'] },
    { label: 'Info', value: '5', classNames: ['green'] },
];

// 接口返回的 各种状态告警统计数量的字段，通过status字段 可以关联 该统计数量 所属的告警
export const ALARM_LEVELS_FIELD_NAMES = [
    { fieldName: 'alarm_critical_num', status: 1 },
    { fieldName: 'alarm_major_num', status: 2 },
    { fieldName: 'alarm_minor_num', status: 3 },
    { fieldName: 'alarm_warning_num', status: 4 },
    { fieldName: 'alarm_info_num', status: 5 },
];

// 应用状态列表
export const APPLICATION_STATUSES = [
    { label: '已提交', value: 1 },
    { label: '运行中', value: 3 },
    { label: '通过校验', value: 4, classNames: ['green'] },
    { label: '失败', value: 7, classNames: ['red'] },
    { label: '未通过校验', value: 8, classNames: ['orange'] },
    { label: '任务初始化失败', value: 9, classNames: ['red'] },
    { label: '任务初始化成功', value: 10 },
    { label: '参数错误', value: 11 },
];

// 接口返回的 各种应用状态统计数量的字段，通过status字段 可以关联 该统计数量 所属的 应用状态
export const APPLICATION_STATUS_FIELD_NAMES = [
    { fieldName: 'application_succ_num', status: 4 },
    { fieldName: 'application_fail_check_num', status: 8 },
    { fieldName: 'application_fail_num', status: 7 },
];
