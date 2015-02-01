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

  public static final int TYPE_MEMBER = 1;

  public static final int TYPE_MEMBER_EMPTY = 2;

  @Getter private int type;

  @Getter @Setter private Event event;

  @Getter @Setter private User user;

  private EventDetailViewModel(int type) {
    this.type = type;
  }

  public static EventDetailViewModel header(Event event) {

    EventDetailViewModel model = new EventDetailViewModel(TYPE_HEADER);
    model.setEvent(event);
    return model;
  }

  public static EventDetailViewModel user(User user) {

    EventDetailViewModel model = new EventDetailViewModel(TYPE_MEMBER);
    model.setUser(user);
    return model;
  }
}
