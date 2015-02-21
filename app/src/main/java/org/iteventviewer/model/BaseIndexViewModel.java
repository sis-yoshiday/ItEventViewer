package org.iteventviewer.model;

import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/02/21.
 */
public abstract class BaseIndexViewModel implements IndexViewModel {

  @Getter private int tag;

  public BaseIndexViewModel(int tag) {
    this.tag = tag;
  }
}
