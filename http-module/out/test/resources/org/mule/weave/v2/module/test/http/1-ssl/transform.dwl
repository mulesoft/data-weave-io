%dw 2.0
import * from dw::io::http::Client
import form, field from dw::module::Multipart
import HttpClientResult from dw::io::http::Types

output application/java

var mapResult = ['sha256', 'sha384', 'sha512', 'rsa2048', 'ecc256', 'ecc384'] map (
                  request('GET', 'https://$.badssl.com/')
                )

var result = [
  // TEST weird https behavior
  request('GET', 'https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1', { allowUnsafeSSL: true }),

  request('GET', 'https://tls-v1-0.badssl.com:1010/'),
  request('GET', 'https://tls-v1-1.badssl.com:1011/'),

  request('GET', 'https://self-signed.badssl.com/', { allowUnsafeSSL: true }),



  // Test against HTTP2 url
  request('GET', 'https://www.cloudflare.com/'),

  // our server has a weird https behavior.
  request('GET', 'https://anypoint.mulesoft.com/accounts/me'),
  request('POST', 'https://anypoint.mulesoft.com/accounts/login', { body: {user: 'data-weave', password: 'data-weave'} }),

  request('GET', 'https://github.com/'),
  request('GET', 'https://google.com/')
] ++ mapResult

---
result reduce (a, b = false) -> (a.err or b)