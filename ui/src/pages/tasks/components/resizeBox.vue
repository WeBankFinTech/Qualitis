<template>
    <div class="content-box">
        <slot></slot>
        <div
            ref="handle"
            class="resize-handle"
            @mousedown="handleMouseDown"
        ></div>
    </div>
</template>
<script setup>
import {
    ref, onMounted, reactive, defineEmits,
} from 'vue';

const handle = ref(null);
const delta = reactive({
    deltaX: 0,
    deltaY: 0,
});

const emit = defineEmits(['onMouseMove', 'onMouseUp']);
const handleMouseDown = (event) => {
    event.preventDefault();
    const startX = event.pageX;
    const startY = event.pageY;

    const handleMouseMove = (e) => {
        delta.deltaX = e.pageX - startX;
        delta.deltaY = e.pageY - startY;
        emit('onMouseMove', delta);
    };

    const handleMouseUp = () => {
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', handleMouseUp);
        emit('onMouseUp', delta);
    };

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
};


</script>
<style lang='less' scoped>
.content-box {
    position: relative;

    .resize-handle {
        position: absolute;
        bottom: 0;
        right: 0;
        width: 100%;
        height: 5px;
        cursor: ns-resize;
    }
}
</style>
