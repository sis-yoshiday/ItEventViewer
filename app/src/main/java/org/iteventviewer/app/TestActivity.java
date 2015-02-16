package org.iteventviewer.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import org.iteventviewer.service.ServiceModule;
import org.iteventviewer.service.atnd.AtndService;
import org.iteventviewer.service.atnd.model.AtndIndexViewModel;
import rx.Observable;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yuki_yoshida on 15/02/14.
 */
public class TestActivity extends ToolBarActivity {

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  MyAdapter adapter;

  AtndService atndService;

  private CompositeSubscription subscription = new CompositeSubscription();

  public static void launch(Context context) {
    context.startActivity(new Intent(context, TestActivity.class));
  }

  @Override protected int contentView() {
    return R.layout.activity_test;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.inject(this);

    recyclerView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    adapter = new MyAdapter();
    recyclerView.setAdapter(adapter);

    ServiceModule module = new ServiceModule(this);
    atndService = new AtndService(module.provideAtndApi(module.provideOkHttpClientForApi()), 10);

    Observable<List<AtndIndexViewModel>> initialStream =
        atndService.search(null, Sets.newHashSet("Android"), 1);

    subscription.add(AppObservable.bindActivity(this, initialStream)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(adapter.subscriber));

    atndService.nextSearchStream().subscribe(new Action1<Observable<List<AtndIndexViewModel>>>() {
      @Override public void call(final Observable<List<AtndIndexViewModel>> listObservable) {

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
          @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

            if (llm.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {

              subscription.add(AppObservable.bindActivity(self, listObservable)
                  .subscribeOn(AndroidSchedulers.mainThread())
                  .subscribe(adapter.subscriber));

              recyclerView.setOnScrollListener(null);
            }
          }
        });
      }
    });
  }

  @Override protected void onDestroy() {
    subscription.unsubscribe();
    super.onDestroy();
  }

  class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<AtndIndexViewModel> items;

    Subscriber<List<AtndIndexViewModel>> subscriber = new Subscriber<List<AtndIndexViewModel>>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {

      }

      @Override public void onNext(List<AtndIndexViewModel> models) {
        addItems(models);
        notifyDataSetChanged();
      }
    };

    public MyAdapter() {
      items = new ArrayList<>();
    }

    public AtndIndexViewModel getItem(int position) {
      return items.get(position);
    }

    public void addItems(List<AtndIndexViewModel> items) {
      this.items.addAll(items);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view =
          LayoutInflater.from(self).inflate(android.R.layout.simple_list_item_1, parent, false);
      return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      holder.title.setText(getItem(position).getEvent().getTitle());
    }

    @Override public int getItemCount() {
      return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

      @InjectView(android.R.id.text1) TextView title;

      public ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }
    }
  }
}
