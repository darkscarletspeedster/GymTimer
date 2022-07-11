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
import com.example.gymtimer.models.LinkGroupTimer;
import com.example.gymtimer.models.Timer;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.Locale;
import java.util.Objects;

public class GroupViewAdapter extends ListAdapter<LinkGroupTimer, GroupViewAdapter.GroupViewListHolder> {
  private Context context;
  public int colorChangePosition;

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

  public GroupViewAdapter(Context context, int colorChangePosition) {
    super(DIFF_TIMER);
    this.context = context;
    this.colorChangePosition = colorChangePosition;
  }

  @NonNull
  @Override
  public GroupViewListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingroup_timer_card, parent, false);
    return new GroupViewAdapter.GroupViewListHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull GroupViewListHolder holder, int position) {
    LinkGroupTimer linkGroupTimer = getItem(position);
    if (linkGroupTimer != null) {
      Timer currentTimer = linkGroupTimer.getTimer();
      holder.timerName.setText(currentTimer.getTimerName());
      holder.inGroupTime.setText(linkGroupTimer.getInGroupTime());
      holder.workTimeValueText.setText(currentTimer.getWorkOutTime());
      String setsValue = String.format(Locale.ENGLISH, "%02d", currentTimer.getSets());
      holder.setsValueText.setText(setsValue);
      holder.setBreakValueText.setText(currentTimer.getSetBreak());
      holder.alert1Text.setText(currentTimer.getInWorkoutAlert1());
      holder.alert2Text.setText(currentTimer.getInWorkoutAlert2());
      if (colorChangePosition != -1 && colorChangePosition == position)
        holder.timerCard.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.red, null));
      else
        holder.timerCard.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.border_white_filled_black, null));
    }
  }

  class GroupViewListHolder extends RecyclerView.ViewHolder {
    TextView timerName;
    ImageButton moreInfoBtn;
    TextView inGroupTime;
    ConstraintLayout moreInfoView;
    TextView workTimeValueText;
    TextView alert1Text;
    TextView alert2Text;
    TextView setsValueText;
    TextView setBreakValueText;
    CardView timerCard;

    public GroupViewListHolder(@NonNull View itemView) {
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
      timerCard = itemView.findViewById(R.id.timerCard);

      timerCard.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.border_white_filled_black, null));

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
              Objects.requireNonNull(drawable).setTint(ResourcesCompat.getColor(context.getResources(), R.color.white, null));
              moreInfoBtn.setBackground(drawable);
              moreInfoView.setVisibility(View.VISIBLE);
            }
            linkGroupTimer.setInfoExpanded(!linkGroupTimer.isInfoExpanded());
          }
        });
    }
  }
}
