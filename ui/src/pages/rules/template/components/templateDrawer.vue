<template>
    <FDrawer
        v-model:show="show"
        :title="drawerTitle"
        :footer="true"
        width="50%"
        displayDirective="if"
        @cancel="handleClose"
    >
        <template #footer>
            <div v-if="mode !== 'view'">
                <FButton
                    :disabled="isLoading"
                    type="primary"
                    style="margin-right: 16px;"
                    @click="handleOk"
                >
                    确定
                </FButton>
                <FButton @click="handleClose">取消</FButton>
            </div>
            <div v-else>
                <FButton type="info" style="margin-right: 16px;" @click="authorityEdit">编辑</FButton>
                <FTooltip
                    mode="confirm"
                    :confirmOption="{ okText: '确认删除' }"
                    placement="top"
                    @ok="handleDelete"
                >
                    <FButton type="danger">{{$t('common.delete')}}</FButton>
                    <template #title>
                        <div style="width: 200px">确认删除此模板吗？</div>
                    </template>
                </FTooltip>
            </div>
        </template>
        <div :class="{ view: mode === 'view' }">
            <div class="selection">
                <div class="header">基础信息</div>
                <BaseInfo ref="baseInfo" v-model:templateData="templateData" :mode="mode" />
            </div>
            <div class="selection">
                <div class="header">模板配置</div>
                <TemplateConfig
                    ref="templateConfig"
                    v-model:templateData="templateData"
                    :templateType="templateType"
                    :mode="mode"
                />
            </div>
            <div class="selection">
                <div class="header">占位符</div>
                <InputMeta
                    v-for="(item,index) in templateData.template_mid_table_input_meta"
                    :ref="el => { if (el) inputMetaRefs[index] = el }"
                    :key="item"
                    v-model:inputMetaData="templateData.template_mid_table_input_meta[index]"
                    :placeholders="placeholders"
                    :removable="templateData.template_mid_table_input_meta.length > 1"
                    :index="index + 1"
                    :mode="mode"
                    @delete="deleteInputMeta(index)"
                />
                <div v-if="mode !== 'view'" class="add-sign">
                    <span class="add-sign-btn" @click="addInputMeta">
                        <PlusCircleOutlined style="padding-right: 4.58px;" />添加占位符
                    </span>
                </div>
            </div>
        </div>
    </FDrawer>
</template>
<script setup>
import {
    ref, computed, defineProps, defineEmits, onUpdated, onMounted, watch,
} from 'vue';
import {
    useI18n,
} from '@fesjs/fes';
import { PlusCircleOutlined, MinusCircleOutlined } from '@fesjs/fes-design/es/icon';
import { FMessage } from '@fesjs/fes-design';
import { getLabelByValue } from '@/common/utils';
import BaseInfo from './baseInfo.vue';
import TemplateConfig from './templateConfig.vue';
import InputMeta from './inputMeta.vue';
import {
    addTemplate, editTemplate, deleteTemplate, fetchTemplateDetail, fetchPlaceholders,
} from '../api';

const { t: $t } = useI18n();
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
    templateType: {
        type: Number,
        default: 1,
    },
    tid: {
        type: Number,
        default: -1,
    },
});
const emit = defineEmits(['update:mode', 'update:show', 'addTemplate', 'editTemplate', 'deleteTemplate']);

