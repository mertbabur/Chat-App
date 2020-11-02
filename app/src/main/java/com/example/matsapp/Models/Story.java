package com.example.matsapp.Models;

public class Story {

    private String storyId, storyKey, userPhone, storyTime;
    private Long timeEnd, timeStart;

    public Story() {
    }

    public Story(String storyId, String storyKey, String userPhone, String storyTime, Long timeEnd, Long timeStart) {
        this.storyId = storyId;
        this.storyKey = storyKey;
        this.userPhone = userPhone;
        this.storyTime = storyTime;
        this.timeEnd = timeEnd;
        this.timeStart = timeStart;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getStoryKey() {
        return storyKey;
    }

    public void setStoryKey(String storyKey) {
        this.storyKey = storyKey;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getStoryTime() {
        return storyTime;
    }

    public void setStoryTime(String storyTime) {
        this.storyTime = storyTime;
    }

    public Long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Long timeStart) {
        this.timeStart = timeStart;
    }
}
