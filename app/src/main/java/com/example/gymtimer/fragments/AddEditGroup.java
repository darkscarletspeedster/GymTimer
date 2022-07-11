package com.example.gymtimer.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymtimer.MainActivity;
import com.example.gymtimer.R;
import com.example.gymtimer.adapters.InGroupTimerAdapter;
import com.example.gymtimer.dialogs.ListDialog;
import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.models.Group;
import com.example.gymtimer.models.LinkGroupTimer;
import com.example.gymtimer.models.Timer;
import com.example.gymtimer.viewmodels.GroupViewModel;
import com.example.gymtimer.viewmodels.LinkGroupTimerViewModel;
import com.example.gymtimer.viewmodels.TimerViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AddEditGroup extends Fragment {

  final private Context context;
  private Group group;
  private ArrayList<LinkGroupTimer> linkGroupTimers;
  final private TimerViewModel timerViewModel;
  private boolean isNew;
  private InGroupTimerAdapter inGroupTimerAdapter;
  private LinearLayoutManager linearLayoutManager;
  final private MainActivity mainActivity;
  private GroupViewModel groupViewModel;
  private LinkGroupTimerViewModel linkGroupTimerViewModel;
  private HashSet<LinkGroupTimer> toAddElements;
  private RecyclerView inGroupView;
  private HashSet<LinkGroupTimer> toDeleteLinkGroupTimers;
  private boolean isToBeAddedWithUpdate;
  private ItemTouchHelper itemTouchHelper;

  public AddEditGroup(Context context, Group group, TimerViewModel timerViewModel) {
    this.context = context;
    this.group = group;
    this.timerViewModel = timerViewModel;
    mainActivity = (MainActivity) context;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_add_edit_group, container, false);
    TextView textView = rootView.findViewById(R.id.addEditLabelText);
    inGroupView = rootView.findViewById(R.id.timerListView);
    linearLayoutManager = new LinearLayoutManager(context);
    inGroupTimerAdapter = new InGroupTimerAdapter(context);
    final ImageButton addTimers = rootView.findViewById(R.id.addTimerBtn);
    linkGroupTimers = new ArrayList<>();
    Button cancelBtn = rootView.findViewById(R.id.cancelBtn);
    Button saveBtn = rootView.findViewById(R.id.saveGroupBtn);
    final EditText groupNameEdit = rootView.findViewById(R.id.groupNameEdit);
    groupViewModel = new GroupViewModel(mainActivity.application);
    linkGroupTimerViewModel = new LinkGroupTimerViewModel(mainActivity.application);
    toAddElements = new HashSet<>();
    toDeleteLinkGroupTimers = new HashSet<>();

    // rearranging Items
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
      | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
      @Override
      public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPos = viewHolder.getAbsoluteAdapterPosition();
        int toPos = target.getAbsoluteAdapterPosition();
        Collections.swap(linkGroupTimers, fromPos, toPos);
        linkGroupTimers.get(fromPos).setPosition(fromPos + 1);
        linkGroupTimers.get(toPos).setPosition(toPos + 1);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(fromPos, toPos);
        if (fromPos == recyclerView.getAdapter().getItemCount() - 1)
          target.itemView.findViewById(R.id.inGroupTime).setVisibility(View.INVISIBLE);
        else
          target.itemView.findViewById(R.id.inGroupTime).setVisibility(View.VISIBLE);

        if (toPos == recyclerView.getAdapter().getItemCount() - 1)
          viewHolder.itemView.findViewById(R.id.inGroupTime).setVisibility(View.INVISIBLE);
        else
          viewHolder.itemView.findViewById(R.id.inGroupTime).setVisibility(View.VISIBLE);

        return true;
      }

      @Override
      public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAbsoluteAdapterPosition();
        final LinkGroupTimer linkGroupTimer = linkGroupTimers.get(position);
        linkGroupTimers.remove(position);
        inGroupTimerAdapter.notifyItemRemoved(position);
        if (linkGroupTimers.size() == position) {
          View view = linearLayoutManager.findViewByPosition(position - 1);
          Objects.requireNonNull(view).findViewById(R.id.inGroupTime).setVisibility(View.GONE);
        }

        toDeleteLinkGroupTimers.add(linkGroupTimer);
        if (toAddElements.contains(linkGroupTimer)) {
          toAddElements.remove(linkGroupTimer);
          isToBeAddedWithUpdate = true;
        }
        showMessage(linkGroupTimer.getTimer().getTimerName() + getString(R.string.workout_deleted));
        for (int i = position; i < linkGroupTimers.size(); i++) {
          linkGroupTimers.get(i).setPosition(i + 1);
        }
        Snackbar.make(inGroupView, linkGroupTimer.getTimer().getTimerName() , Snackbar.LENGTH_SHORT)
          .setAction(R.string.undo_delete, v -> {
            if (getActivity() != null) {
              if (linkGroupTimers.size() == position) {
                View view = linearLayoutManager.findViewByPosition(position - 1);
                Objects.requireNonNull(view).findViewById(R.id.inGroupTime).setVisibility(View.VISIBLE);
              }

              linkGroupTimers.add(position, linkGroupTimer);
              inGroupTimerAdapter.notifyItemInserted(position);

              toDeleteLinkGroupTimers.remove(linkGroupTimer);
              if (isToBeAddedWithUpdate) {
                isToBeAddedWithUpdate = false;
                toAddElements.add(linkGroupTimer);
              }
              showMessage(linkGroupTimer.getTimer().getTimerName()  + getString(R.string.workout_added_back));
              for (int i = position; i < linkGroupTimers.size(); i++) {
                linkGroupTimers.get(i).setPosition(i + 1);
              }
            } else {
              new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(mainActivity, R.string.could_not_undo_as_screen_closed, Toast.LENGTH_SHORT).show());
            }
          }).show();
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
    };

    itemTouchHelper = new ItemTouchHelper(simpleCallback);

    inGroupView.setLayoutManager(linearLayoutManager);
    inGroupView.setAdapter(inGroupTimerAdapter);
    itemTouchHelper.attachToRecyclerView(inGroupView);
    inGroupTimerAdapter.submitList(linkGroupTimers);
    inGroupTimerAdapter.setTouchHelper(inGroupView, itemTouchHelper);

    if (group == null) {
      textView.setText(R.string.add_new_group);
      isNew = true;
      group = new Group("");
    } else {
      textView.setText(R.string.edit_group);
      isNew = false;
      if (getActivity() != null) {
        groupNameEdit.setText(group.getGroupName());
        mainActivity.appProgressBar.setVisibility(View.VISIBLE);
        addTimers.setEnabled(false);
        linkGroupTimerViewModel.getAllByGroup(group.getId(), new DMLOperationsOnMultiple<>() {
          @Override
          public void onSuccess(ArrayList<LinkGroupTimer> items) {
            inGroupTimerAdapter.submitList(items);
            linkGroupTimers = items;
            mainActivity.appProgressBar.setVisibility(View.GONE);
            addTimers.setEnabled(true);
          }

          @Override
          public void onFailure(ArrayList<LinkGroupTimer> items, Exception e) {
            showMessage(e.getMessage());
            mainActivity.appProgressBar.setVisibility(View.GONE);
            addTimers.setEnabled(true);
          }
        });
      }
    }

    PushDownAnim.setPushDownAnimTo(cancelBtn)
      .setOnClickListener(v -> requireActivity().onBackPressed());

    PushDownAnim.setPushDownAnimTo(addTimers)
      .setOnClickListener(v -> {
        mainActivity.appProgressBar.setVisibility(View.VISIBLE);
        timerViewModel.getAllSavedTimers(new DMLOperationsOnMultiple<>() {
          @Override
          public void onSuccess(ArrayList<Timer> items) {
            mainActivity.appProgressBar.setVisibility(View.GONE);
            ListDialog listDialog = new ListDialog(context, items, AddEditGroup.this);
            listDialog.show();
          }

          @Override
          public void onFailure(ArrayList<Timer> items, Exception e) {
            mainActivity.appProgressBar.setVisibility(View.GONE);
            showMessage(e.getMessage());
          }
        });
      });

    PushDownAnim.setPushDownAnimTo(saveBtn)
      .setOnClickListener(v -> {
        if (groupNameEdit.getText().toString().isEmpty()) {
          showMessage(getString(R.string.group_name_cannot_be_empty));
          groupNameEdit.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_red, null));
          return;
        }

        mainActivity.appProgressBar.setVisibility(View.VISIBLE);
        group.setGroupName(groupNameEdit.getText().toString());
        if (isNew) {
          groupViewModel.insert(group, new DMLOperations<>() {
            @Override
            public void onSuccess(Group item) {
              group = item;
              for (int i = 0; i < linkGroupTimers.size(); i++) {
                linkGroupTimers.get(i).setGroup(group);
              }
              linkGroupTimerViewModel.insert(linkGroupTimers, new DMLOperationsOnMultiple<>() {
                @Override
                public void onSuccess(ArrayList<LinkGroupTimer> items) {
                  mainActivity.appProgressBar.setVisibility(View.GONE);
                  if (getActivity() != null) {
                    showMessage(group.getGroupName() + getString(R.string.inserted_successfully));
                    requireActivity().onBackPressed();
                  }
                }

                @Override
                public void onFailure(ArrayList<LinkGroupTimer> items, Exception e) {
                  showMessage(e.getMessage());
                  mainActivity.appProgressBar.setVisibility(View.GONE);
                }
              });
            }

            @Override
            public void onFailure(Group item, Exception e) {
              showMessage(e.getMessage());
              mainActivity.appProgressBar.setVisibility(View.GONE);
            }
          });
        } else {
          groupViewModel.update(group, new DMLOperations<>() {
            @Override
            public void onSuccess(Group item) {
              linkGroupTimerViewModel.deleteMultiple(new ArrayList<>(toDeleteLinkGroupTimers), new DMLOperationsOnMultiple<>() {
                @Override
                public void onSuccess(ArrayList<LinkGroupTimer> items) {
                  for (LinkGroupTimer item : linkGroupTimers ) {
                    if (item.getGroup().getId() == group.getId()){
                      item.setGroup(group);
                    }
                  }
                  linkGroupTimerViewModel.update(linkGroupTimers, new DMLOperationsOnMultiple<>() {
                    @Override
                    public void onSuccess(ArrayList<LinkGroupTimer> items) {
                      linkGroupTimerViewModel.insert(new ArrayList<>(toAddElements), new DMLOperationsOnMultiple<>() {
                        @Override
                        public void onSuccess(ArrayList<LinkGroupTimer> items) {
                          mainActivity.appProgressBar.setVisibility(View.GONE);
                          if (getActivity() != null) {
                            showMessage(group.getGroupName() + getString(R.string.updated_successfully));
                            requireActivity().onBackPressed();
                          }
                        }

                        @Override
                        public void onFailure(ArrayList<LinkGroupTimer> items, Exception e) {
                          showMessage(e.getMessage());
                          mainActivity.appProgressBar.setVisibility(View.GONE);
                        }
                      });
                    }

                    @Override
                    public void onFailure(ArrayList<LinkGroupTimer> items, Exception e) {
                      showMessage(e.getMessage());
                      mainActivity.appProgressBar.setVisibility(View.GONE);
                    }
                  });
                }

                @Override
                public void onFailure(ArrayList<LinkGroupTimer> items, Exception e) {
                  showMessage(e.getMessage());
                  mainActivity.appProgressBar.setVisibility(View.GONE);
                }
              });
            }

            @Override
            public void onFailure(Group item, Exception e) {
              showMessage(e.getMessage());
              mainActivity.appProgressBar.setVisibility(View.GONE);
            }
          });
        }
      });

    groupNameEdit.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        groupNameEdit.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_white, null));
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    return rootView;
  }

  private void showMessage(String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  public void addTimersToList (final ArrayList<Timer> timers) {
    final int n = linkGroupTimers.size();
    for (int i = 0; i < timers.size(); i++) {
      LinkGroupTimer linkGroupTimer = new LinkGroupTimer(group, timers.get(i), n + i + 1, "01:00");
      if (!isNew)
        toAddElements.add(linkGroupTimer);

      linkGroupTimers.add(linkGroupTimer);
    }
    inGroupTimerAdapter.notifyItemRangeInserted(n, timers.size());
    if (n != 0) {
      inGroupTimerAdapter.notifyItemChanged(n - 1);
    }
    showMessage(timers.size() + getString(R.string.timers_added_at_the_end));
  }
}
