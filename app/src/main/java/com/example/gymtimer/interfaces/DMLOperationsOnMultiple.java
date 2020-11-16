package com.example.gymtimer.interfaces;

import java.util.ArrayList;
public interface DMLOperationsOnMultiple<T> {
  void onSuccess(ArrayList<T> items);
  void onFailure(ArrayList<T> items, Exception e);
}
