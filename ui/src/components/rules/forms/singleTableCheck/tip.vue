<template>
    <FTooltip>
        <ExclamationCircleOutlined class="hint" />
        <template #content>
            <slot>{{props.content}}</slot>
        </template>
    </FTooltip>
</template>
<script setup>
import { defineProps } from 'vue';
import { ExclamationCircleOutlined } from '@fesjs/fes-design/icon';
import { FTooltip } from '@fesjs/fes-design';

const props = defineProps({ content: String });

</script>

<style lang="less" scoped>
  .hint {
    display: inline-block;
    margin-left: 8px;
    color: #646670;
  }
</style>
