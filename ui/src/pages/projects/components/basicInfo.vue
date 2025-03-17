<template>
    <div class="wd-content-body">
        <ul class="wd-body-menus">
            <li v-if="!showBasicEdit && !isWorkflow" class="wd-body-menu-item" @click="toggleBasicEdit">{{$t('common.edit')}}</li>
            <li class="wd-body-menu-item" @click="toggleHistory">{{$t('myProject.history')}}</li>
            <li class="wd-body-menu-item" @click="toggleProjectAuth">{{$t('common.projectPermission')}}</li>
            <FDropdown v-if="!isWorkflow" trigger="hover" :options="moreMenus" @click="toggleMoreAction">
                <li class="wd-body-menu-item">{{$t('common.moreAction')}}</li>
            </FDropdown>
        </ul>
        <h6 class="wd-body-title base-info-title">{{$t('myProject.basic')}}</h6>
        <!-- 编辑基本信息 -->
        <FForm v-show="showBasicEdit" ref="projectBasicForm" class="wd-form form-rule" labelPosition="right" labelWidth="78px" :model="tempProject" :rules="rules" style="width: 400px;">
            <FFormItem :label="`${$t('label.projectId')}`" prop="project_id">
                <FInput
                    ref="projectIdRef"
                    v-model="tempProject.project_id"
                    disabled
                />
            </FFormItem>
            <FFormItem :label="`${$t('common.projectName')}`" prop="project_name">
                <FInput
                    ref="projectNameRef"
                    v-model="tempProject.project_name"
                    :maxlength="64"
                    showWordLimit
                />
            </FFormItem>
            <FFormItem :label="`${$t('common.cnName')}`" prop="cn_name">
                <FInput
                    ref="cnNameRef"
                    v-model="tempProject.cn_name"
                    :maxlength="128"
                    showWordLimit
                />
            </FFormItem>
            <FFormItem :label="`${$t('label.projectIntro')}`" prop="description">
                <FInput
                    ref="projectIntroRef"
                    v-model="tempProject.description"
                    type="textarea"
                    :maxlength="500"
                    showWordLimit
                />
            </FFormItem>
            <FFormItem :label="`${$t('myProject.owningSubsystem')}`" prop="sub_system_id">
                <FSelect
                    ref="subSystemRef"
                    v-model="tempProject.sub_system_id"
                    clearable
                    filterable
                    valueField="value"
                    labelField="label"
                    :options="subSystemList"
                    :filter="upperCaseFilter"
                    @change="setSubSysName"
                ></FSelect>
            </FFormItem>
            <FFormItem :label="`${$t('common.itemTag')}`" prop="project_label">
                <TagsPanel v-model:tags="tempProject.project_label" />
            </FFormItem>
            <div v-show="showBasicEdit" class="btns">
                <FButton type="primary" :loading="isSaving" :disabled="isSaving" style="margin-right: 16px;" @click="editProject">{{$t('common.save')}}</FButton>
                <FButton :disabled="isSaving" @click="toggleBasicEdit">{{$t('common.cancel')}}</FButton>
            </div>
        </FForm>
        <!-- 显示基本信息 -->
        <div v-show="!showBasicEdit" class="wd-table-list">
            <div class="grid">
                <div class="wd-table-row grid-item-8">
                    <div class="wd-table-header">{{$t('label.projectId')}}</div>
                    <div class="wd-table-detail">
                        <FEllipsis :content="project.project_id || '--'"></FEllipsis>
                        <!-- {{project.project_id || '--'}} -->
                    </div>
                </div>
                <div class="wd-table-row grid-item-8">
                    <div class="wd-table-header">{{$t('common.projectEnName1')}}</div>
                    <div class="wd-table-detail">
                        <FEllipsis :content="project.project_name || '--'"></FEllipsis>
                        <!-- {{project.project_name || '--'}} -->
                    </div>
                </div>
                <div class="wd-table-row grid-item-8">
                    <div class="wd-table-header">{{$t('common.projectCnName1')}}</div>
                    <div class="wd-table-detail">
                        <FEllipsis :content="project.cn_name || '--'"></FEllipsis>
                        <!-- {{project.cn_name || '--'}} -->
                    </div>
                </div>
            </div>
            <div class="grid">
                <div class="wd-table-row grid-item-8">
                    <div class="wd-table-header">{{$t('myProject.owningSubsystem')}}</div>
                    <div class="wd-table-detail">
                        <FEllipsis :content="project.sub_system_name || '--'"></FEllipsis>
                        <!-- {{project.sub_system_name || '--'}} -->
                    </div>
                </div>
                <div class="wd-table-row grid-item-8">
                    <div class="wd-table-header">{{$t('label.projectDesc')}}</div>
                    <div class="wd-table-detail">
                        <FEllipsis :content="project.description || '--'"></FEllipsis>
                        <!-- {{project.description || '--'}} -->
                    </div>
                </div>
                <div class="wd-table-row grid-item-8">
                    <div class="wd-table-header">{{$t('common.itemTag')}}</div>
                    <div class="wd-table-detail project-tags">
                        <FTooltip :content="project.project_label.toString()" :disabled="disableTooltip" placement="top">
                            <div ref="projectTagRef" class="project-tag-list" @mouseover="onMouseOver" @mouseleave="onMouseLeave">
                                <span v-for="(item, index) in project.project_label" :key="index" class="tags-item">{{item}}</span>
                                <div v-if="project.project_label && project.project_label.length === 0">--</div>
                            </div>
                        </FTooltip>
                    </div>
                </div>
            </div>
        </div>
        <!-- 显示编辑历史 -->
        <FDrawer v-model:show="showHistory" :title="$t('myProject.history')" width="800px" @cancel="resetHistoryList">
            <f-table :data="history || []">
                <f-table-column prop="operate_type" :label="$t('myProject.operationType')" min-:width="144" ellipsis></f-table-column>
                <f-table-column prop="content" :label="$t('myProject.operationRemark')" :min-width="176" ellipsis></f-table-column>
                <f-table-column prop="operate_user" :label="$t('myProject.operateUser')" :min-width="120" ellipsis></f-table-column>
                <f-table-column prop="time" :label="$t('myProject.operationTime')" :min-width="180" ellipsis></f-table-column>
                <template #empty>
                    <BPageLoading actionType="emptyInitResult" />
                </template>
            </f-table>
            <div class="table-pagination-container">
                <FPagination
                    v-model:currentPage="historyPagination.current"
                    v-model:pageSize="historyPagination.size"
                    show-size-changer
                    show-total
                    :total-count="historyPagination.total"
                    @change="historyPageChange(false)"
                    @pageSizeChange="historyPageChange(true)"
                ></FPagination>
            </div>
        </FDrawer>
        <!-- 显示权限列表 -->
        <FDrawer v-model:show="showProjectAuth" :title="$t('common.projectAuthorityManagement')" width="800px">
            <div class="wd-add-user">
                <h3 v-if="!isWorkflow" class="wd-add-user-title" :class="{ collapse: showAddUserForm }" @click="toggleAddUserForm">{{$t('personnelManagePage.addUser')}}<DownOutlined /></h3>
                <FForm v-show="showAddUserForm" ref="addUserForm" class="wd-form" labelWidth="70px" style="width: 430px; padding-bottom: 16px;" :model="newUser" :rules="userRules">
                    <FFormItem :label="$t('personnelManagePage.selectUser')" prop="authorized_user">
                        <FSelect v-model="newUser.authorized_user" filterable clearable :options="projectUserList">
                            <!-- <FOption v-for="(item, index) in projectUserList" :key="index" :value="item" :disabled="isProjectUser(item)">{{initialState.userName === item ? `${item}(you)` : item}}</FOption> -->
                        </FSelect>
                    </FFormItem>
                    <FFormItem :label="$t('personnelManagePage.authConfig')" prop="permission">
                        <FCheckboxGroup v-model="newUser.permission" @change="permissionChange">
                            <FCheckbox :value="1">Admin</FCheckbox>
                            <FCheckbox :value="2" :disabled="newUser.permission.includes(1)">{{$t('common.edit')}}</FCheckbox>
                            <FCheckbox :value="3" :disabled="newUser.permission.includes(1)">{{$t('common.run')}}</FCheckbox>
                            <FCheckbox :value="4" disabled>{{$t('common.view')}}</FCheckbox>
                        </FCheckboxGroup>
                    </FFormItem>
                    <FButton type="primary" @click="addAuthUser">{{$t('crossTableCheck.confirmToAdd')}}</FButton>
                </FForm>
            </div>
            <f-table :data="permissionData || []">
                <f-table-column prop="name" :label="$t('common.username')" :width="120" ellipsis></f-table-column>
                <f-table-column v-slot="{ row }" :label="'Admin'" :width="76" ellipsis>
                    <FCheckbox v-model="row.admin" :disabled="!row.enabled" @change="updateRowAuths(row)"></FCheckbox>
                </f-table-column>
                <f-table-column v-slot="{ row }" :label="$t('common.edit')" :width="60" ellipsis>
                    <FCheckbox v-model="row.edit" :disabled="!row.enabled || row.admin" @change="updateRowAuths(row)"></FCheckbox>
                </f-table-column>
                <f-table-column v-slot="{ row }" :label="$t('common.run')" :width="60" ellipsis>
                    <FCheckbox v-model="row.run" :disabled="!row.enabled || row.admin" @change="updateRowAuths(row)"></FCheckbox>
                </f-table-column>
                <f-table-column v-slot="{ row }" :label="$t('common.view')" :width="60" ellipsis>
                    <FCheckbox v-model="row.view" disabled></FCheckbox>
                </f-table-column>
                <f-table-column v-if="isAdmin && !isWorkflow" v-slot="{ row, rowIndex }" :label="$t('common.operate')" :width="104" ellipsis>
                    <ul v-if="!row.enabled" class="wd-table-operate-btns">
                        <li
                            v-for="item in projectAuthBtns"
                            :key="item.value"
                            class="btn-item"
                            :class="{ red: item.isDelete, disabled: item.disabled }"
                            @click="projectAuthHandler(item, row)"
                        >
                            {{item.label}}
                        </li>
                    </ul>
                    <div v-else class="wd-table-operate-btns">
                        <FButton type="primary" @click="updateAuthUser(row)">{{$t('common.ok')}}</FButton>
                        <FButton @click="cancelEdit(row, rowIndex)">{{$t('common.cancel')}}</FButton>
                    </div>
                </f-table-column>
            </f-table>
        </FDrawer>
        <!-- 代码存储配置 -->
        <CodeConfig v-if="showGitBase" v-model:show="showGitBase" :project="project" @updateGit="updateGit"></CodeConfig>
        <!-- 导入项目 -->
        <ImportProjectByDetail v-if="showImport" v-model:show="showImport" :project="project" type="detail" @uploadSucess="updateGit"></ImportProjectByDetail>
        <!-- 导出项目 -->
        <EXportProjectByDetail v-if="showExport" v-model:show="showExport" :project="project" type="detail"></EXportProjectByDetail>
    </div>
