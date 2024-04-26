%dw 2.0
import * from dw::io::http::BodyUtils
import try from dw::Runtime

output application/json
---
{
  "unknownContentType": try(() -> writeToBinary(root: {name: "Mariano", lastname: "Lischetti"}, "*/*", {}))
    then {
      kind: $.error.kind,
      message: $.error.message contains "Unable to find data format for: */*"
    }
}