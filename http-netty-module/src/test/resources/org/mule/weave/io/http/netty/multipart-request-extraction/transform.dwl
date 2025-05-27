%dw 2.0

import * from dw::io::http::Client

var requestCfg = {
  followRedirects: false,
  readTimeout: 60000,
  requestTimeout: 60000,
  streamResponse: true
}

---
((1 to 10) as Array) map (do {
  var response = get("http://localhost:8080/multipart/multiple_parts_mp4", {}, requestCfg)
  var part = response.body.parts."protobuf_demo_3"
  ---
  {
    i: log("Iteration:", $),
    response: {
      status: response.status,
      headers: response.headers - "Matched-Stub-Id",
      contentType: response.contentType,
      part: {
        headers: part.headers,
        isBinaryContent: part.content is Binary,
        sizeOf: sizeOf(part.content)
      }
    }
  }
})
