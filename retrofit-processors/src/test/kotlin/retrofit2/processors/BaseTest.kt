package retrofit2.processors

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import org.junit.Test
import kotlin.test.assertEquals

class BaseTest {

  @Test
  fun sampleTest() {
    val compilation =
        compiler()
            .compile(JavaFileObjects.forSourceLines(
                "retrofit2.processors.SampleService",
                """
                package retrofit2.processors;

                import retrofit2.http.HEAD;

                @RetrofitService
                public interface SampleService {
                  @HEAD("/")
                  public void sampleMethod();
                }
                """))

    assertEquals(compilation.status(), Compilation.Status.SUCCESS)
    assertEquals(compilation.warnings().size, 0)
  }

}