package org.iteventviewer.service.atnd.json;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.iteventviewer.app.AtndEventDetailActivity;
import org.iteventviewer.app.R;
import org.iteventviewer.service.atnd.AtndEventSearchQuery;
import org.joda.time.LocalDateTime;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter public class AtndEventBase implements Serializable {

  /**
   * イベントID（共通）
   */
  @SerializedName("event_id") long eventId;

  /**
   * タイトル（共通）
   */
  String title;

  /**
   * ATNDのURL（共通）
   */
  @SerializedName("event_url") String eventUrl;

  /**
   * 定員（共通）
   */
  Integer limit;

  /**
   * 参加者（共通）
   */
  int accepted;

  /**
   * 補欠者（共通）
   */
  int waiting;

  /**
   * 更新日時（共通）
   */
  @SerializedName("updated_at") LocalDateTime updatedAt;

  public boolean hasLimit() {
    return limit != null;
  }

  public final int getMemberFetchCount() {

    if (limit > AtndEventSearchQuery.MAX_COUNT) {
      // 定員が検索可能件数オーバー
      return AtndEventSearchQuery.MAX_COUNT;
    } else if (accepted > AtndEventSearchQuery.MAX_COUNT) {
      // 参加者が検索可能件数オーバー
      return AtndEventSearchQuery.MAX_COUNT;
    } else if (accepted + waiting > AtndEventSearchQuery.MAX_COUNT) {
      // 参加者とキャンセル待ちの合計が検索可能件数オーバー
      return AtndEventSearchQuery.MAX_COUNT;
    } else {
      return accepted + waiting;
    }
  }

  public String getEventUrlText(Context context) {
    if (TextUtils.isEmpty(eventUrl)) {
      return context.getString(R.string.no_url);
    }
    return eventUrl;
  }

  public int getAcceptedColor(Context context) {

    if (limit == null) {
      return context.getResources().getColor(R.color.success);
    }

    if (accepted < limit * 0.6) {
      // 余裕がある
      return context.getResources().getColor(R.color.success);
    } else if (accepted < limit) {
      // 埋まりそう
      return context.getResources().getColor(R.color.warning);
    } else {
      // 埋まってる
      return context.getResources().getColor(R.color.danger);
    }
  }

  public int getWaitingColor(Context context) {

    if (waiting == 0) {
      return context.getResources().getColor(R.color.success);
    } else {
      return context.getResources().getColor(R.color.danger);
    }
  }
}
