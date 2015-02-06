/*
 * Copyright 2015. Yuki YOSHIDA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.iteventviewer.app.drawer;

import android.support.annotation.Nullable;
import android.view.View;
import lombok.Getter;

/**
 * Created by yuki_yoshida on 15/02/07.
 */
@Getter
public class SelectMenuEvent {

  private View view;

  private int position;

  private boolean alreadySelected;

  public SelectMenuEvent(@Nullable View view, int position, boolean alreadySelected) {
    this.view = view;
    this.position = position;
    this.alreadySelected = alreadySelected;
  }
}
