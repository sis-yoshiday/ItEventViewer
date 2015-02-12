package org.iteventviewer.service.doorkeeper.model;

import android.support.annotation.Nullable;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.iteventviewer.app.R;
import org.iteventviewer.model.IndexViewModel;
import org.iteventviewer.service.doorkeeper.json.DoorkeeperEvent;
import org.iteventviewer.service.doorkeeper.json.DoorkeeperGroup;
import org.iteventviewer.util.Region;
import org.joda.time.LocalDateTime;
import rx.functions.Func1;

/**
 * Created by yuki_yoshida on 15/02/09.
 */
@Getter public class DoorkeeperIndexViewModel extends IndexViewModel {

  private DoorkeeperEvent event;

  public DoorkeeperIndexViewModel(DoorkeeperEvent event) {
    super(R.string.doorkeeper);
    this.event = event;
  }

  @Override public String getTitle() {
    return event.getTitle();
  }

  @Override public LocalDateTime getStartedAt() {
    return event.getStartsAt();
  }

  public static Func1<DoorkeeperIndexViewModel, Boolean> filter(@Nullable final Region region,
      final Set<String> categories) {
    return new Func1<DoorkeeperIndexViewModel, Boolean>() {
      @Override public Boolean call(DoorkeeperIndexViewModel indexViewModel) {
        DoorkeeperEvent event = indexViewModel.getEvent();
        return filterRegion(event, region) && filterKeyword(event, categories);
      }
    };
  }

  private static boolean filterRegion(DoorkeeperEvent event, Region region) {
    // NOTE : APIの制約により地域は取得後にフィルタする
    if (region != null) {
      for (String pref : region.getPrefs()) {
        if (event.getAddress().contains(pref)) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  private static boolean filterKeyword(DoorkeeperEvent event, Set<String> categories) {
    // NOTE : APIの制約によりキーワードは取得後にフィルタする
    for (String category : categories) {

      if (event.getTitle().contains(category)) {
        return true;
      }
      if (event.getDescription().contains(category)) {
        return true;
      }
      DoorkeeperGroup group = event.getGroup();
      if (group != null) {
        if (group.getName().contains(category)) {
          return true;
        }
        if (group.getDescription().contains(category)) {
          return true;
        }
      }
    }
    return false;
  }
}
