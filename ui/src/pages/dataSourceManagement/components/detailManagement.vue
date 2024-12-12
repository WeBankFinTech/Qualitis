<template>
    <div class="preview">
        <div class="title">基础信息</div>
        <div class="out-border">
            <FForm ref="baseRef" class="index-detail-form" labelWidth="70px" labelPosition="left" :model="data">
                <FFormItem :label="$t('dataSourceManagement.dataSourceType')" prop="dataSourceType">{{data.dataSourceType}}</FFormItem>
                <FFormItem :label="$t('dataSourceManagement.dataSourceName')" prop="dataSourceName">{{data.dataSourceName}}</FFormItem>
                <FFormItem :label="$t('dataSourceManagement.dataSourceDescription')" prop="dataSourceDescription">{{data.dataSourceDescription}}</FFormItem>
                <FFormItem :label="$t('dataSourceManagement.associatedSubSystem')" prop="subSystem">{{data.subSystem}}</FFormItem>
                <FFormItem :label="$t('dataSourceManagement.developDepartment')" prop="dev_department_name">{{departmentDetail(data.dev_department_name)}}</FFormItem>
                <FFormItem :label="$t('dataSourceManagement.maintainDepartment')" prop="ops_department_name">{{departmentDetail(data.ops_department_name)}}</FFormItem>
                <FFormItem :label="$t('dataSourceManagement.visibleRange')" prop="action_range">
                    <FEllipsis v-if="data.action_range?.length" :line="2">
                        {{actionRangeDetail(data.action_range)}}
                        <template #tooltip>
                            <div style="text-align:center">可见范围</div>
                            <div style="width:300px;word-wrap:break-word">
                                {{actionRangeDetail(data.action_range)}}
                            </div>
                        </template>
                    </FEllipsis>
                    <div v-else>--</div>
                </FFormItem>
                <FFormItem :label="$t('dataSourceManagement.dataSourceLabel')" prop="label">
                    <div class="project-tags">
                        <div v-for="(item, index) in data.labels" :key="index" class="tags-item">{{item}}</div>
                    </div>
                </FFormItem>
            </FForm>
        </div>
        <div class="title">连接配置</div>
        <div class="out-border">
            <FForm ref="connectRef" class="index-detail-form" labelWidth="70px" labelPosition="left" :model="data">
                <FFormItem :label="$t('dataSourceManagement.inputType')" prop="inputType">{{getInputTypeName(data.inputType)}}</FFormItem>
                <FFormItem :label="$t('dataSourceManagement.verifyType')" prop="verifyType">{{getVerifyTypeName(data.verifyType)}}</FFormItem>
            </FForm>
        </div>
        <div class="title">环境配置</div>
        <div v-if="data.inputType === 1">
            <div v-for="(env, index) in data.dataSourceEnvs" :key="env.id" class="out-border">
                <FForm
                    label-width="120px"
                    :model="data.dataSourceEnvs[index]"
                    class="env-form"
                    labelPosition="left"
                    :labelWidth="70"
                >
                    <div class="sub-title">
                        <div class="left">环境{{index + 1}}</div>
                    </div>
                    <FFormItem label="环境名称" prop="envName">{{data.dataSourceEnvs[index].envName}}</FFormItem>
                    <FFormItem label="环境描述" prop="envDesc">{{data.dataSourceEnvs[index].envDesc}}</FFormItem>
                    <div v-if="data.dataSourceEnvs[index].connectParams">
                        <FFormItem :label="$t('dataSourceManagement.host')" prop="host">{{data.dataSourceEnvs[index].connectParams.host}}</FFormItem>
                        <FFormItem :label="$t('dataSourceManagement.connectPort')" prop="port">{{data.dataSourceEnvs[index].connectParams.port}}</FFormItem>
                        <FFormItem v-if="Number(data.dataSourceTypeId) === 1 || Number(data.dataSourceTypeId) === 5 "
                                   :label="$t('dataSourceManagement.connectParams')" prop="connectParams">
                            {{data.dataSourceEnvs[index].connectParams.connectParam}}
                        </FFormItem>
                        <div v-if="data.verifyType === 2">
                            <FFormItem :label="$t('dataSourceManagement.authType')" prop="authType">
                                {{getAuthTypeName(data.dataSourceEnvs[index].connectParams.authType)}}
                            </FFormItem>
                            <div v-if="data.dataSourceEnvs[index].connectParams.authType === 'accountPwd'">
                                <FFormItem :label="$t('dataSourceManagement.username')" prop="username">{{data.dataSourceEnvs[index].connectParams.username}}</FFormItem>
                                <FFormItem :label="$t('dataSourceManagement.password')" prop="password">********</FFormItem>
                            </div>
                            <div v-if="data.dataSourceEnvs[index].connectParams.authType === 'dpm'">
                                <FFormItem label="appId" prop="appId">{{data.dataSourceEnvs[index].connectParams.appId}}</FFormItem>
                                <FFormItem label="objectId" prop="objectId">{{data.dataSourceEnvs[index].connectParams.objectId}}</FFormItem>
                                <FFormItem label="mkPrivate" prop="mkPrivate">********</FFormItem>
                            </div>
                        </div>
                    </div>
                </FForm>
            </div>
        </div>
        <div v-if="data.inputType === 2" class="out-border">
            <!-- 只要是自动导入，必是DCN展示，DCN放在dataSourceEnvs-envName字段中-->
            <FForm ref="dcnRef" class="index-detail-form" labelWidth="70px" labelPosition="left" :model="data">
                <FFormItem label="DCN" prop="dcn">
                    {{getDcnString(data.dataSourceEnvs)}}
                </FFormItem>
            </FForm>
        </div>
        <div v-if="!(data.inputType === 1 && data.verifyType === 2)" class="title">登录认证</div>
        <div v-if="data.connectParams && !(data.inputType === 1 && data.verifyType === 2)" class="out-border">
            <FForm ref="loginRef" class="index-detail-form" labelWidth="70px" labelPosition="left" :model="data">
                <FFormItem v-if="data.inputType === data.verifyType"
                           :label="$t('dataSourceManagement.authType')" prop="authType">
                    {{getAuthTypeName(data.connectParams.authType)}}
                </FFormItem>
                <div v-if="data.connectParams.authType === 'accountPwd'">
                    <FFormItem :label="$t('dataSourceManagement.username')" prop="username">{{data.connectParams.username}}</FFormItem>
                    <FFormItem :label="$t('dataSourceManagement.password')" prop="password">********</FFormItem>
                </div>
                <div v-if="data.connectParams.authType === 'dpm'">
                    <FFormItem label="appId" prop="appId">{{data.connectParams.appId}}</FFormItem>
                    <FFormItem label="objectId" prop="objectId">{{data.connectParams.objectId}}</FFormItem>
                    <FFormItem label="mkPrivate" prop="mkPrivate">********</FFormItem>
                </div>
            </FForm>
        </div>
    </div>
