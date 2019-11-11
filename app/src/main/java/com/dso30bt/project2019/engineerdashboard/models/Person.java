package com.dso30bt.project2019.engineerdashboard.models;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Joesta on 2019/08/02.
 */
@Data
@ToString
public class Person implements Serializable {
    private int userId;
    private String firstName;
    private String lastName;
    private String gender;
    private String idNumber;
    private String dob;
    private String password;
    private String emailAddress;
    private String cellNumber;
    private Role role;

    public Person() {
        super();
    }

    public Person(int userId, String firstName, String lastName, String gender, String idNumber, String dob, String password, String emailAddress, Role role, String cellNumber) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.idNumber = idNumber;
        this.dob = dob;
        this.password = password;
        this.emailAddress = emailAddress;
        this.role = role;
        this.cellNumber = cellNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            if (this.idNumber.equals(((Person) obj).getIdNumber())
                    || this.emailAddress.equals(((Person) obj).getEmailAddress())
                    || this.cellNumber.equals(((Person) obj).getCellNumber())) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}
