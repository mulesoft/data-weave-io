/**
* Simple module to execute a native system process
*/
%dw 2.0

type ProcessResult = {
    stdOut: Binary,
    stdErr: Binary,
    exitCode?: Number,
    status: "GRACEFUL" | "KILLED"
}

type FailureResult = {
    status: "ERROR",
    errorMessage: String
}

type ExecConfig = {
  envVars?: {},
  workingDirectory?: String,
  stdIn?: Binary,
  timeout?: Number
}

/**
*
* Executes the specified command and arguments in a separate process with the specified environment and working directory.
* The execution is in a sync way, so it waits till the process ends or kills it if the timeout is reached.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | `cmd` | Array<String&#62; | The array of strings that builds the command to be executed
* | `config` | ExecConfig | Used to configure the process. Specifies things like timeout, working directory, environment variables
* |===
*
* === Example
*
* This example shows how the `exec` function behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* import * from dw::io::process::Process
* ---
*  {
*    ls: exec(["ls", "-l"], {}),
*    invalid: exec(["xxaaaQQQ", "-l"], {}),
*    emptyArg: exec([], {}),
*    invalidParams: exec(["ls", "---r"], {}),
*    timeout: exec(["sleep", "1234"], {
*      timeout: 12
*    })
*  }
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
* {
*    "ls": {
*      "status": "GRACEFUL",
*      "exitCode": 0,
*      "stdOut": "total 120\n-rw-r--r--  1 mdeachaval  staff    905 May 11  2021 Jenkinsfile\n-rw-r--r--@ 1 \n",
*      "stdErr": ""
*    },
*    "invalid": {
*      "status": "ERROR",
*      "message": "Cannot run program \"xxaaaQQQ\": error=2, No such file or directory"
*    },
*    "emptyArg": {
*      "status": "ERROR",
*      "message": "Invalid argument `cmd` should not be empty or have null values."
*    },
*    "invalidParams": {
*      "status": "GRACEFUL",
*      "exitCode": 1,
*      "stdOut": "",
*      "stdErr": "ls: illegal option -- -\nusage: ls [-@ABCFGHLOPRSTUWabcdefghiklmnopqrstuwx1%] [file ...]\n"
*    },
*    "timeout": {
*      "status": "KILLED",
*      "stdOut": "\u0000",
*      "stdErr": "\u0000"
*    }
*  }
* ----
**/
@RuntimePrivilege(requires = "Process::exec")
fun exec(cmd: Array<String>, config: ExecConfig ): ProcessResult | FailureResult = native("Process::ExecProcessFunction")