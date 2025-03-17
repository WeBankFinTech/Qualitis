<template>
    <div style="padding: 16px 16px 32px">
        <BTablePage :isLoading="isLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <Query v-model:advancedValue="advancedConditions" v-model:commonValue="commonConditions" @search="search" @reset="reset" />
            </template>
            <template v-slot:operate>
                <FSpace :size="16">
                    <FButton type="primary" class="button" @click="openDrawer('add')"><PlusCircleFilled />{{$t('_.新建函数')}}</FButton>
                    <FDropdown trigger="hover" :options="options" @click="select">
                        <FButton type="default"><MoreCircleOutlined />{{$t('_.更多')}}</FButton>
                    </FDropdown>
                </FSpace>
            </template>
            <template v-slot:table>
                <f-table ref="functionTable" :data="functions">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :formatter="tableFormatter" prop="cn_name" :visible="checkTColShow('cn_name')" :label="$t('functionManagement.cn_name')" align="left" :minWidth="172">
                        <template #default="{ row }">
                            <clipboard :val="row.cn_name" />
                        </template>
                    </f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="name" :visible="checkTColShow('name')" :label="$t('functionManagement.name')" align="left" :minWidth="159">
                        <template #default="{ row }">
                            <clipboard :val="row.name" />
                        </template>
                    </f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="desc" :visible="checkTColShow('desc')" :label="$t('functionManagement.desc')" align="left" :minWidth="186" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="enter" :visible="checkTColShow('enter')" :label="$t('functionManagement.enter')" align="left" :minWidth="174" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="return" :visible="checkTColShow('return')" :label="$t('functionManagement.return')" align="left" :minWidth="140" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="version" :visible="checkTColShow('version')" :label="$t('functionManagement.version')" align="left" :minWidth="88" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="dir" :visible="checkTColShow('dir')" :label="$t('functionManagement.dir')" align="left" :minWidth="88" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="impl_type" :visible="checkTColShow('impl_type')" :label="$t('functionManagement.impl_type')" align="left" :minWidth="88" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="enable_engine" :visible="checkTColShow('enable_engine')" :label="$t('functionManagement.enable_engine')" align="left" :minWidth="88" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="enable_cluster" :visible="checkTColShow('enable_cluster')" :label="$t('functionManagement.enable_cluster')" align="left" :minWidth="88" ellipsis></f-table-column>
                    <f-table-column #default="{ row } = {}" prop="status" :visible="checkTColShow('status')" :label="$t('functionManagement.status')" align="left" :minWidth="88" ellipsis>
                        <span :class="[row.status ? 'enable' : 'disable']">{{row.status ? $t('_.启用') : $t('_.禁用')}}</span>
                    </f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="create_user" :visible="checkTColShow('create_user')" :label="$t('functionManagement.create_user')" align="left" :minWidth="120" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="modify_user" :visible="checkTColShow('modify_user')" :label="$t('functionManagement.modify_user')" align="left" :minWidth="120" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="create_time" :visible="checkTColShow('create_time')" :label="$t('functionManagement.create_time')" align="left" :minWidth="160" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="modify_time" :visible="checkTColShow('modify_time')" :label="$t('functionManagement.modify_time')" align="left" :minWidth="160" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="dev_department_name" :visible="checkTColShow('devDepartment')" :label="$t('functionManagement.devDepartment')" align="left" :minWidth="240" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="ops_department_name" :visible="checkTColShow('opsDepartment')" :label="$t('functionManagement.opsDepartment')" align="left" :minWidth="240" ellipsis></f-table-column>
                    <f-table-column :formatter="tableFormatter" prop="visibility_department_list" :visible="checkTColShow('visibility_department')" :label="$t('functionManagement.visibility_department')" align="left" :minWidth="240" ellipsis></f-table-column>
                    <f-table-column #default="{ row } = {}" prop="actions" class="actions" :label="$t('functionManagement.actions')" align="center" :minWidth="104" fixed="right">
                        <div class="actions">
                            <a class="a-link" @click="changeStatus(row)">{{row.status ? $t('_.启用') : $t('_.禁用')}}</a>
                            <a class="a-link" @click="openDrawer('display', row)">{{$t('_.详情')}}</a>
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
            type="function_management"
        />
        <SideDrawer
            v-if="drawerShow"
            v-model:show="drawerShow"
            :data="drawerFormData"
            :type="type"
            @submit="handleSubmit"
        />
    </div>
</template>
<script setup>

