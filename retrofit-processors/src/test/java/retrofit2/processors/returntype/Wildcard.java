package retrofit2.processors.returntype;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.processors.RetrofitService;

@RetrofitService
public interface Wildcard {
  @GET("/") Call<?> wildcard();
}
