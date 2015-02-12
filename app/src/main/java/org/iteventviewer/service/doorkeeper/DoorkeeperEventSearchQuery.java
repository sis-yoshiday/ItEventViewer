package org.iteventviewer.service.doorkeeper;

import java.util.LinkedHashMap;
import java.util.Map;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDateTime;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
public class DoorkeeperEventSearchQuery {

  public static final String LOCALE_EN = "en";

  public static final String LOCALE_JA = "ja";

  public static final String SORT_PUBLISH_AT = "publish_at";

  public static final String SORT_STARTS_AT = "starts_at";

  public static final String SORT_UPDATED_AT = "updated_at";

  /**
   * The page offset of the results
   */
  public static final String PAGE = "page";

  /**
   * The localized text for an event
   */
  public static final String LOCALE = "locale";

  /**
   * The order of the results
   */
  public static final String SORT = "sort";

  /**
   * Only events taking place during or after this date will be included. (ISO 8601) Default: Today
   */
  public static final String SINCE = "since";

  /**
   * Only events taking place during or before this date will be included. (ISO 8601)
   */
  public static final String UNTIL = "until";

  public static class Builder {

    private Map<String, String> queryMap;

    private int page;

    private String locale;

    private String sort;

    private LocalDateTime since;

    private LocalDateTime until;

    public Builder() {
      queryMap = new LinkedHashMap<>();
      page = 1;
      locale = LOCALE_EN;
      sort = SORT_PUBLISH_AT;
      since = new LocalDateTime();
      until = since.withFieldAdded(DurationFieldType.days(), 30);
    }

    public Builder page(int page) {
      this.page = page;
      return this;
    }

    public Builder locale(String locale) {
      this.locale = locale;
      return this;
    }

    public Builder sort(String sort) {
      this.sort = sort;
      return this;
    }

    public Builder since(LocalDateTime dateTime) {
      since = dateTime;
      return this;
    }

    public Builder until(LocalDateTime dateTime) {
      until = dateTime;
      return this;
    }

    public Map<String, String> build() {

      queryMap.put(PAGE, String.valueOf(page));
      queryMap.put(LOCALE, locale);
      queryMap.put(SORT, sort);
      queryMap.put(SINCE, since.toString());
      queryMap.put(UNTIL, until.toString());

      return queryMap;
    }
  }
}
