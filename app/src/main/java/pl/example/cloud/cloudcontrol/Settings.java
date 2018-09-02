package pl.example.cloud.cloudcontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.cloud.cloudcontrol.R;

/**
 * TODO:
 *
 * toolbar
 * strzałka do tyłu do kontroli chmury
 * zmiana stylu na dark
 * przejcie do wyboru chmury
 *
 */
public class Settings extends AppCompatActivity {

    public static final String PREFS_NAME = "prefs";
    public static final String PREF_DARK_THEME = "dark_theme";

    Switch darkThemeSwitch;

    private boolean useDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Use the chosen theme
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setComponents();
    }

    private void setComponents(){
        setToolbar();
        setDarkThemeSwitch();

    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_settings_menu, menu);
        return true;
    }

    private void setDarkThemeSwitch(){
        darkThemeSwitch = (Switch) findViewById(R.id.dark_theme_switch);
        darkThemeSwitch.setChecked(useDarkTheme);
        darkThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleTheme(isChecked);
            }
        });
    }

    private void toggleTheme(boolean isChecked){
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, isChecked);
        editor.apply();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
