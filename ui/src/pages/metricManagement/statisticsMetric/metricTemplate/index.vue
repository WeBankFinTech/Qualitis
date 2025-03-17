<template>
    <div class="wd-content metricTemplate" style="padding: 16px 16px 32px">
        <BTablePage :isLoading="isLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch v-model:form="searchForm" v-model:advanceForm="advanceForm" :is-advance="true"
                         @advance="showAdvance = true" @search="handleInit" @reset="handleReset">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('ruleTemplatelist.templateENFullName')}}</span>
                            <FInput v-model="searchForm.en_name" :placeholder="$t('common.pleaseEnter')" clearable />
                        </div>
                        <div>
                            <span class="condition-label">{{$t('ruleTemplatelist.templateCNFullName')}}</span>
                            <FInput v-model="searchForm.cn_name" :placeholder="$t('common.pleaseEnter')" clearable />
                        </div>
                        <!-- <div>
                            <span class="condition-label">{{$t('ruleTemplatelist.developDepartment')}}</span>
                            <FSelectCascader v-model="searchForm.dev_department_id" style="width: 160px"
                                             :data="devDivisions" :loadData="loadDevDivisions" valueField="code" clearable remote
                                             showPath checkStrictly="child"></FSelectCascader>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('ruleTemplatelist.maintainDepartment')}}</span>
                            <FSelectCascader v-model="searchForm.ops_department_id" style="width: 160px"
                                             :data="opsDivisions" :loadData="loadOpsDivisions" valueField="code" clearable remote
                                             showPath checkStrictly="child"></FSelectCascader>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('ruleTemplatelist.creator')}}</span>
                            <FSelect v-model="searchForm.create_name" :options="formatProjectUserList" clearable filterable />
                        </div> -->
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <FButton type="primary" @click="create">
                    {{$t('_.新增采集算子')}}<template #icon>
                        <PlusOutlined />
                    </template>
                </FButton>
            </template>
            <template v-slot:table>
                <FTable ref="tableRef" :data="tableList">
                    <template #empty>
                        <BPageLoading :actionType="isLoading ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <FTable-column prop="template_id" :label="$t('_.算子ID')" :width="160" ellipsis
                                   :formatter="fillText" />
                    <FTable-column prop="en_name" :label="$t('ruleTemplatelist.templateENFullName')" :width="160"
                                   ellipsis :formatter="fillText" />
                    <FTable-column prop="template_name" :label="$t('ruleTemplatelist.templateCNFullName')" :width="160"
                                   ellipsis :formatter="fillText" />
                    <FTable-column prop="description" :label="$t('ruleTemplatelist.templateFullDesc')" :width="134"
                                   ellipsis :formatter="fillText" />
                    <FTable-column :label="$t('ruleTemplatelist.acquisitionLogic')" :width="88" :formatter="fillText">
                        <template v-slot="{ row }">
                            <span style="color: #5384ff;cursor: pointer;" @click="handleDrawer(row)">{{$t('ruleTemplatelist.viewDetails')}}</span>
                        </template>
                    </FTable-column>
                    <FTable-column prop="dev_department_name" :label="$t('ruleTemplatelist.developDepartment')"
                                   ellipsis :width="210" :formatter="fillText" />
                    <FTable-column prop="ops_department_name" :label="$t('ruleTemplatelist.maintainDepartment')"
                                   ellipsis :width="210" :formatter="fillText" />
                    <!-- <FTable-column prop="visibility_department_text" :label="$t('ruleTemplatelist.visibleRange')"
                                   ellipsis :width="210" :formatter="fillText" /> -->
                    <FTable-column #default="{ row } = {}" :width="230" prop="visibility_department_text" :label="$t('ruleTemplatelist.visibleRange')">
                        <template v-if="!row.visibility_department_list">- -</template>
                        <template v-else>
                            <FTag type="info" style="margin-right: 4px">
                                <FEllipsis style="max-width: 126px">
                                    {{row.visibility_department_list[0].name}}
                                </FEllipsis>
                            </FTag>
                            <template v-if="row.visibility_department_list.length > 1">
                                <FTooltip mode="popover">
                                    <FTag type="info" style="min-wdith: 0px">+{{row.visibility_department_list.length - 1}}</FTag>
                                    <template #content>
                                        <div style="max-width: 40vw;">
                                            <FTag v-for="item in row.visibility_department_list.slice(1, row.visibility_department_list.length)" :key="item" type="info" style="min-wdith: 0px; margin: 2px">
                                                {{item.name}}
                                            </FTag>
                                        </div>
                                    </template>
                                </FTooltip>
                            </template>
                        </template>
                    </FTable-column>
                    <FTable-column prop="creator" :label="$t('ruleTemplatelist.creator')" :width="130"
                                   :formatter="fillText" />
                    <FTable-column prop="create_time" :label="$t('ruleTemplatelist.createTime')" :width="186"
                                   ellipsis :formatter="fillText" />
                    <FTable-column prop="modify_name" :label="$t('ruleTemplatelist.updator')" :width="130"
                                   :formatter="fillText" />
                    <FTable-column prop="modify_time" :label="$t('ruleTemplatelist.updateTime')" :width="186"
                                   ellipsis :formatter="fillText" />
                    <FTable-column #default="{ row } = {}" prop="actions" class="actions" :label="$t('_.操作')" align="center" :width="104" fixed="right">
                        <div class="actions">
                            <a class="a-link" @click="check(row)">{{$t('_.查看')}}</a>
                            <a class="a-link" @click="edit(row)">{{$t('_.编辑')}}</a>
                        </div>
                    </FTable-column>
                </FTable>
            </template>
            <template v-slot:pagination>
                <FPagination v-show="tableList.length > 0" show-total :pageSize="pagination.size"
                             :currentPage="pagination.page" show-size-changer :pageSizeOption="[10, 20, 50, 100]"
                             :total-count="pagination.totalCount" @change="handleCurrentChange">
                </FPagination>
            </template>
        </BTablePage>

        <!-- 高级筛选 -->
        <AdvanceModal ref="advanceRef" v-model:show="showAdvance" @success="handleAdvanceSearch" />

        <!-- 查看详情 -->
        <ViewDetailDrawer v-model:show="showDrawer" :content="currentContent" :name="currentName" />

        <TemplateDetail
            v-if="detailShow"
            v-model:show="detailShow"
            :tempId="curTempId"
            :type="type"
            @submit="handleSubmit"
        />
    </div>
