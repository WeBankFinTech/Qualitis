<template>
    <FDrawer
        v-model:show="historyScheduleShow"
        displayDirective="if"
        :title="$t('_.发布历史采集')"
        width="50%"
        :footer="true"
        :oktext="$t('_.发布')"
        @cancel="handleClose"
        @ok="handleSubmit">
        <div class="selection">
            <Addhistory
                v-for="(item, index) in scheduleData.scheduleList"
                :ref="el => { if (el) scheduleRefs[index] = el }"
                :key="item"
                v-model:inputHistoryConfigData="scheduleData.scheduleList[index]"
                :removable="scheduleData.scheduleList.length > 1"
                :index="index + 1"
                @delete="deleteSchedule(index)"
            />
            <div class="add-sign">
                <span class="add-sign-btn" @click="addSchedule">
                    <PlusCircleOutlined style="padding-right: 4.58px;" />{{$t('_.添加发布组')}}</span>
            </div>
        </div>
    </FDrawer>
</template>
<script setup>

import {
    ref, defineProps, defineEmits, computed, watch,
} from 'vue';
import { request, useI18n } from '@fesjs/fes';
import {
    PlusOutlined, PlusCircleOutlined, MinusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { FMessage, FModal, FTimePicker } from '@fesjs/fes-design';
import dayjs from 'dayjs';
import {
    fomatPreviewExecutionTime, executeIntervalList, executeIntervalDict, executionTimeWeekOptions, executionTimeMonthOptions,
} from '../utils';
import Addhistory from './addhistory.vue';


const { t: $t } = useI18n();


const props = defineProps({
    isHistoryCollect: {
        type: Boolean,
        required: true,
    },

});
const emit = defineEmits(['histortSubmit', 'update:isHistoryCollect']);

const historyScheduleShow = computed({
    get() {
        return props.isHistoryCollect;
    },
    set(value) {
        emit('update:isHistoryCollect', value);
    },
});
const handleClose = () => {
    historyScheduleShow.value = false;
};
const scheduleRef = ref(null);
const scheduleData = ref({
    scheduleList: [{}],
});
// 组件
const scheduleRefs = ref([]);
const deleteSchedule = (index) => {
    scheduleData.value.scheduleList.splice(index, 1);
};
const addSchedule = () => {
    scheduleData.value.scheduleList.push({
        collect_type: true,
    });
    console.log('data.value.collect_configs', scheduleData.value);
};
function hasDuplicates(params) {
    const seen = new Set();

    return params.some((item) => {
        const key = `${item.cluster_name}|${item.db_name}|${item.table_name}`;
        if (seen.has(key)) {
            return true; // 找到重复项
        }
        seen.add(key);
        return false;
    });
}
const handleSubmit = async () => {
    try {
        const result = await Promise.all([...scheduleRefs.value.map(item => item.valid())]);
        console.log('提交', scheduleData.value);
        const params = scheduleData.value.scheduleList.map(item => ({
            cluster_name: item.cluster_name ?? '',
            db_name: item.db_name ?? '',
            table_name: item.table_name ?? '',
            column_name_list: item.column_name_list ?? [],
            start_date: item?.time?.length === 2 ? dayjs(item.time[0]).format('YYYY-MM-DD') : '',
            end_date: item?.time?.length === 2 ? dayjs(item.time[1]).format('YYYY-MM-DD') : '',
        }));
        const hasDuplicateItems = hasDuplicates(params);
        if (hasDuplicateItems) {
            console.log('存在重复项');
            return FMessage.warn('存在重复的集群库表');
        }
        console.log('不存在重复项');

        const res = await request('/api/v1/projector/imsmetric/collect/scheduler/history/update', params);
        FMessage.success($t('_.发布成功'));
        emit('histortSubmit');
        handleClose();
    } catch (err) {
        console.log(err);
    }
};

</script>
<style lang="less" scoped>
@import "@/style/varible";
.workflows-select {
    width: 100%;
}
.execution-time-wrapper {
    width: 100%;
    align-items: center;

    .mr-8px {
        margin-right: 8px;
    }
}
.add-sign {
    color: #5384FF;
    .add-sign-btn {
        display: flex;
        align-items: center;
        justify-content: flex-start;
        cursor: pointer;
    }
}
</style>
