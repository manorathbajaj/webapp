package com.manorath.csye6225.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Embeddable
public class BillAttachment {

    @Column(name = "attachment_id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Getter
    private String id;


    @Column(name = "file_name")
    @Getter
    @Setter
    private String fileName;

    @Column(name = "url")
    @Getter
    @Setter
    private String url;

    @Column(name = "upload_date")
    @Getter
    @Setter
    private Date uploadDate;
}
