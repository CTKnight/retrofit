package retrofit2.processors

import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@AutoService(Processor::class)
class StaticCheckProcessor() : AbstractProcessor() {
    private var typeUtils: Types? = null
    private var elementUtils: Elements? = null
    private var filer: Filer? = null
    private var messager: Messager? = null

    @Synchronized
    override fun init(env: ProcessingEnvironment) {
        super.init(env)
        typeUtils = env.typeUtils
        elementUtils = env.elementUtils
        filer = env.filer
        messager = env.messager
    }

    override fun process(annoations: Set<TypeElement>, env: RoundEnvironment): Boolean {
        var result = false
        return result
    }

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(RetrofitService::class.java.canonicalName)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    private fun error(e: Element, msg: String) {
        messager!!.printMessage(Diagnostic.Kind.ERROR, msg, e)
    }
}