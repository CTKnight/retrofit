package retrofit2.processors.returntype;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.processors.RetrofitService;

@RetrofitService
public interface TypeVariableUpperBound {
  @GET("/") <T extends ResponseBody> Call<T> typeVariableUpperBound();
}
