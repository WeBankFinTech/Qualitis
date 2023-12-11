<template>
    <span class="count" :class="props.type">{{props.count}}</span>
</template>
<script setup>
import { defineProps } from 'vue';

const props = defineProps({
    count: {
        type: Number,
        required: true,
        default: 0,
    },
    type: {
        type: String,
        required: true,
        default: 'all',
    },
});

</script>
<style lang="less" scoped>
.count {
    display: inline-block;
    margin-left: 5px;
}
.error {
    color: #ed4014;
}
.info {
    color: #2db7f5;
}
.warn {
    color: #ff9900;
}
</style>
