 %dw 2.0
import * from dw::io::http::Client



var result = [

//  request('GET', 'https://tls-v1-0.badssl.com:1010/'),
//  request('GET', 'https://tls-v1-1.badssl.com:1011/'),

  // Test against HTTP2 url
  get('https://www.cloudflare.com/'),

  // our server has a weird https behavior.
  get('https://anypoint.mulesoft.com/accounts/me'),

  post('https://anypoint.mulesoft.com/accounts/login', {}, { user: 'data-weave', password: 'data-weave' }),

  sendRequestAndReadResponse({ method: 'GET', url: 'https://github.com/'}),

  sendRequestAndReadResponse({ method: 'GET', url: 'https://google.com/'})
]

---
result map ((item, index) -> do {
  var schema = item.^
  var timers = schema.timers
  ---
  {
    status: item.status,
    statusText: item.statusText,
    total: schema.total is Number,
    timers: {
      dns: timers.dns is Number,
      connect: timers.connect is Number,
      tls: timers.tls is Number,
      send: timers.send is Number,
      wait: timers.wait is Number,
      receive: timers.receive is Number,
      total: timers.total is Number
    }
  }
})