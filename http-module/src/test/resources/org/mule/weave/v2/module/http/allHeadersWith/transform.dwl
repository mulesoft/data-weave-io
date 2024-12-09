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
  a: allHeadersWith(headers, CONTENT_TYPE_HEADER),
  b: headers allHeadersWith 'Cache-Control',
  c: allHeadersWith(headers, 'duplicated'),
  d: allHeadersWith(headers, 'non-exists'),
  e: allHeadersWith({}, CONTENT_TYPE_HEADER),
  f: allHeadersWith(null, CONTENT_TYPE_HEADER),
}
