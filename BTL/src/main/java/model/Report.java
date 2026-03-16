package model;

public class Report {
    private String classId;
    private String subjectId;
    private String className;
    private String subjectName;
    private int totalStudents;
    private float averageScore;
    private float highestScore;
    private float lowestScore;
    private int countGradeA; // Số sinh viên đạt điểm A
    private int countGradeB; // Số sinh viên đạt điểm B
    private int countGradeC; // Số sinh viên đạt điểm C
    private int countGradeD; // Số sinh viên đạt điểm D

    // Constructor
    public Report(String classId, String subjectId, String className, String subjectName,
                  int totalStudents, float averageScore, float highestScore, float lowestScore,
                  int countGradeA, int countGradeB, int countGradeC, int countGradeD) {
        this.classId = classId;
        this.subjectId = subjectId;
        this.className = className;
        this.subjectName = subjectName;
        this.totalStudents = totalStudents;
        this.averageScore = averageScore;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
        this.countGradeA = countGradeA;
        this.countGradeB = countGradeB;
        this.countGradeC = countGradeC;
        this.countGradeD = countGradeD;
    }

    // Getters and Setters
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    public float getAverageScore() { return averageScore; }
    public void setAverageScore(float averageScore) { this.averageScore = averageScore; }
    public float getHighestScore() { return highestScore; }
    public void setHighestScore(float highestScore) { this.highestScore = highestScore; }
    public float getLowestScore() { return lowestScore; }
    public void setLowestScore(float lowestScore) { this.lowestScore = lowestScore; }
    public int getCountGradeA() { return countGradeA; }
    public void setCountGradeA(int countGradeA) { this.countGradeA = countGradeA; }
    public int getCountGradeB() { return countGradeB; }
    public void setCountGradeB(int countGradeB) { this.countGradeB = countGradeB; }
    public int getCountGradeC() { return countGradeC; }
    public void setCountGradeC(int countGradeC) { this.countGradeC = countGradeC; }
    public int getCountGradeD() { return countGradeD; }
    public void setCountGradeD(int countGradeD) { this.countGradeD = countGradeD; }
}