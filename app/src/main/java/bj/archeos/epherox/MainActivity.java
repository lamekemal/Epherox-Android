package bj.archeos.epherox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import bj.archeos.epherox.model.EphxGPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    BottomAppBar bottomAppBar;
    TabLayout tabLayout;
    TinyDB tinydb;
    String tdDate; //today date format dd-mm
    String longDateFormat; //date at long format
    String tdMouth; //today date format dd-mm
    String longMouthFormat; //date at long format
    String DEVICE_ID;

    boolean isPremiumVersion = true;  //Variable for Apps Premium Version
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.fabHome);
        bottomAppBar = findViewById(R.id.bottomAppbar);
        setSupportActionBar(bottomAppBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color. colorAccent));
        }
        tdDate = new SimpleDateFormat("dd_MM", Locale.getDefault()).format(new Date());
        longDateFormat = new SimpleDateFormat(" E d MMM", Locale.FRENCH).format(new Date());
        tdMouth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        longMouthFormat = new SimpleDateFormat(" MMMM", Locale.FRENCH).format(new Date());
        tabLayout = findViewById(R.id.tabs);
        ImageButton imGamifyst = findViewById(R.id.buttonBadge);
        ProgressBar prGamifyst = findViewById(R.id.myprogressBar);

        imGamifyst.setOnClickListener(v -> {
            Intent i = new Intent(this, GamifyActivity.class);
            startActivity(i);
        });

        prGamifyst.setOnClickListener(v -> {
            Intent i = new Intent(this, GamifyActivity.class);
            startActivity(i);
        });

        //TabLayout listner for action
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    LoadEphxWithTab(getBaseContext(),(int) tab.getTag(),tdDate);
                }catch (NullPointerException Ex){
                    Log.i("LOAD_TAB_SECTION",Ex.getMessage());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Toast.makeText(getApplicationContext(),tab.getText(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                try {
                    LoadEphxWithTab(getBaseContext(),(int) tab.getTag(),tdDate);
                }catch (NullPointerException Ex){
                    Log.i("LOAD_TAB_SECTION",Ex.getMessage());
                }
            }
        });

        //Bottom appbar left Navigation
        bottomAppBar.setNavigationOnClickListener(v -> {
            BottomDrawerFragment bottomNawdw = new BottomDrawerFragment();
            bottomNawdw.show(getSupportFragmentManager(), "TAG");
        });

        //Reload apps home page
        floatingActionButton.setOnClickListener(view -> {
            tdDate = new SimpleDateFormat("dd_MM", Locale.getDefault()).format(new Date());
            longDateFormat = new SimpleDateFormat(" E d MMM", Locale.FRENCH).format(new Date());
            tabLayout.removeAllTabs();
            loadAllTab(getBaseContext(),tdDate);
        });

        //default action for apps on load
        webViewInt();
        setUpBottomAppBarShapeAppearance();
        loadAllTab(getBaseContext(),tdDate);
        Intent intent = getIntent();
        int fragmentOption = intent.getIntExtra("fragmentRequest",0);
        fragmentRequest(fragmentOption);

        //gamify helper
        tinydb = new TinyDB(this);
        DEVICE_ID = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        if (!tinydb.getBoolean("isTargetRViewed")) {
            tinydb.putBoolean("isRegistered",false);
            tinydb.putDate("gamify_saveddate", new Date(System.currentTimeMillis()));
            tinydb.putInt(getString(R.string.gamify_ui_var_days), 1);
            tinydb.putString("ui_gamify_userid", UUID.randomUUID().toString());
            tinydb.putInt(getString(R.string.gamify_account_TAG), 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                TapTargetView.showFor(this,                 // `this` is an Activity
                        TapTarget.forView(findViewById(R.id.buttonBadge), "Récompenses journalières", getString(R.string.tagethelpdesc))
                                // All options below are optional
                                .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.blanc)   // Specify a color for the target circle
                                .titleTextSize(35)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.blanc)      // Specify the color of the title text
                                .descriptionTextSize(22)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.blanc)  // Specify the color of the description text
                                .textColor(R.color.blanc)            // Specify a color for both the title and description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(R.color.material_on_background_disabled)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                .icon(AppCompatResources.getDrawable(this,R.drawable.outline_local_police_24))                     // Specify a custom drawable to draw as the target
                                .targetRadius(60),                  // Specify the target radius (in dp)
                        new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);      // This call is optional
                                tinydb.putBoolean("isTargetRViewed", true);
                            }
                        });
            }else{
                TapTargetView.showFor(this,                 // `this` is an Activity
                        TapTarget.forView(findViewById(R.id.buttonBadge), "Récompenses journalières", getString(R.string.tagethelpdesc))
                                // All options below are optional
                                .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.blanc)   // Specify a color for the target circle
                                .titleTextSize(35)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.blanc)      // Specify the color of the title text
                                .descriptionTextSize(22)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.blanc)  // Specify the color of the description text
                                .textColor(R.color.blanc)            // Specify a color for both the title and description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(R.color.material_on_background_disabled)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60),                  // Specify the target radius (in dp)
                        new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);      // This call is optional
                                //doSomething();
                                tinydb.putBoolean("isTargetRViewed", true);
                            }
                        });
            }
        }
        gamify_assitment(new Date(System.currentTimeMillis()));

        if(tinydb.getBoolean("is_login")){
            ProgressBar gamifyIndicator = findViewById(R.id.myprogressBar);
            gamifyIndicator.setIndeterminate(true);
            gamifyIndicator.setVisibility(View.VISIBLE);
            saveGamifyData(tinydb.getInt(getString(R.string.gamify_account_TAG)),
                    tinydb.getString("user_mail"),
                    tinydb.getDate("gamify_saveddate"),DEVICE_ID);
        }else {ProgressBar gamifyIndicator = findViewById(R.id.myprogressBar);
            gamifyIndicator.setIndeterminate(true);
            gamifyIndicator.setVisibility(View.GONE);}

    }

    private void saveGamifyData(int score, String email, Date userDate, String key) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
            String userDateFormated = df.format(userDate);
            byte[] emailData = email.getBytes(StandardCharsets.UTF_8);
            byte[] keyData = key.getBytes(StandardCharsets.UTF_8);
            byte[] userDateData = userDateFormated.getBytes(StandardCharsets.UTF_8);
            String emailData64 = Base64.encodeToString(emailData, Base64.DEFAULT);
            String keyData64 = Base64.encodeToString(keyData, Base64.DEFAULT);
            String userDateData64 = Base64.encodeToString(userDateData, Base64.DEFAULT);

            Call<EphxGPojo> call = RetrofitClient.getInstance().getMyApi().saveScore(emailData64,score,userDateData64,keyData64);
            call.enqueue(new Callback<EphxGPojo>() {
                @Override
                public void onResponse(Call<EphxGPojo> call, Response<EphxGPojo> response) {
                    EphxGPojo result = response.body();
                    String stat = result.getStatus();
                    if(stat.equals("ok")){
                        Log.w("TAG_SYNC", "SYNCRO-OK");
                    }
                    ProgressBar gamifyIndicator = findViewById(R.id.myprogressBar);
                    gamifyIndicator.setIndeterminate(false);
                    gamifyIndicator.setProgress(100);
                    gamifyIndicator.setVisibility(View.GONE);
                }
                @Override
                public void onFailure(Call<EphxGPojo> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), getString(R.string.systm_error_str), Toast.LENGTH_LONG).show();
                    ProgressBar gamifyIndicator = findViewById(R.id.myprogressBar);
                    gamifyIndicator.setIndeterminate(false);
                    gamifyIndicator.setProgress(0);
                }
            });
    }

    private void gamify_assitment(Date dateActual) {
        Date lastConnect = tinydb.getDate("gamify_saveddate");
       long caltest =  getChoicedData(dateActual, Calendar.DAY_OF_YEAR) - getChoicedData(lastConnect, Calendar.DAY_OF_YEAR);
       Log.i("HOUR_CALCULATED", String.valueOf(caltest));
        if(dateActual.getTime() >= lastConnect.getTime()){
            int user_acc = tinydb.getInt(getString(R.string.gamify_account_TAG));
            if (getChoicedData(dateActual, Calendar.YEAR)>getChoicedData(lastConnect, Calendar.YEAR)){
                //new year zone
                tinydb.putInt(getString(R.string.gamify_ui_var_days), 1);
                tinydb.putInt(getString(R.string.gamify_account_TAG), (user_acc + 1));
                tinydb.putDate("gamify_saveddate", new Date(System.currentTimeMillis()));
            }else if(getChoicedData(dateActual, Calendar.YEAR) == getChoicedData(lastConnect, Calendar.YEAR)){
                //actual zone
                if(getChoicedData(dateActual, Calendar.DAY_OF_YEAR)  == getChoicedData(lastConnect, Calendar.DAY_OF_YEAR) ){
                        //do nothing
                        tinydb.putDate("gamify_saveddate", new Date(System.currentTimeMillis()));
                }else if(getChoicedData(dateActual, Calendar.DAY_OF_YEAR) > getChoicedData(lastConnect, Calendar.DAY_OF_YEAR) ){
                    if((getChoicedData(dateActual, Calendar.DAY_OF_YEAR) - getChoicedData(lastConnect, Calendar.DAY_OF_YEAR)) == 1){
                        //do somthing
                        int varDays = tinydb.getInt(getString(R.string.gamify_ui_var_days));
                        if(varDays == 4){
                            varDays = varDays + 1;
                            tinydb.putInt(getString(R.string.gamify_ui_var_days), varDays);
                            tinydb.putDate("gamify_saveddate", new Date(System.currentTimeMillis()));
                            tinydb.putInt(getString(R.string.gamify_account_TAG), (user_acc + 6));
                        }else if(varDays == 6){
                            varDays = varDays + 1;
                            tinydb.putInt(getString(R.string.gamify_ui_var_days), varDays);
                            tinydb.putDate("gamify_saveddate", new Date(System.currentTimeMillis()));
                            tinydb.putInt(getString(R.string.gamify_account_TAG), (user_acc + 8));
                        }else if(varDays == 7){
                            tinydb.putInt(getString(R.string.gamify_ui_var_days), 1);
                            tinydb.putInt(getString(R.string.gamify_account_TAG), (user_acc + 1));
                            tinydb.putDate("gamify_saveddate", new Date(System.currentTimeMillis()));
                        }else {
                            if (varDays > 7){
                                tinydb.putInt(getString(R.string.gamify_ui_var_days), 0);
                            }
                            varDays = varDays + 1;
                            tinydb.putInt(getString(R.string.gamify_ui_var_days), varDays);
                            tinydb.putInt(getString(R.string.gamify_account_TAG), (user_acc + 1));
                            tinydb.putDate("gamify_saveddate", new Date(System.currentTimeMillis()));
                        }
                        tinydb.putDate("gamify_saveddate", new Date(System.currentTimeMillis()));
                    }else{
                        //do sothings
                        tinydb.putInt(getString(R.string.gamify_ui_var_days), 1);
                        tinydb.putDate("gamify_saveddate", new Date(System.currentTimeMillis()));
                        tinydb.putInt(getString(R.string.gamify_account_TAG), (user_acc + 1));
                    }
                }
            }
        }
    }

    int getChoicedData(Date dateVar, int myOption){
        Calendar varinst = Calendar.getInstance();
        varinst.setTime(dateVar);
       return varinst.get(myOption);
    }
    void webViewInt(){
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.clearCache(true);
        myWebView.clearFormData();
        myWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        myWebView.setBackgroundColor(Color.TRANSPARENT);
    }

    void loadAllTab(Context Ctx, String dateText){
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_eph).concat(longDateFormat)).setTag(0));
        LoadEphxWithTab(getBaseContext(),0,dateText);
        if (checkEphxData(Ctx, "ncs_" + dateText)){
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_naiss).setTag(1));
        }
        if (checkEphxData(Ctx, "dcs_" + dateText)){
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_dec).setTag(2));
        }
        if (checkEphxData(Ctx, "fte_" + dateText)){
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_natf).setTag(3));
        }
        if (checkEphxData(Ctx, "oth_" + dateText)){
            tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_other).setTag(4));
        }
    }

    void fragmentRequest(int requestText){
        int citation = 1;
        int month = 2;
        //Toast.makeText(getApplicationContext(),String.valueOf(requestText),Toast.LENGTH_LONG).show();
        if (requestText == citation){
            Random r = new Random();
            int low = 1;
            int high = 40;
            int resultRandom = r.nextInt(high-low) + low;
            tabLayout.removeAllTabs();
            loadCitation(getBaseContext(), resultRandom);
        } else if (requestText == month){

            tabLayout.removeAllTabs();
            loadMouthHistory(getBaseContext(),tdMouth);
        }
    }

    void LoadEphxWithTab(Context Ctx, int TabId, String DateTexte){
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.clearCache(true);
        myWebView.clearFormData();

       if (TabId == 0){
           myWebView.loadUrl("about:blank");
           if (isPremiumVersion && checkEphxData(Ctx, "eph_plus_" + DateTexte)){
               setEphxView(Ctx, "eph_plus_" + DateTexte);
           }else{
               setEphxView(Ctx, "eph_" + DateTexte);
           }
       }else if (TabId == 1){
           myWebView.loadUrl("about:blank");
           if (checkEphxData(Ctx, "ncs_" + DateTexte)){
               setEphxView(Ctx, "ncs_" + DateTexte);
           }
       }else if (TabId == 2){
           myWebView.loadUrl("about:blank");
           if (checkEphxData(Ctx, "dcs_" + DateTexte)){
               setEphxView(Ctx, "dcs_" + DateTexte);
           }
       }else if (TabId == 3){
           myWebView.loadUrl("about:blank");
           if (checkEphxData(Ctx, "fte_" + DateTexte)){
               setEphxView(Ctx, "fte_" + DateTexte);
           }
       }else if (TabId == 4){
           myWebView.loadUrl("about:blank");
           if (checkEphxData(Ctx, "oth_" + DateTexte)){
               setEphxView(Ctx, "oth_" + DateTexte);
           }
       }
   }

    private boolean checkEphxData(Context context, String dateText){
       if (readephxFromFile(context, dateText) ==null){
           return false;
       }  else return readephxFromFile(context, dateText) != "NONE";
   }

    private boolean setEphxView(Context context, String dateText){
        if (readephxFromFile(context, dateText) ==null){
            return false;
        }else {
            WebView myWebView = (WebView) findViewById(R.id.webview);
            String webdata = readephxFromFile(context, dateText);
            myWebView.clearCache(true);
            myWebView.clearFormData();
            myWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            myWebView.loadDataWithBaseURL("ephrox://default",webdata, "text/html", "UTF-8","");
            myWebView.setBackgroundColor(Color.TRANSPARENT);
            return readephxFromFile(context, dateText) !="NONE";
        }
    }

    @org.jetbrains.annotations.Nullable
    private String readephxFromFile(Context context, String dateId) {
        try {
            int myID = getResourceID(dateId, context);
            if(myID == -1){
                return null;
            }else {
                InputStream inputStream  = context.getResources().openRawResource(myID);
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }
                    inputStream.close();
                    return stringBuilder.toString();
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("readFromFile", "File not found: " + e);
            return"NONE";
        } catch (IOException e) {
            Log.e("readFromFile", "Can not read file: " + e);
            return"NONE";
        }
        return null;
    }

    protected static int getResourceID(final String resName, final Context ctx){
        try {
            final int ResourceID =
                    ctx.getResources().getIdentifier(resName, "raw",
                            ctx.getApplicationInfo().packageName);
            if (ResourceID == 0)
            {throw new IllegalArgumentException
                    ("No resource string found with name " + resName );}
            else
            {return ResourceID;}
        }catch (Exception e){
            return -1;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.citation:
                Random r = new Random();
                int low = 1;
                int high = 40;
                int resultRandom = r.nextInt(high-low) + low;
                tabLayout.removeAllTabs();
                loadCitation(getBaseContext(), resultRandom);
                return true;
            case R.id.hit_month:
                tabLayout.removeAllTabs();
                loadMouthHistory(getBaseContext(),tdMouth);
                return true;
            case R.id.eph_game:
                MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .setTitleText(R.string.datePiker_text)
                        .setTheme(R.style.ThemeOverlay_App_DatePicker)
                        .build();
                datePicker.show(getSupportFragmentManager(), "tag");
                datePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener) selection -> {
                    String dateValue =  new SimpleDateFormat("dd_MM", Locale.getDefault()).format(selection);
                    tdDate = new SimpleDateFormat("dd_MM", Locale.getDefault()).format(selection);
                    longDateFormat = new SimpleDateFormat(" E d MMM", Locale.FRENCH).format(selection);
                    tabLayout.removeAllTabs();
                    loadAllTab(getApplicationContext(),dateValue);
                });

                datePicker.addOnNegativeButtonClickListener(v -> {
                    tabLayout.removeAllTabs();
                    loadAllTab(getBaseContext(),tdDate);
                });
                datePicker.addOnCancelListener(dialog -> {
                    tabLayout.removeAllTabs();
                    loadAllTab(getBaseContext(),tdDate);
                });
                datePicker.addOnDismissListener(dialog -> {
                    tabLayout.removeAllTabs();
                    loadAllTab(getBaseContext(),tdDate);
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMouthHistory(Context baseContext, String todayDate) {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.mouth_htext).concat(longMouthFormat)).setTag(5));
        setEphxView(baseContext, "m_" + todayDate);
    }

    private void loadCitation(Context baseContext, int randomNumber) {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.citation)).setTag(6));
        setEphxView(baseContext, "c_".concat(Integer.toString(randomNumber)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    private void setUpBottomAppBarShapeAppearance() {
        ShapeAppearanceModel fabShapeAppearanceModel = floatingActionButton.getShapeAppearanceModel();
        boolean cutCornersFab =
                fabShapeAppearanceModel.getBottomLeftCorner() instanceof CutCornerTreatment
                        && fabShapeAppearanceModel.getBottomRightCorner() instanceof CutCornerTreatment;

        BottomAppBarTopEdgeTreatment topEdge =
                cutCornersFab
                        ? new BottomAppBarCutCornersTopEdge(
                        bottomAppBar.getFabCradleMargin(),
                        bottomAppBar.getFabCradleRoundedCornerRadius(),
                        bottomAppBar.getCradleVerticalOffset())
                        : new BottomAppBarTopEdgeTreatment(
                        bottomAppBar.getFabCradleMargin(),
                        bottomAppBar.getFabCradleRoundedCornerRadius(),
                        bottomAppBar.getCradleVerticalOffset());

        MaterialShapeDrawable babBackground = (MaterialShapeDrawable) bottomAppBar.getBackground();
        babBackground.setShapeAppearanceModel(
                babBackground.getShapeAppearanceModel().toBuilder().setTopEdge(topEdge).build());
    }

}
