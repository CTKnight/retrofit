package retrofit2.processors

class ErrorMessage {
  companion object {
    const val INVALID_TYPE = "API declarations must be interfaces."
    const val TOO_MUCH_INTERFACE = "API interfaces must not extend other interfaces."
    const val NOT_TYPE_ELEMENT = "The annotated element is not an instance of TypeElement, skipped"
  }
}