package com.example.gymtimer.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gymtimer.MainActivity;
import com.example.gymtimer.R;
import com.example.gymtimer.adapters.GroupListAdapter;
import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.interfaces.ObservableList;
import com.example.gymtimer.interfaces.OnGroupFinish;
import com.example.gymtimer.models.Group;
import com.example.gymtimer.models.LinkGroupTimer;
import com.example.gymtimer.viewmodels.GroupViewModel;
import com.example.gymtimer.viewmodels.LinkGroupTimerViewModel;
import com.example.gymtimer.viewmodels.TimerViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class GroupManager extends Fragment {
  private Context context;
  private ArrayList<Group> groups;
  private TimerViewModel timerViewModel;
  public MainActivity mainActivity;

  public GroupManager(Context context, TimerViewModel timerViewModel) {
    this.context = context;
    this.timerViewModel = timerViewModel;
    this.mainActivity = (MainActivity)context;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_group_manager, container, false);

    final ImageButton addNewGroup = rootView.findViewById(R.id.addGroup);
    final RecyclerView groupRecyclerView = rootView.findViewById(R.id.recyclerView);
    final GroupListAdapter groupListAdapter = new GroupListAdapter(GroupManager.this);
    groups = new ArrayList<>();
    final GroupViewModel groupViewModel = new GroupViewModel(mainActivity.application);
    final LinkGroupTimerViewModel linkGroupTimerViewModel = new LinkGroupTimerViewModel(mainActivity.application);

    groupRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    groupRecyclerView.setAdapter(groupListAdapter);
    groupListAdapter.submitList(groups);

    groupViewModel.getAllGroups(new ObservableList<Group>() {
      @Override
      public void onSuccess(LiveData<List<Group>> itemsList) {
        itemsList.observe(getViewLifecycleOwner(), new Observer<List<Group>>() {
          @Override
          public void onChanged(List<Group> groups) {
            ArrayList<Group> updatedGroupList = (ArrayList<Group>) groups;
            groupListAdapter.submitList(updatedGroupList);
            GroupManager.this.groups = updatedGroupList;
          }
        });
        mainActivity.appProgressBar.setVisibility(View.GONE);
      }

      @Override
      public void onFailure(Exception e) {
        mainActivity.appProgressBar.setVisibility(View.GONE);
        showMessage(e.getMessage());
      }
    });

    PushDownAnim.setPushDownAnimTo(addNewGroup)
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Objects.requireNonNull(getActivity()).getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            .replace(R.id.fragmentContainer, new AddEditGroup(context, null, timerViewModel))
            .addToBackStack(null)
            .commit();
        }
      });

    groupListAdapter.setOnEditClicked(new GroupListAdapter.OnEditClicked() {
      @Override
      public void onEditClick(Group group) {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
          .beginTransaction()
          .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
          .replace(R.id.fragmentContainer, new AddEditGroup(context, group, timerViewModel))
          .addToBackStack(null)
          .commit();
      }
    });

    groupListAdapter.setOnStartClicked(new GroupListAdapter.OnStartClicked() {
      @Override
      public void onStartClicked(Group group) {
        if(!mainActivity.getIsTimerOn()) {
          addNewGroup.setEnabled(false);
          groupListAdapter.isGroupOn = true;
          LinkGroupTimerViewModel linkGroupTimerViewModel = new LinkGroupTimerViewModel(mainActivity.application);
          mainActivity.appProgressBar.setVisibility(View.VISIBLE);
          linkGroupTimerViewModel.getAllByGroup(group.getId(), new DMLOperationsOnMultiple<LinkGroupTimer>() {
            @Override
            public void onSuccess(ArrayList<LinkGroupTimer> items) {
              mainActivity.appProgressBar.setVisibility(View.GONE);
              if (items != null && items.size() != 0)
                mainActivity.startGroup(items, new OnGroupFinish() {
                  @Override
                  public void setOnGroupFinishListener() {
                    addNewGroup.setEnabled(true);
                    groupListAdapter.isGroupOn = false;
                  }
                });
              else
                showMessage(getString(R.string.no_workouts_present));
            }

            @Override
            public void onFailure(ArrayList<LinkGroupTimer> items, Exception e) {
              mainActivity.appProgressBar.setVisibility(View.GONE);
              showMessage(e.getMessage());
            }
          });
        }
      }
    });

    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
      @Override
      public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
      }

      @Override
      public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        mainActivity.appProgressBar.setVisibility(View.VISIBLE);
        final Group group = groups.get(viewHolder.getAdapterPosition());
        linkGroupTimerViewModel.getAllByGroup(group.getId(), new DMLOperationsOnMultiple<LinkGroupTimer>() {
          @Override
          public void onSuccess(final ArrayList<LinkGroupTimer> items) {
            groupViewModel.delete(group, new DMLOperations<Group>() {
              @Override
              public void onSuccess(final Group groupToDelete) {
                mainActivity.appProgressBar.setVisibility(View.GONE);
                showMessage(groupToDelete.getGroupName() + getString(R.string.group_deleted));
                Snackbar.make(groupRecyclerView, groupToDelete.getGroupName(), Snackbar.LENGTH_SHORT)
                  .setAction(R.string.undo_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      if (getActivity() != null) {
                        mainActivity.appProgressBar.setVisibility(View.VISIBLE);
                        groupViewModel.insert(groupToDelete, new DMLOperations<Group>() {
                          @Override
                          public void onSuccess(final Group groupInserted) {
                            linkGroupTimerViewModel.insert(items, new DMLOperationsOnMultiple<LinkGroupTimer>() {
                              @Override
                              public void onSuccess(ArrayList<LinkGroupTimer> items) {
                                mainActivity.appProgressBar.setVisibility(View.GONE);
                                showMessage(groupInserted.getGroupName() + getString(R.string.group_added_back));
                              }

                              @Override
                              public void onFailure(ArrayList<LinkGroupTimer> items, Exception e) {
                                mainActivity.appProgressBar.setVisibility(View.GONE);
                                showMessage(e.getMessage());
                              }
                            });
                          }

                          @Override
                          public void onFailure(Group item, Exception e) {
                            mainActivity.appProgressBar.setVisibility(View.GONE);
                            showMessage(e.getMessage());
                          }
                        });
                      } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                          @Override
                          public void run() {
                            Toast.makeText(mainActivity, R.string.could_not_undo_as_screen_closed, Toast.LENGTH_SHORT).show();
                          }
                        });
                      }
                    }
                  }).show();
              }

              @Override
              public void onFailure(Group item, Exception e) {
                mainActivity.appProgressBar.setVisibility(View.GONE);
                groups.add(viewHolder.getAdapterPosition(), item);
                groupListAdapter.notifyItemInserted(viewHolder.getAdapterPosition());
                showMessage(e.getMessage());
              }
            });
          }

          @Override
          public void onFailure(ArrayList<LinkGroupTimer> items, Exception e) {
            mainActivity.appProgressBar.setVisibility(View.GONE);
            showMessage(e.getMessage());
          }
        });
      }

      @Override
      public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
          .addBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null))
          .addActionIcon(R.drawable.delete_timers)
          .create()
          .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
      }
    }).attachToRecyclerView(groupRecyclerView);

    return rootView;
  }

  public void showMessage(String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }
}
