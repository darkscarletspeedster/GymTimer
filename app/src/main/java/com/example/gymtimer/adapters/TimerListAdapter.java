package com.example.gymtimer.adapters;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gymtimer.MainActivity;
import com.example.gymtimer.R;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.models.Timer;
import com.example.gymtimer.viewmodels.TimerViewModel;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class TimerListAdapter extends ListAdapter<Timer, TimerListAdapter.TimerListHolder> {
  private Context parentContext;
  private OnEditClicked onEditClicked;
  private MainActivity mainActivity;
  private HashMap<Integer, Timer> timersToDelete;

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

  public TimerListAdapter(Context context) {
    super(DIFF_TIMER);
    this.parentContext = context;
    mainActivity = (MainActivity) context;
  }

  @NonNull
  @Override
  public TimerListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_card, parent, false);
    return new TimerListHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull TimerListHolder holder, int position) {
    Timer currentTimer = getItem(position);
    if (currentTimer != null) {
      holder.timerName.setText(currentTimer.getTimerName());
      holder.timerCard.setCardBackgroundColor(ResourcesCompat.getColor(parentContext.getResources(), R.color.transparent, null));
      if (mainActivity.selectAll) {
        holder.timerCard.setCardBackgroundColor(ResourcesCompat.getColor(parentContext.getResources(), R.color.red, null));
        timersToDelete.put(currentTimer.getId(), currentTimer);
        mainActivity.updateCounter(timersToDelete.size());
      } else {
        holder.timerCard.setCardBackgroundColor(ResourcesCompat.getColor(parentContext.getResources(), R.color.transparent, null));
        mainActivity.updateCounter(0);
        if (timersToDelete != null)
          timersToDelete.remove(currentTimer.getId());
      }

      if (mainActivity.isDeleteOn) {
        holder.editButton.setVisibility(View.GONE);
        holder.startButton.setVisibility(View.GONE);
        holder.expandBtn.setVisibility(View.GONE);
        currentTimer.setIsExpanded(false);
        currentTimer.setIsSelected(false);
        holder.expandBtn.setImageDrawable(ResourcesCompat.getDrawable(parentContext.getResources(), R.drawable.expand_card, null));
        holder.moreInfoView.setVisibility(View.GONE);
      } else {
        holder.editButton.setVisibility(View.VISIBLE);
        holder.startButton.setVisibility(View.VISIBLE);
        holder.expandBtn.setVisibility(View.VISIBLE);
        holder.workTimeValueText.setText(currentTimer.getWorkOutTime());
        String setsValue = String.format(Locale.ENGLISH, "%02d", currentTimer.getSets());
        holder.setsValueText.setText(setsValue);
        holder.setBreakValueText.setText(currentTimer.getSetBreak());
        holder.alert1Text.setText(currentTimer.getInWorkoutAlert1());
        holder.alert2Text.setText(currentTimer.getInWorkoutAlert2());
      }
    }
  }

  public Timer getTimer(int position) {
    return getItem(position);
  }

  class TimerListHolder extends RecyclerView.ViewHolder {
    ConstraintLayout moreInfoView;
    ImageButton expandBtn;
    TextView timerName;
    TextView workTimeValueText;
    TextView setsValueText;
    TextView setBreakValueText;
    ImageButton editButton;
    CardView timerCard;
    Button startButton;
    TextView alert1Text;
    TextView alert2Text;

    public TimerListHolder(@NonNull View itemView) {
      super(itemView);

      moreInfoView = itemView.findViewById(R.id.moreInfoView);
      expandBtn = itemView.findViewById(R.id.expandBtn);
      timerName = itemView.findViewById(R.id.timerName);
      workTimeValueText = itemView.findViewById(R.id.workTimeValueText);
      setsValueText = itemView.findViewById(R.id.setsValueText);
      setBreakValueText = itemView.findViewById(R.id.setBreakValueText);
      editButton = itemView.findViewById(R.id.editBtn);
      timerCard = itemView.findViewById(R.id.timerCard);
      startButton = itemView.findViewById(R.id.startBtn);
      alert1Text = itemView.findViewById(R.id.alert1Text);
      alert2Text = itemView.findViewById(R.id.alert2Text);

      PushDownAnim.setPushDownAnimTo(startButton)
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
              mainActivity.startTimer(getItem(position));
            }
          }
        });

      expandBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          int position = getAdapterPosition();
          if (position != RecyclerView.NO_POSITION) {
            Timer timer = getItem(position);
            if (timer.isExpanded()) {
              expandBtn.setImageDrawable(ResourcesCompat.getDrawable(parentContext.getResources(), R.drawable.expand_card, null));
              moreInfoView.setVisibility(View.GONE);
            } else {
              expandBtn.setImageDrawable(ResourcesCompat.getDrawable(parentContext.getResources(), R.drawable.collapse_card, null));
              moreInfoView.setVisibility(View.VISIBLE);
            }
            timer.setIsExpanded(!timer.isExpanded());
          }
        }
      });

      editButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          int position = getAdapterPosition();
          if (onEditClicked != null && position != RecyclerView.NO_POSITION)
            onEditClicked.onEditClick(getItem(position));
        }
      });

      timerCard.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mainActivity.isDeleteOn) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
              Timer timer = getItem(position);
              if (timer.isSelected()) {
                timerCard.setCardBackgroundColor(ResourcesCompat.getColor(parentContext.getResources(), R.color.transparent, null));
                timersToDelete.remove(timer.getId());
              } else {
                timerCard.setCardBackgroundColor(ResourcesCompat.getColor(parentContext.getResources(), R.color.red, null));
                timersToDelete.put(timer.getId(), timer);
              }
              mainActivity.updateCounter(timersToDelete.size());
              timer.setIsSelected(!timer.isSelected());
            }
          }
        }
      });

      timerCard.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          if (!mainActivity.isDeleteOn)
            timersToDelete = new HashMap<>();
          else
            timersToDelete = null;

          return mainActivity.onLongClick(v);
        }
      });
    }
  }

  public interface OnEditClicked {
    void onEditClick(Timer timer);
  }

  public void setOnEditClicked(OnEditClicked onEditClicked) {
    this.onEditClicked = onEditClicked;
  }

  public boolean deleteSelectedItems(TimerViewModel timerViewModel, DMLOperationsOnMultiple<Timer> dmlOperationsOnMultiple) {
    if (timersToDelete.size() == 0)
      return false;

    timerViewModel.deleteTimers(new ArrayList<>(timersToDelete.values()), dmlOperationsOnMultiple);
    timersToDelete = null;
    return true;
  }
}
