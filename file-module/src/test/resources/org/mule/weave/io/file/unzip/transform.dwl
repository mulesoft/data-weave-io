%dw 2.0
import * from dw::io::file::FileSystem
output application/json
var folder2 = unzip(path(tmp(),"dw_io_test", "zips/test.zip"),path(tmp(),"dw_io_test" ,"outputZip"))
---
{
  unzip : ls(tmp(), "outputZip") map ((item, index) -> baseNameOf(item)),
}