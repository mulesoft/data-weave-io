%dw 2.0
import * from dw::io::http::Client

output application/json
---
{
 get: createHttpRequest("GET", {url: "url", queryParams: {p1: "value", p2: "true"}}),
 post: createHttpRequest("POST", "url"),
 put: createHttpRequest("PUT", "url", {}, {a: "B"}),
}