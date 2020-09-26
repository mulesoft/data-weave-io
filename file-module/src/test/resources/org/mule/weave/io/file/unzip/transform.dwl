%dw 2.0
import * from dw::io::file::FileSystem
output application/json
var fileToUnzip = log(path(tmp(),"dw_io_test", "zips/test.zip"))
var folder2 = unzip(fileToUnzip,path(tmp(),"dw_io_test" ,"outputZip"))
---
{
  fileToUnzip: exists(fileToUnzip),
  unzip : tree(log(folder2)) map ((item, index) -> nameOf(item)),
}