import { ref } from 'vue';
import { getDcnData } from './api';

export const useDcnData = () => {
    // IMS-CONFIG
    const dcnTreeData = ref([]);
    const dcnListData = ref([]); // 打平后的dcndata
    const genTreeData = (data) => {
        const result = Object.keys(data).map(
            logicDcn => ({
                value: logicDcn,
                label: logicDcn,
                children: Object.keys(data[logicDcn]).map(dcnName => ({
                    value: logicDcn + dcnName,
                    label: dcnName,
                    children: data[logicDcn][dcnName].map(vip => ({
                        value: vip.db_name + vip.vip + vip.gwport,
                        label: `${dcnName}-${vip.vip}:${vip.gwport} ${vip.set_type ? `(${vip.set_type})` : ''}`,
                        // disabled: vip.set_type === 'MASTER',
                        ...vip,
                    })),
                })),
            }),
        );
        console.log(result);
        return result;
    };
    const flatTreeData = tree => tree.reduce((pre, cur) => {
        if (cur.children && cur.children.length > 0) {
            return pre.concat([cur, ...flatTreeData(cur.children)]);
        }
        return pre.concat(cur);
    }, []);
    const genDcnTreeData = async ({ subSystemId = 2819 }) => {
        const result = await getDcnData({ subSystemId });
        const data = result.res;
        console.log(data);
        // {
        //     "logic_dcn": "IA0",# 逻辑DCN编码
        //     "db_name": "cuecsbcore",# 数据库名
        //     "clu_name": "ADM_IA1", # 集群名
        //     "set_name": "ADM_IA1_set_1",# SET名称
        //     "gwport": "3306", # vip 端口
        //     "dbinstance_name": "cuecsbcore_ADM_IA1_set_1",# 实例名称
        //     "set_type": "MASTER", # 主备类型
        //     "idc": "I", # IDC
        //     "logic_area": "ADM", # 逻辑区域名称
        //     "vip": "10.107.96.172",# vip 地址
        //     "phy_set_name": "ADM_IA1_set_1",# 物理SET名称
        //     "dcn_num": "IA1" # DCN编号
        // },
        dcnTreeData.value = genTreeData(data);
        dcnListData.value = flatTreeData(dcnTreeData.value);
    };
    return {
        dcnTreeData,
        dcnListData,
        genDcnTreeData,
    };
};
