<template>
    <FDrawer
        :show="show"
        :title="title"
        :maskClosable="false"
        :footer="true"
        width="50%"
        @cancel="handleCancel">
        <MetricDetail
            v-show="mode === FORM_MODE.REVIEW"
            :data="formModel"
            :metricCategories="metricCategories"
            :metricFrequencies="metricFrequencies"
            :bussinessDimensions="bussinessDimensions"
            :subSystems="subSystems"
            :products="products" />
        <FForm v-show="mode === FORM_MODE.ADD || mode === FORM_MODE.EDIT" ref="formRef" label-width="72px" class="metric-form" :model="formModel" :rules="formRule">
            <!-- 指标名 -->
            <FFormItem :label="$t('indexManagement.indexName')" prop="name">
                <FInput :modelValue="formModel.name" readonly disabled :maxlength="500" type="textarea" :placeholder="$t('indexManagement.autoGenerate')" />
            </FFormItem>
            <!-- 指标描述 -->
            <FFormItem :label="$t('indexManagement.indexDesc')" prop="metric_desc">
                <FInput v-model="formModel.metric_desc" clearable :maxlength="500" type="textarea" :placeholder="$t('common.pleaseEnter')" />
            </FFormItem>
            <!-- 指标分类 -->
            <FFormItem :label="$t('indexManagement.indexCategory')" prop="type">
                <FSelect v-model="formModel.type" clearable filterable valueField="value" labelField="label" :options="metricCategories" @change="selectIndicatorsType">
                </FSelect>
            </FFormItem>
            <!-- 指标频率 -->
            <FFormItem :label="$t('indexManagement.indexFrequency')" prop="frequency">
                <FSelect v-model="formModel.frequency" clearable filterable valueField="value" labelField="label" :options="metricFrequencies" @change="selectIndicatorsFrequency">
                </FSelect>
            </FFormItem>
            <!-- 英文编码 -->
            <FFormItem :label="$t('indexManagement.en_code')" prop="en_code">
                <FInput v-model="formModel.en_code" :maxlength="100" :placeholder="$t('common.pleaseEnter')" @input="inputEnCodeHandler" />
            </FFormItem>
            <!-- 业务维度 -->
            <FFormItem :label="$t('indexManagement.bussinessDimension')" prop="buss_code">
                <FSelect
                    v-model="formModel.buss_code"
                    clearable
                    disabled
                    default-first-option
                    placeholder="根据选择的指标分类匹配"
                    filterable
                    valueField="value"
                    labelField="label"
                    :options="bussinessDimensions"
                    @change="selectBussinessDimension">
                </FSelect>
            </FFormItem>
            <!-- 子系统 -->
            <FFormItem v-if="formModel.buss_code === 1" :label="$t('indexManagement.subsystem')" prop="sub_system_name">
                <FInput
                    v-model="formModel.sub_system_name"
                    clearable
                    @change="selectSubSystem" />
            </FFormItem>
            <!-- 产品 -->
            <FFormItem v-if="formModel.buss_code === 2" :label="$t('indexManagement.product')" prop="product_name">
                <FInput
                    v-model="formModel.product_name"
                    clearable />
            </FFormItem>
            <!-- 自定义 -->
            <FFormItem v-if="formModel.buss_code === 3" :label="$t('indexManagement.customize')" prop="buss_custom">
                <FInput
                    v-model="formModel.buss_custom"
                    clearable
                    :maxlength="50"
                    :placeholder="$t('common.pleaseEnter')"
                    @blur="blurBussCustom" />
            </FFormItem>
            <!--指标是否为多DCN-->
            <FFormItem :label="$t('indexManagement.inMultiDCN')" prop="multi_env">
                <FRadioGroup v-model="formModel.multi_env" :cancelable="false">
                    <FRadio :value="true">{{$t('common.yes')}}</FRadio>
                    <FRadio :value="false">{{$t('common.no')}}</FRadio>
                </FRadioGroup>
            </FFormItem>
            <!-- 开发科室 -->
            <FFormItem :label="$t('indexManagement.developDepartment')" prop="dev_department_name">
                <FSelectCascader
                    v-model="formModel.dev_department_name"
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
            <FFormItem :label="$t('indexManagement.maintainDepartment')" prop="ops_department_name">
                <FSelectCascader
                    v-model="formModel.ops_department_name"
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
            <FFormItem :label="$t('indexManagement.visibleRange')" prop="action_range">
                <FSelectCascader
                    v-model="formModel.action_range"
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
            <!-- 指标是否可用 -->
            <FFormItem :label="$t('indexManagement.inUse')" prop="available">
                <FRadioGroup v-model="formModel.available" :cancelable="false">
                    <FRadio :value="true">{{$t('common.yes')}}</FRadio>
                    <FRadio :value="false">{{$t('common.no')}}</FRadio>
                </FRadioGroup>
            </FFormItem>
        </FForm>
        <template #footer>
            <div v-show="mode === FORM_MODE.REVIEW">
                <FButton type="danger" style="margin-right: 15px" @click="handleDeleteMetric">{{$t('common.delete')}}</FButton>
                <FButton type="info" @click="handleEditMetric">{{$t('common.edit')}}</FButton>
            </div>
            <div v-show="mode === FORM_MODE.ADD">
                <FButton style="margin-right: 15px" @click="handleCancel">{{$t('common.cancel')}}</FButton>
                <FButton type="primary" @click="submit">{{$t('common.ok')}}</FButton>
            </div>
            <div v-show="mode === FORM_MODE.EDIT">
                <FButton style="margin-right: 15px" @click="handleCancel">{{$t('common.cancel')}}</FButton>
                <FButton type="primary" @click="submit">{{$t('common.save')}}</FButton>
            </div>
        </template>
    </FDrawer>
