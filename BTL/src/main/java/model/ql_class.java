package model;

import java.util.List;

public class ql_class {
    private String classId;
    private String className;
    private String subjectId;
    private String subjectName;
    private String teacherId;
    private String lecturer;
    private List<String> allSubjects; // Added to store all subject names

    // Default constructor
    public ql_class() {
    }

    // Full constructor
    public ql_class(String classId, String className, String subjectId, String subjectName, String teacherId, String lecturer) {
        this.classId = classId;
        this.className = className;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.teacherId = teacherId;
        this.lecturer = lecturer;
    }

    // Constructor without lecturer and subjectName
    public ql_class(String classId, String className, String subjectId, String teacherId) {
        this.classId = classId;
        this.className = className;
        this.subjectId = subjectId;
        this.teacherId = teacherId;
    }

    // Getters and Setters
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public List<String> getAllSubjects() {
        return allSubjects;
    }

    public void setAllSubjects(List<String> allSubjects) {
        this.allSubjects = allSubjects;
    }
}