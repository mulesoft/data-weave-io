# Data Weave IO
This repo will contain everything that is related to IO support for data weave: file, HTTP, Process.

## Docs

For documentation please read [Documentation](./docs/home.md) That contains all the technical documentation.

## Release Process

Today we have two supported versions based on different dataweave versions.
Releases should be based of one of these branches:
- master -> DataWeave 2.5.0
- support/0.6.x -> DataWeave 2.4.0

### Steps to release:
1. Create a new branch with the following format: `release/{io-version}/{data-weave-version}`.
2. Change the weave-gradle-plugin version to a fix release in the `gradle.properties` file. (e.g: `2.5.0-20221020`)
3. Push your release branch.
4. If you are releasing a `0.6.x` version coming from the support branch you need to do the following:
    - Move to the support/0.6.x branch and increase the patch version for the next release.
    - Update versions for projects based on DataWeave 2.4.0 with support branches to the next snapshot.

## Usages

The libraries from this repo are being used on our cli our playground and the file module by our testing framework.

## Disclaimer

The modules are NOT available in the mule runtime, and most probably they will never be as the same functionality is being provided by mule runtime components such as File Module, Http Module etc.


