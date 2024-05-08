%dw 2.0
import * from dw::io::http::Client
import * from dw::io::http::Types
import * from dw::io::http::utils::HttpHeaders

output application/json

fun createHttpResponse<H <: HttpHeaders>(status: Number, headers: H, body: Binary | Null = null): HttpResponse<Binary, H> =
  if (body != null) {
    status: status,
    headers: headers,
    cookies: {},
    body: body
  } else {
    status: status,
    headers: headers,
    cookies: {}
  }
---
{
 pets:
   parseHttpResponse(
     createHttpResponse(
       200,
       {
         (CONTENT_TYPE_HEADER): "application/json"
       },
       in0 <~ { mimeType: "application/json" }),
     DEFAULT_SERIALIZATION_CONFIG),
 user:
   parseHttpResponse(
     createHttpResponse(
       200,
       {
         (CONTENT_TYPE_HEADER): "application/xml"
       },
       in1 <~ { mimeType: "application/xml"}),
     DEFAULT_SERIALIZATION_CONFIG).body.user,
 nobody:
   parseHttpResponse(
     createHttpResponse(500,{}),
       DEFAULT_SERIALIZATION_CONFIG)
}