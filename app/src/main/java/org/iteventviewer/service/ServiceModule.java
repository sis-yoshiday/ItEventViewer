package org.iteventviewer.service;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import org.iteventviewer.app.BuildConfig;
import org.iteventviewer.app.AtndEventDetailActivity;
import org.iteventviewer.app.main.CategorySettingsFragment;
import org.iteventviewer.app.main.IndexFragment;
import org.iteventviewer.common.LocalDateTimeConverter;
import org.iteventviewer.service.atnd.AtndApi;
import org.iteventviewer.service.compass.ConnpassApi;
import org.iteventviewer.service.qiita.QiitaApi;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Module(injects = {
    IndexFragment.class, CategorySettingsFragment.class, AtndEventDetailActivity.class
}, complete = false, library = true) public class ServiceModule {

  private Context context;

  public ServiceModule(Context context) {
    this.context = context;
  }

  @Provides @Singleton public AtndApi provideAtndApi() {

    LocalDateTimeConverter localDateTimeConverter =
        new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    Gson gson =
        new GsonBuilder().registerTypeAdapter(LocalDateTimeConverter.TYPE, localDateTimeConverter)
            .create();

    OkHttpClient httpClient = new OkHttpClient();
    httpClient.setConnectTimeout(3, TimeUnit.SECONDS);
    httpClient.setReadTimeout(3, TimeUnit.SECONDS);
    try {
      File cacheDir = new File(context.getCacheDir(), "http");
      Cache cache = new Cache(cacheDir, 2 * 1024 * 1024);
      httpClient.setCache(cache);
    } catch (IOException e) {
      Timber.e(e, "Unable to install disk cache.");
    }

    return new RestAdapter.Builder().setEndpoint(AtndApi.ENDPOINT)
        .setClient(new OkClient(httpClient))
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .build()
        .create(AtndApi.class);
  }

  @Provides @Singleton public ConnpassApi provideConpassApi() {

    LocalDateTimeConverter localDateTimeConverter =
        new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm:ssZ");

    Gson gson =
        new GsonBuilder().registerTypeAdapter(LocalDateTimeConverter.TYPE, localDateTimeConverter)
            .create();

    OkHttpClient httpClient = new OkHttpClient();
    httpClient.setConnectTimeout(3, TimeUnit.SECONDS);
    httpClient.setReadTimeout(3, TimeUnit.SECONDS);
    try {
      File cacheDir = new File(context.getCacheDir(), "http");
      Cache cache = new Cache(cacheDir, 2 * 1024 * 1024);
      httpClient.setCache(cache);
    } catch (IOException e) {
      Timber.e(e, "Unable to install disk cache.");
    }

    return new RestAdapter.Builder().setEndpoint(ConnpassApi.ENDPOINT)
        .setClient(new OkClient(httpClient))
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .build()
        .create(ConnpassApi.class);
  }

  @Provides @Singleton public QiitaApi provideQiitaApi() {

    Gson gson = new GsonBuilder().create();

    OkHttpClient httpClient = new OkHttpClient();
    httpClient.setConnectTimeout(3, TimeUnit.SECONDS);
    httpClient.setReadTimeout(3, TimeUnit.SECONDS);

    return new RestAdapter.Builder().setEndpoint(QiitaApi.ENDPOINT)
        .setClient(new OkClient(httpClient))
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .build()
        .create(QiitaApi.class);
  }
}
