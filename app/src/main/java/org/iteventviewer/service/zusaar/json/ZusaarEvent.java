package org.iteventviewer.service.zusaar.json;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Getter;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by yuki_yoshida on 15/02/10.
 */

@Getter public class ZusaarEvent implements Serializable {

  /**
   * 無料
   */
  public static final int PAY_TYPE_FREE = 0;

  /**
   * 現地払い
   */
  public static final int PAY_TYPE_PAYMENT_IN_SPOT = 1;

  /**
   * 前払い
   */
  public static final int PAY_TYPE_PAYMENT_IN_ADVANCE = 2;

  private static final long serialVersionUID = 7966056744743557450L;

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
   * ZusaarのURL
   */
  @SerializedName("event_url") private String eventUrl;

  /**
   * イベント開催日時 2012-04-17T18:30:00+09:00
   */
  @SerializedName("started_at") private LocalDateTime startedAt;

  /**
   * イベント終了日時 2012-04-17T18:30:00+09:00
   */
  @SerializedName("ended_at") private LocalDateTime endedAt;

  /**
   * 無料・有料イベント
   */
  @SerializedName("pay_type") private int payType;

  /**
   * 定員
   */
  private int limit;

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
  private Double lat;

  /**
   * 開催会場の経度
   */
  @SerializedName("lon") private Double lng;

  /**
   * 管理者のID
   */
  @SerializedName("owner_id") private int ownerId;

  /**
   * 管理者のプロフィールURL
   */
  @SerializedName("owner_profile_url") private String ownerProfileUrl;

  /**
   * 管理者のニックネーム
   */
  @SerializedName("owner_nickname") private String ownerNickname;

  /**
   * 参加者数
   */
  private int accepted;

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

  public @NonNull String getAddressAndPlaceString() {

    StringBuilder builder = new StringBuilder();
    boolean addressIsEmpty = TextUtils.isEmpty(address);
    if (!addressIsEmpty) {
      builder.append(address);
    }
    if (!TextUtils.isEmpty(place)) {
      if (!addressIsEmpty) {
        builder.append(" ");
      }
      builder.append(place);
    }
    return builder.toString();
  }

  public boolean isLimitOver() {
    return waiting > 0;
  }
}

