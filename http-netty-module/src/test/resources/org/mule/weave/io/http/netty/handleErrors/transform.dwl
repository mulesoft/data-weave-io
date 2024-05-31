%dw 2.0
import * from dw::io::http::Client
import try from dw::Runtime
---
{
  a: try(() -> get('absolute/url')) then {
      message: $.error.message contains  "Unable to connect to http://unknown-host/path",
      stack: sizeOf($.error.stack default []),
      kind: $.error.kind
    },
  b: try(() -> get('http://unknown-host/path')) then {
    message: $.error.message contains  "Unable to connect to http://unknown-host/path",
    stack: sizeOf($.error.stack default []),
    kind: $.error.kind
  },
  c: try(() -> get( 'http://127.0.0.1:32191/url')) then {
       message: $.error.message contains  "Unable to connect to http://unknown-host/path",
       stack: sizeOf($.error.stack default []),
       kind: $.error.kind
     },
}