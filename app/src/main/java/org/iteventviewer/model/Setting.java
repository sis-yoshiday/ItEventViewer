package org.iteventviewer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by yuki_yoshida on 15/01/29.
 */
@Getter @Setter
public class Setting {

  public static final int TYPE_NORMAL = 0;

  public static final int TYPE_CHECKBOX = 1;

  private int type;

  private int titleResId;

  private boolean checked = false;

  public Setting(int type, int titleResId) {
    this.type = type;
    this.titleResId = titleResId;
  }

  public void toggleCheck() {
    checked = !checked;
  }
}
