<template>
    <div class="tag-editor">
        <div class="my-tag-list">
            <FTag
                v-for="tag in tags"
                :key="tag"
                closable
                class="my-tag-item"
                @close="removeTag(tag)">
                <div class="my-tag-item-content">{{tag}}</div>
            </FTag>
        </div>
        <div>
            <div>
                <FInput
                    v-show="flag"
                    v-model="currentTag"
                    class="my-input"
                    clearable
                    :placehodler="$t('common.pleaseEnter')"
                    :maxlength="25"
                    @blur="addTag"
                    @keyup.enter="addTag" />
            </div>
            <FButton v-show="!flag" @click="addTagPre">
                <template #icon><PlusOutlined /></template>{{$t('common.addTag')}}
            </FButton>
        </div>
    </div>
</template>
<script setup>
import { defineProps, ref, nextTick } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { PlusOutlined } from '@fesjs/fes-design/es/icon';

const props = defineProps({
    tags: {
        type: Array,
        required: true,
    },
});
const { t: $t } = useI18n();
const flag = ref(false);
const currentTag = ref('');
const addTagPre = async () => {
    flag.value = true;
    const el = document.querySelector('.my-input input');
    if (el) {
        await nextTick();
        el.focus();
    }
};
const addTag = () => {
    if (currentTag.value === '' || currentTag.value.trim() === '') {
        flag.value = false;
        return;
    }
    const index = props.tags.findIndex(item => item === currentTag.value);
    if (index !== -1) {
        FMessage.warn($t('toastWarn.tagRepeat'));
        return;
    }
    props.tags.push(currentTag.value);
    currentTag.value = '';
    flag.value = false;
};
const removeTag = (tag) => {
    const index = props.tags.findIndex(item => item === tag);
    if (index === -1) return;
    props.tags.splice(index, 1);
};
</script>
<style lang="less" scoped>
.tag-editor {
    width: 100%;
    .my-tag-list {
        display: flex;
        flex-flow: row wrap;
        align-items: center;
        flex: 0 0 100%;
        .my-tag-item {
            margin: 0 10px 8px 0;
            max-width: calc(50% - 27px);
            &-content {
              text-overflow: ellipsis;
              overflow: hidden;
              white-space: nowrap;
            }
        }
    }
}
.my-input {
    width: 108px;
}
</style>
