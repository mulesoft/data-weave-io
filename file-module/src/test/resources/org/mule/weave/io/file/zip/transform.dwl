%dw 2.0
import * from dw::io::file::FileSystem
output application/json
var folder2 = [path(tmp(),"dw_io_test")] zipInto path(tmp(),"outputZip.zip")
---
{
  zip : {
   exists: exists(folder2),
   extension: extensionOf(folder2),
  }
}