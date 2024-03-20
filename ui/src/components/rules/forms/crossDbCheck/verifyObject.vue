<template>
    <div class="rule-detail-form" :class="{ edit: ruleData.currentProject.editMode !== 'display' }">
        <h6 class="wd-body-title">校验对象</h6>
        <FForm
            ref="verifyObjectFormRef"
            :model="verifyObjectData"
            :rules="verifyObjectRules"
            :labelWidth="96"
            :labelPosition="ruleData.currentProject.editMode !== 'display' ? 'right' : 'left'"
        >
            <!-- 任务执行集群 -->
            <FFormItem :label="$t('common.taskExecCluster')" prop="cluster_name">
                <FSelect
                    v-model="verifyObjectData.cluster_name"
                    filterable
                    class="form-edit-input"
                    @change="onClusterChange"
                >
                    <FOption
                        v-for="(item, index) in clusterList"
                        :key="index"
                        :disabled="item.disabled"
                        :value="item.cluster_name"
                        :label="item.cluster_name"
                    ></FOption>
                </FSelect>
                <div class="form-preview-label">{{verifyObjectData.cluster_name}}</div>
            </FFormItem>
            <!-- 来源表，目标表设置 -->
            <div class="config-wrapper">
                <div class="config">
                    <verifyObject
                        :ref="(e) => ele.sourceVerifyObjectRef = e"
                        key="source"
                        type="source"
                        :isNormalMode="false"
                    />
                </div>
                <div class="config">
                    <verifyObject
                        :ref="(e) => ele.targetVerifyObjectRef = e"
                        key="target"
                        type="target"
                        :isNormalMode="false"
                    />
                </div>
            </div>
            <!-- 白名单 -->
            <FFormItem :label="$t('common.whiteList')" prop="white_list">
                <div class="rule-form-table">
                    <table v-if="verifyObjectData.white_list?.length > 0" class="form-table">
                        <thead class="table-header">
                            <th>{{$t('common.sourceName')}}</th>
                            <th>{{$t('common.targetName')}}</th>
                            <th
                                v-if="ruleData.currentProject.editMode !== 'display'"
                            >
                                {{$t('common.operate')}}
                            </th>
                        </thead>
                        <tbody>
                            <tr
                                v-for="(whiteN,index) in verifyObjectData.white_list"
                                :key="whiteN.id"
                                class="table-row"
                            >
                                <td>
                                    <FSelect
                                        v-model="whiteN.sourceName"
                                        class="form-edit-input"
                                        filterable
                                        valueField="table_name"
                                        labelField="table_name"
                                        :options="verifyObjectData.originTableList"
                                    ></FSelect>
                                </td>
                                <td>
                                    <FSelect
                                        v-model="whiteN.targetName"
                                        class="form-edit-input"
                                        filterable
                                        valueField="table_name"
                                        labelField="table_name"
                                        :options="verifyObjectData.targetTableList"
                                    ></FSelect>
                                </td>
                                <td v-if="ruleData.currentProject.editMode !== 'display'">
                                    <a
                                        class="btn del"
                                        href="javascript:;"
                                        @click="delList(0, index)"
                                    >{{$t('common.delete')}}</a>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div
                        class="table-operation"
                        :class="{ empty: verifyObjectData.white_list?.length === 0 }"
                    >
                        <FButton class="add-btn" @click="addNameList('white')">
                            <PlusOutlined />
                            添加名单
                        </FButton>
                    </div>
                </div>
            </FFormItem>
            <!-- 黑名单 -->
            <FFormItem :label="$t('common.blacklist')" prop="black_list">
                <div class="rule-form-table">
                    <table v-if="verifyObjectData.black_list?.length > 0" class="form-table">
                        <thead class="table-header">
                            <th>{{$t('common.filterMethod')}}</th>
                            <th>{{$t('common.filterFields')}}</th>
                            <th
                                v-if="ruleData.currentProject.editMode !== 'display'"
                            >
                                {{$t('common.operate')}}
                            </th>
                        </thead>
                        <tbody>
                            <tr
                                v-for="(blackN,index) in verifyObjectData.black_list"
                                :key="blackN.id"
                                class="table-row"
                            >
                                <td>
                                    <FSelect
                                        v-model="blackN.filterWay"
                                        class="form-edit-input"
                                        filterable
                                        :options="filterList"
                                    ></FSelect>
                                </td>
                                <td>
                                    <FInput
                                        v-model="blackN.filterContent"
                                        :disabled="blackN.filterWay === 4"
                                        class="form-edit-input"
                                        placeholder="请输入"
                                    />
                                </td>
                                <td v-if="ruleData.currentProject.editMode !== 'display'">
                                    <a
                                        class="btn del"
                                        href="javascript:;"
                                        @click="delList(1, index)"
                                    >{{$t('common.delete')}}</a>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div
                        class="table-operation"
                        :class="{ empty: verifyObjectData.black_list?.length === 0 }"
                    >
                        <FButton class="add-btn" @click="addNameList('black')">
                            <PlusOutlined />
                            添加名单
                        </FButton>
                    </div>
                </div>
            </FFormItem>
        </FForm>
    </div>
