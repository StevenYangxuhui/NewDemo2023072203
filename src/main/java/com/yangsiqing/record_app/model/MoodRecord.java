package com.yangsiqing.record_app.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

// 对应数据库的 mood_record 表（表名和类名不一致时必须指定）
@TableName("mood_record")
public class MoodRecord {
    // 主键 ID，自增（和数据库表的 id 字段对应）
    @TableId(type = IdType.AUTO)
    private Long id;
    // 发生的事（对应 happen_thing 字段，MP 会自动驼峰转下划线）
    private String happenThing;
    // 心情（对应 mood 字段）
    private String mood;
    // 对方形象（对应 partner_image 字段）
    private String partnerImage;
    // 提交时间（对应 create_time 字段）
    private LocalDateTime createTime;
    // 提交者：（对应 submitter 字段）
    private String submitter;

    // 无参构造器（MP 必须）
    public MoodRecord() {
    }

    // 有参构造器（方便创建对象）
    public MoodRecord(String happenThing, String mood, String partnerImage, String submitter) {
        this.happenThing = happenThing;
        this.mood = mood;
        this.partnerImage = partnerImage;
        this.submitter = submitter;
    }

    // 生成 Getter/Setter（必须，否则 MP 无法赋值）
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHappenThing() {
        return happenThing;
    }

    public void setHappenThing(String happenThing) {
        this.happenThing = happenThing;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getPartnerImage() {
        return partnerImage;
    }

    public void setPartnerImage(String partnerImage) {
        this.partnerImage = partnerImage;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }
}