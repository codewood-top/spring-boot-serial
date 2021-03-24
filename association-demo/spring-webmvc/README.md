# Hello, spring-webmvc

> 了解`spring-webmvc`的配置运行方式，从而了解`spring-boot`做了什么

## 参考文档

[spring-webmvc](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc)

ps: 多读文档，获取第一手资料

## 开发资料

> ps: 阅读英文文档，大多知道是什么意思，但是要准确翻译出来，还得多练习练习

The DispatcherServlet delegates to special beans to process requests and render the appropriate responses. By “special beans” we mean Spring-managed Object instances that implement framework contracts. Those usually come with built-in contracts, but you can customize their properties and extend or replace them.

`DispatcherServlet`委派特定的`bean`来处理请求以及做出恰当的响应。这些特定的`bean`, 是指符合spring框架约定并被spring管理的实例化对象。这些对象通常是内置在spring框架里面，但我们可以自定义它们的属性来扩展或替换它们。

### 特定的`Bean`类型

- `HandlerMapping`: Map a request to a handler along with a list of interceptors for pre- and post-processing. The mapping is based on some criteria, the details of which vary by HandlerMapping implementation.
The two main HandlerMapping implementations are RequestMappingHandlerMapping (which supports @RequestMapping annotated methods) and SimpleUrlHandlerMapping (which maintains explicit registrations of URI path patterns to handlers).
  
- `HandlerAdapter`: 	
  Help the DispatcherServlet to invoke a handler mapped to a request, regardless of how the handler is actually invoked. For example, invoking an annotated controller requires resolving annotations. The main purpose of a HandlerAdapter is to shield the DispatcherServlet from such details.
  
- `HandlerExceptionResolver`: Strategy to resolve exceptions, possibly mapping them to handlers, to HTML error views, or other targets. See [Exceptions](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-exceptionhandlers).

- `ViewResolver`: Resolve logical String-based view names returned from a handler to an actual View with which to render to the response. See [View Resolution](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-viewresolver) and [View Technologies](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-view).

- `LocaleResolver`,`LocaleContextResolver`: Resolve the Locale a client is using and possibly their time zone, in order to be able to offer internationalized views. See [Locale](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-localeresolver).

- `ThemeResolver`: Resolve themes your web application can use -- for example, to offer personalized layouts. See [Themes](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-themeresolver).

- `MultipartResolver`: Abstraction for parsing a multi-part request (for example, browser form file upload) with the help of some multipart parsing library. See [Multipart Resolver](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-themeresolver).

- `FlashMapManager`: Store and retrieve the “input” and the “output” FlashMap that can be used to pass attributes from one request to another, usually across a redirect. See [Flash Attributes](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-flash-attributes).