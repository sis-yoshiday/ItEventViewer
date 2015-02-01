package org.iteventviewer.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.Map;
import javax.inject.Inject;
import org.iteventviewer.common.BindableViewHolder;
import org.iteventviewer.common.SimpleRecyclerAdapter;
import org.iteventviewer.service.atnd.AtndService;
import org.iteventviewer.service.atnd.EventDetailViewModel;
import org.iteventviewer.service.atnd.MemberSearchQuery;
import org.iteventviewer.service.atnd.json.Event;
import org.iteventviewer.service.atnd.json.EventMember;
import org.iteventviewer.service.atnd.json.SearchResult;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class EventDetailActivity extends ToolBarActivity {

  public static final String EXTRA_EVENT = "event";

  @Inject AtndService atndService;

  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  EventDetailAdapter adapter;

  private Event event;

  public static void launch(Context context, Event event) {

    Intent intent = new Intent(context, EventDetailActivity.class);
    intent.putExtra(EXTRA_EVENT, event);
    context.startActivity(intent);
  }

  /* activity lifecycle */

  @Override protected String title() {
    return getString(R.string.app_name);
  }

  @Override protected int contentView() {
    return R.layout.activity_event_detail;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.inject(this);
    ((MyApplication) getApplication()).inject(this);

    event = (Event) getIntent().getSerializableExtra(EXTRA_EVENT);

    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new EventDetailAdapter(this);
    adapter.addItem(EventDetailViewModel.header(event));
    recyclerView.setAdapter(adapter);

    Map<String, String> query =
        new MemberSearchQuery.Builder().addEventId(event.getEventId()).build();

    AndroidObservable.bindActivity(this, atndService.searchEventMember(query))
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<SearchResult<EventMember>>() {
          @Override public void call(SearchResult<EventMember> result) {

          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {

          }
        });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  class EventDetailAdapter extends SimpleRecyclerAdapter<EventDetailViewModel, BindableViewHolder> {

    public EventDetailAdapter(Context context) {
      super(context);
    }

    @Override public int getItemViewType(int position) {
      return getItem(position).getType();
    }

    @Override protected View newView(ViewGroup viewGroup, int viewType) {
      switch (viewType) {
        case EventDetailViewModel.TYPE_HEADER:
          return inflater.inflate(R.layout.item_event_header, viewGroup, false);
        case EventDetailViewModel.TYPE_MEMBER:
          return inflater.inflate(R.layout.item_event_member, viewGroup, false);
        case EventDetailViewModel.TYPE_MEMBER_EMPTY:
          return inflater.inflate(R.layout.item_event_member_empty, viewGroup, false);
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override protected BindableViewHolder newViewHolder(View view, int viewType) {
      switch (viewType) {
        case EventDetailViewModel.TYPE_HEADER:
          return new HeaderViewHolder(view);
        case EventDetailViewModel.TYPE_MEMBER:
          return new MemberViewHolder(view);
        case EventDetailViewModel.TYPE_MEMBER_EMPTY:
          return new MemberEmptyViewHolder(view);
        default:
          throw new IllegalArgumentException("viewType");
      }
    }

    @Override public void onBindViewHolder(BindableViewHolder holder, int position) {
      holder.bind(position);
    }

    class HeaderViewHolder extends BindableViewHolder {

      @InjectView(R.id.title) TextView title;
      @InjectView(R.id.catchText) TextView catchText;
      @InjectView(R.id.description) TextView description;
      @InjectView(R.id.eventUrl) TextView eventUrl;
      @InjectView(R.id.url) TextView url;

      @InjectView(R.id.startedAt) TextView startedAt;
      @InjectView(R.id.endedAt) TextView endedAt;

      @InjectView(R.id.accepted) TextView accepted;
      @InjectView(R.id.limit) TextView limit;
      @InjectView(R.id.waiting) TextView waiting;

      @InjectView(R.id.address) TextView address;
      @InjectView(R.id.place) TextView place;

      @InjectView(R.id.ownerNickname) TextView ownerNickname;
      @InjectView(R.id.ownerTwitterId) TextView ownerTwitterId;

      public HeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

        Event item = getItem(position).getEvent();

        // what
        title.setText(item.getTitle());
        catchText.setText(item.getCatchText());
        description.setText(item.getDescription());
        eventUrl.setText(item.getEventUrl());
        url.setText(item.getUrl());

        // when
        startedAt.setText(
            item.getStartedAt().toString(DateTimeFormat.forPattern("yyyy/MM/dd HH:mm")));
        LocalDateTime _endedAt = item.getEndedAt();
        if (_endedAt != null) {
          endedAt.setText(_endedAt.toString(DateTimeFormat.forPattern("yyyy/MM/dd HH:mm")));
        } else {
          endedAt.setText("");
        }

        // how
        limit.setText(String.valueOf(item.getLimit()));
        accepted.setText(String.valueOf(item.getAccepted()));
        waiting.setText(String.valueOf(item.getWaiting()));

        // where
        address.setText(item.getAddress());
        place.setText(item.getPlace());
        item.getLat();
        item.getLng();

        // who
        ownerNickname.setText(item.getOwnerNickname());
        ownerTwitterId.setText(item.getOwnerTwitterId());
      }
    }

    class MemberViewHolder extends BindableViewHolder {

      public MemberViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

      }
    }

    class MemberEmptyViewHolder extends BindableViewHolder {

      public MemberEmptyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
      }

      @Override public void bind(int position) {

      }
    }
  }
}
