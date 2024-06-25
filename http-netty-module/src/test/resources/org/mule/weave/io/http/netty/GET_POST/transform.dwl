%dw 2.0
import * from dw::io::http::Types
import * from dw::io::http::Client
import form, field, file from dw::module::Multipart


fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)
---
{
  a: get( 'http://httpbin.org/get') then {
      "statusText": $.statusText,
      "status" : $.status,
      "contentType": $.contentType
    },

  // validate urlEncode in invalid uri characters
  b: get('http://httpbin.org/anything/%7Basdasd%7D?a')
    then {
      "statusText": $.statusText,
      "status" : $.status,
      "headersIsEmpty": isEmpty($.headers)
    },
  // Test octet-stream
  c: get('http://httpbin.org/bytes/1024') then {
    body: $.body is Binary,
    bodySize: sizeOf($.body!) == 1024,
    contentType: $.contentType
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
     contentType : $.contentType,
  },
  ee: postMultipart( 'http://httpbin.org/post', form([
    field('field', 'value'),
    field({name: 'field2', value:'value2'}),
    file({name: 'fileX', path: 'MyApi.dwl' }),
    file('fileY','MyApi.dwl')]), { "Content-TYPE": "multipart/form-data"})
    then {
     mimeType : $.body.^mimeType,
     body : $.body as Object - "headers" - "origin",
     contentType : $.contentType,
  },
  eee: postMultipart( 'http://httpbin.org/post', form([
    field('field', 'value'),
    field({name: 'field2', value:'value2'}),
    file({name: 'fileX', path: 'MyApi.dwl' }),
    file('fileY','MyApi.dwl')]), {"Content-TYPE": "multipart/form-data"})
    then {
     mimeType : $.body.^mimeType,
     body : $.body as Object - "headers" - "origin",
     contentType : $.contentType,
  },
  f: post(`http://httpbin.org/post?asd=$(123)&space=$("Mariano de Achaval")`) then {
      mimeType : $.body.^mimeType,
      body : $.body as Object - "headers" - "origin",
      contentType : $.contentType,
  },
  g: post({url: "http://httpbin.org/post", queryParams: {asd: "123", space: "Mariano de Achaval"} }) then {
    mimeType : $.body.^mimeType,
    body : $.body as Object - "headers" - "origin",
    contentType : $.contentType,
  },
  h: sendRequest({ method: "POST", url: "http://httpbin.org/post", queryParams: { asd: "123", space: "Mariano de Achaval" } }) then {
    contentType: $.contentType,
    body: $.body is Binary,
    mimeType: $.body.^mimeType,
    raw: $.body.^raw
  },
  i: get({url: 'http://httpbin.org/cookies/set', queryParams: { k2: "v2", k1: "v1"}}) then {
    k1: {
     name: $.cookies.k1.name,
     value: $.cookies.k1.value,
     maxAge: $.cookies.k1.maxAge,
     httpOnly: $.cookies.k1.httpOnly,
     secure: $.cookies.k1.secure,
     domain: $.cookies.k1.domain,
     comment: $.cookies.k1.comment,
     path: $.cookies.k1.path
    },
    k2: {
     name: $.cookies.k2.name,
     value: $.cookies.k2.value,
     maxAge: $.cookies.k2.maxAge,
     httpOnly: $.cookies.k2.httpOnly,
     secure: $.cookies.k2.secure,
     domain: $.cookies.k2.domain,
     comment: $.cookies.k2.comment,
     path: $.cookies.k2.path
    }
  },
  ii: get('http://httpbin.org/cookies') then {
    cookies: $.cookies,
    body: {
      cookies: $.body.cookies
    }
  },
  iii: get('http://httpbin.org/cookies', { Cookie: "Token=_asdf;age=39" }) then {
    cookies: $.cookies,
    body: {
      cookies: $.body.cookies
    }
  }
}

