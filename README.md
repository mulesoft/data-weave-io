# Data Weave IO
This repository contains everything that is related to `IO` support for `DataWeave` such as:
- **File**: This module allows the user to interact with everything that is related to File System. 
For example, creating, listing and navigating a File System.
- **HTTP**: This module allows the user to call or serve HTTP.
- **Process**: A simple module to execute a native system process.

## Documentation
Look at the following [documentation](./docs/home.md) for technical details about the modules.

## Releases
`DataWeave IO` uses `DataWeave automation scripts` for release. 
Please read the following [documentation](https://github.com/mulesoft/data-weave-automation-scripts) for details.

## DataWeave - IO compatibility
The following table describes the `DataWeave IO` and the `DataWeave` version compatibility:

| DataWeave IO | DataWeave |
|--------------|-----------|
| 2.8.x        | 2.8.x     |
| 2.7.x        | 2.7.x     |
| 0.10.x       | 2.6.x     |
| 0.9.x        | 2.5.x     |
| 0.6.x        | 2.4.0     |

## Usages
The libraries from this repository are being used at:
- [DataWeave CLI](https://github.com/mulesoft-labs/data-weave-cli/)
- [DataWeave Playground](https://developer.mulesoft.com/learn/dataweave) 
- [DataWeave Testing Framework](https://github.com/mulesoft/data-weave-testing-framework)

## HTTP module implementation
The `DataWeave IO` module provides an `HTTP module` implementation based on [Netty](./http-netty-module/README.md).

## Disclaimer
The modules are NOT available in the mule runtime, and most probably they will never be as the same functionality is being provided by mule runtime components such as `File Module`, `Http Module`, etc.

