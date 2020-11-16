package com.example.gymtimer.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "LinkCoreGroupCoreTimer",
  indices = {@Index(value = {"id", "group_id", "timer_id", "position", "inGroupTime"}, unique = true),
            @Index(value = {"group_id", "group_groupName"}),
            @Index(value = {"timer_id", "timer_timerName", "timer_workOutTime", "timer_sets", "timer_setBreak", "timer_inWorkoutAlert1", "timer_inWorkoutAlert2"})},
  foreignKeys = {@ForeignKey(entity = Group.class,
     parentColumns = {"id", "groupName"},
     childColumns = {"group_id", "group_groupName"},
     onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE),
    @ForeignKey(entity = Timer.class,
      parentColumns = {"id", "timerName", "workOutTime", "sets", "setBreak", "inWorkoutAlert1", "inWorkoutAlert2"},
      childColumns = {"timer_id", "timer_timerName", "timer_workOutTime", "timer_sets", "timer_setBreak", "timer_inWorkoutAlert1", "timer_inWorkoutAlert2"},
      onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)})
public class LinkGroupTimer {
  // variables
  @PrimaryKey(autoGenerate = true)
  private int id;
  @Embedded(prefix = "group_")
  @NonNull
  private Group group;
  @Embedded(prefix = "timer_")
  @NonNull
  private Timer timer;
  private int position;
  private String inGroupTime;
  @Ignore
  private boolean isInfoExpanded;
  @Ignore
  private boolean isInGroupTimeEditOn;

  // constructor
  public LinkGroupTimer(@NonNull Group group, @NonNull Timer timer, int position, String inGroupTime) {
    this.group = group;
    this.timer = timer;
    this.position = position;
    this.inGroupTime = inGroupTime;
  }

  // getter-setter methods
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @NonNull
  public Group getGroup() {
    return group;
  }

  public void setGroup(@NonNull Group group) {
    this.group = group;
  }

  @NonNull
  public Timer getTimer() {
    return timer;
  }

  public void setTimer(@NonNull Timer timer) {
    this.timer = timer;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getInGroupTime() {
    return inGroupTime;
  }

  public void setInGroupTime(String inGroupTime) {
    this.inGroupTime = inGroupTime;
  }

  public boolean isInfoExpanded() {
    return isInfoExpanded;
  }

  public void setInfoExpanded(boolean infoExpanded) {
    isInfoExpanded = infoExpanded;
  }

  public boolean isInGroupTimeEditOn() {
    return isInGroupTimeEditOn;
  }

  public void setInGroupTimeEditOn(boolean inGroupTimeEditOn) {
    isInGroupTimeEditOn = inGroupTimeEditOn;
  }

  // equals and hashcode
  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj instanceof LinkGroupTimer) {
      LinkGroupTimer newLink = (LinkGroupTimer) obj;
      return group.getId() == newLink.getGroup().getId()
        && timer.getId() == newLink.getTimer().getId()
        && position == newLink.getPosition()
        && inGroupTime.equals(newLink.getInGroupTime());
    }

    return false;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
