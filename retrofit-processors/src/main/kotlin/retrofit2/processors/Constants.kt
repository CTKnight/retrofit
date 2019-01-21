package retrofit2.processors

class ErrorMessage {
  companion object {
    const val INVALID_TYPE = "API declarations must be interfaces."
    const val TOO_MUCH_INTERFACE = "API interfaces must not extend other interfaces."
    const val NOT_TYPE_ELEMENT = "The annotated element is not an instance of TypeElement, skipped."
    const val METHOD_RETURN_VOID = "Service methods cannot return void."
    const val METHOD_RETURN_UNRESOLVED =
        "Method's return type must not include a type variable or wildcard"
    const val METHOD_PARAMETER_UNRESOLVED =
        "Method's parameter type must not include a type variable or wildcard"
  }
}