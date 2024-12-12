<template>
    <div class="wd-content">
        <div v-if="showHeader" class="wd-project-header">
            <LeftOutlined class="back" @click="backToProjectList" />
            <div class="name">{{$t('myProject.executeParamsTemplate')}}</div>
        </div>
        <!-- 模板列表 -->
        <div v-for="(template,index) in templateList" :key="template.id" class="template-pag-style wd-content-body template-list rule-detail-form" :class="{ edit: template.isEditing, selected: template.isSelected, isEmbed: isEmbed }">
            <ul class="wd-body-menus">
                <template v-if="(isOperate || route.query.isOperate) && !template.isEditing">
                    <li class="template-menu-item">
                        <span style="cursor: pointer;" @click="editTemplate(template, index)">{{$t('common.edit')}}</span>
                    </li>
                    <li class="last-delete-menu-item template-menu-item">
                        <span style="cursor: pointer;" @click="deleteTemplate(template)">{{$t('common.delete')}}</span>
                    </li>
                </template>
                <UpOutlined v-show="isOpenTemplateList[index]" :class="{ isEmbed: isEmbed }" class="template-fold" @click="closeTemplatePanel(index)" />
                <DownOutlined v-show="!isOpenTemplateList[index]" :class="{ isEmbed: isEmbed }" class="template-fold" @click="openTemplatePanel(index)" />
            </ul>
            <div class="template-name-style" :class="{ isEmbed: isEmbed }" :style="{ margin: isOpenTemplateList[index] ? '0' : '0 0 -6px' }">
                <FRadio v-if="isEmbed && !isOperate" v-model="templateList[index].isSelected" @change="selectTemplate($event,index)"></FRadio>
                {{template.ref}}
            </div>
            <div v-if="isOpenTemplateList[index]">
                <template v-if="template.isEditing">
                    <FForm :ref="(e) => ele['form_header' + index] = e" class="name-form" :model="template"
                           :rules="excutionParamsRules" labelPosition="right">
                        <FFormItem prop="name" labelClass="name-label-form" :label="template.isEditing ? $t('myProject.templateName') : ''">
                            <FInput
                                v-model="template.name"
                                class="form-edit-input"
                                :placeholder="$t('common.pleaseEnter')"
                            />
                        </FFormItem>
                    </FForm>
                </template>
                <ExecutionParams :ref="(e) => ele['form_body' + index] = e" v-model:info="templateList[index]" :isBatch="isBatch || route.query.isBatch"
                                 :isEmbed="isEmbed" :mode="template.isEditing ? 'edit' : 'display'"></ExecutionParams>
                <div v-if="template.isEditing" :style="{ marginTop: '-16px', padding: '0 16px 16px', backgroundColor: '#F7F7F8' }">
                    <FButton type="primary" :style="{ marginRight: '16px' }" @click="confirmEditTemplate(template, index)">{{$t('common.save')}}</FButton>
                    <FButton @click="cancelEditTemplate(template, index)">{{$t('common.cancel')}}</FButton>
                </div>
                <div v-if="!template.isEditing" class="collapse-template" @click="closeTemplatePanel(index)">{{$t('common.collapseTemplate')}}</div>
            </div>
        </div>
        <!-- 新增模板 -->
        <div class="wd-content-body edit-template" :class="{ isEmbed: isEmbed , isOpen: openAdd }" :style="{ height: openAdd ? '' : '72px' }">
            <div class="header-bar" style="margin-top:-4px">
                <FButton type="link" :class="openAdd ? 'open-title' : 'close-title'" @click="openAddBar">
                    <template v-if="!openAdd" #icon><PlusCircleOutlined /></template>{{$t('myProject.createNewTemplate')}}
                </FButton>
            </div>
            <template v-if="openAdd">
                <FForm
                    ref="templateNameRef"
                    labelPosition="left"
                    labelWidth="68"
                    :model="templateModel"
                    :rules="rules"
                    class="create-name-form"
                >
                    <FFormItem :label="$t('myProject.templateName')" prop="name" labelClass="name-label-form">
                        <FInput
                            v-model="templateModel.name"
                            class="form-edit-input"
                            :placeholder="$t('common.pleaseEnter')"
                        />
                    </FFormItem>
                </FForm>
                <ExecutionParams ref="editTemplateRef" v-model:info="templateModel" style="padding: 0px" :isBatch="isBatch || route.query.isBatch" :isEmbed="isEmbed" mode="edit"></ExecutionParams>
                <FButton type="primary" :style="{ marginRight: '16px',marginTop: '-16px' }" @click="confirmAddTemplate">{{$t('myProject.confirmCreate')}}</FButton>
                <FButton :style="{ marginTop: '-16px' }" @click="cancelTemplate">{{$t('common.cancel')}}</FButton>
            </template>
            <FModal
                v-model:show="showDeleteConfirmModal"
                :title="$t('common.delete')"
                displayDirective="if"
                @ok="confirmDeleteTemplate"
            >
                <div>{{$t('myProject.confirmDelete') + currentTemlate.name + '?'}}</div>
            </FModal>
        </div>
    </div>