</template>
<script setup>
import {
    ref, computed, inject, nextTick, watch,
} from 'vue';
import { useStore } from 'vuex';
import { ruleTypes } from '@/common/utils';
import { cloneDeep } from 'lodash-es';
import { useI18n, request as FRequest } from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import {
    ExclamationCircleOutlined, PlusOutlined,
} from '@fesjs/fes-design/es/icon';
import { FMessage } from '@fesjs/fes-design';
import {
    ruleTypeChangeEvent, dataSourceTypeList, getDataSourceType, buildColumnData,
} from '@/components/rules/utils';
import useDataSource from '@/components/rules/hook/useDataSource';
import { getDBList, getDBListByResourceID } from '@/components/rules/api';
import {
    getTables,
} from '@/components/rules/utils/datasource';
import { MAX_PAGE_SIZE } from '@/assets/js/const';
import verifyObject from '@/components/rules/VerifyObject';

const store = useStore();
const { t: $t } = useI18n();

const ruleData = computed(() => store.state.rule);

const sourceDatasource = computed(() => ruleData.value.currentRuleDetail.source || {});
const targetDatasource = computed(() => ruleData.value.currentRuleDetail.target || {});

// 为了与接口对应，datasource是一个数组
const verifyObjectData = ref({
    cluster_name: '',
    white_list: [],
    black_list: [],
});

const clusterList = inject('clusterList');
// 黑名单 筛选方式list
const filterList = ref([{
    label: '包含',
    value: 1,
}, {
    label: '以字符开头',
    value: 2,
}, {
    label: '以字符结尾',
    value: 3,
}, {
    label: '同名表',
    value: 4,
}, {
    label: '正则表达式',
    value: 5,
}]);

const getCommonInfoByTargetName = (targetName) => {
    const dataSourceId = targetName === 'source' ? sourceDatasource.value.linkis_datasource_id : targetDatasource.value.linkis_datasource_id;
    const proxyUser = targetName === 'source' ? sourceDatasource.value.proxy_user : targetDatasource.value.proxy_user;
    const dataSourceType = targetName === 'source' ? sourceDatasource.value.type : targetDatasource.value.type;
    return {
        dataSourceId,
        proxyUser,
        dataSourceType,
    };
};

const getTableList = async (targetName, dbName) => {
    try {
        if (!dbName) return [];
        console.log('getTableList dbName', dbName);
        const commonInfo = getCommonInfoByTargetName(targetName);
        const clusterName = verifyObjectData.value.cluster_name;
        const dataSourceId = commonInfo.dataSourceId;
        const proxyUser = commonInfo.proxyUser;
        const tables = await getTables({
            clusterName: verifyObjectData.value.cluster_name,
            proxyUser: commonInfo.proxyUser,
            dataSourceType: commonInfo.dataSourceType,
            dataSourceId: commonInfo.dataSourceId,
            dbName,
        });
        return tables;
    } catch (err) {
        console.warn(err);
    }
};


