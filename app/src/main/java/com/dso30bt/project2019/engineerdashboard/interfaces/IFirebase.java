package com.dso30bt.project2019.engineerdashboard.interfaces;

import com.dso30bt.project2019.engineerdashboard.models.Constructor;
import com.dso30bt.project2019.engineerdashboard.models.Engineer;
import com.dso30bt.project2019.engineerdashboard.models.Report;
import com.dso30bt.project2019.engineerdashboard.models.User;

import java.util.List;

/**
 * Created by Joesta on 2019/09/14.
 */
public interface IFirebase {
    interface EngineerDetails {
        void onFetchedEngineerDetails(String details);
    }

    interface Reports {
        void onFetchedReports(List<Report> reportList);
    }

    interface Users {
        void onFetchedUsers(List<User> userList);
    }

    interface Constructors {
        void onFetchedConstructors(List<Constructor> constructorsList);
    }

    interface Engineers {
        void onFetchedEngineers(List<Engineer> engineersList);
    }
}
