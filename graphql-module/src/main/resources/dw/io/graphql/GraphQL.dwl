%dw 2.0
import * from dw::core::Types
import * from dw::core::Arrays


fun toGraphqlSchema(t: Type): String = do {

  fun isTemporalType(t: Type): Boolean =
    isDateTimeType(t) or isDateType(t) or isLocalTimeType(t) or isTimeType(t) or isLocalTimeType(t) or isLocalDateTimeType(t) or isPeriodType(t)

  fun isEnumType(t: Type): Boolean =
    isUnionType(t) and (unionItems(t) every ((item) -> isLiteralType(item)))

  fun toFunctionFieldSchema(function: Type) = do {
    "(" ++ (functionParamTypes(function) map ((item, index) -> "$(item.name): " ++ toGraphqlSchema(item.paramType) ++ if (!item.optional)
        "!"
      else
        "") joinBy ",") ++ ") : " ++ toGraphqlSchema(functionReturnType(function) default Any)
  }

  fun toGraphqlFieldSchema(field: Field): String =
    if (isFunctionType(field.value))
      "\t" ++ field.key.name.localName ++ toGraphqlSchema(field.value)
    else
      "\t" ++ field.key.name.localName ++ ": " ++ toGraphqlSchema(field.value) ++ (if (field.required)
        "!"
      else
        "")
  ---
  t match {
    case ref if isReferenceType(t) -> nameOf(ref)
    case array if isArrayType(t) -> do {
      "[" ++ toGraphqlSchema(arrayItem(t)) ++ "]"
    }
    case object if isObjectType(t) -> do {
      "{\n" ++ (objectFields(object) map ((item, index) -> toGraphqlFieldSchema(item)) reduce ((item, accumulator) -> item ++ ",\n" ++ accumulator) default "") ++ "\n}"
    }
    case union if isEnumType(union) -> do {
      // We should change this to be a reference type to an enum
      "String"
    }
    case number if isNumberType(t) -> if (lower(t.^precision as String default "") == "integer")
      "Integer"
    else
      "Float"
    case function if isFunctionType(function) -> do {
      toFunctionFieldSchema(function)
    }
    case number if isTemporalType(t) -> "String"
    else -> nameOf(baseTypeOf($))
  }
}

fun toGraphqlCatalog(types: { _?: Type<Any> }) =
  types pluck ((value, key, index) -> "\ntype $(key) $(toGraphqlSchema(baseTypeOf(value)))") joinBy "\n\n"


// Returns a Catalog of all the Types that need to be represented in order to have a complete type
fun collectOnChildren(theType: Type, newCollector: { _?: Type }) =
    theType match {
      case array if isArrayType(theType) -> do {
        collectRefTypes(arrayItem(array), newCollector)
      }
      case object if isObjectType(theType) -> do {
        objectFields(object)
            reduce ((item, accumulator = newCollector) -> collectRefTypes(item.value, accumulator))
      }
      case function if isFunctionType(function) -> do {
        (
            (functionParamTypes(function)
                map ((item, index) -> item.paramType)) << (functionReturnType(function) default Any)
            )
         reduce ((item, accumulator = newCollector) -> collectRefTypes(item, accumulator))
      }
      else -> newCollector
}

 fun collectRefTypes(theType: Type, collector: { _?: Type } = {}): { _?: Type<Any> } = do {
    theType match {
      case ref if isReferenceType(theType) -> do {
        if (collector[nameOf(ref)]?) collector else do {
          var newCollector = collector ++ {
            (nameOf(ref)): ref
          }
          ---
          collectOnChildren(theType, newCollector)
        }
      }
      else -> collectOnChildren(theType, collector)
    }
  }

fun graphQL<T>(queryType: Type<T>, v: T) = do {
  var types: { _?: Type } = collectRefTypes(queryType)
  ---
  toGraphqlCatalog(types ++ {"Query" : queryType})
}
