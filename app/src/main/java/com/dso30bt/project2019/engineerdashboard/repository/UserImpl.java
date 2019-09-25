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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OnlineState;
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

/**
 * Created by Joesta on 2019/09/12.
 */
public class UserImpl implements IUserRepository {

    private static final String ENGINEER_COLLECTION = "engineers";
    private static final String TAG = "UserImpl";

    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //pdf converter document
    private Document pdfDocument;

    public UserImpl(Context context) {
        this.context = context;
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
                        }
                    } else {
                        String localizedMessage = task.getException().getLocalizedMessage();
                        Log.e(TAG, "loginUserByEmail: " + localizedMessage);
                        Utils.showToast(context, localizedMessage);
                    }
                });
    }

    @Override
    public void deleteUserOrConstructor(Person person) {
        String role = getUserOrConstructorRole(person);
        String collection = getUserOrConstructorCollectionByRole(role);

        if (null != person) {
            db.collection(collection)
                    .document(person.getEmailAddress())
                    .delete()
                    .addOnCompleteListener(((Activity) context), task -> {
                        if (task.isComplete()) {
                            Log.i(TAG, "deleteUserOrConstructor: user deleted");
                        } else {
                            Log.i(TAG, "deleteUserOrConstructor: failed to delete user");
                        }
                    });
        }
    }

    private String getUserOrConstructorCollectionByRole(String role) {
        return role.equals("User") ? Constants.USER_COLLECTION : Constants.CONSTRUCTOR_COLLECTION;
    }

    private String getUserOrConstructorRole(Person person) {
        return person.getRole().getRoleDescription();
    }

    @Override
    public void addEngineer(final Person person) {
        if (null != person) {
            getEngineers(engineersList -> {

                int count = 0;
                for (Engineer e : engineersList) {
                    if (e.getEmailAddress().equalsIgnoreCase(person.getEmailAddress())
                            || e.getIdNumber().equals(person.getIdNumber())) {
                        count++;
                    }
                }

                if (count > 0) {
                    Utils.showToast(context, "Engineer already exist!");
                    return;
                }

                db.collection(ENGINEER_COLLECTION)
                        .document(person.getEmailAddress())
                        .set(person)
                        .addOnCompleteListener(((Activity) context), task -> {
                            if (task.isComplete()) {
                                Utils.showToast(context, "Engineer is added!");
                                Log.d(TAG, "addEngineer: engineer added successfully");
                            } else {
                                Utils.showToast(context, "Failed to add Engineer! Please try again later!");
                                Log.d(TAG, "addEngineer: failed to add engineer");
                            }
                        });
            });
        }
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

                db.collection("constructors")
                        .document(emailAddress)
                        .update("reportList", FieldValue.arrayUnion(report))
                        .addOnCompleteListener(((Activity) context), task -> {
                            if (!task.isComplete()) {
                                Log.d(TAG, "assignReport: failed to update report!");
                                return;
                            }

                            Log.i(TAG, "assignReport: report updated successfully");
                        });
            }

        }, documentId);
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

    @Override
    public void getReports(IFirebase.Reports reportsCallback) {
        db.collection(Constants.REPORT_COLLECTION)
                .get()
                .addOnCompleteListener(((Activity) context), task -> {
                    if (!task.isComplete()) {
                        Log.e(TAG, "getReports: failed to get reports", task.getException());
                        return;
                    }

                    QuerySnapshot querySnapshot = task.getResult();
                    assert querySnapshot != null;
                    if ((querySnapshot.isEmpty()) && querySnapshot.size() == 0) {
                        Utils.showToast(context, "No reports found!");
                        Log.e(TAG, "getReports: Reports not found ", task.getException());
                        return;
                    }

                    final List<Report> reportList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : querySnapshot) {
                        final Report report = snapshot.toObject(Report.class);
                        reportList.add(report);
                    }

                    reportsCallback.onFetchedReports(reportList);
                });
    }

    @Override
    public void getUsers(IFirebase.Users usersCallback) {
        db.collection(Constants.USER_COLLECTION)
                .get()
                .addOnCompleteListener(((Activity) context), task -> {
                    if (task.isComplete()) {
                        final List<User> userList = new ArrayList<>();

                        QuerySnapshot result = task.getResult();
                        for (DocumentSnapshot snapshot : result) {
                            User user = snapshot.toObject(User.class);
                            userList.add(user);
                        }

                        usersCallback.onFetchedUsers(userList);
                    }
                });
    }

    @Override
    public void getConstructors(IFirebase.Constructors constructorsCallback) {
        db.collection(Constants.CONSTRUCTOR_COLLECTION)
                .get()
                .addOnCompleteListener(((Activity) context), task -> {
                    if (task.isComplete()) {
                        final List<Constructor> constructorList = new ArrayList<>();

                        QuerySnapshot result = task.getResult();
                        for (DocumentSnapshot snapshot : result) {
                            Constructor constructor = snapshot.toObject(Constructor.class);
                            constructorList.add(constructor);
                        }

                        constructorsCallback.onFetchedConstructors(constructorList);
                    }
                });
    }

    @Override
    public void getEngineers(IFirebase.Engineers engineersCallback) {
        Log.i(TAG, "getEngineers: in method");
        db.collection(Constants.ENGINEER_COLLECTION)
                .get()
                .addOnCompleteListener(((Activity) context), task -> {
                    if (task.isComplete()) {
                        final List<Engineer> engineerList = new ArrayList<>();

                        QuerySnapshot result = task.getResult();
                        for (DocumentSnapshot snapshot : result) {
                            Engineer engineer = snapshot.toObject(Engineer.class);
                            engineerList.add(engineer);
                            String firstName = snapshot.get("firstName").toString();
                            Log.i(TAG, "getEngineers: " + firstName);
                        }

                        engineersCallback.onFetchedEngineers(engineerList);
                    }
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
}