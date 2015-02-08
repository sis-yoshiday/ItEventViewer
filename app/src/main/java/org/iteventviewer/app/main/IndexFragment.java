package org.iteventviewer.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.Setter;
import org.iteventviewer.app.BaseFragment;
import org.iteventviewer.app.EventDetailActivity;
import org.iteventviewer.app.MyApplication;
import org.iteventviewer.app.R;
import org.iteventviewer.app.util.PreferenceUtil;
import org.iteventviewer.app.util.Region;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.ClickableViewHolder;
import org.iteventviewer.common.OnItemClickListener;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.model.IndexViewModel;
import org.iteventviewer.service.atnd.AtndService;
import org.iteventviewer.service.atnd.EventSearchQuery;
import org.iteventviewer.service.atnd.json.Event;
import org.iteventviewer.service.atnd.json.SearchResult;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Created by yuki_yoshida on 15/01/30.
 *
 * TODO 検索処理をサービス層に移したい
 */
public class IndexFragment extends BaseFragment {

  @Inject AtndService atndService;

  private Subscription subscription = Subscriptions.empty();

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;
  @InjectView(R.id.warningLayout) ViewGroup warningLayout;
  @InjectView(R.id.emptyLayout) ViewGroup emptyLayout;
  @InjectView(R.id.progressBar) ProgressBarCircularIndeterminate progressBar;

  IndexAdapter adapter;

  public static IndexFragment newInstance() {
    IndexFragment fragment = new IndexFragment();
    return fragment;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    ((MyApplication) activity.getApplication()).inject(this);
  }

  @Override public View onCreateView(final LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_index, container, false);
    ButterKnife.inject(this, view);

    LinearLayoutManager layoutManager =
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(true);

    adapter = new IndexAdapter(getActivity());
    recyclerView.setAdapter(adapter);

    recyclerView.addItemDecoration(new Divider(getActivity()));

    adapter.setOnItemClickListener(new OnItemClickListener() {
      @Override public void onItemClick(View itemView, int position) {
        IndexViewModel item = adapter.getItem(position);
        switch (item.getTag()) {
          case R.string.atnd:
            EventDetailActivity.launch(getActivity(), item.getEvent());
            break;
          // TODO サービスごとに追加
        }
      }
    });

    Set<String> categories = PreferenceUtil.getCategories(getActivity());

    if (categories.isEmpty()) {
      warningLayout.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    } else {
      warningLayout.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);

      search(Region.byId(PreferenceUtil.getRegion(getActivity())), categories);
    }

    return view;
  }

  @Override public void onDestroyView() {
    subscription.unsubscribe();
    ButterKnife.reset(this);
    super.onDestroyView();
  }

  private void search(Region region, Set<String> categories) {

    progressBar.setVisibility(View.VISIBLE);

    // サービスごとのデータ取得

    // atnd
    Observable<List<IndexViewModel>> atndSearchResultStream = searchAtnd(region, categories);

    // 各サービスからの取得データをマージ（マージ後に日付でソート）

    subscription = AppObservable.bindFragment(this, atndSearchResultStream)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<IndexViewModel>>() {
          @Override public void call(List<IndexViewModel> indexViewModels) {
            // success
            if (indexViewModels.isEmpty()) {
              emptyLayout.setVisibility(View.VISIBLE);
              recyclerView.setVisibility(View.GONE);
            } else {
              emptyLayout.setVisibility(View.GONE);
              recyclerView.setVisibility(View.VISIBLE);

              adapter.setItems(indexViewModels);
            }
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            // failure
            Timber.e(throwable, throwable.getMessage());
          }
        }, new Action0() {
          @Override public void call() {
            progressBar.setVisibility(View.GONE);
          }
        });
  }

  private Observable<List<IndexViewModel>> searchAtnd(@Nullable final Region region,
      Set<String> categories) {

    // 検索クエリを生成
    EventSearchQuery.Builder queryBuilder = new EventSearchQuery.Builder();
    for (String category : categories) {
      queryBuilder.addKeywordOr(category);
    }
    // 30日後まで
    queryBuilder.addYmds(30);
    Map<String, String> query = queryBuilder.build();

    return atndService.searchEvent(query)
        .map(new Func1<SearchResult<Event>, List<IndexViewModel>>() {
          @Override public List<IndexViewModel> call(SearchResult<Event> eventSearchResult) {
            return Lists.newArrayList(Collections2.transform(eventSearchResult.getEvents(),
                new Function<SearchResult.EventContainer<Event>, IndexViewModel>() {
                  @Override public IndexViewModel apply(SearchResult.EventContainer<Event> input) {
                    return IndexViewModel.atnd(input.getEvent());
                  }
                }));
          }
        })
        .flatMap(new Func1<List<IndexViewModel>, Observable<List<IndexViewModel>>>() {
          @Override
          public Observable<List<IndexViewModel>> call(List<IndexViewModel> indexViewModels) {
            return Observable.from(indexViewModels).filter(new Func1<IndexViewModel, Boolean>() {
              @Override public Boolean call(IndexViewModel indexViewModel) {
                // NOTE : APIの制約により地域は取得後にフィルタする
                if (region != null) {
                  for (String pref : region.getPrefs()) {
                    if (indexViewModel.getEvent().getAddress().contains(pref)) {
                      return true;
                    }
                  }
                  return false;
                } else {
                  return true;
                }
              }
            }).toList();
          }
        });
  }

  class IndexAdapter extends SimpleRecyclerAdapter<IndexViewModel, BindableViewHolder> {

    @Setter private OnItemClickListener onItemClickListener;

    public IndexAdapter(Context context) {
      super(context);
    }

    @Override protected View newView(ViewGroup viewGroup, int viewType) {
      return inflater.inflate(R.layout.item_index, viewGroup, false);
    }

    @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
      return new ViewHolder(view, onItemClickListener);
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    class ViewHolder extends ClickableViewHolder {

      @InjectView(R.id.title) TextView title;
      @InjectView(R.id.tag) TextView tag;

      public ViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView, listener);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        IndexViewModel item = getItem(position);
        Event event = item.getEvent();

        title.setText(event.getTitle());
        tag.setText(item.getTag());
      }
    }
  }

  static class Divider extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[] {
        android.R.attr.listDivider
    };

    private Drawable divider;

    public Divider(Context context) {
      final TypedArray a = context.obtainStyledAttributes(ATTRS);
      divider = a.getDrawable(0);
      a.recycle();
    }

    @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
        RecyclerView.State state) {
      super.getItemOffsets(outRect, view, parent, state);
    }

    @Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

      final int left = parent.getPaddingLeft();
      final int right = parent.getWidth() - parent.getPaddingRight();

      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
        final View child = parent.getChildAt(i);
        final RecyclerView.LayoutParams params =
            (RecyclerView.LayoutParams) child.getLayoutParams();
        final int top = child.getBottom() + params.bottomMargin;
        final int bottom = top + divider.getIntrinsicHeight();
        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
      }
    }
  }
}
