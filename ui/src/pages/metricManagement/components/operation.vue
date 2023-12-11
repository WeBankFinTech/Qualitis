<template>
    <div class="page-header-condition">
        <div class="condition-item">
            <span class="condition-label">{{$t('indexManagement.indexName')}}</span>
            <FInput v-model="formModel.rule_metric_name" :placeholder="$t('indexManagement.indexName')" clearable />
        </div>
        <div class="condition-item">
            <span class="condition-label">{{$t('indexManagement.indexCategory')}}</span>
            <FSelect v-model="formModel.type" filterable clearable valueField="value" labelField="label" :options="metricCategories">
            </FSelect>
        </div>
        <div class="condition-item">
            <span class="condition-label">{{$t('indexManagement.subsystem')}}</span>
            <FSelect v-model="formModel.sub_system_name" filterable clearable>
                <FOption v-for="item in subSystemNames" :key="item" :value="item" :label="item"></FOption>
            </FSelect>
        </div>
        <div class="condition-item">
            <span class="condition-label">{{$t('indexManagement.en_code')}}</span>
            <FSelect v-model="formModel.en_code" filterable clearable>
                <FOption v-for="item in enCodes" :key="item" :value="item" :label="item"></FOption>
            </FSelect>
        </div>
        <div class="condition-item">
            <FSpace :size="CONDITIONBUTTONSPACE">
                <FButton type="primary" class="search-btn-item" @click="handleSearch">{{$t('taskQuery.search')}}</FButton>
                <FButton
                    :class="querySelectedCount > 0 ? 'selected-count' : ''"
                    @click="handleShowAdvanceSearch"
                >
                    {{$t('common.advanceSearch')}}{{querySelectedCount > 0 ? `（已选${querySelectedCount}项）` : ''}}
                </FButton>
                <FButton class="search-btn-item" @click="handleReset">{{$t('common.reset')}}</FButton>
            </FSpace>
        </div>
        <FModal
            :show="showAdvanceSearchModal"
            :width="572"
            :title="$t('common.advanceSearch')"
            :okText="$t('taskQuery.search')"
            :cancelText="$t('common.cancel')"
            :maskClosable="false"
            @ok="handleAdvanceSearch"
            @cancel="handleCancelAdvanceSearch"
        >
            <FForm
                ref="advanceQueryForm"
                :label-width="70"
                labelPosition="right"
                class="task-form"
                :model="advanceQueryModel"
            >
                <FFormItem :label="$t('indexManagement.indexName')" prop="indexName">
                    <FInput v-model="advanceQueryModel.rule_metric_name" :placeholder="$t('indexManagement.indexName')" clearable :width="160" />
                </FFormItem>
                <FFormItem :label="$t('indexManagement.indexCategory')" prop="indexCategory">
                    <FSelect v-model="advanceQueryModel.type" filterable clearable :options="metricCategories" :width="160"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('indexManagement.inUse')" prop="inUse">
                    <FSelect v-model="advanceQueryModel.available" filterable clearable :options="booleans" :width="160"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('indexManagement.subsystem')" prop="subsystem">
                    <FSelect v-model="advanceQueryModel.sub_system_name" filterable clearable :width="160">
                        <FOption v-for="item in subSystemNames" :key="item" :value="item" :label="item"></FOption>
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('indexManagement.en_code')" prop="en_code">
                    <FSelect v-model="advanceQueryModel.en_code" filterable clearable :width="160">
                        <FOption v-for="item in enCodes" :key="item" :value="item" :label="item"></FOption>
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('indexManagement.inMultiDCN')" prop="multi_envs">
                    <FSelect v-model="advanceQueryModel.multi_envs" filterable clearabl :options="booleans" :width="160"></FSelect>
                </FFormItem>
                <!-- 开发科室 -->
                <FFormItem :label="$t('indexManagement.developDepartment')" prop="dev_department_id">
                    <FSelectCascader
                        v-model="advanceQueryModel.dev_department_id"
                        :data="devDivisions"
                        :loadData="loadDevDivisions"
                        valueField="code"
                        clearable
                        remote
                        showPath
                        checkStrictly="child"
                    ></FSelectCascader>
                </FFormItem>
                <!-- 运维科室 -->
                <FFormItem :label="$t('indexManagement.maintainDepartment')" prop="ops_department_id">
                    <FSelectCascader
                        v-model="advanceQueryModel.ops_department_id"
                        :data="opsDivisions"
                        :loadData="loadOpsDivisions"
                        valueField="code"
                        clearable
                        remote
                        showPath
                        checkStrictly="child"
                    ></FSelectCascader>
                </FFormItem>
                <!-- 可见范围 -->
                <FFormItem :label="$t('indexManagement.visibleRange')" prop="action_range">
                    <FSelectCascader
                        v-model="advanceQueryModel.action_range"
                        :data="visDivisions"
                        :loadData="loadVisDivisions"
                        clearable
                        remote
                        showPath
                        multiple
                        checkStrictly="all"
                        collapseTags
                        valueField="code"
                        :collapseTagsLimit="3" />
                </FFormItem>
                <FFormItem :label="$t('indexManagement.creator')" prop="create_user">
                    <FSelect v-model="advanceQueryModel.create_user" filterable clearable :options="projectUserList" :width="160"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('indexManagement.reviser')" prop="modify_user">
                    <FSelect v-model="advanceQueryModel.modify_user" filterable clearable :options="projectUserList" :width="160"></FSelect>
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import {
    defineProps, defineEmits, ref, defineExpose, watch, onMounted,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
// eslint-disable-next-line import/no-extraneous-dependencies
import { isBoolean } from 'lodash';
import useDivisions from '@/hooks/useDivisions';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';


const { t: $t } = useI18n();

const props = defineProps({
    formModel: {
        type: Object,
        required: true,
    },
    advanceQueryModel: {
        type: Object,
        required: true,
    },
    metricCategories: {
        type: Array,
        required: true,
    },
    booleans: {
        type: Array,
        required: true,
    },
    enCodes: {
        type: Array,
        required: true,
    },
    subSystemNames: {
        type: Array,
        required: true,
    },
});

// 筛选条件数量的watch
const querySelectedCount = ref(0);
watch(props.advanceQueryModel, () => {
    querySelectedCount.value = Object.values(props.advanceQueryModel).filter((item) => {
        if (isBoolean(item)) {
            return true;
        }
        if (Array.isArray(item)) {
            return item.length;
        }
        return !!item;
    }).length;
}, { immediate: true });

const emit = defineEmits([
    // 点击搜索
    'on-search',
    // 重置
    'on-reset',
    // 展示高级筛选弹窗
    'on-show-advance-search',
    // 取消隐藏高级筛选弹窗
    'on-cancel-advance-search',
    // 进行高级筛选
    'on-advance-search',
]);

// 顶部栏查询
const handleSearch = () => {
    emit('on-search');
};

const handleReset = () => {
    emit('on-reset');
};

// 高级筛选
const handleAdvanceSearch = () => {
    emit('on-advance-search');
};
const showAdvanceSearchModal = ref(false);
const handleShowAdvanceSearch = () => {
    showAdvanceSearchModal.value = true;
    emit('on-show-advance-search');
};
const handleCancelAdvanceSearch = () => {
    showAdvanceSearchModal.value = false;
    emit('on-cancel-advance-search');
};
const closeAdvanceModal = () => {
    showAdvanceSearchModal.value = false;
};
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
} = useDivisions();

// 获取用户列表
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();


onMounted(async () => {
    await getProjectUserList();
    projectUserList.value = projectUserList.value.map(item => ({ value: item, label: item }));
    console.log(projectUserList.value);
});

defineExpose({ closeAdvanceModal });
</script>
<style lang="less" scoped>
    .selected-count {
        background: rgba(83,132,255,0.06);
        border: 1px solid #5384FF;
        color: #5384FF;
    }
</style>
