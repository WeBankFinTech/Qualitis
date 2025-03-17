<template>
    <div>
        <BPageLoading :actionType="actionType"></BPageLoading>
    </div>
</template>
<script setup>
import { ref } from 'vue';
import { BPageLoading } from '@fesjs/traction-widget';

const actionType = ref('emptyInitResult');

</script>
