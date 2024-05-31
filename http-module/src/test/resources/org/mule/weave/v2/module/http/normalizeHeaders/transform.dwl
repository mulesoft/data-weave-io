%dw 2.0
output application/json

import * from dw::io::http::BodyUtils
---
normalizeHeaders({
    "Authorization": "authorization value",
    "Content-Type": "application/xml",
    "cache-control": "no-cache",
    "Accept-ENCODING": "gzip",
    "Set-Cookie": "value",
    "x-uow": "uow"})
