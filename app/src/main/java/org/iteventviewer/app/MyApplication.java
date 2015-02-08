package org.iteventviewer.app;

import android.content.Context;
import com.orm.SugarApp;
import dagger.ObjectGraph;
import java.util.Arrays;
import java.util.List;
import net.danlew.android.joda.JodaTimeAndroid;
import org.iteventviewer.app.module.AndroidModule;
import org.iteventviewer.app.module.ApplicationModule;
import org.iteventviewer.service.ServiceModule;
import timber.log.Timber;

/**
 * Created by yuki_yoshida on 15/01/25.
 */
public class MyApplication extends SugarApp {

  ObjectGraph objectGraph;

  @Override public void onCreate() {
    super.onCreate();
    JodaTimeAndroid.init(this);

    // NOTE : ログ以外の振る舞いをさせたいならここを変更する
    if (BuildConfig.DEBUG) {
      // Log
      Timber.plant(new Timber.DebugTree());
    }

    objectGraph = ObjectGraph.create(getModules().toArray());
  }

  protected List<Object> getModules() {
    return Arrays.asList(new ApplicationModule(this), new AndroidModule(this),
        new ServiceModule(this));
  }

  public void inject(Object object) {
    objectGraph.inject(object);
  }

  public static MyApplication get(Context context) {
    return (MyApplication) context.getApplicationContext();
  }
}
