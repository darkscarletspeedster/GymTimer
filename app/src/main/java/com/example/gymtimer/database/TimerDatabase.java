package com.example.gymtimer.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gymtimer.interfaces.GroupDao;
import com.example.gymtimer.interfaces.LinkGroupTimerDao;
import com.example.gymtimer.models.Group;
import com.example.gymtimer.models.LinkGroupTimer;
import com.example.gymtimer.models.Timer;
import com.example.gymtimer.interfaces.TimerDao;

@Database(entities = {Timer.class, Group.class, LinkGroupTimer.class}, version = 2, exportSchema = false)
public abstract class TimerDatabase extends RoomDatabase {
  private static TimerDatabase instance;
  public abstract TimerDao timerDao();
  public abstract GroupDao groupDao();
  public abstract LinkGroupTimerDao linkGroupTimerDao();

  public static synchronized TimerDatabase getInstance(Context context) {
    if (instance == null) {
      instance = Room.databaseBuilder(context.getApplicationContext(),
        TimerDatabase.class, "timer_database")
        .fallbackToDestructiveMigration() // has to be changed and add migration strategy while changing schema of the tables
        .build();
    }
    return instance;
  }
}
