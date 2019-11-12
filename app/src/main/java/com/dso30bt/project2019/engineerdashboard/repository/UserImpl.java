package com.dso30bt.project2019.engineerdashboard.repository;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.activities.MainActivity;
import com.dso30bt.project2019.engineerdashboard.interfaces.IFirebase;
import com.dso30bt.project2019.engineerdashboard.models.Constructor;
import com.dso30bt.project2019.engineerdashboard.models.Engineer;
import com.dso30bt.project2019.engineerdashboard.models.LoginModel;
import com.dso30bt.project2019.engineerdashboard.models.Person;
import com.dso30bt.project2019.engineerdashboard.models.Report;
import com.dso30bt.project2019.engineerdashboard.models.Role;
import com.dso30bt.project2019.engineerdashboard.models.User;
import com.dso30bt.project2019.engineerdashboard.utils.Constants;
import com.dso30bt.project2019.engineerdashboard.utils.NavUtil;
import com.dso30bt.project2019.engineerdashboard.utils.SharedPreferenceManager;
import com.dso30bt.project2019.engineerdashboard.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.mateware.snacky.Snacky;

/**
 * Created by Joesta on 2019/09/12.
 */
public class UserImpl implements IUserRepository {

    private static final String ENGINEER_COLLECTION = "ENGINEER";
    private static final String TAG = "UserImpl";

    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //pdf converter document
    private Document pdfDocument;

    public UserImpl(Context context) {
        this.context = context;
        Log.i(TAG, "UserImpl: Called by " + context.toString());
    }

    @Override
    public void loginUserByEmail(LoginModel loginModel) {
        db.collection(ENGINEER_COLLECTION)
                .document(loginModel.getEmailAddress())
                .get()
                .addOnCompleteListener(((Activity) context), task -> {
                    if (task.isComplete()) {
                        // check for credentials match
                        DocumentSnapshot snapshot = task.getResult();
                        Engineer engineer = snapshot.toObject(Engineer.class);
                        if (loginModel.getPassword().equals(engineer.getPassword())) {
                            // redirect to welcome screen.
                            SharedPreferenceManager.saveEmail(context, loginModel.getEmailAddress(), 101);
                            NavUtil.moveToNextActivity(context, MainActivity.class);
                            ((Activity) context).finish();
                        } else {
                            showSnackBar("Username or password incorrect", true);
                        }
                    } else {
                        String localizedMessage = task.getException().getLocalizedMessage();
                        Log.e(TAG, "loginUserByEmail: " + localizedMessage);
                        Utils.showToast(context, localizedMessage);
                    }
                });
    }

    private void showSnackBar(String msg, boolean error) {
        Snacky.Builder builder = Snacky.builder();
        builder.setActivity(((Activity) context));
        builder.setText(msg);
        builder.setDuration(Snacky.LENGTH_LONG);

        if (error) {
            builder.error().show();
        } else {
            builder.info().show();
        }
    }

    @Override
    public void deleteUserOrConstructor(Person person) {
        String role = getUserOrConstructorRole(person);

        if (null != person) {
            db.collection(Constants.USER_COLLECTION)
                    .document(person.getEmailAddress())
                    .delete()
                    .addOnCompleteListener(((Activity) context), task -> {
                        if (task.isComplete()) {
                            Log.i(TAG, "deleteUserOrConstructor: user deleted");
                            Utils.showToast(context.getApplicationContext(), "User deleted.");
                        } else {
                            Log.i(TAG, "deleteUserOrConstructor: failed to delete user");
                            Utils.showToast(context.getApplicationContext(), "Failed to delete user! Please try again.");
                        }
                    });
        }
    }

    private String getUserOrConstructorRole(Person person) {
        return person.getRole().getRoleDescription();
    }

