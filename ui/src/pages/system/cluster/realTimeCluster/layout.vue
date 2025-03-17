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
    { label: $t('_.Kafka集群'), value: 'Cluster', path: '/system/cluster/realTimeCluster/kafkaClusterConfiguration' },
    { label: $t('_.RMB集群'), value: 'Rmb', path: '/system/cluster/realTimeCluster/rmbClusterConfiguration' },
];

const { currentNavBar } = useWatchTabSwitch('Cluster', navBar);

</script>
