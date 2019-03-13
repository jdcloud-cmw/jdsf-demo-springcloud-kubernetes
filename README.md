# 环境准备

1. 要求jdk版本1.8以上；<br />
2. 本Demo为idea项目，构建工具为maven。如果您的环境也是idea+maven，那么可以直接下载使用。否则请按照自己的工具和环境来创建项目；


# 项目结构

|- client <br />
|- server <br />
|- k8s-config <br />
|- pom.xml <br />
其中client表示服务调用者; <br />
server表示服务提供者；<br />
k8s-config放置了deploy和server的配置文件;<br>
pom.xml中定义了项目需要的依赖包<br />


# POM说明
### 1.parent项
parent配置表示我们配置哪个项目作为本项目的父项目，配置好之后就能从父项目继承一些配置信息了。<br />
本项目继承于springcloud2.0，方便基于springcloud2.0的各种扩展管理和开发。
### 2.dependencies项
引入项目开发中需要的具体依赖，根据需要来增加、删除依赖项目。<br />
本项目中主要引入了服务注册/发现依赖、监控（探活）依赖、服务调用依赖、负载均衡依赖、配置中心依赖、调用链依赖——jaeger和zipkin，用户根据自己项目的需要来修改。
### 3.dependencyManagement项
使用dependencyManagement可以统一管理项目的版本号，确保应用的各个项目的依赖和版本一致，不用每个模块项目都弄一个版本号，不利于管理，当需要变更版本号的时候只需要在父类容器里更新，不需要任何一个子项目的修改；如果某个子项目需要另外一个特殊的版本号时，只需要在自己的模块dependencies中声明一个版本号即可。子类就会使用子类声明的版本号，不继承于父类版本号。


# 配置及使用说明
## 1.在京东云开通分布式服务框架产品，并创建调用链分析服务
请参考[帮助文档](https://docs.jdcloud.com/cn/jd-distributed-service-framework/create-analysis-service)创建集群，然后在[调用链详情](https://docs.jdcloud.com/cn/jd-distributed-service-framework/analysis-detail)页面得到服务HTTP协议的请求地址。


## 2.生成docker镜像并上传镜像仓库
本demo已经上传至dockerhub仓库，如果直接使用dockerhub仓库的镜像，请直接跳至第3步。

请先行在编译机器装好docker服务

在根目录执行

`$ mvn package`

然后在client和server目录分别执行

`$ mvn package docker:build -DpushImageTags -DdockerImageTags=0.1.0`

正确的话应该可以查看到新打出的镜像

`$ docker images`

可以先别地执行看下是否错误，如果有错误相应处理，注意：启动时需要指定CHAIN_HOST环境变量，值为创建的调用链分析服务HTTP协议的请求地址

服务提供端

`$ docker run -p 9999:9999 -t jdsf/server -e CHAIN_HOST=collector-tc-1tn3d6lpkbvgg-nlb.jvessel-open-hb.jdcloud.com:14268`

服务调用端

`$ docker run -p 8888:8888 -t jdsf/client -e CHAIN_HOST=collector-tc-1tn3d6lpkbvgg-nlb.jvessel-open-hb.jdcloud.com:14268`

如果您选择上传到dockerhub，请参考[dockerhub帮助文档](https://docs.docker.com/docker-hub/)
如果您选择上传至京东云镜像仓库，请参考[京东云镜像仓库帮助文档](https://docs.jdcloud.com/cn/container-registry/create-image)

## 3.使用docker镜像启动服务
代码包的k8s-config目录放了部署和服务的定义文件，修改deploy-chain-server.yml和deploy-chain-client.yml中的CHAIN_HOST值为实际的调用链分析服务HTTP协议的请求地址

然后执行

`$ kubectl apply -f deploy-chain-server.yml `

`$ kubectl apply -f chain-server.yml `

`$ kubectl apply -f deploy-chain-client.yml `

`$ kubectl apply -f chain-client.yml `

然后查看service的端口信息，

`$ kubectl get service`

之后就可以发起调用了

`$curl {nodeip}:{jdsf-chain-client port}/client/nn`

正常应该返回类似

`nn<------server------>1591172220`

执行几次后，就可以在调用链分析页面查看[依赖图谱](https://docs.jdcloud.com/cn/jd-distributed-service-framework/analysis-service-dependline)和详细调用链信息
