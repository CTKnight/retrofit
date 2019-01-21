package retrofit2

import com.google.auto.service.AutoService
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import com.squareup.javapoet.WildcardTypeName
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.OPTIONS
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.processors.ErrorMessage
import retrofit2.processors.RetrofitService
import retrofit2.processors.error
import retrofit2.processors.warning
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

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
        .mapNotNull { it as? ExecutableElement }
        .filter { !it.isDefault }
        .toList()

    methods.forEach { validateMethod(it) }
  }

  private fun validateMethod(methodElement: ExecutableElement) {
    val returnType = methodElement.returnType
    val returnTypeName = TypeName.get(returnType)
    val parameterTypes = methodElement.parameters

    validateReturnType(methodElement, returnTypeName)
//    validateParameterTypes(methodElement, parameterTypes)
    validateMethodAnnotations(methodElement, parameterTypes)
  }

  private fun validateParameterTypes(
    methodElement: ExecutableElement,
    parameterTypes: List<VariableElement>
  ) {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  private fun validateMethodAnnotations(
    element: ExecutableElement,
    parameters: List<VariableElement>
  ) {
    val annotations = element.annotationMirrors
    annotations.forEach {
      val typeName = TypeName.get(it.annotationType)
      when (typeName) {
        TypeName.get(DELETE::class.java),
        TypeName.get(GET::class.java),
        TypeName.get(HEAD::class.java),
        TypeName.get(PATCH::class.java),
        TypeName.get(POST::class.java),
        TypeName.get(PUT::class.java),
        TypeName.get(OPTIONS::class.java) -> {

        }
        TypeName.get(HTTP::class.java) -> {

        }
        TypeName.get(Headers::class.java) -> {

        }
        TypeName.get(Multipart::class.java) -> {

        }
        TypeName.get(FormUrlEncoded::class.java) -> {

        }
      }
    }
  }

  private fun validateReturnType(element: ExecutableElement, typeName: TypeName) {
    when (typeName) {
      TypeName.VOID -> {
        processingEnvironment.error(element, ErrorMessage.METHOD_RETURN_VOID)
      }
      else -> {
        containsUnresolvedType(element, typeName)
      }
    }
    // TODO: use retrofit instance to check return types with call adapters and converters
  }

  private fun containsUnresolvedType(
    element: Element,
    typeName: TypeName,
    recursiveLevel: Int = 0
  ) {
    if (recursiveLevel > 100) {
//      Wow, impressive nested type!
      TODO("Place a Warning here to notify users we give up checking this deep nested type")
    }

    when (typeName) {
      is TypeVariableName -> {
        processingEnvironment.error(element,
            "${ErrorMessage.METHOD_RETURN_UNRESOLVED}: ${typeName.name}")
      }
      is WildcardTypeName -> {
        processingEnvironment.error(element,
            ErrorMessage.METHOD_RETURN_UNRESOLVED)
      }
      is ArrayTypeName ->
        containsUnresolvedType(element, typeName.componentType, recursiveLevel + 1)
      is ParameterizedTypeName ->
        typeName.typeArguments.forEach {
          containsUnresolvedType(element, it, recursiveLevel + 1)
        }
    }
  }
}
