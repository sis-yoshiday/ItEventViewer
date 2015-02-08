package org.iteventviewer.app.module;

import android.net.Uri;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.ThreadEnforcer;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import org.iteventviewer.app.BuildConfig;
import org.iteventviewer.app.MyApplication;
import org.iteventviewer.app.main.CategorySettingsFragment;
import org.iteventviewer.common.AndroidBus;
import timber.log.Timber;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Module(
    injects = { CategorySettingsFragment.CategoryAdapter.class },
    library = true) public class ApplicationModule {

  private final MyApplication app;

  public ApplicationModule(MyApplication app) {
    this.app = app;
  }

  @Provides @Singleton public Picasso providePicasso() {

    OkHttpClient client = new OkHttpClient();
    client.setConnectTimeout(3, TimeUnit.SECONDS);
    client.setReadTimeout(10, TimeUnit.SECONDS);
    client.setWriteTimeout(10, TimeUnit.SECONDS);
    try {
      File cacheDir = new File(app.getCacheDir(), "images");
      Cache cache = new Cache(cacheDir, 5 * 1024 * 1024);
      client.setCache(cache);
    } catch (IOException e) {
      Timber.e(e, "Unable to install disk cache.");
    }

    return new Picasso.Builder(app).indicatorsEnabled(BuildConfig.DEBUG)
        .downloader(new OkHttpDownloader(client))
        .loggingEnabled(BuildConfig.DEBUG)
        .listener(new Picasso.Listener() {
          @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
            Timber.e(exception, "image load failed : " + uri);
          }
        })
        .build();
  }

  @Provides @Singleton public AndroidBus getEventBus() {
    return new AndroidBus(ThreadEnforcer.ANY);
  }
}
