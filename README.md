# springcloud-twocache
springcloud-twocache本地加redis高效双级缓存

## 使用教程
打包安装项目
```
git clone https://github.com/dounine/spring-cloud.git
cd spring-cloud
gradle install -xtest
```
在项目中引用
build.gradle
```
dependencies {
    compile('com.dounine.twocache:springcloud-twocache:0.0.1-SNAPSHOT')
}
```
在`application.yml`添加如下代码
```
spring:
  redis:
    host: localhost
    port: 6379
twocache:
  enable: true
  redis:
    topic: 项目名
```
java代码中使用(与spring cache使用缓存一样)
```
@Cacheable(cacheNames = "user",key = "#userId")
public String queryUser(@PathVariable String userId) {
...
}
```
