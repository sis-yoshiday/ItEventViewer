package org.iteventviewer.service.compass.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import lombok.Getter;
import org.iteventviewer.app.R;
import org.iteventviewer.model.BaseIndexViewModel;
import org.iteventviewer.util.Region;
import org.iteventviewer.model.IndexViewModel;
import org.iteventviewer.service.compass.json.ConnpassEvent;
import org.joda.time.LocalDateTime;
import rx.functions.Func1;

/**
 * Created by yuki_yoshida on 15/02/09.
 */
@Getter
public class ConnpassIndexViewModel extends BaseIndexViewModel {

  private ConnpassEvent event;

  public ConnpassIndexViewModel(ConnpassEvent event) {
    super(R.string.connpass);
    this.event = event;
  }

  @Override public String getTitle() {
    return event.getTitle();
  }

  @Override public LocalDateTime getStartedAt() {
    return event.getStartedAt();
  }

  public static Func1<ConnpassIndexViewModel, Boolean> filter(@Nullable final Region region) {
    return new Func1<ConnpassIndexViewModel, Boolean>() {
      @Override public Boolean call(ConnpassIndexViewModel indexViewModel) {
        // NOTE : APIの制約により地域は取得後にフィルタする
        if (region != null) {
          for (String pref : region.getPrefs()) {
            if (indexViewModel.getEvent().getAddressAndPlaceString().contains(pref)) {
              return true;
            }
          }
          return false;
        } else {
          return true;
        }
      }
    };
  }
}
