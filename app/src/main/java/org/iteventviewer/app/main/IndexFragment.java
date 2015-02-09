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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.Setter;
import org.iteventviewer.app.AtndEventDetailActivity;
import org.iteventviewer.app.BaseFragment;
import org.iteventviewer.app.ConnpassEventDetailActivity;
import org.iteventviewer.app.MyApplication;
import org.iteventviewer.app.R;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.ClickableViewHolder;
import org.iteventviewer.common.OnItemClickListener;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.model.IndexViewModel;
import org.iteventviewer.service.atnd.AtndApi;
import org.iteventviewer.service.atnd.AtndEventSearchQuery;
import org.iteventviewer.service.atnd.json.AtndEvent;
import org.iteventviewer.service.atnd.json.AtndSearchResult;
import org.iteventviewer.service.atnd.model.AtndIndexViewModel;
import org.iteventviewer.service.compass.ConnpassApi;
import org.iteventviewer.service.compass.ConnpassEventSearchQuery;
import org.iteventviewer.service.compass.json.ConnpassEvent;
import org.iteventviewer.service.compass.json.ConnpassSearchResult;
import org.iteventviewer.service.compass.model.ConnpassIndexViewModel;
import org.iteventviewer.util.PreferenceUtil;
import org.iteventviewer.util.Region;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by yuki_yoshida on 15/01/30.
 *
 * TODO 検索処理をサービス層に移したい
 */
public class IndexFragment extends BaseFragment {

  @Inject AtndApi atndApi;
  @Inject ConnpassApi connpassApi;

  private CompositeSubscription subscription = new CompositeSubscription();

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
        // NOTE : サービスが増えたら追加
        switch (item.getTag()) {
          case R.string.atnd:
            AtndEventDetailActivity.launch(getActivity(), ((AtndIndexViewModel) item).getEvent());
            break;
          case R.string.connpass:
            ConnpassEventDetailActivity.launch(getActivity(),
                ((ConnpassIndexViewModel) item).getEvent());
            break;
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

    // atnd
    Observable<List<AtndIndexViewModel>> atndResultStream = searchAtnd(region, categories);

    // connpass
    Observable<List<ConnpassIndexViewModel>> connpassResultStream =
        searchConnpass(region, categories);

    progressBar.setVisibility(View.VISIBLE);

    // 各サービスからの取得データのハンドリング
    subscription.add(AppObservable.bindFragment(this,
        Observable.zip(atndResultStream, connpassResultStream,
            new Func2<List<AtndIndexViewModel>, List<ConnpassIndexViewModel>, List<IndexViewModel>>() {
              @Override public List<IndexViewModel> call(List<AtndIndexViewModel> atndModels,
                  List<ConnpassIndexViewModel> connpassModels) {
                // 取得データをマージ
                List<IndexViewModel> result = Lists.newArrayList();
                result.addAll(atndModels);
                result.addAll(connpassModels);
                return result;
              }
            }))
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<IndexViewModel>>() {
          @Override public void call(List<IndexViewModel> indexViewModels) {
            // 日付でソート
            Collections.sort(indexViewModels, IndexViewModel.START_AT_ASC_COMPARATOR);
            adapter.setItems(indexViewModels);
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            Timber.e(throwable, throwable.getMessage());
            progressBar.setVisibility(View.GONE);
          }
        }, new Action0() {
          @Override public void call() {
            progressBar.setVisibility(View.GONE);
          }
        }));
  }

  private Observable<List<AtndIndexViewModel>> searchAtnd(@Nullable final Region region,
      Set<String> categories) {

    // 検索クエリを生成
    Map<String, String> query = new AtndEventSearchQuery.Builder().addKeywordsOr(categories)
        .addYmds(30)
        .count(AtndEventSearchQuery.MAX_COUNT)
        .build();

    return atndApi.searchEvent(query)
        .map(new Func1<AtndSearchResult<AtndEvent>, List<AtndIndexViewModel>>() {
          @Override
          public List<AtndIndexViewModel> call(AtndSearchResult<AtndEvent> eventSearchResult) {
            return Lists.newArrayList(Collections2.transform(eventSearchResult.getEvents(),
                new Function<AtndSearchResult.EventContainer<AtndEvent>, AtndIndexViewModel>() {
                  @Override public AtndIndexViewModel apply(
                      AtndSearchResult.EventContainer<AtndEvent> input) {
                    return new AtndIndexViewModel(R.string.atnd, input.getEvent());
                  }
                }));
          }
        })
        .flatMap(new Func1<List<AtndIndexViewModel>, Observable<List<AtndIndexViewModel>>>() {
          @Override public Observable<List<AtndIndexViewModel>> call(
              List<AtndIndexViewModel> indexViewModels) {
            return Observable.from(indexViewModels)
                .filter(AtndIndexViewModel.filter(region))
                .toList();
          }
        });
  }

  private Observable<List<ConnpassIndexViewModel>> searchConnpass(@Nullable final Region region,
      Set<String> categories) {

    // 検索クエリを生成
    Map<String, String> query = new ConnpassEventSearchQuery.Builder().addKeywordsOr(categories)
        .addYmds(30)
        .count(AtndEventSearchQuery.MAX_COUNT)
        .build();

    return connpassApi.searchEvent(query)
        .map(new Func1<ConnpassSearchResult, List<ConnpassIndexViewModel>>() {
          @Override
          public List<ConnpassIndexViewModel> call(ConnpassSearchResult eventSearchResult) {
            return Lists.newArrayList(Collections2.transform(eventSearchResult.getEvents(),
                new Function<ConnpassEvent, ConnpassIndexViewModel>() {
                  @Override public ConnpassIndexViewModel apply(ConnpassEvent input) {
                    return new ConnpassIndexViewModel(R.string.connpass, input);
                  }
                }));
          }
        })
        .flatMap(
            new Func1<List<ConnpassIndexViewModel>, Observable<List<ConnpassIndexViewModel>>>() {
              @Override public Observable<List<ConnpassIndexViewModel>> call(
                  List<ConnpassIndexViewModel> indexViewModels) {
                return Observable.from(indexViewModels)
                    .filter(ConnpassIndexViewModel.filter(region))
                    .toList();
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
      @InjectView(R.id.date) TextView date;

      public ViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView, listener);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        IndexViewModel item = getItem(position);

        title.setText(item.getTitle());
        tag.setText(item.getTag());
        date.setText(item.getStartedAt().toString("yyyy/MM/dd HH:mm"));
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
