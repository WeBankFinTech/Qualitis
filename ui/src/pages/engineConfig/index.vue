<template>
    <div class="wd-content engine-config">
        <div class="wd-content-body">
            <div class="page-header-condition">
                <div class="condition-item">
                    <span class="condition-label">{{$t('engineConfig.cluster')}}</span>
                    <FSelect
                        v-model="clusterName"
                        clearable
                        :options="clusterList"
                        labelField="label"
                        valueField="label"
                    />
                </div>
                <!-- 用户 -->
                <div class="condition-item">
                    <span class="condition-label">{{$t('engineConfig.user')}}</span>
                    <FSelect
                        v-model="userName"
                        clearable
                        filterable
                        :options="proxyUserList"
                        labelField="label"
                        valueField="label"
                    />
                </div>
                <div class="condition-item">
                    <FSpace :size="CONDITIONBUTTONSPACE">
                        <FButton type="primary" @click="search">{{$t('engineConfig.search')}}</FButton>
                    </FSpace>
                </div>
            </div>
            <div v-if="showLoading">
                <BPageLoading :loadingText="{ loading: '' }" />
            </div>
            <div v-else class="engine-list">
                <!-- Links资源配置 -->
                <div v-for="(variable,vIndex ) in tree1" v-show="variable.name !== 'tidb设置' && variable.name !== 'spark引擎设置'" :key="vIndex" class="engine-item">
                    <div class="engine-title" :class="{ settings: variable.operation !== 'settings' }">
                        <div class="title">
                            <span class="span-name">{{variable.editName}}</span>
                            ({{variable.settings.length}})
                        </div>
                        <div v-if="variable.operation === 'settings'"
                             class="btn"
                             @click="variableSetting(variable,vIndex,1)"
                        >
                            {{$t('engineConfig.settings')}}
                        </div>
                        <div class="settings-btn"
                             @click="variableSave(variable)"
                        >
                            {{$t('engineConfig.save')}}
                        </div>
                        <div class="settings-btn"
                             @click="variableCancel(variable,vIndex,1)"
                        >
                            {{$t('engineConfig.cancel')}}
                        </div>
                    </div>
                    <div class="ranks-list">
                        <div v-for="(item , index) in variable.settings" :key="index" class="ranks-item">
                            <div class="ranks-left">
                                <div class="ranks-title"><FEllipsis style="max-width: 100%">{{item.name}}</FEllipsis></div>
                                <div class="ranks-subtitle">
                                    <FEllipsis style="max-width: 100%">[{{item.key}}]</FEllipsis>
                                </div>
                            </div>
                            <div class="ranks-right">
                                <template v-if="variable.operation === 'settings'">
                                    <FEllipsis style="max-width: 128px">{{item.configValue || item.defaultValue}}</FEllipsis>
                                    <span v-if="unValidMsg && unValidMsg.key === item.key"
                                          class="we-warning-bar">
                                        {{unValidMsg.msg}}
                                    </span>
                                </template>
                                <template v-else>
                                    <FSelect
                                        v-if="item.validateType === 'OFT'"
                                        v-model="item.configValue"
                                        class="iview-select"
                                        :placeholder="item.defaultValue ? `默认值:${item.defaultValue}` : '无默认值'"
                                    >
                                        <FOption v-for="(validateItem, i) in JSON.parse(item.validateRange)"
                                                 :key="i"
                                                 :value="validateItem"
                                                 :label="validateItem">
                                        </FOption>
                                    </FSelect>
                                    <FInput
                                        v-else
                                        v-model="item.configValue"
                                        :placeholder="item.defaultValue ? `默认值:${item.defaultValue}` : '无默认值'"
                                        type="text"
                                        class="we-variable-content-input"
                                        :class="{ 'un-valid': unValidMsg && unValidMsg.key === item.key }"
                                    />
                                    <span v-if="unValidMsg && unValidMsg.key === item.key"
                                          class="we-warning-bar">
                                        {{unValidMsg.msg}}
                                    </span>
                                </template>
                            </div>
                        </div>
                    </div>
                </div>
                <div>
                    <div v-for="(variable,vIndex ) in tree2" v-show="variable.name !== 'tidb设置' && variable.name !== 'spark引擎设置'" :key="vIndex" class="engine-item">
                        <div class="rabnks-box">
                            <div class="engine-title" :class="{ settings: variable.operation !== 'settings' }">
                                <div class="title">
                                    <span class="span-name">{{variable.editName}}</span>
                                    ({{variable.settings.length}})
                                </div>
                                <div v-if="variable.operation === 'settings'"
                                     class="btn"
                                     @click="variableSetting(variable,vIndex,2)"
                                >
                                    {{$t('engineConfig.settings')}}
                                </div>
                                <div class="settings-btn"
                                     @click="variableSave(variable)"
                                >
                                    {{$t('engineConfig.save')}}
                                </div>
                                <div class="settings-btn"
                                     @click="variableCancel(variable,vIndex,2)"
                                >
                                    {{$t('engineConfig.cancel')}}
                                </div>
                            </div>
                            <div class="ranks-list">
                                <div v-for="(item , index) in variable.settings" :key="index" class="ranks-item">
                                    <div class="ranks-left">
                                        <div class="ranks-title"><FEllipsis style="max-width: 100%">{{item.name}}</FEllipsis></div>
                                        <div class="ranks-subtitle">
                                            <FEllipsis style="max-width: 100%">[{{item.key}}]</FEllipsis>
                                        </div>
                                    </div>
                                    <div class="ranks-right">
                                        <template v-if="variable.operation === 'settings'">
                                            <FEllipsis style="max-width: 128px">{{item.configValue || item.defaultValue}}</FEllipsis>
                                            <span v-if="unValidMsg && unValidMsg.key === item.key"
                                                  class="we-warning-bar">
                                                {{unValidMsg.msg}}
                                            </span>
                                        </template>
                                        <template v-else>
                                            <FSelect
                                                v-if="item.validateType === 'OFT'"
                                                v-model="item.configValue"
                                                class="iview-select"
                                                :placeholder="item.defaultValue ? `默认值:${item.defaultValue}` : '无默认值'"
                                            >
                                                <FOption v-for="(validateItem, i) in JSON.parse(item.validateRange)"
                                                         :key="i"
                                                         :value="validateItem"
                                                         :label="validateItem">
                                                </FOption>
                                            </FSelect>
                                            <FInput
                                                v-else
                                                v-model="item.configValue"
                                                :placeholder="item.defaultValue ? `默认值:${item.defaultValue}` : '无默认值'"
                                                type="text"
                                                class="we-variable-content-input"
                                                :class="{ 'un-valid': unValidMsg && unValidMsg.key === item.key }"
                                            />
                                            <span v-if="unValidMsg && unValidMsg.key === item.key"
                                                  class="we-warning-bar">
                                                {{unValidMsg.msg}}
                                            </span>
                                        </template>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div v-for="(variable,vIndex ) in tree4" v-show="variable.name !== 'tidb设置' && variable.name !== 'spark引擎设置'" :key="vIndex" class="engine-item">
                        <div class="rabnks-box">
                            <div class="engine-title" :class="{ settings: variable.operation !== 'settings' }">
                                <div class="title">
                                    <span class="span-name">{{variable.editName}}</span>
                                    ({{variable.settings.length}})
                                </div>
                                <div v-if="variable.operation === 'settings'"
                                     class="btn"
                                     @click="variableSetting(variable,vIndex,4)"
                                >
                                    {{$t('engineConfig.settings')}}
                                </div>
                                <div class="settings-btn"
                                     @click="variableSave(variable)"
                                >
                                    {{$t('engineConfig.save')}}
                                </div>
                                <div class="settings-btn"
                                     @click="variableCancel(variable,vIndex,4)"
                                >
                                    {{$t('engineConfig.cancel')}}
                                </div>
                            </div>
                            <div class="ranks-list">
                                <div v-for="(item , index) in variable.settings" :key="index" class="ranks-item">
                                    <div class="ranks-left">
                                        <div class="ranks-title"><FEllipsis style="max-width: 100%">{{item.name}}</FEllipsis></div>
                                        <div class="ranks-subtitle">
                                            <FEllipsis style="max-width: 100%">[{{item.key}}]</FEllipsis>
                                        </div>
                                    </div>
                                    <div class="ranks-right">
                                        <template v-if="variable.operation === 'settings'">
                                            <FEllipsis style="max-width: 128px">{{item.configValue || item.defaultValue}}</FEllipsis>
                                            <span v-if="unValidMsg && unValidMsg.key === item.key"
                                                  class="we-warning-bar">
                                                {{unValidMsg.msg}}
                                            </span>
                                        </template>
                                        <template v-else>
                                            <FSelect
                                                v-if="item.validateType === 'OFT'"
                                                v-model="item.configValue"
                                                class="iview-select"
                                                :placeholder="item.defaultValue ? `默认值:${item.defaultValue}` : '无默认值'"
                                            >
                                                <FOption v-for="(validateItem, i) in JSON.parse(item.validateRange)"
                                                         :key="i"
                                                         :value="validateItem"
                                                         :label="validateItem">
                                                </FOption>
                                            </FSelect>
                                            <FInput
                                                v-else
                                                v-model="item.configValue"
                                                :placeholder="item.defaultValue ? `默认值:${item.defaultValue}` : '无默认值'"
                                                type="text"
                                                class="we-variable-content-input"
                                                :class="{ 'un-valid': unValidMsg && unValidMsg.key === item.key }"
                                            />
                                            <span v-if="unValidMsg && unValidMsg.key === item.key"
                                                  class="we-warning-bar">
                                                {{unValidMsg.msg}}
                                            </span>
                                        </template>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div v-for="(variable,vIndex ) in tree3" v-show="variable.name !== 'tidb设置' && variable.name !== 'spark引擎设置'" :key="vIndex" class="engine-item">
                        <div class="rabnks-box">
                            <div class="engine-title" :class="{ settings: variable.operation !== 'settings' }">
                                <div class="title">
                                    <span class="span-name">{{variable.editName}}</span>
                                    ({{variable.settings.length}})
                                </div>
                                <div v-if="variable.operation === 'settings'"
                                     class="btn"
                                     @click="variableSetting(variable,vIndex,3)"
                                >
                                    {{$t('engineConfig.settings')}}
                                </div>
                                <div class="settings-btn"
                                     @click="variableSave(variable)"
                                >
                                    {{$t('engineConfig.save')}}
                                </div>
                                <div class="settings-btn"
                                     @click="variableCancel(variable,vIndex,3)"
                                >
                                    {{$t('engineConfig.cancel')}}
                                </div>
                            </div>
                            <div class="ranks-list">
                                <div v-for="(item , index) in variable.settings" :key="index" class="ranks-item">
                                    <div class="ranks-left">
                                        <div class="ranks-title"><FEllipsis style="max-width: 100%">{{item.name}}</FEllipsis></div>
                                        <div class="ranks-subtitle">
                                            <FEllipsis style="max-width: 100%">[{{item.key}}]</FEllipsis>
                                        </div>
                                    </div>
                                    <div class="ranks-right">
                                        <template v-if="variable.operation === 'settings'">
                                            <FEllipsis style="max-width: 128px">{{item.configValue || item.defaultValue}}</FEllipsis>
                                            <span v-if="unValidMsg && unValidMsg.key === item.key"
                                                  class="we-warning-bar">
                                                {{unValidMsg.msg}}
                                            </span>
                                        </template>
                                        <template v-else>
                                            <FSelect
                                                v-if="item.validateType === 'OFT'"
                                                v-model="item.configValue"
                                                class="iview-select"
                                                :placeholder="item.defaultValue ? `默认值:${item.defaultValue}` : '无默认值'"
                                            >
                                                <FOption v-for="(validateItem, i) in JSON.parse(item.validateRange)"
                                                         :key="i"
                                                         :value="validateItem"
                                                         :label="validateItem">
                                                </FOption>
                                            </FSelect>
                                            <FInput
                                                v-else
                                                v-model="item.configValue"
                                                :placeholder="item.defaultValue ? `默认值:${item.defaultValue}` : '无默认值'"
                                                type="text"
                                                class="we-variable-content-input"
                                                :class="{ 'un-valid': unValidMsg && unValidMsg.key === item.key }"
                                            />
                                            <span v-if="unValidMsg && unValidMsg.key === item.key"
                                                  class="we-warning-bar">
                                                {{unValidMsg.msg}}
                                            </span>
                                        </template>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div v-if="!showLoading && !(tree1.length && tree1.length > 0) && !(tree2.length && tree2.length > 0) && !(tree3.length && tree3.length > 0) && !(tree4.length && tree4.length > 0)" class="empty-block">
                <BPageLoading actionType="emptyInitResult" />
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref } from 'vue';
import { FMessage } from '@fesjs/fes-design';
import { useI18n, useModel } from '@fesjs/fes';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
import { cloneDeep } from 'lodash-es';
import { BPageLoading } from '@fesjs/traction-widget';
import {
    getClusterListData, configurationData, configurationSave, getProxyUsers,
} from './api';