</template>
<script setup>
import {
    defineProps, defineEmits, reactive, ref, toRefs, computed, onMounted, onUnmounted,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FModal, FMessage, FDrawer } from '@fesjs/fes-design';
import { cloneDeep } from 'lodash-es';
import eventbus from '@/common/useEvents';
import { FORM_MODE, FORM_MODES, COMMON_REG } from '@/assets/js/const';
import useDivisions from '@/hooks/useDivisions';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import useDepartment from '@/hooks/useDepartment';
import MetricDetail from './components/detail';
import {
    fetchSubSystemInfo, fetchProductInfo, addMetric, modifyMetric, metricDetail,
} from './api';


const { t: $t } = useI18n();
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    mode: {
        validator: value => value === '' || FORM_MODES.includes(value),
        requried: true,
    },
    metricCategories: {
        type: Array,
        required: true,
    },
    metricFrequencies: {
        type: Array,
        required: true,
    },
});

const {
    mode,
    metricCategories,
    metricFrequencies,
} = toRefs(props);

const emit = defineEmits([
    'update:show',
    'update:mode',
    'on-delete',
    'on-add-success',
    'on-edit-success',
]);

// 弹窗title
const titleMapping = {
    [FORM_MODE.ADD]: '新增指标',
    [FORM_MODE.EDIT]: '指标编辑',
    [FORM_MODE.REVIEW]: '指标详情',
};
const title = computed(() => titleMapping[props.mode] || $t('common.prompt'));

// 指标表单ref引用
const formRef = ref(null);

