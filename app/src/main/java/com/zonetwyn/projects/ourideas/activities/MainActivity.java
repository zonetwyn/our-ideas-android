package com.zonetwyn.projects.ourideas.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.R;
import com.zonetwyn.projects.ourideas.database.SessionManager;
import com.zonetwyn.projects.ourideas.fragments.HelpFragment;
import com.zonetwyn.projects.ourideas.fragments.HomeFragment;
import com.zonetwyn.projects.ourideas.fragments.NewIdeaFragment;
import com.zonetwyn.projects.ourideas.fragments.NewSubjectFragment;
import com.zonetwyn.projects.ourideas.fragments.SubjectsFragment;
import com.zonetwyn.projects.ourideas.models.Subject;
import com.zonetwyn.projects.ourideas.payloads.ApiResponse;
import com.zonetwyn.projects.ourideas.payloads.IdeaData;
import com.zonetwyn.projects.ourideas.payloads.IdeaRequest;
import com.zonetwyn.projects.ourideas.payloads.SubjectRequest;
import com.zonetwyn.projects.ourideas.utils.Event;
import com.zonetwyn.projects.ourideas.utils.EventBus;

import io.reactivex.functions.Consumer;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    
    // setting up bottom navigation
    private FrameLayout homeFrame;
    private FrameLayout subjectsFrame;
    private FrameLayout helpFrame;

    private ImageView homeIcon;
    private ImageView subjectsIcon;
    private ImageView helpIcon;

    private int currentMenu = 1;

    private ProgressDialog progressDialog;

    private Dialog actionsDialog;
    private String[] actions;

    private SessionManager sessionManager;

    private static  String keyMenu = "Menu";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/poppins/Poppins-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        sessionManager = SessionManager.getInstance(getApplicationContext());

        // view model
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.name);
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/poppins/Poppins-Bold.otf"));

        homeFrame = findViewById(R.id.homeFrame);
        subjectsFrame = findViewById(R.id.subjectsFrame);
        helpFrame = findViewById(R.id.helpFrame);

        homeIcon = findViewById(R.id.homeIcon);
        subjectsIcon = findViewById(R.id.subjectsIcon);
        helpIcon = findViewById(R.id.helpIcon);

        setupMenuListeners();
        subscribeToBus();
        initProgressDialog();

        // view model binding
        viewModel.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    if (aBoolean) {
                        progressDialog.show();
                    } else {
                        progressDialog.dismiss();
                    }
                }
            }
        });

        viewModel.getSubjectResponse().observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(@Nullable ApiResponse apiResponse) {
                if (apiResponse != null) {
                    if (apiResponse.getError() != null) {
                        showToast(apiResponse.getError());
                    } else if (apiResponse.getMessage() != null) {
                        showToast(apiResponse.getMessage());
                        currentMenu = 2;
                        viewModel.setCurrentMenu(currentMenu);
                    }
                }
            }
        });

        viewModel.getIdeaResponse().observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(@Nullable ApiResponse apiResponse) {
                if (apiResponse != null) {
                    if (apiResponse.getError() != null) {
                        showToast(apiResponse.getError());
                    } else if (apiResponse.getMessage() != null) {
                        showToast(apiResponse.getMessage());
                        // open subject activity
                        Subject subject = new Gson().fromJson(apiResponse.getData(), Subject.class);
                        if (subject != null) {
                            Event event = new Event(Event.SUBJECT_HOME_UPDATE_COUNT, new Gson().toJson(subject));
                            EventBus.publish(EventBus.SUBJECT_HOME_FRAGMENT, event);

                            // open activity
                            Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
                            intent.putExtra(SubjectActivity.keySubject, subject);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        viewModel.getCurrentMenu().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    currentMenu = integer;
                    menuItemSelected(integer);
                }
            }
        });

        // init menu
        viewModel.setCurrentMenu(currentMenu);
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void subscribeToBus() {
        EventBus.subscribe(EventBus.SUBJECT_MAIN_ACTIVITY, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof Event) {
                    Event event = (Event) o;
                    if (event.getData() != null && event.getSubject() != 0) {
                        switch (event.getSubject()) {
                            case Event.SUBJECT_MAIN_NEW_SUBJECT:
                                showNewSubjectFragment();
                                break;
                            case Event.SUBJECT_MAIN_SAVE_SUBJECT:
                                if (event.getData() instanceof String) {
                                    String json = (String) event.getData();
                                    SubjectRequest request = new Gson().fromJson(json, SubjectRequest.class);
                                    viewModel.newSubject(MainActivity.this, request);
                                }
                                break;
                            case Event.SUBJECT_MAIN_NEW_IDEA:
                                if (event.getData() instanceof String) {
                                    String json = (String) event.getData();
                                    Subject subject = new Gson().fromJson(json, Subject.class);
                                    showNewIdeaFragment(subject);
                                }
                                break;
                            case Event.SUBJECT_MAIN_SAVE_IDEA:
                                if (event.getData() instanceof String) {
                                    String json = (String) event.getData();
                                    IdeaData ideaData = new Gson().fromJson(json, IdeaData.class);
                                    viewModel.newIdea(MainActivity.this, ideaData.getRequest(), ideaData.getSubject());
                                }
                                break;
                            case Event.SUBJECT_MAIN_ACCOUNT:
                                break;
                        }
                    }
                }
            }
        });
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
    }

    private void showActionsDialog() {
        String label;
        if (sessionManager.isLoggedIn()) {
            actions = new String[]{getString(R.string.logout)};
            label = getString(R.string.greeting) + " " + sessionManager.getUsername();
        } else {
            actions = new String[]{getString(R.string.sign_in), getString(R.string.sign_up)};
            label = getString(R.string.what_do_you_want);
        }

        // init dialog
        actionsDialog = new Dialog(this);
        actionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionsDialog.setContentView(R.layout.action_dialog);
        Window window = actionsDialog.getWindow();
        if (window != null) {
            window.setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        }

        TextView actionLabel = actionsDialog.findViewById(R.id.actionLabel);
        actionLabel.setText(label);
        ListView listView = actionsDialog.findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.action_item, R.id.action, actions);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actionsDialog.dismiss();
                String action = actions[position];
                if (action.equals(getString(R.string.sign_in))) {
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                } else if (action.equals(getString(R.string.sign_up))) {
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                } else if (action.equals(getString(R.string.logout))) {
                    sessionManager.logout();
                }
            }
        });

        actionsDialog.show();
    }

    private void showNewSubjectFragment() {
        NewSubjectFragment fragment = new NewSubjectFragment();
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    private void showNewIdeaFragment(Subject subject) {
        NewIdeaFragment fragment = new NewIdeaFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NewIdeaFragment.keySubject, subject);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    private void  setupMenuListeners() {
        homeFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMenu != 1) {
                    viewModel.setCurrentMenu(1);
                }
            }
        });

        subjectsFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMenu != 2) {
                    viewModel.setCurrentMenu(2);
                }
            }
        });

        helpFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMenu != 3) {
                    viewModel.setCurrentMenu(3);
                }
            }
        });
    }

    private void menuItemSelected(int currentMenu) {
        switch (currentMenu) {
            case 1:
                applyMenuSelected(currentMenu);
                break;
            case 2:
                applyMenuSelected(currentMenu);
                break;
            case 3:
                applyMenuSelected(currentMenu);
                break;
        }
    }

    private void applyMenuSelected(int currentMenu) {
        Fragment fragment = null;
        switch (currentMenu) {
            case 1:
                fragment = new HomeFragment();
                subjectsIcon.setImageResource(R.drawable.ic_list_black);
                helpIcon.setImageResource(R.drawable.ic_help_black);
                homeIcon.setImageResource(R.drawable.ic_home_green);
                break;
            case 2:
                homeIcon.setImageResource(R.drawable.ic_home_black);
                helpIcon.setImageResource(R.drawable.ic_help_black);
                subjectsIcon.setImageResource(R.drawable.ic_list_green);
                fragment = new SubjectsFragment();
                break;
            case 3:
                homeIcon.setImageResource(R.drawable.ic_home_black);
                subjectsIcon.setImageResource(R.drawable.ic_list_black);
                helpIcon.setImageResource(R.drawable.ic_help_green);
                fragment = new HelpFragment();
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().popBackStack();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();

            this.currentMenu = currentMenu;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_actions) {
            showActionsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(keyMenu, currentMenu);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int menu = savedInstanceState.getInt(keyMenu);
        viewModel.setCurrentMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.unregister(this);
    }
}
