package retrofit2.processors

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import org.intellij.lang.annotations.Language
import org.junit.Test
import kotlin.test.assertEquals

class ReturnTypeTest {
  @Test
  fun typeVariableTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test.type.response;

        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public interface UnresolvableParameterType {
          @GET("/") <T> Call<T> typeVariable();
        }
        """.trimIndent()

    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.response.UnresolvableParameterType",
        TEST_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_RETURN_UNRESOLVED}: T",
        compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun typeVariableUpperBoundTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test.type.response;

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public interface TypeVariableUpperBound {
          @GET("/") <T extends ResponseBody> Call<T> typeVariableUpperBound();
        }
        """.trimIndent()

    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.response.TypeVariableUpperBound",
        TEST_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_RETURN_UNRESOLVED}: T",
        compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun wildcardTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test.type.response;

        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public interface Wildcard {
          @GET("/") Call<?> wildcard();
        }
        """.trimIndent()

    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.response.Wildcard",
        TEST_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals(ErrorMessage.METHOD_RETURN_UNRESOLVED,
        compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun wildcardUpperBoundTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test.type.response;

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public interface WildcardUpperBound {
          @GET("/") Call<? extends ResponseBody> wildcardUpperBound();
        }
        """.trimIndent()

    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.response.WildcardUpperBound",
        TEST_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals(ErrorMessage.METHOD_RETURN_UNRESOLVED,
        compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun nestedTypeVariableTest() {
    @Language("JAVA") val TEST_SRC =
        """
        package retrofit2.processors.test.type.response;

        import java.util.List;
        import java.util.Map;
        import java.util.Set;
        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public interface NestedTypeVariable {
          @GET("/") <T> Call<List<Map<String, Set<T[]>>>> crazy();
        }
        """

    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.test.type.response.NestedTypeVariable",
        TEST_SRC
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_RETURN_UNRESOLVED}: T",
        compilation.errors().firstOrNull()?.getMessage())
  }
}