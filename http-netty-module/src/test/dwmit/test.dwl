%dw 2.0
import * from dw::io::http::Client
output application/json
var myQ = "hola rtrtrtrtrrt "
---
{
  foo: GET(  url `http:google.com?q=$(myQ)`),
  get: GET `http://google.com?q$(myQ)` with {
    headers: {}
  },
  get2: GET()

}