package com.example.gymtimer.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymtimer.R;
import com.example.gymtimer.models.Timer;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class SelectTimerAdapter extends ListAdapter<Timer, SelectTimerAdapter.TimerListHolder> {
  private Context context;
  public HashMap<Integer, Timer> timersToAdd;

  private static final DiffUtil.ItemCallback<Timer> DIFF_TIMER = new DiffUtil.ItemCallback<Timer>() {
    @Override
    public boolean areItemsTheSame(@NonNull Timer oldItem, @NonNull Timer newItem) {
      return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Timer oldItem, @NonNull Timer newItem) {
      return oldItem.equals(newItem);
    }
  };

  public SelectTimerAdapter(Context context) {
    super(DIFF_TIMER);
    this.context = context;
    timersToAdd = new HashMap<>();
  }

  @NonNull
  @Override
  public TimerListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_timer_card, parent, false);
    return new SelectTimerAdapter.TimerListHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull TimerListHolder holder, int position) {
    Timer currentTimer = getItem(position);
    if (currentTimer != null) {
      holder.timerName.setText(currentTimer.getTimerName());
      holder.workTimeValueText.setText(currentTimer.getWorkOutTime());
      String setsValue = String.format(Locale.ENGLISH, "%02d", currentTimer.getSets());
      holder.setsValueText.setText(setsValue);
      holder.setBreakValueText.setText(currentTimer.getSetBreak());
      holder.alert1Text.setText(currentTimer.getInWorkoutAlert1());
      holder.alert2Text.setText(currentTimer.getInWorkoutAlert2());
    }
  }

  class TimerListHolder extends RecyclerView.ViewHolder {
    TextView timerName;
    ImageButton moreInfoBtn;
    ConstraintLayout moreInfoView;
    TextView workTimeValueText;
    TextView alert1Text;
    TextView alert2Text;
    TextView setsValueText;
    TextView setBreakValueText;
    CardView timerCard;

    public TimerListHolder(@NonNull View itemView) {
      super(itemView);

      timerName = itemView.findViewById(R.id.timerName);
      moreInfoBtn = itemView.findViewById(R.id.moreInfoBtn);
      moreInfoView = itemView.findViewById(R.id.moreInfoView);
      workTimeValueText = itemView.findViewById(R.id.workTimeValueText);
      alert1Text = itemView.findViewById(R.id.alert1Text);
      alert2Text = itemView.findViewById(R.id.alert2Text);
      setsValueText = itemView.findViewById(R.id.setsValueText);
      setBreakValueText = itemView.findViewById(R.id.setBreakValueText);
      timerCard = itemView.findViewById(R.id.timerCard);

      PushDownAnim.setPushDownAnimTo(moreInfoBtn)
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
              Timer timer = getItem(position);
              Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.more_info, null);
              if (timer.isExpanded()) {
                Objects.requireNonNull(drawable).setTint(ResourcesCompat.getColor(context.getResources(), R.color.yellow, null));
                moreInfoBtn.setBackground(drawable);
                moreInfoView.setVisibility(View.GONE);
              } else {
                Objects.requireNonNull(drawable).setTint(ResourcesCompat.getColor(context.getResources(), R.color.white, null));
                moreInfoBtn.setBackground(drawable);
                moreInfoView.setVisibility(View.VISIBLE);
              }
              timer.setIsExpanded(!timer.isExpanded());
            }
          }
        });

      timerCard.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          int position = getAdapterPosition();
          if (position != RecyclerView.NO_POSITION) {
            Timer timer = getItem(position);
            if (timer.isSelected()) {
              timerCard.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.black, null));
              timersToAdd.remove(timer.getId());
            } else {
              timerCard.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.red, null));
              timersToAdd.put(timer.getId(), timer);
            }
            timer.setIsSelected(!timer.isSelected());
          }
        }
      });
    }
  }
}
