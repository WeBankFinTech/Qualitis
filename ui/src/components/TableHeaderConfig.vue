<template>
    <FModal
        :show="show"
        :top="50"
        :width="500"
        :maskClosable="false"
        @ok="updateHeaders"
        @update:show="$emit('update:show', $event)">
        <template #title>
            <span>{{$t('common.setTableHeaderConfig')}}</span>
            <span class="title-desc">({{$t('common.setTableHeaderConfigDesc')}})</span>
        </template>
        <div class="table-header-config">
            <div class="content">
                <ul class="section-list">
                    <li v-if="activatedHeadersLocal && activatedHeadersLocal.length > 0" class="section-item">
                        <h3 class="sub-title">{{$t('common.activatedHeaders')}}</h3>
                        <ul class="header-list">
                            <li
                                v-for="header in activatedHeadersLocal"
                                :key="header.prop"
                                class="header-item">
                                <FTag closable type="info" @close="removeActivatedField(header)">{{$t(header.label)}}</FTag>
                            </li>
                        </ul>
                    </li>
                    <li v-if="inactivatedHeadersLocal && inactivatedHeadersLocal.length > 0" class="section-item">
                        <h3 class="sub-title">{{$t('common.inactivatedHeaders')}}</h3>
                        <ul class="header-list">
                            <li
                                v-for="header in inactivatedHeadersLocal"
                                :key="header.prop"
                                class="header-item">
                                <FTag type="info" style="cursor: pointer;" @click.native="removeInactivatedField(header)">{{$t(header.label)}}</FTag>
                            </li>
                        </ul>
                    </li>
                    <li v-if="inactivatedHeadersLocal && inactivatedHeadersLocal.length === 0" class="section-item">
                        <h3 class="sub-title">{{$t('common.inactivatedHeaders')}}</h3>
                        <div class="hint">{{$t('common.noneInactivatedHeaders')}}</div>
                    </li>
                </ul>
            </div>
        </div>
    </FModal>
</template>
<script setup>
import {
    defineProps, defineEmits, onMounted, ref,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';

const { t: $t } = useI18n();
const props = defineProps({
    originHeaders: {
        type: Array,
        required: true,
        default: [],
    },
    headers: {
        type: Array,
        required: true,
        default: [],
    },
    show: {
        type: Boolean,
        required: true,
        default: false,
    },
    type: {
        type: String,
        required: true,
        default: '',
    },
});
const ACTIVES = props.headers.map((item) => {
    const target = props.originHeaders.find(subItem => subItem.prop === item.prop);
    if (target) {
        return Object.assign({}, item, {
            index: target.index,
        });
    }
    return item;
});
// 过滤数据
const IN_ACTIVES = props.originHeaders.filter((item) => {
    let shouldReturn = true;
    for (let i = 0; i < props.headers.length; i++) {
        const col = props.headers[i];
        if (item.prop === col.prop) {
            shouldReturn = false;
            break;
        }
    }
    return shouldReturn;
});
const emit = defineEmits(['update:headers', 'update:show']);
const activatedHeadersLocal = ref(cloneDeep(ACTIVES));
const inactivatedHeadersLocal = ref(cloneDeep(IN_ACTIVES));
const sortHeaders = headers => headers.sort((a, b) => a.index - b.index);
const doSort = () => {
    activatedHeadersLocal.value = sortHeaders(activatedHeadersLocal.value);
    inactivatedHeadersLocal.value = sortHeaders(inactivatedHeadersLocal.value);
};
const removeActivatedField = (header) => {
    activatedHeadersLocal.value = activatedHeadersLocal.value.filter(item => item.prop !== header.prop);
    inactivatedHeadersLocal.value.push(header);
    doSort();
};
const removeInactivatedField = (header) => {
    inactivatedHeadersLocal.value = inactivatedHeadersLocal.value.filter(item => item.prop !== header.prop);
    // 这里不能用push，push会导致父组件的 projectActivatedHeaders数组也更新了
    // activatedHeadersLocal.value.push(header);
    activatedHeadersLocal.value = [
        ...activatedHeadersLocal.value,
        header,
    ];
    doSort();
};
const checkHeaders = headers => Array.from(new Set(headers.map(item => item.prop))).length === headers.length;

const updateHeaders = () => {
    emit('update:headers', activatedHeadersLocal.value);
    emit('update:show', false);
    // 设置缓存
    if (props.type) {
        localStorage.setItem(`${props.type}_table_config`, JSON.stringify({
            active: activatedHeadersLocal.value,
            inActive: inactivatedHeadersLocal.value,
        }));
    }
};

onMounted(() => {
    if (!checkHeaders(props.headers)) {
        console.warn('[TableHeaderConfig.vue]: 存在prop属性重复的header！');
    }
    doSort();
});
</script>
<style lang="less" scoped>
@import "~@/style/varible";
.title-desc {
    line-height: 20px;
    font-size: 12px;
    font-weight: 400;
    color: @placeholder-color;
}
.table-header-config {
    .content {
        padding: 0;
        width: 100%;
        max-height: 600px;
        overflow-y: auto;
        text-align: left;
        background-color: transparent;
        border-radius: 0;
        .section-list {
            .section-item {
                margin-bottom: 16px;
                &:last-of-type {
                    margin-bottom: 0;
                }
                .sub-title {
                    margin-bottom: 8px;
                    line-height: 22px;
                    font-size: 14px;
                    color: @disabled-color;
                    font-weight: 400;
                }
                .hint {
                    line-height: 20px;
                    font-size: 12px;
                    font-weight: 400;
                    color: @placeholder-color;
                    margin-left: 0;
                }
            }
            .header-list {
                display: flex;
                flex-flow: row wrap;
                align-items: flex-start;
                .header-item {
                    margin: 0 8px 8px 0;
                    flex: 0 0;
                }
            }
        }
    }
}
</style>
