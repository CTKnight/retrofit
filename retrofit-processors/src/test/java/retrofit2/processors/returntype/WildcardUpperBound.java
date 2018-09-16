package retrofit2.processors.returntype;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.processors.RetrofitService;

@RetrofitService
public interface WildcardUpperBound {
  @GET("/") Call<? extends ResponseBody> wildcardUpperBound();
}
