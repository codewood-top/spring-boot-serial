# Hello, spring-boot-serial.

该项目主要记录`spring-boot`相关应用代码，基于版本号`2.4.4`

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.4</version>
</parent>
```

## 启动

项目启动类大多在对应项目`bootstrap`包下

## 打包运行

### 加入配置

在对面项目的`pom.xml`加入下面配置，执行`mvn clean package -Dmaven.test.skip`可将本项目打包成可执行文件
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```
### 打包
```shell
> mvn clean package -Dmaven.test.skip
```

### 运行
```shell
> java -jar target/xxx.jar 
> nohup java -jar target/xxx.jar > xxx.log 2>&1 &
```

### 远程调试 
> 链接远程服务器本地debug

远程服务器端运行
```shell
> java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -jar xxx.jar
```
远程启动后，则可在本地idea运行`remotedebug`