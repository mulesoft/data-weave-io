
%dw 2.0
import * from dw::test::Tests
import * from dw::test::Asserts
---
"Integration tests" describedBy  [
  "Test cors" describedBy [
      "Assert default" in do {
          runMapping("cors.dwl",
              inputsOf("cors"), "application/json")
                must equalTo(outputOf("cors"))
      },
  ],
  "Test GET_POST" describedBy [
      "Assert default" in do {
          runMapping("GET_POST.dwl",
              inputsOf("GET_POST"), "application/json")
                must equalTo(outputOf("GET_POST"))
      },
  ],
  "Test handleErrors" describedBy [
      "Assert default" in do {
          runMapping("handleErrors.dwl",
              inputsOf("handleErrors"), "application/json")
                must equalTo(outputOf("handleErrors"))
      },
  ],
  "Test internal_error" describedBy [
      "Assert default" in do {
          runMapping("internal_error.dwl",
              inputsOf("internal_error"), "application/json")
                must equalTo(outputOf("internal_error"))
      },
  ],
  "Test interseptors" describedBy [
      "Assert default" in do {
          runMapping("interseptors.dwl",
              inputsOf("interseptors"), "application/json")
                must equalTo(outputOf("interseptors"))
      },
  ],
  "Test server" describedBy [
      "Assert default" in do {
          runMapping("server.dwl",
              inputsOf("server"), "application/json")
                must equalTo(outputOf("server"))
      },
  ],
  "Test ssl" describedBy [
      "Assert default" in do {
          runMapping("ssl.dwl",
              inputsOf("ssl"), "application/json")
                must equalTo(outputOf("ssl"))
      },
  ],
  "Test static_file" describedBy [
      "Assert default" in do {
          runMapping("static_file.dwl",
              inputsOf("static_file"), "application/json")
                must equalTo(outputOf("static_file"))
      },
  ],
]