</template>
<script setup>
import { cloneDeep } from 'lodash-es';
import dayjs from 'dayjs';
import {
    onMounted, ref, computed, nextTick, provide,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined } from '@fesjs/fes-design/icon';
import { BPageLoading, BTablePage, BSearch } from '@fesjs/traction-widget';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import { usePagination } from './hooks/usePagination';
import { fetchTemplates } from './api';
import AdvanceModal from './components/advanceModal.vue';
import ViewDetailDrawer from './components/viewDetailDrawer.vue';
import TemplateDetail from './components/templateDetail.vue';

function filteSerachParams(obj) {
    if (obj.toString().toLowerCase() !== '[object object]') {
        return {};
    }
    const result = {};
    Object.keys(obj).forEach((key) => {
        if (Array.isArray(obj[key])) {
            if (obj[key].length > 0) {
                result[key] = obj[key];
            }
        } else if (obj[key]) {
            result[key] = obj[key];
        }
    });
    return result;
}

function getFormatTime(timestamp) {
    return timestamp ? dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss') : '';
}

const { t: $t } = useI18n();

const init = () => ({
    cn_name: '',
    en_name: '',
    dev_department_id: '',
    ops_department_id: '',
    create_name: '',
});
// 算子详情
const curTempId = ref(null);
const type = ref('display');
const detailShow = ref(false);
const check = (row) => {
    if (row.id) {
        curTempId.value = row.id;
        type.value = 'display';
        detailShow.value = true;
    }
};
const edit = (row) => {
    if (row.id) {
        curTempId.value = row.id;
        type.value = 'edit';
        detailShow.value = true;
    }
};
const create = () => {
    type.value = 'create';
    detailShow.value = true;
};

const searchForm = ref(init());
const advanceForm = ref({});
const showAdvance = ref(false);
const searchType = ref('common');
const params = computed(() => {
    if (searchType.value === 'common') {
        return filteSerachParams({ ...searchForm.value });
    }
    const { createTimeRange, updateTimeRange, ...advanceTemp } = cloneDeep(advanceForm.value);
    advanceTemp.create_start_time = getFormatTime(createTimeRange[0]);
    advanceTemp.create_end_time = getFormatTime(createTimeRange[1]);
    advanceTemp.modify_start_time = getFormatTime(updateTimeRange[0]);
    advanceTemp.modify_end_time = getFormatTime(updateTimeRange[1]);
    return filteSerachParams({ ...advanceTemp });
});

const tableList = ref([]);
async function loadTable(param) {
    const res = await fetchTemplates(param);
    tableList.value = (res.data || []).map(item => ({
        ...item,
        visibility_department_text: (item.visibility_department_list || []).map(department => department.name).join(','),
    }));
    return {
        totalPage: Math.ceil(res.total / param.size),
        totalCount: res.total,
        page: param.page + 1,
    };
}

const {
    isLoading,
    pagination,
    handleInit,
    handleCurrentChange,
    fillText,
    fillTimeText,
} = usePagination(params, loadTable);

const advanceRef = ref(null);
function handleSearch() {
    searchType.value = 'common';
    advanceForm.value = {};
    advanceRef.value?.reset();
    handleInit();
}

// 高级筛选查询
function handleAdvanceSearch(data) {
    searchType.value = 'advance';
    searchForm.value = init();
    advanceForm.value = { ...data };
    handleInit();
}

async function handleReset() {
    await nextTick();
    searchForm.value = init();
    handleSearch();
}

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
const formatProjectUserList = ref([]);
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();

onMounted(async () => {
    await getProjectUserList();
    formatProjectUserList.value = projectUserList.value.map(item => ({
        label: item,
        value: item,
    }));
    handleSearch();
});

provide(
    'formatProjectUserList',
    computed(() => formatProjectUserList.value),
);
const handleSubmit = async () => {
    await handleReset();
};
// 查看详情
const showDrawer = ref(false);
const currentContent = ref('');
const currentName = ref('');
function handleDrawer(row) {
    showDrawer.value = true;
    currentContent.value = row.sql_action;
    currentName.value = row.template_id;
}

</script>
<style lang="less" scoped>
.metricTemplate {
    :deep(.table-container) {
        margin-top: 0px;
    }
}
.actions {
    display: flex;
    justify-content: space-around;
}
:deep(.fes-tag.fes-tag-type--info.fes-tag-size--middle.fes-tag-effect--light.is-bordered) {
    min-width: 0px;
}
</style>
