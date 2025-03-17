<template>
    <div>
        <!-- 模板列表 -->
        <div v-for="(template, index) in templateList" :key="template.id" class="template-list" :style="{ height: isOpenTemplateList[index] ? '' : '56px' }">
            <div class="template-header">
                <div class="template-header-left">
                    <UpOutlined v-show="isOpenTemplateList[index] && !template.isEditing" color="#93949B" @click="closeTemplatePanel(index)" />
                    <DownOutlined v-show="!isOpenTemplateList[index] && !template.isEditing" color="#93949B" @click="openTemplatePanel(index)" />
                    <div class="template-name">
                        {{template.name}}
                    </div>
                </div>
                <div v-if="!template.isEditing" class="template-operate-item">
                    <div class="edit-button" style="cursor: pointer;" @click="editTemplate(template, index)">{{$t('common.edit')}}</div>
                    <div class="delete-button" style="cursor: pointer;" @click="deleteTemplate(template)">{{$t('common.delete')}}</div>
                </div>
            </div>
            <div v-if="isOpenTemplateList[index]" style="margin-top: 16px;">
                <FForm :ref="(e) => templateRefList[index] = e" :model="template" :labelPosition="template.isEditing ? 'right' : 'left'" labelWidth="96" :rules="template.isEditing ? rules : {}" :class="{ detail: !template.isEditing }">
                    <FFormItem v-if="template.isEditing" prop="name" :label="$t('myProject.templateName')">
                        <FInput
                            v-if="template.isEditing"
                            v-model="template.name"
                            :placeholder="$t('common.pleaseEnter')"
                            clearable
                        />
                        <span v-else>{{template.name}}</span>
                    </FFormItem>
                    <FFormItem :label="$t('myProject.businessDomain')" prop="business_domain">
                        <FSelect
                            v-if="template.isEditing"
                            v-model="template.business_domain"
                            :options="businessDomainList"
                            :placeholder="$t('common.pleaseSelect')"
                            filterable
                            clearable
                        />
                        <span v-else>{{template.business_domain}}</span>
                    </FFormItem>
                    <FFormItem :label="$t('myProject.subSystem')" prop="sub_system_id">
                        <FSelect
                            v-if="template.isEditing"
                            v-model="template.sub_system_id"
                            :options="subSystemList"
                            :placeholder="$t('common.pleaseSelect')"
                            filterable
                            :filter="upperCaseFilter"
                            clearable
                        />
                        <span v-else>{{subSystemList.find((item) => ( item.subSystemId === template.sub_system_id))?.label}}</span>
                    </FFormItem>
                    <FFormItem :label="$t('myProject.importanceLevel')" prop="bus_res_lvl">
                        <FSelect
                            v-if="template.isEditing"
                            v-model="template.bus_res_lvl"
                            :options="busResLvlList"
                            :placeholder="$t('common.pleaseSelect')"
                            filterable
                            clearable
                        />
                        <span v-else>{{template.bus_res_lvl}}</span>
                    </FFormItem>
                    <FFormItem :label="$t('myProject.planStartTime')" prop="plan_start_time">
                        <FTimePicker
                            v-if="template.isEditing"
                            v-model="template.plan_start_time"
                            format="HH:mm"
                            :placeholder="$t('common.pleaseSelect')"
                        />
                        <span v-else>{{template.plan_start_time}}</span>
                    </FFormItem>
                    <FFormItem :label="$t('myProject.planFinishTime')" prop="plan_finish_time">
                        <FTimePicker
                            v-if="template.isEditing"
                            v-model="template.plan_finish_time"
                            format="HH:mm"
                            :placeholder="$t('common.pleaseSelect')"
                        />
                        <span v-else>{{template.plan_finish_time}}</span>
                    </FFormItem>
                    <FFormItem :label="$t('myProject.lastStartTime')" prop="last_start_time">
                        <FTimePicker
                            v-if="template.isEditing"
                            v-model="template.last_start_time"
                            format="HH:mm"
                            :placeholder="$t('common.pleaseSelect')"
                        />
                        <span v-else>{{template.last_start_time}}</span>
                    </FFormItem>
                    <FFormItem :label="$t('myProject.lastFinishTime')" prop="last_finish_time">
                        <FTimePicker
                            v-if="template.isEditing"
                            v-model="template.last_finish_time"
                            format="HH:mm"
                            :placeholder="$t('common.pleaseSelect')"
                        />
                        <span v-else>{{template.plan_finish_time}}</span>
                    </FFormItem>
                    <!-- 开发科室 -->
                    <FFormItem :label="$t('myProject.devDepartment')" prop="dev_department_name">
                        <FSelectCascader
                            v-if="template.isEditing"
                            v-model="template.dev_department_name"
                            :data="devDivisions"
                            :loadData="loadDevDivisions"
                            clearable
                            remote
                            emitPath
                            showPath
                            checkStrictly="child"
                            @change="handleDevId"
                        />
                        <span v-else>{{template.dev_department_name}}</span>
                    </FFormItem>
                    <!-- 运维科室 -->
                    <FFormItem :label="$t('myProject.opsDepartment')" prop="ops_department_name">
                        <FSelectCascader
                            v-if="template.isEditing"
                            v-model="template.ops_department_name"
                            :data="opsDivisions"
                            :loadData="loadOpsDivisions"
                            clearable
                            remote
                            emitPath
                            showPath
                            checkStrictly="child"
                            @change="handleOpsId"
                        />
                        <span v-else>{{template.ops_department_name}}</span>
                    </FFormItem>
                </FForm>
                <div v-if="template.isEditing">
                    <FButton type="primary" :style="{ marginRight: '16px' }" @click="confirmEditTemplate(template, index)">{{$t('common.save')}}</FButton>
                    <FButton @click="cancelEditTemplate(template, index)">{{$t('common.cancel')}}</FButton>
                </div>
                <div v-if="!template.isEditing" class="collapse-template" @click="closeTemplatePanel(index)">{{$t('common.collapseTemplate')}}</div>
            </div>
        </div>
        <!-- 新增模板 -->
        <div class="edit-template" :style="{ height: openAdd ? '' : '56px' }">
            <div class="header-bar">
                <FButton type="link" :class="openAdd ? 'open-title' : 'close-title'" @click="openAddBar">
                    <template v-if="!openAdd" #icon><PlusCircleOutlined /></template>{{$t('myProject.createNewTemplate')}}
                </FButton>
            </div>
            <div v-if="openAdd" style="margin-top: 16px;">
                <FForm
                    ref="templateNameRef"
                    labelPosition="right"
                    labelWidth="96"
                    :model="templateModel"
                    :rules="rules"
                >
                    <FFormItem :label="$t('myProject.templateName')" prop="name">
                        <FInput
                            v-model="templateModel.name"
                            :placeholder="$t('common.pleaseEnter')"
                            clearable
                        />
                    </FFormItem>
                    <FFormItem :label="$t('myProject.businessDomain')" prop="business_domain">
                        <FSelect
                            v-model="templateModel.business_domain"
                            :options="businessDomainList"
                            :placeholder="$t('common.pleaseSelect')"
                            filterable
                            clearable
                        />
                    </FFormItem>
                    <FFormItem :label="$t('myProject.subSystem')" prop="sub_system_id">
                        <FSelect
                            v-model="templateModel.sub_system_id"
                            :options="subSystemList"
                            :placeholder="$t('common.pleaseSelect')"
                            filterable
                            :filter="upperCaseFilter"
                            clearable
                        />
                    </FFormItem>
                    <FFormItem :label="$t('myProject.importanceLevel')" prop="bus_res_lvl">
                        <FSelect
                            v-model="templateModel.bus_res_lvl"
                            :options="busResLvlList"
                            :placeholder="$t('common.pleaseSelect')"
                            filterable
                            clearable
                        />
                    </FFormItem>
                    <FFormItem :label="$t('myProject.planStartTime')" prop="plan_start_time">
                        <FTimePicker
                            v-model="templateModel.plan_start_time"
                            format="HH:mm"
                            :placeholder="$t('common.pleaseSelect')"
                        />
                    </FFormItem>
                    <FFormItem :label="$t('myProject.planFinishTime')" prop="plan_finish_time">
                        <FTimePicker
                            v-model="templateModel.plan_finish_time"
                            format="HH:mm"
                            :placeholder="$t('common.pleaseSelect')"
                        />
                    </FFormItem>
                    <FFormItem :label="$t('myProject.lastStartTime')" prop="last_start_time">
                        <FTimePicker
                            v-model="templateModel.last_start_time"
                            format="HH:mm"
                            :placeholder="$t('common.pleaseSelect')"
                        />
                    </FFormItem>
                    <FFormItem :label="$t('myProject.lastFinishTime')" prop="last_finish_time">
                        <FTimePicker
                            v-model="templateModel.last_finish_time"
                            format="HH:mm"
                            :placeholder="$t('common.pleaseSelect')"
                        />
                    </FFormItem>
                    <!-- 开发科室 -->
                    <FFormItem :label="$t('myProject.devDepartment')" prop="dev_department_name">
                        <FSelectCascader
                            v-model="templateModel.dev_department_name"
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
                    <FFormItem :label="$t('myProject.opsDepartment')" prop="ops_department_name">
                        <FSelectCascader
                            v-model="templateModel.ops_department_name"
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
                </FForm>
                <div>
                    <FButton type="primary" style="margin-right: 22px" @click="confirmAddTemplate">{{$t('myProject.confirmCreate')}}</FButton>
                    <FButton @click="cancelAddTemplate">{{$t('common.cancel')}}</FButton>
                </div>
            </div>
        </div>
        <FModal
            v-model:show="showDeleteConfirmModal"
            :title="$t('common.delete')"
            displayDirective="if"
            @ok="confirmDeleteTemplate"
        >
            <div>{{$t('myProject.confirmDelete') + currentTemplate.name + '?'}}</div>
        </FModal>
    </div>
