# 获取编译结果

## 下载release包
点击 [here](https://github.com/huaweicloud/Sermant/releases) 下载release包

## 源码编译
- 编译机器需具备git，java jdk，maven环境
- 执行`git clone -b develop https://github.com/huaweicloud/Sermant.git` 克隆最新源码
- 执行`cd Sermant`进入源码目录
- 执行`mvn clean package -Dmaven.test.skip -Pexample` 编译示例项目

# 启动
- 运行环境需要具备zookeeper,kafka环境，zookeeper, kafka正常运行
- 进入可执行文件目录
  ```bash
  # 通过下载release包获取编译结果可以执行以下命令进入可执行文件目录
  cd sermant-agent
  
  # 通过源码编译获取编译结果的可以执行一下命令进入可执行文件目录
  cd sermant-agent-1.0.0
  ```
- 执行以下命令启动backend
  ```bash
  # windows
  java -jar server\sermant\sermant-backend-x.x.x.jar
  
  # mac, linux
  java -jar server/sermant/sermant-backend-x.x.x.jar
  ```
- 