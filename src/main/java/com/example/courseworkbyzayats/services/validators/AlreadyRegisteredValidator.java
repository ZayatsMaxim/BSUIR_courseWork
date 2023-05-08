package com.example.courseworkbyzayats.services.validators;

import com.example.courseworkbyzayats.exceptions.AlreadyRegisteredException;
import com.example.courseworkbyzayats.repositories.GroupRepository;
import org.springframework.stereotype.Component;

@Component
public class AlreadyRegisteredValidator {
    private final GroupRepository groupRepository;

    public AlreadyRegisteredValidator(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void validateIfStudentIsRegisteredForCourse(Integer courseId,
                                                       Integer studentId) throws AlreadyRegisteredException {
        if (groupRepository.howManyTimesStudentIsRegisteredForCourse(courseId, studentId)!=0){
            throw new AlreadyRegisteredException("Вы уже зарегестрированы на этот курс в другой группе!");
        }
    }

    public void validateIfStudentIsRegisteredForGroup(Integer groupId,
                                                      Integer studentId) throws AlreadyRegisteredException{
        if (groupRepository.howManyTimesStudentIsRegisteredInGroup(groupId, studentId) != 0){
            throw new AlreadyRegisteredException("Вы уже зарегестрированы в этой группе!");
        }
    }

}
