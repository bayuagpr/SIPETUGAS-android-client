package fly.speedmeter.grub.network;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import fly.speedmeter.grub.utils.Const;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String url = Const.URL + "api/";

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);



        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        /* This line is where we add the interceptor to the retrofit service  */

        httpClient.addInterceptor(new NetworkInterceptor(context));

        httpClient.addInterceptor(logging);

        httpClient.connectTimeout(30, TimeUnit.SECONDS);

        httpClient.readTimeout(30, TimeUnit.SECONDS);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit;

    }

//    public static Retrofit getClient() {
//
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(url)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .build();
//        }
//
//        return retrofit;
//
//    }

}
