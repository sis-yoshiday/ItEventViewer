package org.iteventviewer.service.atnd.json;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.iteventviewer.service.atnd.EventDetailViewModel;
import org.iteventviewer.service.atnd.EventSearchQuery;
import org.iteventviewer.service.atnd.MemberSearchQuery;
import org.parceler.Parcel;
import rx.Observable;

/**
 * Created by yuki_yoshida on 15/01/31.
 */
@Getter @Setter
public class SearchResult<T> {

  /**
   * 含まれる検索結果の件数
   */
  @SerializedName("results_returned")
  private int resultsReturned;

  /**
   * 検索の開始位置
   */
  @SerializedName("results_start")
  private int resultsStart;

  private List<EventContainer<T>> events;

  @Getter
  public static class EventContainer<T> {

    private T event;
  }

  public boolean hasNext(Map<String, String> query) {
    int queryCount = Integer.valueOf(query.get(MemberSearchQuery.COUNT));
    return queryCount >= resultsReturned;
  }
}
