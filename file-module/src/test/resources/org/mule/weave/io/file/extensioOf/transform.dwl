%dw 2.0
import * from dw::io::file::FileSystem
output application/json

---
{
  a: extensionOf(path("/tmp","foo.txt")),
  b: extensionOf(path("/tmp","foo.html")),
  c: extensionOf(path("/tmp","foo.json")),
  d: extensionOf(tmp()) //Directory should return null
}