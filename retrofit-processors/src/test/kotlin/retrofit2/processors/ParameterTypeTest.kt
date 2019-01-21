package retrofit2.processors

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import org.intellij.lang.annotations.Language
import org.junit.Test
import kotlin.test.assertEquals

class ParameterTypeTest {
  @Test
  fun typeVariableTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test.type.parameter;

        import okhttp3.ResponseBody;
        import retrofit2.processors.RetrofitService;
        import retrofit2.Call;
        import retrofit2.http.Body;
        import retrofit2.http.POST;

        @RetrofitService
        public interface UnresolvableParameterType {
          @POST("/") <T> Call<ResponseBody> typeVariable(@Body T body);
        }
        """.trimIndent()

    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.response.UnresolvableParameterType",
        TEST_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_PARAMETER_UNRESOLVED}: T",
        compilation.errors().firstOrNull()?.getMessage())
  }
}