package com.dso30bt.project2019.engineerdashboard.interfaces;

import com.dso30bt.project2019.engineerdashboard.models.Constructor;
import com.dso30bt.project2019.engineerdashboard.models.Engineer;
import com.dso30bt.project2019.engineerdashboard.models.Person;
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

    interface ReportDocumentRef {
        void onFetchedDocumentId(String documentId);
    }

    interface RoadUsers {
        void onFetchedUsers(List<User> userList);
    }

    interface Users {
        void onFetchedUsers(List<Person> people);
    }

    interface Constructors {
        void onFetchedConstructors(List<Constructor> constructorsList);
    }

    interface Engineers {
        void onFetchedEngineers(List<Engineer> engineersList);
    }
}
