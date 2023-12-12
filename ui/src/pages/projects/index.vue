<template>
    <article class="wd-content my-project">
        <header class="my-project-header">
            <ProjectNavBar v-model="currentProjectNavBar" :data="projectNavBars" />
        </header>
        <main>
            <!-- 普通项目 -->
            <template v-if="currentProjectNavBar === projectNavBars[0].value">
                <CommonProject :origin-project-headers="originProjectHeaders" />
            </template>
            <!-- 工作流项目 -->
            <template v-else-if="currentProjectNavBar === projectNavBars[1].value">
                <WorkflowProject :origin-project-headers="originProjectHeaders" />
            </template>
        </main>
    </article>
</template>
<script setup>
import {
    ref, onMounted, onUnmounted,
} from 'vue';
import { useI18n, useRoute } from '@fesjs/fes';
import ProjectNavBar from './components/NavBar';
import CommonProject from './components/commonProject';
import WorkflowProject from './components/workflowProject';

const { t: $t } = useI18n();
const route = useRoute();

// tab相关
const currentProjectNavBar = ref('');
const projectNavBars = [
    { label: $t('myProject.ordinaryProject'), value: '1' },
    { label: $t('myProject.workflowProject'), value: '2' },
];

// 项目表头设置
const originProjectHeaders = [
    { prop: 'project_id', label: $t('label.projectId') },
    { prop: 'project_name', label: $t('common.projectName') },
    { prop: 'cn_name', label: $t('common.cnName') },
    { prop: 'description', label: $t('label.projectDesc') },
    { prop: 'sub_system_name', label: $t('dataSourceManagement.subSystem') },
    { prop: 'create_user', label: $t('common.founder') },
    { prop: 'create_time', label: $t('common.createTime') },
    { prop: 'modify_user', label: $t('myProject.operationUser') },
    { prop: 'modify_time', label: $t('myProject.operationTime') },
];

onMounted(() => {
    // 初始化的query的tab参数优先
    currentProjectNavBar.value = route.query.tab || projectNavBars[0].value;
});

//  离开页面重置筛选条件
onUnmounted(() => {
    const storageKeys = [
        'commonProjectFilter',
        'workflowProjectFilter',
    ];
    storageKeys.forEach((name) => {
        sessionStorage.removeItem(name);
    });
});
</script>
<config>
{
    "name": "projects",
    "title": "$myProject.myProject"
}
</config>
