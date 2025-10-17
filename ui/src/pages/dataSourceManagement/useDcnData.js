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
                        value: vip.dcn_num + vip.dbinstance_name + vip.vip + vip.gwport,
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
        dcnTreeData.value = genTreeData(data);
        dcnListData.value = flatTreeData(dcnTreeData.value);
    };
    return {
        dcnTreeData,
        dcnListData,
        genDcnTreeData,
    };
};
