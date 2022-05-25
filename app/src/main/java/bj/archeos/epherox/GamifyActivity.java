package bj.archeos.epherox;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bj.archeos.epherox.model.EphxGPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GamifyActivity extends AppCompatActivity {
    TabLayout tabLayout;
    TinyDB tinydb;
    String DEVICE_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamify);
        tabLayout = findViewById(R.id.usertabs);
        Toolbar toolbar = findViewById(R.id.gamifyToolbars);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(getString(R.string.gamifySubTitle));
        tinydb = new TinyDB(this);
        LinearLayout recompensesTabs = findViewById(R.id.recompenseTabs);
        LinearLayout accompteView = findViewById(R.id.sub_accompte_view);
        LinearLayout sycaccompteView = findViewById(R.id.connected_acc_panel);
        accompteView.setVisibility(View.GONE);
        sycaccompteView.setVisibility(View.GONE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            boolean isRegistered = tinydb.getBoolean("is_login");
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() ==0){
                    recompensesTabs.setVisibility(View.VISIBLE);
                    accompteView.setVisibility(View.GONE);
                    sycaccompteView.setVisibility(View.GONE);
                }else if (tab.getPosition() ==1){
                    if(isRegistered){
                        sycaccompteView.setVisibility(View.VISIBLE);
                    }else{
                        accompteView.setVisibility(View.VISIBLE);
                    }
                    recompensesTabs.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                accompteView.setVisibility(View.GONE);
                recompensesTabs.setVisibility(View.GONE);
                sycaccompteView.setVisibility(View.GONE);
                if (tab.getPosition() ==0){
                    recompensesTabs.setVisibility(View.VISIBLE);
                }else if (tab.getPosition() ==1){
                    if(isRegistered){
                        sycaccompteView.setVisibility(View.VISIBLE);
                    }else{
                        accompteView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        TextInputEditText emailText = findViewById(R.id.mail_inputfield);
        TextInputEditText pinText = findViewById(R.id.pin_inputfield);

        //Register
        MaterialButton registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(view -> {
            ProgressBar progindc = findViewById(R.id.myprogressBar);
            progindc.setVisibility(View.VISIBLE);
            int acc = tinydb.getInt(getString(R.string.gamify_account_TAG));
            try {
                String userMaili= String.valueOf(emailText.getText());
              boolean status = registerDirector(userMaili, Integer.valueOf(pinText.getText().toString()),acc );
              if (status){
                  Log.w("KML-NN","ISTER FONCTION OK,,,,,,,RETURN OK");
              }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        DEVICE_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //Login
        MaterialButton loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> {
            ProgressBar progindc = findViewById(R.id.myprogressBar);
            progindc.setVisibility(View.VISIBLE);
            try {
                loginDirector(emailText.getText().toString(), Integer.valueOf(pinText.getText().toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        //Disconnect
        MaterialButton disconnectButton = findViewById(R.id.disconn_button);
        disconnectButton.setOnClickListener(view -> {
            try {
                Log.w("DSC_T", "Pressed disconnected");
                Log.w("DSC_T", tinydb.getString("user_mail"));
                syncDataWithServer(tinydb.getInt(getString(R.string.gamify_account_TAG)),
                        tinydb.getString("user_mail"),
                        tinydb.getDate("gamify_saveddate"),DEVICE_ID );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        TextView syncedControl = findViewById(R.id.text_for_conneced_accuont);
        TextView walletViewer = findViewById(R.id.acc_debit);
        syncedControl.setText(tinydb.getString("user_mail"));
        if (tinydb.getInt(getString(R.string.gamify_account_TAG))>99){
            walletViewer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 51);
            walletViewer.setText(String.valueOf(tinydb.getInt(getString(R.string.gamify_account_TAG))));
        }else {
            walletViewer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 85);
            walletViewer.setText(String.valueOf(tinydb.getInt(getString(R.string.gamify_account_TAG))));
        }

        CardView days_01 = findViewById(R.id.daysJ_1);
        CardView days_02 = findViewById(R.id.daysJ_2);
        CardView days_03 = findViewById(R.id.daysJ_3);
        CardView days_04 = findViewById(R.id.daysJ_4);
        CardView days_05 = findViewById(R.id.daysJ_5);
        CardView days_06 = findViewById(R.id.daysJ_6);
        CardView days_07 = findViewById(R.id.daysJ_7);

        List<CardView> gamifyControlList = new ArrayList<>();
        gamifyControlList.add(0,days_01);
        gamifyControlList.add(1,days_02);
        gamifyControlList.add(2,days_03);
        gamifyControlList.add(3,days_04);
        gamifyControlList.add(4,days_05);
        gamifyControlList.add(5,days_06);
        gamifyControlList.add(6,days_07);

        setGamifyDaysUi(gamifyControlList, tinydb.getInt(getString(R.string.gamify_ui_var_days)));
    }

    private void syncDataWithServer(int score, String email, Date userDate, String key) throws UnsupportedEncodingException {
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
                Log.w("DSC_T", userDateData64 +emailData64 + "*"+ keyData64);
                Log.w("DSC_T", result.getStatus());
                Log.w("DSC_T", result.getMsg());
                TinyDB tinydb = new TinyDB(getApplicationContext());
                if (stat.equals("OK")){
                    tinydb.putBoolean("is_login",false);
                    tinydb.putString("user_mail","*");
                    tinydb.putInt("user_pin",0);
                    tinydb.putInt(getString(R.string.gamify_account_TAG),0);
                    tinydb.putInt(getString(R.string.gamify_ui_var_days), 1);
                    finish();
                    startActivity(getIntent());
                }else if(result.getMsg().equals("0")){
                    tinydb.putBoolean("is_login",false);
                    tinydb.putString("user_mail","*");
                    tinydb.putInt("user_pin",0);
                    tinydb.putInt(getString(R.string.gamify_account_TAG),0);
                    tinydb.putInt(getString(R.string.gamify_ui_var_days), 1);
                    finish();
                    startActivity(getIntent());
                }
            }
            @Override
            public void onFailure(Call<EphxGPojo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.systm_error_str), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setGamifyDaysUi(List<CardView> daysCard, int days){
        if (!(days == 1)){
            try {
                daysCard.get(days-1).setCardBackgroundColor(getResources().getColor(R.color.blancA20));
                daysCard.get(days-2).setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            }catch (IndexOutOfBoundsException ex){
                ex.printStackTrace();
            }
        }else {
            daysCard.get(0).setCardBackgroundColor(getResources().getColor(R.color.blancA20));
        }
    }
    private void loginDirector(String email, int code) throws UnsupportedEncodingException {
        LinearLayout gamifyLayout = findViewById(R.id.gamifyLayout);
        byte[] data = email.getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);

        Call<EphxGPojo> call = RetrofitClient.getInstance().getMyApi().getUser(base64,code);
        call.enqueue(new Callback<EphxGPojo>() {
            @Override
            public void onResponse(Call<EphxGPojo> call, Response<EphxGPojo> response) {
                EphxGPojo myheroList = response.body();
                String stat = myheroList.getStatus();
                //Toast.makeText(getApplicationContext(),stat,Toast.LENGTH_LONG).show();
                if (stat.equals("OK")){
                    TinyDB tinydb = new TinyDB(getApplicationContext());
                    tinydb.putBoolean("is_login",true);
                    tinydb.putString("user_mail",email);
                    tinydb.putInt("user_pin",code);
                    int acc = tinydb.getInt(getString(R.string.gamify_account_TAG));
                    tinydb.putInt(getString(R.string.gamify_account_TAG),(acc + myheroList.getScore()));
                    finish();
                    startActivity(getIntent());
                }else if(!(myheroList.getCode() ==null)){
                    if(Integer.valueOf(myheroList.getCode())  == 404){
                        Snackbar.make(gamifyLayout, getString(R.string.error_login_txt), Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
                ProgressBar progindc = findViewById(R.id.myprogressBar);
                progindc.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<EphxGPojo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.systm_error_str), Toast.LENGTH_LONG).show();
                ProgressBar progindc = findViewById(R.id.myprogressBar);
                progindc.setVisibility(View.GONE);
            }
        });
    }

    private Boolean registerDirector(String userMail, Integer userPin, Integer userData) throws UnsupportedEncodingException {
        final boolean[] result = {false};
        byte[] emailData = userMail.getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.encodeToString(emailData, Base64.DEFAULT);
        Call<EphxGPojo> call = RetrofitClient.getInstance().getMyApi().setUser(base64,userPin,userData);
        call.enqueue(new Callback<EphxGPojo>() {
            @Override
            public void onResponse(Call<EphxGPojo> call, Response<EphxGPojo> response) {
                EphxGPojo apiData = response.body();
                String stat = apiData.getStatus();
                ProgressBar progindc = findViewById(R.id.myprogressBar);
                progindc.setVisibility(View.GONE);
                if (stat.equals("OK")){
                    Toast.makeText(getApplicationContext(), "Compte enregistrer", Toast.LENGTH_LONG).show();
                    TinyDB tinydb = new TinyDB(getApplicationContext());
                    tinydb.putBoolean("is_login",true);
                    tinydb.putString("user_mail",userMail);
                    tinydb.putInt("user_pin",userPin);
                    result[0] = true;
                    finish();
                    startActivity(getIntent());
                }else {
                    Toast.makeText(getApplicationContext(), "Informations incorrectes!", Toast.LENGTH_LONG).show();
                    result[0] = false;
                }
            }
            @Override
            public void onFailure(Call<EphxGPojo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                result[0] = false;
                ProgressBar progindc = findViewById(R.id.myprogressBar);
                progindc.setVisibility(View.GONE);
            }
        });
        return result[0];
    }
}
