package com.example.gymtimer.interfaces;

import com.example.gymtimer.models.Timer;

import java.util.ArrayList;

public interface ITimerDaoManager {
  void insert(Timer timer, DMLOperations<Timer> dmlOperations);
  void update(Timer timer, DMLOperations<Timer> dmlOperations);
  void delete( Timer timer, DMLOperations<Timer> dmlOperations);
  void deleteTimers(ArrayList<Timer> timers, DMLOperationsOnMultiple<Timer> timersToDelete);
  void getAllTimers(ObservableList<Timer> observableList);
  void getTimer(String name, DMLOperations<Timer> dmlOperations);
  void getAllSavedTimers(DMLOperationsOnMultiple<Timer> storedTimers);
}
