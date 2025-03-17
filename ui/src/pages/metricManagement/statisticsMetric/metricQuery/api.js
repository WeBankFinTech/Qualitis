import { request } from '@fesjs/fes';
// 查询指标采集列表
export const fetchImsMetricList = async params => request(
    '/api/v1/projector/imsmetric/collect_list',
    params,
    {
        method: 'post',
    },
);
// 查询指标采集下拉框数据
export const fetchImsMetricOptions = async () => request(
    '/api/v1/projector/imsmetric/data_source/conditions',
    {},
    {
        method: 'get',
    },
);
// 查询指标采集字段下拉框数据
// export const fetchImsMetricColumns = async params => request(
//     '/api/v1/projector/meta_data/column',
//     { ...params, page_size: 1000 },
//     {
//         method: 'post',
//     },
// );
// 查询采集模板下拉框数据
export const fetchImsMetricTemplates = async () => request(
    '/api/v1/projector/rule_template/default/all',
    { page: 0, size: 1000, template_type: 5 },
    {
        method: 'post',
    },
);
// 查询采集人下拉框数据
export const fetchImsMetricUsers = async () => request(
    '/api/v1/projector/imsmetric/option/user_list',
    {},
    {
        method: 'get',
    },
);
// 指标同步
export function fetctmetricSync() {
    return request('/api/v1/projector/imsmetric/metadata/sync', {}, 'get');
}
