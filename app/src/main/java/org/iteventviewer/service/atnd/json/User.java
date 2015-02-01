package org.iteventviewer.service.atnd.json;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter public class User {

  public static final Func1<User, Boolean> FILTER_ACCEPTED = new Func1<User, Boolean>() {
    @Override public Boolean call(User user) {
      return user.getStatus() == User.STATUS_ACCEPTED;
    }
  };

  public static final Func1<User, Boolean> FILTER_WAITING = new Func1<User, Boolean>() {
    @Override public Boolean call(User user) {
      return user.getStatus() == User.STATUS_WAITING;
    }
  };

  public static final Func2<User, User, Integer> NAME_COMPARATOR =
      new Func2<User, User, Integer>() {

        @Override public Integer call(User user, User user2) {
          return user.getNickname().compareTo(user2.getNickname());
        }
      };

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

  public String getNameString() {

    StringBuilder builder = new StringBuilder(nickname);
    if (hasTwitterId()) {
      builder.append(String.format("(@%s)", twitterId));
    }
    return builder.toString();
  }

  public boolean hasTwitterId() {
    return !TextUtils.isEmpty(twitterId);
  }
}
