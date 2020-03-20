%dw 2.0
import * from dw::http::Client
import dw::core::Assertions
import form, field, file from dw::module::Multipart
import HttpClientResult from dw::http::Types

output application/java

fun then<A, V>(result: A, assertions: (result: A) -> V): Boolean =
  using(pass = dw::Runtime::try(() -> assertions(result)).success default false)
  using(x = if(pass) result else log('failed', result))
    pass



var binaryFile: Binary = "PK\u0003\u0004\u0014\u0000\u0000\u0000\b\u0000\u0010nLK5\ufffd\u001eH\ufffd\u0000\u0000\u0000\ufffd\u0000\u0000\u0000\r\u0000\u001c\u0000exchange.jsonUT\t\u0000\u0003ߜ\ufffdY\nO\ufffdYux\u000b\u0000\u0001\u0004\ufffd\u0001\u0000\u0000\u0004\u0014\u0000\u0000\u0000]\ufffd\ufffd\u000e\ufffd \u0010D\ufffd\ufffd\u0015\ufffd\ufffd4Ԃ\u0011oƓ\ufffd`<\ufffdv%\$miXjL\ufffd\ufffd.୷ͼ\ufffd\ufffd\ufffdp&F\ufffd8n\ufffd\u000b\ufffd\rx\"\ufffdԤw\u0012\ufffd\ufffd&\u0018qŪ\ufffd\u0018\ufffd\ufffd?=\ufffdB\ufffd\u000b\ufffd0V\ufffd\ufffdQ\ufffd\ufffd\ufffd\ufffd;\ufffdO\ufffd\ufffd[\u0001.\ufffde\ufffd\ufffd\ufffdn\ufffdݡ2{ٵFK}\ufffdF\u0002\u0000ʮ\ufffd\u0011\ufffd6\ufffd=`-\ufffd\ufffd\ufffdY\u000fxa\$\u001f\ufffd\ufffdQ\ufffd\u0012\ufffd\ufffd\u0003PK\u0003\u0004\u0014\u0000\u0000\u0000\b\u0000\ufffdQLK\ufffd\ufffd\u000b?\ufffd\u0000\u0000\u0000\ufffd\u0000\u0000\u0000\b\u0000\u001c\u0000main.dwlUT\t\u0000\u0003Hj\ufffdY\nO\ufffdYux\u000b\u0000\u0001\u0004\ufffd\u0001\u0000\u0000\u0004\u0014\u0000\u0000\u0000m\ufffd=\u000b\ufffd0\u0014E\ufffd\ufffd\ufffd78\tI\ufffdc\ufffdRB)~\u0004L\ufffd\ufffd\ufffdTb\u001bhLM^\ufffd \ufffdw\ufffd\ufffd\ufffd\ufffd\ufffd˹\ufffd.\ufffd#l\ufffdAaQT\ufffdkb~\ufffd2\u0004\ufffdѸG\ufffd\ufffd\u001e#\ufffdaքRJd\ufffdO\ufffd\u0011\ufffd\ufffdB\ufffd\u0007Ne[\ufffd\u0017Iž\ufffdF\u0095\u0000Լ\ufffd~B\\B\ufffde)%6:7Κݜe\ufffd\ufffd\ufffd\fN\ufffdz\ufffdZ\ufffd\ufffd\u0004`ü\u000e\ufffd\ufffd\ufffdf\u0001\u0015\ufffd\u00006\u0006\ufffdϨf\ufffd\ufffd\ufffd\ufffd\ufffdH\ufffd\u0001PK\u0001\u0002\u001e\u0003\u0014\u0000\u0000\u0000\b\u0000\u0010nLK5\ufffd\u001eH\ufffd\u0000\u0000\u0000\ufffd\u0000\u0000\u0000\r\u0000\u0018\u0000\u0000\u0000\u0000\u0000\u0001\u0000\u0000\u0000\ufffd\ufffd\u0000\u0000\u0000\u0000exchange.jsonUT\u0005\u0000\u0003ߜ\ufffdYux\u000b\u0000\u0001\u0004\ufffd\u0001\u0000\u0000\u0004\u0014\u0000\u0000\u0000PK\u0001\u0002\u001e\u0003\u0014\u0000\u0000\u0000\b\u0000\ufffdQLK\ufffd\ufffd\u000b?\ufffd\u0000\u0000\u0000\ufffd\u0000\u0000\u0000\b\u0000\u0018\u0000\u0000\u0000\u0000\u0000\u0001\u0000\u0000\u0000\ufffd\ufffd\ufffd\u0000\u0000\u0000main.dwlUT\u0005\u0000\u0003Hj\ufffdYux\u000b\u0000\u0001\u0004\ufffd\u0001\u0000\u0000\u0004\u0014\u0000\u0000\u0000PK\u0005\u0006\u0000\u0000\u0000\u0000\u0002\u0000\u0002\u0000\ufffd\u0000\u0000\u0000\ufffd\u0001\u0000\u0000\u0000\u0000" as Binary

