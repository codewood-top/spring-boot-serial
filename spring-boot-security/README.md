# Hello, spring-boot-security.

> 本例demo结合`thymeleaf`

## 参考文档

[spring-security](https://docs.spring.io/spring-security/site/docs/5.4.5/reference/html5/)


## 开发点

### 页面端启用权限判断表达式
```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity5</artifactId>
</dependency>
```
#### 页面表达式：

- 是否有ADMINISTRATOR权限：`<th:block th:text="${#authorization.expression('hasRole(''ROLE_ADMINISTRATOR'')')}"></th:block>`

- 是否有USER,ADMINISTRATOR中的任一权限：`<th:block th:text="${#authorization.expression('hasAnyRole(''ROLE_USER'', ''ROLE_ADMINISTRATOR'')')}"></th:block>`

- 注意符号

#### controller表达式

- 在`WebSecurityConfig`中配置`@EnableGlobalMethodSecurity(prePostEnabled = true)`
- 在需要判断权限的controller或method上加入`@PreAuthorize("hasRole('ADMINISTRATOR')")`