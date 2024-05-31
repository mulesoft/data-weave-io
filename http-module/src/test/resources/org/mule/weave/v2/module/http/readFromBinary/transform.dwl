%dw 2.0
import * from dw::io::http::BodyUtils
import * from dw::module::Mime

output application/json

var JSON = { 'type': "application", subtype: 'json', parameters: {}}
var XML = { 'type': "application", subtype: 'xml', parameters: {}}
---
{
 pets: readFromBinary(JSON, in0, {}).pets,
 user: readFromBinary(XML, in1, {}).user
}