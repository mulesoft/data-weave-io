%dw 2.0
import * from dw::io::http::Client

output application/json
---
{
 noHeadersBody: do {
  var request = createBinaryHttpRequest({
    method: "POST",
    url: "http://localhost:8081/post",
    body: {
      root: {user: "Mariano"}
    }
  }, DEFAULT_SERIALIZATION_CONFIG)
  ---
  { request: request, binary: request.body is Binary, contentType: request.headers."Content-Type"}
 },
 noBody:
   createBinaryHttpRequest({
     method: "GET",
     url: { url: "http://localhost:8081/get", queryParams: {"p": "param-value"}},
     headers: {"Accept": "*/*"}
   }, DEFAULT_SERIALIZATION_CONFIG),
 bodyUsingHeaders: do {
   var request = createBinaryHttpRequest({
     method: "POST",
     url: "http://localhost:8081/post",
     headers: {"content-type": "application/xml", "Accept": "*/*"},
     body: {
       root: {user: "Mariano"}
     }
   }, { contentType: "application/json", readerProperties: {}, writerProperties: {encoding: "UTF-8", indent: false}})
   ---
   { request: request, binary: request.body is Binary }
 },
 bodyUsingSerializationConfig: do {
   var request = createBinaryHttpRequest({
     method: "POST",
     url: "http://localhost:8081/post",
     headers: {"Accept": "*/*"},
     body: {
       root: {user: "Mariano"}
     }
    }, { contentType: "application/xml", readerProperties: {}, writerProperties: {encoding: "UTF-8", indent: true}})
    ---
    { request: request, binary: request.body is Binary, contentType: request.headers."Content-Type"}
 }
}