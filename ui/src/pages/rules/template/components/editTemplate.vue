<template>
    <div>
        <FDrawer
            v-model:show="show"
            :title="modalTitle"
            :footer="true"
            width="50%"
            displayDirective="if"
            @cancel="closeEditPanel"
        >
            <!-- 页脚动态菜单 -->
            <template #footer>
                <div v-if="mode !== 'add' && isPreviewMode" class="modalFooter" @click="authority">
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
                    <FButton type="info" @click="authorityEdit">{{$t('common.edit')}}</FButton>
                </div>
                <div v-else class="modalFooter">
                    <FButton :disabled="isLoading" @click="closeEditPanel">{{$t('common.cancel')}}</FButton>
                    <!-- 在模版定义 -->
                    <FButton v-if="activeTab === 'defined'" type="primary" @click="checkDefinedForm">{{$t('common.next')}}</FButton>
                    <!-- 在数据统计 -->
                    <FButton v-if="activeTab === 'caculate'" type="primary" @click="activeTab = 'defined'">{{$t('common.prev')}}</FButton>
                    <FButton v-if="activeTab === 'caculate'" type="primary" @click="checkCaculateForm">{{$t('common.next')}}</FButton>
                    <!-- 在中间表 -->
                    <FButton v-if="activeTab === 'table'" :disabled="isLoading" type="primary" @click="activeTab = 'caculate'">{{$t('common.prev')}}</FButton>
                    <FButton v-if="activeTab === 'table'" :disabled="isLoading" :loading="isLoading" type="primary" @click="checkTableForm">{{$t('common.ok')}}</FButton>
                </div>
            </template>
            <!-- 查看详情 -->
            <Detail
                v-if="mode !== 'add' && isPreviewMode"
                :templateForm="templateForm"
                :caculateForm="caculateForm"
                :tableForm="tableForm"
                :statisticalFunctionsLabel="statisticalFunctionsLabel"
                :numberTypeLabel="numberTypeLabel"
                :templateTypeFormatter="templateTypeFormatter"
                :booleanFormatter="booleanFormatter"
                :dataSourceTypeFormatter="dataSourceTypeFormatter"></Detail>
            <FTabs v-else v-model="activeTab">
                <FTabPane name="模版定义" displayDirective="show" value="defined">
                    <FForm ref="templateFormRef" labelWidth="90px" labelPosition="right" :model="templateForm" :rules="templateFormRules" style="padding: 20px 0 0;">
                        <FFormItem :label="`${$t('ruleTemplatelist.templateName')}`" prop="templateName">
                            <FInput v-model="templateForm.templateName" :placeholder="$t('common.pleaseEnter')" :disabled="mode === 'edit'" />
                        </FFormItem>
                        <FFormItem :label="`${$t('ruleTemplatelist.clusterNum')}`" prop="clusterNum">
                            <FInputNumber v-model="templateForm.clusterNum" :placeholder="$t('common.pleaseEnter')" />
                        </FFormItem>
                        <FFormItem :label="`${$t('ruleTemplatelist.dbNum')}`" prop="dbNum">
                            <FInputNumber v-model="templateForm.dbNum" :placeholder="$t('common.pleaseEnter')" />
                        </FFormItem>
                        <FFormItem :label="`${$t('ruleTemplatelist.tableNum')}`" prop="tableNum">
                            <FInputNumber v-model="templateForm.tableNum" :placeholder="$t('common.pleaseEnter')" />
                        </FFormItem>
                        <FFormItem :label="`${$t('ruleTemplatelist.fieldNum')}`" prop="fieldNum">
                            <FInputNumber v-model="templateForm.fieldNum" :placeholder="$t('common.pleaseEnter')" />
                        </FFormItem>
                        <FFormItem :label="$t('ruleTemplatelist.dataSourceType')" prop="datasourceType">
                            <FSelect
                                v-model="templateForm.datasourceType"
                                multiple
                                collapseTags
                                :collapseTagsLimit="3"
                                :options="sourceList"
                                labelField="value"
                                valueField="key"
                            />
                        </FFormItem>
                        <FFormItem :label="$t('ruleTemplatelist.parseFormat')" prop="actionType">
                            <FSelect
                                v-model="templateForm.actionType"
                                :options="TEMPLATE_FORMAT_LIST"
                                labelField="value"
                                valueField="key"
                            />
                        </FFormItem>
                        <!-- 开发科室 -->
                        <FFormItem :label="$t('indexManagement.developDepartment')" prop="dev_department_name">
                            <FSelectCascader
                                v-model="templateForm.dev_department_name"
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
                                v-model="templateForm.ops_department_name"
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
                                v-model="templateForm.action_range"
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
                    </FForm>
                </FTabPane>
                <FTabPane value="step1" disabled>
                    <template #tab><RightOutlined /></template>
                </FTabPane>
                <FTabPane name="数据统计" displayDirective="show" value="caculate" :disabled="!isCaculateFormValid">
                    <FForm ref="caculateFormRef" labelWidth="100px" labelPosition="right" :model="caculateForm" :rules="caculateFormRules" style="padding: 20px 0 0;">
                        <FFormItem :label="`${$t('ruleTemplatelist.templateStatisticalFunctions')}`" prop="statisticalFunctions">
                            <FSelect v-model="caculateForm.statisticalFunctions">
                                <FOption v-for="(k,i) in statisticalFun" :key="i" :value="k.code">{{k.type}}</FOption>
                            </FSelect>
                        </FFormItem>
                        <FFormItem :label="`${$t('ruleTemplatelist.numberType')}`" prop="numberType">
                            <FSelect v-model="caculateForm.numberType">
                                <FOption v-for="(k,i) in numTypeList" :key="i" :value="k.code">{{k.value}}</FOption>
                            </FSelect>
                        </FFormItem>
                        <FFormItem :label="`${$t('ruleTemplatelist.templateStatisticsName')}`" prop="statisticsName">
                            <FInput v-model="caculateForm.statisticsName" :placeholder="$t('common.pleaseEnter')" />
                        </FFormItem>
                        <FFormItem :label="'统计函数值'" prop="funcValue">
                            <FInput v-model="caculateForm.funcValue" :placeholder="$t('common.pleaseEnter')" />
                        </FFormItem>
                    </FForm>
                </FTabPane>
                <FTabPane value="step2" disabled>
                    <template #tab><RightOutlined /></template>
                </FTabPane>
                <FTabPane name="中间表" displayDirective="show" value="table" :disabled="!isTableFormValid">
                    <FForm ref="tableFormRef" labelPosition="right" :model="tableForm" :rules="tableFormRules" style="padding: 20px 0 0;">
                        <FFormItem prop="templateSql">
                            <template v-slot:label>
                                <span @click="descClickHandler">
                                    {{$t('ruleTemplatelist.createIntermediateTable')}}
                                    <FTooltip :content="$t('ruleTemplatelist.tableHint')" placement="top"><QuestionCircleOutlined /></FTooltip>
                                </span>
                            </template>
                            <FInput v-model="tableForm.templateSql" :placeholder="$t('common.pleaseEnter')" type="textarea" :rows="2" />
                        </FFormItem>
                        <FTabs v-model="selectedTabs" type="card" addable @add="handleAddTab" @close="handleCloseTab">
                            <FTabPane v-for="(item,index) in tableForm.templateItems" :key="index" displayDirective="show" :closable="true" :name="item.name" :value="index" class="table-tabs-ctn">
                                <FFormItem labelWidth="80px" :label="`${$t('ruleTemplatelist.replaceName')}`" :prop="`templateItems[${index}].input_meta_name`" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                                    <FInput v-model="tableForm.templateItems[index].input_meta_name" />
                                </FFormItem>
                                <FFormItem labelWidth="80px" :label="`${$t('ruleTemplatelist.replaceContent')}`" :prop="`templateItems[${index}].input_meta_placeholder`" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                                    <FInput v-model="tableForm.templateItems[index].input_meta_placeholder" />
                                </FFormItem>
                                <FFormItem labelWidth="80px" :label="`${$t('ruleTemplatelist.replaceType')}`" :prop="`templateItems[${index}].input_type`" :rules="[{ required: true, trigger: 'change', type: 'number', message: $t('common.notEmpty') }]">
                                    <FSelect v-model="tableForm.templateItems[index].input_type">
                                        <FOption v-for="(k,i) in templateTypeList" :key="i" :value="k.code" :label="k.value">{{k.value}}</FOption>
                                    </FSelect>
                                </FFormItem>
                                <FFormItem labelWidth="80px" :label="`${$t('common.description')}`" :prop="`templateItems[${index}].placeholder_description`" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                                    <FInput v-model="tableForm.templateItems[index].placeholder_description" />
                                </FFormItem>
                            </FTabPane>
                        </FTabs>
                        <br>
                        <FFormItem :label="$t('ruleTemplatelist.saveTable')" prop="saveTable">
                            <FRadioGroup v-model="tableForm.saveTable">
                                <FRadio :value="true">{{$t('common.yes')}}</FRadio>
                                <FRadio :value="false">{{$t('common.no')}}</FRadio>
                            </FRadioGroup>
                        </FFormItem>
                    </FForm>
                </FTabPane>
            </FTabs>
        </FDrawer>
    </div>
