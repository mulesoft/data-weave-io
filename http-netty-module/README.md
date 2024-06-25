# HTTP Netty module
A `HTTP module` implementation based on [Netty](https://github.com/netty/netty).

The implementation used [AsyncHttpClient](https://github.com/AsyncHttpClient/async-http-client).

## Installation
To enables the `DataWeave HTTP Netty` module in your project, you need to add the following dependency to the project:

### Maven:
```xml
<dependencies>
    <dependency>
        <groupId>org.mule.weave</groupId>
        <artifactId>http-netty-module</artifactId>
        <version>${data.weave.http.netty.module}</version>
        <version>2.7.0</version>
    </dependency>
</dependencies>
```

### Gradle:
```groovy
dependencies {
    implementation 'org.mule.weave:http-netty-module:2.7.0'
}
```

## Turn on wire logging
To set up the wire logging on your `DataWeave HTTP Netty` module follow these steps:

1. Add an `SL4J` implementation in your project.
2. Configure your `SL4J` implementation.
3. Turn on the following `logger`:

```xml
<!-- Enable 'DEBUG' logs for async-http-client -->
<logger name="org.asynchttpclient" level="DEBUG" />
<!-- Enable Netty wire logs -->
<logger name="org.asynchttpclient.netty.channel.ChannelManager" level="TRACE" />
<logger name="io.netty.handler.logging.LoggingHandler" level="TRACE" />
```

### Example
Take a look at the following example for details:
- [logback dependency](./src/build.gradle)
- [logback-test.xml](./src/test/resources/logback-test.xml)