package org.iteventviewer.service.zusaar;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.iteventviewer.service.atnd.AtndMemberSearchQuery;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDateTime;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
public class ZusaarEventSearchQuery extends AtndMemberSearchQuery {

  /**
   * キーワード（AND）
   */
  public static final String KEYWORD = "key_word";

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

  public static class Builder extends AtndMemberSearchQuery.Builder {

    private Set<String> keywords;

    private Set<String> keywordsOr;

    private Set<Integer> yms;

    private Set<Integer> ymds;

    public Builder() {
      super();
      keywords = new HashSet<>();
      keywordsOr = new HashSet<>();
      yms = new LinkedHashSet<>();
      ymds = new LinkedHashSet<>();
    }

    public Builder addKeyword(String keyword) {
      keywords.add(keyword);
      return this;
    }

    public Builder addKeywordOr(String keywordOr) {
      keywordsOr.add(keywordOr);
      return this;
    }

    public Builder addKeywordsOr(Set<String> keywords) {
      keywordsOr.addAll(keywords);
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