</template>
<script setup>

import {
    ref, defineProps, defineEmits, computed, watch,
} from 'vue';
import {
    useI18n, request as FRequest, useModel, useRoute,
} from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { DownOutlined } from '@fesjs/fes-design/icon';
import TagsPanel from '@/components/TagsPanel.vue';
import useProjectHistory from '@/pages/projects/hooks/useProjectHistory';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import useDataSource from '@/pages/projects/hooks/useDataSource';
import { COMMON_REG } from '@/assets/js/const';
import CodeConfig from '@/pages/projects/components/ImExport/codeConfig';
import ImportProjectByDetail from '@/pages/projects/components/ImExport/importProjectByDetail';
import EXportProjectByDetail from '@/pages/projects/components/ImExport/exportProjectByDetail';
import { BPageLoading } from '@fesjs/traction-widget';
import { upperCaseFilter } from '@/common/utils';

const initialState = useModel('@@initialState');
const { t: $t } = useI18n();

const props = defineProps({
    project: {
        type: Object,
        default: () => {},
    },
});


const {
    permissionData,
    roleList,
    getProjectUserData,
    isProjectUser,
    projectUserList,
    getProjectAuthUserList,
    isPermissionCompute,
} = useProjectAuth(props.project.project_id, initialState.userName);

