<template>
    <div>
        <FDrawer
            v-model:show="drawerShow"
            :title="modalTitle"
            :maskClosable="isPreviewMode ? true : false"
            displayDirective="if"
            width="50%"
            :footer="true"
            @cancel="closeEditPanel"
        >
            <!-- 页脚动态菜单 -->
            <template #footer>
                <div v-if="mode !== 'add' && isPreviewMode" class="modalFooter">
                    <FButton type="info" @click="editTemplate">{{$t('common.edit')}}</FButton>
                    <FTooltip
                        mode="confirm"
                        :confirmOption="{ okText: '确认删除' }"
                        placement="top"
                        @ok="deleteTemplate"
                    >
                        <FButton type="danger">{{$t('common.delete')}}</FButton>
                        <template #title>
                            <div style="width: 200px">确认删除此模板吗？</div>
                        </template>
                    </FTooltip>
                </div>
                <div v-else class="modalFooter">
                    <FButton :disabled="isLoading" :loading="isLoading" type="primary" @click="submit">{{$t('common.ok')}}</FButton>
                    <FButton :disabled="isLoading" @click="closeEditPanel">{{$t('common.cancel')}}</FButton>
                </div>
            </template>
            <!-- 查看详情 -->
            <Detail
                v-if="mode !== 'add' && isPreviewMode"
                :standdard="standardForm">
            </Detail>
            <FForm v-else ref="standardFormRef" labelWidth="94px" labelPosition="right" :model="standardForm" :rules="standardFormRules">
                <FFormItem :label="`中文名称`" prop="cn_name">
                    <FInput v-model="standardForm.cn_name" :placeholder="$t('common.pleaseEnter')" />
                </FFormItem>
                <FFormItem :label="`英文名称`" prop="en_name">
                    <FInput v-model="standardForm.en_name" :placeholder="$t('common.pleaseEnter')" />
                </FFormItem>
                <FFormItem :label="'开发科室'" prop="dev_department_name">
                    <FSelectCascader v-model="standardForm.dev_department_name" :data="devDivisions" :loadData="loadDevDivisions"
                                     clearable remote emitPath showPath checkStrictly="child" @change="handleDevId" />
                </FFormItem>
                <FFormItem :label="'运维科室'" prop="ops_department_name">
                    <FSelectCascader v-model="standardForm.ops_department_name" :data="opsDivisions" :loadData="loadOpsDivisions"
                                     clearable remote emitPath showPath checkStrictly="child" @change="handleOpsId" />
                </FFormItem>
                <FFormItem :label="`可见范围`" prop="action_range">
                    <FSelectCascader
                        v-model="standardForm.action_range"
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
                <FFormItem :label="`标准值内容`" prop="content">
                    <FInput v-model="standardForm.content" :disabled="standardForm.source_value === 2" type="textarea" placeholder="默认英文逗号分隔，多字段请使用{}包含，比如：{id_1,name_1},{id_2,name_2}" />
                </FFormItem>
                <StdForm ref="subStdFormRef" v-model:form="standardForm" :mode="mode" @updateStdContent="updateStdContent"></StdForm>
                <!-- <FFormItem :label="`审批系统`" prop="approve_system">
                    <FSelect
                        v-model="standardForm.approve_system"
                        class="form-edit-input"
                        :options="systemList"
                        labelField="message"
                        valueField="code" />
                </FFormItem>
                <FFormItem :label="`审批单号`" prop="approve_number">
                    <FInput v-model="standardForm.approve_number" :placeholder="$t('common.pleaseEnter')" />
                </FFormItem>
                <FFormItem :label="`标签`" prop="label_name">
                    <TagsPanel v-model:tags="standardForm.label_name" />
                </FFormItem> -->
            </FForm>
        </FDrawer>
    </div>
</template>
<script setup>
import {
    ref, computed, defineProps, defineEmits, onUpdated, inject, watch, defineExpose,
} from 'vue';
import {
    useI18n, request as FRequest, useModel,
} from '@fesjs/fes';

import { FMessage } from '@fesjs/fes-design';
import { COMMON_REG } from '@/assets/js/const';
// import TagsPanel from '@/components/TagsPanel.vue';
import useDepartment from '@/hooks/useDepartment';
import useDivisions from '@/hooks/useDivisions';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { cloneDeep, eq } from 'lodash-es';
import { getActionRangeString, getDepartmentName } from '../utils';
import Detail from './detail';
import StdForm from './stdForm';
import { fetchStdDetail, fetchStdContent } from '../api';

