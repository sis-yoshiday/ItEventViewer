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

package org.iteventviewer.app;

import android.support.v7.widget.Toolbar;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.app.support.RxFragment;

/**
 * Created by yuki_yoshida on 15/01/29.
 */
public class BaseFragment extends RxFragment {

  public Toolbar getToolbar() {
    return ((ToolBarActivity) getActivity()).getToolbar();
  }

  protected <T> Observable<T> bind(Observable<T> source) {
    return AppObservable.bindFragment(this, source);
  }
}
