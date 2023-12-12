import { watch, reactive } from 'vue';
import { isEqual } from 'lodash-es';

export function useFormModel(props, emit, config = []) {
    const target = {};
    config.forEach((item) => {
        target[item] = props[item];
    });
    const datasource = reactive(target);
    watch(() => datasource, () => {
        for (let i = 0; i < config.length; i++) {
            const key = config[i];
            emit(`update:${key}`, datasource[key]);
        }
    }, { deep: true });
    watch(() => props, () => {
        for (let i = 0; i < config.length; i++) {
            const key = config[i];
            if (!isEqual(props[key], datasource[key])) {
                datasource[key] = props[key];
            }
        }
    }, { deep: true });
    return {
        datasource,
    };
}

export function useNewFormModel(props, emit, config = []) {
    const target = {};
    config.forEach((item) => {
        target[item] = props[item];
    });
    const datasource = reactive(target);
    watch(() => datasource, () => {
        for (let i = 0; i < config.length; i++) {
            const key = config[i];
            emit(`update:${key}`, datasource[key]);
        }
    }, { deep: true });
    watch(() => props, () => {
        for (let i = 0; i < config.length; i++) {
            const key = config[i];
            if (!isEqual(props[key], datasource[key])) {
                datasource[key] = props[key];
            }
        }
    }, { deep: true });
    return datasource;
}