const drawerTitle = computed(() => {
    const typeNameDict = {
        1: $t('ruleTemplatelist.singleTemplate'), // 单表模板
        3: $t('ruleTemplatelist.crossTableTemplate'), // 跨表模板
        4: $t('ruleTemplatelist.fileTemplate'), // 表文件模板
    };
    const typeName = typeNameDict[props.templateType];
    let resultString = '';
    if (props.mode === 'view') {
        return $t('ruleTemplatelist.templateDetail');
    }
    if (props.mode === 'add') {
        resultString = `${$t('common.add')}${typeName}`;
    } else {
        resultString = `${$t('common.edit')}${typeName}`;
    }
    return resultString;
});
const isLoading = ref(false);
const templateData = ref({
    naming_method: 2, // 命名方式：默认自定义命名
    save_mid_table: false,
    filter_fields: false,
    whether_using_functions: false,
    whether_solidification: false,
    visibility_departments: [],
    visibility_department_list: [], // 可见范围-{科室，id}
    dev_department_name: [], // 开发科室
    ops_department_name: [], // 运维科室
    datasource_type: [],
    count_function_name: '',
    template_mid_table_input_meta: [{}],
});
const needUpdate = ref(true);
const baseInfo = ref(null);
const templateConfig = ref(null);
const inputMetaRefs = ref([]);
const addInputMeta = () => {
    templateData.value.template_mid_table_input_meta.push({});
    console.log('templateData.value.template_mid_table_input_meta', templateData.value.template_mid_table_input_meta);
};
const deleteInputMeta = (index) => {
    templateData.value.template_mid_table_input_meta.splice(index, 1);
};
const placeholders = ref([]);
const genPlaceholders = async () => {
    const res = await fetchPlaceholders({ template_type: props.templateType });
    placeholders.value = res.placeholders;
    // console.log('genPlaceholders',res)
};
const resetTemplateData = () => {
    templateData.value = {
        naming_method: 2, // 命名方式：默认自定义命名
        save_mid_table: false,
        filter_fields: false,
        whether_using_functions: false,
        whether_solidification: false,
        visibility_departments: [], // 可见范围显示
        visibility_department_list: [], // 可见范围-{科室，id}
        dev_department_name: [], // 开发科室
        ops_department_name: [], // 运维科室
        datasource_type: [],
        count_function_name: '',
        template_mid_table_input_meta: [{}],
    };
    needUpdate.value = true;
    placeholders.value.forEach((item) => { item.disabled = false; });
};
// 有无权限删除编辑
const authorityEdit = () => {
    if (!templateData.value.is_editable) return FMessage.warn('没有权限编辑');
    emit('update:mode', 'edit');
};
const handleDelete = async () => {
    try {
        if (!templateData.value.is_editable) return FMessage.warn('没有权限删除');
        await deleteTemplate({ template_id: props.tid });
        FMessage.success($t('toastSuccess.deleteSuccess'));
        emit('deleteTemplate');
        emit('update:show', false);
        resetTemplateData();
    } catch (error) {
        console.warn('deleteTemplate error:', error);
    }
};
const handleOk = async () => {
    try {
        if (isLoading.value) {
            return;
        }
        isLoading.value = true;
        const result = await Promise.all([baseInfo.value.valid(), templateConfig.value.valid(), ...inputMetaRefs.value.map(item => item.valid())]);
        if (result.includes(false)) {
            isLoading.value = false;
            return;
        }
        const params = { ...templateData.value };
        params.dev_department_name = params.dev_department_name.join('/');
        params.ops_department_name = params.ops_department_name.join('/');
        params.template_type = props.templateType;
        if (params.naming_method === 1) {
            params.major_type = params.major_type.join(',');
        }
        params.mid_table_action = params.mid_table_action || '';
        // console.log(params);
        if (props.mode === 'add') {
            await addTemplate(params);
            FMessage.success($t('toastSuccess.addSuccess'));
            emit('addTemplate');
        } else if (props.mode === 'edit') {
            params.template_id = props.tid;
            await editTemplate(params);
            FMessage.success($t('toastSuccess.editSuccess'));
            emit('editTemplate');
        }
        isLoading.value = false;
        emit('update:show', false);
        resetTemplateData();
    } catch (error) {
        console.log(`${props.mode} template error:`, error);
        isLoading.value = false;
    }
};
const handleClose = () => {
    emit('update:show', false);
    resetTemplateData();
};
onUpdated(async () => {
    if (!props.show) {
        return;
    }
    // 逻辑只在面板打开的时候执行
    try {
        if (props.tid !== -1 && needUpdate.value) {
            const result = await fetchTemplateDetail({ template_id: props.tid });
            templateData.value = result;
            if (result.naming_method === 1) {
                templateData.value.major_type = result.major_type.split(',');
            }
            templateData.value.visibility_department_list = result.visibility_department_list ? result.visibility_department_list : [];
            templateData.value.visibility_departments = templateData.value.visibility_department_list.map(item => item?.id);
            templateData.value.dev_department_name = templateData.value.dev_department_name.split('/');
            templateData.value.ops_department_name = templateData.value.ops_department_name.split('/');
            templateData.value.visibility_departments = templateData.value.visibility_department_list.map(item => item?.name.split('/'));
            templateData.value.template_mid_table_input_meta = templateData.value.template_mid_table_input_meta.map((item) => {
                placeholders.value.forEach((placeholder) => {
                    if (item.input_type === placeholder.input_type) {
                        placeholder.disabled = true;
                    }
                });
                return {
                    input_type_name: getLabelByValue(placeholders.value, item.input_type, {
                        labelFieldName: 'input_cn_name', valueFieldName: 'input_type',
                    }),
                    ...item,
                };
            });
            needUpdate.value = false;
            console.log('templateData', templateData.value);
        }
    } catch (err) {
        console.warn('get templateDetail error:', err);
    }
});
watch(() => props.templateType, () => {
    genPlaceholders();
});
// 校验级别选择表级时，占位符模块去除字段相关逻辑
watch(() => templateData.value.verification_level, () => {
    console.log(templateData.value.verification_level);
    if (templateData.value.verification_level === 1) {
        templateData.value.template_mid_table_input_meta.forEach((item, index) => {
            if (item.input_type === 4) {
                templateData.value.template_mid_table_input_meta[index] = {};
            }
        });
        placeholders.value.forEach((item) => {
            if (item.input_type === 4) {
                item.disabled = true;
            }
        });
    } else {
        placeholders.value.forEach((item) => {
            if (item.input_type === 4) {
                item.disabled = false;
            }
        });
    }
    // console.log(placeholders.value, templateData.value.template_mid_table_input_meta)
});
</script>
<style lang='less' scoped>
@import "@/style/varible";
.selection {
    .header {
        margin-bottom: 16px;
        font-weight: bold;
        color: #0f1222;
    }
}
.view{
    :deep(.fes-form-item-label){
        height: auto;
    }
    :deep(.fes-form-item-content){
        min-height: 22px;
        color: #0F1222;
    }
}
.add-sign {
    color: @blue-color;
    .add-sign-btn {
        display: flex;
        align-items: center;
        justify-content: flex-start;
        cursor: pointer;
    }
}
</style>
