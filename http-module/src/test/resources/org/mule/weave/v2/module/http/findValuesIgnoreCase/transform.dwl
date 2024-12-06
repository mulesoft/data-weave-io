%dw 2.0
output application/json

import * from dw::io::http::utils::HttpHeaders

var headers = {
  "Authorization": "authorization value",
  "Content-Type": "application/xml",
  "cache-control": "no-cache",
  "Accept-ENCODING": "gzip",
  "Set-Cookie": "value",
  "x-uow": "uow",
  "Duplicated": "A",
  "DUPLICATED": "B"
 }
---
{
  a: findValuesIgnoreCase(headers, CONTENT_TYPE_HEADER),
  b: findValuesIgnoreCase(headers, 'Cache-Control'),
  c: findValuesIgnoreCase(headers, 'duplicated'),
  d: findValuesIgnoreCase(headers, 'non-exists'),
  e: findValuesIgnoreCase({}, CONTENT_TYPE_HEADER),
  f: findValuesIgnoreCase(null, CONTENT_TYPE_HEADER),
}
