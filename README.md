# Festvial

Festvial是一个基于Vertx、Rxjava等开源库，拥有IOC、AOP功能的响应式Web框架。

# Feature：

* 基于JSR330规范实现了IOC容器，同时支持JSR250的注解。
* 通过注解标记组件类，被标记的类会被扫描并添加到容器中，解析组件类的依赖关系，进行依赖注入。
* 根据组件注解的描述信息，返回单例对象，或者返回新的对象。
* 支持使用AspectJ的注解使用AOP功能，进行AOP拦截处理。
* 支持配置文件属性注入，将配置文件之间映射到对象的属性中。
* 基于Vertx封装了Web框架，可以使用类似于SpringMVC的注解来完成url的映射以及参数的获取。
* 支持通过注解将Service封装成Verticle，使得每一个Service可以由一个EventLoop来处理，同时通过代理机制解决Service之间通信。
* 对Vertx的Auth部分做了封装，只需要引入相应的依赖，就可以支持Shiro、JWT等权限管理框架，且支持使用注解来管理权限。
* 支持使用Filter来对拦截Http请求。

# 快速使用

```
引入maven依赖
<dependency>
  <groupId>com.github.bdqfork</groupId>
  <artifactId>festival-web</artifactId>
  <version>0.4.0</version>
</dependency>

或者gradle
implementation 'com.github.bdqfork:festival-web:0.4.0'
```

```java
@Singleton
@RouteController
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "hello festival";
    }
    
    public static void main(String[] args) throws Exception {
            WebApplicationContext webApplicationContext = new WebApplicationContext("cn.bdqfork.example");
            webApplicationContext.start();
    }

}
```
访问http://localhost:8080/hello 即可看见hello festival。

# 详细功能查看[wiki](https://github.com/bdqfork/festival/wiki)

##### todolist:
+ 缓存
+ 服务监控
+ verticle service group
+ xml数据请求和响应 已完成！
+ websocket 已完成！
+ kotlin ioc容器
+ 配置文件profile
+ 模版引擎
+ 解耦aop模块 已完成！

License
-------

    Copyright (C) 2017 - present, bdqfork.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