const emit = defineEmits([
    'update:project', // 编辑表单提交更新
    'upateProject', // 向后端接口拉取最新project信息
]);
const overseasVersion = sessionStorage.getItem('overseas_external_version');

// 更多操作
const showGitBase = ref(false);
const showImport = ref(false);
const showExport = ref(false);
const moreMenus = ref(overseasVersion === 'true' ? [
    { label: $t('myProject.importProject'), value: 'import' },
    { label: $t('myProject.downloadProject'), value: 'export' },
] : [
    { label: $t('myProject.codeAddress'), value: 'codeAddress' },
    { label: $t('myProject.importProject'), value: 'import' },
    { label: $t('myProject.downloadProject'), value: 'export' },
]);
const loginUserName = sessionStorage.getItem('firstUserName');
const toggleMoreAction = async (e) => {
    if (e === 'codeAddress') {
        showGitBase.value = true;
    }
    if (e === 'import') {
        const isImport = await isPermissionCompute();
        if (isImport) {
            showImport.value = true;
        } else {
            FMessage.warning($t('_.导入权限不足'));
        }
    }
    if (e === 'export') {
        const isExport = await isPermissionCompute();
        if (isExport) {
            showExport.value = true;
        } else {
            FMessage.warning($t('_.导出权限不足'));
        }
    }
};
// 判断是否为工作流
const route = useRoute();
const isWorkflow = !!route.query.workflowProject;

