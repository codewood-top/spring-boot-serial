# SPI

**SPI (Service Provider Interface)**，是JDK内置的一种服务提供发现机制。

## 具体逻辑

定义一个接口类`top.codewood.spi.service.HelloSPIService`
```java
package top.codewood.spi.service;

public interface HelloSPIService {

    void sayHi();

}
```

定义一个实现类`top.codewood.spi.service.impl.HelloSPIService`
```java
package top.codewood.spi.service.impl;

import top.codewood.spi.service.HelloSPIService;

public class HelloSPIServiceImpl implements HelloSPIService {

    @Override
    public void sayHi() {
        System.out.println("Hi, this is HelloSPIServiceImpl.");
    }

}
```

SPI实现核心代码
```java
public static void main(String[] args) {

    ServiceLoader<HelloSPIService> helloSPIServices = ServiceLoader.load(HelloSPIService.class);
    Iterator<HelloSPIService> iterator = helloSPIServices.iterator();
    while (iterator.hasNext()) {
        HelloSPIService helloSPIService = iterator.next();
        helloSPIService.sayHi();
    }

}
```
这时候是没有加载到`HelloSPIServiceImpl`的, 通过跟踪源码，
的源码，需要一个名为`META-INF/services/${HelloSPIService.class.fullName}`的文件。

具体跟踪过程

- 跟踪`ServiceLoader.load(HelloSPIService.class)`可知会先初始化成员变量`LazyIterator`

- 跟踪`iterator.hasNext()`可知这是会加载`META-INF/services/${HelloSPIService.class.fullName}`文件，并按行读入内容转化成`Iterator<String>`对象

- 跟踪`iterator.next()`可知这时才会真正根据上一步读入的内容`Class.forName & Class.newInstance()`加载实现类，可知文件该写的内容是实现类的类全名。


