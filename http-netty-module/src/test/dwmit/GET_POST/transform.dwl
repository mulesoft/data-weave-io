%dw 2.0
import * from dw::io::http::Client
import form, field, file from dw::module::Multipart


fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)
---
{
  a: GET( 'http://httpbin.org/get') then {
      "statusText": $.statusText,
      "status" : $.status,
      "contentType": $.contentType
    },

  // validate urlEncode in invalid uri characters
  b: GET('http://httpbin.org/anything/%7Basdasd%7D?a')
    then {
      "statusText": $.statusText,
      "status" : $.status,
      "headersIsEmpty": isEmpty($.headers)
    },
  // Test octet-stream
  c: GET('http://httpbin.org/bytes/1024') then {
    body: $.body is Binary,
    bodySize: sizeOf($.body!) == 1024,
    contentType: $.contentType
  },

  // Test octet-stream & chunked streaming
  d: GET( 'http://httpbin.org/stream-bytes/1024?chunk_size=128') then {
       body: $.body is Binary,
       bodySize: sizeOf($.body!) == 1024,
       contentType: $.contentType
  },
  e: POST( 'http://httpbin.org/post',
    {
      body: form([
        field('field', 'value'),
        field({name: 'field2', value:'value2'}),
        file({name: 'fileX', path: 'MyApi.dwl' }),
        file('fileY','MyApi.dwl')
      ]),
      headers: {
        "Content-Type": "multipart/form-data"
      }
    }

  ) then {
     mimeType : $.body.^mimeType,
     body : $.body - "headers" - "origin",
     contentType : $.contentType,
  },

  f: POST(url `http://httpbin.org/post?asd=$(123)&space=$("Mariano de Achaval")`) then {
      mimeType : $.body.^mimeType,
      body : $.body - "headers" - "origin",
      contentType : $.contentType,
  }
}

