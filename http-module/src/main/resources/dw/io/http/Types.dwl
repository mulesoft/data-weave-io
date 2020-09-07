%dw 2.0

// COMMON =========================================================


type HttpBody = Any

type HttpMethod = "GET" | "HEAD" | "POST" | "PUT" | "DELETE" | "CONNECT" | "OPTIONS" | "TRACE" | "PATCH"

type HttpHeaders = {
  "Content-Type"?: String,
  Authorization?: String,
  ETag?: SimpleType,
  Cookie*?: String,
   _ ?: SimpleType
}

type HttpCookies = {
  _ ?: SimpleType
}

type QueryParams = {
  _ ?: String
}

// CLIENT =========================================================




// SERVER IMPLEMENTATION


