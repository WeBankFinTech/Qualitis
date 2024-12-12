<template>
    <FModal
        :show="show"
        :width="572"
        :title="$t('myProject.filtrateRule')"
        :maskClosable="false"
        @update:show="$emit('update:show', $event)"
    >
        <template #footer>
            <FSpace justify="end">
                <FButton @click="$emit('update:show', false)">{{$t('common.cancel')}}</FButton>
                <FButton @click="reset">{{$t('common.reset')}}</FButton>
                <FButton type="primary" @click="handleOk">{{$t('common.ok')}}</FButton>
            </FSpace>
        </template>
        <FForm ref="filtrateForm" :label-width="70" labelPosition="right" :model="filtrateData">
            <FFormItem :label="$t('ruleQuery.ruleGroupName')" prop="rule_group_name">
                <FInput
                    v-model="filtrateData.rule_group_name"
                    clearable
                    :placeholder="$t('common.pleaseInput')"
                />
            </FFormItem>
            <FFormItem :label="$t('common.ruleName')" prop="rule_name">
                <FInput
                    v-model="filtrateData.rule_name"
                    clearable
                    :placeholder="$t('common.searchEnKeyWords')"
                />
            </FFormItem>
            <FFormItem :label="$t('common.ruleCnName')" prop="rule_cn_name">
                <FInput
                    v-model="filtrateData.rule_cn_name"
                    clearable
                    :placeholder="$t('common.searchCnKeyWords')"
                />
            </FFormItem>
            <FFormItem :label="$t('myProject.inspectionTemplate')" prop="rule_template_id">
                <FSelect
                    v-model="filtrateData.rule_template_id"
                    clearable
                    filterable
                    valueField="template_id"
                    labelField="template_name"
                    :options="projectAllRules"
                ></FSelect>
            </FFormItem>
            <FFormItem :label="$t('common.databaseList')" prop="db_name">
                <FSelect
                    v-model="filtrateData.db_name"
                    clearable
                    filterable
                    valueField="database_name"
                    labelField="database_name"
                    :options="dataBaseList"
                    @change="changeDataTableList"
                ></FSelect>
            </FFormItem>
            <FFormItem :label="$t('common.tableLibst')" prop="table_name">
                <FSelect
                    v-model="filtrateData.table_name"
                    clearable
                    filterable
                    labelField="table_name"
                    valueField="table_name"
                    :options="dataTableList"
                ></FSelect>
            </FFormItem>
            <FFormItem v-if="isWorkflowProject" :label="$t('myProject.workflowSpace')" prop="work_flow_space">
                <FSelect
                    v-model="filtrateData.work_flow_space"
                    clearable
                    filterable
                    :options="workflowSpaceList"
                ></FSelect>
            </FFormItem>
            <FFormItem v-if="isWorkflowProject" :label="$t('myProject.workflowProject')" prop="work_flow_project">
                <FSelect
                    v-model="filtrateData.work_flow_project"
                    clearable
                    filterable
                    :options="workflowProjectList"
                ></FSelect>
            </FFormItem>
            <FFormItem v-if="isWorkflowProject" :label="$t('myProject.workflowName')" prop="work_flow_name">
                <FSelect
                    v-model="filtrateData.work_flow_name"
                    clearable
                    filterable
                    :options="workflowNameList"
                ></FSelect>
            </FFormItem>
            <FFormItem v-if="isWorkflowProject" :label="$t('myProject.workflowTaks')" prop="node_name">
                <FSelect
                    v-model="filtrateData.node_name"
                    clearable
                    filterable
                    :options="workflowTaskList"
                ></FSelect>
            </FFormItem>
            <FFormItem :label="$t('myProject.creator')" prop="create_user">
                <FSelect
                    v-model="filtrateData.create_user"
                    clearable
                    filterable
                    :options="projectUserList"
                ></FSelect>
            </FFormItem>
            <FFormItem :label="$t('myProject.createTime')" prop="create_time">
                <FDatePicker
                    v-model="filtrateData.create_time"
                    type="daterange"
                    clearable
                    placeholder="请选择"
                >
                </FDatePicker>
            </FFormItem>
            <FFormItem :label="$t('myProject.modifier')" prop="modify_user">
                <FSelect
                    v-model="filtrateData.modify_user"
                    clearable
                    filterable
                    :options="projectUserList"
                ></FSelect>
            </FFormItem>
            <FFormItem :label="$t('myProject.modifyTime')" prop="modify_time">
                <FDatePicker
                    v-model="filtrateData.modify_time"
                    type="daterange"
                    clearable
                    placeholder="请选择"
                >
                </FDatePicker>
            </FFormItem>
        </FForm>
    </FModal>
