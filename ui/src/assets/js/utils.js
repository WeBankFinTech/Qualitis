import { RULE_TYPE_MAP } from './const';

function dateFormat(fmt, date = new Date()) {
    var o = {
        "M+": date.getMonth() + 1, //月份
        "d+": date.getDate(), //日
        "H+": date.getHours(), //小时
        "m+": date.getMinutes(), //分
        "s+": date.getSeconds(), //秒
        "q+": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k])
                .length)));
        }
    }
    return fmt;
}

/**
 * 获取人事接口提供的人员信息，并缓存
 */
function getStaff(ctx) {
    return new Promise(async (resolve, reject)=> {
        if(typeof ctx === 'undefined' || !ctx.FesApi){
            console.warn('ctx must not undefined');
            resolve([]);
            return;
        }
        let cacheStaff = ctx.FesApp.get("staffList");
        if (Array.isArray(cacheStaff)) {
            resolve(cacheStaff);
            return;
        }

        let staff = [];
        try {
            let result = await ctx.FesApi.fetch("api/v1/projector/staff", {}, "get");
            if (Array.isArray(result)) {
                staff = result.map((item) => {
                    let info = item.split(",");
                    return {
                        "username": info[0],
                        "chinese_name": info[1],
                        "full_name": `${info[0]}(${info[1]})`,
                        "org_name": info[2]
                    }
                });
                staff = _.uniqBy(staff,"username");
            }
            ctx.FesApp.set("staffList", staff);
        } catch (error) {
            console.error(error);
            reject(error);
        }
        resolve(staff);
    })
}


/*
 * Download--Excel
 * @param {blob} data
 * @param {文件名} fileName
 */
function forceDownload(blob, fileName, callback) {
    const elink = document.createElement('a');
    elink.style.display = 'none';
    elink.download = fileName;
    elink.href = blob;
    elink.click();
    if (callback instanceof Function) callback();
}


/**
 *是否为IE浏览器
 */
function isIE() {
    if (!!window.ActiveXObject || "ActiveXObject" in window) {
        alert('请使用Chrome或其他高级浏览器，IE可能会无法正常显示');
        return true;
    }
}

function getUserRole(params) {
    params.FesApi.fetch("api/v1/projector/role", "get").then(({roles,username}) => {
        if(Array.isArray(roles)){
            roles = roles.map(item => item.toLowerCase())
        }
        let role = 'noauth';
        if(roles && roles.indexOf('admin') > -1 ){
            role = 'admin';
            params.FesApp.set("FesRoleName", "管理员");
        }
        params.FesApp.setRole(role);
        if (!params.FesStorage.get('simulatedUser')) {
            params.FesApp.set("FesUserName", username);
        }
        params.$router.push({ path: "/dashboard" });
    }).catch(() => {
        let role = params.FesStorage.get('firstRole');
        if (role) {
            params.FesApp.setRole(role);
        }else {
            params.FesApp.setRole('noauth');
        }
    });
}

function DWSMessage(key, ruleGroupId, action) {
    const jsonStr = {
        type: 'qualitis',
        nodeId: key,
        data: {
            ruleGroupId,
            action
        }
    }
    window.parent.postMessage(JSON.stringify(jsonStr), '*')
}

/**
 * 创建 规则表 标识，由规则分类、表格类型构成，格式为 规则分类-表格类型
 * @param {String} ruleType 规则分类
 * @param {String} type 表格类型
 * @return {String} 规则表 标识
 */
