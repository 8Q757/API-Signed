# API-Signed

## [中文](https://github.com/veneris/API-Signed/blob/dev/README.md) | English

![issues](https://img.shields.io/github/issues/veneris/API-Signed)
![stars](https://img.shields.io/github/stars/veneris/API-Signed)
![forks](https://img.shields.io/github/forks/veneris/API-Signed)
![license](	https://img.shields.io/github/license/veneris/API-Signed)

A library that easily implement API signature verification.

This Repository contains the following:
1. Source code for signature verification
2. Web example based on Spring Boot

## Background

Due to the need to open the API for third parties to call, and use signature verification to ensure security, this project was created.  
The project uses an AOP to verify signatures, and the API itself only needs to care about the processing of business logic.  
At the same time, it prevents replay attacks and also supports the customization of encryption rules and parameter fields.

## Start

### 1. Add [maven](https://maven.apache.org/) dependency

```xml
<dependency>
    <groupId>cn.oever</groupId>
    <artifactId>api-signed</artifactId>
    <version>0.0.1</version>
</dependency>
```
### 2. Add configuration

YAML format:
```yml
oever:
  signature:
    time-diff-max: 300
    algorithm: HmacSHA1
redis:
  host: 127.0.0.1
  port: 6379
```
If you use the properties format configuration file, it should look like this:
```
oever.signature.time-diff-max=300
oever.signature.algorithm=HmacSHA1
redis.host=127.0.0.1
redis.port=6379
```

Parameter|Description
---|---
`time-diff-max` | The maximum allowable difference between the caller and server timestamps
`algorithm` |The standard name of the MAC algorithm, you can refer to appendix A in the [Java Cryptography Architecture Reference Guide](https://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#AppA)

In addition, this project uses Redis as a cache implementation.

### 3. Add scan annotations
```java
@SpringBootApplication
@SignedScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
Add the `@SignedScan` annotation to the startup class to introduce related implementations.  
**So far, all the configurations have been completed, and you can try to implement an API for signature verification.**

### 4. Signature verification API
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
As above, you only need to use the `@SignedMapping` annotation on the API, and the request parameter is the `SignedParam` class, then the signature verification can be achieved.

**`@SignedMapping` annotation can be applied to the class or methods.**

Of course, for some requirements, the parameters in the default `SignedParam` class may not be satisfied, and you may even need to implement a set of encryption rules yourself, see [Custom](#Custom).

## Usage

Name|Value
---|---
Key|APP_ID_TEST
appSecret|APP_SECRET_TEST

### 1. Request description

#### 1.1 Request method
`POST`

#### 1.2 Request parameter
Name|Type|Description
---|---|---
data| String|The value is related to the specific request API, and the format is a string of JSON body
appId|String|ID assigned to the caller
timestamp|Long|10 digits timestamp, if the time difference between the caller and the server is too large, the request will be rejected
nonce|Integer|Random integer, used in conjunction with timestamp to prevent replay attacks
signature|String|The signature used to verify the legitimacy of this request

### 2. Signature method

#### 2.1. Parameter ordering
The request parameters are sorted in **ascending order** according to the ASCII order of the parameter name (the first letter is lowercase), and the parameters involved in the sorting include all request parameters except signature.  
When the data of the business request parameter is a JSON object, it needs to be converted into a string to participate in sorting and signature calculation.
For example, the request parameter is：
```json
{
  "userId": "test"
}
```
Then, the complete list of request parameters:
Key|Value|Description
---|---|---
data|`"{\"userId\":\"test\"}"`|Request parameter
appId|APP_ID_TEST|Test ID
nonce|-2028703096|Random integer
timestamp|1597415679|Timestamp when the request was initiated
signature|**To be calculated**|Signature value

The sorted result is:
```json
{
  "appId": "APP_ID_TEST",
  "data": "{\"userId\":\"test\"}",
  "nonce": -2028703096,
  "timestamp": 1597415679
}
```

#### 2.2. Parameter splicing 
Format the sorted request parameters according to the form of ``key=value``, and then splice each parameter together with an ``&`` in order to obtain the string plainText to be signed
```
appId=APP_ID_TEST&data={"userId":"test"}&nonce=-2028703096&timestamp=1597415679
```

#### 2.3. Generate signature 
Take the HMAC-SHA1 algorithm as an example to encrypt plainText, and then use Base64 to encode the encrypted byte stream, and get the final signature signature
```php
signature=base64_encode(hash_hmac('sha1', $plainText, $appSecret, true));
```

#### 2.4. cURL example 
```bash
curl -v -X POST "127.0.0.1:8080/example/base" -H "Accept: application/json"  -H "Content-Type: application/json; charset=utf-8" -d '{"data":"{\"userId\":\"test\"}","signature":"tFACzWpdduGputwIzxffmkJwij8=","appId":"APP_ID_TEST","nonce":-2028703096,"timestamp":1597415679}'
```

## Custom
### 1. Custom request parameters

Take the request parameter class that we implement by default as an example：
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

Annotation|Description
---|---
`@SignedEntity`|The class is marked as a request parameter class that requires signature calculation
`@SignedAppId`|The field is marked as the caller’s appId, we will use the value of this field to get the corresponding appSecret for signature calculation
`@SignedTimestamp`|The field is marked as the timestamp of the request to check the time difference between the caller and the server
`@SignedNonce`|The field is marked as a random number, used in conjunction with the timestamp to prevent replay attacks
`@SignedIgnore`|It is marked that the field does not participate in the calculation of the signature
`@Signature`|It is marked that this field is the signature calculated by the caller. This field will not participate in the calculation of the signature, but compared with this field after the signature is calculated by other fields


**Among them, the `data` field without any annotations is divided into JSON formatted strings and processed by business logic. This segment is still involved in the signature calculation.**

### 2. Custom validation rules

#### 2.1. Implement
**Extends the `BaseSignedService` and override the method that needs to be modified**

Method|Parameters|Parameter description|Return|Method description
---|---|---|---|---|
`getAppSecret`|`String appId`|appId|`String appSecret`|Get the corresponding appSecret through appId, the default implementation uses Redis, if you use other caches, you can override this method
`isTimeDiffLarge`|`long timestamp`|Timestamp|`void`|Determine whether the time difference between the caller and the server exceeds the maximum value set, if it exceeds the maximum value, an exception will be thrown
`isReplayAttack`|`String appId, long timestamp, int nonce, String signature`|appId, Timestamp, random number, signature|`void`|With `appId + timestamp + random number` as the key and `signature` as the value, each request is judged and cached. If it exists, an exception will be thrown
`getSignature`|`String appId, Map map`|appId,Parameters involved in calculating the signature|`String signature`|Calculate the signature through the appId and the parameter list that needs to be calculated. If you need to customize the encryption rules, you can override this method
`entry`|`Object obj`|The entity class of the request parameter|`void`|Entry method, which sequentially calls the non-empty judgment of the parameters, the time difference, whether to replay the attack, and the calculation of the signature. If you need to add or modify additional verification steps, you can override this method. If you only need one or a few of the above steps, it is better to override the unnecessary method and leave it blank
#### 2.2. Use
**Then, where you use the annotation of `@SignedMapping`, add a custom class as a parameter**, such as:

```diff
@RequestMapping("test")
-@SignedMapping
+@SignedMapping(CustomizeSignedService.class)
public String test(@RequestBody SignedParam signedParam) {
    
    return "SUCCESS";
}
```

At this time, the signature verification will be taken over by the overridden method in the custom class.  
In the example, you can see three examples of default implementation, custom parameters, and custom implementation.  
And each example provides two APIs for signature generation and signature verification, so you can try it yourself.

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