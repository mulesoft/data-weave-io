import * from dw::io::process::Process
---
{
  ls: exec(["ls", "-l"], {}) then {
    status: $.status,
    exitCode: $.exitCode,
    out: isEmpty($.stdOut)
  },
  invalid: exec(["xxaaaQQQ", "-l"], {}),
  emptyArg: exec([], {}),
  invalidParams: exec(["ls", "---r"], {}) then {
    status: $.status,
    exitCode: $.exitCode != 0,
    out: isEmpty($.stdErr)
  },
  timeout: exec(["sleep", "1234"], {
    timeout: 12
  }) then {
    status: $.status,
    out: isEmpty($.stdErr)
  },
  timeoutWithUnit: exec(["sleep", "1234"], {
      timeout: 2,
      timeoutUnit: "seconds"
  }) then {
    status: $.status,
    out: isEmpty($.stdErr)
  }
}
