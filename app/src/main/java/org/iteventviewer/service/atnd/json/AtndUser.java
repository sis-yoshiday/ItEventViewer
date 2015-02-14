package org.iteventviewer.service.atnd.json;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import com.google.gson.annotations.SerializedName;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.iteventviewer.app.R;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter public class AtndUser {

  public static final Func1<AtndUser, Boolean> FILTER_ACCEPTED = new Func1<AtndUser, Boolean>() {
    @Override public Boolean call(AtndUser user) {
      return user.getStatus() == AtndUser.STATUS_ACCEPTED;
    }
  };

  public static final Func1<AtndUser, Boolean> FILTER_WAITING = new Func1<AtndUser, Boolean>() {
    @Override public Boolean call(AtndUser user) {
      return user.getStatus() == AtndUser.STATUS_WAITING;
    }
  };

  public static final Func2<AtndUser, AtndUser, Integer> NAME_COMPARATOR =
      new Func2<AtndUser, AtndUser, Integer>() {

        @Override public Integer call(AtndUser user, AtndUser user2) {
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

  public SpannableString getNameSpannableString(ClickableSpan clickableSpan) {

    String text = getNameString();

    Pattern pattern = Pattern.compile("@" + twitterId);
    Matcher matcher = pattern.matcher(text);

    int start = 0;
    int end = 0;
    while (matcher.find()) {
      start = matcher.start();
      end = matcher.end();
      break;
    }

    SpannableString ss = new SpannableString(text);
    ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    return ss;
  }

  public int getStatusString() {
    switch (status) {
      case STATUS_ACCEPTED:
        return R.string.status_accepted;
      case STATUS_WAITING:
        return R.string.status_waiting;
      default:
        throw new IllegalStateException("status : " + status);
    }
  }

  public boolean hasTwitterId() {
    return !TextUtils.isEmpty(twitterId);
  }
}
