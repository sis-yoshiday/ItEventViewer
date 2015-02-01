package org.iteventviewer.loader;

import android.support.v4.content.Loader;

/**
 * Created by yuki_yoshida on 15/01/25.
 */
public class LoaderCallbackDelegate<T> {

  public void onSuccess(Loader<AsyncTaskResult<T>> loader, T data) {
  }

  public void onFailure(Loader<AsyncTaskResult<T>> loader, Exception e) {
  }

  public void onLoaderReset(Loader<AsyncTaskResult<T>> loader) {
  }
}
