package retrofit2.processors

import com.google.testing.compile.Compiler
import retrofit2.RetrofitCheckProcessor
import java.util.Locale
import javax.tools.Diagnostic

fun compiler(): Compiler = Compiler.javac().withProcessors(RetrofitCheckProcessor())

fun <S> Diagnostic<S>.getMessage(): String? = getMessage(null)