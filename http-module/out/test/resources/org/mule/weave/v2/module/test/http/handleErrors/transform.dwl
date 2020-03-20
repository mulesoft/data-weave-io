%dw 2.0
import * from dw::http::Client
import mustEqual from dw::core::Assertions
---
[
  (request('GET', 'absolute/url')).err mustEqual true,
  (request('GET', 'http://unknown-host/path')).message mustEqual "Cannot resolve host: unknown-host",
  (request('GET', 'http://httpbin.org/anything/{asdasd}')).message mustEqual "Invalid URI, Illegal character in path at index 28: http://httpbin.org/anything/{asdasd}",
  (request('GET', 'http://localhost:32191/url')).message mustEqual "Connection refused",
]