package com.example.gymtimer.models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "CoreTimer", indices = {@Index(value = {"timerName"}, unique = true),
  @Index(value = {"id", "timerName", "workOutTime", "sets", "setBreak", "inWorkoutAlert1", "inWorkoutAlert2"}, unique = true)})
public class Timer {

  // variables
  @PrimaryKey(autoGenerate = true)
  private int id;
  @Ignore
  private boolean isExpanded = false;
  private String timerName;
  private String workOutTime;
  private int sets;
  private String setBreak;
  @Ignore
  private boolean isSelected = false;
  private String inWorkoutAlert1;
  private String inWorkoutAlert2;

  // constructor
  public Timer(String timerName) {
    this.timerName = timerName;
  }

  // getter-setter methods
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isExpanded() {
    return isExpanded;
  }

  public void setIsExpanded(boolean isExpanded) {
    this.isExpanded = isExpanded;
  }

  public String getTimerName() {
    return this.timerName;
  }

  public void setTimerName(String timerName) {
    this.timerName = timerName;
  }

  public String getWorkOutTime() {
    return workOutTime;
  }

  public void setWorkOutTime(String workOutTime) {
    this.workOutTime = workOutTime;
  }

  public int getSets() {
    return sets;
  }

  public void setSets(int sets) {
    this.sets = sets;
  }

  public String getSetBreak() {
    return setBreak;
  }

  public void setSetBreak(String setBreak) {
    this.setBreak = setBreak;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setIsSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }

  public String getInWorkoutAlert1() {
    return inWorkoutAlert1;
  }

  public void setInWorkoutAlert1(String inWorkoutAlert1) {
    this.inWorkoutAlert1 = inWorkoutAlert1;
  }

  public String getInWorkoutAlert2() {
    return inWorkoutAlert2;
  }

  public void setInWorkoutAlert2(String inWorkoutAlert2) {
    this.inWorkoutAlert2 = inWorkoutAlert2;
  }

  // equals and hashcode
  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj instanceof Timer) {
      Timer newTimer = (Timer) obj;
      return timerName.equals(newTimer.getTimerName())
        && workOutTime.equals(newTimer.getWorkOutTime())
        && sets == newTimer.getSets()
        && setBreak.equals(newTimer.getSetBreak())
        && inWorkoutAlert1.equals(newTimer.getInWorkoutAlert1())
        && inWorkoutAlert2.equals(newTimer.getInWorkoutAlert2());
    }

    return false;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
