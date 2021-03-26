%dw 2.0

import java!com::newrelic::api::agent::NewRelic
import * from dw::util::Timer

/**
* This function logs into NewRelic
**/
fun traceNewRelic(arguments: {prefix: String}, functionName: String, args: Array<Any>, callback: (Array<Any>) -> Any) : Any = do {
  var takenTime = duration(() -> callback(args))
  var ignore = NewRelic::recordResponseTimeMetric(arguments.prefix ++ functionName, takenTime.time)
  ---
  takenTime.result
}

/**
* This annotation allows to intercept any function and logs into NewRelic the amount of time that it takes
*/
@Interceptor(interceptorFunction = traceNewRelic)
annotation Trace(prefix: String)
