package org.iteventviewer.service.compass.json;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Getter;
import org.iteventviewer.app.R;
import org.iteventviewer.util.GeoUtil;
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
  private Integer limit;

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
  private int accepted;

  /**
   * 補欠者数
   */
  private int waiting;

  /**
   * 更新日時
   */
  @SerializedName("updated_at") private LocalDateTime updatedAt;

  public boolean hasLimit() {
    return limit != null;
  }

  public boolean hasLocation() {
    return lat != null && lng != null;
  }

  public String getOwnerString() {
    return "by " + ownerDisplayName;
  }

  public String getEventUrlText(Context context) {
    if (TextUtils.isEmpty(eventUrl)) {
      return context.getString(R.string.no_url);
    }
    return eventUrl;
  }

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

  public SpannableString getEventDateSpannableString(final Context context) {

    String text = getEventDateString();

    SpannableString ss = new SpannableString(text);
    ss.setSpan(new ClickableSpan() {
      @Override public void onClick(View widget) {

        // カレンダーアプリを呼び出すIntentの生成
        Intent intent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
        //スケジュールのタイトル
        intent.putExtra(CalendarContract.Events.TITLE, title);
        //スケジュールの開始時刻 ゼロで現在時刻
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startedAt.toDate().getTime());
        if (endedAt != null) {
          //スケジュールの終了時刻　ゼロで現在時刻＋１時間
          intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endedAt.toDate().getTime());
        }
        //スケジュールの場所
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, String.format("%f,%f", lat, lng));
        //スケジュールのアクセスレベル
        intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
        //スケジュールの同時持ちの可否
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
        //Intentを呼び出す
        context.startActivity(intent);
      }
    }, 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

    return ss;
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
    return builder.toString().isEmpty() ? "未設定" : builder.toString();
  }

  public SpannableString getAddressAndPlaceSpannableString(final Context context) {

    String text = getAddressAndPlaceString();

    SpannableString ss = new SpannableString(text);
    ss.setSpan(new ClickableSpan() {
      @Override public void onClick(View widget) {

        context.startActivity(GeoUtil.intent(lat, lng, 20, place));
      }
    }, 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

    return ss;
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
