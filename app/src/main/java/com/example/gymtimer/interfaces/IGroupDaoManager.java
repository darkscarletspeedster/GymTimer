package com.example.gymtimer.interfaces;

import com.example.gymtimer.models.Group;

public interface IGroupDaoManager {
  void insert(Group group, DMLOperations<Group> dmlOperations);
  void update(Group group, DMLOperations<Group> dmlOperations);
  void getAllGroups(ObservableList<Group> observableList);
  void delete(Group group, DMLOperations<Group> dmlOperations);
}
