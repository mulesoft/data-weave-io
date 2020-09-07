{
  a: {
    success: false,
    error: {
      kind: "InvalidUrlException",
      message: "The absolute/url is not valid.",
      location: "\n90| fun request(method: HttpMethod, url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse = native(\"http::HttpRequestFunction\")\n                                                                                                                   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^",
      stack: [
        "request (dw::io::http::Client:90:112)", 
        "GET (dw::io::http::Client:49:3)", 
        "main (Main:8:16)"
      ]
    }
  },
  b: {
    message: true,
    stack: [
      "main (dw::io::http::Client:90:112)"
    ],
    kind: "UrlConnectionException"
  },
  c: {
    success: false,
    error: {
      kind: "UrlConnectionException",
      message: "Unable to connect to http://127.0.0.1:32191/url. Caused by Connection refused: /127.0.0.1:32191.",
      location: "\n90| fun request(method: HttpMethod, url: String | UrlBuilder, httpRequest: HttpRequest = {}): HttpClientResponse = native(\"http::HttpRequestFunction\")\n                                                                                                                   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^",
      stack: [
        "main (dw::io::http::Client:90:112)"
      ]
    }
  }
}