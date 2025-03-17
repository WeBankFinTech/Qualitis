<template>
    <div>
        <FDrawer
            :show="show"
            :title="$t('_.运维报表订阅管理')"
            width="50%"
            @cancel="() => { emit('update:show', false) }"
        >
            <div>
                <BTablePage layout="drawer" :isDivider="true" :isLoading="isLoading" :actionType="actionType" :loadingText="loadingText">
                    <template v-slot:search>
                        <BSearch v-model:form="searchForm" @search="handleSearch" @reset="handleReset">
                            <template v-slot:form>
                                <div>
                                    <span class="condition-label">{{$t('_.项目名称')}}</span>
                                    <FInput v-model="searchForm.project_name" clearable :placeholder="$t('_.搜索项目名称')">
                                    </FInput>
                                </div>
                                <div>
                                    <span class="condition-label">{{$t('_.接收人')}}</span>
                                    <FSelect v-model="searchForm.receiver" filterable clearable :options="projectUserList" :placeholder="$t('_.请选择')">
                                    </FSelect>
                                </div>
                            </template>
                            <template v-slot:exButton>
                                <FButton @click="newSubscribe">{{$t('_.新增订阅')}}</FButton>
                            </template>
                        </BSearch>
                    </template>
                    <template v-slot:table>
                        <f-table :data="tableShowLists">
                            <f-table-column prop="project_name" :label="$t('_.项目名称')" :width="280" ellipsis />
                            <f-table-column prop="receiver" :label="$t('_.接收人')" :width="150" ellipsis />
                            <f-table-column #default="{ row }" prop="execution_frequency" :label="$t('_.执行频率')" :width="88" ellipsis>{{frequencyOptions.find(item => +item.value === +row.execution_frequency)?.label || '--'}}</f-table-column>
                            <f-table-column prop="create_user" :label="$t('_.创建人')" :width="120" ellipsis />
                            <f-table-column prop="create_time" :label="$t('_.创建时间')" :width="182" ellipsis />
                            <f-table-column :label="$t('common.operate')" :width="104" fixed="right" ellipsis>
                                <template #default="{ row }">
                                    <ul class="wd-table-operate-btns">
                                        <li
                                            v-for="(action, index) in ruleTemplateTableActions"
                                            :key="index"
                                            class="btn-item"
                                            :class="action.class"
                                            @click="action.func(row, $event)">
                                            {{action.label}}
                                        </li>
                                    </ul>
                                </template>
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
                            @change="fetchTableData"
                            @pageSizeChange="fetchTableData" />
                    </template>
                </BTablePage>
            </div>
        </FDrawer>
        <!-- 新增/编辑订阅弹窗 -->
        <NewSubscribeModel
            v-model:show="showNewSubscribeModel"
            v-model:modelType="modelType"
            v-model:modelData="editData"
            @save="fetchTableData"
        />
    </div>
</template>

<script setup>

import {
    computed, onMounted, onUpdated, ref, nextTick, reactive, defineEmits, inject,
} from 'vue';
import { BTablePage, BSearch, formatterEmptyValue } from '@fesjs/traction-widget';
import { useI18n, useRoute } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import { FModal, FMessage } from '@fesjs/fes-design';
import NewSubscribeModel from './newSubscribeModel';
import {
    fetchOprReportList, deleteOprReport, getOprDetailReport,
} from '../api.js';

const { t: $t } = useI18n();
const frequencyOptions = reactive([
    { value: '1', label: $t('_.每天') },
    { value: '2', label: $t('_.每周') },
]);
// 获取用户列表
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();

const route = useRoute();
// eslint-disable-next-line no-undef
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
});
const projectType = inject('projectType');

// eslint-disable-next-line no-undef
const emit = defineEmits(['update:show']);
const pagination = reactive({
    page: 1,
    size: 10,
    total: 0,
});
const searchForm = ref({ receiver: '', project_name: '' });
const isLoading = ref(false);
const actionType = ref('loading');
const showDrawer = ref(false);
const loadingText = {
    loading: $t('_.自定义Loading文字'),
    emptyInitResult: $t('_.这里还没有数据'),
    emptyQueryResult: $t('_.没有符合条件的结果'),
};

const tableShowLists = ref([]);

const searchModel = ref();

// 获取表格数据
const fetchTableData = async () => {
    isLoading.value = true;
    actionType.value = 'loading';

    try {
        const pageParams = {
            page: pagination.page - 1,
            size: pagination.size,
            project_type: projectType,
        };
        const params = { ...pageParams, ...searchModel.value };
        const result = await fetchOprReportList(params);
        tableShowLists.value = result.data;
        pagination.total = result.total;
        isLoading.value = false;
    } catch (e) {
        console.log(e);
    }
    if (pagination.total === 0) {
        actionType.value = 'emptyQueryResult';
    }
};
const modelType = ref('add');
const showNewSubscribeModel = ref(false);
const editData = ref({});
const ruleTemplateTableActions = [
    {
        label: $t('_.编辑'),
        func: async (row) => {
            modelType.value = 'edit';
            try {
                const res = await getOprDetailReport(row.id);
                editData.value = cloneDeep({
                    project_ids: res.project_ids,
                    receiver: res.receiver,
                    execution_frequency: (res.execution_frequency).toString(),
                    id: res.id,
                });
                showNewSubscribeModel.value = true;
            } catch (err) {
                console.log('err', err);
            }
        },
    },
    {
        label: $t('_.删除'),
        class: 'btn-delete',
        func: async (row) => {
            FModal.confirm({
                title: $t('common.prompt'),
                content: $t('_.确认删除该订阅？'),
                onOk: async () => {
                    try {
                        const res = await deleteOprReport(row.id);
                        console.log('res', res);
                        FMessage.success($t('toastSuccess.deleteSuccess'));
                        fetchTableData();
                    } catch (err) {
                        console.log(err);
                        return Promise.reject();
                    }
                },
            });
        },
    },
];
// 查询操作
const handleSearch = (e) => {
    searchModel.value = cloneDeep(searchForm.value);
    fetchTableData();
};

// 重置操作
const handleReset = () => {
    pagination.page = 1;
    searchModel.value = {};
    fetchTableData();
};

// 打开抽屉
const openDrawer = () => {
    showDrawer.value = true;
};
onMounted(async () => {
    fetchTableData();
    await getProjectUserList();
    projectUserList.value = projectUserList.value.map(item => ({ value: item, label: item }));
});
const newSubscribe = () => {
    modelType.value = 'add';
    showNewSubscribeModel.value = true;
};

</script>;

<style scoped lang="less">

</style>
