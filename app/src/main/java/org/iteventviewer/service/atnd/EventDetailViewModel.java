package org.iteventviewer.service.atnd;

import lombok.Getter;
import lombok.Setter;
import org.iteventviewer.service.atnd.json.Event;
import org.iteventviewer.service.atnd.json.User;

/**
 * Created by yuki_yoshida on 15/02/01.
 */
public class EventDetailViewModel {

  public static final int TYPE_HEADER = 0;

  public static final int TYPE_DETAIL = 1;

  public static final int TYPE_MEMBER = 2;

  @Getter private int type;

  @Getter private String title;

  @Getter private Event event;

  @Getter private User user;

  private EventDetailViewModel(int type) {
    this.type = type;
  }

  public static EventDetailViewModel header(String title) {

    EventDetailViewModel model = new EventDetailViewModel(TYPE_HEADER);
    model.title = title;
    return model;
  }

  public static EventDetailViewModel detail(Event event) {

    EventDetailViewModel model = new EventDetailViewModel(TYPE_DETAIL);
    model.event = event;
    return model;
  }

  public static EventDetailViewModel user(User user) {

    EventDetailViewModel model = new EventDetailViewModel(TYPE_MEMBER);
    model.user = user;
    return model;
  }
}