</template>
<script setup>
import {
    ref, onMounted, computed, defineProps, provide,
} from 'vue';
import {
    LeftOutlined, DownOutlined, UpOutlined, CheckOutlined, PlusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    useI18n, useRouter, useRoute, useModel,
} from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import ExecutionParams from '@/components/rules/ExecutionParams.vue';
import { cloneDeep } from 'lodash-es';
import useRuleBasicData from '@/hooks/useRuleBasicData';
import {
    fetchAddTemplate, fetchModifyTemplate, fetchDeleteTemplate, fetchTemplateList,
} from './api';


const props = defineProps({
    showHeader: {
        type: Boolean,
        default: true,
    },
    // 当前选择的执行参数名称
    curExeName: {
        type: String,
        default: '',
    },
    // 是否被嵌套
    isEmbed: {
        type: Boolean,
        default: false,
    },
    // 是否是批量
    isBatch: {
        type: Boolean,
        default: false,
    },
    // 是否可以对执行参数模板进行编辑删除
    isOperate: {
        type: Boolean,
        default: false,
    },
});
provide('isTemplateCall', true);
const { t: $t } = useI18n();
const router = useRouter();
const route = useRoute();
const initialState = useModel('@@initialState');

const backToProjectList = () => {
    if (window.history?.state?.back) {
        // 从项目里正常跳转进来
        router.back();
    } else {
        // 复制url直接进来
        router.replace('/projects');
    }
};

const rules = computed(() => ({
    name: [
        {
            required: true,
            message: $t('common.pleaseEnter'),
            trigger: ['blur', 'input'],
        },
    ],
}));
const templateList = ref();

// 执行参数模板的折叠展开
const isOpenTemplateList = ref([]);
const openTemplatePanel = (index) => {
    isOpenTemplateList.value[index] = true;
};

const closeTemplatePanel = (index) => {
    templateList.value[index].isEditing = false;
    isOpenTemplateList.value[index] = false;
};

