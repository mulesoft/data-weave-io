#### _dw::io::http::Types_
__________________________________________

This module has the types being used for Server and Client

# Index




### Types
| Name | Description|
|------|------------|
|[HttpBody](#httpbody-index ) | |
|[HttpCookies](#httpcookies-index ) | |
|[HttpHeaders](#httpheaders-index ) | |
|[HttpMethod](#httpmethod-index ) | |
|[QueryParams](#queryparams-index ) | |







__________________________________________





__________________________________________

# Types

### **HttpBody** [↑↑](#index )




#### Definition

```dataweave
Any
```


### **HttpCookies** [↑↑](#index )




#### Definition

```dataweave
{ _?: SimpleType }
```


### **HttpHeaders** [↑↑](#index )




#### Definition

```dataweave
{ "Content-Type"?: String, Authorization?: String, ETag?: SimpleType, Cookie*?: String, _?: SimpleType }
```


### **HttpMethod** [↑↑](#index )




#### Definition

```dataweave
"GET" | "HEAD" | "POST" | "PUT" | "DELETE" | "CONNECT" | "OPTIONS" | "TRACE" | "PATCH"
```


### **QueryParams** [↑↑](#index )




#### Definition

```dataweave
{ _?: String }
```