const initialState = useModel('@@initialState');
const { t: $t } = useI18n();
const showLoading = ref(false);
const unValidMsg = ref({});

const clusterName = ref('');
const userName = ref('');
const fullTree = ref([]);

function handleFullTree(dataTree, queueNameTree) {
    if (Array.isArray(dataTree) && Array.isArray(queueNameTree) && queueNameTree.length > 0) {
        // let queueTree;
        // let queueTree1;
        // let queueTree2;
        // let queueSettings1;
        // let queueSettings2;
        // let index;
        const queueTree1 = queueNameTree[0] || {};
        const queueSettings1 = queueTree1.settings || [];
        const index = dataTree.findIndex(item => item.name === queueTree1.name);
        const queueTree2 = dataTree[index] || {};
        const queueSettings2 = queueTree2.settings || [];
        const queueTree = Object.assign({}, queueTree1, queueTree2, { settings: queueSettings1.concat(queueSettings2) });
        if (index !== -1) {
            return [
                ...dataTree.slice(0, index),
                queueTree,
                ...dataTree.slice(index + 1),
            ];
        }
        return [
            queueTree,
            ...dataTree,
        ];
    }
    return dataTree;
}

/**
 * 检验集群列表数据
 */
function formatValidateRange(value, type) {
    const formatValue = [];
    let tmpList = [];
    try {
        tmpList = JSON.parse(value);
    } catch (e) {
        tmpList = value.slice(1, value.length - 1).split(',');
    }
    tmpList.forEach((item) => {
        formatValue.push({
            name: item === 'BLANK' && type === 'pipeline.out.null.type' ? '空字符串' : item,
            value: item,
        });
    });
    return formatValue;
}

