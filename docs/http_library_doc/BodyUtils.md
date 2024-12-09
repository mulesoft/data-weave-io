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
| [writeToBinary](#writetobinary ) | Transforms the given HTTP body to a `BinaryBodyType` using:<br>- `contentType`: to select the proper DataFormat<br>- `properties`: the set of configuration properties specified by the DataFormat to write the current body.|




### Types
| Name | Description|
|------|------------|
|[BinaryBodyType](#binarybodytype ) | DataWeave type for representing a `Binary` body.<br>Supports the following fields:|






__________________________________________


# Functions

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




