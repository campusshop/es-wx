package com.bailiban.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhulang
 * @Classname Item
 * @Description TODO
 * @Date 2020/5/12 18:04
 */
@Data
@Entity
@Table(name = "tab_wx")
public class Wx implements Serializable {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wx_id")
    private Integer id;
    private String department;
    private String platform;
    private String author;
    @Column(name = "oa_name")
    private String oaName;
    @Column(name = "article_type")
    private String article_type;
    @Column(name = "is_original")
    private String isOriginal;
    private String title;
    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "publish_time")
    private Date publishTime;
    @Column(name = "views_count")
    private Integer viewsCount;
    @Column(name = "share_count")
    private Integer shareCount;
    @Column(name = "star_count")
    private Integer starCount;
    private String statisticians;
}