// 加载表名列表数据
const loadTable = async (flag, DbName) => {
    try {
        if (flag === 'source') {
            if (!DbName) {
                verifyObjectData.value.originTableList = [];
            } else {
                const originTableList = await getTableList('source', DbName);
                verifyObjectData.value.originTableList = originTableList || [];
            }
        } else if (flag === 'target') {
            if (!DbName) {
                verifyObjectData.value.targetTableList = [];
            } else {
                const targetTableList = await getTableList('target', DbName);
                verifyObjectData.value.targetTableList = targetTableList || [];
            }
        }
        store.commit('rule/updateCurrentRuleDetail', cloneDeep(verifyObjectData.value)); // 校验规则需要用到这两个列表，故保存在store传递
    } catch (err) {
        console.warn(err);
    }
};

watch(() => sourceDatasource.value?.db_name, (cur) => {
    loadTable('source', cur);
    verifyObjectData.value.white_list = [];
});
watch(() => targetDatasource.value?.db_name, (cur) => {
    loadTable('target', cur);
    verifyObjectData.value.white_list = [];
});

// 白名单、黑名单处理
function addNameList(type) {
    // console.log(verifyObjectData.value, 'datasource');
    console.log('sourceDatasource', sourceDatasource.value);
    console.log('targetDatasource', targetDatasource.value);
    if (type === 'white') {
        if (!sourceDatasource.value.db_name) {
            return FMessage.warn(`${$t('common.pleaseSelect')}${$t('common.sourceDatabase')}`);
        }
        if (!targetDatasource.value.db_name) {
            return FMessage.warn(`${$t('common.pleaseSelect')}${$t('common.targetDatabase')}`);
        }
        // 添加白名单
        verifyObjectData.value.white_list.push({
            sourceName: '', targetName: '',
        });
    } else if (type === 'black') {
        // 添加黑名单
        verifyObjectData.value.black_list.push({
            filterWay: '', filterContent: '',
        });
    }
}

function delList(target, index) { // 删除名单
    if (!target) {
        verifyObjectData.value.white_list.splice(index, 1);
    } else {
        verifyObjectData.value.black_list.splice(index, 1);
    }
}

// 表单规则
const verifyObjectRules = ref({
    cluster_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
});

// 更新cluster的时候需要重置数据
const onClusterChange = () => {
    eventbus.emit('CLUSTER_NAME_CHANGE', verifyObjectData.value.cluster_name);
};

const verifyObjectFormRef = ref(null);
// 组件ref
const ele = ref({
    sourceVerifyObjectRef: null,
    targetVerifyObjectRef: null,
});
const valid = async () => {
    try {
        const result = await Promise.all([
            verifyObjectFormRef.value.validate(),
            ele.value.sourceVerifyObjectRef.valid(),
            ele.value.targetVerifyObjectRef.valid(),
        ]);
        if (result.includes(false)) {
            console.log('datasrouce表单验证失败: ', result);
            return false;
        }
        console.log('datasrouce表单验证成功: ', result);
        store.commit('rule/updateCurrentRuleDetail', cloneDeep(verifyObjectData.value));
        return true;
    } catch (error) {
        console.log('datasrouce表单验证失败: ', error);
        return false;
    }
};
// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
<style lang='less' scoped>
.add-btn{
    width: 315px;
}
.config-wrapper {
    border-radius: 4px;
    padding-left: 112px;
    display: grid;
    gap: 0 24px;
    grid-template-columns: 498px 498px;
    margin-bottom: 22px;
    .config {
        width: 498px;
        background: #ffffff;
        background: rgba(15, 18, 34, 0.03);
        border-radius: 4px;
        padding: 24px;
        :deep(.wd-body-title) {
            display: none;
        }
        :deep(.rule-id) {
            display: none;
        }
        :deep(.rule-type) {
            display: none;
        }
        :deep(.upstream) {
            display: none;
        }
    }
}
</style>
