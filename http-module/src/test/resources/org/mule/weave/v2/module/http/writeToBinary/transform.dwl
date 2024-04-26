%dw 2.0
import * from dw::io::http::BodyUtils
import * from dw::io::http::Client
import * from dw::module::Multipart

output application/json

var multipartForm = form([field('field', 'value'),
  field({name: 'field2', value:'value2'})])
---
{
  json: writeToBinary({name: "Mariano", lastname: "Lischetti"}, DEFAULT_SERIALIZATION_CONFIG.contentType, DEFAULT_SERIALIZATION_CONFIG.writerProperties),
  xml: writeToBinary(root: {name: "Mariano", lastname: "Lischetti"}, "application/xml", {}),
  multipart: writeToBinary(multipartForm, "multipart/form-data", { boundary: "boundary"}),
  multipartBoundaryContentType: writeToBinary(multipartForm, "multipart/form-data;boundary=my-boundary"),
  multipartGeneratedBoundary: do {
    var binaryBody = writeToBinary(multipartForm, "multipart/form-data")
    ---
    {
      hasBody: binaryBody.body != null,
      mime: {
        `type`: binaryBody.mime."type",
        subtype: binaryBody.mime.subtype,
        hasBoundary: binaryBody.mime.parameters["boundary"] != null
      }
    }
  }
}