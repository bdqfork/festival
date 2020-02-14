package cn.bdqfork.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {

    int id;
    String username;
    boolean isActive;
    @JsonFormat(pattern = "YYYY")
    Date createDate;

}
