Annotation processor
==============

## Purpose

The main caveat for retrofit is that it was not until runtime that 
retrofit validates the interfaces and their methods' annotations or return types
as well as parameters'.

These could be done at compile time with help of annotation.

So we come up with a annotation processor which will validate the interface 
with only an extra annotation `@RetrofitService`

The interface marked by `@RetrofitService` will be checked by annotation processor to
avoid some issues like a method is marked by `@GET` and `@POST` at the same time or 
a method marked by `@HEAD` has any return type other than `void`.

## Plan

- phase 1

    implement the annotation processor to check 
    this processor should be consistent to the current retrofit 
    aka compile time error at circumstances when retrofit would invoke `methodError`
    or `parameterError`
    
- phase 2

    implement the code generator to generate classes
    
    ```java
    package your.packagename.whatever;
    @RetrofitService
    public interface YourInterface {
      @GET("/{param1}") @AnnotationsNotInRetrofit
      Observable<YourModel> sample(@Path("param1") @AnnotationsNotInRetrofit String param1);
      // and other methods 
    }
    ```
    the generated classes should look like this:
    ```java
    package retrofit2.generated;
    public class YourInterfaceService {
      @AnnotationsNotInRetrofit
      Observable<YourModel> sample(@AnnotationsNotInRetrofit2 String param1) {
        // generated code
      }
      public static YourInterface build(Retrofit retrofit) {
        // generated code
      }  
    }
    ```
    
    usage:
    ```java
    package your.packagename.whatever;
    public class Main {
      public static void main(String[] args) {
          // just like how you used to build retrofit instance
          Retrofit retrofit = new Retrofit.Builder()
                  .baseUrl(HttpUrl.get("https://example.com/"))
                  .addConverterFactory(yourConverterFactory)
                  .client(yourOkHttpClient)
                  .build();
          YourInterface serviceInstance = YourInterfaceService.build(retrofit);
          Observable<YourModel> yourModelObservable = serviceInstance.sample("your_path");
          // do things with the method return value
      }
    }
    ```