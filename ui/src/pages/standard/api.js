import { request as FRequest } from '@fesjs/fes';

// 标准值来源下拉框数据
export function fetchStdSource() {
    return FRequest('/api/v1/projector/standardValue/source/all', {});
}

// 获取标准值详情
export function fetchStdDetail(tid) {
    return FRequest(`/api/v1/projector/standardValue/${tid}`, {}, 'get');
}

// 获取数据标准类别
export function fetchStdType(params) {
    return FRequest('/api/v1/projector/standardValue/dms/standard/category', params);
}

// 获取数据标准大类
export function fetchBigStd(params) {
    return FRequest('/api/v1/projector/standardValue/dms/standard/big/category', params);
}

// 获取数据标准小类
export function fetchSmallStd(params) {
    return FRequest('/api/v1/projector/standardValue/dms/standard/small/category', params);
}

// 获取数据标准
export function fetchDataStd(params) {
    return FRequest('/api/v1/projector/standardValue/dms/standard/data', params);
}

// 获取标准代码
export function fetchStdCode(params) {
    return FRequest('/api/v1/projector/standardValue/dms/standard/code', params);
}

// 获取标准值内容
export function fetchStdContent(params) {
    return FRequest('/api/v1/projector/standardValue/dms/standard/code/table', params);
}
