package org.iteventviewer.service.compass.json;

import java.io.Serializable;
import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/02/09.
 */
@Getter
public class Series implements Serializable {

  private static final long serialVersionUID = -6897100951140460280L;

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
