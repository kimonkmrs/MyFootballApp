package com.myapp.minifootballstats;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        // Create logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log the body of requests and responses

        // Create OkHttpClient with the logging interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Add interceptor to client
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:3000/") // Update with your API base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) // Set the client with the interceptor
                    .build();
        }
        return retrofit;
    }
}
