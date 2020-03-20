import * from dw::http::Client
import form, field from dw::module::Multipart

var result = {
               err: false,
               options: {
                 headers: {
                   "Content-Type": "multipart/form-data; boundary=TLSenisvee812j3iom1Ilg27Hx0--TgMS0ypMcK0XQQd9jNueyA6HDabjFETl-m-mhy27b",
                   "Content-Length": 205
                 },
                 body: "--TLSenisvee812j3iom1Ilg27Hx0--TgMS0ypMcK0XQQd9jNueyA6HDabjFETl-m-mhy27b\r\nContent-Disposition: form-data; name=\"field\"\r\n\r\nvalue\r\n--TLSenisvee812j3iom1Ilg27Hx0--TgMS0ypMcK0XQQd9jNueyA6HDabjFETl-m-mhy27b--\r\n",
                 method: "POST",
                 url: "http://httpbin.org/post"
               },
               request: {
                 httpVersion: "HTTP/1.1",
                 url: "http://httpbin.org/post",
                 path: "/post",
                 method: "POST",
                 ip: "23.23.122.246",
                 port: 80,
                 headers: {
                   Connection: "close",
                   "Accept-Encoding": "gzip,deflate",
                   "Content-Type": "multipart/form-data; boundary=TLSenisvee812j3iom1Ilg27Hx0--TgMS0ypMcK0XQQd9jNueyA6HDabjFETl-m-mhy27b",
                   "Content-Length": "205",
                   Host: "httpbin.org"
                 },
                 payload: "--TLSenisvee812j3iom1Ilg27Hx0--TgMS0ypMcK0XQQd9jNueyA6HDabjFETl-m-mhy27b\r\nContent-Disposition: form-data; name=\"field\"\r\n\r\nvalue\r\n--TLSenisvee812j3iom1Ilg27Hx0--TgMS0ypMcK0XQQd9jNueyA6HDabjFETl-m-mhy27b--\r\n"
               },
               timers: {
                 blocked: 0.236175,
                 connect: 157.956608,
                 wait: 0.162888,
                 receive: 174.0044,
                 total: 332.449184
               },
               response: {
                 status: 200,
                 statusText: "OK",
                 headers: {
                   "X-Powered-By": "Flask",
                   Server: "meinheld/0.6.1",
                   Date: "Fri, 08 Sep 2017 19:46:52 GMT",
                   Connection: "close",
                   Via: "1.1 vegur",
                   "Access-Control-Allow-Origin": "*",
                   "Access-Control-Allow-Credentials": "true",
                   "X-Processed-Time": "0.00134801864624",
                   "Content-Type": "application/json",
                   "Content-Length": "441"
                 },
                 payload: "{\n  \"args\": {}, \n  \"data\": \"\", \n  \"files\": {}, \n  \"form\": {\n    \"field\": \"value\"\n  }, \n  \"headers\": {\n    \"Accept-Encoding\": \"gzip,deflate\", \n    \"Connection\": \"close\", \n    \"Content-Length\": \"205\", \n    \"Content-Type\": \"multipart/form-data; boundary=TLSenisvee812j3iom1Ilg27Hx0--TgMS0ypMcK0XQQd9jNueyA6HDabjFETl-m-mhy27b\", \n    \"Host\": \"httpbin.org\"\n  }, \n  \"json\": null, \n  \"origin\": \"74.115.21.214\", \n  \"url\": \"http://httpbin.org/post\"\n}\n",
                 body: {
                   args: {},
                   data: "",
                   files: {},
                   form: {
                     field: "value"
                   },
                   headers: {
                     "Accept-Encoding": "gzip,deflate",
                     Connection: "close",
                     "Content-Length": "205",
                     "Content-Type": "multipart/form-data; boundary=TLSenisvee812j3iom1Ilg27Hx0--TgMS0ypMcK0XQQd9jNueyA6HDabjFETl-m-mhy27b",
                     Host: "httpbin.org"
                   },
                   json: null,
                   origin: "74.115.21.214",
                   url: "http://httpbin.org/post"
                 },
                 mime: "application/json",
                 contentType: "application/json"
               }
             }
             /*request('POST', 'http://httpbin.org/post',
               {
                 body: form([
                   field('field', 'value')
                 ])
               }
             )*/



fun xx(r: dw::http::Types::HttpClientResult) = false
---
result match {
  case is Null -> false
  case is dw::http::Types::HttpClientResult -> true // This should break if the types are not assignable in runtime
}