import {
    ref, reactive, provide, onMounted, computed, unref,
} from 'vue';
import {
    useI18n, request,
} from '@fesjs/fes';
import {
    PlusCircleFilled, MoreCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { FMessage, FModal } from '@fesjs/fes-design';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import clipboard from '@/components/clipboard';
// import TableHeaderConfig from '@/components/TableHeaderConfig';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import { cloneDeep } from 'lodash-es';
import Query from './components/query.vue';
import SideDrawer from './components/sideDrawer.vue';
import useBasicOptions from './hooks/useBasicOptions';

const {
    clusterList,
    engineList,
    implTypeList,
    dirList,
    getClusterList,
} = useBasicOptions();
const pagination = ref({
    page: 1,
    size: 10,
    total: 1,
});
const { t: $t } = useI18n();
// 表头设置
const originFunctionHeaders = [
    { prop: 'cn_name', label: $t('functionManagement.cn_name') },
    { prop: 'name', label: $t('functionManagement.name') },
    { prop: 'desc', label: $t('functionManagement.desc') },
    { prop: 'enter', label: $t('functionManagement.enter') },
    { prop: 'return', label: $t('functionManagement.return') },
    { prop: 'version', label: $t('functionManagement.version') },
    { prop: 'dir', label: $t('functionManagement.dir') },
    { prop: 'impl_type', label: $t('functionManagement.impl_type') },
    { prop: 'enable_engine', label: $t('functionManagement.enable_engine') },
    { prop: 'enable_cluster', label: $t('functionManagement.enable_cluster') },
    { prop: 'status', label: $t('functionManagement.status') },
    { prop: 'create_user', label: $t('functionManagement.create_user') },
    { prop: 'modify_user', label: $t('functionManagement.modify_user') },
    { prop: 'create_time', label: $t('functionManagement.create_time'), hidden: true },
    { prop: 'modify_time', label: $t('functionManagement.modify_time'), hidden: true },
    { prop: 'devDepartment', label: $t('functionManagement.devDepartment') },
    { prop: 'opsDepartment', label: $t('functionManagement.opsDepartment') },
    { prop: 'visibility_department', label: $t('functionManagement.visibility_department'), hidden: true },
];
// // 未设置过缓存，默认不展示创建时间/修改时间/可见范围
// if (!localStorage.getItem('function_management_table_config')) {
//     localStorage.setItem('function_management_table_config', JSON.stringify({
//         active: [
//             {
//                 prop: 'cn_name',
//                 label: 'functionManagement.cn_name',
//             },
//             {
//                 prop: 'name',
//                 label: 'functionManagement.name',
//             },
//             {
//                 prop: 'desc',
//                 label: 'functionManagement.desc',
//             },
//             {
//                 prop: 'enter',
//                 label: 'functionManagement.enter',
//             },
//             {
//                 prop: 'return',
//                 label: 'functionManagement.return',
//             },
//             {
//                 prop: 'version',
//                 label: 'functionManagement.version',
//             },
//             {
//                 prop: 'dir',
//                 label: 'functionManagement.dir',
//             },
//             {
//                 prop: 'impl_type',
//                 label: 'functionManagement.impl_type',
//             },
//             {
//                 prop: 'enable_engine',
//                 label: 'functionManagement.enable_engine',
//             },
//             {
//                 prop: 'enable_cluster',
//                 label: 'functionManagement.enable_cluster',
//             },
//             {
//                 prop: 'status',
//                 label: 'functionManagement.status',
//             },
//             {
//                 prop: 'create_user',
//                 label: 'functionManagement.create_user',
//             },
//             {
//                 prop: 'modify_user',
//                 label: 'functionManagement.modify_user',
//             },
//             {
//                 prop: 'devDepartment',
//                 label: 'functionManagement.devDepartment',
//             },
//             {
//                 prop: 'opsDepartment',
//                 label: 'functionManagement.opsDepartment',
//             },
//         ],
//         inActive: [
//             {
//                 prop: 'create_time',
//                 label: 'functionManagement.create_time',
//             },
//             {
//                 prop: 'modify_time',
//                 label: 'functionManagement.modify_time',
//             },
//             {
//                 prop: 'visibility_department',
//                 label: 'functionManagement.visibility_department',
//             },
//         ],
//     }));
// }
// const {
//     checkTColShow,
//     toggleTColConfig,
//     projectTableHeaders,
//     showProjectTableColConfig,
// } = useProjectTable({
//     data: originFunctionHeaders,
//     key: 'function_management',
// });
const projectTableHeaders = ref([]);
const checkTColShow = col => projectTableHeaders.value.map(item => item.prop).includes(col);
provide('clusterList', clusterList);
// 更多
const options = ref([{ value: 'configTable', label: $t('_.设置表格') }]);
const showProjectTableColConfig = ref(false);
const select = (type) => {
    switch (type) {
        case 'configTable':
            showProjectTableColConfig.value = true;
            break;
        default:
            break;
    }
};
// 表格加载
const isLoading = ref(false);
// 列表数据
const functions = ref([]);
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
        formatConditions.dev_department_name = formatConditions.dev_department_name?.join('/') || '';
        formatConditions.ops_department_name = formatConditions.ops_department_name?.join('/') || '';
        const keys = Object.keys(formatConditions);
        for (let i = 0; i < keys.length; i++) {
            if (!formatConditions[keys[i]] || ((Array.isArray(formatConditions[keys[i]]) && formatConditions[keys[i]].length === 0))) {
                delete formatConditions[keys[i]];
            }
        }
        const { content = [], totalCount = 0 } = await request('/api/v1/projector/meta_data/udf/all', Object.assign({
            page: pagination.value.page - 1,
            size: pagination.value.size,
        }, formatConditions));
        functions.value = content;
        pagination.value.total = totalCount;
        isLoading.value = false;
    } catch (err) {
        console.warn(err);
        isLoading.value = false;
    }
};
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
// 查询
const search = async (visDivisions = '', selectVisDate = '') => {
    pagination.value.page = 1;
    // eslint-disable-next-line no-use-before-define
    curConditions.value = cloneDeep(conditions.value);
    // eslint-disable-next-line no-use-before-define
    if (visDivisions) {
        // eslint-disable-next-line no-use-before-define, array-callback-return
        curConditions.value.visibility_department_list = Array.isArray(selectVisDate.value[0]) ? selectVisDate.value.map((item) => {
            if (item.length === 1) {
                const target = visDivisions.value.find(division => division.label === item[0]);
                const id = target.code;
                const name = target.label;
                return {
                    id,
                    name,
                };
            }

            const target = visDivisions.value.find(division => division.label === item[0]);
            const id = target.children.find(division => division.label === item[1]).code;
            const name = `${target.label}/${target.children.find(division => division.label === item[1]).label}`;
            return {
                id,
                name,
            };
        }) : selectVisDate.value;
    }

    await loadListData();
    resultByInit.value = false;
};
const conditions = computed(() => Object.assign({}, commonConditions.value, advancedConditions.value));
// 搜索条件在点击查询时再进行更新
const curConditions = ref({});
// Drawer相关
const getInitFormData = (() => reactive({
    cn_name: '',
    name: '',
    desc: '',
    enter: '',
    return: '',
    dir: '',
    impl_type: '',
    enable_engine: [],
    enable_cluster: [],
    status: true,
    dev_department_name: [],
    dev_department_id: '',
    ops_department_name: [],
    ops_department_id: '',
    visibility_department_list: [],
}));
const drawerFormData = ref({});
const type = ref('add');
const drawerShow = ref(false);
const openDrawer = async (drawerType, data = getInitFormData()) => {
    type.value = drawerType;
    if (drawerType === 'display') {
        // eslint-disable-next-line no-use-before-define
        if (!editable.value) return;
        const detail = await request('/api/v1/projector/meta_data/udf/detail', { id: data.id }, 'get');
        // 传进去做级联组件初始化
        detail.original_list = cloneDeep(detail.visibility_department_list);
        detail.visibility_department_list = detail.visibility_department_list?.map(item => item.name?.split('/') || []) || [];
        detail.dev_department_name = detail.dev_department_name.split('/');
        detail.ops_department_name = detail.ops_department_name.split('/');
        drawerFormData.value = detail;
    } else {
        drawerFormData.value = data;
    }
    drawerShow.value = true;
};
onMounted(async () => {
    try {
        await getClusterList();
        await loadListData();
    } catch (error) {
        console.warn(error);
    }
});

