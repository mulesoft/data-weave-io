#### _dw::io::process::Process_
__________________________________________

Simple module to execute a native system process

# Index

### Functions
| Name | Description|
|------|------------|
| [exec](#exec-index ) | Executes the specified command and arguments in a separate process with the specified environment and working directory.<br>The execution is in a sync way, so it waits till the process ends or kills it if the timeout is reached.|




### Types
| Name | Description|
|------|------------|
|[ExecConfig](#execconfig-index ) | |
|[FailureResult](#failureresult-index ) | |
|[ProcessResult](#processresult-index ) | |







__________________________________________


# Functions

## **exec** [↑↑](#index )

### _exec(cmd: Array<String>, config: ExecConfig): ProcessResult | FailureResult_

Executes the specified command and arguments in a separate process with the specified environment and working directory.
The execution is in a sync way, so it waits till the process ends or kills it if the timeout is reached.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| `cmd` | `Array<String&#62;` | The array of strings that builds the command to be executed|
| `config` | `ExecConfig` | Used to configure the process. An object that specifies the following key/value pairs:<br><br>- `envVars`, a key/value pair that set the environment variable for the subprocess.<br>- `workingDirectory`, the working directory of the subprocess.<br>- `stdIn`, the standard input to be used for the subprocess.<br>- `timeout`, the maximum time to wait.<br>- `timeoutUnit`, the time unit of the timeout argument (Uses `milliseconds` by default).|


##### Example

This example shows how the `exec` function behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
import * from dw::io::process::Process
---
 {
   ls: exec(["ls", "-l"], {}),
   invalid: exec(["xxaaaQQQ", "-l"], {}),
   emptyArg: exec([], {}),
   invalidParams: exec(["ls", "---r"], {}),
   timeout: exec(["sleep", "1234"], {
     timeout: 12
   })
 }
```

###### Output

```json
{
   "ls": {
     "status": "GRACEFUL",
     "exitCode": 0,
     "stdOut": "total 120\n-rw-r--r--  1 mdeachaval  staff    905 May 11  2021 Jenkinsfile\n-rw-r--r--@ 1 \n",
     "stdErr": ""
   },
   "invalid": {
     "status": "ERROR",
     "message": "Cannot run program \"xxaaaQQQ\": error=2, No such file or directory"
   },
   "emptyArg": {
     "status": "ERROR",
     "message": "Invalid argument `cmd` should not be empty or have null values."
   },
   "invalidParams": {
     "status": "GRACEFUL",
     "exitCode": 1,
     "stdOut": "",
     "stdErr": "ls: illegal option -- -\nusage: ls [-@ABCFGHLOPRSTUWabcdefghiklmnopqrstuwx1%] [file ...]\n"
   },
   "timeout": {
     "status": "KILLED",
     "stdOut": "\u0000",
     "stdErr": "\u0000"
   }
 }
```
__________________________________________




__________________________________________

# Types

### **ExecConfig** [↑↑](#index )




#### Definition

```dataweave
{ envVars?: Object, workingDirectory?: String, stdIn?: Binary, timeout?: Number, timeoutUnit?: PeriodUnits }
```


### **FailureResult** [↑↑](#index )




#### Definition

```dataweave
{ status: "ERROR", errorMessage: String }
```


### **ProcessResult** [↑↑](#index )




#### Definition

```dataweave
{ stdOut: Binary, stdErr: Binary, exitCode?: Number, status: "GRACEFUL" | "KILLED" }
```




