# API-Signed

## 中文 | [English](https://github.com/veneris/API-Signed/blob/dev/README_EN.md)

![issues](https://img.shields.io/github/issues/veneris/API-Signed)
![stars](https://img.shields.io/github/stars/veneris/API-Signed)
![forks](https://img.shields.io/github/forks/veneris/API-Signed)
![license](	https://img.shields.io/github/license/veneris/API-Signed)

一个轻松实现API签名校验的项目。  

本仓库包含以下内容：
1. 签名校验的源码  
2. 基于Spring boot的web示例

## Background

由于要开放接口供第三方调用, 采用签名校验的方式以保证安全, 于是有了这个项目。  
该项目使用面向切面的方式对签名进行校验, 接口本身只需要关心业务逻辑的处理。  
同时防止了重放攻击, 也支持对加密规则, 参数字段的自定义。  

## Start

### 1. 添加 [maven](https://maven.apache.org/) 依赖

```xml
<dependency>
    <groupId>cn.oever</groupId>
    <artifactId>api-signed</artifactId>
    <version>0.0.1</version>
</dependency>
```
### 2. 添加配置信息

YAML格式：
```yml
oever:
  signature:
    time-diff-max: 300
    algorithm: HmacSHA1
redis:
  host: 127.0.0.1
  port: 6379
```
如果使用properties格式配置文件, 那它应该看起来是这样：
```
oever.signature.time-diff-max=300
oever.signature.algorithm=HmacSHA1
redis.host=127.0.0.1
redis.port=6379
```

参数|说明
---|---
`time-diff-max` | 调用方与服务器时间戳允许的最大差值
`algorithm` | MAC算法的标准名称, 可以参阅 [Java Cryptography Architecture Reference Guide](https://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#AppA) 中的附录A

另外, 本项目使用Redis作为缓存的实现。

### 3. 添加扫描注解
```java
@SpringBootApplication
@SignedScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
将`@SignedScan`注解添加到启动类, 以引入相关实现。  
**到此为止, 已经完成了全部的配置, 可以尝试实现一个进行签名校验的API了。**

### 4. 签名校验接口
```java
@RestController
@RequestMapping("api")
@SignedMapping
public class TestController {

    @RequestMapping("test")
    public String test(@RequestBody SignedParam signedParam) {
        
        // the request data is signedParam.getData() in JSON
        // then do something in service
        return "SUCCESS";
    }
}

```
如上, 只需要在接口上使用`@SignedMapping`注解, 且请求参数为`SignedParam`类, 即可实现对签名的校验。  
**`@SignedMapping`注解既可以作用于类, 也可以作用于单独的方法。**  
当然, 对于部分需求, 或许默认的`SignedParam`类中的参数并不能满足, 甚至有可能需要自己实现一套加密规则, 可以参见[自定义](#Custom)。

## Usage

名称|取值
---|---
appId|APP_ID_TEST
appSecret|APP_SECRET_TEST

### 1. 请求说明

#### 1.1 请求方式
`POST`

#### 1.2 请求参数
名称|类型|说明
---|---|---
data| String|取值与具体请求接口相关, 格式为JSON体的字符串形式
appId|String|分配给调用方的ID
timestamp|Long|10位时间戳, 若调用方与服务端时间相差过大, 将拒绝本次请求
nonce|Integer|随机整数, 与timestamp联合使用以防止重放攻击
signature|String|用于验证此次请求合法性的签名

### 2. 签名方法

#### 2.1. 参数排序
将请求参数依据参数名称（首字母小写）的ASCII序进行**升序排列**, 参与排序的参数包括除signature以外的所有请求参数。  
业务请求参数的数据为JSON对象时, 需要将其转化为字符串再参与排序和签名计算。  
例如, 请求参数为：
```json
{
  "userId": "test"
}
```
则, 完整的请求参数列表：
名称|取值|说明
---|---|---
data|`"{\"userId\":\"test\"}"`|请求参数
appId|APP_ID_TEST|测试ID
nonce|-2028703096|随机整数
timestamp|1597415679|发起请求时的时间戳
signature|**待计算**|签名值

则排序后的结果为：
```json
{
  "appId": "APP_ID_TEST",
  "data": "{\"userId\":\"test\"}",
  "nonce": -2028703096,
  "timestamp": 1597415679
}
```

#### 2.2. 参数拼接
将排序后的请求参数依照`参数名=参数值`的形式格式化, 然后将各个参数依序用`&`符号拼接在一起, 得到待签名字符串plainText
```
appId=APP_ID_TEST&data={"userId":"test"}&nonce=-2028703096&timestamp=1597415679
```

#### 2.3. 生成签名
以HMAC-SHA1算法为例对plainText进行加密, 再使用Base64对加密后的字节流进行编码, 即得到了最终签名signature  
```php
signature=base64_encode(hash_hmac('sha1', $plainText, $appSecret, true));
```

#### 2.4. cURL示例
```bash
curl -v -X POST "127.0.0.1:8080/example/base" -H "Accept: application/json"  -H "Content-Type: application/json; charset=utf-8" -d '{"data":"{\"userId\":\"test\"}","signature":"tFACzWpdduGputwIzxffmkJwij8=","appId":"APP_ID_TEST","nonce":-2028703096,"timestamp":1597415679}'
```

## Custom
### 1. 自定义请求参数

以我们默认实现的请求参数类为例：
```java
@SignedEntity
public class SignedParam {
    @SignedAppId
    private String appId;
    private String data;
    @SignedTimestamp
    private long timestamp;
    @SignedNonce
    private int nonce;
    @Signature
    private String signature;
    
    // getter and setter...
}
```

注解|说明
---|---
`@SignedEntity`|标注了该类为一个需要进行签名计算的请求参数类
`@SignedAppId`|标注了该字段为调用方的AppId, 我们将使用此字段的值获取对应的AppSecret进行签名的计算
`@SignedTimestamp`|标注了该字段为请求的时间戳, 以检查调用方与服务器的时间差
`@SignedNonce`|标注了该字段为一个随机数, 和时间戳联合使用以防止重放攻击
`@SignedIgnore`|标注了该字段不参与签名的计算
`@Signature`|标注了该字段为调用方计算出来的签名, 该字段并不会参与签名的计算, 而是其他字段计算得出签名后与该字段进行比较


**其中, 未使用任何注解的data字段为JSON格式的字符串, 用于业务逻辑处理, 该字段仍参与签名计算。**

### 2. 自定义校验规则

#### 2.1. 实现
**继承`BaseSignedService`并重写需要修改的方法**

方法|参数列表|参数说明|返回值|方法说明
---|---|---|---|---|
`getAppSecret`|`String appId`|appId|`String appSecret`|通过appId获取对应的appSecret, 默认实现使用Redis, 如果使用其他缓存, 可以重写此方法
`isTimeDiffLarge`|`long timestamp`|时间戳|`void`|判断调用方与服务器的时间差值是否超过设定的最大值, 若超出, 则抛出异常
`isReplayAttack`|`String appId, long timestamp, int nonce, String signature`|appId, 时间戳, 随机数, 签名|`void`|通过`appId + 时间戳 + 随机数`作为键, `签名`作为值, 每次请求进行判重并缓存, 如存在, 则抛出异常
`getSignature`|`String appId, Map map`|appId, 参与计算签名的参数|`String signature`|通过appId和需要计算签名的参数列表, 计算签名, 如果需要自定义加密规则, 可以重写该方法
`entry`|`Object obj`|请求参数的实体类|`void`|入口方法, 该方法依次调用了参数的非空判断, 时间差, 是否重放攻击, 与签名的计算。如果需要增加或修改额外的验证步骤, 可以重写此方法。如果只需要以上步骤中的一步或几步, 更好的做法是重写不需要的方法并留空

#### 2.2. 使用
**然后, 在使用`@SignedMapping`注解的地方, 添加自定义的类作为参数**, 如：

```diff
@RequestMapping("test")
-@SignedMapping
+@SignedMapping(CustomizeSignedService.class)
public String test(@RequestBody SignedParam signedParam) {
    
    return "SUCCESS";
}
```

此时, 签名校验将被自定义类中重写的方法所接管。  
在example中, 可以看到默认实现, 自定义参数, 自定义实现三种例子。  
并且每一种例子都提供了签名生成与签名校验两个接口, 可以自行尝试。

## License

```plainText
MIT License

Copyright (c) 2020 刘思宁

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.