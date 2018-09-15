package retrofit2.processors;

import retrofit2.http.HEAD;

@RetrofitService
public interface SampleService {
  @HEAD("/")
  void sampleMethod();
}
