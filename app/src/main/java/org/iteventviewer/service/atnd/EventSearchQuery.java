package org.iteventviewer.service.atnd;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Setter;

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

    @Override public Map<String, String> build() {

      putQuery(KEYWORD, keywords);
      putQuery(KEYWORD_OR, keywordsOr);
      putQuery(YM, yms);
      putQuery(YMD, ymds);

      return super.build();
    }
  }
}
