package model;


import java.util.HashMap;
import java.util.Map;


public class Subject {
    private String subjectId;
    private String subjectName;
    private Integer credits;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Subject(String subjectId, String subjectName, Integer credits) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.credits = credits;
    }

    public Subject(String subjectId, String subjectName) {
        this(subjectId, subjectName, 0);
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

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }
    
    public Object getAdditionalProperty(String key) {
        return additionalProperties.get(key);
    }

    public void setAdditionalProperty(String key, Object value) {
        this.additionalProperties.put(key, value);
    }
}