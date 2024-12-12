<template>
    <div>
        <FDrawer
            v-model:show="drawerShow"
            contentClass="add-or-edit-management"
            :title="curMode === 'add' ? $t('dataSourceManagement.addDataSource') : curMode === 'edit' ?
                $t('dataSourceManagement.editDataSource') : $t('dataSourceManagement.dataSourceDetail')"
            displayDirective="if"
            :footer="true"
            width="50%"
            @cancel="closePanel"
            @ok="confirmAddDataSource"
        >
            <div v-if="curMode === 'preview' || curMode === 'versionPreview'">
                <DetailManagement :data="curDataSourceDetail"></DetailManagement>
            </div>
            <div v-else>
                <FForm ref="addDataSourceFormRef" label-width="120px" :model="addDataSourceForm" labelPosition="right" :labelWidth="82" :rules="addRuleValidate">
                    <div class="title">基础信息</div>
                    <div>
                        <!-- 数据源类型 -->
                        <FFormItem :label="$t('dataSourceManagement.dataSourceType')" prop="dataSourceTypeId">
                            <FSelect v-model="addDataSourceForm.dataSourceTypeId" clearable filterable @change="handleSelectedDataSourceType">
                                <FOption v-for="item in dataSourceAddTypeList" :key="item.id" :label="item.name" :value="item.id" />
                            </FSelect>
                        </FFormItem>
                        <!-- 数据源名称 -->
                        <FFormItem :label="$t('dataSourceManagement.dataSourceName')" prop="dataSourceName">
                            <FInput v-model="addDataSourceForm.dataSourceName" clearable :placeholder="$t('common.pleaseEnter')" />
                        </FFormItem>
                        <!-- 数据源描述 -->
                        <FFormItem :label="$t('dataSourceManagement.dataSourceDescription')" prop="dataSourceDesc">
                            <FInput v-model="addDataSourceForm.dataSourceDesc" clearable :placeholder="$t('common.pleaseEnter')" />
                        </FFormItem>
                        <!-- 关联子系统 -->
                        <FFormItem :label="$t('dataSourceManagement.associatedSubSystem')" prop="subSystem">
                            <FInput
                                v-model="addDataSourceForm.subSystem"
                                clearable
                                :placehodler="$t('common.pleaseEnter')"
                            />
                        </FFormItem>
                        <!-- 开发科室 -->
                        <FFormItem :label="$t('dataSourceManagement.developDepartment')" prop="dev_department_name">
                            <FSelectCascader
                                v-model="addDataSourceForm.dev_department_name"
                                :data="devDivisions"
                                :loadData="loadDevDivisions"
                                clearable
                                remote
                                emitPath
                                showPath
                                checkStrictly="child"
                                @change="handleDevId"
                            ></FSelectCascader>
                        </FFormItem>
                        <!-- 运维科室 -->
                        <FFormItem :label="$t('dataSourceManagement.maintainDepartment')" prop="ops_department_name">
                            <FSelectCascader
                                v-model="addDataSourceForm.ops_department_name"
                                :data="opsDivisions"
                                :loadData="loadOpsDivisions"
                                clearable
                                remote
                                emitPath
                                showPath
                                checkStrictly="child"
                                @change="handleOpsId"
                            ></FSelectCascader>
                        </FFormItem>
                        <!-- 可见范围 -->
                        <FFormItem :label="$t('dataSourceManagement.visibleRange')" prop="action_range">
                            <FSelectCascader
                                v-model="addDataSourceForm.action_range"
                                :data="visDivisions"
                                :loadData="loadVisDivisions"
                                clearable
                                remote
                                emitPath
                                showPath
                                multiple
                                checkStrictly="all"
                                collapseTags
                                :collapseTagsLimit="3"
                                @change="visSelectChange" />
                        </FFormItem>
                        <FFormItem :label="$t('dataSourceManagement.dataSourceLabel')" prop="label">
                            <TagsPanel v-model:tags="addDataSourceForm.labels" />
                        </FFormItem>
                    </div>
                    <div class="title">{{$t('dataSourceManagement.connectConf')}}</div>
                    <!-- 录入方式 -->
                    <FFormItem :label="$t('dataSourceManagement.inputType')" prop="inputType">
                        <FRadioGroup v-model="addDataSourceForm.inputType" :cancelable="false" @change="onInputTypeChange">
                            <FRadio :value="1">{{$t('dataSourceManagement.manualEntry')}}</FRadio>
                            <!-- <FRadio :value="2" :disabled="!(addDataSourceForm.dataSourceTypeId === '5')">自动导入</FRadio> -->
                            <!-- <FRadio :value="2">自动导入</FRadio> -->
                        </FRadioGroup>
                    </FFormItem>
                    <!-- 认证方式 -->
                    <FFormItem :label="$t('dataSourceManagement.verifyType')" prop="verifyType">
                        <FRadioGroup v-model="addDataSourceForm.verifyType" :cancelable="false">
                            <FRadio :value="1">{{$t('dataSourceManagement.sharedLogin')}}</FRadio>
                            <FRadio :value="2" :disabled="addDataSourceForm.inputType === 2">{{$t('dataSourceManagement.unsharedLogin')}}</FRadio>
                        </FRadioGroup>
                    </FFormItem>
                    <div class="title">{{$t('dataSourceManagement.basicInfo')}}</div>
                    <div v-if="addDataSourceForm.inputType === 1">
                        <div v-for="(env, index) in envList" :key="env.id" class="env-item">
                            <FForm
                                :ref="el => { if (el) envListRef[index] = el; }"
                                label-width="120px"
                                :model="envList[index]"
                                class="env-form"
                                labelPosition="right"
                                :labelWidth="80"
                                :rules="envRuleValidate"
                            >
                                <div class="sub-title">
                                    <div class="left">{{$t('dataSourceManagement.env')}}{{index + 1}}</div>
                                    <div v-if="envList.length > 1" class="right">
                                        <div @click="deleteEnv(envList[index])">{{$t('common.delete')}}</div>
                                        <!-- 使用FTooltip有警告  -->
                                        <!-- <FTooltip
                                            mode="confirm"
                                            :confirmOption="{ okText: '确认删除' }"
                                            placement="left"
                                            @ok="deleteEnv(envList[index])"
                                        >
                                            <div>{{$t('common.delete')}}</div>
                                            <template #title>
                                                <div style="width: 200px">确认删除此环境么？</div>
                                            </template>
                                        </FTooltip> -->
                                    </div>
                                </div>
                                <!-- 环境名称 -->
                                <FFormItem :label="$t('dataSourceManagement.envName')" prop="envName">
                                    <FInput v-model="envList[index].envName" clearable :placeholder="$t('common.pleaseEnter')" />
                                </FFormItem>
                                <!-- 环境描述 -->
                                <FFormItem :label="$t('dataSourceManagement.envDesc')" prop="envDesc">
                                    <FInput v-model="envList[index].envDesc" clearable :placeholder="$t('common.pleaseEnter')" />
                                </FFormItem>
                                <!-- Host -->
                                <FFormItem :label="$t('dataSourceManagement.host')" prop="host">
                                    <FInput v-model="envList[index].host" clearable :placeholder="$t('common.pleaseEnter')" />
                                </FFormItem>
                                <!-- TODO:连接端口 -->
                                <FFormItem :label="$t('dataSourceManagement.connectPort')" prop="port">
                                    <FInput v-model="envList[index].port" clearable :placeholder="$t('common.pleaseEnter')" />
                                </FFormItem>
                                <!-- 连接参数 -->
                                <FFormItem v-if="addDataSourceForm.dataSourceTypeId === '1' || addDataSourceForm.dataSourceTypeId === '5' " :label="$t('dataSourceManagement.connectParams')" prop="connectParam">
                                    <FInput v-model="envList[index].connectParam" clearable placeholder="输入JSON格式:{'params': 'value'}" />
                                </FFormItem>
                                <template v-if="addDataSourceForm.verifyType === 2">
                                    <!-- 认证方式 -->
                                    <FFormItem :label="$t('dataSourceManagement.authType')" prop="authType">
                                        <FSelect v-model="envList[index].authType" clearable filterable :options="curAuthTypeList" @change="resetAuthData(index,'envList')"></FSelect>
                                    </FFormItem>
                                    <div v-if="envList[index].authType === 'accountPwd'">
                                        <!-- 用户名 -->
                                        <FFormItem :label="$t('dataSourceManagement.username')" prop="username">
                                            <FInput v-model="envList[index].username" clearable :placeholder="$t('common.pleaseEnter')" />
                                        </FFormItem>
                                        <!-- 密码 -->
                                        <FFormItem :label="$t('dataSourceManagement.password')" prop="password">
                                            <FInput v-model="envList[index].password" clearable type="password" showPassword :placeholder="$t('common.pleaseEnter')" />
                                        </FFormItem>
                                    </div>
                                    <div v-if="envList[index].authType === 'dpm'">
                                        <!-- appId -->
                                        <FFormItem label="appId" prop="appId">
                                            <FInput v-model="envList[index].appId" clearable placeholder="业务的appId" />
                                        </FFormItem>
                                        <!-- objectId -->
                                        <FFormItem label="objectId" prop="objectId">
                                            <FInput v-model="envList[index].objectId" clearable placeholder="业务的账号id" />
                                        </FFormItem>
                                        <!-- 账户私钥 -->
                                        <FFormItem :label="$t('dataSourceManagement.mkPrivate')" prop="mkPrivate">
                                            <FInput v-model="envList[index].mkPrivate" clearable :placeholder="$t('common.pleaseEnter')" />
                                        </FFormItem>
                                    </div>
                                </template>
                            </FForm>
                        </div>
                        <div class="add-env">
                            <FButton type="link" class="link-button" @click="addEnv">
                                <template #icon><PlusCircleOutlined /></template>{{$t('dataSourceManagement.addEnv')}}
                            </FButton>
                        </div>
                    </div>
                    <template v-if="!(addDataSourceForm.inputType === 1 && addDataSourceForm.verifyType === 2)">
                        <div class="title">{{$t('dataSourceManagement.loginAuth')}}</div>
                        <div>
                            <!-- 认证方式 手动非共享、自动共享隐藏-->
                            <FFormItem v-if="addDataSourceForm.verifyType === 1" :label="$t('dataSourceManagement.authType')" prop="authType">
                                <FSelect v-model="addDataSourceForm.authType" clearable filterable :options="curAuthTypeList" @change="resetAuthData(0,'addDataSourceForm')"></FSelect>
                            </FFormItem>
                            <div v-if="addDataSourceForm.authType === 'accountPwd'">
                                <!-- 用户名 -->
                                <FFormItem :label="$t('dataSourceManagement.username')" prop="username">
                                    <FInput v-model="addDataSourceForm.username" clearable :placeholder="$t('common.pleaseEnter')" />
                                </FFormItem>
                                <!-- 密码 -->
                                <FFormItem :label="$t('dataSourceManagement.password')" prop="password">
                                    <FInput v-model="addDataSourceForm.password" clearable type="password" showPassword :placeholder="$t('common.pleaseEnter')" />
                                </FFormItem>
                            </div>
                            <div v-if="addDataSourceForm.authType === 'dpm'">
                                <!-- appId -->
                                <FFormItem label="appId" prop="appId">
                                    <FInput v-model="addDataSourceForm.appId" clearable placeholder="业务的appId" />
                                </FFormItem>
                                <!-- objectId -->
                                <FFormItem label="objectId" prop="objectId">
                                    <FInput v-model="addDataSourceForm.objectId" clearable placeholder="业务的账号id" />
                                </FFormItem>
                                <!-- 账户私钥 -->
                                <FFormItem :label="$t('dataSourceManagement.mkPrivate')" prop="mkPrivate">
                                    <FInput v-model="addDataSourceForm.mkPrivate" clearable :placeholder="$t('common.pleaseEnter')" />
                                </FFormItem>
                            </div>
                        </div>
                    </template>
                </FForm>
            </div>
            <!-- 页脚动态菜单 versionPreview 无按钮-->
            <template #footer>
                <div v-if="curMode === 'preview'" class="modalFooter">
                    <FButton type="info" @click="editSource">{{$t('common.edit')}}</FButton>
                </div>
                <div v-if="curMode === 'add' || curMode === 'edit'" class="modalFooter">
                    <FButton type="primary" @click="confirmAddDataSource">{{$t('common.ok')}}</FButton>
                    <FButton @click="closePanel">{{$t('common.cancel')}}</FButton>
                </div>
            </template>
        </FDrawer>
    </div>
