package org.iteventviewer.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.gc.materialdesign.views.ButtonFlat;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.service.atnd.model.AtndEventDetailViewModel;
import org.iteventviewer.service.compass.json.ConnpassEvent;
import org.iteventviewer.service.compass.model.ConnpassEventDetailViewModel;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * TODO 全体的に途中
 */
public class ConnpassEventDetailActivity extends ToolBarActivity {

  public static final String EXTRA_EVENT = "event";

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  EventDetailAdapter adapter;

  private Subscription subscription = Subscriptions.empty();

  private ConnpassEvent event;

  public static void launch(Context context, ConnpassEvent event) {

    Intent intent = new Intent(context, ConnpassEventDetailActivity.class);
    intent.putExtra(EXTRA_EVENT, event);
    context.startActivity(intent);
  }

  /* activity lifecycle */

  @Override protected String title() {
    return getString(R.string.title_event_detail);
  }

  @Override protected int contentView() {
    return R.layout.activity_atnd_event_detail;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.inject(this);

    event = (ConnpassEvent) getIntent().getSerializableExtra(EXTRA_EVENT);

    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new EventDetailAdapter(this);
    recyclerView.setAdapter(adapter);
  }

  @Override protected void onDestroy() {
    subscription.unsubscribe();
    super.onDestroy();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  class EventDetailAdapter
      extends SimpleRecyclerAdapter<ConnpassEventDetailViewModel, BindableViewHolder> {

    public EventDetailAdapter(Context context) {
      super(context);
    }

    @Override public int getItemViewType(int position) {
      return getItem(position).getType();
    }

    @Override protected View newView(ViewGroup viewGroup, int viewType) {
      switch (viewType) {
        case AtndEventDetailViewModel.TYPE_HEADER:
          return inflater.inflate(R.layout.item_event_header, viewGroup, false);
        case AtndEventDetailViewModel.TYPE_DETAIL:
          return inflater.inflate(R.layout.item_event_detail, viewGroup, false);
        case AtndEventDetailViewModel.TYPE_MEMBER:
          return inflater.inflate(R.layout.item_event_member, viewGroup, false);
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
      switch (viewType) {
        case ConnpassEventDetailViewModel.TYPE_HEADER:
          return new HeaderViewHolder(view);
        case ConnpassEventDetailViewModel.TYPE_DETAIL:
          return new DetailViewHolder(view);
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    class HeaderViewHolder extends BindableViewHolder {

      @InjectView(R.id.title) TextView title;

      public HeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        title.setText(getItem(position).getTitle());
      }
    }

    class DetailViewHolder extends BindableViewHolder {

      @InjectView(R.id.title) TextView title;
      @InjectView(R.id.owner) TextView owner;

      @InjectView(R.id.catchText) TextView catchText;
      @InjectView(R.id.description) TextView description;
      @InjectView(R.id.eventUrl) ButtonFlat eventUrlButton;

      @InjectView(R.id.date) TextView date;

      @InjectView(R.id.accepted) TextView accepted;
      @InjectView(R.id.limit) TextView limit;
      @InjectView(R.id.waiting) TextView waiting;

      public DetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        ConnpassEvent item = getItem(position).getEvent();

        // what
        title.setText(item.getTitle());
        //owner.setText(item.getOwnerString());
        catchText.setText(item.getCatchText());
        description.setText(Html.fromHtml(item.getDescription()));

        final String eventUrl = item.getEventUrl();
        eventUrlButton.setVisibility(TextUtils.isEmpty(eventUrl) ? View.GONE : View.VISIBLE);
        eventUrlButton.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(eventUrl)));
          }
        });

        final String url = item.getEventUrl();

        // when
        date.setText(item.getEventDateString());

        // how
        limit.setText(String.valueOf(item.getLimit()));
        accepted.setText(String.valueOf(item.getAccept()));
        if (item.isLimitOver()) {
          waiting.setVisibility(View.VISIBLE);
          waiting.setText(String.valueOf(item.getWaiting()));
        } else {
          waiting.setVisibility(View.GONE);
        }

        // where
      }
    }
  }
}
