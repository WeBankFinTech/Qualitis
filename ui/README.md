# Qualities Web

项目使用基于`vue3`的框架[FesJs](https://winixt.gitee.io/fesjs/zh/)以及[FesUI](https://fes-design-4gvn317r3b6bfe17-1254145788.ap-shanghai.app.tcloudbase.com/)，搭配`hooks`思想组合功能

`注：后台接口需要在开发网中访问`

## 本地开发：
```
npm i
npm run dev
```

## 跨域联调

使用`whistle`代理

在whistle规则中配置如下规则并启用，在浏览器访问`http://backendip:8090/web/`既可以将html代理到本地开发环境，localhost的端口需要根据实际情况替换
```
http://backendip:8090/web/		http://localhost:8000/
```

或者跨域启动浏览器（chrome 84版本及以下版本推荐）
- windows

    旧版-在桌面新增一个chrome的快捷方式并重命名，右键属性在`快捷方式-目标`输入框中最后加入参数`--disable-web-security --user-data-dir`，关闭chrome直接双击新建的快捷方式就可以跨域打开浏览器

    新版-在桌面新增一个chrome的快捷方式并重命名，右键属性在`快捷方式-目标`输入框中最后加入参数`--args --disable-web-security --user-data-dir=C:\MyChromeDevUserData`，MyChromeDevUserData目录可以自定义但一定是必须的，关闭chrome直接双击新建的快捷方式就可以跨域打开浏览器

- mac

    新建目录chormedata，并在终端执行命令`open -n /Applications/Google\ Chrome.app/ --args --disable-web-security  --user-data-dir=/Users/zhuguifeng/Documents/chromedata`即可跨域打开浏览器，其中新建的目录绝对路径放在--user-data-dir的参数中

注意，以上参数中的`--user-data-dir`必须根据实际情况修改

## 代码提交规范

每次提交代码都会经过`eslint`的校验，开发者需要处理报告中出现的错误

针对提交message也需要按照规范填写，方便后面回溯记录，规范如下：

`['upd', 'feat', 'fix', 'refactor', 'docs', 'chore', 'style', 'revert']: 提交信息`

## @fesjs/fes-design的使用

- 需要全局注入组件需要在`app.js`文件中引入并且`use`
- API使用的组件直接在对应页面按需引入即可，如：`import { FMessage } from '@fesjs/fes-design';`

## 项目结构

## svg filter generator

项目中使用到了一些svg图片，需要实现hover变色的效果，svg filter的生成器地址：[https://codepen.io/sosuke/pen/Pjoqqp](https://codepen.io/sosuke/pen/Pjoqqp)

## 规则设计

|集合种类|文件入口|包含种类|
|:---|:---|:---|
|普通规则|src/components/rules/commonList|单表校验、多表比对、表文件校验、库一致性比对、自定义SQL校验|
|规则组|src/components/rules/groupList|单表校验、表文件校验|


|规则类型|目录|表单项|数据项|
|:---|:---|:---|:---|
|单表校验|src/components/rules/singleTableCheck|![单表校验](/docs/dbjy.png)|![单表校验数据项](/docs/dbjysjx.png)|
|多表比对|src/components/rules/crossTableCheck|![多表比对](/docs/dbbd.png)|![多表比对数据项](/docs/dbbdsjx.png)|
|自定义SQL校验|src/components/rules/customCheck|![自定义SQL校验](/docs/zdysqljy.png)|![自定义SQL校验数据项](/docs/zdysqljysjx.png)|
|表文件校验|src/components/rules/fileCheck|![表文件校验](/docs/bwjjy.png)|![表文件校验数据项](/docs/bwjjysjx.png)|
|库一致性比对|src/components/rules/crossDbCheck|![库一致性对比](/docs/kyzxdb.png)|![库一致性对比数据项](/docs/kyzxdbsjx.png)|

## 表格数据为空的缺省页说明

### 1. 针对没有查询条件的表格，直接使用如下缺省页
```html
<template #empty>
    <div class="empty-block">
        <div class="empty-data"></div>
        <div class="table-empty-tips">这里还没有数据. . .</div>
    </div>
</template>
```
### 2. 有查询条件的表格，使用如下缺省页
```html
<template #empty>
    <div class="empty-block">
        <div v-if="resultByInit">
            <div class="empty-data"></div>
            <div class="table-empty-tips">这里还没有数据. . .</div>
        </div>
        <div v-else>
            <div class="empty-query-result"></div>
            <div class="table-empty-tips">没有符合条件的结果. . .</div>
        </div>
    </div>
</template>
```
`resultByInit`表示表格数据是否是初始加载的，如果是，为空的时候展示没有数据缺省图；否则展示没有符合条件的结果缺省图。
因此，后续新的带查询的表格建议将发请求操作和查询操作分开，如下处理
```js
// 发请求的函数
const resultByInit = ref(true)  // 初始表格数据是初始加载的
const loadData = ()=>{xxxxx}
// search函数
const search = async()=>{
    xxxxx; // 查询参数相关操作
    await loadData()
    resultByInit.value = false  // 这样表示此时表格数据是搜索得到的
}
const reset = async()=>{
    xxxxx; // 重置相关操作
    await loadData()
    resultByInit.value = true  // 这样表示此时表格数据是初始化状态
}
onMounted(()=>{
    loadData() // 挂载页面时的初始化表格数据
})
```
但是历史页面中有些查询和加载时没有分开的，没有用loadData嫁接一层，这时候是如下处理的：

```js
// ！！！注意下面的代码只是为了说明老的页面中为啥么如此处理，新的表格请不要这样写，这样会将获取表格数据操作和查询操作耦合在一起，不利于后续针对查询、重置等功能进行特殊升级处理
const resultByInit = ref(true)  
const search = ()=>{
    xxxxxx; // search参数相关操作
    xxxxxx; // 发请求相关操作
    resultByInit.value = false  // 这样表示此时表格数据是搜索得到的
}
const reset = async()=>{
    xxxxxx;
    await search()
    resultByInit.value = true  // 将search中设置为false的resultByInit设置回来,这样表示此时表格数据是初始化状态
}
onMounted(async()=>{
    await loadData() // 挂载页面时的初始话表格数据
    resultByInit.value = true  // 将search中设置为false的resultByInit设置回来,这样表示此时表格数据是初始化状态
})
```


