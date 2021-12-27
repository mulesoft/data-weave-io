#DISCLAIMER

EXPERIMENTAL REPO Use it at your own risk

# Data Weave IO

This repo contains modules that do IO. These modules are for using in DW standalone only. 
And are already embedded in the DW cli.



## File Module

This module provides basic functionality to work with Files. 


For example doing a list over a folder

```dw
%dw 2.0
import * from dw::io::file::FileSystem
---
ls(folder)
```


## Http Module

This module provides basic functionality to work with Http both as client or server.

```dw
%dw 2.0
import * from dw::io::http::Client
---
GET( 'http://httpbin.org/get') 
```

