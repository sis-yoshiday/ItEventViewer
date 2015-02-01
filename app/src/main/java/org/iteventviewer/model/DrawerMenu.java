package org.iteventviewer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by yuki_yoshida on 15/01/20.
 */
@Getter @Setter
public class DrawerMenu {

  private int iconResId;

  private int titleResId;

  public DrawerMenu(int iconResId, int titleResId) {
    this.iconResId = iconResId;
    this.titleResId = titleResId;
  }
}
