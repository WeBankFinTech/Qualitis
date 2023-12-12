# Development specifications

When Contributors contribute code to Qualitis, they must follow the following specifications for development.

## 1. Interface specification

#### 1.URL specification
Internal interface:
```
/api/v1/{role}/{module}/.+
```

External interface:
```
/outer/api/v1/{module}/.+
```

Agreement:
v1 is the version number of the service.
{role} is the role name. Currently, Qualitis has two roles: admin and projector.
{module} is the module name.

#### 2. Return value specification
```
{
     "code": "200",
     "message": "",
     "data": {
        
     }
}
```

Agreement:
code: Return status information.
message: Returns the requested prompt information.
data: Return specific data.

|code status code|meaning|remarks|
|:----|:---|:----- |
|200 |Successful request |When the request is successful, return 200 status code |
|500 |Server error, request failed |Server internal error, such as failure to save the database, etc., return 500 status code |
|400 |Request parameter error |The request parameter does not meet the specification, and a 400 status code is returned |
|401 |Authentication failed |User authentication failed, either not logged in or with wrong signature |
|403 |Insufficient permissions |User authentication passed, but insufficient permissions |

## 2. Log specifications
[Convention] The Qualitis project selects slf4j and Log4j2 as the log printing framework, and removes the Logback logging framework that comes with the Spring Boot package. Since Slf4j will randomly select a log framework for binding, when introducing new dependencies, you need to exclude bridging packages such as slf4j-log4j, otherwise problems will occur in log printing. If the newly introduced dependency includes packages such as Log4j, do not exclude it, otherwise an error may be reported when running the code.

[Mandatory] The API in the log system (log4j2, Log4j, Logback) cannot be used directly in the class. LoggerFactory.getLogger(getClass) should be used.

[Mandatory] Strictly distinguish log levels. Fatal level logs should be thrown when the application is initialized and exited using System.out(-1). ERROR level exceptions are exceptions that developers must pay attention to and handle. Do not use ERROR level exceptions casually. The Warn level is user operation abnormal logs and some logs that are convenient for troubleshooting bugs in the future. INFO is the key process log. DEBUG is a debug log, so write as little as possible.

[Mandatory] Requirement: Every small module must have INFO-level logs, and all key processes must have at least INFO-level logs.

[Mandatory] Exception information should include two types of information: crime scene information and exception stack information. If it is not processed, it will be thrown upward through the keyword throws.

## 3. Concurrency specifications
[Mandatory] Thread resources must be provided through the thread pool, and explicit creation of threads in the application is not allowed.

[Mandatory] When concurrency is high, synchronous calls should consider the performance loss of locks. If you can use lock-free data structures, don't use locks; if you can lock code blocks, don't lock the entire method body; if you can use object locks, don't use class locks.

[Mandatory] Use ThreadLocal as little as possible. When using it, if it stores an object that needs to be closed, remember to close it and release it in time.