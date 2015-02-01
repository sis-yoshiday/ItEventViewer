package org.iteventviewer.app.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.Map;
import javax.inject.Inject;
import lombok.Setter;
import org.iteventviewer.app.BaseFragment;
import org.iteventviewer.app.EventDetailActivity;
import org.iteventviewer.app.MyApplication;
import org.iteventviewer.app.R;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.ClickableViewHolder;
import org.iteventviewer.common.ExRecyclerView;
import org.iteventviewer.common.OnItemClickListener;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.service.atnd.AtndService;
import org.iteventviewer.service.atnd.EventSearchQuery;
import org.iteventviewer.service.atnd.json.Event;
import org.iteventviewer.service.atnd.json.SearchResult;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

/**
 * Created by yuki_yoshida on 15/01/30.
 */
public class IndexFragment extends BaseFragment {

  private static final String TAG = IndexFragment.class.getSimpleName();

  @Inject AtndService atndService;

  private Subscription subscription = Subscriptions.empty();

  @InjectView(R.id.recyclerView) ExRecyclerView recyclerView;
  @InjectView(R.id.emptyLayout) ViewGroup emptyLayout;

  IndexAdapter adapter;

  public static IndexFragment newInstance() {
    IndexFragment fragment = new IndexFragment();
    return fragment;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    ((MyApplication) activity.getApplication()).inject(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_index, container, false);
    ButterKnife.inject(this, view);

    LinearLayoutManager layoutManager =
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new IndexAdapter(getActivity());
    recyclerView.setAdapter(adapter);

    adapter.setOnItemClickListener(new OnItemClickListener() {
      @Override public void onItemClick(View itemView, int position) {
        EventDetailActivity.launch(getActivity(), adapter.getItem(position).getEvent());
      }
    });

    Map<String, String> query = new EventSearchQuery.Builder().addKeyword("Android").build();

    subscription = AndroidObservable.bindFragment(this, atndService.searchEvent(query))
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<SearchResult<Event>>() {
          @Override public void call(SearchResult<Event> eventSearchResult) {
            // success
            adapter.setItems(eventSearchResult.getEvents());
            recyclerView.setEmptyView(emptyLayout);
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            // failure
            Log.e(TAG, throwable.getMessage(), throwable);
          }
        }, new Action0() {
          @Override public void call() {
            // always
          }
        });

    return view;
  }

  @Override public void onDestroyView() {
    subscription.unsubscribe();
    ButterKnife.reset(this);
    super.onDestroyView();
  }

  class IndexAdapter
      extends SimpleRecyclerAdapter<SearchResult.EventContainer<Event>, BindableViewHolder> {

    @Setter private OnItemClickListener onItemClickListener;

    public IndexAdapter(Context context) {
      super(context);
    }

    @Override protected View newView(ViewGroup viewGroup, int viewType) {
      return inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
    }

    @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
      return new ViewHolder(view, onItemClickListener);
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    class ViewHolder extends ClickableViewHolder {

      @InjectView(android.R.id.text1) TextView title;

      public ViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView, listener);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        Event item = getItem(position).getEvent();

        title.setText(item.getTitle());
      }
    }
  }
}
