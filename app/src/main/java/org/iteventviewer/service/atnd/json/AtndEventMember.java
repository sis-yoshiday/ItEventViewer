package org.iteventviewer.service.atnd.json;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter
public class AtndEventMember extends AtndEventBase {

  /**
   * 参加者（追加取得）
   */
  List<UserContainer> users;

  @Getter
  public class UserContainer {

    AtndUser user;
  }
}
