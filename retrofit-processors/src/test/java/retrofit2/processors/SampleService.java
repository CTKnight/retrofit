package retrofit2.processors;

import retrofit2.Call;
import retrofit2.http.GET;

@RetrofitService
public interface SampleService {
  @GET("/") Call<Void> getVoid();
}
