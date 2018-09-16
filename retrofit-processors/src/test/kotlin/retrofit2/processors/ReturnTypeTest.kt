package retrofit2.processors

import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import org.junit.Test
import kotlin.test.assertEquals

class ReturnTypeTest {
  @Test
  fun typeVariableTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.returntype.TypeVariable",
        """
        package retrofit2.processors.returntype;

        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public interface TypeVariable {
          @GET("/") <T> Call<T> typeVariable();
        }
        """
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_RETURN_UNRESOLVED}: T", compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun typeVariableUpperBoundTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.returntype.TypeVariableUpperBound",
        """
        package retrofit2.processors.returntype;

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public interface TypeVariableUpperBound {
          @GET("/") <T extends ResponseBody> Call<T> typeVariableUpperBound();
        }
        """
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_RETURN_UNRESOLVED}: T", compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun wildcardTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.returntype.Wildcard",
        """
        package retrofit2.processors.returntype;

        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public interface Wildcard {
          @GET("/") Call<?> wildcard();
        }
        """
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals(ErrorMessage.METHOD_RETURN_UNRESOLVED, compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun wildcardUpperBoundTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.returntype.WildcardUpperBound",
        """
        package retrofit2.processors.returntype;

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.processors.RetrofitService;

        @RetrofitService
        public interface WildcardUpperBound {
          @GET("/") Call<? extends ResponseBody> wildcardUpperBound();
        }
        """
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals(ErrorMessage.METHOD_RETURN_UNRESOLVED, compilation.errors().firstOrNull()?.getMessage())
  }

  @Test
  fun nestedTypeVariableTest() {
    val compilation = compiler().compile(JavaFileObjects.forSourceLines(
        "retrofit2.processors.returntype.NestedTypeVariable",
        """
        package retrofit2.processors.returntype;

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
    ))

    assertEquals(Compilation.Status.FAILURE, compilation.status())
    assertEquals(1, compilation.errors().size)
    assertEquals("${ErrorMessage.METHOD_RETURN_UNRESOLVED}: T", compilation.errors().firstOrNull()?.getMessage())
  }
}