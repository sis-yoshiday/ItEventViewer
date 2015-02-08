package org.iteventviewer.model;

import lombok.Getter;
import org.iteventviewer.app.R;
import org.iteventviewer.service.atnd.json.Event;

/**
 * Created by yuki_yoshida on 15/02/07.
 */
@Getter
public class IndexViewModel {

  private int tag;

  private Event event;

  public IndexViewModel(int tag) {
    this.tag = tag;
  }

  public static IndexViewModel atnd(Event event) {
    IndexViewModel model = new IndexViewModel(R.string.atnd);
    model.event = event;
    return model;
  }
}