    @Override
    public void registerEngineer(final Engineer engineer) {

        getEngineers(engineers -> {
            if (engineers.isEmpty()) {
                addEngineer(engineer);
                return;
            }

            int count = 0;
            Engineer targetEngineer = null;
            for (Engineer e : engineers) {
                if (e.equals(engineer)) {
                    targetEngineer = e;
                    count++;
                }
            }

            if (count > 0) {
                if (targetEngineer.getIdNumber().equals(engineer.getIdNumber())) {
                    showToast("ID Number \"" + engineer.getIdNumber() + "\" already exist");
                } else if (targetEngineer.getEmailAddress().equalsIgnoreCase(engineer.getEmailAddress())) {
                    showToast("Email address \"" + engineer.getEmailAddress() + "\" already exist.");
                } else if (targetEngineer.getCellNumber().equals(engineer.getCellNumber())) {
                    showToast("Cell number \"" + engineer.getCellNumber() + "\"already exist");
                } else {
                    showToast("User already exist");
                }

            } else {
                addEngineer(engineer);
            }

        });
    }

    private void showToast(String msg) {
        Utils.showToast(context.getApplicationContext(), msg);
    }

    private void addEngineer(final Engineer engineer) {
        db.collection(ENGINEER_COLLECTION)
                .document(engineer.getEmailAddress())
                .set(engineer)
                .addOnCompleteListener(((Activity) context), task -> {
                    if (!task.isComplete()) {
                        Utils.showToast(context.getApplicationContext(), "Failed to add Engineer! Please try again later!");
                        Log.d(TAG, "registerEngineer: failed to add engineer");
                        return;
                    }

                    Utils.showToast(context.getApplicationContext(), "Engineer is added!");
                    Log.d(TAG, "registerEngineer: engineer added successfully");
                });
    }

    @Override
    public void generateReport(List<Report> reportList) {

        Log.i(TAG, "generateReport: generating report");
        String dateGenerated = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());

        File folder = getReportFile();
        if (!folder.exists()) {
            Log.i(TAG, "generateReport: no report directory found. Creating one...");
            folder.mkdirs();
        }

        File pdfFile = new File(folder, "Report-" + dateGenerated + ".pdf");

        pdfDocument = new Document(PageSize.A4);

        // document settings
        pdfDocument.addCreationDate();
        pdfDocument.addAuthor(context.getResources().getString(R.string.app_name));
        pdfDocument.addCreator("Joesta");

