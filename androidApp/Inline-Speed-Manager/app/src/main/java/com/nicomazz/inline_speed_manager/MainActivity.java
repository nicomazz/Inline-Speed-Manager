package com.nicomazz.inline_speed_manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bmb)
    BoomMenuButton boomMenuButton;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setManualStartFragment();

        setupBoomMenu();
    }

    private void setupBoomMenu() {
        boomMenuButton.setButtonEnum(ButtonEnum.Ham);
        boomMenuButton.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        boomMenuButton.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);
        HamButton.Builder builder = new HamButton.Builder()
                //.normalImageRes(R.drawable.map)
                .normalText("Free start")
                .subNormalText("Partenza libera, senza un via")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        setFreeStartFragment();
                    }
                });

        HamButton.Builder builder1 = new HamButton.Builder()
                //.normalImageRes(R.drawable.compass)
                .normalText("Partenza manuale")
                .subNormalText("Il cronometro parte quando si rilascia il pulsante start")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        setManualStartFragment();
                    }
                });
        HamButton.Builder builder2 = new HamButton.Builder()
                .normalText("Partenza automatica")
                .subNormalText("Una volta premuto start una voce darà la partenza.")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        setAutoStartFragment();
                    }
                });
        HamButton.Builder builder3 = new HamButton.Builder()
                .normalText("Impostazioni")
                //.subNormalText(getString(R.string.selling_point_desc))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                       Preferences.startSettings(MainActivity.this);
                    }
                });
        boomMenuButton.addBuilder(builder);
        boomMenuButton.addBuilder(builder1);
        boomMenuButton.addBuilder(builder2);
        boomMenuButton.addBuilder(builder3);
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
        if (id == R.id.action_settings) {
            Preferences.startSettings(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setFreeStartFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new FreeStartFragment()).commit();
    }

    private void setManualStartFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new ManualStartFragment()).commit();
    }

    private void setAutoStartFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new AutomaticStartFragment()).commit();
    }
}
