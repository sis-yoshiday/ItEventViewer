package org.iteventviewer.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by yuki_yoshida on 15/01/25.
 */
public abstract class BaseAsyncTaskLoader<T> extends AsyncTaskLoader<AsyncTaskResult<T>> {

  private AsyncTaskResult<T> data;

  public BaseAsyncTaskLoader(Context context) {
    super(context);
  }

  @Override public final AsyncTaskResult<T> loadInBackground() {

    try {
      return AsyncTaskResult.success(loadInBackgroundSafe());
    } catch (Exception e) {
      return AsyncTaskResult.failure(e);
    }
  }

  abstract protected T loadInBackgroundSafe() throws Exception;

  /**
   * workaround of
   *
   * https://code.google.com/p/android/issues/detail?id=14944
   */
  @Override public void deliverResult(AsyncTaskResult<T> data) {
    if (isReset()) {
      // An async query came in while the loader is stopped
      return;
    }

    this.data = data;

    super.deliverResult(data);
  }

  /**
   * workaround of
   *
   * https://code.google.com/p/android/issues/detail?id=14944
   */
  @Override protected void onStartLoading() {
    if (data != null) {
      deliverResult(data);
    }

    if (takeContentChanged() || data == null) {
      forceLoad();
    }
  }

  /**
   * workaround of
   *
   * https://code.google.com/p/android/issues/detail?id=14944
   */
  @Override protected void onStopLoading() {
    // Attempt to cancel the current load task if possible.
    cancelLoad();
  }

  /**
   * workaround of
   *
   * https://code.google.com/p/android/issues/detail?id=14944
   */
  @Override protected void onReset() {
    super.onReset();

    // Ensure the loader is stopped
    onStopLoading();

    data = null;
  }
}
