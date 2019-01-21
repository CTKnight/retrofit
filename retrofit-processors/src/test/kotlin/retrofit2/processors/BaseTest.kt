package retrofit2.processors

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import org.intellij.lang.annotations.Language
import org.junit.Test
import kotlin.test.assertEquals

class BaseTest {

  @Test
  fun sampleTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test;

        import retrofit2.Call;
        import retrofit2.http.GET;

        @RetrofitService
        public interface SampleService {
          @GET("/") Call<Void> getVoid();
        }
        """.trimIndent()

    val compilation =
        compiler()
            .compile(JavaFileObjects.forSourceLines(
                "retrofit2.processors.test.SampleService", TEST_SRC
            ))

    assertEquals(compilation.status(), Compilation.Status.SUCCESS)
    assertEquals(compilation.warnings().size, 0)
  }

}