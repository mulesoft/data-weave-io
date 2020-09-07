%dw 2.0
import * from dw::io::http::Client
import try from dw::Runtime

fun then<A, V>(result: A, assertions: (result: A) -> V): V = assertions(result)
---
{
  a: try(() -> GET('absolute/url')),
  b: try(() -> GET('http://unknown-host/path')) then {
    message: $.error.message contains  "Unable to connect to http://unknown-host/path",
    stack: $.error.stack,
    kind: $.error.kind
  },
  c: try(() -> GET( 'http://127.0.0.1:32191/url')),
}