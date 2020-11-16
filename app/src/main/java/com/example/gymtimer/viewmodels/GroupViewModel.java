package com.example.gymtimer.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.interfaces.IGroupDaoManager;
import com.example.gymtimer.interfaces.ObservableList;
import com.example.gymtimer.models.Group;
import com.example.gymtimer.repositories.GroupRepository;

public class GroupViewModel extends AndroidViewModel implements IGroupDaoManager {
  private GroupRepository groupRepository;

  public GroupViewModel(@NonNull Application application) {
    super(application);
    groupRepository = new GroupRepository(application);
  }

  @Override
  public void insert(Group group, DMLOperations<Group> dmlOperations) {
    groupRepository.insert(group, dmlOperations);
  }

  @Override
  public void update(Group group, DMLOperations<Group> dmlOperations) {
    groupRepository.update(group, dmlOperations);
  }

  @Override
  public void getAllGroups(ObservableList<Group> observableList) {
    groupRepository.getAllGroups(observableList);
  }

  @Override
  public void delete(Group group, DMLOperations<Group> dmlOperations) {
    groupRepository.delete(group, dmlOperations);
  }
}
