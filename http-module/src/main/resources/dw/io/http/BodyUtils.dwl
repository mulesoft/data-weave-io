%dw 2.0

import dw::io::http::Types
import * from dw::core::Objects

fun formatHeader(header: String): String =
  lower(header)
    replace /\b([a-z])/
    with upper($[0])

/**
* Helper method of `normalizeHeaders` to work with Null
**/
fun normalizeHeaders(headers: Null): {_?: String} =
  {}

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
fun normalizeHeaders(headers: { _*?: (SimpleType | Null) }): {_?: String} =
  headers mapObject {(formatHeader($$ as String)): $ default "" as String}


fun generateBody(config: { body?: Any, headers?: Types::HttpHeaders, writerOptions?: Dictionary<Any> }): {
  body?: Binary,
  headers: Types::HttpHeaders
} =
  do {
    var headers = normalizeHeaders(config.headers)
    ---
    if (!config.body?)
      {
        headers: headers
      }
    else do {
      var contentType = headers['Content-Type'] default (
                                      config.body match {
                                        case is Binary -> 'application/octet-stream'
                                        case is dw::module::Multipart::Multipart -> 'multipart/form-data; boundary=$(config.writerOptions.boundary default dw::module::Multipart::generateBoundary())'
                                        case is String -> 'text/plain'
                                        else -> 'application/json'
                                      })
      var writerOptions = (config.writerOptions default {}) mergeWith {
        (boundary: (contentType scan /boundary=(.*)/)[0][1]) if (contentType startsWith 'multipart/form-data')
      }
      var body = (config.body match {
        case is Binary -> config.body
        else -> write(config.body, contentType, log(writerOptions))
      }) as Binary
      ---
      {
        headers: headers mergeWith {
          'Content-Type': contentType,
          ('Content-Length': sizeOf(body)) if (body is Binary)
        },
        body: body
      }
  }
}

fun safeRead(mime: String, payload: String | Binary | Null, readerOptions: Object): Any =
  mime match {
    case matches /.*\/octet-stream/ -> payload as Binary
    case matches /.*\/x-binary/ -> payload as Binary
    else ->
        payload match {
            case is Null -> null
            case content is String | Binary -> do {
                dw::Runtime::try(
                    () -> read(content, mime, readerOptions)
                  ).result
            }
        }

  }