package org.iteventviewer.service.atnd.model;

import android.support.annotation.Nullable;
import lombok.Getter;
import org.iteventviewer.app.R;
import org.iteventviewer.util.Region;
import org.iteventviewer.model.IndexViewModel;
import org.iteventviewer.service.atnd.json.AtndEvent;
import org.joda.time.LocalDateTime;
import rx.functions.Func1;

/**
 * Created by yuki_yoshida on 15/02/09.
 */
@Getter
public class AtndIndexViewModel extends IndexViewModel {

  private AtndEvent event;

  public AtndIndexViewModel(AtndEvent event) {
    super(R.string.atnd);
    this.event = event;
  }

  @Override public String getTitle() {
    return event.getTitle();
  }

  @Override public LocalDateTime getStartedAt() {
    return event.getStartedAt();
  }

  public static Func1<AtndIndexViewModel, Boolean> filter(@Nullable final Region region) {
    return new Func1<AtndIndexViewModel, Boolean>() {
      @Override public Boolean call(AtndIndexViewModel indexViewModel) {
        // NOTE : APIの制約により地域は取得後にフィルタする
        if (region != null) {
          for (String pref : region.getPrefs()) {
            if (indexViewModel.getEvent().getAddress().contains(pref)) {
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
