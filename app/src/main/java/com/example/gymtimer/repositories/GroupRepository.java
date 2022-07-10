package com.example.gymtimer.repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.gymtimer.database.TimerDatabase;
import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.interfaces.GroupDao;
import com.example.gymtimer.interfaces.ObservableList;
import com.example.gymtimer.models.Group;

import java.util.List;
import java.util.Objects;

public class GroupRepository {
  private GroupDao groupDao;
  private Handler handler;

  public GroupRepository(Application application) {
    TimerDatabase timerDatabase = TimerDatabase.getInstance(application);
    groupDao = timerDatabase.groupDao();
    handler = new Handler(Looper.getMainLooper());
  }

  public void insert(final Group group, final DMLOperations<Group> dmlOperations) {
    new Thread(() -> {
      try {
        group.setId((int)groupDao.insert(group));
        handler.post(() -> dmlOperations.onSuccess(group));
      } catch (final Exception e) {
        handler.post(() -> {
          if (Objects.requireNonNull(e.getMessage()).contains("UNIQUE"))
            dmlOperations.onFailure(group, new Exception("Group with same name present."));
          else
            dmlOperations.onFailure(group, e);
        });
      }
    }).start();
  }

  public void update(final Group group, final DMLOperations<Group> dmlOperations) {
    new Thread(() -> {
      try {
        groupDao.update(group);
        handler.post(() -> dmlOperations.onSuccess(group));
      } catch (final Exception e) {
        handler.post(() -> {
          if (Objects.requireNonNull(e.getMessage()).contains("UNIQUE"))
            dmlOperations.onFailure(group, new Exception("Group with same name present."));
          else
            dmlOperations.onFailure(group, e);
        });
      }
    }).start();
  }

  public void getAllGroups(final ObservableList<Group> observableList) {
    new Thread(() -> {
      try {
        final LiveData<List<Group>> timers = groupDao.getAllGroups();
        handler.post(() -> observableList.onSuccess(timers));
      } catch (final Exception e) {
        handler.post(() -> observableList.onFailure(e));
      }
    }).start();
  }

  public void delete(final Group group, final DMLOperations<Group> dmlOperations) {
    new Thread(() -> {
      try {
        groupDao.delete(group);
        handler.post(() -> dmlOperations.onSuccess(group));
      } catch (final Exception e) {
        handler.post(() -> {
          if (Objects.requireNonNull(e.getMessage()).contains("UNIQUE"))
            dmlOperations.onFailure(group, new Exception("Group with same name present."));
          else
            dmlOperations.onFailure(group, e);
        });
      }
    }).start();
  }
}
