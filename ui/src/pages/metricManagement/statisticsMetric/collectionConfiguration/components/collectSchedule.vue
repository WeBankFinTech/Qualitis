<template>
    <FDrawer
        v-model:show="scheduleShow"
        displayDirective="if"
        :title="$t('_.发布采集调度')"
        width="50%"
        :footer="true"
        :oktext="$t('_.发布')"
        @cancel="handleClose"
        @ok="handleSubmit">
        <div class="selection">
            <ScheduleTemplate
                v-for="(item, index) in scheduleData.scheduleList"
                :ref="el => { if (el) scheduleRefs[index] = el }"
                :key="item"
                v-model:inputConfigData="scheduleData.scheduleList[index]"
                :removable="scheduleData.scheduleList.length > 1"
                :index="index + 1"
                :intervalList="intervalList"
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
import {
    fomatPreviewExecutionTime, executeIntervalList, executeIntervalDict, executionTimeWeekOptions, executionTimeMonthOptions,
} from '../utils';
import ScheduleTemplate from './scheduleTemplate.vue';


const { t: $t } = useI18n();


const props = defineProps({
    isScheduleShow: {
        type: Boolean,
        required: true,
    },

});
const emit = defineEmits(['scheduleSubmit', 'update:isScheduleShow']);

const scheduleShow = computed({
    get() {
        return props.isScheduleShow;
    },
    set(value) {
        emit('update:isScheduleShow', value);
    },
});
const handleClose = () => {
    scheduleShow.value = false;
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

const intervalList = [{
    label: $t('_.每月'),
    value: 'month',
}, {
    label: $t('_.每周'),
    value: 'week',
}, {
    label: $t('_.每日'),
    value: 'day',
}, {
    label: $t('_.每小时'),
    value: 'hour',
}];
const handleSubmit = async () => {
    try {
        const result = await Promise.all([...scheduleRefs.value.map(item => item.valid())]);
        console.log('提交', scheduleData.value);
        const params = scheduleData.value.scheduleList.map(item => ({
            cluster_name: item.collect_obj[0] ?? '',
            database: item.collect_obj[1] ?? '',
            table: item.collect_obj[2] ?? '',
            scheduler_configs: [{
                partition: item.partition,
                execute_interval: item.execute_interval,
                execute_date_in_interval: item.executionDateInInterval,
                execute_time_in_date: item.executionTimeInDate,
            }],
        }));
        const res = await request('api/v1/projector/imsmetric/collect/scheduler/create', params);
        FMessage.success($t('_.发布成功'));
        emit('scheduleSubmit');
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
