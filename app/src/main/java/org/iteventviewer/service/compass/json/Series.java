package org.iteventviewer.service.compass.json;

import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/02/09.
 */
@Getter
public class Series {

  /**
   * グループID
   */
  private int id;

  /**
   * グループタイトル
   */
  private String title;

  /**
   * グループのcompass url
   */
  private String url;
}
