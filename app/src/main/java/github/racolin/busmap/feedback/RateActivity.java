package github.racolin.busmap.feedback;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import github.racolin.busmap.R;

public class RateActivity extends AppCompatActivity {
//Các biến để ánh xạ view
    RatingBar rb_rate;
    EditText edt_rate;
    TextView tv_rate;
    Button btn_rate;

    String[] rate_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(R.string.rate_text);
        
        initUI();
        initListener();
    }
//  Ánh xạ view
    private void initUI() {
        rb_rate = findViewById(R.id.rb_rate);
        tv_rate = findViewById(R.id.tv_rate);
        edt_rate = findViewById(R.id.edt_rate);
        btn_rate = findViewById(R.id.btn_rate);

        rate_type = getResources().getStringArray(R.array.rate_type);
        changeTypeRate((int) rb_rate.getRating());
    }

//    Xử lý sự kiện
    private void initListener() {
        btn_rate.setOnClickListener(v -> {
            send_rate();
        });

        rb_rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                changeTypeRate((int) v);
            }
        });
    }

//    hàm sẽ toast lên là đánh giá đã được ghi nhận
    private void send_rate() {
        Toast.makeText(this, R.string.toast_send_rate, Toast.LENGTH_SHORT).show();
    }

//    tùy vào giá trị rate thì sẽ xét tv_rate
    private void changeTypeRate(int r) {
        r = r == 5 ? r - 1 : r;
        tv_rate.setText(rate_type[r]);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}