const { t: $t } = useI18n();
const standardForm = ref({
    action_range: [],
});
const initialState = useModel('@@initialState');
// 参数type代表不同的模版类型
// 参数id代表需要编辑的模版id，尽在mode===edit的时候有效
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
        default: false,
    },
    mode: {
        type: String,
        default: 'add',
    },
    tid: {
        type: Number,
        default: -1,
    },
    // 1标准值列表 2版本管理列表(已删)
    type: {
        type: Number,
        default: 1,
    },
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
} = useDivisions(true);

const {
    selectDevDate,
    selectOpsDate,
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);

// 添加全行节点

const emit = defineEmits(['update:show', 'loadData']);
const drawerShow = computed({
    get: () => props.show,
    set: (value) => {
        emit('update:show', value);
    },
});
const tableForm = ref({
    templateItems: [{
        name: `${$t('ruleTemplatelist.placeholder')}1`,
        input_meta_name: '',
        input_meta_placeholder: '',
        input_type: '',
        placeholder_description: '',
    }],
});

const activeTab = ref('');

// 无预览模式，只有编辑
const isPreviewMode = ref(false);

const modalTitle = computed(() => {
    if (!isPreviewMode.value && props.mode === 'view') {
        return `${$t('common.edit')}标准值`;
    }
    if (props.mode === 'add') {
        return `${$t('common.add')}标准值`;
    }
    return '标准值详情';
});

const selectedTabs = ref(0);

// const deleteTag = async (tag) => {
//     const index = standardForm.value.label_name.indexOf(tag);
//     standardForm.value.label_name.splice(index, 1);
//     // await $forceUpdate();
// };

// const tempTagInput = ref([]);
// const addNewTag = () => {
//     if (tempTagInput.value) {
//         if (standardForm.value.label_name) {
//             standardForm.value.label_name.push(tempTagInput.value);
//         } else {
//             standardForm.value.label_name = [tempTagInput.value];
//         }
//         tempTagInput.value = '';
//     }
// };

