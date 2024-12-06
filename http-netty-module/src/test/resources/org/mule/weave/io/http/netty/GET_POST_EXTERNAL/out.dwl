{
  b: {
    statusText: "OK",
    status: 200,
    headersIsEmpty: false
  },
  d: {
    body: true,
    bodySize: true,
    contentType: "application/octet-stream"
  },
  e: {
    mimeType: "application/json",
    body: {
      args: {},
      data: "",
      files: {
        fileX: "import * from dw::io::http::Server\n\nvar serverConfig = {host:\"localhost\", port: 8081, \"Content-Type\": \"application/xml\"}\n\nfun main() =\napi(serverConfig,\n  {\n    \"/test\": {\n      GET: (request) -> {\n         body: {\n           name: \"Mariano\"\n         }\n      },\n      POST: (request) -> {\n        body: {\n           name: request.body.name\n        }\n      }\n    }\n  }\n)\n\n",
        fileY: "import * from dw::io::http::Server\n\nvar serverConfig = {host:\"localhost\", port: 8081, \"Content-Type\": \"application/xml\"}\n\nfun main() =\napi(serverConfig,\n  {\n    \"/test\": {\n      GET: (request) -> {\n         body: {\n           name: \"Mariano\"\n         }\n      },\n      POST: (request) -> {\n        body: {\n           name: request.body.name\n        }\n      }\n    }\n  }\n)\n\n"
      },
      form: {
        field: "value",
        field2: "value2"
      },
      json: null,
      url: "http://httpbin.org/post"
    },
    contentType: "application/json"
  },
  ee: {
    mimeType: "application/json",
    body: {
      args: {},
      data: "",
      files: {
        fileX: "import * from dw::io::http::Server\n\nvar serverConfig = {host:\"localhost\", port: 8081, \"Content-Type\": \"application/xml\"}\n\nfun main() =\napi(serverConfig,\n  {\n    \"/test\": {\n      GET: (request) -> {\n         body: {\n           name: \"Mariano\"\n         }\n      },\n      POST: (request) -> {\n        body: {\n           name: request.body.name\n        }\n      }\n    }\n  }\n)\n\n",
        fileY: "import * from dw::io::http::Server\n\nvar serverConfig = {host:\"localhost\", port: 8081, \"Content-Type\": \"application/xml\"}\n\nfun main() =\napi(serverConfig,\n  {\n    \"/test\": {\n      GET: (request) -> {\n         body: {\n           name: \"Mariano\"\n         }\n      },\n      POST: (request) -> {\n        body: {\n           name: request.body.name\n        }\n      }\n    }\n  }\n)\n\n"
      },
      form: {
        field: "value",
        field2: "value2"
      },
      json: null,
      url: "http://httpbin.org/post"
    },
    contentType: "application/json"
  },
  eee: {
    mimeType: "application/json",
    body: {
      args: {},
      data: "",
      files: {
        fileX: "import * from dw::io::http::Server\n\nvar serverConfig = {host:\"localhost\", port: 8081, \"Content-Type\": \"application/xml\"}\n\nfun main() =\napi(serverConfig,\n  {\n    \"/test\": {\n      GET: (request) -> {\n         body: {\n           name: \"Mariano\"\n         }\n      },\n      POST: (request) -> {\n        body: {\n           name: request.body.name\n        }\n      }\n    }\n  }\n)\n\n",
        fileY: "import * from dw::io::http::Server\n\nvar serverConfig = {host:\"localhost\", port: 8081, \"Content-Type\": \"application/xml\"}\n\nfun main() =\napi(serverConfig,\n  {\n    \"/test\": {\n      GET: (request) -> {\n         body: {\n           name: \"Mariano\"\n         }\n      },\n      POST: (request) -> {\n        body: {\n           name: request.body.name\n        }\n      }\n    }\n  }\n)\n\n"
      },
      form: {
        field: "value",
        field2: "value2"
      },
      json: null,
      url: "http://httpbin.org/post"
    },
   contentType: "application/json"
  }
}