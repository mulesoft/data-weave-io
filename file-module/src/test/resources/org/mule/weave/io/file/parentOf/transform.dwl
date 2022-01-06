%dw 2.0
output application/json
import * from dw::io::file::FileSystem
---
{
  parent: parentOf("/tmp/subfolder/testfile.dwl"),
  nullParent: parentOf(""),
  defaultedParent: parentOf("") default "/tmp",
  returnsNullType: parentOf("asd") is Null
}