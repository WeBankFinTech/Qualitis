import { access, request as FRequest, useRouter } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { RULE_TYPE_MAP } from './const';

const router = useRouter();
// 获取用户信息
export async function getUserRole() {
    let username = '';
    let roles = [];
    // eslint-disable-next-line camelcase
    let login_random;
    try {
        const res = await FRequest('api/v1/projector/role', {}, { method: 'GET' });

        username = res.username;
        roles = res.roles;
        // eslint-disable-next-line camelcase
        login_random = res.login_random;
        sessionStorage.setItem('login_random', login_random);
        if (Array.isArray(roles) && roles.length > 0) {
            access.setRole(roles[0].toLowerCase());
        }
        let role = 'noauth';
        sessionStorage.setItem('userLogin', true);
        // 如果没有设置模拟账户走原来正常初始化的逻辑
        const isSimulatorMode = !!sessionStorage.getItem('simulatedUser');
        if (!isSimulatorMode) {
            // 兜底处理
            if (roles.length && (roles.includes('admin') || roles.includes('ADMIN'))) {
                role = 'admin';
            }
            // 缓存原本的身份
            sessionStorage.setItem('firstRole', role);
            // 缓存原本身份的名字
            sessionStorage.setItem('firstUserName', username);
            access.setRole(role);
        }
        return {
            userName: username,
            role,
        };
    } catch (error) {
        FMessage.error('登录失败');
        console.error(error);
        access.setRole('noauth');
        sessionStorage.setItem('firstRole', 'noauth');
        sessionStorage.setItem('firstUserName', 'noauth');
        return {
            userName: 'noauth',
            role: 'noauth',
        };
    }
}
/**
 *是否为IE浏览器
 */
export const isIE = () => {
    if (!!window.ActiveXObject || 'ActiveXObject' in window) {
        // eslint-disable-next-line no-alert
        alert('请使用Chrome或其他高级浏览器，IE可能会无法正常显示');
        return true;
    }
};

/*
 * Download--Excel
 * @param {blob} data
 * @param {文件名} fileName
 */
export const forceDownload = (blob, fileName, callback) => {
    const elink = document.createElement('a');
    elink.style.display = 'none';
    elink.download = fileName;
    elink.href = blob;
    elink.click();
    if (callback instanceof Function) {
        callback();
    }
};

export const getLabelFromList = (list, value, { labelFieldName = 'label', valueFieldName = 'value' } = {}) => {
    if (!Array.isArray(list)) return value || '--';
    const target = list.find(item => item[valueFieldName] === value);
    return (target ? target[labelFieldName] : value) || '--';
};

/**
 * 创建 规则表 标识，由规则分类、表格类型构成，格式为 规则分类-表格类型
 * @param {String} ruleType 规则分类
 * @param {String} type 表格类型
 * @return {String} 规则表 标识
 */
function buildRuleTableFlag(ruleType, type) {
    if (!Object.values(RULE_TYPE_MAP).includes(String(ruleType))) {
        console.error(`创建规则表失败: ruleType: ${ruleType} 未定义`);
        return;
    }
    return `${ruleType}-${type}`;
}
// 单表规则
const SINGLE_TABLE_RULE_FLAG = buildRuleTableFlag(RULE_TYPE_MAP.SINGLE_TABLE_RULE, '1');
// 跨表全量校验规则
const CROSS_TABLE_VERIFICATION_FULLY_RULE_FLAG = buildRuleTableFlag(RULE_TYPE_MAP.CROSS_TABLE_VERIFICATION_FULLY_RULE, '1');
// 自定义规则
const CUSTOMIZATION_RULE_FLAG = buildRuleTableFlag(RULE_TYPE_MAP.CUSTOMIZATION_RULE, '1');
// 跨库全量校验规则
const CROSS_DB_VERIFICATION_FULLY_RULE_FLAG = buildRuleTableFlag(RULE_TYPE_MAP.CROSS_TABLE_VERIFICATION_FULLY_RULE, '2');
// 文件校验规则
const FILE_VERIFICATION_RULE_FLAG = buildRuleTableFlag(RULE_TYPE_MAP.FILE_VERIFICATION_RULE, '1');
// sql检验规则
const SQL_VERIFICATION_RULE_FLAG = buildRuleTableFlag(RULE_TYPE_MAP.CUSTOMIZATION_RULE, '2');

const executeConfigRepeatValidator = (value, separator1 = ';', separator2 = '=') => {
    if (typeof value !== 'string') return false;
    const map = {};
    const fieldList = value.split(separator1);
    for (let i = 0; i < fieldList.length; i++) {
        const field = fieldList[i];
        const entry = field.split(separator2);
        const fieldName = entry[0];
        if (map[fieldName]) return false;
        map[fieldName] = true;
    }
    return true;
};

export {
    SINGLE_TABLE_RULE_FLAG,
    CROSS_TABLE_VERIFICATION_FULLY_RULE_FLAG,
    CUSTOMIZATION_RULE_FLAG,
    CROSS_DB_VERIFICATION_FULLY_RULE_FLAG,
    FILE_VERIFICATION_RULE_FLAG,
    SQL_VERIFICATION_RULE_FLAG,
};
