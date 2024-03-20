<template>
    <div class="add-map-modal">
        <FModal
            v-model:show="showModal"
            :title="modelTitle"
            displayDirective="if"
            width="800px"
            :maskClosable="false"
            @ok="saveMap"
            @cancel="cancel"
        >
            <div class="body">
                <div class="expression">
                    <div class="left">
                        <div class="title">左侧表达式</div>
                        <FInput v-model="leftStatement" type="textarea" placeholder="请输入左侧表达式" @click="changeSelection('left')"></FInput>
                    </div>
                    <div class="relation">
                        <FSelect v-model="relationshipBetween" placeholder="关系">
                            <FOption v-for="(item, index) in relationList" :key="index" :value="item.type" :label="item.value"></FOption>
                        </FSelect>
                    </div>
                    <div class="right">
                        <div class="title">右侧表达式</div>
                        <FInput v-model="rightStatement" type="textarea" placeholder="请输入右侧表达式" @click="changeSelection('right')"></FInput>
                    </div>
                </div>
                <div class="tables">
                    <div v-if="leftGroup.length || rightGroup.length" class="desc">展开数据表可查看字段信息</div>
                    <div v-if="leftGroup.length" class="table" :class="{ small: leftExpand }">
                        <div class="info" @click="expand('left')">
                            <div class="left">数据表：{{leftTableName}}</div>
                            <div class="right">
                                <DownOutlined v-if="!leftExpand" />
                                <UpOutlined v-if="leftExpand" />
                            </div>
                        </div>
                        <div v-if="leftExpand" class="expand">
                            <div class="input"><input v-model="leftChar" type="text" placeholder="请输入搜索内容" /></div>
                            <div class="group">
                                <div v-for="(item, index) in leftFilterGroup" :key="index" class="item" @click="selectItem(item)">{{item.column_name}} ({{item.data_type}})</div>
                            </div>
                        </div>
                    </div>
                    <div v-if="rightGroup.length" class="table" :class="{ small: rightExpand }">
                        <div class="info" @click="expand('right')">
                            <div class="left">数据表：{{rightTableName}}</div>
                            <div class="right">
                                <DownOutlined v-if="!rightExpand" />
                                <UpOutlined v-if="rightExpand" />
                            </div>
                        </div>
                        <div v-if="rightExpand" class="expand">
                            <div class="input"><input v-model="rightChar" type="text" placeholder="请输入搜索内容" /></div>
                            <div class="group">
                                <div v-for="(item, index) in rightFilterGroup" :key="index" class="item" @click="selectItem(item)">{{item.column_name}} ({{item.data_type}})</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </FModal>
    </div>
</template>

<script setup>
import {
    ref, computed, watch, defineProps, defineEmits,
} from 'vue';
import { DownOutlined, UpOutlined } from '@fesjs/fes-design/es/icon';
import { FMessage } from '@fesjs/fes-design';

// props: 数据源数据, 打开类型（新增、编辑、详情）

const props = defineProps({
    type: {
        type: String,
        default: 'add',
    },
    show: {
        type: Boolean,
        default: false,
    },
    form: {
        type: Object,
        default: () => ({}),
    },
    modelTitle: {
        type: String,
        required: true,
    },
});
const emit = defineEmits(['update:show', 'saved', 'cancel']);

const map = ref(props.form);
const showModal = computed({
    get() {
        return props.show;
    },
    set(value) {
        emit('update:show', value);
    },
});
const editType = computed(() => props.type);

const leftStatement = ref(props.form.left_statement || '');
const relationshipBetween = ref(props.form.relationshipBetween || '');
const rightStatement = ref(props.form.right_statement || '');
const leftTableName = ref(props.form.leftTableName || '');
const rightTableName = ref(props.form.rightTableName || '');
const leftExpand = ref(false);
const rightExpand = ref(false);
const leftChar = ref('');
const rightChar = ref('');
const leftGroup = ref(props.form.leftGroup || []);
const rightGroup = ref(props.form.rightGroup || []);

const relationList = [
    {
        type: 1,
        value: '=',
    },
    {
        type: 2,
        value: '!=',
    },
    {
        type: 3,
        value: '>',
    },
    {
        type: 4,
        value: '>=',
    },
    {
        type: 5,
        value: '<',
    },
    {
        type: 6,
        value: '<=',
    },
];

