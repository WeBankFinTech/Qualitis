import { request as FRequest } from '@fesjs/fes';

const DEFAULT_CONFIG = {
    method: 'post',
};

export function getMetricData(params = {}) {
    return FRequest('/api/v1/projector/imsmetric/getMetricData', params, DEFAULT_CONFIG);
}
export function getAlarmData(params = {}) {
    return FRequest('/api/v1/projector/imsmetric/getAlarmData', params, DEFAULT_CONFIG);
}

export function getMetricRelation(params = {}) {
    return FRequest('/api/v1/projector/imsmetric/getMetricRelation', params, DEFAULT_CONFIG);
}
