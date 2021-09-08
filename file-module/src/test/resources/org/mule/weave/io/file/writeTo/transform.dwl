%dw 2.0
import * from dw::io::file::FileSystem
output application/json

var thePath = path(path(tmp(),"dw_my_test"),"$(uuid()).tmp")
var written = copyTo( "Hello" as Binary {encoding: "UTF-8"},thePath)
var content = contentOf(thePath) as String {encoding: "UTF-8"}
---
{
  a: written,
  b: content
}