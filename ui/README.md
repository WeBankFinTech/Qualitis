# 项目名称

	BDP-DQM数据质量服务前端

## 项目介绍
    Qualitis是一个数据质量管理系统，用于监控数据质量。它提供了一整套统一的流程来定义和检测数据集的质量并及时报告问题。

    Qualitis基于Spring Boot开发，依赖于Linkis进行数据计算，提供数据质量规则构建，数据质量规则执行，数据质量任务管理，异常数据发现保存等功能。

    同时它也提供了金融级数据质量规则资源隔离，资源管控，权限隔离等企业特性，具备高并发，高性能，高可用的大数据质量管理能力。为用户提高工作效率。
### 负责人
* PM：xxx
* 产品：xxx
* 前端：xxx
* 后台：xxx
* 测试：xxx
* 运维：xxx

### 业务简介
...

### git地址

http://git.******.com/howeye/bdp-dqm-ui

### 需求文档

http://dpms.******.com/#/product/100194/release/list

### 名词解释


### 接口文档


### 子系统
* 前端子系统
	BDP-DQMUI

### 关联域名
#### 接口域名
* ...
* ...

#### 访问域名
* http://127.0.0.1:8090/qualitis
#### cdn域名


#### 跳转域名


#### 其他相关域名
* ...
* ...

### 关联项目


## 开发调试

### 开发环境安装
* 安装fes3
* http://h.test-adm.******.com/s/fes-2.0/guide/install.html#%E5%AE%89%E8%A3%85-node

```

	npm install @webank/wnpm -g --registry=http://wnpm.******.com:8001 // 如果已经安装了wnpm则忽略

	npm install @webank/wnpm -g --registry=http://wnpm.******.com:8001

```
* 项目根目录下执行

```
	wnpm i // 安装所需依赖

```

### 模拟调试
1. 命令
	启动本地服务

	```
		npm run dev // 运行本地项目，默认会自动打开，如未打打开则手动进入http://localhost:5001/#/dashboard ，端口占用 + 1

	```

### 单元测试
....
## 部署发布
### 测试部署
#### 打包构建

  fes3 build --env=dev // 进行服务器部署时的打包命令

##### 测试自动打包部署
...

##### 手动触发
...

#### 验证地址
...

#### 测试账号
...

#### 数据构造
* ...
* ...

### 生产发布
#### 打包构建
1. ...
2. ...
3. ...

#### 注意事项
* ...
* ...
* ...
手动打包命令：在package.json文件 script脚本中 build脚本 添加 --env=dev 参数 然后 执行npm run build


#### 验证地址
 ...

## 前端埋点
### 埋点日志查询方法
...

### 埋点规则

|key |value|备注|
|:--|:--|:--|
|...| ... |...|

### 异常监控日志查询方法
1. ...
2. ...
3. ...

### 生产问题前端排查步骤
1. ...
2. ...
3. ...

## 功能模块
### 文件目录
```
tree -C -L 3 -I "node_modules"
.
页面路径根据pages为'/'根目录，根据页面层级设计url，例如我的项目为：/myProject，我的项目列表为：/myProject/list
├─dist                                  # build包，打包文件
├─node_modules                          # npm 依赖包
├─fes-jenkins.config.js                 # fes-jenkins 配置文件
├─fes.config.js                         # fes 配置文件
├─mock.js                               # 模拟接口数据
├─package-lock.json                     #
├─package.json                          # npm 依赖仓库
└─src                                   # 业务主文件夹
    ├─assets                              # 图片和样式
    ├─components                          # 公共组件
    ├─pages                               # 业务页面
    │  ├─addGroupTechniqueRule              # 新增技术规则组
    │  ├─addTechniqueRule                   # 新增技术规则模块
    │  ├─configureParameter                 #
    │  ├─crossTableCheck                    # 新增跨表规则模块
    │  ├─customTechnicalRule                # 新增自定义规则模块
    │  ├─dashboard                        # 主页
    │  ├─error                            #
    │  ├─home                             #
    │  ├─metadataManagement               # 管理页面
    │  ├─myProject                        # 我的项目页面
    │  │  └─projects                        #
    │  │      └─list                          # 项目列表
    │  ├─optionManage                     # 权限管理
    │  ├─personnelManage                  # 人员管理
    │  ├─ruleQuery                        # 规则查询
    │  ├─ruleTemplateList                 # 规则模板
    │  ├─taskDetail                       # 任务详情
    │  ├─taskQuery                        # 任务查询
    │  └─verifyFailData                   # 数据验证
    └─static                            # 静态资源

```

### 功能介绍
#### 个人中心
##### 个人信息
* [UI图片]...
* [代码文件路径]...
* [流程图]...
* [功能点]...

##### 信息查询
...

## 风险点（待优化项）
