package org.iteventviewer.app.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.ThreadEnforcer;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.iteventviewer.app.BuildConfig;
import org.iteventviewer.app.EventDetailActivity;
import org.iteventviewer.app.main.IndexFragment;
import org.iteventviewer.app.main.RegionSettingsFragment;
import org.iteventviewer.common.AndroidBus;
import org.iteventviewer.common.LocalDateTimeConverter;
import org.iteventviewer.service.atnd.AtndService;
import org.iteventviewer.service.qiita.QiitaService;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Module(injects = {
    IndexFragment.class, RegionSettingsFragment.class, EventDetailActivity.class
}, library = true) public class ApplicationModule {

  @Provides @Singleton public AtndService getAtndService() {

    LocalDateTimeConverter localDateTimeConverter =
        new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    Gson gson =
        new GsonBuilder().registerTypeAdapter(LocalDateTimeConverter.TYPE, localDateTimeConverter)
            .create();

    return new RestAdapter.Builder().setEndpoint(AtndService.ENDPOINT)
        .setClient(new OkClient())
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .build()
        .create(AtndService.class);
  }

  @Provides @Singleton public QiitaService getQiitaService() {

    Gson gson = new GsonBuilder().create();

    return new RestAdapter.Builder().setEndpoint(QiitaService.ENDPOINT)
        .setClient(new OkClient())
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .build()
        .create(QiitaService.class);
  }

  @Provides @Singleton public AndroidBus getEventBus() {
    return new AndroidBus(ThreadEnforcer.ANY);
  }
}
