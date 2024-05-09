%dw 2.0
import * from dw::io::http::Client

output application/json

---
{
 pets:
   parseHttpResponseBody(
     in0 <~ { mimeType: "application/json", raw: in0 },
     DEFAULT_SERIALIZATION_CONFIG),
 user:
   parseHttpResponseBody(in1 <~ { mimeType: "application/xml", raw: in1 },
     DEFAULT_SERIALIZATION_CONFIG).user,
 binary: do {
   var body = parseHttpResponseBody(
      in2 <~ { mimeType: "application/octet-stream", raw: in2 },
      DEFAULT_SERIALIZATION_CONFIG)
   ---
   { body: body is Binary, mimeType: body.^.mimeType, raw: body.^.raw is Binary}
 }
}