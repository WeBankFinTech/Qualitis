<template>
    <FDrawer v-model:show="drawerShow" displayDirective="if" :title="drawerTitle" :footer="true" width="50%" @cancel="handleClose">
        <FForm ref="funcFormRef" :labelWidth="95" :labelPosition="drawerType === 'display' ? 'left' : 'right'" :model="data" :rules="curRules" :class="classMode" class="rule-detail-form">
            <FFormItem prop="cn_name" :label="$t('functionManagement.cn_name')">
                <FInput
                    v-model="data.cn_name"
                    class="form-edit-input"
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.cn_name}}</div>
            </FFormItem>
            <FFormItem prop="name" :label="$t('functionManagement.name')">
                <FInput
                    v-model="data.name"
                    class="form-edit-input"
                    :disabled="drawerType === 'edit'"
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.name}}</div>
            </FFormItem>
            <FFormItem prop="desc" :label="$t('functionManagement.desc')">
                <FInput
                    v-model="data.desc"
                    class="form-edit-input"
                    type="textarea"
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.desc}}</div>
            </FFormItem>
            <FFormItem prop="enable_cluster" :label="$t('functionManagement.enable_cluster')">
                <FSelect
                    v-model="data.enable_cluster"
                    filterable
                    class="form-edit-input"
                    multiple
                    clearable
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
                <div class="form-preview-label">{{data.enable_cluster?.join(', ')}}</div>
            </FFormItem>
            <FFormItem prop="dir" :label="$t('functionManagement.dir')">
                <FSelect
                    v-model="data.dir"
                    class="form-edit-input"
                    remote
                    clearable
                    :disabled="drawerType === 'edit'"
                    :options="tempDirList"
                    @search="searchDir"
                    @clear="clearDir"
                ></FSelect>
                <div class="form-preview-label">{{data.dir}}</div>
            </FFormItem>
            <FFormItem prop="enter" :label="$t('functionManagement.enter')">
                <FInput
                    v-model="data.enter"
                    class="form-edit-input"
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.enter}}</div>
            </FFormItem>
            <FFormItem prop="return" :label="$t('functionManagement.return')">
                <FInput
                    v-model="data.return"
                    class="form-edit-input"
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.return}}</div>
            </FFormItem>
            <FFormItem prop="preview" :label="$t('functionManagement.preview')" class="preview">
                <div>{{preview}}</div>
            </FFormItem>
            <FFormItem prop="impl_type" :label="$t('functionManagement.impl_type')">
                <FSelect
                    v-model="data.impl_type"
                    filterable
                    clearable
                    class="form-edit-input"
                    :disabled="drawerType === 'edit'"
                    :options="implTypeList"
                    @change="delFile"
                >
                </FSelect>
                <div class="form-preview-label">{{implTypeList.find(item => data.impl_type === item.value)?.label}}</div>
            </FFormItem>
            <FFormItem prop="file" :label="$t('functionManagement.file')">
                <div class="form-edit-input">
                    <FButton v-if="!data.file && !uploading" @click="uploadFile"><PlusOutlined />{{$t('_.上传函数')}}</FButton>
                    <div v-else-if="uploading" class="uploading"><FSpin />{{$t('_.上传中')}}</div>
                    <FileBtn v-else :file="data.file" @delFile="delFile" />
                </div>
                <div class="form-preview-label">{{data.file?.split('/')[data.file?.split('/').length - 1]}}</div>
            </FFormItem>
            <FFormItem prop="register_name" :label="$t('functionManagement.register_name')">
                <FInput
                    v-model="data.register_name"
                    class="form-edit-input"
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.register_name}}</div>
            </FFormItem>
            <FFormItem prop="enable_engine" :label="$t('functionManagement.enable_engine')">
                <FSelect
                    v-model="data.enable_engine"
                    filterable
                    clearable
                    multiple
                    class="form-edit-input"
                    :options="engineList"
                >
                </FSelect>
                <div class="form-preview-label">{{data.enable_engine?.map(i => engineList?.find(p => p.value === i)?.label)?.join(', ')}}</div>
            </FFormItem>
            <FFormItem :label="$t('functionManagement.devDepartment')" prop="dev_department_name">
                <FSelectCascader v-model="data.dev_department_name" class="form-edit-input" :data="devDivisions" :loadData="loadDevDivisions"
                                 clearable remote emitPath showPath checkStrictly="child" @change="handleDevId" />
                <div class="form-preview-label">{{data.dev_department_name?.join(' / ')}}</div>
            </FFormItem>
            <FFormItem :label="$t('functionManagement.opsDepartment')" prop="ops_department_name">
                <FSelectCascader v-model="data.ops_department_name" class="form-edit-input" :data="opsDivisions" :loadData="loadOpsDivisions"
                                 clearable remote emitPath showPath checkStrictly="child" @change="handleOpsId" />
                <div class="form-preview-label">{{data.ops_department_name?.join(' / ')}}</div>
            </FFormItem>
            <FFormItem :label="$t('functionManagement.visibility_department')" prop="visibility_department_list">
                <FSelectCascader
                    v-model="data.visibility_department_list"
                    class="form-edit-input"
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
                <div class="form-preview-label">{{data.visibility_department_list?.map(item => item?.join(' / ') || '')?.join(', ') || '--'}}</div>
            </FFormItem>
            <FFormItem prop="status" :label="$t('functionManagement.status')">
                <FRadioGroup v-model="data.status" class="form-edit-input">
                    <FRadio :value="true">{{$t('_.启用')}}</FRadio>
                    <FRadio :value="false">{{$t('_.停用')}}</FRadio>
                </FRadioGroup>
                <div class="form-preview-label">{{data.status ? $t('_.启用') : $t('_.禁用')}}</div>
            </FFormItem>
        </FForm>
        <template #footer>
            <div v-if="drawerType !== 'display'">
                <FButton type="primary" style="margin-right: 16px;" :loading="isSubmitting || uploading" @click="submitForm">{{$t('_.确定')}}</FButton>
                <FButton @click="handleClose">{{$t('_.取消')}}</FButton>
            </div>
            <div v-else>
                <FButton type="info" style="margin-right: 16px;" :loading="isSubmitting" @click="drawerTypeChange">{{$t('_.编辑')}}</FButton>
                <FTooltip
                    mode="confirm"
                    :confirmOption="{ okText: $t('_.确认删除') }"
                    placement="top"
                    @ok="deleteFunc"
                >
                    <FButton type="danger" :loading="isSubmitting" :disabled="data.status">{{$t('_.删除')}}</FButton>
                    <template #title>
                        <div style="width: 200px">{{$t('_.确认删除此函数吗？')}}</div>
                    </template>
                </FTooltip>
            </div>
        </template>
        <input
            v-show="false"
            :ref="el => { if (el) fileInputRefs = el }"
            type="file"
            class="btn-file-input"
            :accept="acceptType"
            @change="importFunc(file)"
        />
    </FDrawer>
