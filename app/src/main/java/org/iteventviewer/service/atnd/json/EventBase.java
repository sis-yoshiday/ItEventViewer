package org.iteventviewer.service.atnd.json;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.iteventviewer.service.atnd.EventSearchQuery;
import org.joda.time.LocalDateTime;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter public class EventBase implements Serializable {

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

  public boolean isLimitOver() {
    return waiting > 0;
  }

  public final int getMemberFetchCount() {

    if (limit > EventSearchQuery.MAX_COUNT) {
      // 定員が検索可能件数オーバー
      return EventSearchQuery.MAX_COUNT;
    } else if (accepted > EventSearchQuery.MAX_COUNT) {
      // 参加者が検索可能件数オーバー
      return EventSearchQuery.MAX_COUNT;
    } else if (accepted + waiting > EventSearchQuery.MAX_COUNT) {
      // 参加者とキャンセル待ちの合計が検索可能件数オーバー
      return EventSearchQuery.MAX_COUNT;
    } else {
      return accepted + waiting;
    }
  }
}
