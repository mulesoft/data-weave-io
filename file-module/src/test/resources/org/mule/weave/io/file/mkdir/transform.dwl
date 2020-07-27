%dw 2.0
import * from dw::io::file::FileSystem
output application/json
var folder = path(tmp(),uuid())
var isDefined = exists(folder)
var created = mkdir(folder)
var kind = kindOf(folder)
var isDefined2 = exists(folder)

---
{
  isDefined: isDefined,
  kind: kind,
  isDefined2: isDefined2
}