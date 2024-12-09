%dw 2.0
output application/json
import * from dw::io::http::utils::HttpHeaders
---
{
  a: normalizeHeader("Authorization"),
  b: normalizeHeader("Content-Type"),
  c: normalizeHeader("cache-control"),
  d: normalizeHeader("Accept-ENCODING"),
  e: normalizeHeader("Set-Cookie"),
  f: normalizeHeader("x-uow")
}