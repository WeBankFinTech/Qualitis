
import {
    getCurrentInstance, ref, watch, computed,
} from 'vue';

export const useVModel = (prop) => {
    const usingProp = prop ?? 'modelValue';
    const vm = getCurrentInstance();
    const props = vm.props;
    const emit = vm.emit;
    const currentValue = ref(props[usingProp]);
    const updateCurrentValue = (value) => {
        if (value === currentValue.value) {
            return;
        }
        currentValue.value = value;
        emit(`update:${usingProp}`, value);
    };

    watch(
        () => props[usingProp],
        (val) => {
            updateCurrentValue(val);
        },
    );

    return computed({
        get() {
            return currentValue.value;
        },
        set(value) {
            updateCurrentValue(value);
        },
    });
};
