package org.iteventviewer.util;

import org.iteventviewer.app.R;
import org.iteventviewer.model.Setting;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by yuki_yoshida on 15/01/30.
 */
public class SettingsUtil {

  public static Observable<Setting> stream() {

    return Observable.create(new Observable.OnSubscribe<Setting>() {
      @Override public void call(Subscriber<? super Setting> subscriber) {

        subscriber.onNext(new Setting(Setting.TYPE_CHECKBOX, R.string.setting_checkbox));
        subscriber.onNext(new Setting(Setting.TYPE_NORMAL, R.string.setting_privacy_policy));
        subscriber.onNext(new Setting(Setting.TYPE_NORMAL, R.string.setting_do_review));
        subscriber.onNext(new Setting(Setting.TYPE_NORMAL, R.string.setting_software_licences));
        subscriber.onNext(new Setting(Setting.TYPE_NORMAL, R.string.setting_test));
        subscriber.onCompleted();
      }
    });
  }
}