// 因为取消的场景需要还原数据，所以不能改传递过来的map，不能用computed(get、set)
watch(() => props.form, (newForm) => {
    console.log('newForm', newForm);
    map.value = newForm;
    leftStatement.value = newForm.left_statement || '';
    relationshipBetween.value = newForm.relationshipBetween || '';
    rightStatement.value = newForm.right_statement || '';

    leftTableName.value = newForm.leftTableName || '';
    rightTableName.value = newForm.rightTableName || '';
    leftGroup.value = newForm.leftGroup || [];
    rightGroup.value = newForm.rightGroup || [];
}, { deep: true });

function expand(side) {
    console.log('expand', side);
    if (side === 'left') {
        leftExpand.value = !leftExpand.value;
    } else if (side === 'right') {
        rightExpand.value = !rightExpand.value;
    }
}

const leftFilterGroup = computed(() => {
    if (!leftChar.value) {
        return leftGroup.value;
    }
    return leftGroup.value.filter(c => c.column_name.includes(leftChar.value));
});

const rightFilterGroup = computed(() => {
    if (!rightChar.value) {
        return rightGroup.value;
    }
    return rightGroup.value.filter(c => c.column_name.includes(rightChar.value));
});
const whichSide = ref('left');

function selectItem(item) {
    console.log('selectLeftItem', item);
    if (whichSide.value === 'left') {
        if (leftStatement.value.length !== 0) {
            leftStatement.value += ' ';
        }
        leftStatement.value += `tmp1.${item.column_name}`;
    } else if (whichSide.value === 'right') {
        if (rightStatement.value.length !== 0) {
            rightStatement.value += ' ';
        }
        rightStatement.value += `tmp2.${item.column_name}`;
    }
}

function changeSelection(side) {
    console.log('changeSelection', side);
    whichSide.value = side;
}

function saveMap() {
    console.log('saveMap');
    if (!leftStatement.value || leftStatement.value.length === 0) {
        return FMessage.warn('请输入左侧表达式');
    }
    if (!relationshipBetween.value) {
        return FMessage.warn('请输选择关系');
    }
    if (!rightStatement.value || rightStatement.value.length === 0) {
        return FMessage.warn('请输入右侧表达式');
    }
    showModal.value = false;
    emit('saved', {
        left_statement: leftStatement.value,
        relationshipBetween: relationshipBetween.value,
        right_statement: rightStatement.value,
        left: leftStatement.value.split(' ').map(v => v.replace('tmp1.', '')).filter(str => str.trim()).map(name => ({
            column_name: `tmp1.${name}`,
            column_type: leftGroup.value.find(v => v.column_name === name)?.data_type,
        })),
        right: rightStatement.value.split(' ').map(v => v.replace('tmp2.', '')).filter(str => str.trim()).map(name => ({
            column_name: `tmp2.${name}`,
            column_type: rightGroup.value.find(v => v.column_name === name)?.data_type,
        })),
    });
    leftChar.value = '';
    rightChar.value = '';
}

function cancel() {
    console.log('cancel', showModal.value);
    showModal.value = false;
    leftChar.value = '';
    rightChar.value = '';
    emit('cancel');
}
</script>

<style lang="less" scoped>
.body {
    .expression {
        display: flex;
        margin-bottom: 22px;
        .left, .right {
            color: #0F1222;
            width: 325px;
            :deep(textarea) {
                height: 120px;
            }
            .title {
                margin-bottom: 8px;
            }
        }
        .relation {
            width: 82px;
            margin: 0 10px;
            align-self: center;
            margin-top: 22px;
        }
    }
    .tables {
        .desc {
            margin-bottom: 8px;
        }
        .table {
            border: 1px solid #CFD0D3;
            border-radius: 4px;
            color: #646670;
            padding: 9px;
            margin-bottom: 16px;
            &.small {
                padding-bottom: 1px;
            }
            .info {
                display: flex;
                align-items: center;
                justify-content: space-between;
            }
            .expand {
                .input {
                    padding: 4px 0 8px;
                    input {
                        padding: 5px 0;
                        width: 100%;
                        outline: 0;
                        border: none;
                        border-bottom: 1px solid #F1F1F2;
                    }
                }
                .group {
                    display: flex;
                    flex-wrap: wrap;
                    .item {
                        padding: 3px 8px;
                        background: #F7F7F8;
                        border: 1px solid #CFD0D3;
                        border-radius: 2px;
                        font-size: 12px;
                        color: #000000;
                        margin-right: 8px;
                        margin-bottom: 8px;
                        cursor: pointer;
                    }
                }
            }
        }
    }
}
</style>
