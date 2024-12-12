<template>
    <div class="wd-tags-panel">
        <FTag
            v-for="tag in tags"
            :key="tag"
            closable
            @close="deleteTag(tag)"
        >
            {{tag}}
        </FTag>
        <FInput
            v-if="showTagInput"
            ref="tagInputRef"
            v-model="tempTagInput"
            class="input-nef-tag"
            size="small"
            :maxlength="25"
            @keyup.enter="addNewTag"
            @blur="addNewTag"
        >
        </FInput>
        <FButton v-else class="button-nef-tag" @click="toggleTagInput"><PlusOutlined />{{$t('common.addTag')}}</FButton>
    </div>
</template>
<script setup>
import {
    ref, nextTick, getCurrentInstance,
} from 'vue';
import {
    useI18n,
} from '@fesjs/fes';
import { useFormModel } from '@/common/useModel';
import { PlusOutlined } from '@fesjs/fes-design/icon';

const { t: $t } = useI18n();

const {
    proxy: { $forceUpdate },
} = getCurrentInstance();

// eslint-disable-next-line no-undef
const props = defineProps({
    tags: {
        type: Array,
        default: () => [],
    },
});

// eslint-disable-next-line no-undef
const emit = defineEmits(['update:tags']);
const { datasource } = useFormModel(props, emit, ['tags']);

const showTagInput = ref(false);

const tempTagInput = ref('');

const deleteTag = async (tag) => {
    const index = datasource.tags.indexOf(tag);
    datasource.tags.splice(index, 1);
    await $forceUpdate();
};

const addNewTag = () => {
    if (tempTagInput.value) {
        if (datasource.tags) {
            datasource.tags.push(tempTagInput.value);
        } else {
            datasource.tags = [tempTagInput.value];
        }
    }
    showTagInput.value = false;
    tempTagInput.value = '';
};

const tagInputRef = ref(null);

const toggleTagInput = async () => {
    showTagInput.value = true;
    await nextTick();
    tagInputRef.value.focus();
};
</script>
<style lang="less" scoped>
.wd-tags-panel{
    display: flex;
    flex-wrap: wrap;
    .fes-tag{
        margin-right: 8px;
        margin-bottom: 8px;
    }
    .input-nef-tag {
        width: 100px;
    }
    .button-nef-tag {
        margin-right: 16px;
    }
}
</style>
