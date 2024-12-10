%dw 2.0
import * from dw::io::http::utils::HttpHeaders

var headers = {
  'content-type': "application/json",
  'Content-Length': "128",
  'Age': "15",
  'Duplicated': "ValueA",
  'DUPLICATED': "ValueB"
}
---
{
  a: withHeader(headers, { name: CONTENT_TYPE_HEADER, value: "application/xml"}),
  b: headers withHeader { name: "missingHeader", value: "newValue" },
  c: withHeader({}, { name: CONTENT_TYPE_HEADER, value: "application/json"}),
  d: withHeader(headers, { name: "Duplicated", value: "ValueC"})
}