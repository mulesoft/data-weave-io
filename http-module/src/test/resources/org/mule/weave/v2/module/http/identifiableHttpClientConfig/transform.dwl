%dw 2.0
import * from dw::io::http::Client

output application/json

var config = {connectionTimeout: 2000}
---
{
  a: identifiableHttpClientConfig(config, "my-prefix"),
  b: identifiableHttpClientConfig({})
}