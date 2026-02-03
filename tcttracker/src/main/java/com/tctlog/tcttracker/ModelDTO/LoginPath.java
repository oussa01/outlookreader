package com.tctlog.tcttracker.ModelDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LoginPath {
    private String type ="logreq";
    private String loginUrlPath;
    private String GeneratedCode;
    private Date Expiration;
}
