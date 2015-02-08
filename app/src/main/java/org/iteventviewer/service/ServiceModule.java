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
import org.iteventviewer.app.EventDetailActivity;
import org.iteventviewer.app.main.CategorySettingsFragment;
import org.iteventviewer.app.main.IndexFragment;
import org.iteventviewer.common.LocalDateTimeConverter;
import org.iteventviewer.service.atnd.AtndService;
import org.iteventviewer.service.qiita.QiitaClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Module(injects = {
    IndexFragment.class, CategorySettingsFragment.class, EventDetailActivity.class
}, complete = false, library = true) public class ServiceModule {

  private Context context;

  public ServiceModule(Context context) {
    this.context = context;
  }

  @Provides @Singleton public AtndService provideAtndService() {

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
      Cache cache = new Cache(cacheDir, 5 * 1024 * 1024);
      httpClient.setCache(cache);
    } catch (IOException e) {
      Timber.e(e, "Unable to install disk cache.");
    }

    return new RestAdapter.Builder().setEndpoint(AtndService.ENDPOINT)
        .setClient(new OkClient(httpClient))
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .build()
        .create(AtndService.class);
  }

  @Provides @Singleton public QiitaClient provideQiitaService() {

    Gson gson = new GsonBuilder().create();

    OkHttpClient httpClient = new OkHttpClient();
    httpClient.setConnectTimeout(3, TimeUnit.SECONDS);
    httpClient.setReadTimeout(3, TimeUnit.SECONDS);

    return new RestAdapter.Builder().setEndpoint(QiitaClient.ENDPOINT)
        .setClient(new OkClient(httpClient))
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .build()
        .create(QiitaClient.class);
  }
}
