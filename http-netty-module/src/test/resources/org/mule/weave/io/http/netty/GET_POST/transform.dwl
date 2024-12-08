%dw 2.0

import * from dw::io::http::utils::HttpHeaders
import * from dw::io::http::Client
import * from dw::io::http::Server
import form, field, file from dw::module::Multipart

output application/json

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort(), normalizeResponseHeaders: false}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/echo.*": {
      "GET": (req: HttpServerRequest) -> do {
        {
          responseStatus: 200,
          body: req
        }
      },
      "POST": (req: HttpServerRequest) -> do {
        {
          responseStatus: 200,
          body: req
        }
      }
    },
    "/bytes": {
      "GET": (req) -> {
        responseStatus: 200,
        headers: {
          "Content-Type": "application/octet-stream",
          "Content-Length": sizeOf(in0)
        },
        body: in0
      }
    },
    "/cookies": {
      "GET": (req) -> do {
        {
          responseStatus: 200,
          body: {
            (cookies: req.headers.Cookie!) if (req.headers.Cookie?)
          }
        }
      }
    },
    "/cookies/set": {
      "GET": (req: HttpServerRequest) -> do {
        var queryParams = req.queryParams
        var headers = queryParams mapObject ((value, key, index) -> { "$(SET_COOKIE_HEADER)": "$(key)=$(value);Path=/"})
        ---
        {
          responseStatus: 200,
          headers: headers
        }
      }
    }
  })

---
{
  a: do {
    var response = get( 'http://$LOCALHOST/echo')
    ---
    {
      statusText: response.statusText,
      status: response.status,
      contentType: response.contentType
    }
  },
  // validate urlEncode in invalid uri characters
  b: do {
    var response = get('http://$LOCALHOST/echo/anything/%7Basdasd%7D?a')
    var body = response.body
    ---
    {
      status : response.status,
      statusText: response.statusText,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
        headers: (body.headers as Object) - "host"
      },
      cookies: response.cookies,
      contentType: response.contentType,
    }
  },
  c: do {
    var response = get( 'http://$LOCALHOST/bytes')
    ---
    {
      statusText: response.statusText,
      status : response.status,
      contentType: response.contentType,
      headers: response.headers
    }
  },
  e: do {
    var response = post( 'http://$LOCALHOST/echo', { "Content-Type": "multipart/form-data"},form([
      field('field', 'value'),
      field({name: 'field2', value:'value2'}),
      file({name: 'fileX', path: 'MyApi.dwl' }),
      file('fileY','MyApi.dwl')]))
    var body = response.body
    ---
    {
      mimeType : body.^mimeType,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
      },
      contentType : response.contentType,
    }
  },
  ee: do {
    var response = postMultipart( 'http://$LOCALHOST/echo', form([
      field('field', 'value'),
      field({name: 'field2', value:'value2'}),
      file({name: 'fileX', path: 'MyApi.dwl' }),
      file('fileY','MyApi.dwl')]), { "Content-TYPE": "multipart/form-data"})
    var body = response.body
    ---
    {
      mimeType: body.^mimeType,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
      },
      contentType: response.contentType
    }
  },
  eee: do {
    var response = postMultipart( 'http://$LOCALHOST/echo', form([
      field('field', 'value'),
      field({name: 'field2', value:'value2'}),
      file({name: 'fileX', path: 'MyApi.dwl' }),
      file('fileY','MyApi.dwl')]))
    var body = response.body
    ---
    {
      mimeType: body.^mimeType,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
      },
      contentType: response.contentType
    }
  },
  f: do {
    var response = post(`http://$LOCALHOST/echo?asd=$(123)&space=$("Mariano de Achaval")`)
    var body = response.body
    ---
    {
      mimeType: body.^mimeType,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
        headers: (body.headers as Object) - "host"
      },
      contentType: response.contentType,
    }
  },
  g: do {
    var response = post({url: "http://$LOCALHOST/echo", queryParams: {asd: "123", space: "Mariano de Achaval"}})
    var body = response.body
    ---
    {
      mimeType: response.body.^mimeType,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
        headers: (body.headers as Object) - "host"
      },
      contentType: response.contentType,
    }
  },
  h: do {
    var response = sendRequest({ method: "POST", url: "http://$LOCALHOST/echo", queryParams: { asd: "123", space: "Mariano de Achaval" }})
    ---
    {
      contentType: response.contentType,
      bodyIsBinary: response.body is Binary,
      mimeType: response.body.^mimeType,
      raw: response.body.^raw
    }
  },
  i: do {
    var response = get({url: 'http://$LOCALHOST/cookies/set', queryParams: { k1: "v1"}})
    ---
    {
      k1: {
        name: response.cookies.k1.name,
        value: response.cookies.k1.value,
        maxAge: response.cookies.k1.maxAge,
        httpOnly: response.cookies.k1.httpOnly,
        secure: response.cookies.k1.secure,
        domain: response.cookies.k1.domain,
        comment: response.cookies.k1.comment,
        path: response.cookies.k1.path
      }
    }
  },
  ii: do {
    var response = get('http://$LOCALHOST/cookies')
    ---
    {
      cookies: response.cookies,
      body: {
        cookies: response.body.cookies
      }
    }
  },
  iii: do {
    var response = get('http://$LOCALHOST/cookies', { Cookie: "Token=_asdf;age=39" })
    ---
    {
      cookies: response.cookies,
      body: {
        cookies: response.body.cookies
      }
    }
  },
  j: do {
    var response = sendRequestAndReadResponse( { method: "GET", url: 'http://$LOCALHOST/cookies', cookies: {Token: "_asdf", age: "39"}})
    ---
    {
      cookies: response.cookies,
      body: {
        cookies: response.body.cookies
      }
    }
  },
  z: server.stop()
}