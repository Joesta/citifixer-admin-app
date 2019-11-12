package com.dso30bt.project2019.engineerdashboard.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Joesta on 2019/05/31.
 */

@Data
@AllArgsConstructor
public class LoginModel {
    private String emailAddress;
    private String password;
}
