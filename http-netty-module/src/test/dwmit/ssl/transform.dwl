 %dw 2.0
import * from dw::io::http::Client



var result = [

  request('GET', 'https://tls-v1-0.badssl.com:1010/'),
  request('GET', 'https://tls-v1-1.badssl.com:1011/'),

  // Test against HTTP2 url
  request('GET', 'https://www.cloudflare.com/'),

  // our server has a weird https behavior.
  request('GET', 'https://anypoint.mulesoft.com/accounts/me'),
  request('POST', 'https://anypoint.mulesoft.com/accounts/login', { body: {user: 'data-weave', password: 'data-weave'} }),

  request('GET', 'https://github.com/'),
  request('GET', 'https://google.com/')
]

---
result map ((item, index) -> {
  status: item.status,
  statusText: item.statusText
})