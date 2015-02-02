package org.iteventviewer.service.atnd;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Setter;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
public class MemberSearchQuery {

  public static final int MAX_COUNT = 100;

  /**
   * イベントID
   */
  public static final String EVENT_ID = "event_id";

  /**
   * 参加者のユーザID
   */
  public static final String USER_ID = "user_id";

  /**
   * 参加者のニックネーム
   */
  public static final String NICKNAME = "nickname";

  /**
   * 参加者のtwitter ID
   */
  public static final String TWITTER_ID = "twitter_id";

  /**
   * 主催者のユーザID
   */
  public static final String OWNER_ID = "owner_id";

  /**
   * 主催者のニックネーム
   */
  public static final String OWNER_NICKNAME = "owner_nickname";

  /**
   * 主催者のtwitter ID
   */
  public static final String OWNER_TWITTER_ID = "owner_twitter_id";

  /**
   * 検索の開始位置
   */
  public static final String START = "start";

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

    @Setter private Set<Long> eventIds;

    @Setter private Set<Integer> userIds;

    @Setter private Set<String> nicknames;

    @Setter private Set<String> twitterIds;

    @Setter private Set<Integer> ownerIds;

    @Setter private Set<String> ownerNicknames;

    @Setter private Set<String> ownerTwitterIds;

    private int start;

    private int count;

    private static final String FORMAT_JSON = "json";

    public Builder() {
      query = new HashMap<>();

      eventIds = new HashSet<>();
      userIds = new HashSet<>();
      nicknames = new HashSet<>();
      twitterIds = new HashSet<>();
      ownerIds = new HashSet<>();
      ownerNicknames = new HashSet<>();
      ownerTwitterIds = new HashSet<>();
      start = 1;
      count = 10;
    }

    public Builder addEventId(long eventId) {
      eventIds.add(eventId);
      return this;
    }

    public Builder addUserId(int userId) {
      userIds.add(userId);
      return this;
    }

    public Builder addNickname(String nickname) {
      nicknames.add(nickname);
      return this;
    }

    public Builder addTwitterId(String twitterId) {
      twitterIds.add(twitterId);
      return this;
    }

    public Builder addOwnerId(int userId) {
      ownerIds.add(userId);
      return this;
    }

    public Builder addOwnerNickname(String nickname) {
      ownerNicknames.add(nickname);
      return this;
    }

    public Builder addOwnerTwitterId(String twitterId) {
      ownerTwitterIds.add(twitterId);
      return this;
    }

    public Builder setStart(int start) {
      this.start = start;
      return this;
    }

    public Builder setCount(int count) {
      this.count = count;
      return this;
    }

    public Map<String, String> build() {

      putQuery(EVENT_ID, eventIds);
      putQuery(USER_ID, userIds);
      putQuery(NICKNAME, nicknames);
      putQuery(TWITTER_ID, twitterIds);
      putQuery(OWNER_ID, ownerIds);
      putQuery(OWNER_NICKNAME, ownerNicknames);
      putQuery(OWNER_TWITTER_ID, ownerTwitterIds);
      query.put(START, String.valueOf(start));
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
