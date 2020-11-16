package com.example.gymtimer.interfaces;

public interface DMLOperations<T> {
  void onSuccess(T item);
  void onFailure(T item, Exception e);
}
