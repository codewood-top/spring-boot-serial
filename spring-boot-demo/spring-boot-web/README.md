# spring-boot-web

## default

`pom.xml` 
```shell
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

只引入`spring-boot-start-web`, 如没有根路径的`@RequestMapping`，则默认`resources/static/index.html`为首页

## WebMvcAutoConfiguration
> 大多`SpringMvc`相关的配置都可在这个类找到

### ViewResolver
默认的是`InternalResourceViewResolver`