</template>
<script setup>
import {
    ref, computed, defineProps, defineEmits, onUpdated, inject, watch, nextTick, onMounted,
} from 'vue';
import {
    FMessage,
} from '@fesjs/fes-design';
import {
    useI18n, request as FRequest, useModel,
} from '@fesjs/fes';
import TagsPanel from '@/components/TagsPanel.vue';
import { PlusCircleOutlined } from '@fesjs/fes-design/icon';
import { cloneDeep, pick } from 'lodash-es';
import { COMMON_REG } from '@/assets/js/const';
import useDepartment from '@/hooks/useDepartment';
import useDivisions from '@/hooks/useDivisions';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { getDataFormTreeSelect } from '@/common/utils';
import {
    fetchDataSourceType, fetchAddDataSource, fetchSubSystemInfo, fetchEditDataSource, fetchUpdateDataSourceVersion,
} from '../api';
import DetailManagement from './detailManagement.vue';


const { t: $t } = useI18n();
const props = defineProps({
    // 控制组件的显示隐藏
    show: {
        type: Boolean,
        required: true,
        default: false,
    },
    // 控制组件模式 add-新增 edit-编辑 preview-预览
    // add空表单、edit引入已有数据、preview纯预览
    mode: {
        type: String,
        default: 'add',
    },
    // edit\preview模式下请求数据源详情的id
    sid: {
        type: Number,
        default: 0,
    },
    // 查询框上的代理用户信息
    queryProxyUser: {
        type: String,
        default: '',
    },
    // 子系统下拉框列表
    subSystemList: {
        type: Array,
        default: [],
    },
    // 详情展示、编辑时的当前数据库
    curDataSourceDetail: {
        type: Object,
        default: {},
    },
});
const emit = defineEmits(['update:show', 'update:mode', 'updateDataSource']);
const envRuleValidate = ref({
    envName: [
        { required: true, message: $t('common.notEmpty') },
    ],
    host: [
        { required: true, message: $t('common.notEmpty') },
    ],
    port: [
        { required: true, message: $t('common.notEmpty') },
    ],
    // connectParam: [
    //     { required: true, message: $t('common.notEmpty') },
    // ],
    authType: [
        { required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty') },
    ],
    username: [
        { required: true, message: $t('common.notEmpty') },
    ],
    password: [
        { required: true, message: $t('common.notEmpty') },
    ],
    appId: [
        { required: true, message: $t('common.notEmpty') },
    ],
    objectId: [
        { required: true, message: $t('common.notEmpty') },
    ],
    mkPrivate: [
        { required: true, message: $t('common.notEmpty') },
    ],
});
const addRuleValidate = ref({ // 表单验证规则
    dataSourceTypeId: [
        { required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty') },
    ],
    dataSourceName: [
        { required: true, message: $t('common.notEmpty') },
        {
            pattern: COMMON_REG.EN_NAME,
            message: $t('myProject.projectEnNameRegTips'),
        },
    ],
    subSystem: [
        { required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty') },
    ],
    dev_department_name: [
        {
            required: true, message: $t('common.notEmpty'), type: 'array', trigger: 'change',
        },
    ],
    ops_department_name: [
        {
            required: true, message: $t('common.notEmpty'), type: 'array', trigger: 'change',
        },
    ],
    action_range: [
        {
            required: false, message: $t('common.notEmpty'), type: 'array', trigger: 'change',
        },
    ],
    inputType: [
        { required: true, message: $t('common.notEmpty'), type: 'number' },
    ],
    verifyType: [
        {
            required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty'), type: 'number',
        },
    ],
    dcn: [
        {
            required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty'), type: 'array',
        },
    ],
    authType: [
        { required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty') },
    ],
    username: [
        { required: true, message: $t('common.notEmpty') },
    ],
    password: [
        { required: true, message: $t('common.notEmpty') },
    ],
    appId: [
        { required: true, message: $t('common.notEmpty') },
    ],
    objectId: [
        { required: true, message: $t('common.notEmpty') },
    ],
    mkPrivate: [
        { required: true, message: $t('common.notEmpty') },
    ],
});

const addDataSourceFormRef = ref();
// 得是空数组，不然子组件渲染有问题
const envList = ref([]);
const envListRef = ref([]);

// 数据源类型列表
const dataSourceAddTypeList = ref([]);
const initaddDataSourceForm = () => ({
    dataSourceName: '',
    dataSourceDesc: '',
    labels: [],
    connectParams: {},
    createSystem: 'Qualitis',
    subSystem: '',
    // 可选范围
    visibility_department_list: [],
    action_range: [],
    // 开发科室
    dev_department_name: [],
    dev_department_id: '',
    // 运维科室
    ops_department_id: '',
    ops_department_name: [],
    dataSourceTypeId: '',
    inputType: 1,
    verifyType: 1,
});
const addDataSourceForm = ref(initaddDataSourceForm);
const drawerShow = computed({
    get: () => props.show,
    set: (value) => {
        emit('update:show', value);
    },
});
const curMode = computed({
    get() {
        if (props.mode === 'add') {
            addDataSourceForm.value = initaddDataSourceForm();
            envList.value = [];
        }
        return props.mode;
    },
    set(value) {
        emit('update:mode', value);
    },
});
const tdsqlAuthTypeList = [
    {
        value: 'accountPwd',
        label: '账户密码',
    },
];
const commonAuthTypeList = [
    {
        value: 'accountPwd',
        label: '账户密码',
    },
];
const {
    divisions: devDivisions,
    loadDivisions: loadDevDivisions,
    curSubDepartData: devCurSubDepartData,
} = usePermissionDivisions();
const {
    divisions: opsDivisions,
    loadDivisions: loadOpsDivisions,
    curSubDepartData: opsCurSubDepartData,
} = usePermissionDivisions();
const {
    divisions: visDivisions,
    loadDivisions: loadVisDivisions,
    curSubDepartData: visCurSubDepartData,
    initDepartment,
} = useDivisions();
const {
    selectDevDate,
    selectOpsDate,
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);


const curAuthTypeList = ref(commonAuthTypeList);
const getDataSourceAddTypeList = async () => {
    addDataSourceForm.value.dataSourceTypeId = '';
    dataSourceAddTypeList.value = [];
    await nextTick();
    try {
        const url = '/api/v1/projector/meta_data/data_source/types/all';
        const { res } = await fetchDataSourceType(url);
        dataSourceAddTypeList.value = res && res.typeList ? res.typeList : [];
    } catch (error) {
        console.log('error: ', error);
    }
};
const getInitEnvItem = () => {
    const item = {
        timestamp: Date.now(),
        envName: '',
        envDesc: '',
        host: '',
        port: '',
        connectParam: '',
        authType: '',
        username: '',
        password: '',
        appId: '',
        objectId: '',
        mkPrivate: '',
    };
    return item;
};

const onInputTypeChange = () => {
    if (addDataSourceForm.value.inputType === 2) {
        addDataSourceForm.value.verifyType = 1;
    }
    addDataSourceForm.value.dcn = [];
};
const handleSelectedDataSourceType = () => {
    // 数据源类型从tdsql换成其他后,无密码管家一项
    if (Number(addDataSourceForm.value.dataSourceTypeId) !== 5) {
        curAuthTypeList.value = commonAuthTypeList;
        addDataSourceForm.value.authType = 'accountPwd';
        envList.value.map((item) => {
            item.authType = 'accountPwd';
            return item;
        });
    } else {
        curAuthTypeList.value = tdsqlAuthTypeList;
    }
};
const requestParams = ref({});
const computeAuthData = (data) => {
    let authParams = {
        authType: data.authType,
        username: '',
        password: '',
        appId: '',
        objectId: '',
        mkPrivate: '',
    };
    if (data.authType === 'accountPwd') authParams = Object.assign(authParams, pick(data, ['username', 'password']));
    else if (data.authType === 'dpm') authParams = Object.assign(authParams, pick(data, ['appId', 'objectId', 'mkPrivate']));
    return authParams;
};
let curDataSourceEnvs = [];
const handleRequestParams = () => {
    let params = {};
    let connectParams = {};
    curDataSourceEnvs = [];
    params = pick(addDataSourceForm.value, Object.keys(initaddDataSourceForm()));
    if (addDataSourceForm.value.inputType === 1) {
        envList.value.forEach((item) => {
            const curEnv = pick(item, ['envName', 'envDesc']);
            connectParams = pick(item, ['port', 'host', 'connectParam']);
            if (addDataSourceForm.value.verifyType === 2) {
                connectParams = Object.assign(connectParams, computeAuthData(item));
            } else if (addDataSourceForm.value.verifyType === 1) {
                connectParams = Object.assign(connectParams, computeAuthData(addDataSourceForm.value));
            }
            curEnv.connectParams = connectParams;
            if (curMode.value === 'edit') {
                curEnv.id = item.id || '';
            }
            curDataSourceEnvs.push(curEnv);
        });
    }
    params.dataSourceEnvs = curDataSourceEnvs;
    params.dcnSequence = addDataSourceForm.value.dcn;
    params.connectParams = {};
    return params;
};
const afterAddOrEditDataSource = async (envIdArray, dataSourceId, type) => {
    console.log('dataSourceId', dataSourceId);
    if (!dataSourceId) return;
    let params = {};
    let url = `/api/v1/projector/meta_data/data_source/param/modify?&dataSourceId=${dataSourceId}&`;
    url += `proxyUser=${props.queryProxyUser || ''}`;
    console.log('afterAddOrEditDataSource-url', url);
    const dataSourceEnvs = cloneDeep(curDataSourceEnvs);
    params = {
        comment: type, // Init or Update
        envIdArray,
        inputType: addDataSourceForm.value.inputType,
        verifyType: addDataSourceForm.value.verifyType,
        dcnSequence: addDataSourceForm.value.dcn,
        dataSourceEnvs,
    };
    console.log('afterAddOrEditDataSource-params', params);
    try {
        await fetchUpdateDataSourceVersion(url, params);
    } catch (error) {
        console.error(error);
    }
};
const confirmAddDataSource = async () => {
    try {
        const baseFormValidResult = await addDataSourceFormRef.value.validate();
        const envFormValidResult = await Promise.all(envListRef.value.map(item => item.validate()));
        const params = handleRequestParams();
        // params.action_range = params.action_range.map(item => item.join('/'));
        params.visibility_department_list = selectVisDate.value;
        params.dev_department_name = params.dev_department_name.join('/');
        params.ops_department_name = params.ops_department_name.join('/');
        params.dev_department_id = selectDevDate.value;
        params.ops_department_id = selectOpsDate.value;
        let url = '';
        if (curMode.value === 'add') {
            url = `/api/v1/projector/meta_data/data_source/create?proxyUser=${props.queryProxyUser}`;
            console.log('confirmAddDataSource-url', url);
            console.log('confirmAddDataSource-params', params);
            const { res } = await fetchAddDataSource(url, params);
            await afterAddOrEditDataSource(res.envIdArray, res.insertId, 'Init');
            FMessage.success($t('toastSuccess.addSuccess'));
        } else if (curMode.value === 'edit') {
            url = `/api/v1/projector/meta_data/data_source/modify?proxyUser=${props.queryProxyUser}`;
            url += `&dataSourceId=${props.sid}`;
            console.log('');
            console.log('confirmEditDataSource-url', url);
            console.log('confirmEditDataSource-params', params);
            const { res } = await fetchEditDataSource(url, params);
            await afterAddOrEditDataSource(res.envIdArray, res.updateId, 'Update');
            FMessage.success($t('toastSuccess.editSuccess'));
        }
        emit('updateDataSource');
        envList.value = [];
        addDataSourceForm.value = initaddDataSourceForm();
        emit('update:show', false);
    } catch (error) {
        console.error(error);
    }
};
const closePanel = () => {
    console.log('关闭数据源新增或编辑抽屉~');
    envList.value = [];
    addDataSourceForm.value = initaddDataSourceForm();
    emit('update:show', false);
};

const editSource = async () => {
    console.log('curDataSourceDetail1111', props.curDataSourceDetail);
    if (!props.curDataSourceDetail.is_editable) {
        return FMessage.error('没有编辑权限');
    }
    curMode.value = 'edit';
    addDataSourceForm.value = pick(props.curDataSourceDetail, ['dataSourceName', 'subSystem', 'action_range', 'dev_department_name', 'dev_department_id', 'ops_department_id', 'ops_department_name', 'visibility_department_list', 'inputType', 'verifyType', 'labels']);
    addDataSourceForm.value.createSystem = 'Qualitis';
    addDataSourceForm.value.dataSourceDesc = props.curDataSourceDetail.dataSourceDescription;
    addDataSourceForm.value.dataSourceTypeId = String(props.curDataSourceDetail.dataSourceTypeId);
    addDataSourceForm.value.action_range = addDataSourceForm.value.visibility_department_list?.map(item => item.name.split('/')) || [];
    // 初始化多DCN相关数据
    addDataSourceForm.value.dcn = props.curDataSourceDetail.dcnSequence;
    console.log(addDataSourceForm.value);
    initDepartment(addDataSourceForm.value.visibility_department_list);
    handleSelectedDataSourceType();
    props.curDataSourceDetail.dataSourceEnvs.forEach((item, index) => {
        envList.value[index] = pick(item, ['envName', 'envDesc']);
        envList.value[index].id = item.id;
        envList.value[index].host = item.connectParams.host;
        envList.value[index].port = item.connectParams.port;
        envList.value[index].dcnId = item.dcnId;
        envList.value[index].connectParam = item.connectParams.connectParam;
        if (addDataSourceForm.value.verifyType === 2) {
            envList.value[index].authType = item.connectParams.authType;
            if (envList.value[index].authType === 'accountPwd') {
                envList.value[index].username = item.connectParams.username;
            }
            if (envList.value[index].authType === 'dpm') {
                envList.value[index].appId = item.connectParams.appId;
                envList.value[index].objectId = item.connectParams.objectId;
            }
        }
    });
    if (addDataSourceForm.value.verifyType === 1) {
        addDataSourceForm.value.authType = props.curDataSourceDetail.connectParams.authType;
        if (addDataSourceForm.value.authType === 'accountPwd') {
            addDataSourceForm.value.username = props.curDataSourceDetail.connectParams.username;
        }
        if (addDataSourceForm.value.authType === 'dpm') {
            addDataSourceForm.value.appId = props.curDataSourceDetail.connectParams.appId;
            addDataSourceForm.value.objectId = props.curDataSourceDetail.connectParams.objectId;
        }
    }
};
const deleteEnv = (data) => {
    const index = envList.value.findIndex(item => item.timestamp === data.timestamp);
    envList.value.splice(index, 1);
};
const addEnv = () => {
    envList.value.push(getInitEnvItem());
};
const resetAuthData = (index, type) => {
    if (type === 'envList') {
        if (envList.value[index].authType === 'accountPwd') {
            envList.value[index].password = '';
            envList.value[index].username = '';
        }
        if (envList.value[index].authType === 'dpm') {
            envList.value[index].appId = '';
            envList.value[index].objectId = '';
            envList.value[index].mkPrivate = '';
        }
    }
    if (type === 'addDataSourceForm') {
        if (addDataSourceForm.value.authType === 'accountPwd') {
            addDataSourceForm.value.password = '';
            addDataSourceForm.value.username = '';
        }
        if (addDataSourceForm.value.authType === 'dpm') {
            addDataSourceForm.value.appId = '';
            addDataSourceForm.value.objectId = '';
            addDataSourceForm.value.mkPrivate = '';
        }
    }
};
onUpdated(() => {
    if (props.show) {
        console.log('数据源组件onUpdated!-showTrue', addDataSourceForm.value);
        // 接收科室初始值
        selectDevDate.value = props.curDataSourceDetail.dev_department_id;
        selectOpsDate.value = props.curDataSourceDetail.ops_department_id;
        selectVisDate.value = props.curDataSourceDetail.visibility_department_list;
        if (props.mode === 'add' || props.mode === 'edit') {
            getDataSourceAddTypeList();
        }
        if (props.mode === 'edit') {
            addDataSourceForm.value.dataSourceTypeId = String(props.curDataSourceDetail.dataSourceTypeId);
        }
        if (envList.value.length === 0) {
            envList.value.push(getInitEnvItem());
        }
    }
});
</script>
<style lang="less" scoped>
.add-or-edit-management {
    .modalFooter {
        .fes-btn{
            margin-left: 8px;
        }
    }
    .title {
        font-weight: 550;
        color: #0f1222;
        font-size: 14px;
        line-height: 22px;
        margin-bottom: 16px;

    }
    .env-item {
        border: 1px solid #cfd0d3;
        border-radius: 4px;
        margin-bottom:16px;
        padding: 16px 16px 0px 16px
    }
    .sub-title {
        font-weight: 550;
        font-size: 14px;
        margin-bottom: 16px;
        .left {
            font-weight: 400;
            display: inline;
            color: #0f1222;
        }
        .right {
            font-weight: 400;
            display: inline;
            color: #93949B;
            float: right;
            cursor: pointer;
        }
    }
    .add-env {
        margin-top: -12px;
        margin-bottom: 8px;
        .link-button{
            padding-left: 0px;
        }
    }
}
</style>
