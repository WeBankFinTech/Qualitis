<template>
    <div class="page-header-condition">
        <!-- <div class="condition-item">
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
        </div> -->
        <div class="condition-item">
            <FSpace :size="CONDITIONBUTTONSPACE">
                <FButton type="primary" @click="handleSearch">{{$t('taskQuery.search')}}</FButton>
                <FButton @click="handleReset">{{$t('common.reset')}}</FButton>
            </FSpace>
        </div>
    </div>
</template>
<script setup>
import {
    reactive, ref, defineProps, defineEmits,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';

const { t: $t } = useI18n();
const tableDetailSearchForm = reactive({});

const props = defineProps({
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
    advanceQueryData: {
        type: Object,
        required: true,
    },
    subSystems: {
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
]);

const handleSearch = () => {
    emit('on-search');
};

const handleAdvanceSearch = () => {
    emit('on-advance-search');
};

const handleReset = () => {
    emit('on-reset');
};

const showAdvanceSearchModal = ref(false);
</script>