// 格式化列表数据
const tableFormatter = ({
    row, column, rowIndex, columnIndex, cellValue,
}) => {
    switch (column.props.prop) {
        case 'impl_type':
            return implTypeList.value.find(item => item.value === row.impl_type)?.label || '--';
        case 'enable_engine':
            if (!Array.isArray(row.enable_engine) || row.enable_engine.length === 0) return '--';
            return row.enable_engine.map(rowItem => engineList.value.find(item => item.value === rowItem)?.label)?.join(', ') || '--';
        case 'enable_cluster':
            if (!Array.isArray(row.enable_cluster) || row.enable_cluster.length === 0) return '--';
            return row.enable_cluster?.join(', ') || '--';
        case 'visibility_department_list':
            console.log(row.visibility_department_list);
            if (!Array.isArray(row.visibility_department_list) || row.visibility_department_list.length === 0) return '--';
            return row.visibility_department_list.map(item => item.name).join(', ') || '--';
        default:
            if (!cellValue) return '--';
    }
};

// 重置列表和查询条件
const reset = async () => {
    pagination.value.page = 1;
    advancedConditions.value = {
        enable_cluster: [],
        dir: '',
        impl_type: '',
        enable_engine: [],
        create_user: '',
        modify_user: '',
        dev_department_name: [],
        ops_department_name: [],
    };
    commonConditions.value = {};
    curConditions.value = {};
    await loadListData();
    resultByInit.value = true;
};
// 处理提交逻辑
const handleSubmit = async () => {
    await reset();
};
// 启停
const editable = ref(true);
const changeStatus = async (row) => {
    if (!editable.value) return;
    FModal.confirm({
        title: row.status ? $t('_.停用函数') : $t('_.启用函数'),
        content: `确认${row.status ? $t('_.停用') : $t('_.启用')}此函数?`,
        okText: $t('_.确认'),
        onOk: async () => {
            try {
                editable.value = false;
                const res = await request('/api/v1/projector/meta_data/udf/switch', { id: row.id, is_load: !row.status }, 'get');
                editable.value = true;
                FMessage.success(row.status ? $t('_.停用成功') : $t('_.启用成功'));
                await reset();
            } catch (err) {
                editable.value = true;
                console.warn(err);
            }
        },
    });
};

</script>
<style lang="less" scoped>
@import "@/style/varible";
@import "./index.less";
</style>