// 模板操作
const selectTemplate = (value, index) => {
    if (!props.isEmbed) {
        return;
    }
    templateList.value.forEach((item, tindex) => {
        if (index === tindex) {
            item.isSelected = value;
        } else if (value) {
            item.isSelected = false;
        }
    });
};
// 去噪参数去json化
const parseNoiseEliminationManagement = (data) => {
    data.forEach((e) => {
        e.business_date = `[${e.business_date}]`;
        e.business_date = JSON.parse(e.business_date);
        e.noise_norm_ratio = JSON.parse(e.noise_norm_ratio);
    });
    return data;
};
const getTemplateList = async () => {
    try {
        const params = {
            project_id: route.query.projectId,
        };
        const data = await fetchTemplateList(params);
        if (data.data) {
            templateList.value = [];
            isOpenTemplateList.value = [];
            data.data.forEach((item) => {
                const temp = {
                    id: item.execution_parameters_id,
                    name: item.excution_parameters_name ? item.excution_parameters_name : item.name,
                    ref: item.excution_parameters_name ? item.excution_parameters_name : item.name,
                    // 执行参数
                    specify_filter: item.specify_filter,
                    filter: item.filter,
                    source_table_filter: item.source_table_filter,
                    target_table_filter: item.target_table_filter,
                    abort_on_failure: item.abort_on_failure,
                    union_all: item.union_all,
                    abnormal_data_storage: item.abnormal_data_storage,
                    cluster: item.cluster,
                    abnormal_proxy_user: item.abnormal_proxy_user,
                    abnormal_database: item.abnormal_database,
                    // 动态引擎参数，原静态参数
                    specify_static_startup_param: item.specify_static_startup_param,
                    static_execution_parameters: item.static_execution_parameters,
                    // 指标参数
                    upload_rule_metric_value: item.upload_rule_metric_value,
                    upload_abnormal_value: item.upload_abnormal_value,
                    delete_fail_check_result: item.delete_fail_check_result,
                    // 告警参数
                    alert: item.alert,
                    alarm_arguments_execution_parameters: item.alarm_arguments_execution_parameters,
                    noise_elimination_management: parseNoiseEliminationManagement(item.noise_elimination_management),
                    // 执行变量配置
                    execution_variable: item.execution_variable,
                    execution_management: item.execution_management || [],
                    // 高级执行配置
                    advanced_execution: item.advanced_execution,
                    engine_reuse: item.engine_reuse,
                    concurrency_granularity: item.concurrency_granularity,
                    dynamic_partitioning: item.dynamic_partitioning,
                    top_partition: item.top_partition,
                };
                temp.static_execution_parameters.forEach((e) => { e.timestamp = e.parameter_id; });
                temp.alarm_arguments_execution_parameters.forEach((e) => { e.timestamp = e.parameter_id; });
                temp.noise_elimination_management.forEach((e, index) => { e.timestamp = index; });
                temp.execution_management.forEach((e) => { e.timestamp = e.variable_id; });
                templateList.value.push(temp);
                // 初始化状态下模板都为折叠状态
                isOpenTemplateList.value.push(false);
            });
        }
    } catch (error) { console.log('error : ', error); }
};

const templateModel = ref({
    static_execution_parameters: [],
    alarm_arguments_execution_parameters: [],
    noise_elimination_management: [],
    execution_management: [],
    advanced_execution: true,
    dynamic_partitioning: false,
    engine_reuse: true,
    concurrency_granularity: 'split_by:merge',
});

const resetTemplateModel = () => {
    templateModel.value = {
        static_execution_parameters: [],
        alarm_arguments_execution_parameters: [],
        noise_elimination_management: [],
        execution_management: [],
        advanced_execution: true,
        dynamic_partitioning: false,
        engine_reuse: true,
        concurrency_granularity: 'split_by:merge',
    };
};

// 模板新增
const openAdd = ref(false);
const cancelTemplate = () => {
    resetTemplateModel();
    openAdd.value = false;
};

// 编辑某一个模版时，取消掉其他所有的模版的编辑
const cancelAllEdit = () => {
    templateList.value.forEach((item) => {
        item.isEditing = false;
    });
};

const closeAddBar = () => {
    openAdd.value = false;
};
const openAddBar = () => {
    cancelTemplate();
    cancelAllEdit();
    openAdd.value = true;
};

const templateNameRef = ref(null);
const editTemplateRef = ref(null);

