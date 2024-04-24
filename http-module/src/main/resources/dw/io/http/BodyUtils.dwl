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
 * * `contentType`: Identifies body content type.
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
         var additionalWriterProperties = mime.'type' match {
           case "multipart" -> { boundary: mime.parameters["boundary"] default generateBoundary() }
           else -> {}
         }
         var writerProperties = (config.writerProperties) default {}
             mergeWith additionalWriterProperties

         var binaryBody = write(body, df.id, writerProperties) as Binary
         var mimeWithAdditionalProperties =
           mime update {
             case p at .parameters -> p mergeWith additionalWriterProperties
           }
         ---
         { body: binaryBody, mime: mimeWithAdditionalProperties }
       }
     }
  }

  var normalizedHeaders = normalizeHeaders(headers)
  var contentType = normalizedHeaders[CONTENT_TYPE_HEADER] default (
    body match {
      case is Binary -> 'application/octet-stream'
      case is Multipart -> 'multipart/form-data; boundary=$(config.writerProperties.boundary default generateBoundary())'
      case is String -> 'text/plain'
      else -> config.contentType
  })

  var mime = fromString(contentType)
  ---
  if (mime.success)
   internalWriteToBinary(body, mime.result!, config)
  else
    fail("Unable to parse MIME type: $(contentType) caused by: $(mime.error.message)")
}

fun readFromBinary(mime: MimeType, payload: Binary, config: SerializationConfig): Any = do {
  fun internalReadFromBinary(payload: Binary, mime: MimeType, readerProperties: Object): Any = payload match {
     case is Null -> null
     case content is String | Binary -> do {
       var df = findDataFormatDescriptorByMime(mime)
       ---
       if (df == null)
         payload
       else
         // TODO: What about mime type properties ?? (e.g: Multipart boundary)
         read(content, df.id, readerProperties)
    }
  }
  var readerProperties = config.readerProperties default {}
  ---
  mime.'type' match {
    case "application" ->
        mime.subtype match {
          case "octet-stream" -> payload
          case "x-binary" -> payload
          else -> internalReadFromBinary(payload,mime, readerProperties)
        }
    else ->
      internalReadFromBinary(payload, mime, readerProperties)
  }
}

fun findDataFormatDescriptorByMime(mime: MimeType): DataFormatDescriptor | Null = do {
    var contentType = "$(mime.'type')/$(mime.subtype)"
    // TODO: Add a function to operate with MimeTypes
    ---
    dataFormatsDescriptor()
        firstWith ((df, index) -> (df.defaultMimeType == contentType) or (df.acceptedMimeTypes contains contentType))
}