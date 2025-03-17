<template>
    <div style="padding: 16px 16px 32px">
        <BTablePage :isLoading="isLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <Query
                    v-model:advancedValue="advancedConditions"
                    v-model:commonValue="commonConditions"
                    :clusterList="clusterList"
                    :tableList="tableList"
                    :dbsLists="dbsLists"
                    :partitionLists="partitionLists"
                    :userList="userList"
                    @search="search"
                    @reset="reset" />
            </template>
            <template v-slot:operate>
                <template v-if="!isBatch">
                    <FSpace :size="16">
                        <FButton type="primary" class="button" @click="openDrawer">{{$t('_.新增采集配置')}}</FButton>
                        <FButton type="primary" class="button" @click="openSchedule">{{$t('_.发布采集调度')}}</FButton>
                        <FButton class="button" @click="batchdete">{{$t('_.批量删除')}}</FButton>
                        <FButton class="button" @click="clickConfigTable">{{$t('_.设置表格')}}</FButton>
                    </FSpace>
                </template>
                <template v-else>
                    <FSpace>
                        <FButton type="primary" class="button" @click="confirm">{{$t('_.确认删除')}}</FButton>
                        <FButton type="default" @click="batchCancel">{{$t('_.取消')}}</FButton>
                    </FSpace>
                </template>
            </template>
            <template v-slot:table>
                <f-table ref="listTableRef" rowKey="id" :data="tableLists" @selectionChange="changeSelection">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :visible="isBatch" fixed type="selection" :width="32" />
                    <f-table-column :formatter="tableFormatter" prop="template_id" :visible="checkTColShow('template_id')" fixed :label="$t('_.算子ID')" align="left" :width="160" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="en_name" :visible="checkTColShow('en_name')" fixed :label="$t('_.算子英文名称')" align="left" :width="160" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="cn_name" :visible="checkTColShow('cn_name')" :label="$t('_.算子中文名称')" align="left" :width="160" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="cluster_name" :visible="checkTColShow('cluster_name')" :label="$t('_.集群')" align="left" :width="120" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="database" :visible="checkTColShow('database')" :label="$t('_.数据库')" align="left" :width="180" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="table" :visible="checkTColShow('table')" :label="$t('_.数据表')" align="left" :width="180" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="partition" :visible="checkTColShow('partition')" :label="$t('_.采集分区')" align="left" :width="138" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="column" :visible="checkTColShow('column')" :label="$t('_.采集字段')" align="left" :width="160" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="exec_freq" :visible="checkTColShow('exec_freq')" :label="$t('_.采集频率')" align="left" :width="120" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="execution_parameters_name" :visible="checkTColShow('execution_parameters_name')" :label="$t('_.执行参数')" align="left" :width="88" ellipsis>
                        <template #default="{ row = {}}">
                            <div v-if="row.execution_parameters_name" class="a-link" @click="openExecutionDrawer(row)">{{$t('_.查看参数')}}</div>
                            <div v-else>{{$t('_.暂无数据')}}</div>
                        </template>
                    </f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="proxy_user" :visible="checkTColShow('proxy_user')" :label="$t('_.代理用户')" align="left" :width="130" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="create_name" :visible="checkTColShow('create_name')" :label="$t('_.创建人')" align="left" :width="130" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="create_time" :visible="checkTColShow('create_time')" :label="$t('_.创建时间')" align="left" :width="186" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="update_name" :visible="checkTColShow('update_name')" :label="$t('_.更新人')" align="left" :width="130" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="update_time" :visible="checkTColShow('update_time')" :label="$t('_.更新时间')" align="left" :width="186" ellipsis></f-table-column>
                    <f-table-column #default="{ row } = {}" prop="actions" class="actions" :label="$t('_.操作')" align="left" :width="148" fixed="right">
                        <div class="actions">
                            <a class="a-link" @click="openeditDrawer('view', row)">{{$t('_.查看')}}</a>
                            <a class="a-link" @click="openeditDrawer('edit', row)">{{$t('_.编辑')}}</a>
                            <a class="del-item" @click="singleDelete(row)">{{$t('_.删除')}}</a>
                        </div>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-model:currentPage="pagination.page"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="loadListData"
                    @pageSizeChange="loadListData"
                ></FPagination>
            </template>
        </BTablePage>
        <!--设置表格-->
        <BTableHeaderConfig
            v-model:headers="projectTableHeaders"
            v-model:show="showProjectTableColConfig"
            :originHeaders="originFunctionHeaders"
            type="collect_management"
        />
        <SideDrawer
            v-if="drawerShow"
            v-model:show="drawerShow"
            :data="drawerFormData"
            :type="type"
            @submit="handleSubmit"
        />
        <CollectSchedule v-if="isScheduleShow" v-model:isScheduleShow="isScheduleShow" @scheduleSubmit="handleSubmit" />
        <EditDrawer
            v-if="isEditShow"
            v-model:isEditShow="isEditShow"
            v-model:drawerTitle="drawerTitle"
            v-model:mode="mode"
            :templateForm="drawerFormData"
            :templateList="templateList"
            @editSubmit="handleSubmit"
            @deleteTask="handleSubmit" />
        <AddDrawer
            v-if="isAddShow"
            v-model:isAddShow="isAddShow"
            @addSubmit="handleSubmit" />
        <FDrawer
            v-model:show="showExecuteParamsDrawer"
            :title="$t('_.执行参数')"
            displayDirective="if"
            :footer="false"
            width="50%"
            contentClass="template-drawer"
            @cancel="clearSelectedRow"
        >
            <ProjectTemplate ref="templateRef" :isEmbed="true" :isOperate="false" :showHeader="false" :curExeName="curExeName" class="project-template-style"></ProjectTemplate>
        </FDrawer>
    </div>
