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
import org.iteventviewer.common.SimpleRecyclerAdapter;
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

    adapter = new MyAdapter(this);
    recyclerView.setAdapter(adapter);

    for (int i = 1; i <= 100; i++) {
      adapter.addItem(i);
    }
  }

  @Override protected void onDestroy() {
    subscription.unsubscribe();
    super.onDestroy();
  }

  class MyAdapter extends SimpleRecyclerAdapter<Integer, MyAdapter.ViewHolder> {

    public MyAdapter(Context context) {
      super(context);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view =
          LayoutInflater.from(self).inflate(android.R.layout.simple_list_item_1, parent, false);
      return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      String title;
      switch (getItem(position) % 15) {
        case 0:
          title = "Fizz Buzz";
          break;
        case 3:
        case 6:
        case 9:
        case 12:
          title = "Fizz";
          break;
        case 5:
        case 10:
          title = "Buzz";
          break;
        default:
          title = String.valueOf(getItem(position));
          break;
      }
      holder.title.setText(title);
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
