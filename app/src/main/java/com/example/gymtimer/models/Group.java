package com.example.gymtimer.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "CoreGroup", indices = {@Index(value = {"groupName"}, unique = true),
  @Index(value = {"id", "groupName"}, unique = true)})
public class Group {
  // variables
  @PrimaryKey(autoGenerate = true)
  @NonNull
  private int id;
  private String groupName;

  // constructor
  public Group(String groupName) {
    this.groupName = groupName;
  }

  // getter-setter methods
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  // equals and hashcode
  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj instanceof Group) {
      Group newGroup = (Group) obj;
      return groupName.equals(newGroup.getGroupName());
    }

    return false;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
