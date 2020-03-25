/**
* Http client module allows to make http calls
*/
%dw 2.0

import dw::core::Objects
import mergeWith from dw::core::Objects
import * from dw::io::http::Types
import * from dw::io::http::BodyUtils

type HttpCustomOptions = HttpClientOptionalOptions & {
  readerOptions?: Object,
  writerOptions?: Object
}

fun nativeRequest(req: HttpClientOptions): HttpClientResult = native("http::HttpRequestFunction")

/**
* Replace the templates of a url according to RFC6570
*/
fun resolveTemplateWith(uri: String, context: Object): String =
    uri replace /\{([^\/]+?)\}/ with ((groups, index) -> context[groups[1]] default index[0])

/**
* Call the specified url with the given method and configuration
*
*/
fun request(method: String, url: String, config: HttpCustomOptions = {}): HttpClientResult = do {
  var nativeRequestConfig =
        config
          mergeWith generateBody(config)
          mergeWith {
            method: method,
            url: url
          }
  var result = nativeRequest(nativeRequestConfig)
  ---
  if (result.response? == false)
    result
  else do {
    var responseHeaders = normalizeHeaders(result.response.headers)
    ---
    result mergeWith
       {
          response:
             result.response! mergeWith
                if (responseHeaders.'Content-Type'? and result.response.payload?) do {
                  var contentType = responseHeaders.'Content-Type'!
                  var mime = (contentType splitBy ";")[0]
                  ---
                  {
                    body: safeRead(mime, result.response.payload, config.readerOptions default {}),
                    mime: mime,
                    contentType: contentType,
                    headers: responseHeaders
                  }
                } else {
                  headers: responseHeaders
                }
       }
  }
}
