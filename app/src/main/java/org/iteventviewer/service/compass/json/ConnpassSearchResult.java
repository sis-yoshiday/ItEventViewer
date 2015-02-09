package org.iteventviewer.service.compass.json;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/02/09.
 */
@Getter
public class ConnpassSearchResult {

  /**
   * 含まれる検索結果の件数
   */
  @SerializedName("results_returned") private int resultsReturned;

  /**
   * 検索結果の総件数
   */
  @SerializedName("results_available") private int resultsAvailable;

  /**
   * 検索の開始位置
   */
  @SerializedName("results_start") private int resultsStart;

  /**
   * 検索結果のイベントリスト
   */
  private List<ConnpassEvent> events;
}
