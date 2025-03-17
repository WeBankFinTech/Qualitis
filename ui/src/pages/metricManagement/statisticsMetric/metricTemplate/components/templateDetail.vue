<template>
    <FDrawer v-model:show="drawerShow" displayDirective="if" :title="drawerTitle" :footer="true" width="50%" @cancel="handleClose">
        <FForm ref="funcFormRef" :labelWidth="95" :labelPosition="drawerType === 'display' ? 'left' : 'right'" :model="data" :rules="curRules" :class="classMode" class="rule-detail-form">
            <FFormItem prop="en_name" :label="$t('_.算子英文名称')">
                <FInput
                    v-model="data.en_name"
                    class="form-edit-input"
                    :disabled="drawerType === 'edit'"
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.en_name}}</div>
            </FFormItem><FFormItem prop="cn_name" :label="$t('_.算子中文名称')">
                <FInput
                    v-model="data.cn_name"
                    class="form-edit-input"
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.cn_name}}</div>
            </FFormItem>
            <FFormItem prop="desc" :label="$t('_.算子描述')">
                <FInput
                    v-model="data.description"
                    class="form-edit-input"
                    type="textarea"
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.description || '--'}}</div>
            </FFormItem>
            <FFormItem prop="udf_id" label="UDF">
                <FSelect
                    v-model="data.udf_id"
                    filterable
                    class="form-edit-input"
                    clearable
                    :options="udfList"
                >
                </FSelect>
                <div class="form-preview-label">{{udfList.find(item => item.value === data.udf_id)?.label || '--'}}</div>
            </FFormItem>
            <FFormItem prop="sql_action" :label="$t('_.采集脚本')">
                <FInput
                    v-model="data.sql_action"
                    :disabled="drawerType === 'edit'"
                    class="form-edit-input"
                    type="textarea"
                    resizeable
                    :placeholder="$t('_.请输入')"
                ></FInput>
                <div class="form-preview-label">{{data.sql_action}}</div>
            </FFormItem>
            <FFormItem :label="$t('functionManagement.devDepartment')" prop="dev_department_name">
                <FSelectCascader v-model="data.dev_department_name" class="form-edit-input" :data="devDivisions" :loadData="loadDevDivisions"
                                 clearable remote emitPath showPath checkStrictly="child" @change="handleDevId" />
                <div class="form-preview-label">{{formatDept(data.dev_department_name)}}</div>
            </FFormItem>
            <FFormItem :label="$t('functionManagement.opsDepartment')" prop="ops_department_name">
                <FSelectCascader v-model="data.ops_department_name" class="form-edit-input" :data="opsDivisions" :loadData="loadOpsDivisions"
                                 clearable remote emitPath showPath checkStrictly="child" @change="handleOpsId" />
                <div class="form-preview-label">{{formatDept(data.ops_department_name)}}</div>
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
                    checkStrictly="child"
                    collapseTags
                    :collapseTagsLimit="3"
                    @change="visSelectChange" />
                <div class="form-preview-label">
                    <FTag v-for="department in data.visibility_department_list" :key="department" type="info" style="margin: 0 5px 5px 0;">
                        {{department?.join(' / ') || ''}}
                    </FTag>
                </div>
            </FFormItem>
        </FForm>
        <template #footer>
            <div v-if="drawerType !== 'display'">
                <FButton type="primary" style="margin-right: 16px;" :loading="isSubmitting" @click="submitForm">{{$t('_.确定')}}</FButton>
                <FButton :loading="isSubmitting" @click="handleClose">{{$t('_.取消')}}</FButton>
            </div>
            <div v-else>
                <FButton type="info" style="margin-right: 16px;" :loading="isSubmitting" @click="drawerTypeChange">{{$t('_.编辑')}}</FButton>
                <!-- <FTooltip
                    mode="confirm"
                    :confirmOption="{ okText: '确认删除' }"
                    placement="top"
                    @ok="deleteFunc"
                >
                    <FButton type="danger" :loading="isSubmitting" :disabled="data.status">删除</FButton>
                    <template #title>
                        <div style="width: 200px">确认删除此函数吗？</div>
                    </template>
                </FTooltip> -->
                <!-- <FButton @click="handleClose">取消</FButton> -->
            </div>
        </template>
    </FDrawer>
</template>
<script setup>

import {
    defineProps, defineEmits, computed, ref, unref, onMounted,
} from 'vue';
import {
    useI18n, request,
} from '@fesjs/fes';
import useDepartment from '@/hooks/useDepartment';
import useDivisions from '@/hooks/useDivisions';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { COMMON_REG } from '@/assets/js/const';
import { cloneDeep } from 'lodash-es';
import { FMessage, FModal } from '@fesjs/fes-design';

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
    tempId: {
        type: Number,
    },
    show: {
        type: Boolean,
        required: true,
    },
    // edit 编辑 display 查看
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
const data = ref({});
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
    display: $t('_.采集算子详情'),
    edit: $t('_.编辑采集算子'),
    create: $t('_.新增采集算子'),
};
const drawerTitle = computed(() => titleMap[drawerType.value]);

const handleClose = () => {
    drawerShow.value = false;
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
        body.visibility_department_list = Array.isArray(selectVisDate.value) && Array.isArray(selectVisDate.value[0]) ? selectVisDate.value.map((item) => {
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
        body.dev_department_name = Array.isArray(body.dev_department_name) ? body.dev_department_name?.join('/') : '';
        body.ops_department_name = Array.isArray(body.ops_department_name) ? body.ops_department_name?.join('/') : '';
        if (props.tempId) body.template_id = props.tempId;
        const target = drawerType.value === 'edit' ? '/api/v1/projector/imsmetric/template/modify' : '/api/v1/projector/imsmetric/template/create';
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
const udfList = ref([]);
const rules = ref({
    cn_name: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    en_name: [
        {
            required: true,
            trigger: ['change', 'blur'],
            message: $t('common.notEmpty'),
        },
        { pattern: COMMON_REG.EN_NAME_64_SPACE, message: $t('_.请输入英文、数字、下划线、空格') },
    ],
    description: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    sql_action: [{
        required: true,
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
function formatDept(dept) {
    if (Array.isArray(dept) && dept.length > 0) {
        return dept.join(' / ');
    }
    return '- -';
}
const fetchTempDetailByID = async (id) => {
    try {
        const res = await request(`/api/v1/projector/imsmetric/template/${id}`, {}, 'get');
        res.original_list = cloneDeep(res.visibility_department_list);
        res.visibility_department_list = res.visibility_department_list?.map(item => item.name?.split('/') || []) || [];
        res.dev_department_name = res?.dev_department_name?.split('/') || '';
        res.ops_department_name = res?.ops_department_name?.split('/') || '';
        data.value = res;
    } catch (err) {
        data.value = {};
        console.warn(err);
    }
};
onMounted(async () => {
    // 手动初始化科室id
    selectDevDate.value = data.value.dev_department_id;
    selectOpsDate.value = data.value.ops_department_id;
    selectVisDate.value = data.value.visibility_department_list;
    try {
        const res = await request('/api/v1/projector/meta_data/udf/all', {
            page: 0,
            size: 999,
        });
        udfList.value = res.content?.map(item => ({ value: item.id, label: item.name }));
    } catch (err) {
        console.warn(err);
        udfList.value = [];
    }

    data.value = {
        visibility_department_list:
            [
                {
                    id: 0,
                    name: $t('_.全行/全行部门'),
                },
            ].map(item => item.name.split('/')),
    };
    if (props.type !== 'create') {
        await fetchTempDetailByID(props.tempId);
    }
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
