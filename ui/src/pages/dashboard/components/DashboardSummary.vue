<template>
    <div class="dashboard-summary">
        <p class="title">
            <span>{{title}}</span>
            <template v-if="total !== undefined && total !== null">
                <span style="margin-left: 6px;">{{$t('dashboard.total', { total })}}</span>
            </template>
        </p>
        <div class="last-update-time">
            <span>{{$t('dashboard.lastModify')}}：</span>
            <span>{{lastUpdateTime}}</span>
        </div>
        <div v-if="data && data.length > 0">
            <ul class="data-list">
                <li
                    v-for="(item, index) in data"
                    :key="index"
                    class="data-item"
                >
                    <div class="data-label">{{item.label}}</div>
                    <div class="data-value" :class="item.classNames">{{item.value}}</div>
                </li>
            </ul>
        </div>
    </div>
</template>
<script setup>
import { defineProps } from 'vue';
import { useI18n } from '@fesjs/fes';

const { t: $t } = useI18n();
defineProps({
    title: String,
    total: Number,
    lastUpdateTime: String,
    data: {
        type: Array,
        required: true,
        default: () => [],
    },
});
</script>
<style lang="less" scoped>
@import "@/style/varible";
.dashboard-summary {
    font-size: 12px;
    .title {
        line-height: 24px;
        font-size: 16px;
        font-weight: 500;
        color: @black-color;
    }
    .last-update-time {
        margin: 4px 0 16px;
        line-height: 20px;
        font-weight: 400;
        color: @tips-color;
    }
    .data-list {
        display: flex;
        align-items: center;
        .data-item {
            margin-right: 36px;
            &:last-of-type {
                margin-right: 0;
            }
        }
        .data-label {
            line-height: 20px;
            font-weight: 400;
            color: @label-color;
        }
        .data-value {
            line-height: 28px;
            font-size: 20px;
            font-weight: 500;
            &.red {
                color: @red-color;
            }
            &.blue {
                color: @blue-color;
            }
            &.orange {
                color: @orange-color;
            }
            &.yellow {
                color: @yellow-color;
            }
            &.green {
                color: @green-color;
            }
        }
    }
}
</style>
