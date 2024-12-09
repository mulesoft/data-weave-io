%dw 2.0
import * from dw::io::http::utils::HttpHeaders

var headers = {
  'content-type': "application/json",
  'Content-Length': "128",
  'Age': "15",
  'Duplicated': "ValueA",
  'Duplicated': "ValueB"
}
---
{
  a: updateHeaderValueIgnoreCase(headers, CONTENT_TYPE_HEADER, "application/xml"),
  b: updateHeaderValueIgnoreCase(headers, "missingHeader", "newValue"),
  c: updateHeaderValueIgnoreCase({}, CONTENT_TYPE_HEADER, "application/json"),
  d: updateHeaderValueIgnoreCase(headers, "Duplicated", "ValueC")
}