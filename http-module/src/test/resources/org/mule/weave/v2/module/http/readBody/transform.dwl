%dw 2.0
import * from dw::io::http::Client

output application/json

---
{
 a:
   readBody("application/json",in0, DEFAULT_SERIALIZATION_CONFIG),
 b:
   readBody("application/xml", in1, DEFAULT_SERIALIZATION_CONFIG).user,
 c: do {
   var body = readBody("application/octet-stream", in2, DEFAULT_SERIALIZATION_CONFIG)
   ---
   { body: body is Binary, mimeType: body.^.mimeType, raw: body.^.raw is Binary}
 }
}