package org.iteventviewer.service.compass.json;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Getter;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by yuki_yoshida on 15/02/09.
 */
@Getter public class ConnpassEvent implements Serializable {

  private static final long serialVersionUID = 5838018057111474337L;

  /**
   * イベント参加タイプ：参加受付あり
   */
  public static final String EVENT_TYPE_PARTICIPATION = "participation";

  /**
   * イベント参加タイプ：告知のみ
   */
  public static final String EVENT_TYPE_ADVERTISEMENT = "advertisement";

  /**
   * イベントID
   */
  @SerializedName("event_id") private long eventId;

  /**
   * タイトル
   */
  private String title;

  /**
   * キャッチ
   */
  @SerializedName("catch") private String catchText;

  /**
   * 概要（HTML）
   */
  private String description;

  /**
   * compassのURL
   */
  @SerializedName("event_url") private String eventUrl;

  /**
   * Twitterのハッシュタグ
   */
  @SerializedName("hash_tag") private String hashTag;

  /**
   * イベント開催日時 2012-04-17T18:30:00+09:00
   */
  @SerializedName("started_at") private LocalDateTime startedAt;

  /**
   * イベント終了日時 2012-04-17T18:30:00+09:00
   */
  @SerializedName("ended_at") private LocalDateTime endedAt;

  /**
   * 定員
   */
  private int limit;

  /**
   * イベント参加タイプ
   */
  @SerializedName("event_type") private String eventType;

  /**
   * グループ
   */
  private Series series;

  /**
   * 開催場所
   */
  private String address;

  /**
   * 開催会場
   */
  private String place;

  /**
   * 開催会場の緯度
   */
  private double lat;

  /**
   * 開催会場の経度
   */
  @SerializedName("lon") private double lng;

  /**
   * 管理者のID
   */
  @SerializedName("owner_id") private int ownerId;

  /**
   * 管理者のニックネーム
   */
  @SerializedName("owner_nickname") private String ownerNickname;

  /**
   * 管理者の表示名
   */
  @SerializedName("owner_display_name") private String ownerDisplayName;

  /**
   * 参加者数
   */
  private int accept;

  /**
   * 補欠者数
   */
  private int waiting;

  /**
   * 更新日時
   */
  @SerializedName("updated_at") private LocalDateTime updatedAt;

  public String getEventDateString() {
    StringBuilder builder =
        new StringBuilder(startedAt.toString(DateTimeFormat.forPattern("yyyy/MM/dd HH:mm")));
    builder.append(" 〜 ");
    if (endedAt != null) {
      if (startedAt.getYear() == endedAt.getYear()
          && startedAt.getMonthOfYear() == endedAt.getMonthOfYear()
          && startedAt.getDayOfMonth() == startedAt.getDayOfMonth()) {
        builder.append(endedAt.toString(DateTimeFormat.forPattern("HH:mm")));
      } else {
        builder.append(endedAt.toString(DateTimeFormat.forPattern("yyyy/MM/dd HH:mm")));
      }
    }
    return builder.toString();
  }

  public boolean isLimitOver() {
    return waiting > 0;
  }
}
