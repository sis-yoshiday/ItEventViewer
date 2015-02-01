package org.iteventviewer.loader;

import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * Created by yuki_yoshida on 15/01/25.
 */
public abstract class BaseLoaderCallbacks<T>
    implements LoaderManager.LoaderCallbacks<AsyncTaskResult<T>> {

  private List<LoaderCallbackDelegate<T>> callbacks;

  public BaseLoaderCallbacks() {
    callbacks = Lists.newArrayList();
  }

  public BaseLoaderCallbacks(@NonNull LoaderCallbackDelegate<T> callback) {
    callbacks = Lists.newArrayList(callback);
  }

  @Override public void onLoadFinished(Loader<AsyncTaskResult<T>> loader, AsyncTaskResult<T> data) {

    if (data.isSuccess()) {
      onSuccess(loader, data.getResult());
    } else {
      onFailure(loader, data.getException());
    }
  }

  private void onSuccess(Loader<AsyncTaskResult<T>> loader, T data) {
    for (LoaderCallbackDelegate<T> callback : callbacks) {
      callback.onSuccess(loader, data);
    }
  }

  private void onFailure(Loader<AsyncTaskResult<T>> loader, Exception exception) {
    for (LoaderCallbackDelegate<T> callback : callbacks) {
      callback.onFailure(loader, exception);
    }
  }

  @Override public void onLoaderReset(Loader<AsyncTaskResult<T>> loader) {
    for (LoaderCallbackDelegate<T> callback : callbacks) {
      callback.onLoaderReset(loader);
    }
  }

  public void addCallback(@NonNull LoaderCallbackDelegate<T> callback) {
    if (!callbacks.contains(callback)) {
      callbacks.add(callback);
    }
  }

  public void removeCallback(LoaderCallbackDelegate<T> callback) {
    callbacks.remove(callback);
  }
}
