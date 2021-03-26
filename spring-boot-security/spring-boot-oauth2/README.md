# Hello, spring-boot-oauth2.

> ps: spring-security-oauth2 后面的版本可能有较大的改动，
> [参考文档](https://docs.spring.io/spring-security/site/docs/5.4.5/reference/html5/#spring-security-oauth2-core)

## 概述

> 本个案例是`AuthorizationServer`和`ResourceServer`集中在一起。
> 如有需要分开配置，可以把`top.codewood.config.security.SecurityConfig`中的`AuthorizationServerConfig`、`ResourceSecurityConfig`分别配置在授权认证服务端，资源服务端。
> 需要注意的是，`JwtTokenStore`两端都需要以同样的`signingKey`或`keyPair`加载到spring容器


## 开发要点

启用`password`登录模式需要提供`AuthenticationManager`

```java
@Bean
public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setHideUserNotFoundExceptions(false);
    authenticationProvider.setUserDetailsService(userDetailsService());
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    ProviderManager providerManager = new ProviderManager(Arrays.asList(authenticationProvider));
    return providerManager;
}
```


启用`refresh_token`需要在`authorizedGrantTypes`中加入`refresh_token`, 这样返回的jwt才会带有`refresh_token`
```java

@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("xxx")
                .secret("xxx")
                .scopes("xxx")
                .authorizedGrantTypes("authorization_code", "password", "refresh_token")
                .accessTokenValiditySeconds(xxx)
                .refreshTokenValiditySeconds(xxx);
    }

}
```  

## 异常捕捉

### 授权认证服务端

`AuthorizationServer`下的授权异常, 与其说捕捉授权异常，不如说把登录异常转为我们想要输出的格式。

未转换前异常：
```json
{
    "error": "invalid_token",
    "error_description": "Cannot convert access token to JSON"
}
```

定义`AppOAuth2Exception.class`(需要加入自定义json解析器)
```java
@JsonSerialize(using = AppOAuth2ExceptionSerializer.class)
public class AppOAuth2Exception extends OAuth2Exception {

    public AppOAuth2Exception(String msg) {
        super(msg);
    }
}
```
`AppOAuth2ExceptionSerializer.class`
```java
public class AppOAuth2ExceptionSerializer extends StdSerializer<AppOAuth2Exception> {

    public AppOAuth2ExceptionSerializer() {
        super(AppOAuth2Exception.class);
    }

    @Override
    public void serialize(AppOAuth2Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("code", value.getHttpErrorCode());
        gen.writeStringField("message", value.getMessage());
        gen.writeEndObject();
    }
}
```
在`AuthorizationServiceConfig`下的配置
```java
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.exceptionTranslator(new WebResponseExceptionTranslator<OAuth2Exception>() {
            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                LOGGER.error("endpoint err: {}", e.getMessage());
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(new AppOAuth2Exception(e.getMessage()));
            }
        });
    }
}
```
转换后的异常：
```json
{
    "code": 400,
    "message": "Cannot convert access token to JSON"
}
```

### 资源提供端
```java
@EnableResourceServer
public class ResourceSecurityConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                LOGGER.error("access denied, err: {}", accessDeniedException.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
            }
        });
        resources.authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                Map<String, Object> map = new HashMap<>();
                map.put("code", HttpServletResponse.SC_FORBIDDEN);
                if (authException instanceof InsufficientAuthenticationException) {
                    if (authException.getCause() instanceof InvalidTokenException) {
                        if (authException.getMessage().startsWith("Access token expired")) {
                            LOGGER.error("token expired.");
                            map.put("message", "token已失效");
                        } else {
                            LOGGER.error("invalid token, err: {}", authException.getMessage());
                            map.put("message", "无效token");
                        }
                    } else {
                        LOGGER.error("authentication entry point err: {}", authException.getMessage());
                        map.put("message", "登录后访问");
                    }
                }
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(objectMapper.writeValueAsString(map));
                response.flushBuffer();
            }
        });
    }
}
```