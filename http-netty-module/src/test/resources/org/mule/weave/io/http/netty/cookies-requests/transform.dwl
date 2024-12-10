%dw 2.0

import * from dw::io::http::utils::HttpHeaders
import * from dw::io::http::Client
import * from dw::io::http::Server

output application/json

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort(), normalizeResponseHeaders: false}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
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
  b: do {
    var response = get('http://$LOCALHOST/cookies')
    ---
    {
      cookies: response.cookies,
      body: {
        cookies: response.body.cookies
      }
    }
  },
  c: do {
    var response = get('http://$LOCALHOST/cookies', { Cookie: "Token=_asdf;age=39" })
    ---
    {
      cookies: response.cookies,
      body: {
        cookies: response.body.cookies
      }
    }
  },
  d: do {
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