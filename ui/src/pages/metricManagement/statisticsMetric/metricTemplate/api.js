import { request as FRequest } from '@fesjs/fes';

// 获取指标模板
export function fetchTemplates(params = {}) {
    return FRequest('/api/v1/projector/imsmetric/template/list', params);
}
