package com.example.gymtimer.models;

import com.example.gymtimer.interfaces.OnBreakFinish;
import com.example.gymtimer.interfaces.OnWorkoutTimeFinish;
public class PauseModel {
  private Timer timer;
  private boolean isBreak;
  private boolean isGroupBreak;
  private long pendingMill;
  private OnBreakFinish onBreakFinish;
  private OnWorkoutTimeFinish onWorkoutTimeFinish;
  private String breakTime;

  public OnBreakFinish getOnBreakFinish() {
    return onBreakFinish;
  }

  public void setOnBreakFinish(OnBreakFinish onBreakFinish) {
    this.onBreakFinish = onBreakFinish;
  }

  public OnWorkoutTimeFinish getOnWorkoutTimeFinish() {
    return onWorkoutTimeFinish;
  }

  public void setOnWorkoutTimeFinish(OnWorkoutTimeFinish onWorkoutTimeFinish) {
    this.onWorkoutTimeFinish = onWorkoutTimeFinish;
  }

  public String getBreakTime() {
    return breakTime;
  }

  public void setBreakTime(String breakTime) {
    this.breakTime = breakTime;
  }

  public Timer getTimer() {
    return timer;
  }

  public void setTimer(Timer timer) {
    this.timer = timer;
  }

  public boolean isBreak() {
    return isBreak;
  }

  public void setIsBreak(boolean aBreak) {
    isBreak = aBreak;
  }

  public boolean isGroupBreak() {
    return isGroupBreak;
  }

  public void setIsGroupBreak(boolean groupBreak) {
    isGroupBreak = groupBreak;
  }

  public long getPendingMill() {
    return pendingMill;
  }

  public void setPendingMill(long pendingMill) {
    this.pendingMill = pendingMill;
  }
}