const tempProject = ref({});

watch(() => props.project, (cur, pre) => {
    tempProject.value = JSON.parse(JSON.stringify(cur));
}, { immediate: true, deep: true });

const showBasicEdit = ref(false);
const toggleBasicEdit = () => {
    showBasicEdit.value = !showBasicEdit.value;
};

// 获取子系统
const { subSystemList } = useDataSource(['subSystemList']);

const setSubSysName = (val) => {
    tempProject.value.sub_system_name = val
        ? subSystemList.value.find(v => v.value === val)?.label
        : '';
};

const projectBasicForm = ref(null);

const isSaving = ref(false);
const editProject = async () => {
    try {
        if (isSaving.value) {
            return;
        }
        isSaving.value = true;
        const result = await projectBasicForm.value.validate();
        console.log('表单验证成功: ', result);
        const {
            project_id,
            project_name,
            cn_name,
            description,
            project_label,
            sub_system_id,
            sub_system_name,
        } = tempProject.value;
        await FRequest(
            'api/v1/projector/project/modify',
            {
                project_id,
                project_name,
                cn_name,
                description,
                project_label,
                sub_system_id,
                sub_system_name,
            },
        );
        FMessage.success($t('toastSuccess.editSuccess'));
        showBasicEdit.value = false;
        isSaving.value = false;
        console.log(tempProject);
        emit('update:project', tempProject.value);
    } catch (err) {
        console.log('提交失败: ', err);
        isSaving.value = false;
        FMessage.error(err?.data?.message || $t('toastError.submitFail'));
    }
};
const updateGit = () => {
    emit('upateProject');
};
const rules = ref({
    project_name: [{
        required: true,
        min: 1,
        max: 50,
        message: $t('common.notEmpty'),
    }, { pattern: COMMON_REG.EN_NAME_64, message: $t('myProject.projectEnNameRegTips') }],
    description: [{
        required: true,
        min: 1,
        max: 500,
        message: $t('common.notEmpty'),
    }],
});

