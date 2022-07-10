package com.example.gymtimer.repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.gymtimer.models.Timer;
import com.example.gymtimer.database.TimerDatabase;
import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.interfaces.ObservableList;
import com.example.gymtimer.interfaces.TimerDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimerRepository {
  private TimerDao timerDao;
  private Handler handler;

  public TimerRepository(Application application) {
    TimerDatabase timerDatabase = TimerDatabase.getInstance(application);
    timerDao = timerDatabase.timerDao();
    handler = new Handler(Looper.getMainLooper());
  }

  public void insert(final Timer timer, final DMLOperations<Timer> dmlOperations) {
    new Thread(() -> {
      try {
        timerDao.insert(timer);
        handler.post(() -> dmlOperations.onSuccess(timer));
      } catch (final Exception e) {
        handler.post(() -> {
          if (Objects.requireNonNull(e.getMessage()).contains("UNIQUE"))
            dmlOperations.onFailure(timer, new Exception("Workout with same name present."));
          else
            dmlOperations.onFailure(timer, e);
        });
      }
    }).start();
  }

  public void update(final Timer timer, final DMLOperations<Timer> dmlOperations) {
    new Thread(() -> {
      try {
        timerDao.update(timer);
        handler.post(() -> dmlOperations.onSuccess(timer));
      } catch (final Exception e) {
        handler.post(() -> {
          if (Objects.requireNonNull(e.getMessage()).contains("UNIQUE"))
            dmlOperations.onFailure(timer, new Exception("Workout with same name present."));
          else
            dmlOperations.onFailure(timer, e);
        });
      }
    }).start();
  }

  public void delete(final Timer timer, final DMLOperations<Timer> dmlOperations) {
    new Thread(() -> {
      try {
        timerDao.delete(timer);
        handler.post(() -> dmlOperations.onSuccess(timer));
      } catch (final Exception e) {
        handler.post(() -> dmlOperations.onFailure(timer, e));
      }
    }).start();
  }

  public void  deleteTimers(final ArrayList<Timer> timersToDelete, final DMLOperationsOnMultiple<Timer> dmlOperationsOnMultiple){
    handler.post(() -> new Thread(() -> {
     try {
       timerDao.deleteTimers(timersToDelete);
       handler.post(() -> dmlOperationsOnMultiple.onSuccess(timersToDelete));
     } catch (final Exception e) {
        handler.post(() -> dmlOperationsOnMultiple.onFailure(timersToDelete, e));
     }
    }).start());
  }

  public void getAllTimers(final ObservableList<Timer> observableList) {
    new Thread(() -> {
      try {
        final LiveData<List<Timer>> timers = timerDao.getAllTimers();
        handler.post(() -> observableList.onSuccess(timers));
      } catch (final Exception e) {
        handler.post(() -> observableList.onFailure(e));
      }
    }).start();
  }

  public void getTimer(final String name, final DMLOperations<Timer> dmlOperations) {
    new Thread(() -> {
      try {
        final Timer timer = timerDao.getTimer(name);
        handler.post(() -> dmlOperations.onSuccess(timer));
      } catch (final Exception e) {
        handler.post(() -> dmlOperations.onFailure(null, e));
      }
    }).start();
  }

  public void getAllSavedTimers(final DMLOperationsOnMultiple<Timer> storedTimers) {
    new Thread(() -> {
      try {
        final List<Timer> timers = timerDao.getAllSavedTimers();
        handler.post(() -> storedTimers.onSuccess((ArrayList<Timer>) timers));
      } catch (final Exception e) {
        handler.post(() -> storedTimers.onFailure(null, e));
      }
    }).start();
  }
}
