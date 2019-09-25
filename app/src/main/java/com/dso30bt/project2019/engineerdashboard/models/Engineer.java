package com.dso30bt.project2019.engineerdashboard.models;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Joesta on 2019/09/14.
 */

@Data
@ToString
@NoArgsConstructor
public class Engineer extends Person implements Serializable {
    private String imageUrl;
    private List<User> userList;
    private List<Constructor> constructorList;
    private List<UserReport> userReportList;



    public Engineer(String imageUrl, List<User> userList, List<Constructor> constructorList, List<UserReport> userReportList) {
        this.imageUrl = imageUrl;
        this.userList = userList;
        this.constructorList = constructorList;
        this.userReportList = userReportList;
    }

    public Engineer(int userId, String firstName, String lastName, String gender, String idNumber, String dob, String password, String emailAddress, Role role, String cellNumber, String imageUrl, List<User> userList, List<Constructor> constructorList, List<UserReport> userReportList) {
        super(userId, firstName, lastName, gender, idNumber, dob, password, emailAddress, role, cellNumber);
        this.imageUrl = imageUrl;
        this.userList = userList;
        this.constructorList = constructorList;
        this.userReportList = userReportList;
    }
}