// 当多选框值为false时，置空其子项数据
const validTemplateValue = (template) => {
    if (!template.specify_filter) {
        template.source_table_filter = '';
        template.target_table_filter = '';
        template.filter = '';
    }
    if (!template.abnormal_data_storage) {
        template.abnormal_proxy_user = '';
        template.abnormal_database = '';
        template.cluster = '';
    }
    if (!template.specify_static_startup_param) {
        template.static_execution_parameters = [];
    }
    if (!template.alert) {
        template.alarm_arguments_execution_parameters = [];
    }
    if (!template.execution_variable) {
        template.execution_management = [];
    }
    if (!template.advanced_execution) {
        template.engine_reuse = true;
        template.concurrency_granularity = 'split_by:merge';
        template.dynamic_partitioning = false;
        template.top_partition = '';
    }
    return template;
};
// 去噪参数json化
const stringfyNoiseEliminationManagement = (data) => {
    data.forEach((e) => {
        delete e.timestamp;
        e.business_date = JSON.stringify(e.business_date);
        e.business_date = e.business_date.substring(1, e.business_date.length - 1);
        e.noise_norm_ratio = e.noise_norm_ratio.map(item => ({
            output_meta_id: item.output_meta_id,
            output_meta_name: item.output_meta_name,
            check_template: item.check_template,
            compare_type: item.compare_type,
            threshold: item.threshold,
        }));
        e.noise_norm_ratio = JSON.stringify(e.noise_norm_ratio);
    });
    return data;
};
const ele = ref({});
const isAdding = ref(false);
// eslint-disable-next-line complexity
const confirmAddTemplate = async () => {
    try {
        const results = await Promise.all([templateNameRef.value.validate(), editTemplateRef.value.valid()]);
        if (results.includes(false)) {
            return;
        }
        isAdding.value = true;
        validTemplateValue(templateModel.value);
        const noiseDataCache = cloneDeep(templateModel.value.noise_elimination_management) || [];
        const stringNoiseData = stringfyNoiseEliminationManagement(noiseDataCache);
        const params = {
            project_id: route.query.projectId,
            name: templateModel.value.name,
            excution_parameters_name: templateModel.value.name,
            // 执行参数
            specify_filter: templateModel.value.specify_filter || false,
            filter: templateModel.value.filter || '',
            source_table_filter: templateModel.value.source_table_filter || '',
            target_table_filter: templateModel.value.target_table_filter || '',
            abort_on_failure: templateModel.value.abort_on_failure || false,
            union_all: templateModel.value.union_all || false,
            abnormal_data_storage: templateModel.value.abnormal_data_storage || false,
            cluster: templateModel.value.cluster || '',
            abnormal_proxy_user: templateModel.value.abnormal_proxy_user,
            abnormal_database: templateModel.value.abnormal_database || '',
            // 动态引擎，原-静态参数
            specify_static_startup_param: templateModel.value.specify_static_startup_param || false,
            static_execution_parameters: cloneDeep(templateModel.value.static_execution_parameters) || [],
            // 指标参数
            upload_rule_metric_value: templateModel.value.upload_rule_metric_value || false,
            upload_abnormal_value: templateModel.value.upload_abnormal_value || false,
            delete_fail_check_result: templateModel.value.delete_fail_check_result || false,
            // 告警参数
            alert: templateModel.value.alert || false,
            alarm_arguments_execution_parameters: cloneDeep(templateModel.value.alarm_arguments_execution_parameters) || [],
            noise_elimination_management: stringNoiseData,
            // 执行变量配置
            execution_variable: templateModel.value.execution_variable || false,
            execution_management: cloneDeep(templateModel.value.execution_management) || [],
            // 高级执行配置
            advanced_execution: templateModel.value.advanced_execution || false,
            engine_reuse: templateModel.value.engine_reuse,
            concurrency_granularity: templateModel.value.concurrency_granularity,
            dynamic_partitioning: templateModel.value.dynamic_partitioning,
            top_partition: templateModel.value.top_partition,
        };
        params.static_execution_parameters.forEach((item) => { delete item.timestamp; });
        params.alarm_arguments_execution_parameters.forEach((item) => { delete item.timestamp; });
        params.execution_management.forEach((item) => { delete item.timestamp; });
        await fetchAddTemplate(params);
        FMessage.success($t('toastSuccess.addSuccess'));
        resetTemplateModel();
        getTemplateList();
        closeAddBar();
        isAdding.value = false;
    } catch (err) {
        console.warn(err);
        isAdding.value = false;
    }
};

const {
    clusterList,
    loadClusterList,
    proxyUserList,
    updateProxyUserList,
} = useRuleBasicData();
provide('clusterList', computed(() => clusterList.value || []));
provide('proxyUserList', computed(() => proxyUserList.value || []));

const excutionParamsRules = {
    name: [{
        required: true,
        message: $t('common.notEmpty'),
    }],
};

const curCachedTemplatData = ref({});
const editTemplate = async (template, index) => {
    curCachedTemplatData.value = cloneDeep(template);
    if (template.isEditing) return;
    template.isEditing = true;
    openTemplatePanel(index);
};