const tree1 = ref([]);
const tree2 = ref([]);
const tree3 = ref([]);
const tree4 = ref([]);

/**
 * @param String clusterName 集群名
 * 获取配置列表
*/
async function getAppVariable(names) {
    showLoading.value = true;
    const params = {
        cluster_name: names.cluster_name,
        user_name: names?.user_name,
    };
    try {
        const res = await configurationData(params);
        const queueNameTree = res.full_tree_queue_name.fullTree;
        const dataTree = res.fule_tree.fullTree;
        fullTree.value = handleFullTree(dataTree, queueNameTree);
        fullTree.value.forEach((item) => {
            console.log(item);
            // 队列资源改成Links资源配置 spark资源设置改成Spark引擎配置
            item.editName = item.name;
            if (item.name === '队列资源') {
                item.editName = 'Links设置';
            }
            if (item.name === 'Spark参数') {
                item.editName = 'Spark设置';
            }
            // item.settings = this.FesUtil._.orderBy(item.settings, ['level'], ['asc']);
            item.settings.sort((a, b) => {
                if (a.level === b.level) {
                    return a.asc - b.asc;
                }
                return a.level - b.level;
            });
            if (item.settings.length) {
                item.settings.forEach((set) => {
                    if (set.validateType === 'OFT') {
                        set.validateRangeList = formatValidateRange(set.validateRange, set.key);
                    }
                    if (set.key === 'spark.application.pyFiles' || set.key === 'python.application.pyFiles') {
                        set.placeholder = '请输入工作空间python包路径（只支持zip）';
                    }
                });
            }
            item.operation = 'settings';
        });
        if (fullTree.value[0]) {
            tree1.value = [fullTree.value[0]];
        }
        if (fullTree.value[1]) {
            tree2.value = [fullTree.value[1]];
        }
        if (fullTree.value[2]) {
            tree3.value = [fullTree.value[2]];
        }
        if (fullTree.value[3]) {
            tree4.value = [fullTree.value[3]];
        }
        showLoading.value = false;
    } catch (error) {
        showLoading.value = false;
        console.error(error);
        fullTree.value = [];
        FMessage.error(error.data.message);
    }
}
// 查询
function search() {
    const parmas = {
        cluster_name: clusterName.value,
        user_name: userName.value,
    };
    console.log(parmas);
    getAppVariable(parmas);
}

