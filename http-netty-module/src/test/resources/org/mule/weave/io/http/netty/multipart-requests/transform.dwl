%dw 2.0

import * from dw::io::http::Client
import * from dw::io::http::Server
import form, field, file from dw::module::Multipart

output application/json

var serverConfig = { host: "localhost", port: dw::io::http::utils::Port::freePort(), normalizeResponseHeaders: false}
var LOCALHOST = '$(serverConfig.host):$(serverConfig.port)'
var server = api(serverConfig,
  {
    "/multipart": {
      "POST": (req: HttpServerRequest) -> do {
        {
          responseStatus: 200,
          body: req
        }
      }
    }
  })

---
{
  a: do {
    var response = post( 'http://$LOCALHOST/multipart', { "Content-Type": "multipart/form-data"},form([
      field('field', 'value'),
      field({name: 'field2', value:'value2'}),
      file({name: 'fileX', path: 'MyApi.dwl' }),
      file('fileY','MyApi.dwl')]))
    var body = response.body
    ---
    {
      mimeType : body.^mimeType,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
      },
      contentType : response.contentType,
    }
  },
  b: do {
    var response = postMultipart( 'http://$LOCALHOST/multipart', form([
      field('field', 'value'),
      field({name: 'field2', value:'value2'}),
      file({name: 'fileX', path: 'MyApi.dwl' }),
      file('fileY','MyApi.dwl')]), { "Content-TYPE": "multipart/form-data"})
    var body = response.body
    ---
    {
      mimeType: body.^mimeType,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
      },
      contentType: response.contentType
    }
  },
  c: do {
    var response = postMultipart( 'http://$LOCALHOST/multipart', form([
      field('field', 'value'),
      field({name: 'field2', value:'value2'}),
      file({name: 'fileX', path: 'MyApi.dwl' }),
      file('fileY','MyApi.dwl')]))
    var body = response.body
    ---
    {
      mimeType: body.^mimeType,
      body: {
        body: body.body,
        method: body.method,
        path: body.path,
        queryParams: body.queryParams,
      },
      contentType: response.contentType
    }
  },
  z: server.stop()
}