const showDeleteConfirmModal = ref(false);

const confirmEditTemplate = async (template, index) => {
    try {
        console.log('template', template);
        // console.log(ele.value[`form_header${index}`].validate, ele.value[`form_body${index}`]);
        // const results = await Promise.all([ele.value[`form_header${index}`].validate(), ele.value[`form_body${index}`].valid()]);
        console.log(ele.value[`form_body${index}`]);
        const results = await Promise.all([ele.value[`form_body${index}`].valid()]);

        if (results.includes(false)) {
            return;
        }
        validTemplateValue(template);
        const noiseDataCache = cloneDeep(template.noise_elimination_management) || [];
        const stringNoiseData = stringfyNoiseEliminationManagement(noiseDataCache);
        const params = {
            execution_parameters_id: template.id,
            project_id: route.query.projectId,
            name: template.name,
            excution_parameters_name: template.name,
            // 执行参数
            specify_filter: template.specify_filter,
            filter: template.filter,
            source_table_filter: template.source_table_filter,
            target_table_filter: template.target_table_filter,
            abort_on_failure: template.abort_on_failure,
            union_all: template.union_all,
            abnormal_data_storage: template.abnormal_data_storage,
            cluster: template.cluster,
            abnormal_proxy_user: template.abnormal_proxy_user,
            abnormal_database: template.abnormal_database,
            specify_static_startup_param: template.specify_static_startup_param,
            static_execution_parameters: template.static_execution_parameters,
            // 指标参数
            upload_rule_metric_value: template.upload_rule_metric_value,
            upload_abnormal_value: template.upload_abnormal_value,
            delete_fail_check_result: template.delete_fail_check_result,
            // 告警参数
            alert: template.alert,
            alarm_arguments_execution_parameters: template.alarm_arguments_execution_parameters,
            noise_elimination_management: stringNoiseData,
            // 执行变量配置
            execution_variable: template.execution_variable,
            execution_management: template.execution_management,
            // 高级执行配置
            advanced_execution: template.advanced_execution,
            engine_reuse: template.engine_reuse,
            concurrency_granularity: template.concurrency_granularity,
            dynamic_partitioning: template.dynamic_partitioning,
            top_partition: template.top_partition,
        };
        params.static_execution_parameters.forEach((item) => { delete item.timestamp; });
        params.alarm_arguments_execution_parameters.forEach((item) => { delete item.timestamp; });
        params.noise_elimination_management.forEach((item) => { delete item.timestamp; });
        params.execution_management.forEach((item) => { delete item.timestamp; });
        console.log('confirmEditTemplate-params', params);
        await fetchModifyTemplate(params);
        FMessage.success($t('toastSuccess.editSuccess'));
        getTemplateList();
    } catch (err) {
        console.warn(err);
    }
};

const cancelEditTemplate = (template, index) => {
    templateList.value[index] = cloneDeep(curCachedTemplatData.value);
    cancelAllEdit();
    template.isEditing = false;
    template.name = template.ref;
};

const currentTemlate = ref({});

const deleteTemplate = (template) => {
    currentTemlate.value = template;
    showDeleteConfirmModal.value = true;
};

const confirmDeleteTemplate = async () => {
    const params = {
        execution_parameters_id: currentTemlate.value.id,
        project_id: route.query.projectId,
    };
    // TODOS: 调用编辑接口
    try {
        await fetchDeleteTemplate(params);
        FMessage.success($t('toastSuccess.deleteSuccess'));
        getTemplateList();
        showDeleteConfirmModal.value = false;
    } catch (error) {}
};

const setDefaultExecutionParams = async () => {
    if (props.curExeName && !props.isBatch) {
        templateList.value.forEach((item, index) => {
            if (item.name === props.curExeName) {
                item.isSelected = true;
                isOpenTemplateList.value[index] = true;
                // 选中的模板，置顶展示
                if (index !== 0) {
                    const firstItem = templateList.value.splice(index, 1)[0];
                    templateList.value.unshift(firstItem);
                    const firstOpenItem = isOpenTemplateList.value.splice(index, 1)[0];
                    isOpenTemplateList.value.unshift(firstOpenItem);
                }
            }
        });
    } else {
        templateList.value.forEach((item, index) => {
            item.isSelected = false;
            isOpenTemplateList.value[index] = false;
        });
    }
};
onMounted(async () => {
    await getTemplateList();
    setDefaultExecutionParams();
    loadClusterList();
    updateProxyUserList();
});

