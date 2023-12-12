<template>
    <FModal
        :show="show"
        :top="160"
        :width="500"
        :title="'复制规则'"
        :maskClosable="false"
        @cancel="toggleModal"
        @ok="copyRule">
        <FForm ref="copyRuleFormRef" :model="datasource" :rules="datasourceRules" :labelWidth="86" labelPosition="right">
            <FFormItem label="集群" prop="cluster_name">
                <FSelect
                    v-model="datasource.cluster_name"
                    filterable
                    clearable
                    class="form-edit-input"
                    @change="getLists"
                >
                    <FOption
                        v-for="(item, index) in clusterList"
                        :key="index"
                        :disabled="item.disabled"
                        :value="item.cluster_name"
                        :label="item.cluster_name"
                    ></FOption>
                </FSelect>
            </FFormItem>
            <FFormItem label="数据源类型" prop="type">
                <FSelect
                    v-model="datasource.type"
                    class="form-edit-input"
                    @change="onTypeChange"
                >
                    <FOption v-for="(item,index) in sourceTypeList" :key="index" :value="item.value" :label="item.label">
                    </FOption>
                </FSelect>
            </FFormItem>
            <!-- 只有在dss嵌入的时候才给用户选择数据来源 -->
            <FFormItem v-if="isEmbedInFrame" label="数据来源" prop="isUpStream">
                <FRadioGroup v-model="datasource.isUpStream" @change="onUpstreamChange">
                    <FRadio :value="true">上游表</FRadio>
                    <FRadio :value="false">正常表</FRadio>
                </FRadioGroup>
            </FFormItem>
            <FFormItem v-if="['mysql', 'tdsql'].includes(datasource.type)" label="连接" prop="linkis_datasource_name">
                <FSelect
                    v-model="datasource.linkis_datasource_id"
                    remote
                    class="form-edit-input"
                    :options="connections"
                    labelField="dataSourceName"
                    valueField="id"
                    @change="onDataSourceChange" />
                <!-- <div class="form-preview-label">{{datasource.linkis_datasource_name}}</div> -->
            </FFormItem>
            <FFormItem v-if="!datasource.isUpStream" :label="$t('crossTableCheck.Database')" prop="db_name">
                <FSelect
                    v-model="datasource.db_name"
                    filterable
                    clearable
                    class="form-edit-input"
                    labelField="db_name"
                    valueField="db_name"
                    :options="dbs"
                    @change="onDBChange" />
            </FFormItem>
            <FFormItem label="数据表" prop="table_name">
                <FSelect
                    v-model="datasource.table_name"
                    filterable
                    clearable
                    class="form-edit-input"
                    labelField="table_name"
                    valueField="table_name"
                    :options="tables"
                    @change="onTableChange" />
            </FFormItem>
            <FFormItem label="字段" prop="col_names">
                <FSelect
                    v-model="datasource.col_names"
                    multiple
                    filterable
                    clearable
                    class="form-edit-input"
                    @change="onColumnChange">
                    <FOption
                        v-for="col in columns"
                        :key="col.column_name"
                        :value="col.column_name"
                        :label="`${col.column_name} (${col.data_type})`"
                    ></FOption>
                </FSelect>
            </FFormItem>
            <FFormItem :label="$t('common.project')" prop="project">
                <FSelect
                    v-model="datasource.project"
                    filterable
                    clearable
                    class="form-edit-input"
                    labelField="project_name"
                    valueField="project_id"
                    :options="projectList"
                    @change="loadRuleListByProject" />
            </FFormItem>
            <FFormItem v-if="datasource.project" :label="$t('projects.rule')" prop="rule">
                <FSelect
                    v-model="datasource.rule"
                    filterable
                    multiple
                    class="form-edit-input"
                    labelField="rule_name"
                    valueField="rule_id"
                    :options="projectRules" />
            </FFormItem>
        </FForm>
    </FModal>
</template>
<script setup>
import {
    ref, reactive, inject, onMounted, computed, unref,
} from 'vue';
import {
    useI18n, useRouter, useRoute, request as FRequest,
} from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import eventbus from '@/common/useEvents';
import { DWSMessage } from '@/common/utils';
import { cloneDeep } from 'lodash-es';
import {
    dataSourceTypeList, forceFormPageUpdateEvent,
} from './utils';
import useDataSource from './hook/useDataSource';

// eslint-disable-next-line no-undef
const props = defineProps({
    show: {
        type: Boolean,
        default: false,
    },
    projectId: {
        type: String,
        default: '',
    },
    groupId: {
        type: [String, Number],
        default: null,
    },
    dssParams: {
        type: Object,
        default: {},
    },
});
const { t: $t } = useI18n();
const router = useRouter();
const route = useRoute();

// eslint-disable-next-line no-undef
const emit = defineEmits(['update:show']);

const datasource = reactive({
    isUpStream: false,
});

const upstreamParams = cloneDeep(unref(inject('upstreamParams')));

// 被嵌入其他项目的情况下不显示侧边栏
// eslint-disable-next-line no-restricted-globals
const isEmbedInFrame = computed(() => top !== self);

const clusterList = inject('clusterList');
const workflowProject = inject('workflowProject');
const sourceTypeList = ref(dataSourceTypeList.filter(item => item.value !== 'fps'));

const {
    connections,
    dbs,
    tables,
    columns,
    handleDbChange,
    handleTableChange,
    handleColumnChange,
    handleDbsDependenciesChange,
    handleDataSourcesDependenciesChange,
    updateDataSource,
    updateUpstreamParams,
} = useDataSource(upstreamParams);