</template>
<script setup>
import {
    ref, onMounted, computed, defineProps, provide,
} from 'vue';
import {
    DownOutlined, UpOutlined, PlusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    useI18n, useRouter, useRoute,
} from '@fesjs/fes';
import { FMessage, FTimePicker } from '@fesjs/fes-design';
import ExecutionParams from '@/components/rules/ExecutionParams.vue';
import { cloneDeep } from 'lodash-es';
import useDepartment from '@/hooks/useDepartment';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { upperCaseFilter } from '@/common/utils';
import {
    getAppInfoTemplateList, newAddAppInfoTemplate, editAppInfoTemplate, deleteAppInfoTemplate, getBusinessDomainList,
} from './api';

const { t: $t } = useI18n();
const router = useRouter();
const route = useRoute();
const businessDomainList = ref([]);

const props = defineProps({
    subSystemList: {
        type: Array,
        default: [],
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
    selectDevDate,
    selectOpsDate,
    handleDevId,
    handleOpsId,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData);
const busResLvlList = ref([
    { label: 'S', value: 'S' },
    { label: 'A', value: 'A' },
    { label: 'B', value: 'B' },
    { label: 'C', value: 'C' },
]);

const templateList = ref([]);
const templateRefList = ref([]);
const rules = computed(() => ({
    name: [
        {
            required: true,
            message: $t('common.pleaseEnter'),
            trigger: ['blur', 'input'],
        },
    ],
    business_domain: [
        {
            required: true,
            message: $t('common.pleaseSelect'),
            trigger: ['blur', 'change'],
        },
    ],
    sub_system_id: [
        {
            required: true,
            message: $t('common.pleaseSelect'),
            trigger: ['blur', 'change'],
        },
    ],
    bus_res_lvl: [
        {
            required: true,
            message: $t('common.pleaseSelect'),
            trigger: ['blur', 'change'],
        },
    ],
    plan_start_time: [
        {
            required: true,
            message: $t('common.pleaseSelect'),
            trigger: ['blur', 'change'],
        },
    ],
    plan_finish_time: [
        {
            required: true,
            message: $t('common.pleaseSelect'),
            trigger: ['blur', 'change'],
        },
    ],
    last_start_time: [
        {
            required: true,
            message: $t('common.pleaseSelect'),
            trigger: ['blur', 'change'],
        },
    ],
    last_finish_time: [
        {
            required: true,
            message: $t('common.pleaseSelect'),
            trigger: ['blur', 'change'],
        },
    ],
    dev_department_name: [
        {
            required: true,
            type: 'array',
            message: $t('common.pleaseSelect'),
            trigger: ['blur', 'change'],
        },
    ],
    ops_department_name: [
        {
            required: true,
            type: 'array',
            message: $t('common.pleaseSelect'),
            trigger: ['blur', 'change'],
        },
    ],
}));

// 模板的折叠展开
const isOpenTemplateList = ref([]);
const openTemplatePanel = (index) => {
    isOpenTemplateList.value[index] = true;
};
// 关闭模板
const closeTemplatePanel = (index) => {
    templateList.value[index].isEditing = false;
    isOpenTemplateList.value[index] = false;
};

// 查询所有模版
const getTemplateList = async () => {
    try {
        templateList.value = await getAppInfoTemplateList(route.query.projectId);
        isOpenTemplateList.value = new Array(templateList.value.length).fill(false);
    } catch (error) {
        console.error(error);
    }
};

const templateModel = ref({});
const resetTemplateModel = () => {
    templateModel.value = {};
};

// 模板新增
const templateNameRef = ref(null);
const openAdd = ref(false);
const cancelAddTemplate = () => {
    templateModel.value = {};
    openAdd.value = false;
};

// 编辑某一个模版时，取消掉其他所有的模版的编辑
const cancelAllEdit = () => {
    templateList.value.forEach((item) => {
        item.isEditing = false;
    });
};

const openAddBar = () => {
    // cancelAllEdit();
    resetTemplateModel();
    openAdd.value = true;
};

// 确定添加模版
const confirmAddTemplate = async () => {
    try {
        await templateNameRef.value.validate();
        const params = {
            project_id: route.query.projectId,
            ...templateModel.value,
        };
        console.log(templateModel.value);
        params.dev_department_id = selectDevDate.value;
        params.dev_department_name = templateModel.value.dev_department_name.join('/');
        params.ops_department_id = selectOpsDate.value;
        params.ops_department_name = templateModel.value.ops_department_name.join('/');
        await newAddAppInfoTemplate(params);
        FMessage.success($t('toastSuccess.addSuccess'));
        resetTemplateModel();
        getTemplateList();
        openAdd.value = false;
    } catch (err) {
        console.error(err);
    }
};

// 删除模版
const showDeleteConfirmModal = ref(false);
const currentTemplate = ref({});

const deleteTemplate = (template) => {
    currentTemplate.value = template;
    showDeleteConfirmModal.value = true;
};

const confirmDeleteTemplate = async () => {
    try {
        await deleteAppInfoTemplate(currentTemplate.value.id);
        FMessage.success($t('toastSuccess.deleteSuccess'));
        getTemplateList();
        showDeleteConfirmModal.value = false;
    } catch (error) {
        console.error(error);
    }
};

// 编辑模版
const curCachedTemplateDataList = ref([]);
const editTemplate = async (template, index) => {
    curCachedTemplateDataList.value[index] = cloneDeep(template);
    isOpenTemplateList.value[index] = true;
    template.isEditing = true;
    template.dev_department_name = template.dev_department_name.split('/');
    template.ops_department_name = template.ops_department_name.split('/');
};

// 确认编辑模版
const confirmEditTemplate = async (template, index) => {
    try {
        console.log('template', template);
        await templateRefList.value[index].validate();
        const params = { ...template };
        params.dev_department_id = Array.isArray(selectDevDate.value) ? template.dev_department_id : selectDevDate.value;
        params.dev_department_name = template.dev_department_name.join('/');
        params.ops_department_id = Array.isArray(selectOpsDate.value) ? template.ops_department_id : selectOpsDate.value;
        params.ops_department_name = template.ops_department_name.join('/');
        await editAppInfoTemplate(params);
        FMessage.success($t('toastSuccess.editSuccess'));
        getTemplateList();
    } catch (err) {
        console.error(err);
    }
};

const cancelEditTemplate = (template, index) => {
    templateList.value[index] = cloneDeep(curCachedTemplateDataList.value[index]);
    isOpenTemplateList.value[index] = false;
};


onMounted(async () => {
    try {
        await getTemplateList();
        businessDomainList.value = (await getBusinessDomainList()).map(item => ({ label: item.appdomain_cnname, value: item.appdomain_cnname }));
    } catch (error) {
        console.warn(error);
    }
});

</script>
<style lang="less" scoped>
@import "@/style/varible";
.template-list {
    margin-bottom: 16px;
    background: rgb(247, 247, 248);
    border: 1px solid #F1F1F2;
    border-radius: 4px;
    padding: 16px;
    .template-header {
        display: flex;
        .template-header-left {
            display: flex;
            align-items: center;
            flex-grow: 1;
        }
        .template-name {
            font-size: 16px;
            font-weight: 500;
            margin-left: 10px;
        }
        .template-operate-item {
            display: flex;
            .edit-button {
               color: #5384FF;
               margin-right: 16px;
            }
            .delete-button {
                color: #FF4D4F;
            }
        }
    }
}
.collapse-template {
    cursor: pointer;
    color: #5384FF;
    background-color: rgb(247, 247, 248);
    padding: 0 16px 16px;
    align-items: center;
}
.create-name-form {
    background: #F7F7F8;
    padding: 0 16px 16px;
    /* .name-label-form{
        margin-top: 16px;
    } */
}
.edit-template {
  font-family: PingFangSC-Medium;
  background: rgb(247, 247, 248);
  overflow: hidden;
  border: 1px solid #F1F1F2;
  border-radius: 4px;
  padding: 11px 16px 16px;
}
.close-title {
    font-family: PingFangSC-Regular;
    font-size: 14px;
    font-weight: 400;
    color: #5384FF;
    letter-spacing: 0;
    padding-left: 0px;
}
.open-title {
    font-family: PingFangSC-Medium;
    color: #0F1222;
    font-weight: 500;
    letter-spacing: 0;
    padding-left: 0px;
}
:deep(.fes-form-item-content .fes-input){
    max-width: none !important;
}
:deep(.fes-form-item-content .fes-select){
    max-width: none !important;
}
.detail {
    padding-left: 16px;
    :deep(.fes-form-item) {
        margin-bottom: 8px;
    }
}
</style>
