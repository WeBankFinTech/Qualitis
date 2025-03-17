import { ref, watch, onUnmounted } from 'vue';
import { useRouter, useRoute, onBeforeRouteLeave } from '@fesjs/fes';

export const useWatchTabSwitch = (defaultBar, tabs) => {
    const route = useRoute();
    const router = useRouter();

    const currentNavBar = ref(defaultBar);

    watch(() => currentNavBar.value, (curBar) => {
        if (curBar) {
            const curPath = tabs.find(item => item.value === curBar).path;
            router.push({ path: curPath });
        }
    });

    const watchTrigger = watch(() => route.path, (curPath) => {
        if (curPath) {
            const curBar = tabs.find(item => curPath.includes(item.path))?.value;
            currentNavBar.value = curBar;
        }
    }, { immediate: true });

    onBeforeRouteLeave(() => {
        watchTrigger();
    });

    return {
        currentNavBar,
    };
};
