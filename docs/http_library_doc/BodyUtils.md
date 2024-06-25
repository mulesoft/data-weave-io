#### _dw::io::http::BodyUtils_
__________________________________________

A utility module that provides a set of functions to interact HTTP body.

To use this module, you must import it to your DataWeave code, for example,
by adding the line `import * from dw::io::http::BodyUtils` to the header of your
DataWeave script.

# Index

### Functions
| Name | Description|
|------|------------|
| [formatHeader](#formatheader ) | Formats the given HTTP header value with the following rules:<br>- The first char of every word is in upper case and the remaining chars are in lower case.|
| [normalizeHeaders](#normalizeheaders ) | Normalize the name of the given `HttpHeaders` value following the `formatHeader` function rules.|
| [readFromBinary](#readfrombinary ) | Reads a `Binary` body value and returns the parsed content.|
| [writeToBinary](#writetobinary ) | Transforms the given HTTP body to a `BinaryBodyType` using:<br>- `contentType`: to select the proper DataFormat<br>- `properties`: the set of configuration properties specified by the DataFormat to write the current body.|




### Types
| Name | Description|
|------|------------|
|[BinaryBodyType](#binarybodytype ) | DataWeave type for representing a `Binary` body.<br>Supports the following fields:|






__________________________________________


# Functions

## **formatHeader**

### _formatHeader&#40;header: String&#41;: String_

Formats the given HTTP header value with the following rules:
- The first char of every word is in upper case and the remaining chars are in lower case.


##### Parameters

| Name | Type | Description|
|------|------|------------|
| header | `String` | The header value to format.|


##### Example

This example format several HTTP header values.

###### Source

```dataweave
%dw 2.0
output application/json
import * from dw::io::http::BodyUtils
---
{
  a: formatHeader("Authorization"),
  b: formatHeader("Content-Type"),
  c: formatHeader("cache-control"),
  d: formatHeader("Accept-ENCODING"),
  e: formatHeader("Set-Cookie"),
  f: formatHeader("x-uow")
}
```

###### Output

```json
{
  "a": "Authorization",
  "b": "Content-Type",
  "c": "Cache-Control",
  "d": "Accept-Encoding",
  "e": "Set-Cookie",
  "f": "X-Uow"
}
```
__________________________________________


## **normalizeHeaders**

### _normalizeHeaders<H <: HttpHeaders&#62;&#40;headers: H&#41;: { _?: String }_

Normalize the name of the given `HttpHeaders` value following the `formatHeader` function rules.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| headers | `HttpHeaders` | The HTTP header value to normalize.|


##### Example

This example normalize several HTTP header values.

###### Source

```dataweave
%dw 2.0
output application/json
import * from dw::io::http::BodyUtils
---
normalizeHeaders({
  "Authorization": "authorization value",
  "Content-Type": "application/xml",
  "cache-control": "no-cache",
  "Accept-ENCODING": "gzip",
  "Set-Cookie": "value",
  "x-uow": "uow"})
```

###### Output

```json
{
  "Authorization": "authorization value",
  "Content-Type": "application/xml",
  "Cache-Control": "no-cache",
  "Accept-Encoding": "gzip",
  "Set-Cookie": "value",
  "X-Uow": "uow"
}
```
__________________________________________

### _normalizeHeaders<H <: HttpHeaders&#62;&#40;headers: Null&#41;: { _?: String }_

Helper method of `normalizeHeaders` to work with Null
__________________________________________


## **readFromBinary**

### _readFromBinary&#40;mime: MimeType, payload: Binary, properties: Object = {}&#41;: Any_

Reads a `Binary` body value and returns the parsed content.

If this function can cannot determine the DataFormat to use by the `MimeType` value returns the received `Binary` value.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| mime | `MimeType` | The MIME type value used to select the proper DataFormat.|
| payload | `Binary` | The body value to be parsed.|
| properties | `Object` | The set of configuration properties specified by the DataFormat to read the current body.|

__________________________________________


## **writeToBinary**

### _writeToBinary&#40;body: HttpBody, contentType: String, properties: Object = {}&#41;: BinaryBodyType_

Transforms the given HTTP body to a `BinaryBodyType` using:
- `contentType`: to select the proper DataFormat
- `properties`: the set of configuration properties specified by the DataFormat to write the current body.

A failure will be thrown if there is no valid DataFormat for the given `contentType` value.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| body | `HttpBody` | The HTTP request body to transform to a `Binary` value.|
| contentType | `String` | The `Content-Type` used to select the proper DataFormat.|
| properties | `Object` | The set of configuration properties specified by the DataFormat to write the current body.|


##### Example

This example transforms a JSON HTTP request body to a `BinaryBodyType` value.

###### Source

```dataweave
%dw 2.0
import * from dw::io::http::BodyUtils
import * from dw::io::http::Client

output application/json
---
{
  json: writeToBinary({name: "Mariano", lastname: "Lischetti"}, DEFAULT_SERIALIZATION_CONFIG.contentType, DEFAULT_SERIALIZATION_CONFIG.writerProperties)
}
```

###### Output

```json
{
  "json": {
    "body": "{\n  \"name\": \"Mariano\",\n  \"lastname\": \"Lischetti\"\n}",
    "mime": {
      "type": "application",
      "subtype": "json",
      "parameters": {}
    }
  }
}
```

##### Example

This example transforms a Multipart HTTP body to a `BinaryBodyType` value using the `boundary` writer configuration.

###### Source

```dataweave
%dw 2.0
import * from dw::io::http::BodyUtils
import * from dw::module::Multipart

output application/json
---
{
  multipart: writeToBinary(
   form([
     field('field', 'value'),
     field({name: 'field2', value:'value2'})]), "multipart/form-data", {boundary: "boundary"})
}
```

###### Output

```json
{
  "multipart": {
    "body": "--boundary\r\nContent-Disposition: form-data; name=\"field\"\r\n\r\nvalue\r\n--boundary\r\nContent-Disposition: form-data; name=\"field2\"\r\n\r\nvalue2\r\n--boundary--\r\n",
    "mime": {
      "type": "multipart",
      "subtype": "form-data",
      "parameters": {
        "boundary": "boundary"
      }
    }
  }
}
```
__________________________________________




__________________________________________

# Types

### **BinaryBodyType**


DataWeave type for representing a `Binary` body.
Supports the following fields:

- `body`: Represents the `Binary` body.
- `mime`: Represent the body `MimeType`.

#### Definition

```dataweave
{ body: Binary, mime: MimeType }
```




