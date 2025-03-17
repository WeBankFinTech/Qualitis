<template>
    <article class="wd-content my-project">
        <header class="my-project-header">
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
    { label: $t('_.Flink Jar应用'), value: 'Jar', path: '/projects/realTimeProject/flink' },
    { label: $t('_.Flink Sql应用'), value: 'Sql', path: '/projects/realTimeProject/sql' },
];

const { currentNavBar } = useWatchTabSwitch('Jar', navBar);

</script>
