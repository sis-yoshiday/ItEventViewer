package org.iteventviewer.service.doorkeeper.json;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Getter;
import org.joda.time.LocalDateTime;

/**
 * Created by yuki_yoshida on 15/02/13.
 */
@Getter
public class DoorkeeperEvent implements Serializable {

  private static final long serialVersionUID = -276273757783627773L;

  /**
   * イベントID
   */
  private long id;

  /**
   * タイトル
   */
  private String title;

  /**
   * 概要
   */
  private String description;

  /**
   * バナー画像
   */
  private String banner;

  /**
   * イベントURL
   */
  @SerializedName("public_url") private String publicUrl;

  /**
   * 開始日時
   */
  @SerializedName("starts_at") private LocalDateTime startsAt;

  /**
   * 終了日時
   */
  @SerializedName("ends_at") private LocalDateTime endsAt;

  /**
   * 募集日時
   */
  @SerializedName("publish_at") private LocalDateTime publishAt;

  /**
   * 開催住所
   */
  private String address;

  /**
   * 開催場所
   */
  private String venue;

  /**
   * 緯度
   */
  private Long lat;

  /**
   * 経度
   */
  @SerializedName("long") private Long lng;

  /**
   * 定員
   */
  @SerializedName("ticket_limit") private int ticketLimit;

  /**
   * 参加者数
   */
  private int participants;

  /**
   * キャンセル待ち数
   */
  private int waitlisted;

  /**
   * グループ
   */
  private DoorkeeperGroup group;

  /**
   * 更新日時
   */
  @SerializedName("updated_at") private LocalDateTime updatedAt;
}
