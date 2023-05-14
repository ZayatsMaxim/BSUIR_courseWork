package com.example.courseworkbyzayats.services;

import com.example.courseworkbyzayats.models.Course;
import com.example.courseworkbyzayats.models.Group;
import com.example.courseworkbyzayats.models.User;
import com.example.courseworkbyzayats.models.dto.CourseStatsDTO;
import com.example.courseworkbyzayats.repositories.CourseRepository;
import com.example.courseworkbyzayats.repositories.CourseStatsRepository;
import com.example.courseworkbyzayats.repositories.GroupRepository;
import com.example.courseworkbyzayats.repositories.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class StatisticsService {
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CourseStatsRepository courseStatsRepository;

    public StatisticsService(CourseRepository courseRepository,
                             GroupRepository groupRepository,
                             UserRepository userRepository,
                             CourseStatsRepository courseStatsRepository) {
        this.courseRepository = courseRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.courseStatsRepository = courseStatsRepository;
    }

    public List<Course> getAllCoursesListForJSON() {
        return courseRepository.getAllCoursesForJSON();
    }

    public List<Group> getAllGroupsListForJSON(){
        return groupRepository.getAllGroupsForJSON();
    }

    public List<User> getAllUsersListForJSON(){
        return userRepository.getAllUsersForJSON();
    }

//    public HashMap<String, Integer> getCoursesStudentsAmount(){
//        return courseRepository.getCoursesSortedByTotalStudents();
//    }

    public List<CourseStatsDTO> getCoursesStudentsAmount() {
        return courseStatsRepository.getCoursesStudentsAmounts();
    }

    public ByteArrayInputStream createGroupPDFReport(Integer groupId) throws DocumentException, IOException {
        Group group = groupRepository.findGroupById(groupId);
        String groupName = group.getName();
        String courseName = courseRepository.getCourseNameById(group.getCourseId());
        List<User> groupStudents = userRepository.findGroupStudents(groupId);

        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        BaseFont TIMES_RUSSIAN = BaseFont.createFont("C:\\Windows\\Fonts\\times.ttf", "cp1251", BaseFont.EMBEDDED);

        Font titleFont = new Font(TIMES_RUSSIAN, 20);
        Paragraph title = new Paragraph("Информация о группе", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font paragraphTitleFont = new Font(TIMES_RUSSIAN, 16);
        Paragraph groupNameTitle = new Paragraph("Название группы: ",paragraphTitleFont);
        groupNameTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(groupNameTitle);

        Font phraseFont = new Font(TIMES_RUSSIAN, 14);
        Phrase groupNamePhrase =  new Phrase(groupName,phraseFont);
        document.add(groupNamePhrase);

        Paragraph courseNameTitle = new Paragraph("Название курса: ", paragraphTitleFont);
        courseNameTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(courseNameTitle);

        Phrase courseNamePhrase =  new Phrase(courseName,phraseFont);
        document.add(courseNamePhrase);

        PdfPTable studentsListTable = new PdfPTable(4);
        studentsListTable.setWidthPercentage(90);
        studentsListTable.setWidths(new int[]{1,1,2,1});

        Font tableHeaderFont = new Font(TIMES_RUSSIAN, 16);
        PdfPCell loginHeaderCell;
        loginHeaderCell = new PdfPCell(new Phrase("Логин", tableHeaderFont));
        loginHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        studentsListTable.addCell(loginHeaderCell);

        PdfPCell nameHeader = new PdfPCell(new Phrase("Имя", tableHeaderFont));
        nameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        studentsListTable.addCell(nameHeader);

        PdfPCell emailHeader = new PdfPCell(new Phrase("Эл. почта", tableHeaderFont));
        emailHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        studentsListTable.addCell(emailHeader);

        PdfPCell phoneHeader = new PdfPCell(new Phrase("Телефон", tableHeaderFont));
        phoneHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        studentsListTable.addCell(phoneHeader);

        Font tableTextFont = new Font(TIMES_RUSSIAN, 12);
        for (User student : groupStudents) {
            PdfPCell cell;

            cell = new PdfPCell(new Phrase(student.getUsername(), tableTextFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            studentsListTable.addCell(cell);

            cell = new PdfPCell(new Phrase(student.getName(), tableTextFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            studentsListTable.addCell(cell);

            cell = new PdfPCell(new Phrase(student.getEmail(), tableTextFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            studentsListTable.addCell(cell);

            cell = new PdfPCell(new Phrase(student.getPhoneNumber(), tableTextFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            studentsListTable.addCell(cell);

        }

        document.add(studentsListTable);
        document.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
