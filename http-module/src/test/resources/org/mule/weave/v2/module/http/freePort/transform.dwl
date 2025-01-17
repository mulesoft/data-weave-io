%dw 2.0
output application/json

import * from dw::io::http::utils::Port

var port = freePort()
---
{
  isNumber: port is Number,
  isBiggerThanZero: port > 0
}