<template>
    <div class="rule-detail-form"
         :class="{ edit: mode !== 'view' }">
        <FForm
            ref="baseInfoForm"

            :labelWidth="96"
            :labelPosition="mode !== 'view' ? 'right' : 'left'"
            :model="templateData"
            :rules="mode !== 'view' ? templateRules : []"
        >
            <!-- 命名方式-->
            <FFormItem :label="$t('ruleTemplatelist.templateNameType')" prop="naming_method">
                <FSelect
                    v-model="templateData.naming_method"
                    class="form-edit-input"
                    :options="nameTypes"
                    :disabled="mode === 'edit'"
                    @change="onNamingMethodChange"
                ></FSelect>
                <span class="form-preview-label">{{templateData.naming_method === 2 ? '自定义命名' : '规范命名'}}</span>
            </FFormItem>
            <!-- 模板中文名 -->
            <FFormItem v-if="templateData.naming_method === 2 || mode === 'view'" :label="$t('ruleTemplatelist.templateCNName')" prop="template_name">
                <FInput
                    v-model="templateData.template_name"
                    class="form-edit-input"
                    :maxlength="16"
                    showWordLimit
                    :disabled="mode === 'edit'"
                    placeholder="请输入"
                />
                <span class="form-preview-label">{{templateData.template_name || '--'}}</span>
            </FFormItem>
            <!-- 模板英文名 -->
            <FFormItem v-if="templateData.naming_method === 2 || mode === 'view'" :label="$t('ruleTemplatelist.templateENName')" prop="en_name">
                <FInput
                    v-model="templateData.en_name"
                    class="form-edit-input"
                    :maxlength="64"
                    showWordLimit
                    placeholder="请输入"
                />
                <span class="form-preview-label">{{templateData.en_name || '--'}}</span>
            </FFormItem>
            <!-- 模板名称-->
            <FFormItem v-if="templateData.naming_method === 1 && mode !== 'view'" :label="$t('ruleTemplatelist.templateName')" prop="major_type">
                <FSelectCascader
                    v-model="templateData.major_type"
                    class="form-edit-input"
                    :data="templateTypeList"
                    emitPath
                    showPath
                    checkStrictly="child"
                    placeholder="请选择模板大类与类别"
                    :disabled="mode === 'edit'"
                    @change="generatePreview"
                ></FSelectCascader>
            </FFormItem>
            <FFormItem v-if="templateData.naming_method === 1 && mode !== 'view'" label=" ">
                <FForm
                    ref="nameFormRef"
                    layout="inline"
                    :model="templateData"
                    :rules="mode !== 'view' ? templateRules : []"
                    class="pure-form"
                >
                    <FFormItem label="" prop="custom_zh_code" :span="12">
                        <FInput
                            v-model="templateData.custom_zh_code"
                            class="form-edit-input"
                            :maxlength="16"
                            placeholder="请输入自定义中文码（示例：空值校验）"
                            :disabled="mode === 'edit'"
                            @change="generatePreview"
                        />
                    </FFormItem>
                    <FFormItem label="" prop="template_number" :span="12">
                        <FInput
                            v-model="templateData.template_number"
                            class="form-edit-input"
                            :maxlength="64"
                            placeholder="请输入模板编号（示例：日期_编号）"
                            :disabled="mode === 'edit'"
                            @change="generatePreview"
                        />
                    </FFormItem>
                </FForm>
            </FFormItem>
            <!-- 名称预览 -->
            <FFormItem v-if="templateData.naming_method === 1 && mode !== 'view'" :label="$t('ruleTemplatelist.templateNamePreview')" prop="templateNamePreview">
                <FInput
                    v-model="templateData.template_name"
                    class="form-edit-input"
                    disabled
                    placeholder="中文名：大类_类别_自定义中文码"
                    style="margin-right:8px"
                />
                <FInput
                    v-model="templateData.en_name"
                    class="form-edit-input"
                    disabled
                    placeholder="英文名：大类简写_类别简写_YYYYMMDD_XXX"
                />
            </FFormItem>
            <!-- 模板描述 -->
            <FFormItem :label="$t('ruleTemplatelist.templateDesc')" prop="description">
                <FInput
                    v-model="templateData.description"
                    class="form-edit-input form-edit-input-textarea"
                    type="textarea"
                    :maxlength="256"
                    showWordLimit
                    :autosize="{ minRows: 2, maxRows: 5 }"
                    placeholder="请输入"
                />
                <span class="form-preview-label">{{templateData.description || '--'}}</span>
            </FFormItem>
            <!-- 开发科室 -->
            <FFormItem :label="$t('indexManagement.developDepartment')" prop="dev_department_name">
                <FSelectCascader
                    v-model="templateData.dev_department_name"
                    class="form-edit-input"
                    :data="devDivisions"
                    :loadData="loadDevDivisions"
                    clearable
                    remote
                    emitPath
                    showPath
                    checkStrictly="child"
                    @change="handleDevId"
                ></FSelectCascader>
                <span class="form-preview-label">{{templateData.dev_department_name.join('/') || '--'}}</span>
            </FFormItem>
            <!-- 运维科室 -->
            <FFormItem :label="$t('indexManagement.maintainDepartment')" prop="ops_department_name">
                <FSelectCascader
                    v-model="templateData.ops_department_name"
                    class="form-edit-input"
                    :data="opsDivisions"
                    :loadData="loadOpsDivisions"
                    clearable
                    remote
                    emitPath
                    showPath
                    checkStrictly="child"
                    @change="handleOpsId"
                ></FSelectCascader>
                <span class="form-preview-label">{{templateData.ops_department_name.join('/') || '--'}}</span>
            </FFormItem>
            <!-- 可见范围 -->
            <FFormItem :label="$t('indexManagement.visibleRange')" prop="visibility_departments">
                <FSelectCascader
                    v-model="templateData.visibility_departments"
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
                    @change="visSelectChange"
                />
                <span
                    class="form-preview-label"
                >{{templateData.visibility_department_list.map(item => item.name).join('、') || '--'}}</span>
            </FFormItem>
        </FForm>
    </div>
