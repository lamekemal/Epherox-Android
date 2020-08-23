package bj.archeos.epherox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    BottomAppBar bottomAppBar;
    Context mycrt;
    TabLayout tabLayout;
    String tdDate = "02_07"; //today date format dd-mm
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mycrt = this;
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.fabHome);
        bottomAppBar = findViewById(R.id.bottomAppbar);
        //main line for setting menu in bottom app bar
        setSupportActionBar(bottomAppBar);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color. blanc));
        }

        tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LoadEphxWithTab(getBaseContext(),tab.getPosition(),tdDate);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Toast.makeText(getApplicationContext(),tab.getText(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                LoadEphxWithTab(getBaseContext(),tab.getPosition(),tdDate);
            }
        });

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDrawerFragment bottomNawdw = new BottomDrawerFragment();
                bottomNawdw.show(getSupportFragmentManager(), "TAG");
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"HOME BUTTON",Toast.LENGTH_LONG).show();
            }
        });

        setUpBottomAppBarShapeAppearance();

        WebView myWebView = (WebView) findViewById(R.id.webview);
        //String webdata = readephxFromFile(this, "eph_" + tdDate);
        myWebView.clearCache(true);
        myWebView.clearFormData();
        myWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        //myWebView.loadDataWithBaseURL("ephrox://default",webdata, "text/html", "UTF-8","");
        myWebView.setBackgroundColor(Color.TRANSPARENT);
        LoadEphxWithTab(getBaseContext(),4,tdDate);
        LoadEphxWithTab(getBaseContext(),3,tdDate);
        LoadEphxWithTab(getBaseContext(),2,tdDate);
        LoadEphxWithTab(getBaseContext(),1,tdDate);
        LoadEphxWithTab(getBaseContext(),0,tdDate);
    }
    void LoadEphxWithTab(Context Ctx, int TabId, String DateTexte){
       if (TabId == 0){
           if ( setEphxView(Ctx, "eph_" + DateTexte) == true){
               setEphxView(Ctx, "eph_" + DateTexte);
           }else {
               //tab hide
                tabLayout.getTabAt(0).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
           }

       }else if (TabId == 1){
           if (  setEphxView(Ctx, "ncs_" + DateTexte) == true){
               setEphxView(Ctx, "ncs_" + DateTexte);
           }else {
               //tab hide
               tabLayout.getTabAt(1).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
           }

       }else if (TabId == 2){
           if (     setEphxView(Ctx, "dcs_" + DateTexte) == true){
               setEphxView(Ctx, "dcs_" + DateTexte);
           }else {
               //tab hide
               tabLayout.getTabAt(2).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
           }
       }else if (TabId == 3){
           if (setEphxView(Ctx, "fte_" + DateTexte) == true){
               setEphxView(Ctx, "fte_" + DateTexte);
           }else {
               //tab hide
               tabLayout.getTabAt(3).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
           }
       }else if (TabId == 4){
           if (   setEphxView(Ctx, "oth_" + DateTexte) == true){
               setEphxView(Ctx, "oth_" + DateTexte);
           }else {
               //tab hide
               tabLayout.getTabAt(4).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
           }
       }
   }

    private boolean setEphxView(Context context, String dateText){
        if (readephxFromFile(context, dateText) ==null){
            return false;
        } else if (readephxFromFile(context, dateText) =="NONE"){
            WebView myWebView = (WebView) findViewById(R.id.webview);
            String webdata = readephxFromFile(context, dateText);
            myWebView.clearCache(true);
            myWebView.clearFormData();
            myWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            myWebView.loadDataWithBaseURL("ephrox://default",webdata, "text/html", "UTF-8","");
            myWebView.setBackgroundColor(Color.TRANSPARENT);
            Log.v("WEBV", webdata);
            return false;
        }else {
            WebView myWebView = (WebView) findViewById(R.id.webview);
            String webdata = readephxFromFile(context, dateText);
            myWebView.clearCache(true);
            myWebView.clearFormData();
            myWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            myWebView.loadDataWithBaseURL("ephrox://default",webdata, "text/html", "UTF-8","");
            myWebView.setBackgroundColor(Color.TRANSPARENT);
            Log.v("WEBV", webdata);
            return true;
        }
    }

    private String readephxFromFile(Context context, String dateId) {
        try {
            int myID = getResourceID(dateId, "raw", getApplicationContext());
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
            Log.e("readFromFile", "File not found: " + e.toString());
            return"NONE";
        } catch (IOException e) {
            Log.e("readFromFile", "Can not read file: " + e.toString());
            return"NONE";
        }
        return null;
    }

    protected final static int getResourceID (final String resName, final String resType, final Context ctx){
        try {
            final int ResourceID =
                    ctx.getResources().getIdentifier(resName, resType,
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.citation:
                String currentDate = new SimpleDateFormat("dd_MM", Locale.getDefault()).format(new Date());
                Toast.makeText(getApplicationContext(),currentDate,Toast.LENGTH_LONG).show();
                return true;
            case R.id.hit_month:
                Toast.makeText(getApplicationContext(),getPackageResourcePath(),Toast.LENGTH_LONG).show();
                return true;
            case R.id.eph_game:
                Toast.makeText(getApplicationContext(),"JEUX EPH",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