const validateContent = (rule, value) => {
    if (standardForm.value.source_value === 2) {
        return true;
    }
    return !!value;
};
const standardFormRules = {
    cn_name: [
        { required: true, message: $t('common.notEmpty'), trigger: 'input' },
        { pattern: COMMON_REG.CN_NAME, message: $t('myProject.projectEnNameRegTips') },
    ],
    en_name: [
        { required: true, message: $t('common.notEmpty'), trigger: 'input' },
        { pattern: COMMON_REG.EN_NAME, message: $t('myProject.projectEnNameRegTips') },
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
    content: [
        {
            validator: validateContent,
            message: '不能为空',
        },
    ],
    source: [
        { required: true, message: $t('common.notEmpty'), trigger: 'change' },
    ],
};
// 判断几维度数组
const dimension = arr => arr?.some(items => Array.isArray(items));

const isLoading = ref(false);
const standardFormRef = ref(null);
const subStdFormRef = ref(null);
let currentDetail = null;
const initPanel = () => {
    currentDetail = null;
    isPreviewMode.value = false;
    standardForm.value = {
        action_range: [],
    };
};
const submit = async () => {
    try {
        const results = await Promise.all([standardFormRef.value.validate(), subStdFormRef.value.valid(), !isLoading.value]);
        if (results.includes(false)) {
            return;
        }
        isLoading.value = true;
        if (standardForm.value.source_value === 2) {
            subStdFormRef.value.completionStd();
        }
        const params = cloneDeep(standardForm.value);
        console.log('维度', dimension(params.action_range));
        params.modify_user = initialState.userName;
        params.visibility_department_list = selectVisDate.value;
        params.action_range = params.visibility_department_list?.map(item => item.name);
        params.dev_department_id = selectDevDate.value;
        params.dev_department_name = Array.isArray(params.dev_department_name) ? params.dev_department_name.join('/') : params.dev_department_name;
        params.ops_department_id = selectOpsDate.value;
        params.ops_department_name = Array.isArray(params.ops_department_name) ? params.ops_department_name.join('/') : params.ops_department_name;
        let url = '';
        let mes = '';
        if (props.mode === 'add') {
            url = '/api/v1/projector/standardValue/add';
            mes = $t('toastSuccess.addSuccess');
        } else {
            url = '/api/v1/projector/standardValue/modify';
            params.standard_value_id = props.tid;
            mes = $t('toastSuccess.editSuccess');
        }
        console.log('🚀🚀', params);
        await FRequest(url, params);
        FMessage.success(mes);
        emit('update:show', false);
        isLoading.value = false;
        emit('loadData');
        initPanel();
        // isPreviewMode.value = true;
    } catch (err) {
        console.warn(err);
        isLoading.value = false;
        // isPreviewMode.value = true;
    }
};
const closeEditPanel = () => {
    emit('update:show', false);
    initPanel();
};

// 表格行操作-编辑后
const getDetailByTable = async (id) => {
    try {
        isPreviewMode.value = false;
        currentDetail = await fetchStdDetail(id);
        // 数据类型转换
        currentDetail.approve_system = +currentDetail.approve_system;
        standardForm.value = currentDetail;
        standardForm.value.action_range = currentDetail.visibility_department_list?.map(item => item.name);
        standardForm.value.dev_department_name = currentDetail.dev_department_name?.split(' ');
        standardForm.value.ops_department_name = currentDetail.ops_department_name?.split(' ');
        // 接收科室初始值
        selectDevDate.value = standardForm.value.dev_department_id;
        selectOpsDate.value = standardForm.value.ops_department_id;
        selectVisDate.value = standardForm.value.visibility_department_list;
        return currentDetail;
    } catch (err) {
        console.warn(err);
    }
};


// const systemList = inject('systemList');

// watch(() => props.show, (newVal, oldVal) => {
//     console.log(!eq(newVal, oldVal), '新旧值：', newVal, oldVal, props);
//     if (!eq(newVal, oldVal)) {
//         standardForm.value = {
//             action_range: [],
//         };
//     }
// });

// watch(() => props.tid, async () => {
//     console.log('watch props updated: ', props);
//     if (!props.show) {
//         currentDetail = null;
//         isPreviewMode.value = false;
//         standardForm.value = {
//             action_range: [],
//         };
//         return;
//     }
//     // 逻辑只在面板打开的时候执行
//     try {
//         // 每次进来的时候都是显示第一个tab
//         activeTab.value = 'defined';
//         if (props.mode === 'view' && props.tid !== -1) {
//             // if (currentDetail) {
//             //     // 不需要重新初始化
//             //     return;
//             // }
//             isPreviewMode.value = false;
//             // 编辑模式开始填充数据 页面初始数据
//             currentDetail = await FRequest(`/api/v1/projector/standardValue/${props.tid}`, {}, 'get');
//             // 数据类型转换
//             currentDetail.approve_system = +currentDetail.approve_system;
//             standardForm.value = currentDetail;
//             standardForm.value.action_range = currentDetail.visibility_department_list?.map(item => item.name);
//             standardForm.value.dev_department_name = currentDetail.dev_department_name?.split(' ');
//             standardForm.value.ops_department_name = currentDetail.ops_department_name?.split(' ');
//             // 接收科室初始值
//             selectDevDate.value = standardForm.value.dev_department_id;
//             selectOpsDate.value = standardForm.value.ops_department_id;
//             selectVisDate.value = standardForm.value.visibility_department_list;
//         } else {
//             // 重复使用面板的时候需要清空数据
//             standardForm.value = {
//                 action_range: [],
//             };
//         }
//     } catch (err) {
//         console.warn(err);
//     }
// }, { deep: true });

// onUpdated(async () => {
//     // eslint-disable-next-line no-debugger
//     debugger;
//     console.log(11111);
//     if (!props.show) {
//         return;
//     }
//     // 逻辑只在面板打开的时候执行
//     try {
//         // 每次进来的时候都是显示第一个tab
//         activeTab.value = 'defined';
//         if (props.mode === 'edit' && props.tid !== -1) {
//             if (currentDetail) {
//                 // 不需要重新初始化
//                 return;
//             }
//             isPreviewMode.value = true;
//             // 编辑模式开始填充数据 页面初始数据
//             currentDetail = await FRequest(`/api/v1/projector/standardValue/${props.tid}`, {}, 'get');
//             // 数据类型转换
//             currentDetail.approve_system = +currentDetail.approve_system;
//             standardForm.value = currentDetail;
//             standardForm.value.action_range = currentDetail.visibility_department_list.map(item => item.name);
//             standardForm.value.dev_department_name = currentDetail.dev_department_name?.split(' ');
//             standardForm.value.ops_department_name = currentDetail.ops_department_name?.split(' ');
//             // 接收科室初始值
//             selectDevDate.value = standardForm.value.dev_department_id;
//             selectOpsDate.value = standardForm.value.ops_department_id;
//             selectVisDate.value = standardForm.value.visibility_department_list;
//             console.log(standardForm.value, 'standardForm.value');
//         } else {
//             // 重复使用面板的时候需要清空数据
//             standardForm.value = {
//                 action_range: [],
//             };
//         }
//     } catch (err) {
//         console.warn(err);
//     }
// });


// 全局的初始化信息
// const checkUserRights = () => {
//     const isAdmin = sessionStorage.getItem('firstRole') === 'admin';
//     const userName = initialState.userName;
//     // 非管理员用户，不能删除 standard_value_type 为 1 的模板，(内置模板)
//     const flag1 = !isAdmin && currentDetail.standard_value_type === 1;
//     // 管理员用户，不能删除 standard_value_type 为 1 并且 (没有创建用户的模板 或者 创建用户不是本人) 的模板（内置模板）
//     const flag2 = isAdmin && currentDetail.standard_value_type === 1 && (!currentDetail.create_user || currentDetail.create_user !== userName);
//     if (flag1 || flag2) return false;
//     return true;
// };

// 删除规则
const deleteTemplate = async () => {
    console.log('信息', standardForm.value);
    if (!standardForm.value.is_editable) {
        return FMessage.error('没有删除权限');
    }

    // 1标准值列表2版本管理列表
    await FRequest('api/v1/projector/standardValue/delete', {
        edition_id: currentDetail.edition_id,
        type: props.type,
    });
    FMessage.success($t('toastSuccess.deleteSuccess'));

    emit('update:show', false);
    emit('loadData');
};

const editTemplate = () => {
    console.log('权限', standardForm.value, !standardForm.value.is_editable);
    if (!standardForm.value.is_editable) {
        return FMessage.error('没有权限编辑');
    }
    standardForm.value.action_range = standardForm.value.visibility_department_list?.map(item => item.name.split('/')) || [];
    standardForm.value.dev_department_name = Array.isArray(standardForm.value.dev_department_name) ? standardForm.value.dev_department_name : standardForm.value.dev_department_name?.split(' ') || [];
    standardForm.value.ops_department_name = Array.isArray(standardForm.value.ops_department_name) ? standardForm.value.ops_department_name : standardForm.value.ops_department_name?.split(' ') || [];
    initDepartment(standardForm.value.visibility_department_list);
    console.log('编辑后：', standardForm.value);
    isPreviewMode.value = false;
};

const updateStdContent = async (code) => {
    try {
        if (code) {
            const r = await fetchStdContent(
                {
                    page: 1,
                    size: 500,
                    stdCode: code,
                },
            );
            let stdContentArray = [];
            stdContentArray = r.content.map(e => e.codeTableValue);
            stdContentArray = stdContentArray.map(e => `'${e}'`);
            standardForm.value.content = stdContentArray.join(',');
        } else {
            standardForm.value.content = '';
        }
        standardFormRef.value.clearValidate();
    } catch (e) {
        console.error(e);
    }
};
defineExpose({ getDetailByTable });
</script>
<style lang="less" scoped>
.table-tabs-ctn{
    padding: 20px 16px 0;
    // 按照fes的标准写
    border-right: var(--f-border-width-base) var(--f-border-style-base) var(--f-border-color-split);
    border-bottom: var(--f-border-width-base) var(--f-border-style-base) var(--f-border-color-split);
    border-left: var(--f-border-width-base) var(--f-border-style-base) var(--f-border-color-split);
    border-bottom-left-radius: var(--f-border-radius-base);
    border-bottom-right-radius: var(--f-border-radius-base);
}
.fes-tabs-tab.fes-tabs-tab-disabled {
    padding-left: 0;
    padding-right: 0;
    cursor: default;
}
.modalFooter{
    .fes-btn{
        margin-left: 8px;
    }
}
</style>
