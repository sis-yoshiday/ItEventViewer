package org.iteventviewer.service.atnd;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Setter;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDateTime;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
public class EventSearchQuery extends MemberSearchQuery {

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

  public static class Builder extends MemberSearchQuery.Builder {

    @Setter private Set<String> keywords;

    @Setter private Set<String> keywordsOr;

    @Setter private Set<Integer> yms;

    @Setter private Set<Integer> ymds;

    public Builder() {
      super();
      keywords = new HashSet<>();
      keywordsOr = new HashSet<>();
      yms = new HashSet<>();
      ymds = new HashSet<>();
    }

    public Builder addKeyword(String keyword) {
      keywords.add(keyword);
      return this;
    }

    public Builder addKeywordOr(String keywordOr) {
      keywordsOr.add(keywordOr);
      return this;
    }

    public Builder addYm(int year, int month) {
      yms.add(Integer.valueOf(String.format("%04d%02d", year, month)));
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

    @Override public Map<String, String> build() {

      putQuery(KEYWORD, keywords);
      putQuery(KEYWORD_OR, keywordsOr);
      putQuery(YM, yms);
      putQuery(YMD, ymds);

      return super.build();
    }
  }

  public static Map<String, String> next(Map<String, String> query) {

    query.put(START, String.valueOf(Integer.valueOf(query.get(START)) + MAX_COUNT));
    query.put(COUNT, String.valueOf(MAX_COUNT));
    return query;
  }
}
