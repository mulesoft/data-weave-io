%dw 2.0

import * from dw::io::http::Client

fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)
---
{
  // Test octet-stream & chunked streaming
  d: get( 'http://httpbin.org/stream-bytes/1024?chunk_size=128') then {
       body: $.body is Binary,
       bodySize: sizeOf($.body!) == 1024,
       contentType: $.contentType
  }
}

