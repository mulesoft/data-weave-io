%dw 2.0
import * from dw::io::http::BodyUtils

output application/json
---
writeToBinary(root: {name: "Mariano", lastname: "Lischetti"}, "application/xml", {})