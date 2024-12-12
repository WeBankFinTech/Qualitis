<template>
    <!-- 版本列表 -->
    <FDrawer
        v-model:show="show"
        title="版本记录"
        displayDirective="if"
        :width="940"
        @cancel="closeVersionList"
    >
        <f-table :data="versionList" :no-data-text="$t('common.noData')">
            <template #empty>
                <BPageLoading actionType="emptyInitResult" />
            </template>
            <f-table-column prop="version" :label="`版本号`" ellipsis></f-table-column>
            <f-table-column prop="create_time" :label="`创建时间`" ellipsis></f-table-column>
            <f-table-column prop="create_user" :label="`创建用户`" ellipsis></f-table-column>
            <f-table-column :label="$t('common.operate')" :width="104" ellipsis>
                <template #default="{ row }">
                    <ul class="wd-table-operate-btns">
                        <li
                            v-for="(action, index) in ruleTemplateTableActions"
                            :key="index"
                            class="btn-item"
                            @click="action.func(row.edition_id, $event)">
                            {{action.label}}
                        </li>
                    </ul>
                </template>
            </f-table-column>
        </f-table>
        <div class="table-pagination-container">
            <FPagination
                v-model:currentPage="pagination.page"
                v-model:pageSize="pagination.size"
                show-size-changer
                show-total
                :total-count="pagination.total"
                @change="loadVersionList"
                @pageSizeChange="loadVersionList"
            ></FPagination>
        </div>
        <!-- 新增以及编辑操作 -->
        <EditTemplate
            v-model:show="showEditPanel"
            :tid="selectedDataId"
            :mode="editMode"
            :type="2"
            @loadData="loadVersionList"
        ></EditTemplate>
    </FDrawer>
</template>
<script setup>
import {
    ref, onUpdated,
} from 'vue';
import {
    useI18n, request as FRequest,
} from '@fesjs/fes';
import useEditTemplate from '@/pages/rules/template/hooks/useEditTemplate';
import EditTemplate from './editTemplate';

// eslint-disable-next-line no-undef
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
        default: false,
    },
    sid: {
        type: Number,
        default: -1,
    },
});

const { t: $t } = useI18n();

// eslint-disable-next-line no-undef
const emit = defineEmits(['update:show']);

const versionList = ref([]);

const {
    editMode,
    showEditPanel,
    openEditPanel,
    selectedDataId,
    ruleTemplateTableActions,
} = useEditTemplate();

const pagination = ref({
    page: 1,
    size: 10,
    total: 0,
});

const loadVersionList = async () => {
    try {
        versionList.value = [];
        const { data = [], total = 0 } = await FRequest('/api/v1/projector/standardValue/edition/all', {
            page: pagination.value.page - 1,
            size: pagination.value.size,
            edition_id: props.sid,
        });
        pagination.value.total = total;
        versionList.value = data;
    } catch (err) {
        console.warn(err);
    }
};

const closeVersionList = () => {
    emit('update:show', false);
};

// const
onUpdated(async () => {
    if (!props.show) {
        return;
    }
    // 逻辑只在面板打开的时候执行
    loadVersionList();
});

</script>
