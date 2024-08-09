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
   readBody("application/json",in0),
 b:
   readBody("application/xml", in1).user,
 c: do {
   var body = readBody("application/octet-stream", in2)
   ---
   { body: body is Binary, mimeType: body.^.mimeType, raw: body.^.raw is Binary}
 },
 // should not fail on broken binaries
 d: do {
   var body = readBody("application/json", in3)
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
 // Use charset
 e: readBody("application/json; charset=UTF-32", in4),
 // Use reader properties
 f: do {
   var body = readBody("application/csv", in5, {separator: "|"})
   ---
   {
     name: body[0]."Name",
     age: body[0]."Age"
   }
 }
}