package org.iteventviewer.app;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * Created by yuki_yoshida on 15/01/29.
 */
public class BaseFragment extends Fragment {

  public Toolbar getToolbar() {
    return ((ToolBarActivity) getActivity()).getToolbar();
  }
}
