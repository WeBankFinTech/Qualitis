<template>
    <div class="date-range">
        <div class="mr16 my-date-picker">
            <FDatePicker
                type="datemonthrange"
                :maxDate="new Date()"
                :modelValue="monthRange"
                @update:modelValue="updateMonthRange"
            />
        </div>
        <FButton
            key="btn-1"
            class="mr16"
            :class="{ 'my-btn': true, active: months === 1 }"
            @click="updateMonths(1)"
        >
            {{$t('_.最近1个月')}}
        </FButton>
        <FButton key="btn-2" :class="{ 'my-btn': true, active: months === 3 }" @click="updateMonths(3)">{{$t('_.最近3个月')}}</FButton>
    </div>
</template>
<script setup>
import {
    defineProps, defineEmits, toRefs, computed,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import {
    getYear, getMonth, subMonths, differenceInMonths,
} from 'date-fns';

const { t: $t } = useI18n();
const props = defineProps({
    startMonth: Number,
    endMonth: Number,
    months: Number,
});
const emit = defineEmits([
    'update:start-month',
    'update:end-month',
    'update:months',
]);

const { startMonth, endMonth } = toRefs(props);
const monthRange = computed(() => [startMonth.value, endMonth.value]);
const updateMonthRange = (range) => {
    const startStamp = range[0];
    const endStamp = range[1];
    const now = Date.now();
    emit('update:start-month', startStamp);
    emit('update:end-month', endStamp);
    if (startStamp && endStamp) {
        const flag1 = getYear(now) === getYear(endStamp) && getMonth(now) === getMonth(endStamp);
        const months = differenceInMonths(endStamp, startStamp) + 1;
        if (flag1 && [1, 3].includes(months)) {
            emit('update:months', months);
        } else {
            emit('update:months', 0);
        }
    }
};
const updateMonths = (months) => {
    const _endMonth = new Date();
    const _startMonth = subMonths(_endMonth, months - 1);
    emit('update:months', months);
    emit('update:start-month', _startMonth.getTime());
    emit('update:end-month', _endMonth.getTime());
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
