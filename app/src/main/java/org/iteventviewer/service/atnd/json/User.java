package org.iteventviewer.service.atnd.json;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.parceler.Parcel;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter
public class User {

  /**
   * キャンセル待ち
   */
  public static final int STATUS_WAITING = 0;

  /**
   * 出席
   */
  public static final int STATUS_ACCEPTED = 1;

  /**
   * 参加者のユーザID
   */
  @SerializedName("user_id") int userId;

  /**
   * 参加者のニックネーム
   */
  String nickname;

  /**
   * 参加者のtwitter id
   */
  @SerializedName("twitter_id") String twitterId;

  /**
   * 参加者のステータス
   */
  int status;
}
