package org.iteventviewer.app.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.squareup.picasso.Picasso;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import jp.yokomark.widget.compound.CompoundFrameLayout;
import jp.yokomark.widget.compound.CompoundViewGroup;
import jp.yokomark.widget.compound.OnCheckedChangeListener;
import org.iteventviewer.app.BaseFragment;
import org.iteventviewer.app.MyApplication;
import org.iteventviewer.app.R;
import org.iteventviewer.app.util.PreferenceUtil;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.service.qiita.QiitaClient;
import org.iteventviewer.service.qiita.json.Tag;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

/**
 * 地域の登録
 *
 * Created by yuki_yoshida on 15/02/02.
 */
public class CategorySettingsFragment extends BaseFragment {

  private static final String TAG = CategorySettingsFragment.class.getSimpleName();

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;
  @InjectView(R.id.errorLayout) ViewGroup errorLayout;
  @InjectView(R.id.progressBar) ProgressBarCircularIndeterminate progressBar;

  @Inject QiitaClient qiitaClient;

  CategoryAdapter adapter;

  private Subscription subscription = Subscriptions.empty();

  public static CategorySettingsFragment newInstance() {
    return new CategorySettingsFragment();
  }

  @OnClick(R.id.retry) void retry(View view) {
    getTags();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_category_settings, container, false);
    ButterKnife.inject(this, view);
    MyApplication.get(getActivity()).inject(this);

    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
        getResources().getInteger(R.integer.category_grid_column)));

    adapter = new CategoryAdapter(getActivity());
    recyclerView.setAdapter(adapter);

    getTags();

    return view;
  }

  @Override public void onDestroyView() {
    subscription.unsubscribe();
    ButterKnife.reset(this);
    super.onDestroyView();
  }

  private void getTags() {

    progressBar.setVisibility(View.VISIBLE);

    // NOTE : 汎用的にタグを取得する方法がないため、"tags_for_api"ユーザを作成し手動で管理する
    subscription = AppObservable.bindFragment(this,
        qiitaClient.tags("Bearer " + getString(R.string.qiita_token), "tags_for_api", 1, 100))
        .subscribe(new Action1<List<Tag>>() {
          @Override public void call(List<Tag> tags) {

            recyclerView.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);

            adapter.setItems(
                Observable.from(tags).toSortedList(Tag.COMPARATOR_HOT).toBlocking().first());
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {

            recyclerView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);

            Log.e(TAG, throwable.getMessage(), throwable);
          }
        }, new Action0() {
          @Override public void call() {
            progressBar.setVisibility(View.GONE);
          }
        });
  }

  public static class CategoryAdapter extends SimpleRecyclerAdapter<Tag, BindableViewHolder> {

    @Inject Picasso picasso;

    public CategoryAdapter(Context context) {
      super(context);
      MyApplication.get(context).inject(this);
    }

    @Override protected View newView(ViewGroup viewGroup, int viewType) {
      return inflater.inflate(R.layout.item_tag, viewGroup, false);
    }

    @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
      return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    class ViewHolder extends BindableViewHolder implements OnCheckedChangeListener {

      @InjectView(R.id.container) CompoundFrameLayout container;
      @InjectView(R.id.icon) ImageView icon;
      @InjectView(R.id.title) TextView title;

      private Set<String> categories;

      public ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        categories = PreferenceUtil.getCategories(context);
      }

      @Override public void bind(int position) {

        final Tag item = getItem(position);

        String iconUrl = item.getIconUrl();
        if (!TextUtils.isEmpty(iconUrl)) {
          picasso.load(iconUrl).into(icon);
        }
        final String id = item.getId();
        title.setText(id);

        container.setChecked(categories.contains(id));
        container.setOnCheckedChangeListener(this);
      }

      @Override public void onCheckedChanged(CompoundViewGroup view, boolean checked) {

        String id = getItem(getPosition()).getId();

        if (checked) {
          categories.add(id);
        } else {
          categories.remove(id);
        }
        PreferenceUtil.saveCategories(context, categories);
      }
    }
  }
}
