%dw 2.0
import * from dw::io::http::Client

output application/json

var config = { connectionTimeout: 2000 }
---
{
  a: do {
    var i = identifiableHttpClientConfig(config, "my-prefix")
    ---
    { connectionTimeout : i.connectionTimeout, id: i.id startsWith "my-prefix" }
  },
  b: do {
    var i = identifiableHttpClientConfig({})
    ---
    { id: i.id startsWith "CUSTOM" }
  }
}