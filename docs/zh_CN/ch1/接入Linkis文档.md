# 接入Linkis文档

## Linkis配置
### 增加Linkis Token
```
cd $LINKIS_HOME/linkis-gateway/conf
vim token.properties
```

增加以下行：
```
QUALITIS-AUTH=*
```

### 修改用户默认提交队列
1. 在DataSphere Studio中登录Qualitis同名的用户名

2. 进入Linkis管理台 -> 设置 -> yarn队列名中，并设置一个可用的yarn队列；

### 加入MySQL驱动包
```
cd $SPARK_HOME/jars
```
并加入mysql驱动包

最后，重启Linkis

## Qualitis配置
在Linkis Token中填入'QUALITIS-AUTH'  
如下图所示：  
![](../../../images/zh_CN/ch1/规则配置样例.png)

注意集群类型的写法，如果你已部署linkis 1.x以上版本，集群类型加个1.0后缀，如BDAP_CLUSTER1.0，否则可以不写后缀，或者写0.9，以帮助区分不同版本的linkis。
