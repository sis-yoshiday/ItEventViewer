package org.iteventviewer.loader;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by yuki_yoshida on 15/01/25.
 */
@Getter @Setter
public class AsyncTaskResult<T> {

  private T result;

  private Exception exception;

  private AsyncTaskResult(T result, Exception e) {
    this.result = result;
    this.exception = e;
  }

  public static <T> AsyncTaskResult<T> success(T result) {
    return new AsyncTaskResult<>(result, null);
  }

  public static <T> AsyncTaskResult<T> failure(Exception e) {
    return new AsyncTaskResult<>(null, e);
  }

  public boolean isSuccess() {
    return result != null;
  }
}
