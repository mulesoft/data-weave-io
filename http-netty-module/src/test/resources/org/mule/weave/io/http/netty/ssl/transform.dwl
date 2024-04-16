 %dw 2.0
import * from dw::io::http::Client



var result = [

//  request('GET', 'https://tls-v1-0.badssl.com:1010/'),
//  request('GET', 'https://tls-v1-1.badssl.com:1011/'),

  // Test against HTTP2 url
  get('https://www.cloudflare.com/'),

  // our server has a weird https behavior.
  get('https://anypoint.mulesoft.com/accounts/me'),

  post('https://anypoint.mulesoft.com/accounts/login',  { user: 'data-weave', password: 'data-weave' }),

  request({ method: 'GET', url: 'https://github.com/'}),

  request({ method: 'GET', url: 'https://google.com/'})
]

---
result map ((item, index) -> {
  status: item.status,
  statusText: item.statusText
})