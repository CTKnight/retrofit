package retrofit2.processors

import com.google.testing.compile.Compiler
import retrofit2.RetrofitCheckProcessor

fun compiler(): Compiler = Compiler.javac().withProcessors(RetrofitCheckProcessor())