---
using( result = [
  nativeRequest({
    method: 'GET',
    url: 'http://httpbin.org/get'
  }) then [
    Assertions::mustEqual($.request.url, "http://httpbin.org/get"),
    Assertions::mustEqual($.request.method, "GET"),
    Assertions::mustEqual($.response.statusText, "OK"),
    Assertions::mustEqual($.err, false),
    Assertions::mustEqual($.response.status, 200),
    Assertions::assert($.request.headers is Object)
  ],

  // validate urlEncode in invalid uri characters
  request('GET', 'http://httpbin.org/anything/%7Basdasd%7D?a') then [
    Assertions::mustEqual($.request.path, '/anything/%7Basdasd%7D?a'),
    Assertions::mustEqual($.response.status, 200),
    Assertions::mustEqual($.response.body.url, 'http://httpbin.org/anything/{asdasd}?a'),
  ],

  // our server has a weird https behavior.
//  request('GET', 'https://anypoint.mulesoft.com/accounts/me') then [
//    Assertions::mustEqual($.response.status, 403),
//    Assertions::assert(($ as HttpClientResult) is HttpClientResult)
//  ],

  request('GET', 'https://self-signed.badssl.com/', { allowUnsafeSSL: true })  then [
    Assertions::mustEqual($.err, false)
  ],

  request('GET', 'https://self-signed.badssl.com/', { allowUnsafeSSL: false }) then [
    Assertions::mustEqual($.err, true)
  ],

  // Test octet-stream
  request('GET', 'http://httpbin.org/bytes/1024') then [
    Assertions::assert($.response.body is Binary),
    Assertions::assert(sizeOf($.response.body!) == 1024),
    Assertions::assert($.response.body == $.response.payload)
  ],

  // Test octet-stream & chunked streaming
  request('GET', 'http://httpbin.org/stream-bytes/1024?chunk_size=128') then [
    Assertions::assert($.response.body is Binary),
    Assertions::assert(sizeOf($.response.body!) == 1024),
    Assertions::assert($.response.body == $.response.payload),
    Assertions::assert($.request.path == '/stream-bytes/1024?chunk_size=128')
  ],

//  // our server has a weird https behavior.
//  request('POST', 'https://anypoint.mulesoft.com/accounts/login', { body: {user: 'data-weave', password: 'data-weave'} }) then [
//    Assertions::mustEqual($.request.port, 443),
//    Assertions::mustEqual($.request.headers.Host, 'anypoint.mulesoft.com:443'),
//    Assertions::mustEqual($.response.status, 400)
//  ],

  request('GET', 'https://github.com/') then [
    Assertions::mustEqual($.response.status, 200)
  ],

  request('GET', 'https://google.com/') then [
    Assertions::mustEqual($.err, false)
  ],

  request('POST', 'http://httpbin.org/post',
    {
      body: form([
        field('field', 'value'),
        field({name: 'field2', value:'value2'}),
        file({name: 'fileX', path: 'MyApi.dwl' }),
        file('fileY','MyApi.dwl')
      ])
    }
  ) then [
     Assertions::mustEqual($.request.port, 80),
     Assertions::assert(sizeOf($.request.payload!) > 0, "request.payload must exist"),
     Assertions::mustEqual($.request.headers.Host, 'httpbin.org'),
     Assertions::mustEqual($.response.body.headers.'Content-Type' contains "multipart", true),
     Assertions::mustEqual($.response.body.form, { field: 'value', field2: 'value2' }),
     Assertions::mustEqual($.response.body.files.fileX?, true),
     Assertions::mustEqual($.response.body.files.fileY?, true)
  ],

  request('POST', 'http://httpbin.org/post?asd=123', {}) then [
     Assertions::mustEqual(($).response.body.args, { asd: '123' })
  ],

  request('POST', 'http://httpbin.org/post',
    {
      body: form([
        field('file', binaryFile, 'application/octet-stream', 'artifact.zip'),
      ])
    }
  ) then [
     Assertions::mustEqual($.request.port, 80),
     Assertions::mustEqual($.response.body.files.file as Binary, binaryFile as Binary)
  ],


  request('POST', 'http://httpbin.org/post',
    {
      body: form([
        field('field', 'value'),
        field('field', 'value')
      ])
    }
  ) then [
     Assertions::mustEqual(($).response.body.form, { field: ['value', 'value'] })
  ],

  request('GET', 'http://httpbin.org/stream-bytes/1000?chunk_size=1024',{}) then [
    Assertions::mustEqual(sizeOf(($).response.payload!), 1000)
  ],

  request('GET', 'http://httpbin.org/stream-bytes/1024?chunk_size=1024',{}) then [
    Assertions::mustEqual(sizeOf(($).response.payload!), 1024)
  ],

  request('GET', 'http://httpbin.org/stream-bytes/1024?chunk_size=128',{}) then [
     Assertions::mustEqual(sizeOf(($).response.payload!), 1024)
  ],

  request(
    'POST',
    'http://httpbin.org/post',
    {
      body: 'asd',
      headers: {
        'x-aaa': 'bbb',
        'authorization': 'aaaaa'
      }
    }
  ) then [
     Assertions::mustEqual($.response.body.data, 'asd'),
     Assertions::mustEqual($.response.body.headers.'X-Aaa', 'bbb'),
     Assertions::mustEqual($.response.body.headers.'Content-Length', '3'),
     Assertions::mustEqual($.response.body.headers.'Content-Type', 'text/plain'),
     Assertions::mustEqual($.response.body.headers.'Authorization', 'aaaaa'),
     Assertions::mustEqual($.response.body.headers.*'Authorization', ['aaaaa']),
     Assertions::mustEqual(($).request.headers.*'Authorization', ['aaaaa']),
  ],

  using(theTime = now())
    request(
      'POST',
      'http://httpbin.org/post',
      {
        body: 'asd',
        headers: {
          'x-aaa': 111,
          'x-bbb': true,
          'x-ccc': theTime,
          'authorization': 'aaaaa'
        }
      }
    ) then [
       Assertions::mustEqual($.response.body.headers.'X-Aaa', '111'),
       Assertions::mustEqual($.response.body.headers.'X-Bbb', 'true'),
       // This may fail in runtime if the format is not correct
       Assertions::assert($.response.body.headers.'X-Ccc' is String),
       Assertions::assert(($.response.body.headers.'X-Ccc' as DateTime) is DateTime),
       Assertions::mustEqual($.response.body.headers.'X-Ccc' as DateTime as String, theTime as String),
       Assertions::mustEqual($.request.headers.'X-Aaa', '111'),
       Assertions::mustEqual($.response.body.headers.'Authorization', 'aaaaa'),
       Assertions::mustEqual($.request.headers.'Authorization', 'aaaaa'),
    ],

  request('GET', 'http://httpbin.org/gzip') then [
    Assertions::mustEqual($.response.body.gzipped, true)
  ],

  request('GET', 'http://httpbin.org/deflate') then [
    Assertions::mustEqual($.response.body.deflated, true)
  ],

  request('GET', 'http://httpbin.org/encoding/utf8') then [
    Assertions::mustEqual($.response.mime, 'text/html'),
    Assertions::mustMatch($.response.contentType!, /.*utf-8/)
  ],

  request('POST', 'http://httpbin.org/post',
    {
      body: 'test0',
      headers: {
        'Content-Type': 'application/json'
      }
    }
  ) then [
     Assertions::mustEqual($.response.body.data, '"test0"'),
     Assertions::mustEqual($.response.body.json, 'test0'),
     Assertions::mustEqual($.response.body.headers.'Content-Type', 'application/json'),
     Assertions::mustEqual($.response.contentType, "application/json")
  ],

  request('POST', 'http://httpbin.org/post',
    {
      body: "'test1'",
      headers: {
        'Content-Type': 'application/json'
      }
    }
  ) then [
     Assertions::mustEqual($.response.body.data, "\"'test1'\""),
     Assertions::mustEqual($.response.body.json, "'test1'"),
     Assertions::mustEqual($.response.body.headers.'Content-Type', 'application/json')
  ],

  request('POST', 'http://httpbin.org/post',
    {
      body: root: 'test2',
      headers: {
        'Content-Type': 'application/xml'
      }
    }
  ) then [
     Assertions::mustEqual($.response.body.data, "<?xml version='1.0' encoding='UTF-8'?>\n<root>test2</root>"),
     Assertions::mustEqual($.response.body.headers.'Content-Type', 'application/xml')
  ],

  request('POST', 'http://httpbin.org/post',
    {
      body: root: 'test2',
      headers: {
        'content-TYPE': 'application/xml'
      }
    }
  ) then [
     Assertions::mustEqual($.response.body.data, "<?xml version='1.0' encoding='UTF-8'?>\n<root>test2</root>"),
     Assertions::mustEqual($.response.body.headers.'Content-Type', 'application/xml')
  ],


  request('POST', 'http://httpbin.org/post',
    {
      body: '"test3"' as Binary,
      headers: {
        'Content-Type': 'application/json'
      }
    }
  ) then [
     Assertions::mustEqual($.response.body.data, '"test3"'),
     Assertions::mustEqual($.response.body.json, 'test3'),
     Assertions::mustEqual($.response.body.headers.'Content-Type', 'application/json')
  ],

  request('GET', 'http://httpbin.org/xml') then [
    Assertions::mustEqual(
      $.response.body,
      {
        slideshow @(
          title: "Sample Slide Show",
          date: "Date of publication",
          author: "Yours Truly"
        ): {
          slide @(
            "type": "all"
          ): {
            title: "Wake up to WonderWidgets!"
          },
          slide @(
            "type": "all"
          ): {
            title: "Overview",
            item: {
              "__text": "Why  are great",
              em: "WonderWidgets"
            },
            item: null,
            item: {
              "__text": "Who  WonderWidgets",
              em: "buys"
            }
          }
        }
      }
    )
  ],

  request('GET', 'http://httpbin.org/cookies/set?k2=v2&k1=v1') then [
    Assertions::mustEqual($.response.cookies.k1.value, 'v1'),
    Assertions::mustEqual($.response.cookies.k2.value, 'v2'),
    Assertions::mustEqual($.response.headers.Location, '/cookies'),
  ],

  request('GET', 'http://httpbin.org/cookies', { cookies: { Token: '_asdf_' }}) then [
    Assertions::mustEqual($.response.body.cookies, Token: '_asdf_')
  ],

  request('GET', 'http://httpbin.org/delay/3', { readTimeout: 500 }) then [
    Assertions::mustEqual($.err, true)
  ],

  request('GET', 'http://httpbin.org/delay/1', { readTimeout: 4500 }) then [
    Assertions::mustEqual($.err, false)
  ],

  request('GET', 'http://httpbin.org/delay/1', { connectionTimeout: 4500 }) then [
    Assertions::mustEqual($.err, false)
  ],

  request('GET', 'http://httpbin.org/delay/1', { connectionTimeout: 1 }) then [
    Assertions::mustEqual($.err, true)
  ],

  request('GET', 'http://httpbin.org/redirect/6') then [
    Assertions::mustEqual($.response.status, 302),
    Assertions::mustEqual($.response.headers.Location, '/relative-redirect/5'),
  ],

//   request('GET', 'http://httpbin.org/redirect/6', {allowRedirect: true}) then [
//     Assertions::mustEqual($.response.status, 200),
//     // Assertions::mustEqual($.response.url, "http://httpbin.org/get"),
//     Assertions::mustEqual($.response.body.url, "http://httpbin.org/get")
//   ],
])
(result) reduce ($ and $$) dw::Runtime::failIf ($ == false)
