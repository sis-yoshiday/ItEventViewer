package org.iteventviewer.service.compass.model;

import lombok.Getter;
import org.iteventviewer.service.compass.json.ConnpassEvent;

/**
 * Created by yuki_yoshida on 15/02/01.
 */
public class ConnpassEventDetailViewModel {

  public static final int TYPE_DETAIL = 0;

  @Getter private int type;

  @Getter private ConnpassEvent event;

  private ConnpassEventDetailViewModel(int type) {
    this.type = type;
  }

  public static ConnpassEventDetailViewModel detail(ConnpassEvent event) {

    ConnpassEventDetailViewModel model = new ConnpassEventDetailViewModel(TYPE_DETAIL);
    model.event = event;
    return model;
  }
}
