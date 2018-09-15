package retrofit2

import com.google.auto.service.AutoService
import retrofit2.processors.ErrorMessage
import retrofit2.processors.RetrofitService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@AutoService(Processor::class)
class RetrofitCheckProcessor : AbstractProcessor() {
  private lateinit var typeUtils: Types
  private lateinit var elementUtils: Elements
  private lateinit var filer: Filer
  private lateinit var messager: Messager

  @Synchronized
  override fun init(env: ProcessingEnvironment) {
    super.init(env)
    typeUtils = env.typeUtils
    elementUtils = env.elementUtils
    filer = env.filer
    messager = env.messager
  }

  override fun process(annoations: Set<TypeElement>, env: RoundEnvironment): Boolean {
    env.getElementsAnnotatedWith(RetrofitService::class.java).forEach { annotated ->
      if (annotated !is TypeElement) {
        warning(annotated, ErrorMessage.NOT_TYPE_ELEMENT)
        return@forEach
      }

      validateInterface(annotated)
    }
    return true
  }

  override fun getSupportedAnnotationTypes(): Set<String> = setOf(
      RetrofitService::class.java.canonicalName)

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  private fun error(e: Element, msg: String) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg, e)
  }

  private fun warning(e: Element, msg: String) {
    messager.printMessage(Diagnostic.Kind.WARNING, msg, e)
  }

  private fun validateInterface(annotated: TypeElement) {
    if (annotated.kind != ElementKind.INTERFACE) {
      error(annotated, ErrorMessage.INVALID_TYPE)
    }

    if (!annotated.interfaces.isEmpty()) {
      error(annotated, ErrorMessage.TOO_MUCH_INTERFACE)
    }

    val methods = annotated
        .enclosedElements
        .mapNotNull { element -> element as? ExecutableElement }
        .filter { element -> !element.isDefault }

    methods.forEach { validateMethod(it) }
  }

  private fun validateMethod(element: ExecutableElement) {
    val returnType = element.returnType
  }
}