// 集群
const clusterList = ref([]);
const proxyUserList = ref([]);
/**
 * 获取集群列表 用户列表
 */
async function getClusterList() {
    try {
        const { optional_clusters } = await getClusterListData();
        const res = await getProxyUsers();
        if (Array.isArray(optional_clusters)) {
            clusterList.value = optional_clusters.map(item => ({ label: item, value: item }));
            // console.log(clusterList.value[0].value)
            // 默认查询集群
            if (clusterList.value.length) {
                clusterName.value = clusterList.value[0].value;
                getAppVariable({ cluster_name: clusterName.value });
            }
        }
        if (Array.isArray(res) && res.length !== 0) {
            res.push(initialState.userName);
            proxyUserList.value = res.map(item => ({
                label: item,
                value: item,
            }));
        }
    } catch (error) {
        console.error(error);
    }
}
getClusterList();
const cacheBox = ref({});
// 设置
function variableSetting(variable, vIndex, treeIndex) {
    variable.operation = 'save';
    const key = `${treeIndex}${vIndex}`;
    cacheBox.value[key] = cloneDeep(variable);
}

// 保存设置
function variableSave() {
    showLoading.value = true;
    const params = {
        cluster_name: clusterName.value,
        full_tree: fullTree.value,
        user_name: userName.value,
    };
    configurationSave(params).then(() => {
        getAppVariable(params);
        unValidMsg.value = {};
        FMessage.success($t('engineConfig.saveSuccess'));
    }).catch((err) => {
        FMessage.error(err.response.data.message);
        if (err.message) {
            let key = '';
            let msg = '';
            fullTree.value.forEach((item) => {
                if (item.settings) {
                    item.settings.forEach((s) => {
                        if (s.validateType === 'OFT' && Object.prototype.hasOwnProperty.call(s, 'validateRangeList')) {
                            delete s.validateRangeList;
                        }
                        if (err.message.indexOf(s.key) > -1) {
                            msg = s.description;
                            key = s.key;
                        }
                    });
                }
            });
            unValidMsg.value = { key, msg };
            console.log(unValidMsg.value);
        }
    }).finally(() => {
        showLoading.value = false;
    });
}


