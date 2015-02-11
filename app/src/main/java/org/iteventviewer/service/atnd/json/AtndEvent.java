package org.iteventviewer.service.atnd.json;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.iteventviewer.app.R;
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

  public boolean isEnded() {
    LocalDateTime now = new LocalDateTime(DateTimeZone.getDefault());
    if (endedAt != null) {
      return now.compareTo(endedAt) > 0;
    }
    // NOTE : 適当だが開始日時の24時間後なら終了済みとする
    return now.compareTo(startedAt.plus(Period.days(1))) > 0;
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

  public String getAddressAndPlaceString() {

    if (TextUtils.isEmpty(place)) {
      return address;
    }
    return address + " " + place;
  }

  public SpannableString getAddressAndPlaceSpannableString(final Context context) {

    String text = getAddressAndPlaceString();

    SpannableString ss = new SpannableString(text);
    ss.setSpan(new ClickableSpan() {
      @Override public void onClick(View widget) {

        context.startActivity(
            new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("geo:%f,%f", lat, lng))));
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
