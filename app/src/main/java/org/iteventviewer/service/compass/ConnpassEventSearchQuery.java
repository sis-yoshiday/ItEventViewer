package org.iteventviewer.service.compass;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.Setter;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDateTime;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
public class ConnpassEventSearchQuery {

  public static final int MAX_COUNT = 100;

  /**
   * 表示順：更新日時順
   */
  public static final int ORDER_UPDATE_AT = 1;

  /**
   * 表示順：開催日時順
   */
  public static final int ORDER_STARTED_AT = 2;

  /**
   * 表示順：新着順
   */
  public static final int ORDER_LATEST = 3;

  /**
   * イベントID
   */
  public static final String EVENT_ID = "event_id";

  /**
   * キーワード（AND）
   */
  public static final String KEYWORD = "keyword";

  /**
   * キーワード（OR）
   */
  public static final String KEYWORD_OR = "keyword_or";

  /**
   * イベント開催年月
   */
  public static final String YM = "ym";

  /**
   * イベント開催年月日
   */
  public static final String YMD = "ymd";

  /**
   * 参加者のニックネーム
   */
  public static final String NICKNAME = "nickname";

  /**
   * 主催者のニックネーム
   */
  public static final String OWNER_NICKNAME = "owner_nickname";

  /**
   * グループID
   */
  public static final String SERIES_ID = "series_id";

  /**
   * 検索の開始位置
   */
  public static final String START = "start";

  /**
   * 検索結果の表示順
   */
  public static final String ORDER = "order";

  /**
   * 取得件数
   */
  public static final String COUNT = "count";

  /**
   * レスポンス形式
   */
  public static final String FORMAT = "format";

  public static class Builder {

    private static final char SEPARATOR = ',';

    private Map<String, String> query;

    private Set<Long> eventIds;

    private Set<String> keywords;

    private Set<String> keywordsOr;

    private Set<Integer> yms;

    private Set<Integer> ymds;

    private Set<String> nicknames;

    private Set<String> ownerNicknames;

    private Set<Integer> seriesIds;

    private int start;

    private int order;

    private int count;

    private static final String FORMAT_JSON = "json";

    public Builder() {
      query = new HashMap<>();

      eventIds = new HashSet<>();
      keywords = new HashSet<>();
      keywordsOr = new HashSet<>();
      yms = new LinkedHashSet<>();
      ymds = new LinkedHashSet<>();
      nicknames = new HashSet<>();
      ownerNicknames = new HashSet<>();
      seriesIds = new HashSet<>();
      start = 1;
      order = ORDER_UPDATE_AT;
      count = 10;
    }

    public Builder addEventId(long eventId) {
      eventIds.add(eventId);
      return this;
    }

    public Builder addKeyword(String keyword) {
      keywords.add(keyword);
      return this;
    }

    public Builder addKeywordOr(String keyword) {
      keywordsOr.add(keyword);
      return this;
    }

    public Builder addKeywordsOr(Set<String> keywords) {
      keywordsOr.addAll(keywords);
      return this;
    }

    public Builder addYmd(int year, int month, int day) {
      ymds.add(Integer.valueOf(String.format("%04d%02d%02d", year, month, day)));
      return this;
    }

    public Builder addYmds(int days) {
      LocalDateTime date = new LocalDateTime();
      for (int i = 0; i < days; i++) {
        addYmd(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
        date = date.withFieldAdded(DurationFieldType.days(), 1);
      }
      return this;
    }

    public Builder addNickname(String nickname) {
      nicknames.add(nickname);
      return this;
    }

    public Builder addOwnerNickname(String nickname) {
      ownerNicknames.add(nickname);
      return this;
    }

    public Builder addSeriesId(int seriesId) {
      seriesIds.add(seriesId);
      return this;
    }

    public Builder start(int start) {
      this.start = start;
      return this;
    }

    public Builder order(int order) {
      this.order = order;
      return this;
    }

    public Builder count(int count) {
      this.count = count;
      return this;
    }

    public Map<String, String> build() {

      putQuery(EVENT_ID, eventIds);
      putQuery(KEYWORD, keywords);
      putQuery(KEYWORD_OR, keywordsOr);
      putQuery(YM, yms);
      putQuery(YMD, ymds);
      putQuery(NICKNAME, nicknames);
      putQuery(OWNER_NICKNAME, ownerNicknames);
      putQuery(SERIES_ID, seriesIds);
      query.put(START, String.valueOf(start));
      query.put(ORDER, String.valueOf(order));
      query.put(COUNT, String.valueOf(count));
      query.put(FORMAT, FORMAT_JSON);
      return query;
    }

    protected void putQuery(String key, Set set) {
      if (!set.isEmpty()) {
        query.put(key, buildQuery(set));
      }
    }

    private static <T> String buildQuery(Iterable<T> iterable) {
      return FluentIterable.from(iterable).join(Joiner.on(SEPARATOR));
    }
  }
}
