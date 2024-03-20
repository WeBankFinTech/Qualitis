<template>
    <!-- 当页面有新增的时候，由于数据清洗比较多，所以在这个地方变更key，使整个组件重新渲染 -->
    <commonList :key="formDetailPageKey" />
</template>
<script setup>
import {
    ref,
} from 'vue';
import commonList from '@/components/rules/commonList';
import eventbus from '@/common/useEvents';
import { forceFormPageUpdateEvent, useListener } from '@/components/rules/utils';

const formDetailPageKey = ref(new Date().getTime());

useListener(forceFormPageUpdateEvent, () => {
    // 更新组件的key，让他重新渲染
    formDetailPageKey.value = new Date().getTime();
    setTimeout(() => {
        document.getElementById('ruleFormPage').scrollTop = 0;
    }, 0);
});
</script>
<config>
{
    "name": "projectRulesDetail",
    "title": "项目详情"
}
</config>
