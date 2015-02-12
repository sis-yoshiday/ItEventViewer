package org.iteventviewer.service.atnd.model;

import lombok.Getter;
import org.iteventviewer.service.atnd.json.AtndEvent;
import org.iteventviewer.service.atnd.json.AtndUser;

/**
 * Created by yuki_yoshida on 15/02/01.
 */
public class AtndEventDetailViewModel {

  public static final int TYPE_HEADER = 0;

  public static final int TYPE_DETAIL = 1;

  public static final int TYPE_MEMBER = 2;

  @Getter private int type;

  @Getter private Header header;

  @Getter private AtndEvent event;

  @Getter private AtndUser user;

  private AtndEventDetailViewModel(int type) {
    this.type = type;
  }

  public static AtndEventDetailViewModel header(Header header) {

    AtndEventDetailViewModel model = new AtndEventDetailViewModel(TYPE_HEADER);
    model.header = header;
    return model;
  }

  public static AtndEventDetailViewModel detail(AtndEvent event) {

    AtndEventDetailViewModel model = new AtndEventDetailViewModel(TYPE_DETAIL);
    model.event = event;
    return model;
  }

  public static AtndEventDetailViewModel user(AtndUser user) {

    AtndEventDetailViewModel model = new AtndEventDetailViewModel(TYPE_MEMBER);
    model.user = user;
    return model;
  }

  @Getter
  public static class Header {

    private String title;
    private int count;

    public Header(String title, int count) {
      this.title = title;
      this.count = count;
    }
  }
}
