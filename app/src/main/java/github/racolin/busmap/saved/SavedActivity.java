package github.racolin.busmap.saved;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.data.SavedRouteDAO;
import github.racolin.busmap.data.SavedStationDAO;
import github.racolin.busmap.entities.Route;
import github.racolin.busmap.entities.Station;
import github.racolin.busmap.entities.User;
import github.racolin.busmap.entities.UserAccount;
import github.racolin.busmap.user.LoginActivity;

public class SavedActivity extends AppCompatActivity {
//    khai báo các view để ánh xạ
    ViewPager2 vp2_saved;
    TabLayout tab_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        nếu người dùng chưa đăng nhập thì sẽ load trang need_logged
//        và xử lý xự hiện click trên button login là chuyển hướng đến trang đăng nhập
        if (UserAccount.getUser() == null) {
            setContentView(R.layout.need_logged);
            Button btn_login = findViewById(R.id.btn_login);
            btn_login.setOnClickListener(v -> {
                goToLogin();
            });
        } else {
            setContentView(R.layout.activity_saved);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle(R.string.saved);

            initUI();
        }
    }

    private void initUI() {
        vp2_saved = findViewById(R.id.vp2_saved);
        SavedStateAdapter adapter = new SavedStateAdapter(this, getSavedRoutes(), getSavedStations());
        vp2_saved.setAdapter(adapter);
        tab_layout = findViewById(R.id.tab_layout);
//        tablayout gồm 2 tab là tuyến xe và trạm dừng
        new TabLayoutMediator(tab_layout, vp2_saved, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.route_tab);
                        break;
                    case 1:
                        tab.setText(R.string.route_tab_bus_stop);
                        break;
                }
            }
        }).attach();
    }

//    Xử lý sự kiện người dùng ấn button back trên toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    chuyển hướng đến trang đăng nhập
    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

//    Lấy danh sách các routes đã lưu
    private List<Route> getSavedRoutes() {
        User user = UserAccount.getUser();
        if (user != null) {
            return SavedRouteDAO.getSavedRoutesByUserID(this, user.getEmail());
        }
        return new ArrayList<>();
    }

//    Lấy danh sách các stations đã lưu
    private List<Station> getSavedStations() {
        User user = UserAccount.getUser();
        if (user != null) {
            return SavedStationDAO.getSavedStationsByUserId(this, user.getEmail());
        }
        return new ArrayList<>();
    }
}