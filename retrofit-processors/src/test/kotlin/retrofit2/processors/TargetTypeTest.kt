package retrofit2.processors

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import org.intellij.lang.annotations.Language
import org.junit.Test
import javax.tools.JavaFileObject
import kotlin.test.assertEquals

class TargetTypeTest {

  @Test
  fun extendedInterfaceTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test.type.target;

        import retrofit2.processors.RetrofitService;
        import retrofit2.processors.SampleService;

        @RetrofitService
        public interface ExtendedInterface extends SampleService {
        }
        """.trimIndent()

    val javaFileObject = JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.target.ExtendedInterface",
        TEST_SRC
    )
    val compilation = compiler().compile(javaFileObject)

    assertEquals(1, compilation.errors().size)
    val onlyError = compilation.errors().firstOrNull()
    assertEquals(ErrorMessage.TOO_MUCH_INTERFACE,
        onlyError?.getMessage())
  }

  @Test
  fun abstractClassTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test.type.target;

        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public abstract class SampleAbstractClass {
        }
        """.trimIndent()
    val javaFileObject = JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.target.SampleAbstractClass",
        TEST_SRC
    )
    invalidTypeTest(javaFileObject)
  }

  @Test
  fun classTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test.type.target;

        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public class SampleClass {
        }
        """.trimIndent()
    val javaFileObject = JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.target.SampleClass",
        TEST_SRC
    )
    invalidTypeTest(javaFileObject)
  }

  private fun invalidTypeTest(javaFileObject: JavaFileObject) {
    val compilation = compiler()
        .compile(javaFileObject)

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    val onlyWarning = compilation.errors().firstOrNull()
    assertEquals(ErrorMessage.INVALID_TYPE, onlyWarning?.getMessage())
  }
}
