package com.example.gymtimer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymtimer.R;
import com.example.gymtimer.dialogs.GroupTimersDialog;
import com.example.gymtimer.fragments.GroupManager;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.models.Group;
import com.example.gymtimer.models.LinkGroupTimer;
import com.example.gymtimer.viewmodels.LinkGroupTimerViewModel;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupListAdapter extends ListAdapter<Group, GroupListAdapter.GroupListHolder> {
  private OnEditClicked onEditClicked;
  private OnStartClicked onStartClicked;
  private GroupManager groupManager;
  public boolean isExpanded;
  private HashMap<Integer, ArrayList<LinkGroupTimer>> linkGroupTimerHashMap;
  public boolean isGroupOn;


  private static final DiffUtil.ItemCallback<Group> DIFF_GROUP = new DiffUtil.ItemCallback<>() {
    @Override
    public boolean areItemsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
      return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
      return oldItem.equals(newItem);
    }
  };

  public GroupListAdapter(GroupManager groupManager) {
    super(DIFF_GROUP);
    this.groupManager = groupManager;
    linkGroupTimerHashMap = new HashMap<>();
  }

  @NonNull
  @Override
  public GroupListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_card, parent, false);
    return new GroupListHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull GroupListHolder holder, int position) {
    Group group = getItem(position);
    if (group != null) {
      holder.groupName.setText(group.getGroupName());
    }
  }

  class GroupListHolder extends RecyclerView.ViewHolder {
    TextView groupName;
    ImageButton editGroup;
    Button startBtn;
    ImageButton expandGroupBtn;

    public GroupListHolder(@NonNull View itemView) {
      super(itemView);

      groupName = itemView.findViewById(R.id.groupName);
      editGroup = itemView.findViewById(R.id.editGroup);
      startBtn = itemView.findViewById(R.id.startBtn);
      expandGroupBtn = itemView.findViewById(R.id.expandGroupBtn);

      editGroup.setOnClickListener(v -> {
        if(!isGroupOn) {
          int position = getAbsoluteAdapterPosition();
          if (onEditClicked != null && position != RecyclerView.NO_POSITION)
            onEditClicked.onEditClick(getItem(position));
        }
      });

      PushDownAnim.setPushDownAnimTo(startBtn)
        .setOnClickListener(v -> {
          int position = getAbsoluteAdapterPosition();
          if (onStartClicked != null && position != RecyclerView.NO_POSITION) {
            onStartClicked.onStartClicked(getItem(position));
          }
        });

      PushDownAnim.setPushDownAnimTo(expandGroupBtn)
        .setOnClickListener(v -> {
          if (!isExpanded) {
            isExpanded = true;
            int position = getAbsoluteAdapterPosition();
            if (onEditClicked != null && position != RecyclerView.NO_POSITION) {
              final Group group = getItem(position);
              if (linkGroupTimerHashMap.containsKey(group.getId())) {
                GroupTimersDialog groupTimersDialog = new GroupTimersDialog(groupManager.requireContext(),
                  GroupListAdapter.this, linkGroupTimerHashMap.get(group.getId()));
                groupTimersDialog.show();
              } else {
                groupManager.mainActivity.appProgressBar.setVisibility(View.VISIBLE);
                LinkGroupTimerViewModel linkGroupTimerViewModel = new LinkGroupTimerViewModel(groupManager.requireActivity().getApplication());
                linkGroupTimerViewModel.getAllByGroup(group.getId(), new DMLOperationsOnMultiple<>() {
                  @Override
                  public void onSuccess(ArrayList<LinkGroupTimer> items) {
                    linkGroupTimerHashMap.put(group.getId(), items);
                    groupManager.mainActivity.appProgressBar.setVisibility(View.GONE);
                    GroupTimersDialog groupTimersDialog = new GroupTimersDialog(groupManager.requireContext(), GroupListAdapter.this, items);
                    groupTimersDialog.show();
                  }

                  @Override
                  public void onFailure(ArrayList<LinkGroupTimer> items, Exception e) {
                    groupManager.mainActivity.appProgressBar.setVisibility(View.GONE);
                    groupManager.showMessage(e.getMessage());
                  }
                });
              }
            }
          }
        });
    }
  }

  public interface OnEditClicked {
    void onEditClick(Group group);
  }

  public interface OnStartClicked {
    void onStartClicked(Group group);
  }

  public void setOnEditClicked(OnEditClicked onEditClicked) {
    this.onEditClicked = onEditClicked;
  }

  public void setOnStartClicked(OnStartClicked onStartClicked) {
    this.onStartClicked = onStartClicked;
  }
}
