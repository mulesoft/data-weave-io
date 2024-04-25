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

fun formatHeader(header: String): String =
  lower(header)
    replace /\b([a-z])/
    with upper($[0])

/**
* Helper method of `normalizeHeaders` to work with Null
**/
fun normalizeHeaders<H <: HttpHeaders>(headers: Null): {_?: String} = {}

/**
* Normalize the object to be compliant with the http header
*
* === Parameters
*
* [%header, cols="1,3"]
* |===
* | Name   | Description
* | headers | The headers to normalize
* |===
*
**/
fun normalizeHeaders<H <: HttpHeaders>(headers: H): {_?: String} =
  headers mapObject {(formatHeader($$ as String)): $ default "" as String}

fun writeToBinary(body: HttpBody, headers: HttpHeaders, config: SerializationConfig): BinaryBodyType = do {
  fun internalWriteToBinary(body: HttpBody, mime: MimeType, writerProperties: Object): BinaryBodyType = body match {
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
           case "multipart" -> { boundary: mime.parameters["boundary"] default generateBoundary() }
           else -> {}
         }
         // Extract encoding
         var encodingConfig = mime.parameters["charset"] match {
           case charset is String -> { encoding: charset }
           else -> {}
         }

         var writerProperties = config.writerProperties default {}
             mergeWith boundaryConfig
             mergeWith encodingConfig

         var binaryBody = write(body, df.defaultMimeType, writerProperties) as Binary {(encoding: writerProperties["encoding"]) if (writerProperties["encoding"]?)}
         // Update MIME with boundary & charset
         var mimeWithAdditionalProperties =
           mime update {
             case p at .parameters -> p
               mergeWith boundaryConfig
               mergeWith {(charset: encodingConfig["encoding"] as String) if (encodingConfig["encoding"]?)}
           }
         ---
         { body: binaryBody, mime: mimeWithAdditionalProperties }
       }
     }
  }

  var normalizedHeaders = normalizeHeaders(headers)
  var contentType = normalizedHeaders[CONTENT_TYPE_HEADER] default config.contentType
  var mime = fromString(contentType)
  ---
  if (mime.success)
    internalWriteToBinary(body, mime.result!, config)
  else
    fail("Unable to parse MIME type: $(contentType) caused by: $(mime.error.message)")
}

fun readFromBinary(mime: MimeType, payload: Binary, config: SerializationConfig): Any = do {
   var df = findDataFormatDescriptorByMime(mime)
   ---
   if (df == null)
     payload
   else do {
     // Extract boundary
     var boundaryConfig = mime.'type' match {
       case "multipart" -> {(boundary: mime.parameters["boundary"]) if (mime.parameters["boundary"]?)}
       else -> {}
     }
     // Extract encoding
     var encodingConfig = mime.parameters["charset"] match {
       case charset is String -> { encoding: charset }
       else -> {}
     }
     var readerProperties = config.readerProperties default {}
       mergeWith boundaryConfig
       mergeWith encodingConfig
     ---
     read(payload, df.defaultMimeType, readerProperties)
   }
}