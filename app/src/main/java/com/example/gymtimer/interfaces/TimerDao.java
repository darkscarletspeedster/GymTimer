package com.example.gymtimer.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gymtimer.models.Timer;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TimerDao {
  @Insert
  void insert(Timer timer);

  @Update
  void update(Timer timer);

  @Delete
  void delete(Timer timer);

  @Delete
  void deleteTimers(ArrayList<Timer> timers);

  @Query("SELECT * FROM CoreTimer ORDER BY id DESC")
  LiveData<List<Timer>> getAllTimers();

  @Query("SELECT * FROM CoreTimer WHERE timerName = :name")
  Timer getTimer(String name);

  @Query("SELECT * FROM CoreTimer ORDER BY id DESC")
  List<Timer> getAllSavedTimers();
}
