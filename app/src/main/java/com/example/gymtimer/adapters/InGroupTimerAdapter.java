package com.example.gymtimer.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymtimer.R;
import com.example.gymtimer.models.LinkGroupTimer;
import com.example.gymtimer.models.Timer;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.Locale;
import java.util.Objects;

public class InGroupTimerAdapter extends ListAdapter<LinkGroupTimer, InGroupTimerAdapter.InGroupTimerListHolder> {
  private final Context context;
  private RecyclerView inGroupView;
  private ItemTouchHelper itemTouchHelper;

  private final static DiffUtil.ItemCallback<LinkGroupTimer> DIFF_TIMER = new DiffUtil.ItemCallback<>() {
    @Override
    public boolean areItemsTheSame(@NonNull LinkGroupTimer oldItem, @NonNull LinkGroupTimer newItem) {
      return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull LinkGroupTimer oldItem, @NonNull LinkGroupTimer newItem) {
      return oldItem.equals(newItem);
    }
  };

  public InGroupTimerAdapter(Context context) {
    super(DIFF_TIMER);
    this.context = context;
  }

  @NonNull
  @Override
  public InGroupTimerListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingroup_timer_card, parent, false);
    return new InGroupTimerListHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull InGroupTimerListHolder holder, int position) {
    LinkGroupTimer linkGroupTimer = getItem(position);
    if (linkGroupTimer != null) {
      linkGroupTimer.setPosition(position + 1);
      Timer currentTimer = linkGroupTimer.getTimer();
      if (getItemCount() - 1 == position)
        holder.inGroupTime.setVisibility(View.INVISIBLE);
      else
        holder.inGroupTime.setVisibility(View.VISIBLE);

      holder.timerName.setText(currentTimer.getTimerName());
      holder.inGroupTime.setText(linkGroupTimer.getInGroupTime());
      holder.workTimeValueText.setText(currentTimer.getWorkOutTime());
      String setsValue = String.format(Locale.ENGLISH, "%02d", currentTimer.getSets());
      holder.setsValueText.setText(setsValue);
      holder.setBreakValueText.setText(currentTimer.getSetBreak());
      holder.alert1Text.setText(currentTimer.getInWorkoutAlert1());
      holder.alert2Text.setText(currentTimer.getInWorkoutAlert2());
    }
  }

  public void setTouchHelper (RecyclerView inGroupView, ItemTouchHelper itemTouchHelper) {
    this.inGroupView = inGroupView;
    this.itemTouchHelper = itemTouchHelper;
  }

  class InGroupTimerListHolder extends RecyclerView.ViewHolder {
    TextView timerName;
    ImageButton moreInfoBtn;
    TextView inGroupTime;
    ConstraintLayout moreInfoView;
    TextView workTimeValueText;
    TextView alert1Text;
    TextView alert2Text;
    TextView setsValueText;
    TextView setBreakValueText;
    ConstraintLayout inGroupTimerLayout;
    NumberPicker inGroupTimeMin;
    NumberPicker inGroupTimeSec;

    public InGroupTimerListHolder(@NonNull final View itemView) {
      super(itemView);

      timerName = itemView.findViewById(R.id.timerName);
      moreInfoBtn = itemView.findViewById(R.id.moreInfoBtn);
      inGroupTime = itemView.findViewById(R.id.inGroupTime);
      moreInfoView = itemView.findViewById(R.id.moreInfoView);
      workTimeValueText = itemView.findViewById(R.id.workTimeValueText);
      alert1Text = itemView.findViewById(R.id.alert1Text);
      alert2Text = itemView.findViewById(R.id.alert2Text);
      setsValueText = itemView.findViewById(R.id.setsValueText);
      setBreakValueText = itemView.findViewById(R.id.setBreakValueText);
      inGroupTimerLayout = itemView.findViewById(R.id.inGroupTimerLayout);
      inGroupTimeMin = itemView.findViewById(R.id.inGroupTimeMin);
      inGroupTimeSec = itemView.findViewById(R.id.inGroupTimeSec);

      PushDownAnim.setPushDownAnimTo(moreInfoBtn)
        .setOnClickListener(v -> {
          int position = getAbsoluteAdapterPosition();
          if (position != RecyclerView.NO_POSITION) {
            LinkGroupTimer linkGroupTimer = getItem(position);
            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.more_info, null);
            if (linkGroupTimer.isInfoExpanded()) {
              Objects.requireNonNull(drawable).setTint(ResourcesCompat.getColor(context.getResources(), R.color.yellow, null));
              moreInfoBtn.setBackground(drawable);
              moreInfoView.setVisibility(View.GONE);
            } else {
              Objects.requireNonNull(drawable).setTint(ResourcesCompat.getColor(context.getResources(), R.color.veryLightBlack, null));
              moreInfoBtn.setBackground(drawable);
              inGroupTimerLayout.setVisibility(View.GONE);
              linkGroupTimer.setInGroupTimeEditOn(false);
              moreInfoView.setVisibility(View.VISIBLE);
            }
            linkGroupTimer.setInfoExpanded(!linkGroupTimer.isInfoExpanded());
          }
        });

      inGroupTime.setOnClickListener(v -> {
        int position = getAbsoluteAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
          final LinkGroupTimer linkGroupTimer = getItem(position);

          if (linkGroupTimer.isInGroupTimeEditOn()) {
            inGroupTimerLayout.setVisibility(View.GONE);
          } else {
            int min = Integer.parseInt(linkGroupTimer.getInGroupTime().substring(0, 2));
            int sec = Integer.parseInt(linkGroupTimer.getInGroupTime().substring(3));

            inGroupTimeMin.setMinValue(0);
            inGroupTimeMin.setMaxValue(59);
            inGroupTimeMin.setValue(min);
            inGroupTimeSec.setMinValue(0);
            inGroupTimeSec.setMaxValue(59);
            inGroupTimeSec.setValue(sec);
            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.more_info, null);
            Objects.requireNonNull(drawable).setTint(ResourcesCompat.getColor(context.getResources(), R.color.yellow, null));
            moreInfoBtn.setBackground(drawable);
            moreInfoView.setVisibility(View.GONE);
            linkGroupTimer.setInfoExpanded(false);
            inGroupTimerLayout.setVisibility(View.VISIBLE);

            inGroupTimeMin.setOnScrollListener((view, scrollState) -> {
              if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_FLING || scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                itemTouchHelper.attachToRecyclerView(null);
              else
                itemTouchHelper.attachToRecyclerView(inGroupView);
            });
            inGroupTimeMin.setOnValueChangedListener((picker, oldVal, newVal) -> {
              String time = String.format(Locale.ENGLISH, "%02d", newVal) + inGroupTime.getText().toString().substring(2);
              inGroupTime.setText(time);
              linkGroupTimer.setInGroupTime(time);
              itemTouchHelper.attachToRecyclerView(inGroupView);
            });

            inGroupTimeSec.setOnScrollListener((view, scrollState) -> {
              if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_FLING || scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                itemTouchHelper.attachToRecyclerView(null);
              else
                itemTouchHelper.attachToRecyclerView(inGroupView);
            });
            inGroupTimeSec.setOnValueChangedListener((picker, oldVal, newVal) -> {
              String time = inGroupTime.getText().toString().substring(0, 3) + String.format(Locale.ENGLISH, "%02d", newVal);
              inGroupTime.setText(time);
              linkGroupTimer.setInGroupTime(time);
              itemTouchHelper.attachToRecyclerView(inGroupView);
            });
          }
          linkGroupTimer.setInGroupTimeEditOn(!linkGroupTimer.isInGroupTimeEditOn());
        }
      });
    }
  }
}
