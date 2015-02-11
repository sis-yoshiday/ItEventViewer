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
import org.iteventviewer.app.AtndEventDetailActivity;
import org.iteventviewer.app.BuildConfig;
import org.iteventviewer.app.main.CategorySettingsFragment;
import org.iteventviewer.app.main.IndexFragment;
import org.iteventviewer.common.LocalDateTimeConverter;
import org.iteventviewer.service.atnd.AtndApi;
import org.iteventviewer.service.compass.ConnpassApi;
import org.iteventviewer.service.qiita.QiitaApi;
import org.iteventviewer.service.zusaar.ZusaarApi;
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

  @Provides @Singleton public OkHttpClient provideOkHttpClientForApi() {

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
    return httpClient;
  }

  @Provides @Singleton public AtndApi provideAtndApi(OkHttpClient httpClient) {

    LocalDateTimeConverter localDateTimeConverter =
        new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    Gson gson =
        new GsonBuilder().registerTypeAdapter(LocalDateTimeConverter.TYPE, localDateTimeConverter)
            .create();

    return createDefaultRestAdapterBuilder(httpClient).setEndpoint(AtndApi.ENDPOINT)
        .setConverter(new GsonConverter(gson))
        .build()
        .create(AtndApi.class);
  }

  @Provides @Singleton public ConnpassApi provideConpassApi(OkHttpClient httpClient) {

    LocalDateTimeConverter localDateTimeConverter =
        new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm:ssZ");

    Gson gson =
        new GsonBuilder().registerTypeAdapter(LocalDateTimeConverter.TYPE, localDateTimeConverter)
            .create();

    return createDefaultRestAdapterBuilder(httpClient).setEndpoint(ConnpassApi.ENDPOINT)
        .setConverter(new GsonConverter(gson))
        .build()
        .create(ConnpassApi.class);
  }

  @Provides @Singleton public ZusaarApi provideZusaarApi(OkHttpClient httpClient) {

    LocalDateTimeConverter localDateTimeConverter =
        new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm:ssZ");

    Gson gson =
        new GsonBuilder().registerTypeAdapter(LocalDateTimeConverter.TYPE, localDateTimeConverter)
            .create();

    return createDefaultRestAdapterBuilder(httpClient).setEndpoint(ZusaarApi.ENDPOINT)
        .setConverter(new GsonConverter(gson))
        .build()
        .create(ZusaarApi.class);
  }

  @Provides @Singleton public QiitaApi provideQiitaApi(OkHttpClient httpClient) {

    Gson gson = new GsonBuilder().create();

    return createDefaultRestAdapterBuilder(httpClient).setEndpoint(QiitaApi.ENDPOINT)
        .setConverter(new GsonConverter(gson))
        .build()
        .create(QiitaApi.class);
  }

  private RestAdapter.Builder createDefaultRestAdapterBuilder(OkHttpClient httpClient) {

    return new RestAdapter.Builder().setClient(new OkClient(httpClient))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);
  }
}
