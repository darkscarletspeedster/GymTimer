package com.example.gymtimer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymtimer.R;
import com.example.gymtimer.adapters.GroupListAdapter;
import com.example.gymtimer.adapters.GroupViewAdapter;
import com.example.gymtimer.models.LinkGroupTimer;

import java.util.ArrayList;
import java.util.Objects;

public class GroupTimersDialog extends Dialog {
  private GroupListAdapter groupListAdapter;
  private ArrayList<LinkGroupTimer> linkGroupTimers;
  private Context context;

  public GroupTimersDialog(@NonNull Context context, GroupListAdapter groupListAdapter, ArrayList<LinkGroupTimer> linkGroupTimers) {
    super(context);
    this.groupListAdapter = groupListAdapter;
    this.linkGroupTimers = linkGroupTimers;
    this.context = context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.group_timers_dialog);

    Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(R.color.transparent);
    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    GroupViewAdapter groupViewAdapter = new GroupViewAdapter(context);

    recyclerView.setLayoutManager(new LinearLayoutManager(context));
    recyclerView.setAdapter(groupViewAdapter);
    groupViewAdapter.submitList(linkGroupTimers);
  }

  @Override
  protected void onStop() {
    groupListAdapter.isExpanded = false;
    super.onStop();
  }
}
