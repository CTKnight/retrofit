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