const {
    history,
    historyPagination,
    historyPageChange,
    resetHistoryList,
    getProjectHistory,
} = useProjectHistory(props.project.project_id);

const showHistory = ref(false);
const toggleHistory = () => {
    showHistory.value = !showHistory.value;
    getProjectHistory();
};

const showProjectAuth = ref(false);
const toggleProjectAuth = () => {
    showProjectAuth.value = !showProjectAuth.value;
    getProjectUserData();
    setTimeout(() => {
        getProjectAuthUserList();
    }, 600);
};

// 判断是否为项目的管理员
const isAdmin = computed(() => roleList.value.includes(1) || roleList.value.includes('1'));

const projectAuthBtns = ref([
    {
        label: $t('common.edit'),
        value: 'editUser',
        disabled: true,
    },
    {
        label: $t('common.delete'),
        value: 'deleteUser',
        disabled: true,
    },
]);

if (isAdmin) {
    // 管理员才有删除按钮
    projectAuthBtns.value = [
        {
            label: $t('common.edit'),
            value: 'editUser',
        },
        {
            label: $t('common.delete'),
            value: 'deleteUser',
            isDelete: true,
        },
    ];
}

const projectAuthHandler = (btn, data) => {
    // 判断是否为项目管理员
    if (!isAdmin) {
        return;
    }
    switch (btn.value) {
        case 'editUser':
            if (`${initialState.userName}(you)` === data.name) return FMessage.error($t('common.yourself'));
            data.enabled = true;
            // 临时存储数据，取消的时候回填
            data.temp = JSON.parse(JSON.stringify(data));
            break;
        case 'deleteUser':
            // 不能删除自己
            if (`${initialState.userName}(you)` === data.name) return FMessage.error($t('common.yourself'));
            // 不能删除项目创建者
            if (data.name === props.project.create_user) return FMessage.error($t('common.deleteCreateUserTips'));
            FModal.confirm({
                title: $t('common.prompt'),
                content: $t('common.deleteUser', { name: data.name }),
                onOk: async () => {
                    try {
                        await FRequest('api/v1/projector/project_user/delete', {
                            project_id: props.project.project_id,
                            project_user: data.name,
                        });
                        getProjectUserData();
                    } catch (err) {
                        console.warn(err);
                    }
                },
            });
            break;
        default:
            break;
    }
};

const showAddUserForm = ref(false);
const userRules = ref({
    authorized_user: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    permission: [{
        required: true,
        min: 1,
        type: 'array',
        message: $t('common.notEmpty'),
    }],
});
const newUser = ref({
    permission: [4],
});

const toggleAddUserForm = () => {
    showAddUserForm.value = !showAddUserForm.value;
};

const addUserForm = ref(null);
// 新增和编辑项目用户
const addAuthUser = async () => {
    try {
        console.log(addUserForm.value, newUser.value);
        await addUserForm.value.validate();
        await FRequest('api/v1/projector/project_user', {
            project_id: props.project.project_id,
            project_user: newUser.value.authorized_user,
            project_permissions: newUser.value.permission.includes(1) ? [1] : newUser.value.permission,
        }, { method: 'PUT' });
        getProjectUserData();
        FMessage.success($t('common.successfulOperate'));
        // 清空表单
        await addUserForm.value.resetFields();
    } catch (err) {
        console.warn(err);
        // if (!err.errorFields) {
        //     FMessage.warn(err.message || (err.data ? err.data.message : err));
        // }
    }
};

