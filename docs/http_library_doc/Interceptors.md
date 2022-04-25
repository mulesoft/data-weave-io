#### _dw::io::http::Interceptors_
__________________________________________



# Index

### Functions
| Name | Description|
|------|------------|
| [CORS](#cors-index ) | |
| [isOptions](#isoptions-index ) | |









__________________________________________


# Functions

## **CORS** [↑↑](#index )

### _CORS(allowOrigin: Array<String> | "SameOrigin" = ["*"], allowMethods: Array<String> = ["POST", "GET", "OPTIONS"], allowHTTPHeaders: Array<String> = ["*"], exposeHeaders: Array<String> = [""], maxAge: Number = -1, allowCredentials: Boolean = false)_


__________________________________________

### _CORS(allowOrigin: (HttpServerRequest) -> Array<String>, allowMethods: (HttpServerRequest) -> Array<String>, allowHTTPHeaders: (HttpServerRequest) -> Array<String>, exposeHeaders: (HttpServerRequest) -> Array<String>, maxAge: Number = -1, allowCredentials: Boolean = false)_


__________________________________________


## **isOptions** [↑↑](#index )

### _isOptions(request: HttpServerRequest)_


__________________________________________






