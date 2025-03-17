<template>
    <div class="query-container">
        <div class="query-item">
            <span class="label">{{$t('_.函数中文名')}}</span>
            <FInput v-model="commonConditions.cn_name" class="input" :placeholder="$t('_.请输入')" @change="handleCommonChange"></FInput>
        </div>
        <div class="query-item">
            <span class="label">{{$t('_.函数英文名')}}</span>
            <FInput v-model="commonConditions.name" class="input" :placeholder="$t('_.请输入')" @change="handleCommonChange"></FInput>
        </div>
        <div class="query-item">
            <FSpace :size="16">
                <FButton type="primary" @click="common">{{$t('_.查询')}}</FButton>
                <FButton :class="advancedConditionsSelectedCount > 0 ? 'selected-count' : ''" @click="openAdvancedSearch">
                    {{$t('_.高级筛选')}}{{advancedConditionsSelectedCount > 0 ? `（已选${advancedConditionsSelectedCount}项）` : ''}}
                </FButton>
                <FButton @click="reset">{{$t('_.重置')}}</FButton>
            </FSpace>
        </div>
        <FModal v-model:show="showModal" :title="$t('_.高级筛选')" :oktext="$t('_.查询')" :width="480" displayDirective="if" @ok="advance">
            <FForm ref="advancedConditionsForm" :labelWidth="74" :model="advancedConditions" labelClass="form-label">
                <FFormItem :label="$t('_.函数中文名')" prop="cn_name">
                    <FInput v-model="advancedConditions.cn_name" class="input" :placeholder="$t('_.请输入')"></FInput>
                </FFormItem>
                <FFormItem :label="$t('_.函数英文名')" prop="name">
                    <FInput v-model="advancedConditions.name" class="input" :placeholder="$t('_.请输入')"></FInput>
                </FFormItem>
                <FFormItem :label="$t('_.可用集群')" prop="enable_cluster">
                    <FSelect v-model="advancedConditions.enable_cluster" multiple style="width: 342px" :options="clusterList" valueField="cluster_name" labelField="cluster_name" @change="onClusterChange"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.函数分类')" prop="dir">
                    <FSelect v-model="advancedConditions.dir"
                             class="form-edit-input"
                             remote
                             clearable
                             :options="tempDirList"
                             @search="searchDir"
                             @clear="clearDir"
                    ></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.实现方式')" prop="impl_type">
                    <FSelect v-model="advancedConditions.impl_type" style="width: 342px" :options="implTypeList"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.可用引擎')" prop="enable_engine">
                    <FSelect v-model="advancedConditions.enable_engine" multiple style="width: 342px" :options="engineList"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.开发科室')" prop="dev_department_name">
                    <FSelectCascader v-model="advancedConditions.dev_department_name" style="width: 342px" :data="devDivisions" :loadData="loadDevDivisions"
                                     clearable remote emitPath showPath checkStrictly="child" @change="handleDevId" />
                </FFormItem>
                <FFormItem :label="$t('_.运维科室')" prop="ops_department_name">
                    <FSelectCascader v-model="advancedConditions.ops_department_name" style="width: 342px" :data="opsDivisions" :loadData="loadOpsDivisions"
                                     clearable remote emitPath showPath checkStrictly="child" @change="handleOpsId" />
                </FFormItem>
                <FFormItem :label="$t('_.可见范围')" prop="visibility_department_list">
                    <FSelectCascader
                        v-model="advancedConditions.visibility_department_list"
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
                        @change="visSelectChange" />
                </FFormItem>
                <FFormItem :label="$t('_.创建人')" prop="create_user">
                    <FSelect v-model="advancedConditions.create_user" clearable filterable style="width: 342px">
                        <FOption v-for="v in projectUserList" :key="v" :value="v" :label="v"></FOption>
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.修改人')" prop="modify_user">
                    <FSelect v-model="advancedConditions.modify_user" clearable filterable style="width: 342px">
                        <FOption v-for="v in projectUserList" :key="v" :value="v" :label="v"></FOption>
                    </FSelect>
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import {
    computed, inject, ref, unref, defineEmits, onMounted, defineProps,
} from 'vue';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import useDivisions from '@/hooks/useDivisions';
import useDepartment from '@/hooks/useDepartment';
import { useI18n } from '@fesjs/fes';
import useBasicOptions from '../hooks/useBasicOptions';

const { t: $t } = useI18n();
// 获取用户列表
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();
const props = defineProps({
    advancedValue: {
        type: Object,
        default: {},
        required: true,
    },
    commonValue: {
        type: Object,
        default: {},
        required: true,
    },
});

const emit = defineEmits(['search', 'reset', 'update:advancedValue', 'update:commonValue']);
const commonConditions = computed({
    get() {
        return props.commonValue;
    },
    set(v) {
        emit('update:commonValue', v);
    },
});
const advancedConditions = computed({
    get() {
        return props.advancedValue;
    },
    set(v) {
        emit('update:advancedValue', v);
    },
});
const clusterList = inject('clusterList');
const {
    engineList,
    implTypeList,
    dirList,
    handleClusterChange,
} = useBasicOptions();
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
    // code
    selectDevDate,
    // code
    selectOpsDate,
    // code
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);
const showModal = ref(false);

const tempDirList = ref([]);
const searchDir = (val) => {
    if (val === '') {
        tempDirList.value = unref(dirList);
        return;
    }
    if (!tempDirList.value.find(item => item.value === val)) {
        // 不存在对应元素，追加自定义输入
        tempDirList.value = [{ value: val, label: val }].concat(dirList.value);
    } else {
        tempDirList.value = dirList.value.filter(item => item.value === val);
    }
};
const clearDir = (val) => {
    tempDirList.value = unref(dirList);
};
const advancedConditionsSelectedCount = computed(() => {
    const keys = Object.keys(advancedConditions.value);
    let count = 0;
    for (let i = 0; i < keys.length; i++) {
        if (!(Array.isArray(advancedConditions.value[keys[i]]) && advancedConditions.value[keys[i]].length === 0) && advancedConditions.value[keys[i]] !== '' && keys[i] !== 'dev_department_id' && keys[i] !== 'ops_department_id') {
            count++;
        }
    }
    return count;
});
const advance = () => {
    advancedConditions.value.dev_department_id = selectDevDate.value.toString();
    advancedConditions.value.ops_department_id = selectOpsDate.value.toString();
    emit('search', visDivisions, selectVisDate);
    showModal.value = false;
};
const common = async () => {
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
    emit('search');
};
const reset = () => {
    emit('reset');
};
const openAdvancedSearch = () => {
    showModal.value = true;
    commonConditions.value = {};
};

const handleCommonChange = () => {
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
    selectDevDate.value = '';
    selectOpsDate.value = '';
};
// eslint-disable-next-line camelcase
const onClusterChange = async (cluster_name) => {
    advancedConditions.value.dir = '';
    await handleClusterChange(cluster_name[0]);
    // eslint-disable-next-line no-use-before-define
    searchDir('');
};
onMounted(async () => {
    await handleClusterChange();
    searchDir('');
    getProjectUserList();
});

</script>
<style lang="less" scoped>
.query-container {
    .query-item {
        margin-right: 16px;
        display: inline-block;
        padding-bottom: 24px;
        .input {
            width: 160px;
        }
        .label {
            display: inline-block;
            margin-right: 16px;
        }
    }
}
:deep(.form-label) {
    justify-content: flex-end;
}
.selected-count {
    background: rgba(83,132,255,0.06);
    border: 1px solid #5384FF;
    color: #5384FF;
}
</style>
