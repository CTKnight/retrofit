package retrofit2.processors

import com.google.testing.compile.Compilation
import com.google.testing.compile.Compiler
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.JavaFileObjects
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaseTest {

    private fun compiler(): Compiler = javac().withProcessors(StaticCheckProcessor())

    @Test
    fun sampleTest() {
        val compilation =
                compiler()
                        .compile(JavaFileObjects.forSourceLines(
                                "SampleService",
                                """
                    package retrofit2.processors;

                    import retrofit2.http.HEAD;

                    @RetrofitService
                    public interface SampleService {
                        @HEAD("/")
                        public void sampleMethod();
                    }
                """.trimIndent()))

        assertEquals(compilation.status(), Compilation.Status.SUCCESS)
        assertEquals(compilation.warnings().size, 0)

    }

    @Test
    fun interfaceOnlyTest() {
        val compilation =  compiler()
                .compile(JavaFileObjects.forSourceLines("SampleAbstractClass",
                """
            package retrofit2.processors;

            @RetrofitService
            public abstract class SampleAbstractClass {
            }
        """.trimIndent()
        ))

        assertEquals(compilation.status(), Compilation.Status.SUCCESS)
        assertTrue(compilation.warnings().size == 1)
        val onlyWarning = compilation.warnings().first()
        assertTrue(onlyWarning.getMessage(Locale.getDefault())!!
                .contentEquals("@RetrofitService is not annotated on an Interface, skipped"))
    }
}