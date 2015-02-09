package org.iteventviewer.service.compass.model;

import lombok.Getter;
import org.iteventviewer.service.atnd.json.AtndEvent;
import org.iteventviewer.service.atnd.json.AtndUser;
import org.iteventviewer.service.compass.json.ConnpassEvent;

/**
 * Created by yuki_yoshida on 15/02/01.
 */
public class ConnpassEventDetailViewModel {

  public static final int TYPE_HEADER = 0;

  public static final int TYPE_DETAIL = 1;

  @Getter private int type;

  @Getter private String title;

  @Getter private ConnpassEvent event;

  private ConnpassEventDetailViewModel(int type) {
    this.type = type;
  }

  public static ConnpassEventDetailViewModel header(String title) {

    ConnpassEventDetailViewModel model = new ConnpassEventDetailViewModel(TYPE_HEADER);
    model.title = title;
    return model;
  }

  public static ConnpassEventDetailViewModel detail(ConnpassEvent event) {

    ConnpassEventDetailViewModel model = new ConnpassEventDetailViewModel(TYPE_DETAIL);
    model.event = event;
    return model;
  }
}
