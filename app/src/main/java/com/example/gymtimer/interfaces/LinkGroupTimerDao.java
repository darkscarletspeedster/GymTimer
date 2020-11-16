package com.example.gymtimer.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gymtimer.models.LinkGroupTimer;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface LinkGroupTimerDao {
  @Insert
  void insert(ArrayList<LinkGroupTimer> linkGroupTimers);

  @Update
  void update(ArrayList<LinkGroupTimer> linkGroupTimers);

  @Query("SELECT * FROM LinkCoreGroupCoreTimer WHERE group_id = :groupId ORDER BY position")
  List<LinkGroupTimer> getAllByGroup (int groupId);

  @Delete
  void delete(LinkGroupTimer linkGroupTimer);

  @Delete
  void deleteMultiple(ArrayList<LinkGroupTimer> linkGroupTimers);

  @Query("SELECT * FROM LinkCoreGroupCoreTimer WHERE timer_id = :timerId")
  List<LinkGroupTimer> getAllByTimer (int timerId);
}
