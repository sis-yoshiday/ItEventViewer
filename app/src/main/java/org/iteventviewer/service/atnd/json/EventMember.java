package org.iteventviewer.service.atnd.json;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.parceler.Parcel;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter
public class EventMember extends EventBase {

  /**
   * 参加者（追加取得）
   */
  List<UserContainer> users;

  @Getter
  public class UserContainer {

    User user;
  }
}