</template>
<script setup>

import {
    defineProps, defineEmits, computed, ref, unref, inject, onMounted,
} from 'vue';
import {
    useI18n, request,
} from '@fesjs/fes';
import {
    PlusOutlined,
} from '@fesjs/fes-design/es/icon';
import useDepartment from '@/hooks/useDepartment';
import { ACCEPT_TYPE_MAP } from '@/assets/js/const.js';
import useDivisions from '@/hooks/useDivisions';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { cloneDeep } from 'lodash-es';
import { FMessage, FModal } from '@fesjs/fes-design';
import useBasicOptions from '../hooks/useBasicOptions';
import FileBtn from './fileBtn';

const {
    engineList,
    implTypeList,
    dirList,
    handleClusterChange,
} = useBasicOptions();
const clusterList = inject('clusterList');
const { t: $t } = useI18n();
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
} = useDivisions(true);

const {
    // code
    selectDevDate,
    // code
    selectOpsDate,
    // 可见code
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);
const props = defineProps({
    data: {
        type: Object,
        required: true,
    },
    show: {
        type: Boolean,
        required: true,
    },
    type: {
        type: String,
        required: true,
    },
});
const drawerType = ref(props.type);
const classMode = computed(() => {
    if (drawerType.value !== 'display') {
        return 'edit';
    }
    return 'index-detail-form';
});
const drawerTypeChange = () => {
    drawerType.value = 'edit';
    // eslint-disable-next-line no-use-before-define
    initDepartment(data.value.original_list);
    // eslint-disable-next-line no-use-before-define
    loadDevDivisions(devDivisions.value.find(item => data.value.dev_department_name[0] === item.label));
    // eslint-disable-next-line no-use-before-define
    loadOpsDivisions(opsDivisions.value.find(item => data.value.ops_department_name[0] === item.label));
};
const data = ref(props.data);
const emit = defineEmits(['submit', 'update:show']);

