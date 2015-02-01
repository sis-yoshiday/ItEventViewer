package org.iteventviewer.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;

/**
 * Created by yuki_yoshida on 15/01/29.
 */
public class ExRecyclerView extends RecyclerView {

  private MultiScrollListener multiScrollListener;

  @Setter
  private View emptyView;

  public ExRecyclerView(Context context) {
    super(context);
    init(context);
  }

  public ExRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public ExRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  private void init(Context context) {
    multiScrollListener = new MultiScrollListener();
    setOnScrollListener(multiScrollListener);
    addOnLayoutChangeListener(new OnLayoutChangeListener() {
      @Override
      public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
          int oldTop, int oldRight, int oldBottom) {

        if (emptyView == null) {
          return;
        }
        boolean empty = getChildCount() == 0;

        emptyView.setVisibility(empty ? VISIBLE : GONE);
        setVisibility(empty ? GONE : VISIBLE);
      }
    });
  }

  public void addScrollListener(@NonNull OnScrollListener listener) {
    multiScrollListener.add(listener);
  }

  public void removeScrollListener(OnScrollListener listener) {
    multiScrollListener.remove(listener);
  }

  class MultiScrollListener extends OnScrollListener {

    private List<OnScrollListener> listeners;

    public MultiScrollListener() {
      super();
      listeners = new ArrayList<>();
    }

    public void add(@NonNull OnScrollListener listener) {
      if (!listeners.contains(listener)) {
        listeners.add(listener);
      }
    }

    public void remove(OnScrollListener listener) {
      listeners.remove(listener);
    }

    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      for (OnScrollListener listener : listeners) {
        if (listener != null) {
          listener.onScrollStateChanged(recyclerView, newState);
        }
      }
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      for (OnScrollListener listener : listeners) {
        if (listener != null) {
          listener.onScrolled(recyclerView, dx, dy);
        }
      }
    }
  }
}
