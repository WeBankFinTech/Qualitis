<template>
    <div class="date-range">
        <div class="mr16 my-date-picker">
            <FDatePicker
                type="daterange"
                :modelValue="dateRange"
                @update:modelValue="updateDateRange"
            />
        </div>
        <FButton
            key="btn-1"
            class="mr16"
            :class="{ 'my-btn': true, active: days === 7 }"
            @click="updateDays(7)"
        >
            {{$t('dashboard.last7Days')}}
        </FButton>
        <FButton key="btn-2" :class="{ 'my-btn': true, active: days === 30 }" @click="updateDays(30)">{{$t('dashboard.last30Days')}}</FButton>
    </div>
</template>
<script setup>
import {
    defineProps, defineEmits, toRefs, computed,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import {
    getYear, getMonth, getDate, subDays, differenceInDays,
} from 'date-fns';

const { t: $t } = useI18n();
const props = defineProps({
    startDate: Number,
    endDate: Number,
    days: Number,
});
const emit = defineEmits([
    'update:start-date',
    'update:end-date',
    'update:days',
]);

const { startDate, endDate } = toRefs(props);
const dateRange = computed(() => [startDate.value, endDate.value]);
const updateDateRange = (range) => {
    const startStamp = range[0];
    const endStamp = range[1];
    const now = Date.now();
    emit('update:start-date', startStamp);
    emit('update:end-date', endStamp);
    if (startStamp && endStamp) {
        const flag1 = getYear(now) === getYear(endStamp) && getMonth(now) === getMonth(endStamp) && getDate(now) === getDate(endStamp);
        const days = differenceInDays(endStamp, startStamp) + 1;
        if (flag1 && [7, 30].includes(days)) {
            emit('update:days', days);
        } else {
            emit('update:days', 0);
        }
    }
};
const updateDays = (days) => {
    const _endDate = new Date();
    const _startDate = subDays(_endDate, days - 1);
    emit('update:days', days);
    emit('update:start-date', _startDate.getTime());
    emit('update:end-date', _endDate.getTime());
};
</script>
<style lang="less" scoped>
@import "@/style/varible";
.date-range {
    display: flex;
    align-items: center;
    .my-date-picker {
        width: 294px;
        :deep(.fes-range-input-inner) {
            width: 110px;
        }
        :deep(.fes-range-input-separator) {
            width: 16px;
            height: 16px;
        }
    }
    .my-btn {
        color: @black-color;
        background-color: #FFFFFF;
        border-color: #cfd0d3;
        &.active {
            color: @blue-color;
            background-color: #F5F8FF;
            border-color: @blue-color;
        }
    }
}
.mr16 {
    margin-right: 16px;
}
</style>