const cancelEdit = (data, index) => {
    // data = Object.assign({}, data.temp);
    // data.enabled = false;
    // delete data.temp;
    console.log(data);
    permissionData.value[index] = Object.assign({}, data.temp, { enabled: false });
};

const updateAuthUser = async (data) => {
    try {
        console.log(data);
        await FRequest('api/v1/projector/project_user/modify', {
            project_id: props.project.project_id,
            project_user: data.name,
            project_permissions: data.permission.includes(1) ? [1] : data.permission,
        });
        FMessage.success($t('common.successfulOperate'));
        data.enabled = false;
    } catch (err) {
        console.warn(err);
    }
};

const transferAuths = (value) => {
    // 如果选择1则其他都都默认选择
    if (value.includes(1)) {
        return [1, 2, 3, 4];
    }
    return value;
};

// 项目用户权限变动
const permissionChange = (value) => {
    newUser.value.permission = transferAuths(value);
};

const projectTagRef = ref(null);
const projectTagBoxRef = ref(null);
const disableTooltip = ref(true); // 是否显示tooltip
const onMouseOver = () => {
    const target = projectTagRef.value;
    const parentWidth = target.parentNode.offsetWidth;
    const targettWidth = target.offsetWidth;
    disableTooltip.value = parentWidth - 50 > targettWidth;
};
// 隐藏
const onMouseLeave = () => {
    disableTooltip.value = true;
};

const updateRowAuths = (row) => {
    console.log(row);
    const {
        admin, edit, run, view,
    } = row;
    const permission = [4];
    if (admin) {
        row.admin = true;
        row.edit = true;
        row.run = true;
        row.view = true;
        permission.unshift(3);
        permission.unshift(2);
        permission.unshift(1);
    }
    if (edit) {
        permission.unshift(2);
    }
    if (run) {
        permission.unshift(3);
    }
    console.log(permission);
    row.permission = permission;
};


</script>
<style lang="less" scoped>
@import '@/style/varible.less';
.history-table {
    width: 100%!important;
}
.wd-add-user{
    margin-bottom: 16px;
    background: #F7F7F8;
    border-radius: 4px;
    .wd-add-user-title{
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0 16px;
        line-height: 54px;
        color: @black-color;
        cursor: pointer;
        .fes-design-icon{
            transition: transform ease .2s;
        }
        &.collapse{
            .fes-design-icon{
                transform: rotate(180deg);
            }
        }
    }
    .fes-form{
        padding: 0 16px;
        .fes-checkbox{
            margin-right: 30px;
            &:last-child{
                margin-right: 0;
            }
        }
    }
}
.form-rule{
    .btns{
        padding-left: 16px;
    }
}
.grid {
    position: relative;
    display: flex;
    box-sizing: border-box;
    margin-bottom: 16px;
    .grid-item-8 {
        position: relative;
        display: block;
        box-sizing: border-box;
        flex: 0 0 33.33333333%;
        max-width: 33.33333333%;
        min-height: 1px;
        padding-right: 16px;
    }
    &:last-child {
        padding-right: 0;
    }
}
.wd-content .wd-content-body .wd-table-list .wd-table-row {
    margin-bottom: 0;
}
.wd-content .wd-content-body .wd-table-list .wd-table-row .wd-table-header {
    min-width: 72px;
    color: #646670;
    align-items: center;
    display: inline-flex;
    flex-shrink: 0;
    height: 22px;
    justify-content: flex-start;
}

.wd-table-detail{
    overflow: hidden;
    margin-left: 16px;
    align-items: center;
    display: flex;
    flex: 1;
    min-height: 22px;
    position: relative;
}
.project-tags .tags-item {
    overflow: hidden;
    text-overflow: ellipsis;
}
.project-tag-list {
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    height: 26px;
}
.wd-content .wd-content-body .wd-table-list .grid {
    &:last-child {
        margin-bottom: 0;
    }
}
.wd-content .wd-content-body .base-info-title {
    margin-bottom: 16px;
}
</style>
