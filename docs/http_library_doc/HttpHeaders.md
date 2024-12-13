#### _dw::io::http::utils::HttpHeaders_
__________________________________________

Module with the list of well known HTTP headers

To use this module, you must import it to your DataWeave code, for example,
by adding the line `import * from dw::io::http::utils::HttpHeaders` to the header of your
DataWeave script.

# Index

### Functions
| Name | Description|
|------|------------|
| [allHeadersWith](#allheaderswith ) | Gets an `Array` of `HttpHeader` for a given HTTP header name ignoring case.|
| [normalizeHeader](#normalizeheader ) | Formats the given HTTP header value with the following rules:<br>- The first char of every word is in upper case and the remaining chars are in lower case.|
| [normalizeHeaders](#normalizeheaders ) | Normalize the name of the given `HttpHeaders` value following the `normalizeHeader` function rules.|
| [withHeader](#withheader ) | Set an specific HTTP header to a set of `HttpHeaders`.|


### Variables
| Name | Description|
|------|------------|
| [ACCEPT_CHARSET_HEADER](#accept_charset_header ) | |
| [ACCEPT_ENCODING_HEADER](#accept_encoding_header ) | |
| [ACCEPT_HEADER](#accept_header ) | |
| [ACCEPT_LANGUAGE_HEADER](#accept_language_header ) | |
| [ACCEPT_RANGES_HEADER](#accept_ranges_header ) | |
| [ACCESS_CONTROL_ALLOW_HEADERS](#access_control_allow_headers ) | |
| [ACCESS_CONTROL_ALLOW_METHODS](#access_control_allow_methods ) | |
| [ACCESS_CONTROL_ALLOW_ORIGIN](#access_control_allow_origin ) | |
| [ACCESS_CONTROL_MAX_AGE](#access_control_max_age ) | |
| [AGE_HEADER](#age_header ) | |
| [ALLOW_HEADER](#allow_header ) | |
| [AUTHENTICATION_INFO_HEADER](#authentication_info_header ) | |
| [AUTHORIZATION_HEADER](#authorization_header ) | |
| [CACHE_CONTROL_HEADER](#cache_control_header ) | |
| [CONNECTION_HEADER](#connection_header ) | |
| [CONTENT_DISPOSITION_HEADER](#content_disposition_header ) | |
| [CONTENT_ENCODING_HEADER](#content_encoding_header ) | |
| [CONTENT_LANGUAGE_HEADER](#content_language_header ) | |
| [CONTENT_LENGTH_HEADER](#content_length_header ) | |
| [CONTENT_LOCATION_HEADER](#content_location_header ) | |
| [CONTENT_MD5_HEADER](#content_md5_header ) | |
| [CONTENT_RANGE_HEADER](#content_range_header ) | |
| [CONTENT_TYPE_HEADER](#content_type_header ) | |
| [COOKIE2_HEADER](#cookie2_header ) | |
| [COOKIE_HEADER](#cookie_header ) | |
| [DATE_HEADER](#date_header ) | |
| [ETAG_HEADER](#etag_header ) | |
| [EXPECT_HEADER](#expect_header ) | |
| [EXPIRES_HEADER](#expires_header ) | |
| [FROM_HEADER](#from_header ) | |
| [HOST_HEADER](#host_header ) | |
| [IF_MATCH_HEADER](#if_match_header ) | |
| [IF_MODIFIED_SINCE_HEADER](#if_modified_since_header ) | |
| [IF_NONE_MATCH_HEADER](#if_none_match_header ) | |
| [IF_RANGE_HEADER](#if_range_header ) | |
| [IF_UNMODIFIED_SINCE_HEADER](#if_unmodified_since_header ) | |
| [LAST_MODIFIED_HEADER](#last_modified_header ) | |
| [LOCATION_HEADER](#location_header ) | |
| [MAX_FORWARDS_HEADER](#max_forwards_header ) | |
| [ORIGIN_HEADER](#origin_header ) | |
| [PRAGMA_HEADER](#pragma_header ) | |
| [PROXY_AUTHENTICATE_HEADER](#proxy_authenticate_header ) | |
| [PROXY_AUTHORIZATION_HEADER](#proxy_authorization_header ) | |
| [RANGE_HEADER](#range_header ) | |
| [REFERER_HEADER](#referer_header ) | |
| [REFRESH_HEADER](#refresh_header ) | |
| [RETRY_AFTER_HEADER](#retry_after_header ) | |
| [SEC_WEB_SOCKET_ACCEPT_HEADER](#sec_web_socket_accept_header ) | |
| [SEC_WEB_SOCKET_EXTENSIONS_HEADER](#sec_web_socket_extensions_header ) | |
| [SEC_WEB_SOCKET_KEY1_HEADER](#sec_web_socket_key1_header ) | |
| [SEC_WEB_SOCKET_KEY2_HEADER](#sec_web_socket_key2_header ) | |
| [SEC_WEB_SOCKET_KEY_HEADER](#sec_web_socket_key_header ) | |
| [SEC_WEB_SOCKET_LOCATION_HEADER](#sec_web_socket_location_header ) | |
| [SEC_WEB_SOCKET_ORIGIN_HEADER](#sec_web_socket_origin_header ) | |
| [SEC_WEB_SOCKET_PROTOCOL_HEADER](#sec_web_socket_protocol_header ) | |
| [SEC_WEB_SOCKET_VERSION_HEADER](#sec_web_socket_version_header ) | |
| [SERVER_HEADER](#server_header ) | |
| [SERVLET_ENGINE_HEADER](#servlet_engine_header ) | |
| [SET_COOKIE2_HEADER](#set_cookie2_header ) | |
| [SET_COOKIE_HEADER](#set_cookie_header ) | |
| [SSL_CIPHER_HEADER](#ssl_cipher_header ) | |
| [SSL_CIPHER_USEKEYSIZE_HEADER](#ssl_cipher_usekeysize_header ) | |
| [SSL_CLIENT_CERT_HEADER](#ssl_client_cert_header ) | |
| [SSL_SESSION_ID_HEADER](#ssl_session_id_header ) | |
| [STATUS_HEADER](#status_header ) | |
| [STRICT_TRANSPORT_SECURITY_HEADER](#strict_transport_security_header ) | |
| [TE_HEADER](#te_header ) | |
| [TRAILER_HEADER](#trailer_header ) | |
| [TRANSFER_ENCODING_HEADER](#transfer_encoding_header ) | |
| [UPGRADE_HEADER](#upgrade_header ) | |
| [USER_AGENT_HEADER](#user_agent_header ) | |
| [VARY_HEADER](#vary_header ) | |
| [VIA_HEADER](#via_header ) | |
| [WARNING_HEADER](#warning_header ) | |
| [WWW_AUTHENTICATE_HEADER](#www_authenticate_header ) | |
| [X_DISABLE_PUSH_HEADER](#x_disable_push_header ) | |
| [X_FORWARDED_FOR_HEADER](#x_forwarded_for_header ) | |
| [X_FORWARDED_HOST_HEADER](#x_forwarded_host_header ) | |
| [X_FORWARDED_PORT_HEADER](#x_forwarded_port_header ) | |
| [X_FORWARDED_PROTO_HEADER](#x_forwarded_proto_header ) | |
| [X_FORWARDED_SERVER_HEADER](#x_forwarded_server_header ) | |



### Types
| Name | Description|
|------|------------|
|[HttpHeaderEntry](#httpheaderentry ) | DataWeave type for representing an HTTP Header entry.<br>Supports the following fields:|






__________________________________________


# Functions

## **allHeadersWith**

### _allHeadersWith&#40;headers: HttpHeaders, name: String&#41;: Array<HttpHeaderEntry&#62;_

Gets an `Array` of `HttpHeader` for a given HTTP header name ignoring case.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| `headers` | `HttpHeaders` | The HTTP headers.|
| `name` | String | The HTTP header name to search.|


##### Example

This example search for the `Content-Type` header.

###### Source

```dataweave
%dw 2.0
output application/json

var headers = {
  'content-type': "application/json",
  'Content-Length': "128",
  'Age': "15"
}
---
allHeaderWith(headers, 'Content-Type')

```

###### Output

```json
[ { "name": "content-type", "value": "application/json" } ]
```

##### Example

This example search for the `Content-Type` header. (Notice that the `Content-Type` header is duplicated)

###### Source

```dataweave
%dw 2.0
output application/json

var headers = {
  'content-type': "application/json",
  'CONTENT-TYPE': "multipart/form-data",
  'Content-Length': "128",
  'Age': "15"
}
---
allHeadersWith(headers, 'content-type')
```

###### Output

```json
[
  { "name": "content-type", "value": "application/json" },
  { "name": "CONTENT-TYPE", "value": "multipart/form-data" }
]
```
__________________________________________

### _allHeadersWith&#40;headers: Null, name: String&#41;: Array<HttpHeaderEntry&#62;_

Helper function of `allHeadersWith` to work with a `null` value.
__________________________________________


## **normalizeHeader**

### _normalizeHeader&#40;header: String&#41;: String_

Formats the given HTTP header value with the following rules:
- The first char of every word is in upper case and the remaining chars are in lower case.


##### Parameters

| Name | Type | Description|
|------|------|------------|
| header | `String` | The header value to format.|


##### Example

This example format several HTTP header values.

###### Source

```dataweave
%dw 2.0
output application/json
import * from dw::io::http::utils::HttpHeaders
---
{
  a: normalizeHeader("Authorization"),
  b: normalizeHeader("Content-Type"),
  c: normalizeHeader("cache-control"),
  d: normalizeHeader("Accept-ENCODING"),
  e: normalizeHeader("Set-Cookie"),
  f: normalizeHeader("x-uow")
}
```

###### Output

```json
{
  "a": "Authorization",
  "b": "Content-Type",
  "c": "Cache-Control",
  "d": "Accept-Encoding",
  "e": "Set-Cookie",
  "f": "X-Uow"
}
```
__________________________________________


## **normalizeHeaders**

### _normalizeHeaders&#40;headers: HttpHeaders&#41;: HttpHeaders_

Normalize the name of the given `HttpHeaders` value following the `normalizeHeader` function rules.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| headers | `HttpHeaders` | The HTTP header value to normalize.|


##### Example

This example normalize several HTTP header values.

###### Source

```dataweave
%dw 2.0
output application/json
import * from dw::io::http::utils::HttpHeaders
---
normalizeHeaders({
  "Authorization": "authorization value",
  "Content-Type": "application/xml",
  "cache-control": "no-cache",
  "Accept-ENCODING": "gzip",
  "Set-Cookie": "value",
  "x-uow": "uow"})
```

###### Output

```json
{
  "Authorization": "authorization value",
  "Content-Type": "application/xml",
  "Cache-Control": "no-cache",
  "Accept-Encoding": "gzip",
  "Set-Cookie": "value",
  "X-Uow": "uow"
}
```
__________________________________________

### _normalizeHeaders<H <: HttpHeaders&#62;&#40;headers: Null&#41;: { _?: SimpleType }_

Helper function of `normalizeHeaders` to work with a `null` value.
__________________________________________


## **withHeader**

### _withHeader&#40;headers: HttpHeaders, header: HttpHeaderEntry&#41;: HttpHeaders_

Set an specific HTTP header to a set of `HttpHeaders`.

##### Parameters

| Name | Type | Description|
|------|------|------------|
| `headers` | `HttpHeaders` | The HTTP headers.|
| `header` | `HttpHeader` | The HTTP header to set.|


##### Example

This example update the `Content-Type` header.

###### Source

```dataweave
%dw 2.0
output application/json

var headers = {
  'content-type': "application/json",
  'Content-Length': "128",
  'Age': "15"
}
---
withHeader(headers, { "name": "Content-Type", "value": "application/xml" })

```

###### Output

```json
{
  "Content-Length": "128",
  "Age": "15",
  "Content-Type": "application/xml"
 }
```
__________________________________________




# Variables

## **ACCEPT_CHARSET_HEADER**




## **ACCEPT_ENCODING_HEADER**




## **ACCEPT_HEADER**




## **ACCEPT_LANGUAGE_HEADER**




## **ACCEPT_RANGES_HEADER**




## **ACCESS_CONTROL_ALLOW_HEADERS**




## **ACCESS_CONTROL_ALLOW_METHODS**




## **ACCESS_CONTROL_ALLOW_ORIGIN**




## **ACCESS_CONTROL_MAX_AGE**




## **AGE_HEADER**




## **ALLOW_HEADER**




## **AUTHENTICATION_INFO_HEADER**




## **AUTHORIZATION_HEADER**




## **CACHE_CONTROL_HEADER**




## **CONNECTION_HEADER**




## **CONTENT_DISPOSITION_HEADER**




## **CONTENT_ENCODING_HEADER**




## **CONTENT_LANGUAGE_HEADER**




## **CONTENT_LENGTH_HEADER**




## **CONTENT_LOCATION_HEADER**




## **CONTENT_MD5_HEADER**




## **CONTENT_RANGE_HEADER**




## **CONTENT_TYPE_HEADER**




## **COOKIE2_HEADER**




## **COOKIE_HEADER**




## **DATE_HEADER**




## **ETAG_HEADER**




## **EXPECT_HEADER**




## **EXPIRES_HEADER**




## **FROM_HEADER**




## **HOST_HEADER**




## **IF_MATCH_HEADER**




## **IF_MODIFIED_SINCE_HEADER**




## **IF_NONE_MATCH_HEADER**




## **IF_RANGE_HEADER**




## **IF_UNMODIFIED_SINCE_HEADER**




## **LAST_MODIFIED_HEADER**




## **LOCATION_HEADER**




## **MAX_FORWARDS_HEADER**




## **ORIGIN_HEADER**




## **PRAGMA_HEADER**




## **PROXY_AUTHENTICATE_HEADER**




## **PROXY_AUTHORIZATION_HEADER**




## **RANGE_HEADER**




## **REFERER_HEADER**




## **REFRESH_HEADER**




## **RETRY_AFTER_HEADER**




## **SEC_WEB_SOCKET_ACCEPT_HEADER**




## **SEC_WEB_SOCKET_EXTENSIONS_HEADER**




## **SEC_WEB_SOCKET_KEY1_HEADER**




## **SEC_WEB_SOCKET_KEY2_HEADER**




## **SEC_WEB_SOCKET_KEY_HEADER**




## **SEC_WEB_SOCKET_LOCATION_HEADER**




## **SEC_WEB_SOCKET_ORIGIN_HEADER**




## **SEC_WEB_SOCKET_PROTOCOL_HEADER**




## **SEC_WEB_SOCKET_VERSION_HEADER**




## **SERVER_HEADER**




## **SERVLET_ENGINE_HEADER**




## **SET_COOKIE2_HEADER**




## **SET_COOKIE_HEADER**




## **SSL_CIPHER_HEADER**




## **SSL_CIPHER_USEKEYSIZE_HEADER**




## **SSL_CLIENT_CERT_HEADER**




## **SSL_SESSION_ID_HEADER**




## **STATUS_HEADER**




## **STRICT_TRANSPORT_SECURITY_HEADER**




## **TE_HEADER**




## **TRAILER_HEADER**




## **TRANSFER_ENCODING_HEADER**




## **UPGRADE_HEADER**




## **USER_AGENT_HEADER**




## **VARY_HEADER**




## **VIA_HEADER**




## **WARNING_HEADER**




## **WWW_AUTHENTICATE_HEADER**




## **X_DISABLE_PUSH_HEADER**




## **X_FORWARDED_FOR_HEADER**




## **X_FORWARDED_HOST_HEADER**




## **X_FORWARDED_PORT_HEADER**




## **X_FORWARDED_PROTO_HEADER**




## **X_FORWARDED_SERVER_HEADER**





__________________________________________

# Types

### **HttpHeaderEntry**


DataWeave type for representing an HTTP Header entry.
Supports the following fields:

- `name`: The HTTP header name.
- `value`: The HTTP header value.

#### Definition

```dataweave
{ name: String, value: SimpleType }
```




