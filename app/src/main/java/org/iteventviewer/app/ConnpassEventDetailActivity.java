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
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.gc.materialdesign.views.ButtonFlat;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.service.atnd.json.AtndEvent;
import org.iteventviewer.service.atnd.model.AtndEventDetailViewModel;
import org.iteventviewer.service.compass.json.ConnpassEvent;
import org.iteventviewer.service.compass.model.ConnpassEventDetailViewModel;
import org.iteventviewer.util.SnsUtil;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 *
 */
public class ConnpassEventDetailActivity extends BaseEventDetailActivity {

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

  @Override protected Intent createShareIntent() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, event.getEventUrl());
    return shareIntent;
  }

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

    adapter.addItem(ConnpassEventDetailViewModel.detail(event));
  }

  @Override protected void onDestroy() {
    subscription.unsubscribe();
    super.onDestroy();
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
        case ConnpassEventDetailViewModel.TYPE_DETAIL:
          return inflater.inflate(R.layout.item_connpass_event_detail, viewGroup, false);
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
      switch (viewType) {
        case ConnpassEventDetailViewModel.TYPE_DETAIL:
          return new DetailViewHolder(view);
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    class DetailViewHolder extends BindableViewHolder {

      @InjectView(R.id.title) TextView title;
      @InjectView(R.id.owner) TextView owner;

      @InjectView(R.id.catchText) TextView catchText;
      @InjectView(R.id.description) TextView description;
      @InjectView(R.id.eventUrl) TextView eventUrlText;

      @InjectView(R.id.date) TextView date;

      @InjectView(R.id.stateNotEmptyContainer) ViewGroup statusNotEmptyContainer;
      @InjectView(R.id.stateEmpty) TextView statusEmpty;
      @InjectView(R.id.accepted) TextView accepted;
      @InjectView(R.id.limit) TextView limit;
      @InjectView(R.id.waiting) TextView waiting;

      @InjectView(R.id.addressAndPlace) TextView addressAndPlace;

      public DetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        final ConnpassEvent item = getItem(position).getEvent();

        // what
        title.setText(item.getTitle());
        owner.setText(item.getOwnerString());
        catchText.setText(item.getCatchText());
        description.setText(Html.fromHtml(item.getDescription()));

        eventUrlText.setText(item.getEventUrlText(self));

        // when
        date.setMovementMethod(LinkMovementMethod.getInstance());
        date.setText(item.getEventDateSpannableString(self));

        // how
        if (item.hasLimit()) {
          statusNotEmptyContainer.setVisibility(View.VISIBLE);
          statusEmpty.setVisibility(View.GONE);
          limit.setText(String.valueOf(item.getLimit()));
          accepted.setText(String.valueOf(item.getAccepted()));
          accepted.setTextColor(item.getAcceptedColor(self));
          waiting.setText(String.valueOf(item.getWaiting()));
          waiting.setTextColor(item.getWaitingColor(self));
        } else {
          statusNotEmptyContainer.setVisibility(View.GONE);
          statusEmpty.setVisibility(View.VISIBLE);
        }

        // where
        if (item.hasLocation()) {
          addressAndPlace.setMovementMethod(LinkMovementMethod.getInstance());
          addressAndPlace.setText(item.getAddressAndPlaceSpannableString(self));
        } else {
          addressAndPlace.setText(item.getAddressAndPlaceString());
        }
      }
    }
  }
}