// eslint-disable-next-line no-undef
defineExpose({ templateList });
</script>
<style lang="less" scoped>
@import "@/style/varible";
.collapse-template {
    cursor: pointer;
    color: #5384FF;
    background-color: rgb(247, 247, 248);
    padding: 0 16px 16px;
    margin-top: -16px;
}
.execution-params-style {
    margin-bottom: 16px;
}
.name-form {
    background: #F7F7F8;
    padding: 0px 0px 4px 16px;
    .name-label-form{
        margin-top: 16px;
    }
}
.create-name-form {
    background: #F7F7F8;
    padding: 16px 24px 8px 16px;
    .name-label-form{
        margin-top: 16px;
    }
}
.template-name-style {
    font-size: 16px;
    font-weight: 500;
    padding: 24px 0 24px 16px;
    &.isEmbed{
        background: #F7F7F8;
        height: 70px;
    }
}
.wd-content {
    .wd-content-body {
        .wd-body-menus {
            .template-fold {
                display: flex;
                align-items: center;
                color: #646670;
                :hover {
                    color: #000;
                }
            }
            .last-delete-menu-item {
                color: #FF4D4F;
                &::after{
                    background: #FFFFFF;
                }
}}}}
.edit-template {
  font-family: PingFangSC-Medium;
  overflow: hidden;
  .header-bar {
      cursor: pointer;
  }
  .fold {
    position: absolute;
    top: 30px;
    right: 30px;
    color: #646670;
    :hover {
      color: #000;
    }
  }
   &.isEmbed{
        padding: 0;
        &.isOpen {
            background: #F7F7F8;
            padding: 16px;
        }
    }
}

.close-title {
    font-size: 16px;
    color: #5384FF;
    letter-spacing: 0;
    line-height: 24px;
    padding-left: 0px;
}
.open-title {
    font-size: 16px;
    color: #0F1222;
    letter-spacing: 0;
    line-height: 24px;
    padding-left: 0px;
}

.template-list {
  font-size: 14px;
  letter-spacing: 0;
  .header {
    display: flex;
    margin-bottom: 16px;
    .label {
        position: relative;
        top: 5px;
    }
    .name {
        margin-left: 16px;
        width: 300px;
    }
  }
}
.template-pag-style {
    padding: 0px !important;
}
.template-menu-item {
    display: flex;
    align-items: center;
    color: @blue-color;
    line-height: 22px;
    padding-left: 16px;
    user-select: none;
    &::after{
        content: '';
        display: block;
        margin-left: 16px;
        width: 1px;
        height: 14px;
        background: #CFD0D3;
    }
    &:last-child{
        &::after{
            display: none;
        }
    }
}
// .card {
//     position: relative;
//     border-radius: 4px;
//     padding: 16px;
//     &.isEmbed{
//         margin: 16px 24px;
//         border: 1px solid #CFD0D3;
//         cursor: pointer;
//         &.selected,
//         &:hover {
//             border: 1px solid #5384FF;
//         }
//     }
//     .check-area {
//         position: absolute;
//         top: 0;
//         left: 0;
//         width: 0;
//         height: 0;
//         border-radius: 4px 0 0;
//         border-top: 12px solid #5384FF;
//         border-right: 12px solid transparent;
//         border-bottom: 12px solid transparent;
//         border-left: 12px solid #5384FF;
//     }
//     .check {
//         position: absolute;
//         top: 0;
//         left: 0;
//         color: #FFFFFF;
//     }
//     .title {
//         font-size: 16px;
//         color: #0F1222;
//         letter-spacing: 0;
//         font-weight: 500;
//     }
// }
</style>
<config>
{
    "name": "template",
    "title": "$myProject.template"
}
</config>
