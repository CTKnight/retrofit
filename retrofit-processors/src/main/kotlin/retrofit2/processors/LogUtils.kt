package retrofit2.processors

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

fun ProcessingEnvironment.error(e: Element, msg: String) {
  messager.printMessage(Diagnostic.Kind.ERROR, msg, e)
}

fun ProcessingEnvironment.warning(e: Element, msg: String) {
  messager.printMessage(Diagnostic.Kind.WARNING, msg, e)
}