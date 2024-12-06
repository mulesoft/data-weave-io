%dw 2.0
output application/json
import * from dw::io::http::utils::HttpHeaders
---
{
  a: formatHeader("Authorization"),
  b: formatHeader("Content-Type"),
  c: formatHeader("cache-control"),
  d: formatHeader("Accept-ENCODING"),
  e: formatHeader("Set-Cookie"),
  f: formatHeader("x-uow")
}