</template>
<script setup>

import {
    ref, reactive, provide, onMounted, computed, unref,
} from 'vue';
import {
    useI18n, request, useRouter, useRoute,
} from '@fesjs/fes';
import dayjs from 'dayjs';
import {
    PlusCircleFilled, MoreCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { FMessage, FModal } from '@fesjs/fes-design';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
// import TableHeaderConfig from '@/components/TableHeaderConfig';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import { cloneDeep, method } from 'lodash-es';
import { getURLQueryParams } from '@/common/utils';
import ProjectTemplate from '@/pages/projects/template';
import Query from './components/query.vue';
import SideDrawer from './components/sideDrawer.vue';
import useBasicOptions from './hooks/useBasicOptions';
import { collectDelete } from './api';
import CollectSchedule from './components/collectSchedule.vue';
import EditDrawer from './components/editDrawer.vue';
import AddDrawer from './addDrawer.vue';

const router = useRouter();
const route = useRoute();
const {
    clusterList,
    tableList,
    dbsLists,
    userList,
    engineList,
    implTypeList,
    templateList,
    partitionLists,
} = useBasicOptions();
const pagination = ref({
    page: 1,
    size: 10,
    total: 1,
});
const { t: $t } = useI18n();
// 表头设置
const originFunctionHeaders = [
    { prop: 'template_id', label: $t('_.算子ID') },
    { prop: 'en_name', label: $t('_.算子英文名称') },
    { prop: 'cn_name', label: $t('_.算子中文名称') },
    { prop: 'cluster_name', label: $t('_.集群') },
    { prop: 'database', label: $t('_.数据库') },
    { prop: 'table', label: $t('_.数据表') },
    { prop: 'partition', label: $t('_.采集分区') },
    { prop: 'column', label: $t('_.采集字段') },
    { prop: 'exec_freq', label: $t('_.采集频率') },
    { prop: 'execution_parameters_name', label: $t('_.执行参数') },
    { prop: 'proxy_user', label: $t('_.代理用户') },
    { prop: 'create_name', label: $t('_.创建人') },
    { prop: 'update_name', label: $t('_.更新人') },
    { prop: 'create_time', label: $t('_.创建时间') },
    { prop: 'update_time', label: $t('_.更新时间') },
];
const projectTableHeaders = ref([]);
const checkTColShow = col => projectTableHeaders.value.map(item => item.prop).includes(col);
const showProjectTableColConfig = ref(false);
const clickConfigTable = () => {
    showProjectTableColConfig.value = true;
};
const listTableRef = ref(null);
// 表格加载
const isLoading = ref(false);
// 列表数据
const tableLists = ref([]);
// 查询条件
const advancedConditions = ref({});
const commonConditions = ref({});
// 加载表格数据
const loadListData = async () => {
    try {
        // eslint-disable-next-line no-use-before-define
        isLoading.value = true;
        // eslint-disable-next-line no-use-before-define
        const formatConditions = cloneDeep(curConditions.value);
        const keys = Object.keys(formatConditions);
        for (let i = 0; i < keys.length; i++) {
            if (!formatConditions[keys[i]] || ((Array.isArray(formatConditions[keys[i]]) && formatConditions[keys[i]].length === 0))) {
                delete formatConditions[keys[i]];
            }
        }
        if (formatConditions?.createTimeRange?.length > 1) {
            formatConditions.create_start_time = dayjs(formatConditions?.createTimeRange[0]).format('YYYY-MM-DD HH:mm:ss');
            formatConditions.create_end_time = dayjs(formatConditions?.createTimeRange[1]).format('YYYY-MM-DD HH:mm:ss');
            delete formatConditions.createTimeRange;
        }
        if (formatConditions?.updateTimeRange?.length > 1) {
            formatConditions.update_start_time = dayjs(formatConditions?.updateTimeRange[0]).format('YYYY-MM-DD HH:mm:ss');
            formatConditions.update_end_time = dayjs(formatConditions?.updateTimeRange[1]).format('YYYY-MM-DD HH:mm:ss');
            delete formatConditions.updateTimeRange;
        }
        const { data = [], total = 0 } = await request('api/v1/projector/imsmetric/collect/list', Object.assign({
            page: pagination.value.page - 1,
            size: pagination.value.size,
        }, formatConditions));
        tableLists.value = data;
        pagination.value.total = total;
        isLoading.value = false;
    } catch (err) {
        console.warn(err);
        isLoading.value = false;
    }
};
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
const conditions = computed(() => Object.assign({}, commonConditions.value, advancedConditions.value));
// 搜索条件在点击查询时再进行更新
const curConditions = ref({});
// 查询
const search = async (visDivisions = '', selectVisDate = '') => {
    pagination.value.page = 1;
    // eslint-disable-next-line no-use-before-define
    curConditions.value = cloneDeep(conditions.value);
    await loadListData();
    resultByInit.value = false;
};

// Drawer相关
const getInitFormData = (() => reactive({
    cn_name: '',
    partition: '',
    proxy_user: '',
    collect_configs: [{
        collect_type: true,
        calcu_unit_configs: [{
            template_id: null,
            columns: null,
        }],
    }],
}));
const drawerFormData = ref({});
const type = ref('add');
const drawerShow = ref(false);
const isEditShow = ref(false);
const drawerTitle = ref('');
const mode = ref('view');
const openeditDrawer = async (drawerType, data = getInitFormData()) => {
    mode.value = drawerType;
    const detail = await request(`api/v1/projector/imsmetric/collect/detail/${data.id}`, {}, { method: 'get' });
    // 传进去做级联组件初始化
    drawerTitle.value = drawerType === 'edit' ? `${$t('common.editCollectConfig')}(${detail.en_name}）` : `${$t('common.viewCollectConfig')}（${detail.en_name}）`;
    detail.collect_obj = [detail.cluster_name, detail.database, detail.table];
    const ress = await request('api/v1/projector/imsmetric/collect/scheduler/detail', { database: detail.database, table: detail.table, partition: detail.partition });
    drawerFormData.value = { ...detail, ...ress };
    drawerFormData.value.id = data.id;
    isEditShow.value = true;
};
const projectId = ref('');
const isAddShow = ref(false);
const openDrawer = async () => {
    isAddShow.value = true;
};
const isScheduleShow = ref(false);
const openSchedule = () => {
    isScheduleShow.value = true;
};
// 执行参数详情
const curExeName = ref('');
const showExecuteParamsDrawer = ref(false);
// 选择模板
const templateRef = ref(null);
// 关闭规则参数面板
const clearSelectedRow = () => {
    curExeName.value = '';
    showExecuteParamsDrawer.value = false;
};
const openExecutionDrawer = (row) => {
    curExeName.value = row.execution_parameters_name;
    showExecuteParamsDrawer.value = true;
};

// 删除相关
const isCommiting = ref(false);
const singleDelete = async (row) => {
    FModal.confirm({
        title: $t('_.删除'),
        content: $t('_.确认删除吗？'),
        okText: $t('_.确认'),
        async onOk() {
            try {
                if (isCommiting.value) return;
                isCommiting.value = true;

                await collectDelete([row.id]);
                FMessage.success($t('_.操作成功'));
                isCommiting.value = false;
                await search();
            } catch (err) {
                console.warn(err);
                isCommiting.value = false;
            }
        },
    });
};
const isBatch = ref(false);
const batchdete = () => {
    isBatch.value = true;
};
// 批量相关
const tableSelection = ref([]);
const changeSelection = (selection) => {
    tableSelection.value = Object.values(selection);
};
const batchCancel = () => {
    isBatch.value = false;
    tableSelection.value = [];
    listTableRef.value.clearSelection();
};
const confirm = async () => {
    console.log('已选中的值:', tableSelection.value.length);
    if (!tableSelection.value.length) return FMessage.error($t('_.请选择数据'));
    FModal.confirm({
        title: $t('_.删除'),
        content: $t('_.数据删除后不可恢复，确定删除勾选的所有算子吗？'),
        okText: $t('_.确认'),
        async onOk() {
            try {
                if (isCommiting.value) return;
                isCommiting.value = true;
                await collectDelete(tableSelection.value);
                FMessage.success($t('_.操作成功'));
                isCommiting.value = false;
                await search();
                batchCancel();
            } catch (err) {
                console.warn(err);
                isCommiting.value = false;
            }
        },
    });
};
const geTProjectid = async () => {
    const res = await request('api/v1/projector/imsmetric/collect/project_info', {}, { method: 'get' });
    if (res.project_id) {
        projectId.value = res.project_id;
        router.replace(
            `${route.path}?${getURLQueryParams({
                query: { projectId: res.project_id },
            })}`,
        );
    }
};
onMounted(async () => {
    try {
        await loadListData();
        geTProjectid();
    } catch (error) {
        console.warn(error);
    }
});

// 格式化列表数据
const tableFormatter = ({
    row, column, rowIndex, columnIndex, cellValue,
}) => {
    if (!cellValue) return '--';
};

// 重置列表和查询条件
const reset = async () => {
    pagination.value.page = 1;
    advancedConditions.value = {};
    commonConditions.value = {};
    curConditions.value = {};
    await loadListData();
    resultByInit.value = true;
};
// 处理提交逻辑
const handleSubmit = async () => {
    await reset();
};

</script>
<style lang="less" scoped>
@import "@/style/varible";
@import "./index.less";
.del-item {
    color: #F75F56;
    cursor: pointer;
}
</style>