</template>
<script setup>
import {
    ref, computed, defineProps, defineEmits, onUpdated,
} from 'vue';
import {
    useI18n, request as FRequest, useModel,
} from '@fesjs/fes';
import useDivisions from '@/hooks/useDivisions';
import useDepartment from '@/hooks/useDepartment';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { FMessage } from '@fesjs/fes-design';
import { cloneDeep } from 'lodash-es';
import { QuestionCircleOutlined, RightOutlined } from '@fesjs/fes-design/icon';
import { TEMPLATE_FORMAT_LIST } from '@/assets/js/const';
import Detail from './detail';

const { t: $t } = useI18n();

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
    type: {
        type: Number,
        default: 1,
    },
    tid: {
        type: Number,
        default: -1,
    },
    dataSourceTypeFormatter: {
        type: Function,
        default: () => {},
    },
    templateTypeFormatter: {
        type: Function,
        default: () => {},
    },
    booleanFormatter: {
        type: Function,
        default: () => {},
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

const emit = defineEmits(['update:show', 'loadData']);

const templateForm = ref({
    templateName: '', // 模板名称
    clusterNum: 0, // 集群数量
    datasourceType: [], // 数据源类型
    dbNum: 0, // 集群数量
    tableNum: 0, // 数据库数量
    fieldNum: 0, // 数据表数量
    actionType: '', // 解析格式
    dev_department_name: [], // 开发科室
    dev_department_id: '', // 开发科室id
    ops_department_name: [], // 运维科室
    ops_department_id: '', // 运维科室id
    action_range: [], // 可见范围显示
    visibility_department_list: [], // 可见范围-{科室，id}
});

const caculateForm = ref({});

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

// 默认是预览模式，但新增的时候不显示
const isPreviewMode = ref(true);

const modalTitle = computed(() => {
    const typeName = props.type === 1 ? $t('ruleTemplatelist.singleTemplate') : $t('ruleTemplatelist.multiTableTemplate');
    let resultString = '';
    if (props.mode !== 'add' && isPreviewMode.value) {
        // 预览模式
        return typeName;
    }

    if (props.mode === 'add') {
        resultString = `${$t('common.add')}${typeName}`;
    } else {
        resultString = `${$t('common.edit')}${typeName}`;
    }
    return resultString;
});

const selectedTabs = ref(0);

const templateFormRules = {
    templateName: [
        { required: true, message: $t('common.notEmpty'), trigger: 'input' },
    ],
    clusterNum: [
        {
            required: true, type: 'number', message: $t('common.invalidNumber'), trigger: 'input',
        },
    ],
    dbNum: [
        {
            required: true, type: 'number', message: $t('common.invalidNumber'), trigger: 'input',
        },
    ],
    tableNum: [
        {
            required: true, type: 'number', message: $t('common.invalidNumber'), trigger: 'input',
        },
    ],
    fieldNum: [
        {
            required: true, type: 'number', message: $t('common.invalidNumber'), trigger: 'input',
        },
    ],
    datasourceType: [
        {
            required: true, type: 'array', message: $t('common.notEmpty'), trigger: 'change',
        },
    ],
    actionType: [
        {
            required: true, type: 'number', message: $t('common.notEmpty'), trigger: 'change',
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
    action_range: [
        {
            required: false, message: $t('common.notEmpty'), type: 'array', trigger: 'change',
        },
    ],
};

const caculateFormRules = {
    statisticalFunctions: [
        {
            required: true, type: 'number', message: $t('common.invalidNumber'), trigger: 'change',
        },
    ],
    numberType: [
        {
            required: true, type: 'number', message: $t('common.invalidNumber'), trigger: 'change',
        },
    ],
    statisticsName: [
        { required: true, message: $t('common.notEmpty'), trigger: 'input' },
    ],
    funcValue: [
        { required: true, message: $t('common.notEmpty'), trigger: 'input' },
    ],
};

const tableTabFormRules = {
    input_meta_name: [
        { required: true, message: $t('common.notEmpty'), trigger: 'change' },
    ],
    input_meta_placeholder: [
        { required: true, message: $t('common.notEmpty'), trigger: 'input' },
    ],
    input_type: [
        { required: true, message: $t('common.notEmpty'), trigger: 'change' },
    ],
    placeholder_description: [
        { required: true, message: $t('common.notEmpty'), trigger: 'change' },
    ],
};

const tableFormRules = {
    templateSql: [
        { required: true, message: $t('common.notEmpty'), trigger: 'change' },
    ],
    input_meta_name: [
        { required: true, message: $t('common.notEmpty'), trigger: 'input' },
    ],
    input_meta_placeholder: [
        { required: true, message: $t('common.notEmpty'), trigger: 'input' },
    ],
    input_type: [
        { required: true, message: $t('common.notEmpty'), trigger: 'change' },
    ],
    placeholder_description: [
        { required: true, message: $t('common.notEmpty'), trigger: 'input' },
    ],
    saveTable: [
        {
            required: true, type: 'boolean', message: $t('common.notEmpty'), trigger: 'change',
        },
    ],
};

const templateTypeList = [{
    value: $t('common.fixedValue'),
    code: 1,
}, {
    value: $t('common.tableList'),
    code: 3,
}, {
    value: $t('common.column'),
    code: 4,
}, {
    value: $t('common.databaseList'),
    code: 5,
}, {
    value: $t('ruleTemplatelist.fieldSplice'),
    code: 6,
}, {
    value: $t('ruleTemplatelist.regular'),
    code: 7,
}, {
    value: $t('ruleTemplatelist.array'),
    code: 8,
}, {
    value: $t('ruleTemplatelist.condition'),
    code: 9,
}, {
    value: $t('ruleTemplatelist.ANDStitching'),
    code: 10,
}, {
    value: $t('ruleTemplatelist.sourceDatabase'),
    code: 11,
}, {
    value: $t('ruleTemplatelist.sourceTable'),
    code: 12,
}, {
    value: $t('ruleTemplatelist.targetDatabase'),
    code: 13,
}, {
    value: $t('ruleTemplatelist.targetTable'),
    code: 14,
}, {
    value: $t('ruleTemplatelist.joinLeftExpression'),
    code: 15,
}, {
    value: $t('ruleTemplatelist.joinOperator'),
    code: 16,
}, {
    value: $t('ruleTemplatelist.joinRightExpression'),
    code: 17,
}, {
    value: $t('ruleTemplatelist.joinLeftField'),
    code: 18,
}, {
    value: $t('ruleTemplatelist.joinRightField'),
    code: 19,
}, {
    value: $t('ruleTemplatelist.precondition'),
    code: 20,
}, {
    value: $t('ruleTemplatelist.postcondition'),
    code: 21,
}];

const sourceList = [{
    value: 'HIVE',
    key: 1,
}, {
    value: 'MYSQL',
    key: 2,
}, {
    value: 'TDSQL',
    key: 3,
}, {
    value: 'KAFKA',
    key: 4,
}, {
    value: 'FPS',
    key: 5,
}];

const templateFormRef = ref(null);
const caculateFormRef = ref(null);
const tableFormRef = ref(null);
const isCaculateFormValid = ref(false);
const isTableFormValid = ref(false);
const checkDefinedForm = async () => {
    console.log('模板数据', templateForm.value);
    try {
        await templateFormRef.value.validate();
        activeTab.value = 'caculate';
        // 校验之后直接激活下一个tab
        isCaculateFormValid.value = true;
    } catch (err) {
        console.log('templateFormRef表单验证失败: ', err);
    }
};

const checkCaculateForm = async () => {
    try {
        await caculateFormRef.value.validate();
        activeTab.value = 'table';
        // 校验之后直接激活下一个tab
        isTableFormValid.value = true;
    } catch (err) {
        console.log('caculateFormRef表单验证失败: ', err);
    }
};
const checkTabsTableForm = async () => {
    try {
        for (let i = 0; i < tableForm.value.templateItems.length; i++) {
            await [`tableTabsFormRef_${i}`].value.validate();
        }
        return Promise.resolve();
    } catch (err) {
        return Promise.reject(err);
    }
};

const statisticalFun = [
    {
        type: 'sum',
        code: 1,
    },
    {
        type: 'avg',
        code: 2,
    },
    {
        type: 'count',
        code: 3,
    },
    {
        type: 'max',
        code: 4,
    },
    {
        type: 'min',
        code: 5,
    },
];

const isLoading = ref(false);
const submitForm = async () => {
    try {
        console.log(tableForm.value);
        if (tableForm.value.templateItems.length === 0) {
            return FMessage.error($t('ruleTemplatelist.countNeed'));
        }
        if (isLoading.value) {
            return;
        }
        isLoading.value = true;
        // 校验占位符
        const {
            templateSql,
            saveTable,
            templateItems,
        } = tableForm.value;
        const errorTabIndex = [];
        for (let i = 0; i < templateItems.length; i++) {
            const item = templateItems[i];
            // 默认字段
            item.replace_by_request = false;
            if (!item.input_meta_name || !item.input_meta_placeholder || !item.input_type || !item.placeholder_description) {
                errorTabIndex.push(i);
            }
        }
        if (errorTabIndex.length > 0) {
            return FMessage.warn($t('ruleTemplatelist.checkPlaceholderContent'));
        }

        const funcType = statisticalFun.filter(item => item.code === caculateForm.value.statisticalFunctions)[0].type;
        // 模板统计名称
        // eslint-disable-next-line camelcase
        const template_statistics_input_meta = [{
            input_meta_name: caculateForm.value.statisticsName,
            func_name: funcType,
            func_value: caculateForm.value.funcValue,
            value_type: caculateForm.value.numberType,
            result_type: 'Long',
        }];
        // 模板输出名称
        // eslint-disable-next-line camelcase
        const template_output_meta = [
            {
                field_name: funcType,
                field_type: caculateForm.value.numberType,
                output_name: caculateForm.value.statisticsName,
            },
        ];
        const {
            clusterNum,
            dbNum,
            tableNum,
            fieldNum,
            templateName,
            datasourceType,
            actionType,
            dev_department_name,
            dev_department_id,
            ops_department_name,
            ops_department_id,
            action_range,
            visibility_department_list,
        } = templateForm.value;
        const params = {
            cluster_num: clusterNum,
            db_num: dbNum,
            table_num: tableNum,
            field_num: fieldNum,
            template_name: templateName,
            datasource_type: datasourceType,
            action_type: actionType,
            save_mid_table: saveTable,
            mid_table_action: templateSql,
            template_mid_table_input_meta: templateItems,
            template_statistics_input_meta,
            template_output_meta,
            dev_department_name,
            dev_department_id,
            ops_department_id,
            ops_department_name,
            action_range,
            template_type: props.type,
        };
        // params.action_range = params.action_range.map(item => item.join('/'));
        params.visibility_department_list = selectVisDate.value;
        params.dev_department_name = params.dev_department_name.join('/');
        params.ops_department_name = params.ops_department_name.join('/');
        params.dev_department_id = selectDevDate.value;
        params.ops_department_id = selectOpsDate.value;
        let url = '';
        let mes = '';
        if (props.mode === 'add') {
            url = '/api/v1/projector/rule_template/default/add';
            mes = $t('toastSuccess.addSuccess');
        } else {
            url = '/api/v1/projector/rule_template/default/modify';
            params.template_id = props.tid;
            mes = $t('toastSuccess.editSuccess');
        }
        console.log(params);
        await FRequest(url, params);
        FMessage.success(mes);
        emit('update:show', false);
        isLoading.value = false;
        emit('loadData');
    } catch (err) {
        console.warn(err);
        isLoading.value = false;
    }
};

const checkTableForm = async () => {
    try {
        await tableFormRef.value.validate();
        // await checkTabsTableForm();
        isTableFormValid.value = true;
        submitForm();
        // TODO-提交数据
        // eslint-disable-next-line no-use-before-define
        currentDetail = null; // 保存后再次进入进入详情页
    } catch (err) {
        console.log('tableFormRef表单验证失败: ', err);
    }
};

const numTypeList = [{
    value: $t('common.fixedValue'),
    code: 1,
}, {
    value: $t('common.column'),
    code: 2,
}];

const handleAddTab = () => {
    tableForm.value.templateItems.push({
        name: `${$t('ruleTemplatelist.placeholder')}${tableForm.value.templateItems.length + 1}`,
        input_meta_name: '',
        input_meta_placeholder: '',
        input_type: '',
        placeholder_description: '',
    });
};

const handleCloseTab = (key) => {
    tableForm.value.templateItems.splice(key, 1);
};

let currentDetail = null;
const closeEditPanel = () => {
    emit('update:show', false);
    currentDetail = null;
};
const statisticalFunctionsLabel = ref('');
const numberTypeLabel = ref('');
onUpdated(async () => {
    if (!props.show) {
        return;
    }
    // 逻辑只在面板打开的时候执行
    try {
        // 每次进来的时候都是显示第一个tab
        activeTab.value = 'defined';
        if (props.mode === 'edit' && props.tid !== -1) {
            if (currentDetail) {
                // 不需要重新初始化
                console.log('不需要重新初始化', currentDetail);
                return;
            }
            // 编辑模式开始填充数据
            currentDetail = await FRequest(`/api/v1/projector/rule_template/modify/detail/${props.tid}`, {}, 'get');
            currentDetail.action_range = Array.isArray(currentDetail.visibility_department_list) ? currentDetail.visibility_department_list.map(item => item.name) : [];
            const {
                template_name,
                cluster_num,
                db_num,
                table_num,
                field_num,
                datasource_type,
                action_type,
                save_mid_table,
                mid_table_action,
                template_output_meta,
                template_mid_table_input_meta,
                template_statistics_input_meta,
                dev_department_id,
                dev_department_name,
                ops_department_id,
                ops_department_name,
                visibility_department_list,
                action_range,
            } = currentDetail;
            // 编辑的时候所有tab都是激活状态
            isCaculateFormValid.value = true;
            isTableFormValid.value = true;
            template_mid_table_input_meta.map((item, index) => {
                item.name = `${$t('ruleTemplatelist.placeholder')}${index + 1}`;
                return item;
            });
            const code = statisticalFun.filter(it => it.type === template_statistics_input_meta[0].func_name);
            templateForm.value = {
                templateName: template_name,
                clusterNum: cluster_num,
                dbNum: db_num,
                tableNum: table_num,
                fieldNum: field_num,
                datasourceType: datasource_type,
                actionType: action_type,
                dev_department_id,
                dev_department_name,
                ops_department_id,
                ops_department_name,
                visibility_department_list,
                action_range,
            };
            // 接收科室初始值
            selectDevDate.value = templateForm.value.dev_department_id;
            selectOpsDate.value = templateForm.value.ops_department_id;
            selectVisDate.value = templateForm.value.visibility_department_list;
            // 科室数据类型转换
            templateForm.value.dev_department_name = templateForm.value.dev_department_name?.split(' ') || [];
            templateForm.value.ops_department_name = templateForm.value.ops_department_name?.split(' ') || [];
            console.log('初始数据', templateForm.value);
            caculateForm.value = {
                statisticalFunctions: code[0].code,
                numberType: template_statistics_input_meta[0].value_type,
                statisticsName: template_statistics_input_meta[0].input_meta_name,
                funcValue: template_statistics_input_meta[0].func_value,
            };
            tableForm.value = {
                templateSql: mid_table_action,
                templateItems: template_mid_table_input_meta,
                // eslint-disable-next-line camelcase
                saveTable: save_mid_table,
            };
            // 数据反显需要
            statisticalFunctionsLabel.value = code[0].type;
            const targetNumType = numTypeList.filter(item => item.code === template_statistics_input_meta[0].value_type);
            if (targetNumType && targetNumType.length > 0) {
                numberTypeLabel.value = targetNumType[0].value;
            }
            isPreviewMode.value = true;
        } else {
            // 重复使用面板的时候需要清空数据
            templateForm.value = {};
            caculateForm.value = {};
            isCaculateFormValid.value = false;
            isTableFormValid.value = false;
            tableForm.value = {
                templateItems: [{
                    name: `${$t('ruleTemplatelist.placeholder')}1`,
                    input_meta_name: '',
                    input_meta_placeholder: '',
                    input_type: '',
                    placeholder_description: '',
                }],
            };
        }
    } catch (err) {
        console.warn(err);
    }
});


// 全局的初始化信息
const initialState = useModel('@@initialState');
// 有无权限删除编辑
const authorityEdit = () => {
    if (!currentDetail.is_editable) return FMessage.error($t('没有权限编辑'));
    isPreviewMode.value = false;
    templateForm.value.action_range = templateForm.value.visibility_department_list?.map(item => item.name.split('/')) || [];
    initDepartment(templateForm.value.visibility_department_list);
};
// 删除规则
const deleteTemplate = async () => {
    const isAdmin = sessionStorage.getItem('firstRole') === 'admin';
    const userName = initialState.userName;
    // 非管理员用户，不能删除 template_level 为 1 的模板，(内置模板)
    const flag1 = !isAdmin && currentDetail.template_level === 1;
    // 管理员用户，不能删除 template_level 为 1 并且 (没有创建用户的模板 或者 创建用户不是本人) 的模板（内置模板）
    const flag2 = isAdmin && currentDetail.template_level === 1 && (!currentDetail.creator || currentDetail.creator !== userName);
    // if (flag1 || flag2) return FMessage.warn($t('ruleTemplatelist.notDeleteTemplate'));
    // 新的权限
    if (!currentDetail.is_editable) return FMessage.error($t('ruleTemplatelist.notDeleteTemplate'));
    await FRequest(`api/v1/projector/rule_template/default/delete/${currentDetail.template_id}`, {});
    FMessage.success($t('toastSuccess.deleteSuccess'));

    emit('update:show', false);
    currentDetail = null; // 清除currentDetail内容，防止对后续操作造成影响
    emit('loadData');
};
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
.fes-input-number{
    width: 100%;
}

:deep(.fes-form-item) {
    margin-bottom: 16px;
    .fes-form-item-label {
       height: 22px;
    }
    .fes-form-item-content{
       min-height: 22px;
    }
}
</style>
