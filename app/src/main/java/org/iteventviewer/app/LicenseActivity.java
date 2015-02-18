package org.iteventviewer.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.iteventviewer.common.DividerItemDecoration;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class LicenseActivity extends ToolBarActivity {

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  LicenseAdapter adapter;

  private Subscription subscription = Subscriptions.empty();

  public static void launch(Context context) {
    context.startActivity(new Intent(context, LicenseActivity.class));
  }

  @Override protected int contentView() {
    return R.layout.activity_license;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    recyclerView.setPadding(recyclerView.getPaddingLeft(),
        recyclerView.getPaddingTop() + getResources().getDimensionPixelSize(R.dimen.space_sm),
        recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());

    recyclerView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    adapter = new LicenseAdapter(this);
    recyclerView.setAdapter(adapter);

    recyclerView.addItemDecoration(
        new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_sm_tranpaent)));

    Observable<String> licenseTextStream = licenseTextStream(getAssets());

    subscription = AppObservable.bindActivity(this, licenseTextStream)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<String>() {
          @Override public void call(String s) {
            adapter.addItem(s);
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            Timber.e(throwable, throwable.getMessage());
          }
        }, new Action0() {
          @Override public void call() {
          }
        });
  }

  @Override protected void onDestroy() {
    subscription.unsubscribe();
    super.onDestroy();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private static Observable<String> licenseTextStream(final AssetManager assetManager) {
    return Observable.create(new Observable.OnSubscribe<String>() {

      @Override public void call(Subscriber<? super String> subscriber) {

        BufferedReader reader = null;
        try {
          String[] fileNames = assetManager.list("licenses");

          for (String fileName : fileNames) {
            InputStream is = assetManager.open("licenses/" + fileName);

            reader = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));

            StringBuilder builder = new StringBuilder();
            char[] charBuffer = new char[8 * 1024];
            int r;
            while ((r = reader.read(charBuffer)) != -1) {
              builder.append(charBuffer, 0, r);
            }
            subscriber.onNext(builder.toString());
          }
        } catch (IOException e) {
          subscriber.onError(e);
        } finally {
          Closeables.closeQuietly(reader);
          subscriber.onCompleted();
        }
      }
    }).observeOn(Schedulers.io());
  }

  class LicenseAdapter extends SimpleRecyclerAdapter<String, LicenseAdapter.ViewHolder> {

    private List<String> items;

    public LicenseAdapter(Context context) {
      super(context);
      items = new ArrayList<>();
    }

    public String getItem(int position) {
      return items.get(position);
    }

    public void addItem(String item) {
      items.add(item);
      notifyItemInserted(items.size() - 1);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new ViewHolder(inflater.inflate(R.layout.item_license, parent, false));
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      holder.contents.setText(getItem(position));
    }

    @Override public int getItemCount() {
      return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

      @InjectView(R.id.contents) TextView contents;

      public ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }
    }
  }
}
