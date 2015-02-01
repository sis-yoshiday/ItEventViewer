package org.iteventviewer.app.module;

import android.content.Context;
import android.location.LocationManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.iteventviewer.app.MyApplication;

/**
 * Created by yuki_yoshida on 15/02/01.
 */
@Module(library = true)
public class AndroidModule {

  private final MyApplication application;

  public AndroidModule(MyApplication application) {
    this.application = application;
  }

  @Provides @Singleton @ForApplication Context provideApplicationContext() {
    return application;
  }

  @Provides @Singleton LocationManager provideLocationManager() {
    return (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
  }
}