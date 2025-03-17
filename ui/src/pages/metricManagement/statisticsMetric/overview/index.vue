<template>
    <article class="wd-content dashboard">
        <main>
            <section class="section">
                <div class="wd-content-body">
                    <h4 class="wd-content-title">{{$t('_.采集库表处理状态分布记录')}}</h4>
                    <div class="date-range-container">
                        <DateRange
                            v-model:start-month="collectStartMonth"
                            v-model:end-month="collectEndMonth"
                            v-model:months="collectMonths"
                        />
                    </div>
                    <div id="collect-chart" class="my-chart-container"></div>
                </div>
            </section>
        </main>
    </article>
</template>
<script setup>
import { useI18n } from '@fesjs/fes';
import { subMonths } from 'date-fns';
import { BPageLoading } from '@fesjs/traction-widget';
import DateRange from './components/DateRange';
import useDateRange from './hooks/useDateRange';
import useCollectChart from './hooks/useCollectChart';


const { t: $t } = useI18n();

const months = 1;
const endMonth = new Date();
const startMonth = subMonths(endMonth, months - 1);
const {
    startMonth: collectStartMonth,
    endMonth: collectEndMonth,
    months: collectMonths,
} = useDateRange(startMonth.getTime(), endMonth.getTime(), months);
useCollectChart(collectStartMonth, collectEndMonth, collectMonths);

</script>
<style lang="less" scoped>
@import "@/style/varible";
.dashboard {
    max-height: 100%;
    overflow-y: auto;
    .section {
        margin-bottom: 24px;
        &:last-of-type {
            margin-bottom: 0;
        }
        & > .wd-content-body {
            &:last-of-type {
                margin-bottom: 0;;
            }
        }
    }
    .table-container {
        margin: 16px 0 17px;
        .red {
            color: @red-color;
        }
        .orange {
            color: @orange-color;
        }
        .green {
            color: @green-color;
        }
    }
    .chart-title {
        line-height: 24px;
        font-size: 16px;
        color: @black-color;
        font-weight: 500;
    }
    .date-range-container {
        margin: 24px 0;
    }
    .my-chart-container {
        height: 520px;
    }
}
</style>
