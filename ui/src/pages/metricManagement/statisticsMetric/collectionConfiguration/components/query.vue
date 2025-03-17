<template>
    <div class="query-container">
        <div class="query-item">
            <span class="label">{{$t('_.算子英文名')}}</span>
            <FInput v-model="commonConditions.en_name" class="input" :placeholder="$t('_.请输入')"></FInput>
        </div>
        <div class="query-item">
            <span class="label">{{$t('_.集群')}}</span>
            <FSelect v-model="commonConditions.cluster_name" clearable filterable style="width: 160px" :options="clusterList"></FSelect>
        </div>
        <div class="query-item">
            <span class="label">{{$t('_.数据库')}}</span>
            <FSelect v-model="commonConditions.database" clearable filterable style="width: 160px" :options="dbsLists"></FSelect>
        </div>
        <div class="query-item">
            <span class="label">{{$t('_.数据表')}}</span>
            <FSelect v-model="commonConditions.table" clearable filterable style="width: 160px" :options="tableList"></FSelect>
        </div>
        <div class="query-item">
            <span class="label">{{$t('_.创建人')}}</span>
            <FSelect v-model="commonConditions.create_name" filterable clearable style="width: 160px" :options="userList"></FSelect>
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
        <FModal v-model:show="showModal" :title="$t('_.高级筛选')" :okText="$t('_.查询')" :width="600" displayDirective="if" @ok="advance">
            <FForm ref="advancedConditionsForm" :labelWidth="56" :model="advancedConditions" labelClass="form-label">
                <FFormItem :label="$t('_.算子中文名')" prop="cn_name">
                    <FInput v-model="advancedConditions.cn_name" clearable class="input" :placeholder="$t('_.请输入')"></FInput>
                </FFormItem>
                <FFormItem :label="$t('_.算子英文名')" prop="en_name">
                    <FInput v-model="advancedConditions.en_name" clearable class="input" :placeholder="$t('_.请输入')"></FInput>
                </FFormItem>
                <FFormItem :label="$t('_.集群')" prop="cluster_name">
                    <FSelect v-model="advancedConditions.cluster_name" clearable :options="clusterList"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.数据库')" prop="database">
                    <FSelect v-model="advancedConditions.database" clearable filterable :options="dbsLists"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.数据表')" prop="table">
                    <FSelect v-model="advancedConditions.table" clearable filterable :options="tableList"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.采集分区')" prop="partition">
                    <FSelect v-model="advancedConditions.partition" clearable filterable :options="partitionLists"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.创建人')" prop="create_name">
                    <FSelect v-model="advancedConditions.create_name" clearable filterable :options="userList"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.创建时间')" prop="createTimeRange">
                    <FDatePicker
                        v-model="advancedConditions.createTimeRange"
                        type="datetimerange"
                        clearable
                        format="yyyy-MM-dd HH:mm:ss"
                    />
                </FFormItem>
                <FFormItem :label="$t('_.修改人')" prop="update_name">
                    <FSelect v-model="advancedConditions.update_name" clearable filterable :options="userList"></FSelect>
                </FFormItem>
                <FFormItem :label="$t('_.修改时间')" prop="updateTimeRange">
                    <FDatePicker
                        v-model="advancedConditions.updateTimeRange"
                        type="datetimerange"
                        clearable
                        format="yyyy-MM-dd HH:mm:ss"
                    />
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import {
    computed, ref, defineEmits, onMounted, defineProps,
} from 'vue';
import { request, useI18n } from '@fesjs/fes';


const { t: $t } = useI18n();
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
    clusterList: {
        type: Array,
        default: () => [],
    },
    dbsLists: {
        type: Array,
        default: () => [],
    },
    tableList: {
        type: Array,
        default: () => [],
    },
    userList: {
        type: Array,
        default: () => [],
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

const showModal = ref(false);
// // 计算高级筛选已选项 ！数字0和空数组！
// function hasSelectedCount(data) {
//     const keys = Object.keys(data);
//     let count = 0;
//     for (let i = 0; i < keys.length; i++) {
//         if ((typeof data[keys[i]] === 'number' && +data[keys[i]] >= 0) || (data[keys[i]] !== '' && data[keys[i]]?.length > 0)) {
//             count++;
//         }
//     }
//     return count;
// }
const advancedConditionsSelectedCount = computed(() => {
    console.log('高级筛选已选项', advancedConditions.value);
    const keys = Object.keys(advancedConditions.value);
    let count = 0;
    for (let i = 0; i < keys.length; i++) {
        if (!(Array.isArray(advancedConditions.value[keys[i]]) && advancedConditions.value[keys[i]].length === 0) && advancedConditions.value[keys[i]] !== '' && advancedConditions.value[keys[i]] !== null) {
            count++;
        }
    }
    return count;
});
const advance = () => {
    console.log('高级筛选条件', advancedConditions.value);
    emit('search');
    showModal.value = false;
};
const common = async () => {
    advancedConditions.value = {
        // cn_name: '',
        // en_name: '',
        // cluster_name: '',
        // database: '',
        // table: '',
        // partition: '',
        // create_name: '',
        // createTimeRange: [],
        // update_name: '',
        // updateTimeRange: [],
    };
    emit('search');
};
const reset = () => {
    emit('reset');
};
const partitionLists = ref([]);
const openAdvancedSearch = async () => {
    const res = await request('api/v1/projector/imsmetric/collect/partition/list', {});
    partitionLists.value = res?.map(item => ({ value: item, label: item })) ?? [];
    showModal.value = true;
    commonConditions.value = {};
};
onMounted(async () => {
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
