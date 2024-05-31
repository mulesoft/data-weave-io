%dw 2.0
import * from dw::io::http::BodyUtils
import * from dw::io::http::Client
import * from dw::io::http::Types
import * from dw::io::http::utils::HttpHeaders

output application/json

fun createHttpResponse<H <: HttpHeaders>(status: Number, headers: H, body: Binary | Null = null): HttpResponse<Binary, H> = do {
  var normalizedResponseHeaders = normalizeHeaders(headers)
  var contentType = normalizedResponseHeaders[CONTENT_TYPE_HEADER] default ""
  ---
  if (body != null) {
    status: status,
    headers: headers,
    cookies: {},
    contentType: contentType,
    body: body
  } else {
    status: status,
    headers: headers,
    cookies: {},
    contentType: contentType
  }
}
---
{
 a: do {
   var httpResponse = createHttpResponse(200,{ (CONTENT_TYPE_HEADER): "application/json" }, in0)
   var parsedResponse = readHttpResponseBody(httpResponse,DEFAULT_SERIALIZATION_CONFIG)
   ---
   { response: parsedResponse, mimeType: parsedResponse.body.^.mimeType, raw: parsedResponse.body.^.raw is Binary}
 },
 b: do {
   var httpResponse = createHttpResponse(200, { "Content-TYPE": "application/xml" }, in1)
   var parsedResponse = readHttpResponseBody(httpResponse, DEFAULT_SERIALIZATION_CONFIG)
   ---
   { user: parsedResponse.body.user, mimeType: parsedResponse.body.^.mimeType, raw: parsedResponse.body.^.raw is Binary}
 },
 binary: do {
   var httpResponse = createHttpResponse(200,{ "Content-TYPE": "application/octet-stream" }, in2)
   var parsedResponse = readHttpResponseBody(httpResponse, DEFAULT_SERIALIZATION_CONFIG)
   ---
   { status: parsedResponse.status, headers: parsedResponse.headers, body: parsedResponse.body is Binary, mimeType: parsedResponse.body.^.mimeType, raw: parsedResponse.body.^.raw is Binary }
 },
 nobody: do {
   var httpResponse = createHttpResponse(500, {})
   var parsedResponse = readHttpResponseBody(httpResponse, DEFAULT_SERIALIZATION_CONFIG)
   ---
   parsedResponse
 }
}