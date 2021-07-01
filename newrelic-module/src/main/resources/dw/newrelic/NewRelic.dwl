%dw 2.0
import java!com::newrelic::api::agent::NewRelic

fun transactionName(name: String) = do {
  var ignore = NewRelic::setTransactionName(null, name)
  ---
  name
}

fun noticeError(message: String, params: Object) = do {
  var ignore = NewRelic::noticeError(message, params)
  ---
  message
}

fun recordMetric(metric: String, value) = do {
  var ignore = NewRelic::recordMetric(metric, value)
  ---
  metric
}