// 指标表单对象
const formModel = reactive({
    // 指标id
    id: '',
    // 指标英文名
    name: '',
    // 指标中文名
    cn_name: '',
    // 指标描述
    metric_desc: '',
    // 指标分类
    type: '',
    // 指标频率
    frequency: '',
    // 英文编码
    en_code: '',
    // 业务维度:
    buss_code: '',
    // 子系统
    sub_system_id: '',
    // 子系统名
    sub_system_name: '',
    // 是否为多DCN指标
    multi_env: false,
    // 产品
    product_id: '',
    // 产品名
    product_name: '',
    // 自定义
    buss_custom: '',
    // 可选范围
    action_range: [],
    visibility_department_list: [],
    // 开发部门
    department_name: '',
    // 开发科室
    dev_department_id: '',
    dev_department_name: [],
    // 运维科室
    ops_department_id: '',
    ops_department_name: [],
    // 是否可用
    available: false,
    // 是否可编辑
    is_editable: false,
});

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
// 接收科室初始值
// 指标表单校验规则
const formRule = {
    name: [
        { required: true, message: $t('common.notEmpty') },
    ],
    metric_desc: [
        { required: true, message: $t('common.notEmpty') },
    ],
    type: [
        {
            required: true, trigger: ['change', 'blur'], type: 'number', message: $t('common.notEmpty'),
        },
    ],
    frequency: [
        {
            required: true, trigger: ['change', 'blur'], type: 'number', message: $t('common.notEmpty'),
        },
    ],
    en_code: [
        { required: true, message: $t('common.notEmpty') },
        {
            pattern: COMMON_REG.ONLY_EN_NAME,
            message: $t('myProject.projectOnlyEnNameRegTips'),
        },
    ],
    buss_code: [
        {
            required: true, trigger: ['change', 'blur'], type: 'number', message: $t('common.notEmpty'),
        },
    ],
    action_range: [
        {
            required: false, message: $t('common.notEmpty'), type: 'array', trigger: 'change',
        },
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
    sub_system_name: [
        { required: true, message: $t('common.notEmpty') },
    ],
    multi_env: [
        { required: true, type: 'boolean', message: $t('common.notEmpty') },
    ],
    available: [
        { required: true, type: 'boolean', message: $t('common.notEmpty') },
    ],
};

// 业务维度
const bussinessDimensions = [
    { label: $t('indexManagement.subsystem'), value: 1 },
    { label: $t('indexManagement.product'), value: 2 },
    { label: $t('indexManagement.customize'), value: 3 },
];

// 子系统列表
const subSystems = ref([]);
// 获取子系统中文名
const getSystemNameNew = (data, tr) => tr.full_cn_name || tr.subSystemFullCnName || data;
// 获取子系统列表
const getSubSystemInfo = async () => {
    const res = await fetchSubSystemInfo();
    const list = res || [];
    subSystems.value = list.map((item) => {
        const cnName = getSystemNameNew(item.subSystemId, item);
        return Object.assign({}, item, {
            subSystemName: cnName,
            enName: item.subSystemName,
            cnName,
            value: String(item.subSystemId),
            label: item.subSystemName,
        });
    });
};

// 产品列表
const products = ref([]);
// 获取产品列表
const getProducts = async () => {
    const res = await fetchProductInfo();
    if (!Array.isArray(res)) return;
    products.value = res.map(item => Object.assign(item, {
        enName: item.productId,
        cnName: item.productName,
        value: item.productId,
        label: item.productName,
    }));
};

// 更新指标名(拼接的指标名信息)
const updateIndicatorsNameInfo = () => {
    const bussCode = formModel.buss_code;
    const enCode = formModel.en_code;
    const category = metricCategories.value.find(item => item.value === formModel.type);
    const frequency = metricFrequencies.value.find(item => item.value === formModel.frequency);
    const nameItems = [];
    let tempObj = {};
    let enName = '';
    let cnName = '';
    if ([1, '1'].includes(bussCode)) {
        // tempObj = subSystems.value.find(item => +item.subSystemId === +formModel.sub_system_id);
        tempObj = { enName: formModel.sub_system_name, cnName: formModel.sub_system_name };
    } else if ([2, '2'].includes(bussCode)) {
        tempObj = products.value.find(item => +item.productId === +formModel.product_id);
    } else {
        tempObj = {
            enName: formModel.buss_custom,
            cnName: formModel.buss_custom,
        };
    }
    if (tempObj) {
        nameItems.push(tempObj);
    }
    if (category) {
        nameItems.push(category);
    }
    if (enCode) {
        tempObj = {
            enName: enCode,
            cnName: enCode,
        };
        nameItems.push(tempObj);
    }
    if (frequency) {
        nameItems.push(frequency);
    }
    nameItems.forEach((item, index) => {
        if (item.enName) {
            enName += index === nameItems.length - 1 ? item.enName : `${item.enName}_`;
        }
        if (item.cnName) {
            cnName += index === nameItems.length - 1 ? item.cnName : `${item.cnName}_`;
        }
    });
    formModel.name = enName;
    formModel.cn_name = cnName;
};


// 选择业务维度
const selectBussinessDimension = () => {
    formModel.sub_system_id = '';
    formModel.sub_system_name = '';
    formModel.product_id = '';
    formModel.product_name = '';
    formModel.buss_custom = '';
    updateIndicatorsNameInfo();
};

// 选择指标分类
const selectIndicatorsType = () => {
    updateIndicatorsNameInfo();
    formModel.buss_code = formModel.type;
    selectBussinessDimension();
};

// 选择指标频率
const selectIndicatorsFrequency = () => {
    updateIndicatorsNameInfo();
};

// 选择子系统
const selectSubSystem = (value) => {
    // const target = subSystems.value.find(item => item.value === value);
    // if (target) {
    //     formModel.sub_system_name = target.label || '';
    // }
    updateIndicatorsNameInfo();
};

// 失焦英文编码
const inputEnCodeHandler = () => {
    updateIndicatorsNameInfo();
};

// 选择产品
const selectProduct = (value) => {
    const target = products.value.find(item => item.value === value);
    if (target) {
        formModel.product_name = target.label || '';
    }
    updateIndicatorsNameInfo();
};

// 失焦自定义编码
const blurBussCustom = () => {
    updateIndicatorsNameInfo();
};

// 点击删除
const handleDeleteMetric = () => {
    console.log('删除', formModel);
    if (!formModel.is_editable) return FMessage.error('没有权限删除');
    if (mode.value !== FORM_MODE.REVIEW) return;
    emit('on-delete', formModel);
};

// 由查看详情模式修改为编辑模式
const handleEditMetric = () => {
    if (formModel.is_editable) {
        if (mode.value !== FORM_MODE.REVIEW) return;
        formModel.action_range = formModel.visibility_department_list?.map(item => item.name.split('/')) || [];
        initDepartment(formModel.visibility_department_list);
        emit('update:mode', FORM_MODE.EDIT);
    } else {
        FMessage.error('没有权限编辑');
    }
};

// 取消
const handleCancel = () => {
    formRef.value.resetFields();
    emit('update:mode', '');
    emit('update:show', false);
};

// 提交表单数据相关
const reqDataMapping = {
    [FORM_MODE.ADD]: {
        action: addMetric,
        getParams: () => {
            const data = cloneDeep(formModel);
            console.log('devCurSubDepartData', devCurSubDepartData.value, 'opsCurSubDepartData', opsCurSubDepartData.value);
            data.visibility_department_list = selectVisDate.value;
            data.dev_department_id = selectDevDate.value;
            data.dev_department_name = data.dev_department_name.join('/');
            data.ops_department_id = selectOpsDate.value;
            data.ops_department_name = data.ops_department_name.join('/');
            // data.action_range = data.sibility_department_list?.map(item => item.join('/'));
            delete data.id;
            return data;
        },
        successMessage: $t('toastSuccess.addSuccess'),
        successEventName: 'on-add-success',
    },
    [FORM_MODE.EDIT]: {
        action: modifyMetric,
        getParams: () => {
            const data = cloneDeep(formModel);
            data.visibility_department_list = selectVisDate.value;
            data.dev_department_id = selectDevDate.value;
            data.dev_department_name = data.dev_department_name.join('/');
            data.ops_department_id = selectOpsDate.value;
            data.ops_department_name = data.ops_department_name.join('/');
            return data;
        },
        successMessage: $t('toastSuccess.editSuccess'),
        successEventName: 'on-edit-success',
    },
};
const createReqData = () => reqDataMapping[mode.value];
const submit = () => {
    if (mode.value === '') return;
    let action;
    let getParams;
    let successMessage;
    let successEventName;
    formRef.value.validate().then(() => {
        ({
            action,
            getParams,
            successMessage,
            successEventName,
        } = createReqData());
        return action(getParams());
    }).then(() => {
        FMessage.success(successMessage);
        handleCancel();
        emit(successEventName);
    });
};

const getPreparedData = () => {
    // getProducts();
    getSubSystemInfo();
};
let currentDetail = null;
onMounted(() => {
    getPreparedData();

    eventbus.on('metric:set-form-model', async (payload) => {
        console.log(payload, 'payload');
        currentDetail = await metricDetail(payload.id);
        console.log('请求详情', currentDetail);
        Object.keys(formModel).forEach((key) => {
            const value = currentDetail[key];
            if (value) formModel[key] = value;
            if (key === 'sub_system_id') {
                formModel[key] = value ? String(value) : value;
            }
        });
        try {
            formModel.dev_department_name = currentDetail.dev_department_name?.split(' ') || [];
            formModel.ops_department_name = currentDetail.ops_department_name?.split(' ') || [];
            formModel.action_range = currentDetail.visibility_department_list?.map(item => item.name) || [];
            selectDevDate.value = formModel.dev_department_id;
            selectOpsDate.value = formModel.ops_department_id;
            selectVisDate.value = formModel.visibility_department_list;
        } catch (err) {
            console.warn(err);
        }
        console.log('初始数据', formModel);
    });
});

onUnmounted(() => {
    eventbus.off('metric:set-form-model');
});
</script>
<style lang="less">
.metric-form {
    .fes-form-item {
        .fes-form-item-label {
            justify-content: flex-end;
        }
    }
}
</style>
