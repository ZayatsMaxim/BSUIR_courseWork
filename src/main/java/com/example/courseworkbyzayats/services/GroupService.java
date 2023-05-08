package com.example.courseworkbyzayats.services;

import com.example.courseworkbyzayats.exceptions.AlreadyRegisteredException;
import com.example.courseworkbyzayats.models.Group;
import com.example.courseworkbyzayats.repositories.GroupRepository;
import com.example.courseworkbyzayats.services.validators.AlreadyRegisteredValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final AlreadyRegisteredValidator alreadyRegisteredValidator;
    private final GroupRepository groupRepository;

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
}
