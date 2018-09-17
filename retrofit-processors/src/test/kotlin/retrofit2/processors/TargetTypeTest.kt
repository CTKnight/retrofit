package retrofit2.processors

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import org.junit.Test
import java.util.Locale
import javax.tools.JavaFileObject
import kotlin.test.assertEquals

class TargetTypeTest {

  @Test
  fun extendedInterfaceTest() {
    val javaFileObject = JavaFileObjects.forSourceLines(
        "retrofit2.processors.ExtendedInterface",
        """
        package retrofit2.processors;

        @RetrofitService
        public interface ExtendedInterface extends SampleService {
        }
        """
    )
    val compilation = compiler().compile(javaFileObject)


    assertEquals(1, compilation.errors().size)
    val onlyError = compilation.errors().firstOrNull()
    assertEquals(ErrorMessage.TOO_MUCH_INTERFACE,
        onlyError?.getMessage(Locale.getDefault()))
  }

  @Test
  fun abstractClassTest() {
    val javaFileObject = JavaFileObjects.forSourceLines(
        "retrofit2.processors.SampleAbstractClass",
        """
            package retrofit2.processors;

            @RetrofitService
            public abstract class SampleAbstractClass {
            }
            """
    )
    invalidTypeTest(javaFileObject)
  }

  @Test
  fun classTest() {
    val javaFileObject = JavaFileObjects.forSourceLines(
        "retrofit2.processors.SampleClass",
        """
        package retrofit2.processors;

        @RetrofitService
        public class SampleClass {
        }
        """
    )
    invalidTypeTest(javaFileObject)
  }

  private fun invalidTypeTest(javaFileObject: JavaFileObject) {
    val compilation = compiler()
        .compile(javaFileObject)

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    val onlyWarning = compilation.errors().firstOrNull()
    assertEquals(ErrorMessage.INVALID_TYPE, onlyWarning?.getMessage(Locale.getDefault()))
  }
}
