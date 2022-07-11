package com.example.gymtimer.common;

public class VariableTrigger <T>{
  private T value;
  private Listener<T> l;

  public VariableTrigger(T value) {
    this.value = value;
  }

  public T getValue() {
    return this.value;
  }

  public interface Listener <T>{
    void onChange(T value);
  }

  public void setOnChangeListener(Listener<T> listener){
    this.l = listener;
  }

  public void setValue(T value) {
    this.value = value;
    if (this.l != null)
      this.l.onChange(value);
  }

}