</template>
<script setup>
import {
    useI18n,
} from '@fesjs/fes';
import {
    computed, ref, onUpdated, onMounted,
} from 'vue';
import useDivisions from '@/hooks/useDivisions';
import useDepartment from '@/hooks/useDepartment';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { COMMON_REG } from '@/assets/js/const';
import { parseJSON } from 'date-fns';
import { fetchNamingTypeList, fetchTemplateTypeList } from '../api';

const { t: $t } = useI18n();
const nameTypes = ref([]);
const genNamingTypeList = async () => {
    try {
        const result = await fetchNamingTypeList();
        nameTypes.value = result.map(item => ({
            value: item.code,
            label: item.message,
        }));
    } catch (error) {
        console.log('genNamingTypeList error:', error);
    }
};
const templateTypeList = ref([]);
const genTemplateTypeList = async () => {
    try {
        const result = await fetchTemplateTypeList();
        templateTypeList.value = result.map((item) => {
            const majorCategories = JSON.parse(item.major_categories);
            const kind = JSON.parse(item.kind);
            return {
                value: majorCategories.en_name,
                label: majorCategories.zh_name,
                children: kind.map(child => ({
                    value: child.en_name,
                    label: child.zh_name,
                    ...child,
                })),
                ...majorCategories,
            };
        });
        // console.log('templateTypeList',templateTypeList.value)
    } catch (error) {
        console.log('genTemplateList error:', error);
        templateTypeList.value = [];
    }
};


