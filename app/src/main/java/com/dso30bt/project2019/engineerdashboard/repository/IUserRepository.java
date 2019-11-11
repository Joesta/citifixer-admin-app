package com.dso30bt.project2019.engineerdashboard.repository;

import com.dso30bt.project2019.engineerdashboard.interfaces.IFirebase;
import com.dso30bt.project2019.engineerdashboard.models.Constructor;
import com.dso30bt.project2019.engineerdashboard.models.Engineer;
import com.dso30bt.project2019.engineerdashboard.models.LoginModel;
import com.dso30bt.project2019.engineerdashboard.models.Person;
import com.dso30bt.project2019.engineerdashboard.models.Report;

import java.util.List;

/**
 * Created by Joesta on 2019/09/12.
 */
public interface IUserRepository {

    void loginUserByEmail(LoginModel loginModel);

    void deleteUserOrConstructor(Person person);

    void registerEngineer(Engineer engineer);

    void generateReport(List<Report> reportList);

    void assignReport(Report report, Constructor constructor);

    void getReports(IFirebase.Reports reportsCallback);

    void updateReport(Report report);

    void getDocumentReferenceId(IFirebase.ReportDocumentRef reportDocumentCallback, int reportId);

    void getRoadUsers(IFirebase.RoadUsers usersCallback);

    void getAllUsers(IFirebase.Users usersCallback);

    void getConstructors(IFirebase.Constructors constructorsCallback);

    void getEngineers(IFirebase.Engineers engineersCallback);

    void deleteAllReports();
}
