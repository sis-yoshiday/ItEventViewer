package org.iteventviewer.model;

import java.util.Comparator;
import org.joda.time.LocalDateTime;

/**
 * Created by yuki_yoshida on 15/02/07.
 */
public interface IndexViewModel {

  public abstract String getTitle();

  public abstract LocalDateTime getStartedAt();

  public static final Comparator<IndexViewModel> START_AT_ASC_COMPARATOR =
      new Comparator<IndexViewModel>() {
        @Override public int compare(IndexViewModel lhs, IndexViewModel rhs) {
          return lhs.getStartedAt().compareTo(rhs.getStartedAt());
        }
      };
}
