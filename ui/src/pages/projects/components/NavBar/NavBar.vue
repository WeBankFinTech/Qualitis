<template>
    <div class="nav-bar-container">
        <FScrollbar class="scroll" :shadow="{ x: true }">
            <ul class="nav-bar-list">
                <li
                    v-for="item in data"
                    :ref="(el) => setRuleRefs(el, item)"
                    :key="item.value"
                    class="nav-bar-item"
                    :class="{ active: item.value === modelValue, disabled: item.value !== modelValue && disabled }"
                    @click="updateActiveNavBar(item)"
                >
                    <span class="nav-bar-main">{{item.label}}</span>
                </li>
            </ul>
            <FTooltip v-if="hint" placement="bottom" :content="$t('projects.projectHint')">
                <QuestionCircleOutlined />
            </FTooltip>
        </FScrollbar>
        <slot name="suffix"></slot>
    </div>
</template>
<script setup>
import { useRoute, useRouter } from '@fesjs/fes';
import { FScrollbar } from '@fesjs/fes-design';
import { QuestionCircleOutlined } from '@fesjs/fes-design/es/icon';
import {
    defineProps, defineEmits, ref, watch,
} from 'vue';
import { getURLQueryParams } from '@/common/utils';

const router = useRouter();
const route = useRoute();
const props = defineProps({
    modelValue: {
        type: [String, Number],
        required: true,
    },
    data: {
        type: Array,
        required: false,
        default: () => [],
    },
    // 为了可以给我的项目代码公用，这个地方新增控制tip的参数
    hint: {
        type: Boolean,
        default: false,
    },
    disabled: {
        type: Boolean,
        default: false,
    },
    confirm: {
        type: Function,
        required: false,
        default: null,
    },
});
const emit = defineEmits(['update:modelValue']);

const ruleList = ref([]);
const setRuleRefs = (el, item) => {
    if (el) {
        ruleList.value.push({
            id: item,
            el,
        });
    }
};

const updateActiveNavBar = async (navBar) => {
    if (navBar.value === props.modelValue) {
        // 点击自己不做处理
        return;
    }
    if (navBar.value !== props.modelValue && props.disabled) {
        return;
    }
    try {
        if (props.confirm) {
            await props.confirm();
        }
        // 这个地方只是重写url，并不会重新加载
        // 切换tab的时候page和pageSize都要重制
        router.replace(`${route.path}?${getURLQueryParams({
            query: route.query,
            params: {
                tab: navBar.value, page: 1, pageSize: 10, id: navBar?.rule_id ? navBar.rule_id : route.query?.id || '',
            },
        })}`);
        // 下面通知更新一定要在url完成替换之后，否则会有时序问题
        setTimeout(() => {
            emit('update:modelValue', navBar.value);
        }, 0);
    } catch (err) {
        console.log(err, '用户取消更新tab');
    }
};
// 定位到当前tab
watch(() => props.modelValue, (newVal, oldVal) => {
    for (let i = 0; i < props.data.length; i++) {
        if (props.data[i].value === props.modelValue) {
            ruleList.value[i]?.el?.scrollIntoView({ behavior: 'smooth' });
        }
    }
});
</script>
<style lang="less" scoped>
@import "./NavBar.less";
</style>
