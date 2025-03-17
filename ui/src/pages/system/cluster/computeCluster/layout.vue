<template>
    <article class="wd-content my-project">
        <header v-if="currentNavBar" class="my-project-header">
            <ProjectNavBar v-model="currentNavBar" :data="navBar" mode="normal" />
        </header>
        <main>
            <router-view />
        </main>
    </article>
</template>
<script setup>
import { useI18n } from '@fesjs/fes';

import ProjectNavBar from '@/pages/projects/components/NavBar';
import { useWatchTabSwitch } from '@/pages/projects/hooks/useWatchTabSwitch';


const { t: $t } = useI18n();

// tab相关
const navBar = [
    { label: $t('_.Flink集群'), value: 'Flink', path: '/system/cluster/computeCluster/flinkCluster' },
    { label: $t('_.Streamis集群'), value: 'Streamis', path: '/system/cluster/computeCluster/streamisCluster' },
];

const { currentNavBar } = useWatchTabSwitch('Flink', navBar);

</script>
