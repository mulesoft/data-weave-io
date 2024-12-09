/**
* A utility module that provides a set of functions to interact HTTP body.
*
* To use this module, you must import it to your DataWeave code, for example,
* by adding the line `import * from dw::io::http::BodyUtils` to the header of your
* DataWeave script.
*/
%dw 2.0

import * from dw::core::Arrays
import * from dw::core::Objects
import * from dw::io::http::Types
import * from dw::module::Mime
import * from dw::module::Multipart
import * from dw::Runtime

/**
 * DataWeave type for representing a `Binary` body.
 * Supports the following fields:
 *
 * * `body`: Represents the `Binary` body.
 * * `mime`: Represent the body `MimeType`.
 */
type BinaryBodyType = {
 body: Binary,
 mime: MimeType
}

/**
* Transforms the given HTTP body to a `BinaryBodyType` using:
* * `contentType`: to select the proper DataFormat
* * `properties`: the set of configuration properties specified by the DataFormat to write the current body.
*
* A failure will be thrown if there is no valid DataFormat for the given `contentType` value.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | body | `HttpBody` | The HTTP request body to transform to a `Binary` value.
* | contentType | `String` | The `Content-Type` used to select the proper DataFormat.
* | properties | `Object` | The set of configuration properties specified by the DataFormat to write the current body.
* |===
*
* === Example
*
* This example transforms a JSON HTTP request body to a `BinaryBodyType` value.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* import * from dw::io::http::BodyUtils
* import * from dw::io::http::Client
*
* output application/json
* ---
* {
*   json: writeToBinary({name: "Mariano", lastname: "Lischetti"}, DEFAULT_SERIALIZATION_CONFIG.contentType, DEFAULT_SERIALIZATION_CONFIG.writerProperties)
* }
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
* {
*   "json": {
*     "body": "{\n  \"name\": \"Mariano\",\n  \"lastname\": \"Lischetti\"\n}",
*     "mime": {
*       "type": "application",
*       "subtype": "json",
*       "parameters": {}
*     }
*   }
* }
* ----
*
* === Example
*
* This example transforms a Multipart HTTP body to a `BinaryBodyType` value using the `boundary` writer configuration.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* import * from dw::io::http::BodyUtils
* import * from dw::module::Multipart
*
* output application/json
* ---
* {
*   multipart: writeToBinary(
*    form([
*      field('field', 'value'),
*      field({name: 'field2', value:'value2'})]), "multipart/form-data", {boundary: "boundary"})
* }
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
* {
*   "multipart": {
*     "body": "--boundary\r\nContent-Disposition: form-data; name=\"field\"\r\n\r\nvalue\r\n--boundary\r\nContent-Disposition: form-data; name=\"field2\"\r\n\r\nvalue2\r\n--boundary--\r\n",
*     "mime": {
*       "type": "multipart",
*       "subtype": "form-data",
*       "parameters": {
*         "boundary": "boundary"
*       }
*     }
*   }
* }
* ----
**/
fun writeToBinary(body: HttpBody, contentType: String, properties: Object = {}): BinaryBodyType = do {
  fun internalWriteToBinary(body: HttpBody, mime: MimeType, properties: Object): BinaryBodyType = body match {
    case b is Binary ->
      { body: b, mime: mime }
    else -> do {
     var df = findDataFormatDescriptorByMime(mime)
     ---
       if (df == null)
         fail("Unable to find data format for: $(mime.'type')/$(mime.subtype)")
       else do {
         // Extract boundary
         var boundaryConfig = mime.'type' match {
           case "multipart" -> do {
             var boundary = if (mime.parameters.boundary?) mime.parameters.boundary!
               else if (properties.boundary?) properties.boundary! as String
               else generateBoundary()
             ---
             { boundary: boundary }
           }
           else -> {}
         }
         // Extract encoding
         var acceptsEncoding = df.writerProperties some ($.name == "encoding")
         var encodingConfig = if (acceptsEncoding) do {
           var charset = mime.parameters["charset"]
           ---
           charset match {
             case charset is String -> { encoding: charset }
             else -> {}
           }
         } else {}
         var writerProperties = properties
             mergeWith boundaryConfig
             mergeWith encodingConfig

         var binaryBody = write(body, df.defaultMimeType, writerProperties) as Binary {(encoding: writerProperties["encoding"]) if (writerProperties["encoding"]?)}
         // Update MIME with boundary & charset
         var mimeWithAdditionalProperties =
           mime update {
             case p at .parameters -> p
               mergeWith boundaryConfig
               mergeWith { (charset: encodingConfig["encoding"] as String) if (encodingConfig["encoding"]?)}
           }
         ---
         { body: binaryBody, mime: mimeWithAdditionalProperties }
       }
     }
  }

  var mime = fromString(contentType)
  ---
  if (mime.success)
    internalWriteToBinary(body, mime.result!, properties)
  else
    fail("Unable to parse MIME type: $(contentType) caused by: $(mime.error.message)")
}