import { ref, watchEffect } from 'vue';

export function useTransition() {
    const expanded = ref(false);
    const body = ref(null);
    const height = ref(0);
    watchEffect(() => {
        if (expanded.value) {
            height.value = `${body.value?.scrollHeight || 0}px`;
            setTimeout(() => {
                if (height.value !== 0) {
                    height.value = 'auto';
                }
            }, 300);
        } else {
            height.value = `${body.value?.scrollHeight || 0}px`;
            setTimeout(() => {
                height.value = '0px';
            }, 0);
        }
    });
    return {
        expanded,
        body,
        height,

    };
}
