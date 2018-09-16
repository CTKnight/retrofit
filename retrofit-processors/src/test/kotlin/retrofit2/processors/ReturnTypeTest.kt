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
}