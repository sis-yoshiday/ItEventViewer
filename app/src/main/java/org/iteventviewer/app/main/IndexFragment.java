package org.iteventviewer.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import org.iteventviewer.model.IndexViewModel;
import org.iteventviewer.service.atnd.AtndService;
import org.iteventviewer.service.atnd.model.AtndIndexViewModel;
import org.iteventviewer.service.compass.ConnpassService;
import org.iteventviewer.service.compass.model.ConnpassIndexViewModel;
import org.iteventviewer.service.doorkeeper.DoorkeeperService;
import org.iteventviewer.service.doorkeeper.model.DoorkeeperIndexViewModel;
import org.iteventviewer.service.zusaar.ZusaarService;
import org.iteventviewer.service.zusaar.model.ZusaarIndexViewModel;
import org.iteventviewer.util.PreferenceUtil;
import org.iteventviewer.util.Region;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func4;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by yuki_yoshida on 15/01/30.
 *
 * TODO 検索処理をサービス層に移したい
 */
public class IndexFragment extends BaseFragment {

  @Inject AtndService atndService;
  @Inject ConnpassService connpassService;
  @Inject ZusaarService zusaarService;
  @Inject DoorkeeperService doorkeeperService;

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
          case R.string.zusaar:
            // TODO
            break;
          case R.string.doorkeeper:
            // TODO
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

    Observable<List<AtndIndexViewModel>> atndResultStream = atndService.search(region, categories);
    Observable<List<ConnpassIndexViewModel>> connpassResultStream = connpassService.
        search(region, categories);
    Observable<List<ZusaarIndexViewModel>> zusaarResultStream =
        zusaarService.search(region, categories);
    Observable<List<DoorkeeperIndexViewModel>> doorkeeperResultStream =
        doorkeeperService.search(region, categories);

    progressBar.setVisibility(View.VISIBLE);

    // FIXME どれかがエラーになっても大丈夫なようにする
    // 各サービスからの取得データのハンドリング
    subscription.add(bind(Observable.zip(atndResultStream, connpassResultStream, zusaarResultStream,
        doorkeeperResultStream,
        new Func4<List<AtndIndexViewModel>, List<ConnpassIndexViewModel>, List<ZusaarIndexViewModel>, List<DoorkeeperIndexViewModel>, List<IndexViewModel>>() {
          @Override public List<IndexViewModel> call(List<AtndIndexViewModel> atndModels,
              List<ConnpassIndexViewModel> connpassModels, List<ZusaarIndexViewModel> zusaarModels,
              List<DoorkeeperIndexViewModel> doorkeeperModels) {
            // 取得データをマージ
            List<IndexViewModel> result = Lists.newArrayList();
            result.addAll(atndModels);
            result.addAll(connpassModels);
            result.addAll(zusaarModels);
            result.addAll(doorkeeperModels);
            return result;
          }
        })).subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<IndexViewModel>>() {
          @Override public void call(List<IndexViewModel> indexViewModels) {
            // 日付でソート
            adapter.setItems(indexViewModels);
            adapter.sort(IndexViewModel.START_AT_ASC_COMPARATOR);
            adapter.notifyDataSetChanged();
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

  class IndexAdapter extends RecyclerView.Adapter<BindableViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<? extends IndexViewModel> items;

    @Setter private OnItemClickListener onItemClickListener;

    public IndexAdapter(Context context) {
      this.context = context;
      this.inflater = LayoutInflater.from(context);
      this.items = new ArrayList<>();
    }

    @Override public BindableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = inflater.inflate(R.layout.item_index, parent, false);
      return new ViewHolder(view, onItemClickListener);
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    @Override public int getItemCount() {
      return items.size();
    }

    public IndexViewModel getItem(int position) {
      return items.get(position);
    }

    public void setItems(List<? extends IndexViewModel> items) {
      this.items = items;
    }

    public void sort(Comparator<IndexViewModel> comparator) {
      Collections.sort(items, comparator);
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
