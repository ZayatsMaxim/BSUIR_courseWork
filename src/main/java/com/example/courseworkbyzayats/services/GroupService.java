package com.example.courseworkbyzayats.services;

import com.example.courseworkbyzayats.exceptions.AlreadyRegisteredException;
import com.example.courseworkbyzayats.models.Group;
import com.example.courseworkbyzayats.repositories.GroupRepository;
import com.example.courseworkbyzayats.services.validators.AlreadyRegisteredValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final AlreadyRegisteredValidator alreadyRegisteredValidator;
    private final GroupRepository groupRepository;
    private static final int PAGE_SIZE = 6;
    public GroupService(AlreadyRegisteredValidator alreadyRegisteredValidator,
                        GroupRepository groupRepository) {
        this.alreadyRegisteredValidator = alreadyRegisteredValidator;
        this.groupRepository = groupRepository;
    }

    public void registerStudentForGroup(Integer studentId, String groupName) throws AlreadyRegisteredException {
        Group groupForRegistration = groupRepository.findGroupByName(groupName);

        Integer groupId = groupForRegistration.getId();
        Integer courseId = groupForRegistration.getCourseId();

        try{
            alreadyRegisteredValidator.validateIfStudentIsRegisteredForGroup(groupId,studentId);
            alreadyRegisteredValidator.validateIfStudentIsRegisteredForCourse(courseId,studentId);
        } catch (AlreadyRegisteredException e) {
            throw e;
        }
        groupRepository.registerStudentForGroup(groupId, studentId);

    }

    public List<String> getCourseGroupName(Integer id){
        List<Group> courseGroups = groupRepository.findGroupsByCourseId(id);
        return courseGroups.stream().map(Group::getName).toList();
    }

    public Page<Group> getAllGroupsPage(int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber-1, PAGE_SIZE);
        return groupRepository.getAllGroupsPage(pageable);
    }

    public void saveGroup(Group group){
        groupRepository.createGroup(group);
    }

    public Group getGroupById(Integer groupId){
        return  groupRepository.findGroupById(groupId);
    }
}