        // line separator
        String line_separator = System.lineSeparator();

        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFile));
            pdfDocument.open();

            pdfDocument.addTitle("Report");

            // pdfDocument.add(new Paragraph("Thi is my report"));
            Paragraph p = new Paragraph();
            int count = 1;

            for (Report report : reportList) {
                p.add("Report # " + count++);
                p.add(line_separator);
                p.add("Description\t:  " + report.getPothole().getDescription());
                p.add(line_separator);
                p.add("Date reported\t:  " + report.getReportDate());
                p.add(line_separator);
                p.add("Reported By\t:  " + report.getReportedBy());
                p.add(line_separator);

                if (report.getConstructor() == null) {
                    p.add("Assigned to  : N/A");
                } else {
                    p.add("Assigned to\t: " + report.getConstructor().getFirstName() + " " + report.getConstructor().getLastName());
                }

                p.add(line_separator);
                p.add("Coordinates\t:  Lat  [" + report.getPothole().getCoordinates().getLatitude() + "] Lng [" + report.getPothole().getCoordinates().getLongitude() + "]");
                p.add(line_separator);
                p.add(line_separator);
            }

            p.add("Total Reports: " + reportList.size());
            p.add(line_separator);
            p.add("==================================END==================================");
            pdfDocument.add(p);

            p.clear();

            pdfDocument.close();
            pdfWriter.close();
            Utils.showToast(context, "Report generated successfully!");
            Log.i(TAG, "generateReport: report generated");

        } catch (DocumentException e) {
            Utils.showToast(context, "Error: " + e.getLocalizedMessage());
            Log.e(TAG, "generateReport: failed to generate report", e);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Utils.showToast(context, e.getLocalizedMessage());
            Log.e(TAG, "generateReport: File not found", e);
            e.printStackTrace();
        }
    }

    private File getReportFile() {
        return new File(Environment.getExternalStorageDirectory(), "Reports");
    }

    @Override
    public void assignReport(Report report, Constructor constructor) {
        String documentId = SharedPreferenceManager.getEmail(context);
        getEngineerFullNames(names -> {
            if ((!TextUtils.isEmpty(names)) && (TextUtils.getTrimmedLength(names) != 0)) {
                String emailAddress = constructor.getEmailAddress();
                report.setAssignedBy(names);
                report.setConstructor(constructor);

                db.collection(Constants.USER_COLLECTION)
                        .document(emailAddress)
                        .update("reportList", FieldValue.arrayUnion(report))
                        .addOnCompleteListener(((Activity) context), task -> {
                            if (!task.isComplete()) {
                                Log.d(TAG, "assignReport: failed to update report!");
                                Utils.showToast(context, "An error occurred while trying to assign report. Please try again.");
                                return;
                            }

                            updateReport(report);
                        });
            }

        }, documentId);
    }

    @Override
    public void getReports(IFirebase.Reports reportsCallback) {

        db.collection(Constants.REPORT_COLLECTION)
                .get()
                .addOnCompleteListener(((Activity) context), task -> {
                    if (!task.isComplete()) {
                        Utils.showToast(context, "An error occurred while fetching reports. Please try again.");
                        return;
                    }

                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> documents = result.getDocuments();

                    if (documents.isEmpty()) {
                        reportsCallback.onFetchedReports(new ArrayList<>());
                        Log.i(TAG, "getReports: No reports found!");
                        return;
                    }

                    final List<Report> reportList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : documents) {
                        Report report = snapshot.toObject(Report.class);
                        reportList.add(report);
                    }

                    reportsCallback.onFetchedReports(reportList);
                });
    }

    @Override
    public void updateReport(Report report) {
        getReports(reportList -> {
            for (Report currentReport : reportList) {
                if (!currentReport.equals(report)) {
                    getDocumentReferenceId(document -> {
                        db.collection(Constants.REPORT_COLLECTION)
                                .document(document)
                                .set(report)
                                .addOnCompleteListener(((Activity) context), task -> {
                                    if (!task.isComplete()) {
                                        Utils.showToast(context, "Failed to contact server. Please try again.");
                                        Log.i(TAG, "updateReport: Failed contact server. Please try again");
                                        return;
                                    }

                                    Utils.showToast(context, "Report updated successfully.");
                                    Log.i(TAG, "updateReport: document updated.");
                                });
                    }, report.getReportId());
                }
            }
        });

    }

    @Override
    public void getDocumentReferenceId(final IFirebase.ReportDocumentRef reportDocumentCallback, int reportId) {

        db.collection(Constants.REPORT_COLLECTION)
                .get()
                .addOnCompleteListener(((Activity) context), querySnapshotTask -> {
                    if (!querySnapshotTask.isComplete()) {
                        Utils.showToast(context, "An error occurred while contacting server. Please try again.");
                    }

                    QuerySnapshot result = querySnapshotTask.getResult();
                    Query query = result.getQuery();
                    Task<QuerySnapshot> snapshotTask = query.whereEqualTo("reportId", reportId).get();

                    snapshotTask.addOnCompleteListener(((Activity) context), task -> {
                        if (!task.isComplete()) {
                            Utils.showToast(context, "Failed to contact server.");
                            Log.i(TAG, "getDocumentReferenceId: Failed to contact server.");
                            return;
                        }

                        QuerySnapshot _result = task.getResult();
                        List<DocumentSnapshot> documents = _result.getDocuments();
                        if (documents.isEmpty()) {
                            Utils.showToast(context, "No report records found.");
                            Log.i(TAG, "getDocumentReferenceId: No reports record found");
                            return;
                        }

                        // get result for the first document
                        // we get from the first document because we already know that
                        // we queried a report by its own unique id
                        DocumentSnapshot snapshot = _result.getDocuments().get(0);
                        String documentId = snapshot.getReference().getId();
                        reportDocumentCallback.onFetchedDocumentId(documentId);

                    });

                });
    }

    @Override
    public void getRoadUsers(IFirebase.RoadUsers usersCallback) {
        db.collection(Constants.USER_COLLECTION)
                .addSnapshotListener(((Activity) context), ((querySnapshot, error) -> {
                    if (error != null) {
                        Utils.showToast(context, "An error occurred while fetching users. Please try again..");
                        return;
                    }

                    final List<User> userList = new ArrayList<>();

                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    for (DocumentSnapshot snapshot : documents) {
                        Role role = snapshot.get("role", Role.class);
                        String roleType = getRoleType(role);
                        if (roleType.equals("User")) {
                            User user = snapshot.toObject(User.class);
                            userList.add(user);
                        }
                    }
                    usersCallback.onFetchedUsers(userList);
                }));
    }

    @Override
    public void getConstructors(IFirebase.Constructors constructorsCallback) {
        db.collection(Constants.USER_COLLECTION)
                .addSnapshotListener(((Activity) context), (querySnapshot, error) -> {
                    if (error != null) {
                        Utils.showToast(context, "An error occurred while fetching constructors. Please try again");
                        return;
                    }

                    final List<Constructor> constructorList = new ArrayList<>();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                    for (DocumentSnapshot snapshot : documents) {
                        Role role = snapshot.get("role", Role.class);
                        String roleType = getRoleType(role);
                        if (roleType.equals("Constructor")) {
                            Constructor constructor = snapshot.toObject(Constructor.class);
                            constructorList.add(constructor);
                        }
                    }
                    constructorsCallback.onFetchedConstructors(constructorList);
                });
    }

    private String getRoleType(Role role) {
        return role.getRoleDescription();
    }

    @Override
    public void getAllUsers(IFirebase.Users usersCallback) {
        getRoadUsers(userList -> {
            getConstructors(constructorsList -> {
                if (userList.isEmpty() && constructorsList.isEmpty()) {
                    Utils.showToast(context, "Users not found!");
                    return;
                }

                List<Person> people = new ArrayList<>();
                people.addAll(userList);
                people.addAll(constructorsList);

                usersCallback.onFetchedUsers(people);
            });
        });
    }

    @Override
    public void getEngineers(IFirebase.Engineers engineersCallback) {
        db.collection(Constants.ENGINEER_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isComplete()) {
                        Utils.showToast(context, "An error occurred while fetching engineers. Please try again.");
                        return;
                    }

                    QuerySnapshot result = task.getResult();
                    if (result.isEmpty()) {
                        engineersCallback.onFetchedEngineers(new ArrayList<>());
                        return;
                    }

                    final List<Engineer> engineerList = new ArrayList<>();
                    List<DocumentSnapshot> documents = result.getDocuments();

                    for (DocumentSnapshot snapshot : documents) {
                        Engineer engineer = snapshot.toObject(Engineer.class);
                        engineerList.add(engineer);
                    }

                    engineersCallback.onFetchedEngineers(engineerList);
                });
    }

    @Override
    public void deleteAllReports() {
        db.collection(Constants.REPORT_COLLECTION)
                .document()
                .delete()
                .addOnCompleteListener(((Activity) context), task -> {
                    if (task.isComplete()) {
                        Log.i(TAG, "deleteAllReports: reports deleted");
                    } else {
                        Log.e(TAG, "deleteAllReports: failed to delete reports", task.getException());
                    }
                });
    }

    private void getEngineerFullNames(IFirebase.EngineerDetails callback, String engineerDocumentId) {
        db.collection(ENGINEER_COLLECTION)
                .document(engineerDocumentId)
                .get()
                .addOnCompleteListener(((Activity) context), task -> {
                    if (task.isComplete()) {
                        Log.d(TAG, "getEngineerFullNames: task completed.");
                        DocumentSnapshot snapshot = task.getResult();
                        assert snapshot != null;
                        if (snapshot.exists()) {
                            final String namesDetails = snapshot.get("firstName").toString() + " " + snapshot.get("lastName").toString();
                            callback.onFetchedEngineerDetails(namesDetails);
                        } else {
                            Log.d(TAG, "getEngineerFullNames: engineer does not exist");
                        }
                    } else {
                        Log.d(TAG, "getEngineerFullNames: failed to connect to server!");
                    }
                });
    }
}