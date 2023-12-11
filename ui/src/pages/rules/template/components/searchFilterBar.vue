<template>
    <div class="query-container">
        <div class="query-item">
            <span class="query-item-label">{{$t('ruleTemplatelist.templateCNName')}}</span>
            <FInput v-model="queryData.cn_name" class="query-input" clearable :placeholder="$t('common.pleaseInput')" @change="handleCommonChange" />
        </div>
        <div class="query-item">
            <span class="query-item-label">{{$t('ruleTemplatelist.templateENName')}}</span>
            <FInput v-model="queryData.en_name" class="query-input" clearable :placeholder="$t('common.pleaseInput')" @change="handleCommonChange" />
        </div>
        <FButton
            type="primary"
            class="operation-item white"
            @click="clickBaseSearch"
        >
            {{$t('common.query')}}
        </FButton>
        <FButton
            :class="advanceQuerySelectedCount > 0 ? 'operation-item selected-count' : 'operation-item'"
            @click="clickAdvanceQuery"
        >
            {{$t('common.advanceSearch')}}{{advanceQuerySelectedCount > 0 ? `（${$t('common.hasSelect', { count: advanceQuerySelectedCount })}）` : ''}}
        </FButton>
        <FButton class="operation-item" @click="handleReset">{{$t('common.reset')}}</FButton>

        <!-- 高级筛选弹框 -->
        <FModal
            :show="showAdvanceModal"
            :width="480"
            :title="$t('common.advanceSearch')"
            :okText="$t('common.query')"
            :cancelText="$t('common.cancel')"
            :maskClosable="false"
            @ok="clickAdvanceSearch"
            @cancel="cancelAdvanceSearch"
        >
            <FForm
                :label-width="92"
                :model="advanceQueryData"
                labelPosition="right"
                class="task-form"
            >
                <FFormItem :label="$t('ruleTemplatelist.templateCNName')">
                    <FInput
                        v-model="advanceQueryData.cn_name"
                        class="query-input"
                        clearable
                        :placeholder="$t('common.pleaseInput')"
                    />
                </FFormItem>
                <FFormItem :label="$t('ruleTemplatelist.templateENName')">
                    <FInput
                        v-model="advanceQueryData.en_name"
                        class="query-input"
                        clearable
                        :placeholder="$t('common.pleaseInput')"
                    />
                </FFormItem>
                <!-- 数据源类型 -->
                <FFormItem :label="$t('ruleTemplatelist.dataSourceType')">
                    <FSelect v-model="advanceQueryData.data_source_type"
                             :placeholder="$t('common.pleaseSelect')"
                             class="query-input"
                             :options="datasourceTypeList"
                             clearable
                             filterable
                             valueField="label"
                             labelField="label"
                    ></FSelect>
                </FFormItem>
                <!-- 校验类型 -->
                <FFormItem :label="$t('ruleTemplatelist.verificationType')">
                    <FSelect v-model="advanceQueryData.verification_type"
                             :placeholder="$t('common.pleaseSelect')"
                             class="query-input"
                             :options="verificationTypeList"
                             clearable
                             filterable
                    ></FSelect>
                </FFormItem>
                <!-- 校验级别 -->
                <FFormItem :label="$t('ruleTemplatelist.verificationLevel')">
                    <FSelect v-model="advanceQueryData.verification_level"
                             :placeholder="$t('common.pleaseSelect')"
                             class="query-input"
                             :options="verificationLevelList"
                             clearable
                             filterable
                    ></FSelect>
                </FFormItem>
                <!-- 开发科室 -->
                <FFormItem :label="$t('ruleTemplatelist.developDepartment')">
                    <FSelectCascader
                        v-model="advanceQueryData.dev_department_id"
                        :data="devDivisions"
                        :loadData="loadDevDivisions"
                        valueField="code"
                        clearable
                        remote
                        showPath
                        checkStrictly="child"
                        :placeholder="$t('common.pleaseSelect')"
                    ></FSelectCascader>
                </FFormItem>
                <!-- 运维科室 -->
                <FFormItem :label="$t('ruleTemplatelist.maintainDepartment')">
                    <FSelectCascader
                        v-model="advanceQueryData.ops_department_id"
                        :data="opsDivisions"
                        :loadData="loadOpsDivisions"
                        valueField="code"
                        clearable
                        remote
                        showPath
                        checkStrictly="child"
                        :placeholder="$t('common.pleaseSelect')"
                    ></FSelectCascader>
                </FFormItem>
                <FFormItem :label="$t('ruleTemplatelist.visibleRange')" prop="action_range">
                    <FSelectCascader
                        v-model="advanceQueryData.action_range"
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
                        :placeholder="$t('common.pleaseSelect')"
                        @change="visSelectChange" />
                </FFormItem>
                <FFormItem :label="$t('ruleTemplatelist.creator')" prop="create_name">
                    <FSelect v-model="advanceQueryData.create_name" :placeholder="$t('common.pleaseSelect')" :options="formatProjectUserList" clearable filterable>
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('ruleTemplatelist.modifyName')" prop="modify_name">
                    <FSelect v-model="advanceQueryData.modify_name" :placeholder="$t('common.pleaseSelect')" :options="formatProjectUserList" clearable filterable>
                    </FSelect>
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import { cloneDeep } from 'lodash-es';
import {
    computed, ref, inject, onMounted,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import useDivisions from '@/hooks/useDivisions';
import useDepartment from '@/hooks/useDepartment';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import { fetchUserList } from '../api';

// 获取用户列表
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();
const { t: $t } = useI18n();

// 从 ../index.js inject数据
const verificationTypeList = inject('verificationTypeList', []);
const verificationLevelList = inject('verificationLevelList', []);
const datasourceTypeList = inject('datasourceTypeList', []);

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
const {
    selectDevDate,
    selectOpsDate,
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);
// eslint-disable-next-line no-undef
const emit = defineEmits(['search']);


const showAdvanceModal = ref(false);
const queryData = ref({});
const advanceQueryData = ref({
    action_range: [],
});
const handleCommonChange = () => {
    advanceQueryData.value = {
        action_range: [],
    };
};
const advanceQuerySelectedCount = computed(() => {
    const { ...args } = advanceQueryData.value;
    // console.log(args);
    return Object.values(args).filter((item) => {
        if (Array.isArray(item)) {
            return item.length > 0;
        }
        if (item === 0) return true;
        return item;
    }).length;
});
const search = (type) => {
    const advanceTemp = cloneDeep(advanceQueryData.value);
    if (Array.isArray(selectVisDate.value) && selectVisDate.value.length > 0) {
        advanceTemp.action_range = selectVisDate.value.map(item => item.id);
    } else {
        delete advanceTemp.action_range;
    }

    emit('search', Object.assign({ searchType: type }, queryData.value, advanceTemp));
};
const clickBaseSearch = () => {
    advanceQueryData.value = {
        action_range: [],
    };
    search();
};
const reset = () => {
    queryData.value = {};
    advanceQueryData.value = {
        action_range: [],
    };
    selectVisDate.value = [];
};
const handleReset = () => {
    reset();
    search('reset');
};
const clickAdvanceQuery = () => {
    showAdvanceModal.value = true;
    queryData.value = {};
};
const clickAdvanceSearch = () => {
    showAdvanceModal.value = false;
    search();
};
const cancelAdvanceSearch = () => {
    showAdvanceModal.value = false;
};
const formatProjectUserList = ref([]);
onMounted(async () => {
    await getProjectUserList();
    formatProjectUserList.value = projectUserList.value.map(item => ({
        label: item,
        value: item,
    }));
});
// eslint-disable-next-line no-undef
defineExpose({ reset });

</script>
<style lang='less' scoped>
.query-container {
    display: flex;
    flex-wrap: wrap;
    .query-item,
    .operation-item {
        margin-right: 22px;
        margin-bottom: 22px;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #0f1222;
        letter-spacing: 0;
        line-height: 22px;
        font-weight: 400;
        &.white {
            color: #ffffff;
        }
        &-label {
            margin-right: 16px;
            font-family: PingFangSC-Regular;
            font-size: 14px;
            color: #0f1222;
        }
    }
    .selected-count {
        background: rgba(83, 132, 255, 0.06);
        border: 1px solid #5384ff;
        color: #5384ff;
    }
    .query-input {
        width: 160px;
    }
}
</style>
