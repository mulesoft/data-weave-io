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
       in0 <~ { mimeType: "application/json", raw: in0 }),
     DEFAULT_SERIALIZATION_CONFIG),
 user:
   parseHttpResponse(
     createHttpResponse(
       200,
       {
         "Content-TYPE": "application/xml"
       },
       in1 <~ { mimeType: "application/xml", raw: in1 }),
     DEFAULT_SERIALIZATION_CONFIG).body.user,
 binary: do {
   var response = parseHttpResponse(
      createHttpResponse(
        200,
        {
          "Content-TYPE": "application/octet-stream"
        },
        in2 <~ { mimeType: "application/octet-stream", raw: in2 }),
      DEFAULT_SERIALIZATION_CONFIG)
   ---
   { status: response.status, headers: response.headers, body: response.body is Binary, mimeType: response.body.^.mimeType, raw: response.body.^.raw is Binary}
 },
 nobody:
   parseHttpResponse(
     createHttpResponse(500,{}),
       DEFAULT_SERIALIZATION_CONFIG)
}