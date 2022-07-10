package com.example.gymtimer.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gymtimer.MainActivity;
import com.example.gymtimer.R;
import com.example.gymtimer.models.LinkGroupTimer;
import com.example.gymtimer.models.Timer;
import com.example.gymtimer.adapters.TimerListAdapter;
import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.interfaces.ObservableList;
import com.example.gymtimer.viewmodels.LinkGroupTimerViewModel;
import com.example.gymtimer.viewmodels.TimerViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainListTab extends Fragment {
  private Context context;
  private ArrayList<Timer> curTimers;
  private TimerListAdapter timerListAdapter;
  private TimerViewModel timerViewModel;
  private MainActivity mainActivity;

  public MainListTab(Context context, TimerViewModel timerViewModel) {
    this.context = context;
    this.timerViewModel = timerViewModel;
    mainActivity = (MainActivity) context;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main_list_tab, container, false);
    final RecyclerView timerRecyclerView = rootView.findViewById(R.id.recyclerView);
    timerListAdapter = new TimerListAdapter(context);
    curTimers = new ArrayList<>();

    timerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    timerRecyclerView.setAdapter(timerListAdapter);
    timerListAdapter.submitList(curTimers);
    final LinkGroupTimerViewModel linkGroupTimerViewModel = new LinkGroupTimerViewModel(mainActivity.application);

    timerViewModel.getAllTimers(new ObservableList<>() {
      @Override
      public void onSuccess(LiveData<List<Timer>> itemsList) {
        itemsList.observeForever(timers -> {
          ArrayList<Timer> updatedTimerList = (ArrayList<Timer>) timers;
          timerListAdapter.submitList(updatedTimerList);
          curTimers = updatedTimerList;
        });
        mainActivity.appProgressBar.setVisibility(View.GONE);
      }

      @Override
      public void onFailure(Exception e) {
        mainActivity.appProgressBar.setVisibility(View.GONE);
        showMessage(e.getMessage());
      }
    });

    timerListAdapter.setOnEditClicked(timer -> {
      mainActivity.navLayout.setVisibility(View.GONE);
      requireActivity().getSupportFragmentManager()
        .beginTransaction()
        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
        .replace(R.id.fragmentContainer, new AddEditTimer(context, timer, timerViewModel))
        .addToBackStack(null)
        .commit();
    });

    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
      @Override
      public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
      }

      @Override
      public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        mainActivity.appProgressBar.setVisibility(View.VISIBLE);
        final Timer timer = curTimers.get(viewHolder.getAbsoluteAdapterPosition());
        linkGroupTimerViewModel.getAllByTimer(timer.getId(), new DMLOperationsOnMultiple<>() {
          @Override
          public void onSuccess(final ArrayList<LinkGroupTimer> items) {
            timerViewModel.delete(timer, new DMLOperations<>() {
              @Override
              public void onSuccess(final Timer timerToDelete) {
                mainActivity.appProgressBar.setVisibility(View.GONE);
                showMessage(timerToDelete.getTimerName() + getString(R.string.workout_deleted));
                Snackbar.make(timerRecyclerView, timerToDelete.getTimerName(), Snackbar.LENGTH_SHORT)
                  .setAction(R.string.undo_delete, v -> {
                    if (getActivity() != null) {
                      mainActivity.appProgressBar.setVisibility(View.VISIBLE);
                      timerViewModel.insert(timerToDelete, new DMLOperations<>() {
                        @Override
                        public void onSuccess(final Timer timerInserted) {
                          linkGroupTimerViewModel.insert(items, new DMLOperationsOnMultiple<>() {
                            @Override
                            public void onSuccess(ArrayList<LinkGroupTimer> items1) {
                              mainActivity.appProgressBar.setVisibility(View.GONE);
                              showMessage(timerInserted.getTimerName() + getString(R.string.workout_added_back));
                            }

                            @Override
                            public void onFailure(ArrayList<LinkGroupTimer> items1, Exception e) {
                              mainActivity.appProgressBar.setVisibility(View.GONE);
                              showMessage(e.getMessage());
                            }
                          });
                        }

                        @Override
                        public void onFailure(Timer item, Exception e) {
                          mainActivity.appProgressBar.setVisibility(View.GONE);
                          showMessage(e.getMessage());
                        }
                      });
                    } else {
                      new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(mainActivity, R.string.could_not_undo_as_screen_closed, Toast.LENGTH_SHORT).show());
                    }
                  }).show();
              }

              @Override
              public void onFailure(Timer item, Exception e) {
                mainActivity.appProgressBar.setVisibility(View.GONE);
                curTimers.add(viewHolder.getAbsoluteAdapterPosition(), item);
                timerListAdapter.notifyItemInserted(viewHolder.getAbsoluteAdapterPosition());
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
    }).attachToRecyclerView(timerRecyclerView);

    return rootView;
  }

  private void showMessage(String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  public void refreshAdapter() {
    timerListAdapter.notifyDataSetChanged();
  }

  public boolean deleteItems(DMLOperationsOnMultiple<Timer> dmlOperationsOnMultiple) {
    return timerListAdapter.deleteSelectedItems(timerViewModel, dmlOperationsOnMultiple);
  }
}
