package in.co.aarthi.locatormaps;

/**
 * Created by aarthi on 31/1/15.
 */
import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;

public class homescreen extends Activity {

    private static final int SPLASH_TIME = 2 * 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(homescreen.this,
                        GroundOverlayDemoActivity.class);
                startActivity(intent);

                homescreen.this.finish();

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        }, SPLASH_TIME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, SPLASH_TIME);

    }


    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}
