package retrofit2

import com.google.auto.service.AutoService
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import com.squareup.javapoet.WildcardTypeName
import retrofit2.processors.ErrorMessage
import retrofit2.processors.RetrofitService
import retrofit2.processors.error
import retrofit2.processors.warning
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class RetrofitCheckProcessor : AbstractProcessor() {

  lateinit var processingEnvironment: ProcessingEnvironment
    private set

  @Synchronized
  override fun init(env: ProcessingEnvironment) {
    super.init(env)

    this.processingEnvironment = env
  }

  override fun process(annoations: Set<TypeElement>, env: RoundEnvironment): Boolean {
    env.getElementsAnnotatedWith(RetrofitService::class.java).forEach { annotated ->
      if (annotated !is TypeElement) {
        processingEnvironment.warning(annotated, ErrorMessage.NOT_TYPE_ELEMENT)
        return@forEach
      }
      validateInterface(annotated)
    }
    return true
  }

  override fun getSupportedAnnotationTypes(): Set<String> = setOf(
      RetrofitService::class.java.canonicalName)

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  private fun validateInterface(annotated: TypeElement) {
    if (annotated.kind != ElementKind.INTERFACE) {
      processingEnvironment.error(annotated, ErrorMessage.INVALID_TYPE)
    }

    if (!annotated.interfaces.isEmpty()) {
      processingEnvironment.error(annotated, ErrorMessage.TOO_MUCH_INTERFACE)
    }

    val methods = annotated
        .enclosedElements
        .asSequence()
        .mapNotNull { element -> element as? ExecutableElement }
        .filter { element -> !element.isDefault }
        .toList()

    methods.forEach { validateMethod(it) }
  }

  private fun validateMethod(element: ExecutableElement) {
    val returnType = element.returnType
    val returnTypeName = TypeName.get(returnType)
    validReturnType(returnTypeName, element)
  }

  private fun validReturnType(returnTypeName: TypeName, element: ExecutableElement) {
    when (returnTypeName) {
      TypeName.VOID -> {
        processingEnvironment.error(element, ErrorMessage.METHOD_RETURN_VOID)
      }
      is TypeVariableName -> {
        processingEnvironment.error(element,
            "${ErrorMessage.METHOD_RETURN_UNRESOLVED}: ${returnTypeName.name}")
      }
      is WildcardTypeName -> {
        processingEnvironment.error(element,
            ErrorMessage.METHOD_RETURN_UNRESOLVED)
      }
      is ArrayTypeName -> {
        validReturnType(returnTypeName.componentType, element)
      }
      is ParameterizedTypeName -> {
        returnTypeName.typeArguments.forEach { validReturnType(it, element) }
      }
    }
  }
}
