package com.dso30bt.project2019.engineerdashboard;

import android.content.Context;

import com.dso30bt.project2019.engineerdashboard.interfaces.IFirebase;
import com.dso30bt.project2019.engineerdashboard.models.Report;
import com.dso30bt.project2019.engineerdashboard.repository.UserImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by Joesta on 2019/09/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class ReportsTest {
    @Mock
    private UserImpl userImpl;

    @Mock
    private Context context;

    @Mock
    private List<Report> list;

    @Before
    public void initMocks() {
        context = mock(Context.class);
        userImpl = new UserImpl(context);
        list = new ArrayList<>();
    }

    @Test
    public void test_reports() {
        userImpl.getReports(reportList -> {
            list =  reportList;
        });

        assertThat("Report list has at least more than on reports", list.size() > 0);
    }
}
