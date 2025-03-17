<template>
    <template v-if="mode === 'preview'">
        <FForm
            ref="gitFormRef"
            :model="gitForm"
            labelPosition="left"
            :labelWidth="108"
        >
            <FFormItem prop="git_type" :label="$t('_.Git分类')">
                {{getGitType(gitForm.git_type)}}
            </FFormItem>
            <FFormItem prop="git_repo" :label="$t('_.Git地址')">
                {{gitForm.git_repo}}
            </FFormItem>
            <FFormItem prop="git_commit" :label="$t('_.提交备注')">
                {{gitForm.git_commit}}
            </FFormItem>
            <FFormItem prop="git_branch" :label="$t('_.代码分支')">
                {{gitForm.git_branch}}
            </FFormItem>
            <FFormItem prop="git_root_dir" :label="$t('_.代码存储分目录')">
                {{gitForm.git_root_dir}}
            </FFormItem>
        </FForm>
    </template>
    <template v-else>
        <FForm
            ref="gitFormRef"
            :model="gitForm"
            :rules="formRule"
            labelPosition="right"
            :labelWidth="108"
        >
            <FFormItem prop="git_type" :label="$t('_.Git分类')">
                <FSelect v-model="gitForm.git_type" :placeholder="$t('_.请选择Git分类')" :options="gitTypeList" />
            </FFormItem>
            <FFormItem prop="git_repo" :label="$t('_.Git地址')">
                <FInput v-model="gitForm.git_repo" :placeholder="$t('_.请输入代码集的地址')" />
            </FFormItem>
            <FFormItem v-if="portmode === 'export'" prop="git_commit" :label="$t('_.提交备注')">
                <FInput v-model="gitForm.git_commit" type="textarea" :placeholder="$t('_.请输入')" />
            </FFormItem>
            <FFormItem prop="git_branch" :label="$t('_.代码分支')">
                <FInput v-model="gitForm.git_branch" :placeholder="$t('_.请输入代码分支')" />
            </FFormItem>
            <FFormItem prop="git_root_dir" :label="$t('_.代码存储根目录')">
                <FInput v-model="gitForm.git_root_dir" :placeholder="$t('_.请输入代码存储根目录')" />
            </FFormItem>
        </FForm>
    </template>
</template>
<script setup>

import {
    computed, ref, defineProps, defineEmits, defineExpose,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';

const { t: $t } = useI18n();
const props = defineProps({
    // 预览-preview和编辑-detail两种模式
    mode: {
        type: String,
        required: true,
        default: 'preview',
    },
    gitForm: {
        type: Object,
        required: true,
        default: {
            git_branch: 'master',
            git_type: 1,
            git_root_dir: 'dqm/',
        },
    },
    portmode: {
        type: String,
        default: 'import',
    },
});
const emit = defineEmits(['update:gitForm']);
const gitForm = computed({
    get: () => props.gitForm,
    set: (value) => {
        emit('update:gitForm', value);
    },
});

const gitFormRef = ref(null);
const gitTypeList = ref([
    {
        value: 1,
        label: 'Webank Git',
    },
]);
const getGitType = (value) => {
    let gitTypeLable = '';
    gitTypeList.value.forEach((e) => {
        if (e.value === value) {
            gitTypeLable = e.label;
        }
    });
    return gitTypeLable;
};
const validateDir = (rule, value) => {
    if (value.includes('\\')) {
        return false;
    }
    return true;
};
const formRule = ref({
    git_type: [
        {
            type: 'number', required: true, message: $t('_.不能为空'),
        },
    ],
    git_repo: [
        {
            required: true, message: $t('_.不能为空'),
        },
    ],
    git_branch: [
        {
            required: true, message: $t('_.不能为空'),
        },
    ],
    git_root_dir: [
        {
            required: true, message: $t('_.不能为空'),
        },
        {
            validator: validateDir,
            message: $t('_.输入地址不能包含\\'),
        },
    ],
});

const valid = async () => {
    try {
        const result = await gitFormRef.value.validate();
        console.log('gitBase表单验证成功: ', result);
        return true;
    } catch (error) {
        console.log('gitBase表单验证失败: ', error);
        return false;
    }
};

defineExpose({ valid });

</script>
