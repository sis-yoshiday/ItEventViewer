package org.iteventviewer.service.atnd.json;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.iteventviewer.app.R;
import org.iteventviewer.util.GeoUtil;
import org.iteventviewer.util.SnsUtil;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter public class AtndEvent extends AtndEventBase implements Serializable {

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
  Double lat;

  /**
   * 開催会場の経度	139.7605287
   */
  @SerializedName("lon") Double lng;

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

  public String getOwnerString() {

    StringBuilder builder = new StringBuilder("by " + ownerNickname);
    if (hasTwitterId()) {
      builder.append(String.format("(@%s)", ownerTwitterId));
    }
    return builder.toString();
  }

  public SpannableString getOwnerSpannableString(ClickableSpan clickableSpan) {

    String text = getOwnerString();

    Pattern pattern = Pattern.compile("@" + ownerTwitterId);
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

  public String getRefUrlText(Context context) {
    if (TextUtils.isEmpty(url)) {
      return context.getString(R.string.no_url);
    }
    return url;
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

  public boolean hasLocation() {
    return lat != null && lng != null;
  }

  private boolean hasTwitterId() {
    return !TextUtils.isEmpty(ownerTwitterId);
  }
}
