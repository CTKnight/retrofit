package retrofit2.processors

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects

import org.intellij.lang.annotations.Language
import org.junit.Test
import kotlin.test.assertEquals

class ParameterTypeTest {
  companion object {
    @Language("JAVA") private val TYPE_VARIABLE_SRC =
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
    @Language("JAVA") val TYPE_VARIABLE_UPPER_BOUND_SRC =
        """
        package retrofit2.processors.test.type.parameter;

        import okhttp3.RequestBody;
        import okhttp3.ResponseBody;
        import retrofit2.processors.RetrofitService;
        import retrofit2.Call;
        import retrofit2.http.Body;
        import retrofit2.http.POST;

        @RetrofitService
        public interface UnresolvableParameterType {
          @POST("/") <T extends RequestBody> Call<ResponseBody> typeVariableUpperBound(@Body T body);
        }
        """.trimIndent()
    @Language("JAVA") val CRAZY_SRC =
        """
        package retrofit2.processors.test.type.parameter;

        import com.sun.javafx.collections.MappingChange;
        import java.util.List;
        import java.util.Set;
        import okhttp3.ResponseBody;
        import retrofit2.processors.RetrofitService;
        import retrofit2.Call;
        import retrofit2.http.Body;
        import retrofit2.http.POST;

        @RetrofitService
        public interface UnresolvableParameterType {
          @POST("/") <T> Call<ResponseBody> crazy(@Body List<MappingChange.Map<String, Set<T[]>>> body);
        }
        """.trimIndent()
    @Language("JAVA") val WILDCARD_SRC =
        """
        package retrofit2.processors.test.type.parameter;

        import java.util.List;
        import okhttp3.RequestBody;
        import okhttp3.ResponseBody;
        import retrofit2.processors.RetrofitService;
        import retrofit2.Call;
        import retrofit2.http.Body;
        import retrofit2.http.POST;

        @RetrofitService
        public interface UnresolvableParameterType {
          @POST("/") Call<ResponseBody> wildcard(@Body List<?> body);
        }
        """.trimIndent()
    @Language("JAVA") val WILDCARD_UPPER_BOUND_SRC =
        """
        package retrofit2.processors.test.type.parameter;

        import java.util.List;
        import okhttp3.RequestBody;
        import okhttp3.ResponseBody;
        import retrofit2.processors.RetrofitService;
        import retrofit2.Call;
        import retrofit2.http.Body;
        import retrofit2.http.POST;

        @RetrofitService
        public interface UnresolvableParameterType {
          @POST("/") Call<ResponseBody> wildcardUpperBound(@Body List<? extends RequestBody> body);
        }
        """.trimIndent()
  }

  @Test
  fun typeVariableTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.parameter.UnresolvableParameterType",
        TYPE_VARIABLE_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_PARAMETER_UNRESOLVED}: T",
        compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun upperBoundTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.parameter.UnresolvableParameterType",
        TYPE_VARIABLE_UPPER_BOUND_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_PARAMETER_UNRESOLVED}: T",
        compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun crazyTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.parameter.UnresolvableParameterType",
        CRAZY_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_PARAMETER_UNRESOLVED}: T",
        compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun wildcardTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.parameter.UnresolvableParameterType",
        WILDCARD_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals(ErrorMessage.METHOD_PARAMETER_UNRESOLVED,
        compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun wildcardUpperBoundTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.parameter.UnresolvableParameterType",
        WILDCARD_UPPER_BOUND_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals(ErrorMessage.METHOD_PARAMETER_UNRESOLVED,
        compilation.errors().firstOrNull()?.getMessage())
  }
}