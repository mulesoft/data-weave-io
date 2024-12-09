%dw 2.0

import * from dw::io::http::Client
import * from dw::io::http::Server

output application/json

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort(), normalizeResponseHeaders: false}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/post": {
      "POST": (req: HttpServerRequest) -> do {
        {
          responseStatus: 200,
          body: req
        }
      }
    }
  })

---
{
  a: do {
    var response = post(`http://$LOCALHOST/post?asd=$(123)&space=$("Mariano de Achaval")`)
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
  b: do {
    var response = post({url: "http://$LOCALHOST/post", queryParams: {asd: "123", space: "Mariano de Achaval"}})
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
  c: do {
    var response = sendRequest({ method: "POST", url: "http://$LOCALHOST/post", queryParams: { asd: "123", space: "Mariano de Achaval" }})
    ---
    {
      contentType: response.contentType,
      bodyIsBinary: response.body is Binary,
      mimeType: response.body.^mimeType,
      raw: response.body.^raw
    }
  },
  d: do {
    var request = {
      method: "POST",
      url: {
        url: "http://$LOCALHOST/post",
        queryParams: {
          asd: "123",
          space: "Mariano de Achaval",
          a: "a",
          A: "A"
        }
      },
      headers: {
        "CONTENT-Type": "application/xml",
        "X-API-TOKEN": "1st Token",
        "X-Api-Token": "2nd Token"
      },
      cookies: {
        "Cookie1": "A",
        "cookie1": "b"
      },
      body: {
        root: {
          child: "Hi"
        }
      }
    }
    var response = sendRequestAndReadResponse(request)
    var body = response.body
    ---
    {
      status: response.status,
      headers: response.headers,
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
  z: server.stop()
}