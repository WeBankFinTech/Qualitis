<template>
    <div class="page-header-condition">
        <div class="condition-item">
            <span class="condition-label">{{$t('ruleTemplatelist.dataSourceType')}}</span>
            <FSelect v-model="formModel.dataSourceType" filterable clearable :width="160">
                <FOption v-for="item in dataSourceTypes" :key="item.label" :value="item.value">{{item.label}}</FOption>
            </FSelect>
        </div>
        <div class="condition-item">
            <span class="condition-label">{{$t('common.cluster')}}</span>
            <FSelect v-model="formModel.cluster" filterable clearable :width="160">
                <FOption v-for="item in clusters" :key="item" :value="item">{{item}}</FOption>
            </FSelect>
        </div>
        <div class="condition-item">
            <span class="condition-label">{{$t('common.database')}}</span>
            <FSelect v-model="formModel.db" filterable clearable :width="160">
                <FOption v-for="item in databases" :key="item" :value="item">{{item}}</FOption>
            </FSelect>
        </div>
        <div class="condition-item">
            <span class="condition-label">{{$t('common.table')}}</span>
            <!-- <FSelect v-model="formModel.table" filterable clearable>
                <FOption v-for="item in tables" :key="item" :value="item">{{item}}</FOption>
            </FSelect> -->
            <FSelect v-model="formModel.table" filterable clearable :options="tables" :width="160" />
        </div>
        <div class="condition-item">
            <FSpace :size="CONDITIONBUTTONSPACE">
                <FButton type="primary" @click="handleSearch">{{$t('taskQuery.search')}}</FButton>
                <FButton
                    :class="querySelectedCount > 0 ? 'selected-count' : ''"
                    @click="handleShowAdvanceSearch"
                >
                    {{$t('common.advanceSearch')}}{{querySelectedCount > 0 ? `（已选${querySelectedCount}项）` : ''}}
                </FButton>
                <FButton @click="handleReset">{{$t('common.reset')}}</FButton>
            </FSpace>
        </div>
        <!-- 高级筛选弹框 -->
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
                :model="advanceQueryData"
            >
                <FFormItem :label="$t('ruleTemplatelist.dataSourceType')" prop="dataSourceType">
                    <FSelect v-model="advanceQueryData.datasourceType" filterable clearable :width="160">
                        <FOption v-for="item in dataSourceTypes" :key="item.label" :value="item.value">{{item.label}}</FOption>
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.cluster')" prop="cluster">
                    <FSelect v-model="advanceQueryData.cluster" filterable clearable :width="160">
                        <FOption v-for="item in clusters" :key="item" :value="item">{{item}}</FOption>
                    </FSelect>
                </FFormItem>
                <FFormItem label="DCN" props="dcn">
                    <FSelect v-model="advanceQueryData.envName" filterable clearable :options="dcns" :width="160" />
                </FFormItem>
                <FFormItem :label="$t('common.database')" props="database">
                    <FSelect v-model="advanceQueryData.db" filterable clearable :width="160">
                        <FOption v-for="item in databases" :key="item" :value="item">{{item}}</FOption>
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.table')" props="table">
                    <FSelect v-model="advanceQueryData.table" filterable clearable :options="tables" :width="160" />
                </FFormItem>
                <FFormItem :label="$t('common.subSystem')" prop="subSystem">
                    <!-- <FSelect v-model="advanceQueryData.sub_system_id" filterable clearable remote :width="160">
                        <FOption v-for="item in subSystems" :key="item.value" :value="item.value">{{item.label}}</FOption>
                    </FSelect> -->
                    <FSelect v-model="advanceQueryData.sub_system_id" filterable clearable remote :options="subSystems" :width="160" />
                </FFormItem>
                <FFormItem :label="$t('common.developDepartment')" prop="developDepartment">
                    <FSelectCascader
                        v-model="advanceQueryData.dev_department_name"
                        :data="devDivisions"
                        :loadData="loadDevDivisions"
                        clearable
                        remote
                        emitPath
                        showPath
                        checkStrictly="all"
                    ></FSelectCascader>
                </FFormItem>
                <FFormItem :label="$t('common.bussinessDepartment')" prop="bussinessDepartment">
                    <FSelectCascader
                        v-model="advanceQueryData.department_name"
                        :data="busDivisions"
                        :loadData="loadBusDivisions"
                        clearable
                        remote
                        emitPath
                        showPath
                        checkStrictly="all"
                    ></FSelectCascader>
                </FFormItem>
                <FFormItem :label="$t('common.dataLabel')" props="dataLabel">
                    <FSelect v-model="advanceQueryData.tag_code" filterable clearable :width="160" :options="dataLabels">
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('ruleQuery.switchRuleTips')" props="filterFlag">
                    <FSwitch v-model="advanceQueryData.filterFlag" />
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import {
    ref, defineProps, defineEmits, defineExpose, watch,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';

const { t: $t } = useI18n();
const props = defineProps({
    formModel: {
        type: Object,
        required: true,
    },
    clusters: {
        type: Array,
        required: true,
    },
    dataSourceTypes: {
        type: Array,
        required: true,
    },
    databases: {
        type: Array,
        required: true,
    },
    tables: {
        type: Array,
        required: true,
    },
    dcns: {
        type: Array,
        required: true,
    },
    advanceQueryData: {
        type: Object,
        required: true,
    },
    subSystems: {
        type: Array,
        required: true,
    },
    devDivisions: {
        type: Array,
        required: true,
    },
    loadDevDivisions: {
        type: Function,
        required: true,
    },
    busDivisions: {
        type: Array,
        required: true,
    },
    loadBusDivisions: {
        type: Function,
        required: true,
    },
    dataLabels: {
        type: Array,
        required: true,
    },
});

const emit = defineEmits([
    // 点击搜索
    'on-search',
    // 高级筛选
    'on-advance-search',
    // 重置
    'on-reset',
    // 元数据更新
    'on-metadataUpdate',
    // 点开高级筛选
    'on-show-advance-search',
    // 取消高级筛选
    'on-cancel-advance-search',
]);

const handleSearch = () => {
    emit('on-search');
};
const handleCancelAdvanceSearch = () => {
    emit('on-cancel-advance-search');
};
const handleAdvanceSearch = () => {
    emit('on-advance-search');
};
const showAdvanceSearchModal = ref(false);
const handleShowAdvanceSearch = () => {
    showAdvanceSearchModal.value = true;
    emit('on-show-advance-search');
};

const handleReset = () => {
    emit('on-reset');
};
const handlemetadataUpdate = () => {
    emit('on-metadataUpdate');
};

// 筛选条件数量的watch
const querySelectedCount = ref(0);
watch(props.advanceQueryData, () => {
    querySelectedCount.value = Object.values(props.advanceQueryData).filter(v => !!v).length;
}, { immediate: true });

const closeAdvanceModal = () => {
    showAdvanceSearchModal.value = false;
};
defineExpose({ closeAdvanceModal });
</script>
<style lang="less" scoped>
@import "@/style/varible";

.filter-switch {
    .filter-switch-text {
        margin-right: 16px;
    }
    line-height: 22px;
    text-align: right;
    font-size: 14px;
    font-weight: 400;
    color: @label-color;
}
    .selected-count {
        background: rgba(83,132,255,0.06);
        border: 1px solid #5384FF;
        color: #5384FF;
    }
</style>