</template>
<script setup>
import {
    reactive, ref, watch, defineProps, defineEmits, onMounted, computed,
} from 'vue';
import { useI18n, useRoute } from '@fesjs/fes';
import useDataSource from '../hooks/useDataSource';
import useProjectAuth from '../hooks/useProjectAuth';
import { fetchRuleTemplate } from '../api';
import useWorkflowRelList from '../hooks/useWorkflowRelList';

const { t: $t } = useI18n();

const prop = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    filtrateCount: {
        type: Number,
        required: true,
    },
    isWorkflowProject: {
        tyep: Boolean,
        default: false,
    },
});

const emit = defineEmits(['filtrateRules', 'update:filtrateCount']);

// 定义筛选参数
const filtrateData = reactive({
    rule_group_name: '',
    rule_name: '',
    rule_cn_name: '',
    rule_template_id: '',
    db_name: '',
    table_name: '',
    work_flow_space: '',
    work_flow_project: '',
    work_flow_name: '',
    node_name: '',
    create_user: '',
    modify_user: '',
});

const _filtrateCount = computed({
    get() {
        return prop.filtrateCount;
    },
    set(val) {
        emit('update:filtrateCount', val);
    },
});

// 监听筛选条件
watch(filtrateData, () => {
    _filtrateCount.value = Object.values(filtrateData).filter((v) => {
        if (v?.length === 0) return false;
        return !!v;
    }).length;
}, { immediate: true });

// 获取数据源相关数据
const {
    dataBaseList,
    dataTableList,
} = useDataSource(['dataBaseList']);

// 查询数据备份
const filtrateCopy = ref({});

watch(() => prop.show, (newVal) => {
    if (newVal) {
        filtrateCopy.value = { ...filtrateData };
    } else {
        Object.assign(filtrateData, filtrateCopy.value);
    }
});

// 弹窗相关操作
const filtrateForm = ref();

const handleOk = () => {
    filtrateCopy.value = { ...filtrateData };
    emit('filtrateRules', filtrateData);
};

// 重置筛选条件
const reset = () => {
    filtrateForm.value?.resetFields();
    handleOk();
};

// 获取检验规则模板
const projectAllRules = ref([]);

const fetchRuleTemplates = async () => {
    try {
        const res = await fetchRuleTemplate();
        projectAllRules.value = res || [];
    } catch (error) {
        console.log('error:', error);
    }
};

// 获取用户列表
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();

const route = useRoute();
const projectId = route.query.projectId;
const {
    workflowSpaceList,
    workflowNameList,
    workflowProjectList,
    workflowTaskList,
} = useWorkflowRelList(projectId, prop.isWorkflowProject ? 1 : -1);

onMounted(async () => {
    await fetchRuleTemplates();
    await getProjectUserList();
    projectUserList.value = projectUserList.value.map(item => ({ value: item, label: item }));
});

// 通过选中数据库来显示数据表
const changeDataTableList = (val) => {
    const target = dataBaseList.value.find(v => v.database_name === val) || {};
    dataTableList.value = target.table || [];
    if (!val) filtrateData.table_name = '';
};
</script>

<style lang="less" scoped>
</style>
