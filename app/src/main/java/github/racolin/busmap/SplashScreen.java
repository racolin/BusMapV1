package github.racolin.busmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

// Đây không hẳn là SplashScreen mà đây có thể gọi là màn hình chở
// SplashScreen đã định nghĩa trong theme
// Đây chỉ là màn hình chờ
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
//        Load activity và tạo animation rotate icon load trong 3 giây
        ImageView iv = findViewById(R.id.iv_load);
        iv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this, github.racolin.busmap.MainActivity.class));
            finish();
        }).start();
    }
}