function variableCancel(variable, vIndex, treeIndex) {
    const key = `${treeIndex}${vIndex}`;
    Object.assign(variable, cacheBox.value[key]);
    variable.operation = 'settings';
}

</script>
<style lang="less" scoped>
@import "@/style/mixins";
.engine-config{
    .division {
        width: 100%;
        height: 1px;
        background: rgba(15,18,34,0.06);
    }
    .fes-select {
        width: 200px;
    }
    .engine-list{
        display: flex;
        flex-wrap: wrap;
        justify-content: flex-start;
        .engine-item{
            width: 460px;
            display: flex;
            flex-direction: column;
            flex-wrap: wrap;
            border: 1px solid #CFD0D3;
            border-radius: 4px;
            overflow: hidden;
            margin-right: 12px;
            margin-bottom: 12px;
            .engine-title{
                display: flex;
                flex-wrap: wrap;
                background: #F7F7F8;
                padding: 16px;
                .title{
                    font-family: PingFangSC-Semibold;
                    font-size: 14px;
                    color: #0F1222;
                    font-weight: 600;
                    width: calc(100% - 180px);
                    flex: 1;
                    display: flex;
                    .span-name{
                        overflow: hidden;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                    }
                }
                .btn{
                    cursor: pointer;
                    font-family: PingFangSC-Regular;
                    font-size: 14px;
                    color: #5384FF;
                    letter-spacing: 0;
                    font-weight: 400;
                    margin-left: auto;
                    flex-shrink: 0;
                    padding-left: 20px;
                }
            }
            .settings-btn{
                cursor: pointer;
                font-family: PingFangSC-Regular;
                font-size: 14px;
                color: #5384FF;
                letter-spacing: 0;
                font-weight: 400;
                margin-left: auto;
                flex-shrink: 0;
                padding-left: 20px;
                display: none;
            }
            .settings{
                .settings-btn{
                    display: block;
                }
            }
            .ranks-list{
                height: calc(100% - 52px);
                overflow: auto;
                padding: 0 16px;
                .ranks-item{
                    display: flex;
                    align-items: center;
                    padding: 16px 0;
                    font-family: PingFangSC-Regular;
                    &:not(:last-child){
                        border-bottom: 1px rgba(15,18,34,0.06) solid;
                    }
                    .ranks-left{
                        overflow: hidden;
                        flex: 1;
                        .ranks-title{
                            font-size: 14px;
                            color: #0F1222;
                            line-height: 22px;
                            font-weight: 400;
                        }
                        .ranks-subtitle{
                            font-size: 14px;
                            color: #93949B;
                            line-height: 22px;
                            font-weight: 400;
                            overflow: hidden;
                            // white-space: nowrap;
                            // text-overflow: ellipsis;
                        }
                    }
                    .ranks-right{
                        margin-left: auto;
                        padding-left: 16px;
                        flex-shrink: 0;
                        .fes-input,
                        .fes-select{
                            width: 128px;
                        }
                    }
                }
            }
        }
    }
    .we-warning-bar {
        color: #F75F56;
        padding-left: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
        white-space: nowrap;
    }
}
</style>