function buildRuleTableFlag(ruleType, type) {
    if (!Object.values(RULE_TYPE_MAP).includes(String(ruleType))) {
        console.error('创建规则表失败: ruleType: ' + ruleType + ' 未定义');
        return;
    }
    return ruleType + '-' + type;
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

const executeConfigDynamicPartitionPrefixValidator = (value) => {
    const reg = /^([a-zA-Z0-9_-]+\s*=\s*(([a-zA-Z0-9_-]+)|("[a-zA-Z0-9_-]+")|('[a-zA-Z0-9_-]+'))\s+and\s+)*([a-zA-Z0-9_-]+\s*=\s*(([a-zA-Z0-9_-]+)|("[a-zA-Z0-9_-]+")|('[a-zA-Z0-9_-]+')))$/ig;
    return reg.test(value);
};

const buildExecutionParam = (str) => {
    if (!str || typeof str !== 'string') return {};
    let result = {};
    const entries = str.split(';');
    let entry;
    let tempArr;
    for (let i = 0; i < entries.length; i++) {
        entry = entries[i];
        if (entry) {
            tempArr = entry.split(':');
            if (tempArr.length === 2) {
                result = Object.assign(result, {
                    [tempArr[0]]: tempArr[1]
                });
            }
        }
    }
    return result;
};

/**
 *
 * @param {Vue} Vue Vue构造器
 * @param {Object} row 行
 * @param {String} fieldName 字段名
 * @param {Object} config 配置，单元格宽度、Tooltip展示位置
 * @return {Component} Vue组件
 */
const buildTooltipCellComponent = (Vue, row, fieldName, config = {}) => {
    const {
        width = 200,
        direction = 'right',
        filter
    } = config;
    const fieldValue = filter instanceof Function ? filter(row[fieldName]) : row[fieldName];
    return new Vue({
        data: function() {
            return {
                option: {
                    direction,
                    align: 'center',
                    component: new Vue({
                        render: function(h) {
                            return h('div', {
                                class: {
                                    'tip-des': true
                                },
                            }, [h('p', fieldValue)]);
                        }
                    })
                }
            };
        },
        render: function(h) {
            const spanRenderOptions = {
                style: {
                    width: `${width}px`,
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis'
                },
                directives: [
                    {
                        name: 'tooltip',
                        value: this.option
                    }
                ],
                on: {
                    click: function() {
                    }
                }
            };
            return h('div', spanRenderOptions, fieldValue);
        }
    });
};

/**
 * 获取当前输入框的焦点位置信息，start，end
 * @param {String} elementId DOM节点id
 */
const getCursorPositionInformation = (elementId) => {
    const isIE = !!window.ActiveXObject || "ActiveXObject" in window;
    const oTextarea = document.getElementById(elementId);
    let start = 0;
    let end = 0;
    if (isIE) {
        //selection 当前激活选中区，即高亮文本块，和/或文当中用户可执行某些操作的其它元素。
        //createRange 从当前文本选中区中创建 TextRange 对象，
        //或从控件选中区中创建 controlRange 集合。
        const sTextRange = document.selection.createRange();

        //判断选中的是不是textarea对象
        if (sTextRange.parentElement() === oTextarea) {
            //创建一个TextRange对象
            const oTextRange = document.body.createTextRange();
            //移动文本范围以便范围的开始和结束位置能够完全包含给定元素的文本。
            oTextRange.moveToElementText(oTextarea);

            //此时得到两个 TextRange
            //oTextRange文本域(textarea)中文本的TextRange对象
            //sTextRange是选中区域文本的TextRange对象

            //compareEndPoints方法介绍，compareEndPoints方法用于比较两个TextRange对象的位置
            //StartToEnd  比较TextRange开头与参数TextRange的末尾。
            //StartToStart比较TextRange开头与参数TextRange的开头。
            //EndToStart  比较TextRange末尾与参数TextRange的开头。
            //EndToEnd    比较TextRange末尾与参数TextRange的末尾。

            //moveStart方法介绍，更改范围的开始位置
            //character 按字符移动
            //word       按单词移动
            //sentence  按句子移动
            //textedit  启动编辑动作

            //这里我们比较oTextRange和sTextRange的开头，的到选中区域的开头位置
            for (start = 0; oTextRange.compareEndPoints("StartToStart", sTextRange) < 0; start++) {
                oTextRange.moveStart('character', 1);
            }
            //需要计算一下\n的数目(按字符移动的方式不计\n,所以这里加上)
            for (let i = 0; i <= start; i++) {
                if (oTextarea.value.charAt(i) == '\n') {
                    start++;
                }
            }

            //再计算一次结束的位置
            oTextRange.moveToElementText(oTextarea);
            for (end = 0; oTextRange.compareEndPoints('StartToEnd', sTextRange) < 0; end++) {
                oTextRange.moveStart('character', 1);
            }
            for (let i = 0; i <= end; i++) {
                if (oTextarea.value.charAt(i) == '\n') {
                    end++;
                }
            }
        }
    } else {
        start = oTextarea.selectionStart;
        end = oTextarea.selectionEnd;
    }
    return {
        start,
        end
    };
};

/**
 *
 * @param {String} elementId DOM节点id
 * @param {Object} position 位置信息，start, end
 */
const setCursorPositionInformation = (elementId, position) => {
    const isIE = !!window.ActiveXObject || "ActiveXObject" in window;
    const oTextarea = document.getElementById(elementId);
    const start = parseInt(position.start);
    const end = parseInt(position.end);
    if (isNaN(start) || isNaN(end)) {
        console.error('位置输入错误');
    }
    if (isIE) {
        const oTextRange = oTextarea.createTextRange();
        const LStart = start;
        const LEnd = end;
        let start = 0;
        let end = 0;
        var value = oTextarea.value;
        for (let i = 0; i < value.length && i < LStart; i++) {
            const c = value.charAt(i);
            if (c != '\n') {
                start++;
            }
        }
        for (let i = value.length - 1; i >= LEnd && i >= 0; i--) {
            const c = value.charAt(i);
            if (c != '\n') {
                end++;
            }
        }
        oTextRange.moveStart('character', start);
        oTextRange.moveEnd('character', -end);
        //oTextRange.collapse(true);
        oTextRange.select();
        oTextarea.focus();
    } else {
        oTextarea.select();
        oTextarea.selectionStart = start;
        oTextarea.selectionEnd = end;
    }
};

const isNullOrUndefined = (value) => value === null || value === undefined;

export {
    dateFormat,
    getStaff,
    forceDownload,
    isIE,
    getUserRole,
    DWSMessage,
    buildRuleTableFlag,
    SINGLE_TABLE_RULE_FLAG,
    CROSS_TABLE_VERIFICATION_FULLY_RULE_FLAG,
    CUSTOMIZATION_RULE_FLAG,
    CROSS_DB_VERIFICATION_FULLY_RULE_FLAG,
    FILE_VERIFICATION_RULE_FLAG,
    SQL_VERIFICATION_RULE_FLAG,
    executeConfigRepeatValidator,
    executeConfigDynamicPartitionPrefixValidator,
    buildExecutionParam,
    buildTooltipCellComponent,
    getCursorPositionInformation,
    setCursorPositionInformation,
    isNullOrUndefined
};
