package com.virtual_market.planetshipmentapp.MyUils;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor {

    public static String credentials;

    public BasicAuthInterceptor() {
        credentials = Credentials.basic("planet", "planet@#ioi$&*Tqhodktsnifk");
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials)
                .build();
        return chain.proceed(authenticatedRequest);
    }

}