const drawerShow = computed({
    get() {
        return props.show;
    },
    set(value) {
        emit('update:show', value);
    },
});
const titleMap = {
    display: $t('_.函数详情'),
    edit: $t('_.编辑函数'),
    add: $t('_.新增函数'),
};
const drawerTitle = computed(() => titleMap[drawerType.value]);

const handleClose = () => {
    drawerShow.value = false;
};

// 函数文件
const uploading = ref(false);
const fileInputRefs = ref(null);
const uploadFile = () => {
    if (!data.value.impl_type) {
        FMessage.warning($t('_.请先选择实现方式'));
        return;
    }
    fileInputRefs.value?.click();
};
const importFunc = async () => {
    try {
        const fileInputRef = fileInputRefs.value;
        if (!fileInputRef) return;
        const file = fileInputRef.files[0];
        const formData = new FormData();
        formData.append('file', file);
        uploading.value = true;
        const res = await request('/api/v1/projector/meta_data/udf/upload', formData, 'post');
        uploading.value = false;
        data.value.file = res;
    } catch (err) {
        console.warn(err);
        uploading.value = false;
    }
};

const delFile = () => {
    data.value.file = '';
    fileInputRefs.value.value = '';
};
// 提交表单
const isSubmitting = ref(false);
const funcFormRef = ref(null);
const submitForm = async () => {
    try {
        isSubmitting.value = true;
        await funcFormRef.value.validate();
        data.value.dev_department_id = selectDevDate.value;
        data.value.ops_department_id = selectOpsDate.value;
        // 不能直接在data上处理，绑定了级联选择器组件操作对应数据会报错
        const body = cloneDeep(unref(data));
        // 如果可见范围有值，组件需要的数据是嵌套数组，但是后台需要的则是[{id:xxx, name:'xxx/xxx'}]，会出现接口参数格式错误的问题。
        // 如果不处理后台参数值，在编辑反显的时候，级联选择器组件则会报错，所以这里需要判断两种情况：
        // 1、 级联选择器绑定值为嵌套数组，这是判断数组内元素是否为数组，为数组的话用级联组件绑定的data去做预处理传给后台
        // 1.1、 此时会出现两种情况，一种是部门全选，只有部门名称，此时直接过滤。
        // 1.2、 一种是选中部门下科室，这是需要找到对应部门，并利用其children数组来做处理。
        // 2、 级联选择器绑定值为对象数组，此时组件绑定的change事件做了处理，直接传给后台
        // eslint-disable-next-line array-callback-return
        body.visibility_department_list = Array.isArray(selectVisDate.value[0]) ? selectVisDate.value.map((item) => {
            if (item.length === 1) {
                const target = visDivisions.value.find(division => division.label === item[0]);
                const id = target.code;
                const name = target.label;
                return {
                    id,
                    name,
                };
            }
            if (item.length === 2) {
                const target = visDivisions.value.find(division => division.label === item[0]);
                const id = target.children.find(division => division.label === item[1]).code;
                const name = `${target.label}/${target.children.find(division => division.label === item[1]).label}`;
                return {
                    id,
                    name,
                };
            }
        }) : selectVisDate.value;
        body.dev_department_name = body.dev_department_name?.join('/');
        body.ops_department_name = body.ops_department_name?.join('/');
        delete body.original_list;
        const target = drawerType.value === 'edit' ? '/api/v1/projector/meta_data/udf/modify' : '/api/v1/projector/meta_data/udf/add';
        const res = await request(target, body, 'post');
        isSubmitting.value = false;
        FMessage.success(`${drawerType.value === 'edit' ? $t('_.编辑') : $t('_.新建')}成功`);
        emit('submit');
        handleClose();
    } catch (err) {
        console.warn(err);
        isSubmitting.value = false;
    }
};
const rules = ref({
    cn_name: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    name: [
        {
            required: true,
            trigger: ['change', 'blur'],
            message: $t('common.notEmpty'),
        },
        {
            type: 'string',
            trigger: ['change', 'blur'],
            message: $t('_.必须以字母开头，且只支持数字、字母和下划线'),
            pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
        },
    ],
    desc: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    dir: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    enter: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    return: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    impl_type: [{
        required: true,
        type: 'number',
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    file: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    register_name: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    enable_engine: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    enable_cluster: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    dev_department_name: [{
        required: true,
        type: 'array',
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    ops_department_name: [{
        required: true,
        type: 'array',
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    status: [{
        required: true,
        type: 'boolean',
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
});
const curRules = computed(() => {
    if (drawerType.value === 'display') {
        return {};
    }
    return rules.value;
});
// 集群变换函数分类获取列表改变
// eslint-disable-next-line camelcase
const onClusterChange = async (cluster_name) => {
    data.value.dir = '';
    await handleClusterChange(cluster_name[0]);
    // eslint-disable-next-line no-use-before-define
    searchDir('');
};
// 函数分类(支持自定义)
const tempDirList = ref([]);
const searchDir = (val) => {
    if (val === '') {
        tempDirList.value = unref(dirList);
        return;
    }
    if (!tempDirList.value.find(item => item.value === val)) {
        // 不存在对应元素，追加自定义输入
        tempDirList.value = [{ value: val, label: val }].concat(dirList.value);
    } else {
        tempDirList.value = dirList.value.filter(item => item.value === val);
    }
};
const clearDir = (val) => {
    tempDirList.value = unref(dirList);
};

// 函数预览
const preview = computed(() => {
    if (data.value.return && data.value.enter && data.value.name) {
        return `${data.value.return} ${data.value.name}(${data.value.enter})`;
    }
    return '';
});
// 函数删除
const deleteFunc = async () => {
    console.log('delete', data.value.id);
    try {
        isSubmitting.value = true;
        const res = await request('/api/v1/projector/meta_data/udf/delete', { id: data.value.id }, 'post');
        console.log('删除', res);
        FMessage.success($t('_.删除成功'));
        isSubmitting.value = false;
        emit('submit');
        handleClose();
    } catch (err) {
        console.warn(err);
        isSubmitting.value = false;
    }
};

// 可接受类型
const acceptType = computed(() => {
    switch (data.value.impl_type) {
        // jar
        case ACCEPT_TYPE_MAP.JAR:
            return '.jar';
        // scala
        case ACCEPT_TYPE_MAP.SCALA:
            return '.scala';
        // python
        case ACCEPT_TYPE_MAP.PYTHON:
            return '.py';
        default:
            return '.jar,.scala,.py';
    }
});

onMounted(async () => {
    // 手动初始化科室id
    selectDevDate.value = data.value.dev_department_id;
    selectOpsDate.value = data.value.ops_department_id;
    selectVisDate.value = data.value.visibility_department_list;
    await handleClusterChange();
    searchDir('');
});

</script>
<style lang="less" scoped>
.view-font {
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #0f1222;
    line-height: 22px;
    font-weight: 400;
}
.rule-detail-form .fes-select, .rule-detail-form .fes-input, .rule-detail-form .fes-textarea {
    max-width: 800px;
}

/deep/ .preview {
    .fes-form-item-content {
        background-color: #f8f8f8;
        padding: 0 16px;
        border-radius: 4px;
    }
}

</style>
