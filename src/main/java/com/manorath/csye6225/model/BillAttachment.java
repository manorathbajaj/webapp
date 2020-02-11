package com.manorath.csye6225.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Embeddable
@Getter
@Setter
public class BillAttachment {

    @Column(name = "attachment_id")
    private String id;


    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_url")
    private String url;

    @Column(name = "file_upload_date")
    private Date uploadDate;

    @JsonIgnore
    @Column(name = "file_size")
    private long attachmentSize;

    @JsonIgnore
    @Column(name = "file_md5_hash")
    private String md5Hash;

    @JsonIgnore
    @Column(name = "file_content_type")
    private String fileContentType;
}
