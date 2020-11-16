package com.example.gymtimer.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gymtimer.models.Group;

import java.util.List;

@Dao
public interface GroupDao {
  @Insert
  long insert(Group group);

  @Update
  void update(Group group);

  @Query("SELECT * FROM CoreGroup ORDER BY id DESC")
  LiveData<List<Group>> getAllGroups();

  @Delete
  void delete(Group group);
}