const toggleModal = () => {
    emit('update:show', false);
};

const onUpstreamChange = async (data) => {
    // 如果选择了上游表，一定是hive，但是选择hive的时候上游表和正常表都可以选择
    if (data) {
        datasource.type = 'hive';
        // 清空数据库
        datasource.db_name = '';
    }
    updateUpstreamParams(data);
};

const datasourceRules = ref({
    cluster_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
    type: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
    linkis_datasource_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
    db_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
    table_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
    col_names: [{
        required: true,
        message: $t('common.notEmpty'),
    }],
    project: [{
        required: true,
        type: 'number',
        message: $t('common.notEmpty'),
    }],
    rule: [{
        required: true,
        type: 'array',
        message: $t('common.notEmpty'),
    }],
    isUpStream: [{
        required: true,
        type: 'boolean',
        message: $t('common.notEmpty'),
    }],
});

async function getLists() {
    if (!datasource.cluster_name) {
        return;
    }
    datasource.type = '';
    datasource.linkis_datasource_name = '';
    datasource.db_name = '';
    datasource.table_name = '';
    datasource.col_names = [];
    updateDataSource(datasource, 'copy');
    updateUpstreamParams(datasource.isUpStream, datasource.rule_name, datasource.cs_id);
    await handleDataSourcesDependenciesChange();
    await handleDbsDependenciesChange();
    await handleDbChange();
}


async function onTypeChange(t) {
    console.log('type', t);
    datasource.linkis_datasource_name = '';
    datasource.db_name = '';
    datasource.table_name = '';
    datasource.col_names = [];
    updateDataSource(datasource, 'copy');
    await handleDataSourcesDependenciesChange();
    await handleDbsDependenciesChange();
    await handleDbChange();
}

async function onDBChange(db) {
    console.log(db);
    datasource.table_name = '';
    datasource.col_names = [];
    updateDataSource(datasource, 'copy');
    await handleDbChange();
}

async function onTableChange(table) {
    datasource.col_names = [];
    updateDataSource(datasource, 'copy');
    await handleTableChange();
}

const projectList = ref([]);
async function loadProjectList() {
    const { data } = await FRequest('api/v1/projector/project/all', {
        page: 0,
        size: 2147483647,
    });
    projectList.value = data;
}

const projectRules = ref([]);
async function loadRuleListByProject() {
    const { rule_details } = await FRequest(`api/v1/projector/project/detail/${datasource.project}`, {
        page: 0,
        size: 2147483647,
    });
    // eslint-disable-next-line camelcase
    projectRules.value = rule_details;
}

function onDataSourceChange(value) {
    datasource.db_name = '';
    datasource.table_name = '';
    const item = connections.value.find(v => v.id === value);
    datasource.type = item?.dataSourceType.name || '';
    datasource.linkis_datasource_name = item?.dataSourceName || '';
    updateDataSource(datasource, 'copy');
    handleDbsDependenciesChange();
}

async function onColumnChange() {
    handleColumnChange();
}

const copyRuleFormRef = ref(null);
const isLoading = ref(false);
const copyRule = async () => {
    try {
        if (isLoading.value) {
            return;
        }
        isLoading.value = true;
        await copyRuleFormRef.value.validate();
        // eslint-disable-next-line camelcase
        const col_names = (datasource.col_names || []).map((name) => {
            // eslint-disable-next-line camelcase
            const c = columns.value.find(({ column_name }) => column_name === name);
            return { column_name: c.column_name, data_type: c.data_type };
        });
        const { rule_group_id } = await FRequest('api/v1/projector/rule/copy', {
            project_id: props.projectId,
            rule_group_id: props.groupId, // 当节点有规则时，传入规则所属组ID；无规则则不传
            rule_id_list: datasource.rule,
            cs_id: datasource.isUpStream ? route.query.contextID : null, // 处理上游表时，传入工作流的上下文ID；正常表不传
            datasource: [
                {
                    cluster_name: datasource.cluster_name,
                    linkis_datasource_name: datasource.linkis_datasource_name,
                    type: datasource.type,
                    // linkis_datasource_version_id: 1,
                    linkis_datasource_id: datasource.linkis_datasource_id,
                    db_name: datasource.db_name,
                    table_name: datasource.table_name,
                    col_names,
                    linkis_datasource_type: ['mysql', 'tdsql'].includes(datasource.type) ? datasource.type : null,
                },
            ],
        });
        // eslint-disable-next-line camelcase
        if (isEmbedInFrame.value && rule_group_id) {
            DWSMessage(route.query.nodeId, rule_group_id, 'add');
        }
        // 重制数据
        datasource.value = {
            isUpStream: false,
        };
        FMessage.success('复制规则成功');
        // 获取ruleid然后跳转
        // eslint-disable-next-line camelcase
        const { rule_list } = await FRequest(`api/v1/projector/rule/group/${rule_group_id}`, {}, 'get');
        // 跳转到新建的规则
        // eslint-disable-next-line camelcase
        router.replace(`/projects/rules?ruleGroupId=${rule_group_id}&id=${rule_list[0].rule_id}&workflowProject=${workflowProject}&projectId=${props.projectId}`);
        // 上面的url变更并不会通知组件强制刷新
        setTimeout(() => {
            // 通知组件重新渲染
            eventbus.emit(forceFormPageUpdateEvent);
        }, 0);
        toggleModal();
        isLoading.value = false;
    } catch (err) {
        console.warn(err);
        isLoading.value = false;
    }
};

onMounted(() => {
    loadProjectList();
});
</script>
