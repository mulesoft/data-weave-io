/**
* Module with the list of well known HTTP headers
*
* To use this module, you must import it to your DataWeave code, for example,
* by adding the line `import * from dw::io::http::utils::HttpHeaders` to the header of your
* DataWeave script.
*/
%dw 2.0

var ACCEPT_HEADER = "Accept"
var ACCEPT_CHARSET_HEADER = "Accept-Charset"
var ACCEPT_ENCODING_HEADER = "Accept-Encoding"
var ACCEPT_LANGUAGE_HEADER = "Accept-Language"
var ACCEPT_RANGES_HEADER = "Accept-Ranges"
var AGE_HEADER = "Age"
var ALLOW_HEADER = "Allow"
var AUTHENTICATION_INFO_HEADER = "Authentication-Info"
var AUTHORIZATION_HEADER = "Authorization"
var CACHE_CONTROL_HEADER = "Cache-Control"
var COOKIE_HEADER = "Cookie"
var COOKIE2_HEADER = "Cookie2"
var CONNECTION_HEADER = "Connection"
var CONTENT_DISPOSITION_HEADER = "Content-Disposition"
var CONTENT_ENCODING_HEADER = "Content-Encoding"
var CONTENT_LANGUAGE_HEADER = "Content-Language"
var CONTENT_LENGTH_HEADER = "Content-Length"
var CONTENT_LOCATION_HEADER = "Content-Location"
var CONTENT_MD5_HEADER = "Content-MD5"
var CONTENT_RANGE_HEADER = "Content-Range"
var CONTENT_TYPE_HEADER = "Content-Type"
var DATE_HEADER = "Date"
var ETAG_HEADER = "ETag"
var EXPECT_HEADER = "Expect"
var EXPIRES_HEADER = "Expires"
var FROM_HEADER = "From"
var HOST_HEADER = "Host"
var IF_MATCH_HEADER = "If-Match"
var IF_MODIFIED_SINCE_HEADER = "If-Modified-Since"
var IF_NONE_MATCH_HEADER = "If-None-Match"
var IF_RANGE_HEADER = "If-Range"
var IF_UNMODIFIED_SINCE_HEADER = "If-Unmodified-Since"
var LAST_MODIFIED_HEADER = "Last-Modified"
var LOCATION_HEADER = "Location"
var MAX_FORWARDS_HEADER = "Max-Forwards"
var ORIGIN_HEADER = "Origin"
var PRAGMA_HEADER = "Pragma"
var PROXY_AUTHENTICATE_HEADER = "Proxy-Authenticate"
var PROXY_AUTHORIZATION_HEADER = "Proxy-Authorization"
var RANGE_HEADER = "Range"
var REFERER_HEADER = "Referer"
var REFRESH_HEADER = "Refresh"
var RETRY_AFTER_HEADER = "Retry-After"
var SEC_WEB_SOCKET_ACCEPT_HEADER = "Sec-WebSocket-Accept"
var SEC_WEB_SOCKET_EXTENSIONS_HEADER = "Sec-WebSocket-Extensions"
var SEC_WEB_SOCKET_KEY_HEADER = "Sec-WebSocket-Key"
var SEC_WEB_SOCKET_KEY1_HEADER = "Sec-WebSocket-Key1"
var SEC_WEB_SOCKET_KEY2_HEADER = "Sec-WebSocket-Key2"
var SEC_WEB_SOCKET_LOCATION_HEADER = "Sec-WebSocket-Location"
var SEC_WEB_SOCKET_ORIGIN_HEADER = "Sec-WebSocket-Origin"
var SEC_WEB_SOCKET_PROTOCOL_HEADER = "Sec-WebSocket-Protocol"
var SEC_WEB_SOCKET_VERSION_HEADER = "Sec-WebSocket-Version"
var SERVER_HEADER = "Server"
var SERVLET_ENGINE_HEADER = "Servlet-Engine"
var SET_COOKIE_HEADER = "Set-Cookie"
var SET_COOKIE2_HEADER = "Set-Cookie2"
var SSL_CLIENT_CERT_HEADER = "SSL_CLIENT_CERT"
var SSL_CIPHER_HEADER = "SSL_CIPHER"
var SSL_SESSION_ID_HEADER = "SSL_SESSION_ID"
var SSL_CIPHER_USEKEYSIZE_HEADER = "SSL_CIPHER_USEKEYSIZE"
var STATUS_HEADER = "Status"
var STRICT_TRANSPORT_SECURITY_HEADER = "Strict-Transport-Security"
var TE_HEADER = "TE"
var TRAILER_HEADER = "Trailer"
var TRANSFER_ENCODING_HEADER = "Transfer-Encoding"
var UPGRADE_HEADER = "Upgrade"
var USER_AGENT_HEADER = "User-Agent"
var VARY_HEADER = "Vary"
var VIA_HEADER = "Via"
var WARNING_HEADER = "Warning"
var WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
var X_FORWARDED_FOR_HEADER = "X-Forwarded-For"
var X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto"
var X_FORWARDED_HOST_HEADER = "X-Forwarded-Host"
var X_FORWARDED_PORT_HEADER = "X-Forwarded-Port"
var X_DISABLE_PUSH_HEADER = "X-Disable-Push"
var X_FORWARDED_SERVER_HEADER = "X-Forwarded-Server"
var ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin"
var ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods"
var ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers"
var ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age"
