#### _dw::io::http::Interceptors_
__________________________________________

This module has http interceptors that are useful when configuring a server

# Index

### Functions
| Name | Description|
|------|------------|
| [CORS](#cors-index ) | Configure CORS intereceptor|
| [isOptions](#isoptions-index ) | |









__________________________________________


# Functions

## **CORS** [↑↑](#index )

### _CORS(allowOrigin: Array<String> | "SameOrigin" = ["*"], allowMethods: Array<String> = ["POST", "GET", "OPTIONS"], allowHTTPHeaders: Array<String> = ["*"], exposeHeaders: Array<String> = [""], maxAge: Number = -1, allowCredentials: Boolean = false)_

Configure CORS intereceptor

##### Parameters

| Name | Type | Description|
|------|------|------------|
| allowOrigin | Array<String&#62; &#124; "SameOrigin" ||
| allowMethods | Array<String&#62; ||
| allowHTTPHeaders | Array<String&#62; ||
| exposeHeaders | Array<String&#62; ||
| maxAge | Number ||
| allowCredentials | Boolean ||

__________________________________________

### _CORS(allowOrigin: (HttpServerRequest) -> Array<String>, allowMethods: (HttpServerRequest) -> Array<String>, allowHTTPHeaders: (HttpServerRequest) -> Array<String>, exposeHeaders: (HttpServerRequest) -> Array<String>, maxAge: Number = -1, allowCredentials: Boolean = false)_


__________________________________________


## **isOptions** [↑↑](#index )

### _isOptions(request: HttpServerRequest)_


__________________________________________






