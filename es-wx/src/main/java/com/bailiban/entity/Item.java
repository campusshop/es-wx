package com.bailiban.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhulang
 * @Classname Item
 * @Description TODO
 * @Date 2020/5/12 18:04
 */
@Data
public class Item implements Serializable {
    /**
     * 主键id
     */
    @Id
    private Integer id;
    private String department;
    private String platform;
    private String author;
    private String oaName;
    private String article_type;
    private String isOriginal;
    private String title;
    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date publishTime;
    private Integer viewsCount;
    private Integer shareCount;
    private Integer starCount;
    private String statisticians;
}