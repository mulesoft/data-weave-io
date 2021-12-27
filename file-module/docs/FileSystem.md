#### _dw::io::file::FileSystem_
__________________________________________

Simple File module to do basic operations

# Index

### Functions
| Name | Description|
|------|------------|
| [baseNameOf](#basenameof-index ) | Returns the base name of this file|
| [contentOf](#contentof-index ) | Returns the content of the given path|
| [copyTo](#copyto-index ) | Copies the specified binary into the given path.|
| [exists](#exists-index ) | Returns true if the file exits|
| [extensionOf](#extensionof-index ) | Returns the extension of the file with the dot.|
| [home](#home-index ) | Returns the Path value of the home directory.|
| [kindOf](#kindof-index ) | Returns the file type. "File" or "Folder" or null if it doesn't exits|
| [ls](#ls-index ) | Returns the list child file path|
| [mimeTypeOf](#mimetypeof-index ) | Tries to guess the mimeType of the given Path|
| [mkdir](#mkdir-index ) | Creates the a folder in the given path. And returns the path.|
| [nameOf](#nameof-index ) | Returns the name of this file|
| [path](#path-index ) | Creates a valid path with the specified parts|
| [rm](#rm-index ) | Removes the file at the given location. Returns true if the file or folder was removed.|
| [tmp](#tmp-index ) | Returns the Path value of the tmp directory.|
| [toUrl](#tourl-index ) | Transform the specified file path into a valid Url|
| [tree](#tree-index ) | |
| [unzipTo](#unzipto-index ) | Unzips the specified file into the given directory|
| [wd](#wd-index ) | Returns the Path value of the working directory.|
| [writeTo](#writeto-index ) | |
| [zipInto](#zipinto-index ) | Zips the specified collection of files into the given zip path.|


### Variables
| Name | Description|
|------|------------|
| [FILE_EXTENSIONS: { _: String }](#file_extensions-index ) | Mapping between file extension and MimeType|



### Types
| Name | Description|
|------|------------|
|[FileKind](#filekind-index ) | The two kind of file|
|[Path](#path-index ) | |







__________________________________________


# Functions

## **baseNameOf** [↑↑](#index )

### _baseNameOf(path: Path): String_

Returns the base name of this file

##### Parameters

| Name   | Description|
|--------|------------|
| path ||


##### Example

This example shows how the `baseNameOf` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
dw::io::file::FileSystem::baseNameOf("/tmp/a/test.json")

```

###### Output

```json
"test"
```
__________________________________________


## **contentOf** [↑↑](#index )

### _contentOf(path: Path): Binary_

Returns the content of the given path

##### Parameters

| Name   | Description|
|--------|------------|
| path | The path of the file|


##### Example

This example shows how the `contentOf` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
contentOf("/tmp/foo/bar.txt") as String {encoding: "UTF-8"}

```

###### Output

```json
"Hello"
```
__________________________________________


## **copyTo** [↑↑](#index )

### _copyTo(binary: Binary, path: Path): Number_

Copies the specified binary into the given path.

##### Parameters

| Name   | Description|
|--------|------------|
| binary | The content to be written|
| path | The path were is going to be written|


##### Example

This example shows how the `writeTo` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
copyTo( "Hello" as Binary {encoding: "UTF-8"}, "/tmp/foo/bar.txt")
```

###### Output

```json
5
```
__________________________________________


## **exists** [↑↑](#index )

### _exists(path: Path): Boolean_

Returns true if the file exits

##### Parameters

| Name   | Description|
|--------|------------|
| path | The path to be checked|


##### Example

This example shows how the `exists` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
dw::io::file::FileSystem::exists("/tmp")

```

###### Output

```json
true
```
__________________________________________


## **extensionOf** [↑↑](#index )

### _extensionOf(path: Path): String | Null_

Returns the extension of the file with the dot.

##### Parameters

| Name   | Description|
|--------|------------|
| path | The path|


##### Example

This example shows how the `extensionOf` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
%dw 2.0
 import * from dw::io::file::FileSystem
 output application/json
 ---
 {
   a: extensionOf(path("/tmp","foo.txt")),
   b: extensionOf(path("/tmp","foo.html")),
   c: extensionOf(path("/tmp","foo.json")),
   d: extensionOf(tmp()) //Directory should return null
 }

```

###### Output

```json
{
   "a": ".txt",
   "b": ".html",
   "c": ".json",
   "d": null
 }
```
__________________________________________


## **home** [↑↑](#index )

### _home(): Path_

Returns the Path value of the home directory.
__________________________________________


## **kindOf** [↑↑](#index )

### _kindOf(path: Path): FileKind | Null_

Returns the file type. "File" or "Folder" or null if it doesn't exits
__________________________________________


## **ls** [↑↑](#index )

### _ls(folder: Path): Array<Path>_

Returns the list child file path

##### Parameters

| Name   | Description|
|--------|------------|
| folder | The of the contained file|


##### Example

This example shows how the `ls` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
  ls("/tmp")

```

###### Output

```json

["/tmp/foo.txt","/tmp/dw-input-buffer-0.tmp","/tmp/dw-output-buffer-0.tmp"]
```
__________________________________________

### _ls(folder: Path, filterExpr: Regex): Array<Path>_

Return the list of child elements of the specified path. That matches the specified regex pattern

##### Parameters

| Name   | Description|
|--------|------------|
| folder | The of the contained file|
| filterExpr | The file expression regex to be used on each name|


##### Example

This example shows how to filter tmp file to all the one that contains the text 'dw'

###### Source

```dataweave
%dw 2.0
output application/json
---
  ls("/tmp", /dw/)

```

###### Output

```json

["/tmp/dw-input-buffer-0.tmp","/tmp/dw-output-buffer-0.tmp"]
```
__________________________________________


## **mimeTypeOf** [↑↑](#index )

### _mimeTypeOf(path: Path): String | Null_

Tries to guess the mimeType of the given Path

##### Parameters

| Name   | Description|
|--------|------------|
| path | The path|


##### Example

This example shows how the `mimeTypeOf` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
dw::io::file::FileSystem::mimeTypeOf("/tmp/test.json")

```

###### Output

```json
"application/json"
```
__________________________________________


## **mkdir** [↑↑](#index )

### _mkdir(path: Path): Path | Null_

Creates the a folder in the given path. And returns the path.

##### Parameters

| Name   | Description|
|--------|------------|
| path | The path to be created|


##### Example

This example shows how the `mkdir` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
mkdir("/tmp/a")

```

###### Output

```json
"/tmp/a"
```
__________________________________________


## **nameOf** [↑↑](#index )

### _nameOf(path: Path): String_

Returns the name of this file
__________________________________________


## **path** [↑↑](#index )

### _path(basePath: Path, part: String): Path_

Creates a valid path with the specified parts

##### Parameters

| Name   | Description|
|--------|------------|
| basePath | The base path|
| part | The child path to append|


##### Example

This example shows how the `path` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
path("/tmp/a","b")

```

###### Output

```json
"/tmp/a/b"
```
__________________________________________

### _path(basePath: Path, part: String, part2: String): Path_

Creates a valid Path with the specified parts

##### Parameters

| Name   | Description|
|--------|------------|
| basePath | The base path|
| part | Child Part 1|
| part2 | Child Part 2|


##### Example

This example shows how the `path` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
path("/tmp", "a","b")

```

###### Output

```json
"/tmp/a/b"
```
__________________________________________

### _path(basePath: Path, part: String, part2: String, part3: String): Path_

Creates a valid Path with the specified parts

##### Parameters

| Name   | Description|
|--------|------------|
| basePath | The base path|
| part | Child Part 1|
| part2 | Child Part 2|
| part3 | Child Part 3|


##### Example

This example shows how the `path` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
path("/tmp", "a","b","c")

```

###### Output

```json
"/tmp/a/b/c"
```
__________________________________________

### _path(basePath: Path, parts: Array<String>): Path_

Creates a valid Path with the specified parts

##### Parameters

| Name   | Description|
|--------|------------|
| basePath | The base path|
| parts | All that child part|


##### Example

This example shows how the `path` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
path("/tmp", ["a","b","c"])

```

###### Output

```json
"/tmp/a/b/c"
```
__________________________________________


## **rm** [↑↑](#index )

### _rm(path: Path): Boolean_

Removes the file at the given location. Returns true if the file or folder was removed.

If the path is a file it will delete everything recursively.

##### Parameters

| Name   | Description|
|--------|------------|
| path | The path to delete|


##### Example

This example shows how the `rm` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
import rm from dw::io::file::FileSystem
output application/json
---
rm("/home/dw/toRemove")

```

###### Output

```json
true
```
__________________________________________


## **tmp** [↑↑](#index )

### _tmp(): Path_

Returns the Path value of the tmp directory.
__________________________________________


## **toUrl** [↑↑](#index )

### _toUrl(path: Path): String_

Transform the specified file path into a valid Url

##### Parameters

| Name   | Description|
|--------|------------|
| `path` | The path to be converted|


##### Example

This example shows how the `toUrl` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
output application/json
---
toUrl( "/tmp/Application Test")

```

###### Output

```json
"file:/tmp/Application%20Test"
```
__________________________________________


## **tree** [↑↑](#index )

### _tree(path: Path): Array<Path>_


__________________________________________


## **unzipTo** [↑↑](#index )

### _unzipTo(zipPath: Path, targetDirectory: Path): Path_

Unzips the specified file into the given directory

##### Parameters

| Name   | Description|
|--------|------------|
| zipPath ||
| targetDirectory ||


##### Example

This example shows how the `unzipTo` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
import * from dw::io::file::FileSystem
output application/json
---
fileToUnzip unzipTo path(tmp(), "dw_io_test" ,"outputZip")

```

###### Output

```json
"/tmp/dw_io_test/outputZip"
```
__________________________________________


## **wd** [↑↑](#index )

### _wd(): Path_

Returns the Path value of the working directory.
__________________________________________


## **writeTo** [↑↑](#index )

### _writeTo(path: Path, binary: Binary): Number_


__________________________________________


## **zipInto** [↑↑](#index )

### _zipInto(paths: Array<Path>, zipPath: Path): Path_

Zips the specified collection of files into the given zip path.

##### Parameters

| Name   | Description|
|--------|------------|
| paths | The array of paths to be zipped|
| zipPath | The zip file path to be created|


##### Example

This example shows how the `zipInto` behaves under different inputs.

###### Source

```dataweave
%dw 2.0
import * from dw::io::file::FileSystem
output application/json
---
[path(tmp(),"dw_io_test")] zipInto path(tmp(),"outputZip.zip")

```

###### Output

```json
"/tmp/outputZip.zip"
```
__________________________________________




# Variables

## **FILE_EXTENSIONS: { _: String }** [↑↑](#index )


Mapping between file extension and MimeType



__________________________________________

# Types

### **FileKind** [↑↑](#index )


The two kind of file

#### Definition

```dataweave
"File" | "Folder"
```


### **Path** [↑↑](#index )




#### Definition

```dataweave
String
```




