package retrofit2.processors;

import com.sun.javafx.collections.MappingChange;
import com.sun.tools.javac.util.List;
import java.util.Set;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

@RetrofitService
public interface UnresolvableParameterType {
  @POST("/") <T> Call<ResponseBody> typeVariable(@Body T body);

  @POST("/") <T extends RequestBody> Call<ResponseBody> typeVariableUpperBound(@Body T body);

  @POST("/") <T> Call<ResponseBody> crazy(@Body List<MappingChange.Map<String, Set<T[]>>> body);

  @POST("/") Call<ResponseBody> wildcard(@Body List<?> body);

  @POST("/") Call<ResponseBody> wildcardUpperBound(@Body List<? extends RequestBody> body);
}
