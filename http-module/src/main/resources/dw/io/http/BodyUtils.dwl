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
import * from dw::io::http::utils::HttpHeaders
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
* Formats the given HTTP header value with the following rules:
* * The first char of every word is in upper case and the remaining chars are in lower case.
*
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | header | `String` | The header value to format.
* |===
*
* === Example
*
* This example format several HTTP header values.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* import * from dw::io::http::BodyUtils
* ---
* {
*   a: formatHeader("Authorization"),
*   b: formatHeader("Content-Type"),
*   c: formatHeader("cache-control"),
*   d: formatHeader("Accept-ENCODING"),
*   e: formatHeader("Set-Cookie"),
*   f: formatHeader("x-uow")
* }
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
* {
*   "a": "Authorization",
*   "b": "Content-Type",
*   "c": "Cache-Control",
*   "d": "Accept-Encoding",
*   "e": "Set-Cookie",
*   "f": "X-Uow"
* }
* ----
*
**/
fun formatHeader(header: String): String =
  lower(header)
    replace /\b([a-z])/
    with upper($[0])

/**
* Normalize the name of the given `HttpHeaders` value following the `formatHeader` function rules.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | headers | `HttpHeaders` | The HTTP header value to normalize.
* |===
*
* === Example
*
* This example normalize several HTTP header values.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* import * from dw::io::http::BodyUtils
* ---
* normalizeHeaders({
*   "Authorization": "authorization value",
*   "Content-Type": "application/xml",
*   "cache-control": "no-cache",
*   "Accept-ENCODING": "gzip",
*   "Set-Cookie": "value",
*   "x-uow": "uow"})
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
* {
*   "Authorization": "authorization value",
*   "Content-Type": "application/xml",
*   "Cache-Control": "no-cache",
*   "Accept-Encoding": "gzip",
*   "Set-Cookie": "value",
*   "X-Uow": "uow"
* }
* ----
*
**/
fun normalizeHeaders<H <: HttpHeaders>(headers: H): {_?: String} =
  headers mapObject {(formatHeader($$ as String)): $ default "" as String}

/**
* Helper method of `normalizeHeaders` to work with Null
**/
fun normalizeHeaders<H <: HttpHeaders>(headers: Null): {_?: String} = {}

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

/**
* Reads a `Binary` body value and returns the parsed content.
*
* If this function can cannot determine the DataFormat to use by the `MimeType` value returns the received `Binary` value.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | mime | `MimeType` | The MIME type value used to select the proper DataFormat.
* | payload | `Binary` | The body value to be parsed.
* | properties | `Object` | The set of configuration properties specified by the DataFormat to read the current body.
* |===
*
**/
fun readFromBinary(mime: MimeType, payload: Binary, properties: Object = {}): Any = do {
   var df = findDataFormatDescriptorByMime(mime)
   ---
   if (df == null)
     payload
   else do {
     // Extract boundary
     var boundaryConfig = mime.'type' match {
       case "multipart" -> { (boundary: mime.parameters["boundary"]) if (mime.parameters["boundary"]?) }
       else -> {}
     }
     // Extract encoding
     var acceptsEncoding = df.readerProperties some ($.name == "encoding")
     var encodingConfig = if (acceptsEncoding) do {
       var charset = mime.parameters["charset"]
       ---
       charset match {
        case charset is String -> { encoding: charset }
        else -> {}
       }
     } else {}

     var readerProperties = properties default {}
       mergeWith boundaryConfig
       mergeWith encodingConfig
     ---
     read(payload, df.defaultMimeType, readerProperties)
   }
}