// eslint-disable-next-line no-undef
const props = defineProps({
    mode: {
        type: String,
        default: 'add',
    },
    templateData: {
        type: Object,
        default: () => ({}),
        required: true,
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:templateData']);
const currentTemplateData = computed({
    get: () => props.templateData,
    set: (value) => {
        emit('update:templateData', value);
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
} = useDivisions(true);
const {
    selectDevDate,
    selectOpsDate,
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);

const templateRules = {
    naming_method: [
        {
            type: 'number',
            required: true,
            trigger: ['blur', 'change'],
            message: '请选择命名方式',
        },
    ],
    template_name: [
        {
            required: true,
            trigger: ['blur', 'change'],
            message: '请输入模板中文名',
        },
    ],
    en_name: [
        {
            required: true,
            trigger: ['blur', 'change'],
            message: '请输入模板英文名',
        },
        { pattern: COMMON_REG.EN_NAME_64, message: '请输入英文、数字、下划线' },
    ],
    major_type: [
        {
            required: true,
            type: 'array',
            trigger: ['blur', 'change'],
            message: '请选择模板大类与类别',
        },
    ],
    custom_zh_code: [
        {
            required: true,
            trigger: ['blur', 'change'],
            message: '请输入自定义中文码',
        },
    ],
    template_number: [
        {
            required: true,
            trigger: ['blur', 'change'],
            message: '请输入模板编号',
        },
    ],
    description: [
        {
            required: true,
            trigger: ['blur', 'change'],
            message: '请输入模板描述',
        },
    ],
    dev_department_name: [
        {
            required: true,
            type: 'array',
            trigger: ['blur', 'change'],
            message: '请选择开发科室',
        },
    ],
    ops_department_name: [
        {
            required: true,
            type: 'array',
            trigger: ['blur', 'change'],
            message: '请选择运维科室',
        },
    ],
    visibility_departments: [
        {
            required: true,
            type: 'array',
            trigger: ['blur', 'change'],
            message: '请选择可见范围',
        },
    ],
};

const onNamingMethodChange = () => {
    currentTemplateData.value.template_name = '';
    currentTemplateData.value.en_name = '';
};

const baseInfoForm = ref(null);
const nameFormRef = ref(null);
const valid = async () => {
    try {
        await baseInfoForm.value?.validate();
        await nameFormRef.value?.validate();
        currentTemplateData.value.dev_department_id = selectDevDate.value;
        currentTemplateData.value.ops_department_id = selectOpsDate.value;
        currentTemplateData.value.visibility_department_list = selectVisDate.value;
        return true;
    } catch (e) {
        return false;
    }
};

onUpdated(async () => {
    try {
        if (currentTemplateData.value.template_id !== -1) {
            console.log('update baseinfo');
            // 接收科室初始值
            selectDevDate.value = currentTemplateData.value.dev_department_id;
            selectOpsDate.value = currentTemplateData.value.ops_department_id;
            selectVisDate.value = currentTemplateData.value.visibility_department_list;
        }
    } catch (err) {
        console.warn('get templateDetail error:', err);
    }
});
const generatePreview = () => {
    console.log('currentTemplateData.major_type', currentTemplateData.value.major_type);
    const templateMajor = templateTypeList.value.find(item => item.value === currentTemplateData.value.major_type[0]);
    const templateKind = templateMajor?.children.find(item => item.value === currentTemplateData.value.major_type[1]);
    currentTemplateData.value.template_name = `${templateMajor?.zh_name}_${templateKind?.zh_name}_${currentTemplateData.value.custom_zh_code || '自定义中文码'}`;
    currentTemplateData.value.en_name = `${templateMajor?.abbreviation}_${templateKind?.abbreviation}_${currentTemplateData.value.template_number || 'YYYYMMDD_XXX'}`;
};
onMounted(() => {
    genNamingTypeList();
    genTemplateTypeList();
});
// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
<style lang='less' scoped>
.pure-form {
    margin-bottom: 0 !important;
    gap: 8px;
    .fes-form-item {
        margin-bottom: 0 !important;
    }
}
.rule-detail-form {
    :deep(.fes-input) {
        max-width: 100%;
    }
    :deep(.fes-select) {
        max-width: 100%;
    }
    :deep(.fes-textarea) {
        max-width: 100%;
    }
    .form-edit-input-textarea{
        :deep(textarea){
            font-family: auto;
            padding-right: 10px;
        }
    }
}
</style>
