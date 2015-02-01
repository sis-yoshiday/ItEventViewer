package org.iteventviewer.service.atnd.json;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;
import org.parceler.Parcel;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter public class Event extends EventBase implements Serializable {

  private static final long serialVersionUID = -1478461645054264955L;

  /**
   * キャッチ
   */
  @SerializedName("catch") String catchText;
  
  /**
   * 概要
   */
  String description;

  /**
   * イベント開催日時
   */
  @SerializedName("started_at") LocalDateTime startedAt;

  /**
   * イベント終了日時
   */
  @SerializedName("ended_at") LocalDateTime endedAt;

  /**
   * 参考URL	http://groups.google.com/group/tokyocloud
   */
  String url;

  /**
   * 開催場所	東京都中央区銀座7-2-6
   */
  String address;

  /**
   * 開催会場	Recruit Annex 1　（リクルートアネックス1）　B1F
   */
  String place;

  /**
   * 開催会場の緯度	35.6708529
   */
  double lat;

  /**
   * 開催会場の経度	139.7605287
   */
  @SerializedName("lon") double lng;

  /**
   * 主催者のユーザID
   */
  @SerializedName("owner_id") int ownerId;

  /**
   * 主催者のニックネーム
   */
  @SerializedName("owner_nickname") String ownerNickname;

  /**
   * 主催者のtwitter id
   */
  @SerializedName("owner_twitter_id") String ownerTwitterId;
}
