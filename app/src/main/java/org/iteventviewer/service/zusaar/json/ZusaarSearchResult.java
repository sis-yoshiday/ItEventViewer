package org.iteventviewer.service.zusaar.json;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/02/10.
 */
@Getter public class ZusaarSearchResult {

  /**
   * 含まれる検索結果の件数
   */
  @SerializedName("results_returned") private int resultsReturned;

  /**
   * 検索の開始位置
   */
  @SerializedName("results_start") private int resultsStart;

  /**
   * 検索結果のイベントリスト
   */
  @SerializedName("event") private List<ZusaarEvent> events;
}
