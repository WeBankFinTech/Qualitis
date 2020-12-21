### BDP-DQM数据质量服务前端

项目依赖FES2

### 项目目录结构

``` tree -C -L 3 -I "node_modules"
.
├── README.md
├── dist    # fes2 build 之后资源目录
│   ├── assets
│   │   ├── iconfont.0dd8.svg
│   │   ├── iconfont.126d.eot
│   │   ├── iconfont.18b9.ttf
│   │   └── iconfont.c976.woff
│   ├── index.html
│   ├── js
│   │   ├── app.e923.js
│   │   ├── chunk.b48c.js
│   │   ├── common.e923.js
│   │   └── vendor.dll.f3b5.js
│   └── styles
│       └── common.e923.css
├── fes-jenkins.config.js #fes-jenkins 配置文件
├── fes.config.js # fes 配置文件
├── mock.js
├── package-lock.json
├── package.json
└── src
    ├── app.js
    ├── assets
    │   ├── images
    │   ├── js
    │   └── styles
    ├── components
    │   ├── fesLeft.fes
    │   └── goBack.fes
    └── pages
        ├── HelpDocument
        ├── addTechniqueRule
        ├── configureParameter
        ├── customTechnicalRule
        ├── dashboard
        ├── home
        ├── metadataManagement
        ├── myDatasheet
        ├── optionManage
        ├── personnelManage
        ├── projects
        ├── route.fes
        ├── ruleTemplateList
        ├── taskDetail
        ├── taskQuery
        └── verifyFailData
```

### 开发

```
fes2 dev
```

### 代码检查

配置了eslint，规则详见.eslintrc.json

同时添加了提交代码时pre-commit hook，请按规范修改错误信息后提交代码

```
npm run lint
npm run fix
```

### 构建
```
wnpm install -g @webank/fes-jenkins
```

```
fes2 build
fes2 build --env $env
```

### 构建发布

dev分支push代码会自动发布到开发环境 http://test-dqm.weoa.com/dqm-ui-dev/
以下本地命令需全局安装fes-jenkins，不同环境自动发布命令：

```
fj build dev
fj build sit
fj build uat
```

### 其它

编辑器推荐vscode
建议安装eslint，stylelint，editorconfig插件

eslint-plugin-vue 只能检查出来.vue 格式的，暂时不使用template格式部分规则
```
"vue/max-attributes-per-line": 0,
"vue/multiline-html-element-content-newline": 0,
"vue/singleline-html-element-content-newline"
```
