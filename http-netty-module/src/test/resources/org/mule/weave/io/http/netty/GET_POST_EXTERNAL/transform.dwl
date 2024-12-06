%dw 2.0

import * from dw::io::http::Client
import form, field, file from dw::module::Multipart

fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)
---
{
  // validate urlEncode in invalid uri characters
  b: get('http://httpbin.org/anything/%7Basdasd%7D?a')
    then {
      "statusText": $.statusText,
      "status" : $.status,
      "headersIsEmpty": isEmpty($.headers)
    },
  // Test octet-stream & chunked streaming
  d: get( 'http://httpbin.org/stream-bytes/1024?chunk_size=128') then {
       body: $.body is Binary,
       bodySize: sizeOf($.body!) == 1024,
       contentType: $.contentType
  },
  e: post( 'http://httpbin.org/post', { "Content-Type": "multipart/form-data"},form([
    field('field', 'value'),
    field({name: 'field2', value:'value2'}),
    file({name: 'fileX', path: 'MyApi.dwl' }),
    file('fileY','MyApi.dwl')])) then {
     mimeType : $.body.^mimeType,
     body : $.body as Object - "headers" - "origin",
     contentType : $.contentType
  },
  ee: postMultipart( 'http://httpbin.org/post', form([
    field('field', 'value'),
    field({name: 'field2', value:'value2'}),
    file({name: 'fileX', path: 'MyApi.dwl' }),
    file('fileY','MyApi.dwl')]), { "Content-TYPE": "multipart/form-data"})
    then {
     mimeType : $.body.^mimeType,
     body : $.body as Object - "headers" - "origin",
     contentType : $.contentType
  },
  eee: postMultipart( 'http://httpbin.org/post', form([
    field('field', 'value'),
    field({name: 'field2', value:'value2'}),
    file({name: 'fileX', path: 'MyApi.dwl' }),
    file('fileY','MyApi.dwl')]))
    then {
     mimeType : $.body.^mimeType,
     body : $.body as Object - "headers" - "origin",
     contentType : $.contentType
  }
}

