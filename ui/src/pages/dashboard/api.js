import { request as FRequest } from '@fesjs/fes';

const DEFAULT_CONFIG = {
    method: 'POST',
};
// 获取今日告警数据
export function fetchAlarmData(params = {}) {
    return FRequest('/api/v1/projector/application/index/alarm/today', params, DEFAULT_CONFIG);
}

// 获取今日应用数据
export function fetchApplicationData(params = {}) {
    return FRequest('/api/v1/projector/application/index/application/today', params, DEFAULT_CONFIG);
}

// 获取今日告警chart数据
export function fetchAlarmChartData(params = {}) {
    return FRequest('/api/v1/projector/application/index/alarm/chart', params, DEFAULT_CONFIG);
}

// 获取今日应用chart数据
export function fetchApplicationChartData(params = {}) {
    return FRequest('/api/v1/projector/application/index/application/chart', params, DEFAULT_CONFIG);
}
