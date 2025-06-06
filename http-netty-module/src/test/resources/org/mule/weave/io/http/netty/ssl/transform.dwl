 %dw 2.0
import * from dw::io::http::Client

var testRequestConfig = {
                         followRedirects: false,
                         readTimeout: 60000,
                         requestTimeout: 60000,
                         streamResponse: false,
                         enableMetrics: true
                       }

var result = [

//  request('GET', 'https://tls-v1-0.badssl.com:1010/'),
//  request('GET', 'https://tls-v1-1.badssl.com:1011/'),

  // Test against HTTP2 url
  get('https://www.cloudflare.com/', {}, testRequestConfig),

  // our server has a weird https behavior.
  get('https://anypoint.mulesoft.com/accounts/me', {}, testRequestConfig),

  post('https://anypoint.mulesoft.com/accounts/login', {}, { user: 'data-weave', password: 'data-weave' },testRequestConfig),

  sendRequestAndReadResponse({ method: 'GET', url: 'https://github.com/'}, testRequestConfig),

  sendRequestAndReadResponse({ method: 'GET', url: 'https://google.com/'}, testRequestConfig)
]

---
result map ((item, index) -> do {
  var schema = item.^
  var timers = schema.timers
  ---
  {
    status: item.status,
    statusText: item.statusText,
    timers: {
      (dns: timers.dns >= 0) if (timers.dns?),
      connect: timers.connect >= 0,
      (tls: timers.tls >= 0) if (timers.tls?),
      send: timers.send >= 0,
      wait: timers.wait >= 0,
      receive: timers.receive >= 0,
      total: timers.total >= 0
    }
  }
})