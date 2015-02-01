package org.iteventviewer.common;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by yuki_yoshida on 15/01/29.
 */
public class AndroidBus extends Bus {
  private final Handler mainThread = new Handler(Looper.getMainLooper());

  public AndroidBus(ThreadEnforcer enforcer) {
    super(enforcer);
  }

  @Override public void post(final Object event) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      super.post(event);
    } else {
      mainThread.post(new Runnable() {
        @Override public void run() {
          AndroidBus.super.post(event);
        }
      });
    }
  }
}
