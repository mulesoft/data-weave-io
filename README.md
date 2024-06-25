# Data Weave IO
This repo will contain everything that is related to IO support for data weave: file, HTTP, Process.

## Documentation

For documentation please read [documentation](./docs/home.md) with all the technical details.

## Releases
The following table describes the `IO` and the `DataWeave` versions compatibility

| IO     | DataWeave |
|--------|-----------|
| 2.8.x  | 2.8.x     |
| 2.7.x  | 2.7.x     |
| 0.10.x | 2.6.x     |
| 0.9.x  | 2.5.x     |
| 0.6.x  | 2.4.0     |

### Steps to release:
1. Create a new branch with the following format: `release/{io-version}/{data-weave-version}`.
2. Change the weave-gradle-plugin version to a fix release in the `gradle.properties` file. (e.g: `2.5.0-20221020`)
3. Push your release branch.
4. If you are releasing a `0.6.x` version coming from the support branch you need to do the following:
    - Move to the support/0.6.x branch and increase the patch version for the next release.
    - Update versions for projects based on DataWeave 2.4.0 with support branches to the next snapshot.

## Usages
The libraries from this repository are being used on:
- [DataWeave CLI](https://github.com/mulesoft-labs/data-weave-cli/)
- [DataWeave Playground](https://developer.mulesoft.com/learn/dataweave) 
- [DataWeave Testing Framework](https://github.com/mulesoft/data-weave-testing-framework)

## Netty support
[Netty](./http-netty-module/README.md)

## Disclaimer

The modules are NOT available in the mule runtime, and most probably they will never be as the same functionality 
is being provided by `MuleRuntime` components such as `File Module`, `HTTP Module`, etc.


