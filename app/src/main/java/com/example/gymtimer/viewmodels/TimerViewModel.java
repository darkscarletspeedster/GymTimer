package com.example.gymtimer.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.gymtimer.models.Timer;
import com.example.gymtimer.repositories.TimerRepository;
import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.interfaces.ITimerDaoManager;
import com.example.gymtimer.interfaces.ObservableList;

import java.util.ArrayList;

public class TimerViewModel extends AndroidViewModel implements ITimerDaoManager {
  private TimerRepository repository;

  public TimerViewModel(@NonNull Application application) {
    super(application);
    repository = new TimerRepository(application);
  }

  public void insert(Timer timer, DMLOperations<Timer> dmlOperations) {
    repository.insert(timer, dmlOperations);
  }

  public void update(Timer timer, DMLOperations<Timer> dmlOperations) {
    repository.update(timer, dmlOperations);
  }

  public void delete(Timer timer, DMLOperations<Timer> dmlOperations) {
    repository.delete(timer, dmlOperations);
  }

  public void deleteTimers(ArrayList<Timer> timers, DMLOperationsOnMultiple<Timer> timersToDelete) {
    repository.deleteTimers(timers, timersToDelete);
  }

  public void getAllTimers(ObservableList<Timer> observableList) {
    repository.getAllTimers(observableList);
  }

  public void getTimer(String name, DMLOperations<Timer> dmlOperations) {
    repository.getTimer(name, dmlOperations);
  }

  @Override
  public void getAllSavedTimers(DMLOperationsOnMultiple<Timer> storedTimers) {
    repository.getAllSavedTimers(storedTimers);
  }
}
