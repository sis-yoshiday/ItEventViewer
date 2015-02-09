package org.iteventviewer.model;

import java.util.Comparator;
import lombok.Getter;
import org.joda.time.LocalDateTime;

/**
 * Created by yuki_yoshida on 15/02/07.
 */
@Getter public abstract class IndexViewModel {

  private int tag;

  public IndexViewModel(int tag) {
    this.tag = tag;
  }

  public abstract String getTitle();

  public abstract LocalDateTime getStartedAt();

  public static final Comparator<? super IndexViewModel> START_AT_ASC_COMPARATOR =
      new Comparator<IndexViewModel>() {
        @Override public int compare(IndexViewModel lhs, IndexViewModel rhs) {
          return lhs.getStartedAt().compareTo(rhs.getStartedAt());
        }
      };
}
