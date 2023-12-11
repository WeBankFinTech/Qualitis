<template>
    <!-- 高级筛选 -->
    <FModal :maskClosable="false" :title="$t('common.advanceSearch')" :show="showAdvanceQuery" @cancel="advanceCancel" @ok="ok">
        <FForm v-model="advanceQuery" :labelWidth="80" labelPosition="right">
            <FFormItem :label="$t('dataSourceManagement.dataSourceType')">
                <FSelect v-model="advanceQuery.data_source_type_id" clearable filterable :placeholder="$t('common.pleaseInput')" :options="dataSourceTypeList" labelField="name" valueField="id">
                </FSelect>
            </FFormItem>
            <FFormItem :label="$t('dataSourceManagement.dataSourceName')">
                <FSelect v-model="advanceQuery.name" filterable clearable :options="dataSourceNameList" :placeholder="$t('common.pleaseInput')">
                </FSelect>
            </FFormItem>
            <!-- 关联子系统 -->
            <FFormItem :label="$t('dataSourceManagement.associatedSubSystem')">
                <FInput
                    v-model="advanceQuery.sub_system_name"
                    filterable
                    clearable
                    :placeholder="$t('common.pleaseInput')"
                />
            </FFormItem>
            <!-- 开发科室 -->
            <FFormItem :label="$t('dataSourceManagement.developDepartment')" prop="dev_department_name">
                <FSelectCascader
                    v-model="advanceQuery.dev_department_name"
                    :data="devDivisions"
                    :loadData="loadDevDivisions"
                    clearable
                    remote
                    emitPath
                    showPath
                    checkStrictly="child"
                    :placeholder="$t('common.pleaseSelect')"
                    @change="handleDevId"
                ></FSelectCascader>
            </FFormItem>
            <!-- 运维科室 -->
            <FFormItem :label="$t('dataSourceManagement.maintainDepartment')" prop="ops_department_name">
                <FSelectCascader
                    v-model="advanceQuery.ops_department_name"
                    :data="opsDivisions"
                    :loadData="loadOpsDivisions"
                    clearable
                    remote
                    emitPath
                    showPath
                    checkStrictly="child"
                    :placeholder="$t('common.pleaseSelect')"
                    @change="handleOpsId"
                ></FSelectCascader>
            </FFormItem>
            <!-- 可见范围 -->
            <FFormItem :label="$t('dataSourceManagement.visibleRange')" prop="visible_department_ids">
                <FSelectCascader
                    v-model="advanceQuery.visible_department_ids"
                    :data="visDivisions"
                    :loadData="loadVisDivisions"
                    :placeholder="$t('common.pleaseSelect')"
                    clearable
                    remote
                    emitPath
                    showPath
                    multiple
                    checkStrictly="all"
                    collapseTags
                    :collapseTagsLimit="3"
                    @change="visSelectChange" />
            </FFormItem>
            <FFormItem :label="$t('dataSourceManagement.createUser')" prop="create_user">
                <FSelect v-model="advanceQuery.create_user" :placeholder="$t('common.pleaseSelect')" :options="formatProjectUserList" clearable filterable>
                </FSelect>
            </FFormItem>
            <FFormItem :label="$t('dataSourceManagement.modifyUser')" prop="modify_user">
                <FSelect v-model="advanceQuery.modify_user" :placeholder="$t('common.pleaseSelect')" :options="formatProjectUserList" clearable filterable>
                </FSelect>
            </FFormItem>
        </FForm>
    </FModal>
</template>
<script setup>
import {
    defineProps, defineEmits, computed, inject, defineExpose, onMounted, ref,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import useDivisions from '@/hooks/useDivisions';
import useDepartment from '@/hooks/useDepartment';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';

const { t: $t } = useI18n();
// 获取用户列表
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();
const {
    divisions: devDivisions,
    loadDivisions: loadDevDivisions,
    curSubDepartData: devCurSubDepartData,
} = useDivisions();
const {
    divisions: opsDivisions,
    loadDivisions: loadOpsDivisions,
    curSubDepartData: opsCurSubDepartData,
} = useDivisions();
const {
    divisions: visDivisions,
    loadDivisions: loadVisDivisions,
    curSubDepartData: visCurSubDepartData,
    initDepartment,
} = useDivisions(true);
const {
    selectDevDate,
    selectOpsDate,
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);
const emits = defineEmits(['update:advanceQuery', 'advanceSearch', 'update:showAdvanceQuery']);
const props = defineProps({
    showAdvanceQuery: {
        default: false,
        required: true,
    },
    advanceQuery: {
        default: {},
        required: true,
    },
    // 子系统下拉框列表
    subSystemList: {
        type: Array,
        default: [],
    },
});
const advanceQuery = computed({
    get() {
        return props.advanceQuery;
    },
    set(v) {
        emits('update:advanceQuery', v);
    },
});
const showAdvanceQuery = computed({
    get() {
        return props.showAdvanceQuery;
    },
    set(v) {
        emits('update:showAdvanceQuery', v);
    },
});
const ok = () => {
    emits('advanceSearch');
    showAdvanceQuery.value = false;
};
const advanceCancel = () => {
    showAdvanceQuery.value = false;
};
const dataSourceNameList = inject('dataSourceNameList');
const dataSourceTypeList = inject('dataSourceTypeList');
const formatProjectUserList = ref([]);
onMounted(async () => {
    await getProjectUserList();
    formatProjectUserList.value = projectUserList.value.map(item => ({
        label: item,
        value: item,
    }));
});
defineExpose({ selectVisDate, selectDevDate, selectOpsDate });
</script>
<style lang="less" scoped>

</style>
