package retrofit2

import com.google.auto.common.MoreElements
import com.google.auto.service.AutoService
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import com.squareup.javapoet.WildcardTypeName
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
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
import javax.lang.model.element.AnnotationMirror
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
        .mapNotNull { it as? ExecutableElement }
        .filter { !it.isDefault }
        .toList()

    methods.forEach { validateMethod(it) }
  }

  private fun validateMethod(element: ExecutableElement) {
    val returnType = element.returnType
    val returnTypeName = TypeName.get(returnType)
    validReturnType(returnTypeName, element)
    validateMethodAnnotations(element)
  }

  private fun validateMethodAnnotations(element: ExecutableElement) {
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
    // TODO: use retrofit instance to check return types with call adapters and converters
  }
}
