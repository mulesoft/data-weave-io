%dw 2.0
import * from dw::io::file::FileSystem
output application/json
var folder2 = toUrl( "/tmp/Application Test")
---
{
  toUrl : folder2,
}