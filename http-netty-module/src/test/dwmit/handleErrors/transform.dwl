%dw 2.0
import * from dw::io::http::Client
import try from dw::Runtime
---
{
  a: try(() -> GET('absolute/url')),
  b: try(() -> GET('http://unknown-host/path')),
  c: try(() -> GET( 'http://127.0.0.1:32191/url')),
}