%dw 2.0

import * from dw::core::Objects
import * from dw::io::http::Types
import * from dw::io::http::utils::HttpHeaders
import * from dw::module::Multipart
import * from dw::Runtime

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

type BinaryBodyType = {
 body: Binary,
 contentType: String
}

fun toBinaryBody(body: HttpBody, headers: HttpHeaders, config: SerializationConfig): BinaryBodyType = do {
  var normalizedHeaders = normalizeHeaders(headers)
  var contentType = normalizedHeaders[CONTENT_TYPE_HEADER] default (
    body match {
      case is Binary -> 'application/octet-stream'
      case is Multipart -> 'multipart/form-data; boundary=$(config.writerProperties.boundary default generateBoundary())'
      case is String -> 'text/plain'
      else -> 'application/json'
  })

  // TODO: review boundary, is it OK to force it? Use custom function to extract boundary (see Mime class at DW)
  var sanitizedContentType = if (contentType == "multipart/form-data") 'multipart/form-data; boundary=$(config.writerProperties.boundary default generateBoundary())' else contentType

  var writerProperties = (config.writerProperties default {})
    mergeWith {
      (boundary: (sanitizedContentType scan /boundary=(.*)/)[0][1]) if (sanitizedContentType startsWith 'multipart/form-data')
    }
  var binaryBody = (body match {
    case is Binary -> body
    else -> write(body, sanitizedContentType, writerProperties)
  }) as Binary
  ---
  { body: binaryBody, contentType: sanitizedContentType}
}

fun safeReadBody(contentType: String, payload: Binary, config: SerializationConfig): Any = do {
  var readerProperties = config.readerProperties default {}
  ---
  // TODO: Should use custom function Mime ???
  contentType match {
    case matches /.*\/octet-stream/ -> payload
    case matches /.*\/x-binary/ -> payload
    else ->
      payload match {
        case is Null -> null
        // TODO: Remove try, we should fail if we can read it.
        case content is String | Binary -> do {
          try( () -> read(content, contentType, readerProperties)).result
        }
      }
  }
}