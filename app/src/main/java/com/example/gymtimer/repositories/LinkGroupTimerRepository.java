package com.example.gymtimer.repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.gymtimer.R;
import com.example.gymtimer.database.TimerDatabase;
import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.interfaces.LinkGroupTimerDao;
import com.example.gymtimer.models.LinkGroupTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LinkGroupTimerRepository {
  private LinkGroupTimerDao linkGroupTimerDao;
  private Handler handler;
  private Application application;

  public LinkGroupTimerRepository(Application application) {
    TimerDatabase timerDatabase = TimerDatabase.getInstance(application);
    linkGroupTimerDao = timerDatabase.linkGroupTimerDao();
    handler = new Handler(Looper.getMainLooper());
    this.application = application;
  }

  public void insert(final ArrayList<LinkGroupTimer> linkGroupTimers, final DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          linkGroupTimerDao.insert(linkGroupTimers);
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperationsOnMultiple.onSuccess(linkGroupTimers);
            }
          });
        } catch (final Exception e) {
          handler.post(new Runnable() {
            @Override
            public void run() {
              if (Objects.requireNonNull(e.getMessage()).contains("UNIQUE"))
                dmlOperationsOnMultiple.onFailure(linkGroupTimers, new Exception(application.getApplicationContext().getResources().getString(R.string.linkgrouptimer_already_present)));
              else
                dmlOperationsOnMultiple.onFailure(linkGroupTimers, e);
            }
          });
        }
      }
    }).start();
  }

  public void update(final ArrayList<LinkGroupTimer> linkGroupTimers, final DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          linkGroupTimerDao.update(linkGroupTimers);
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperationsOnMultiple.onSuccess(linkGroupTimers);
            }
          });
        } catch (final Exception e) {
          handler.post(new Runnable() {
            @Override
            public void run() {

              if (Objects.requireNonNull(e.getMessage()).contains("UNIQUE"))
                dmlOperationsOnMultiple.onFailure(linkGroupTimers, new Exception(application.getApplicationContext().getResources().getString(R.string.linkgrouptimer_already_present)));
              else
                dmlOperationsOnMultiple.onFailure(linkGroupTimers, e);
            }
          });
        }
      }
    }).start();
  }

  public void getAllByGroup(final int groupId, final DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          final List<LinkGroupTimer> linkGroupTimers = linkGroupTimerDao.getAllByGroup(groupId);
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperationsOnMultiple.onSuccess((ArrayList<LinkGroupTimer>) linkGroupTimers);
            }
          });
        } catch (final Exception e) {
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperationsOnMultiple.onFailure(null, e);
            }
          });
        }
      }
    }).start();
  }

  public void delete(final LinkGroupTimer linkGroupTimer, final DMLOperations<LinkGroupTimer> dmlOperations) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          linkGroupTimerDao.delete(linkGroupTimer);
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperations.onSuccess(linkGroupTimer);
            }
          });
        } catch (final Exception e) {
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperations.onFailure(linkGroupTimer, e);
            }
          });
        }
      }
    }).start();
  }

  public void deleteMultiple(final ArrayList<LinkGroupTimer> linkGroupTimers, final DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          linkGroupTimerDao.deleteMultiple(linkGroupTimers);
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperationsOnMultiple.onSuccess(linkGroupTimers);
            }
          });
        } catch (final Exception e) {
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperationsOnMultiple.onFailure(linkGroupTimers, e);
            }
          });
        }
      }
    }).start();
  }

  public void getAllByTimer(final int timerId, final DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          final List<LinkGroupTimer> linkGroupTimers = linkGroupTimerDao.getAllByTimer(timerId);
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperationsOnMultiple.onSuccess((ArrayList<LinkGroupTimer>) linkGroupTimers);
            }
          });
        } catch (final Exception e) {
          handler.post(new Runnable() {
            @Override
            public void run() {
              dmlOperationsOnMultiple.onFailure(null, e);
            }
          });
        }
      }
    }).start();
  }
}
