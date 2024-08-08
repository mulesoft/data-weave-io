%dw 2.0
import * from dw::io::http::Client
import * from dw::Runtime

output application/json

type User = {
  name: String
}
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
 },
 // should not fail on broken binaries
 d: do {
   var body = readBody("application/json", in3, DEFAULT_SERIALIZATION_CONFIG)
   ---
   {
     raw: body.^raw,
     mimeType: body.^mimeType,
     result: do {
       var result = try(() -> body as User)
       ---
       {
         success: result.success,
         error: {
           kind: result.error.kind,
           message: result.error.message,
         },
         result: result.result
       }
     }
   }
 },
 // Use encoding
 e: readBody("application/json; charset=UTF-32", in4, DEFAULT_SERIALIZATION_CONFIG)
}