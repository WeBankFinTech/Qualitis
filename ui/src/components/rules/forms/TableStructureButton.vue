<template>
    <div class="create-button">
        <div class="content"><PlusOutlined /><span class="text">创建表结构</span></div>
    </div>
</template>
<script setup>
import { PlusOutlined } from '@fesjs/fes-design/icon';
</script>

<style scoped lang="less">
@import "@/style/varible";
.create-button {
    cursor: pointer;
    height: 86px;
    width: 290px;
    border: 0.5px solid @blue-color;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: space-around;
    .content {
        display: flex;
        align-items: center;
        color: @blue-color;
        line-height: 100%;
        .text {
            display: inline-block;
            margin-left: 5px;
            font-size: 15px;
        }
    }
}
</style>
