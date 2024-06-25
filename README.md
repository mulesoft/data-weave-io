# Data Weave IO
This repo will contain everything that is related to IO support for data weave: file, HTTP, Process.

## Documentation
For documentation please read [documentation](./docs/home.md) with all the technical details.

## Releases

[DataWeave automation scripts](https://github.com/mulesoft/data-weave-automation-scripts)

## DataWeave - IO compatibility
The following table describes the `IO` and the `DataWeave` version compatibility

| IO     | DataWeave |
|--------|-----------|
| 2.8.x  | 2.8.x     |
| 2.7.x  | 2.7.x     |
| 0.10.x | 2.6.x     |
| 0.9.x  | 2.5.x     |
| 0.6.x  | 2.4.0     |

## Usages
The libraries from this repository are being used on:
- [DataWeave CLI](https://github.com/mulesoft-labs/data-weave-cli/)
- [DataWeave Playground](https://developer.mulesoft.com/learn/dataweave) 
- [DataWeave Testing Framework](https://github.com/mulesoft/data-weave-testing-framework)

## Http Module implementation

[Netty](./http-netty-module/README.md)

## Disclaimer
The modules are NOT available in the mule runtime, and most probably they will never be as the same functionality is being provided by mule runtime components such as `File Module`, `Http Module`, etc.

