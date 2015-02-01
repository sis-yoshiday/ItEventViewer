package org.iteventviewer.app;

import com.orm.SugarApp;
import dagger.ObjectGraph;
import java.util.Arrays;
import java.util.List;
import net.danlew.android.joda.JodaTimeAndroid;
import org.iteventviewer.app.module.AndroidModule;

/**
 * Created by yuki_yoshida on 15/01/25.
 */
public class MyApplication extends SugarApp {

  ObjectGraph objectGraph;

  @Override public void onCreate() {
    super.onCreate();
    JodaTimeAndroid.init(this);
    objectGraph = ObjectGraph.create(getModules().toArray());
  }

  protected List<Object> getModules() {
    return Arrays.asList(new AndroidModule(this), new ApplicationModule());
  }

  public void inject(Object object) {
    objectGraph.inject(object);
  }
}
