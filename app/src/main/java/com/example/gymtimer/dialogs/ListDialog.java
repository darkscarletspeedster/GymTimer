package com.example.gymtimer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymtimer.R;
import com.example.gymtimer.adapters.SelectTimerAdapter;
import com.example.gymtimer.fragments.AddEditGroup;
import com.example.gymtimer.models.Timer;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.Objects;

public class ListDialog extends Dialog {
  private ArrayList<Timer> timers;
  private Context context;
  private AddEditGroup addEditGroup;

  public ListDialog(@NonNull Context context, ArrayList<Timer> timers, AddEditGroup addEditGroup) {
    super(context);
    this.context = context;
    this.timers = timers;
    this.addEditGroup = addEditGroup;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_dialog);
    Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(R.color.transparent);

    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    final SelectTimerAdapter selectTimerAdapter = new SelectTimerAdapter(context);
    Button saveBtn = findViewById(R.id.saveBtn);

    recyclerView.setLayoutManager(new LinearLayoutManager(context));
    recyclerView.setAdapter(selectTimerAdapter);
    selectTimerAdapter.submitList(timers);

    PushDownAnim.setPushDownAnimTo(saveBtn)
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          addEditGroup.addTimersToList(new ArrayList<>(selectTimerAdapter.timersToAdd.values()));
          dismiss();
        }
      });
  }
}