</template>
<script setup>
import { defineProps, onMounted } from 'vue';
import { useI18n } from '@fesjs/fes';
import { actionRangeDetail, departmentDetail } from '@/common/utils';


const { t: $t } = useI18n();
const props = defineProps({
    // 详情展示、编辑时的当前数据库
    data: {
        type: Object,
        default: {},
    },
});
const getInputTypeName = (value) => {
    if (Number(value) === 1) return $t('dataSourceManagement.manualEntry');
    if (Number(value) === 2) return '自动导入';
};
const getVerifyTypeName = (value) => {
    if (Number(value) === 1) return $t('dataSourceManagement.sharedLogin');
    if (Number(value) === 2) return $t('dataSourceManagement.unsharedLogin');
};
const getAuthTypeName = (value) => {
    if (value === 'accountPwd') return $t('dataSourceManagement.accountPassword');
    // if (value === 'dpm') return $t('dataSourceManagement.passManage');
};
const getDcnString = (objArray) => {
    const dcnArray = objArray.map(({ envName }) => envName);
    return dcnArray.join(',');
};
const detail = () => { console.log('详情', props.data); };
onMounted(() => {
    console.log('详情', props.data);
});
</script>
<style lang="less" scoped>
.preview{
    :deep(.fes-form .fes-form-item) {
        margin-bottom: 8px;
    }
    .title{
    font-weight: 550;
    color: #0f1222;
    font-size: 14px;
    line-height: 22px;
    margin-bottom: 16px;
    }
    .out-border{
        border: 1px solid #cfd0d3;
        border-radius: 4px;
        margin-bottom:16px;
        padding: 16px 16px 0px 16px
    }
    .sub-title{
        font-weight: 550;
        font-size: 14px;
        margin-bottom: 16px;
        .left{
            font-weight: 400;
            display: inline;
            color: #0f1222;
        }
    }
}
</style>
