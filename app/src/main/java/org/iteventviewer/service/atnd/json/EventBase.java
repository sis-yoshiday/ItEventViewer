package org.iteventviewer.service.atnd.json;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
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
}
