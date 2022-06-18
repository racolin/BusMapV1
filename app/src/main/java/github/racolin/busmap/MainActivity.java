package github.racolin.busmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import github.racolin.busmap.address.AddressSearchActivity;
import github.racolin.busmap.component.Side;
import github.racolin.busmap.data.DatabaseHelper;
import github.racolin.busmap.data.StationDAO;
import github.racolin.busmap.entities.Address;
import github.racolin.busmap.entities.User;
import github.racolin.busmap.entities.UserAccount;
import github.racolin.busmap.feedback.FeedbackActivity;
import github.racolin.busmap.feedback.InformationGroupActivity;
import github.racolin.busmap.feedback.RateActivity;
import github.racolin.busmap.feedback.UpdateActivity;
import github.racolin.busmap.notification.NotificationActivity;
import github.racolin.busmap.result.FindRoadActivity;
import github.racolin.busmap.route.RouteActivity;
import github.racolin.busmap.saved.SavedActivity;
import github.racolin.busmap.side.SideAdapter;
import github.racolin.busmap.user.InformationActivity;
import github.racolin.busmap.user.LoginActivity;
import github.racolin.busmap.user.RegisterActivity;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
//    Ở đây là các biến sẽ ánh xạ các View trong main_layout
    DrawerLayout drawable_layout;
    NavigationView nv_group;
    Spinner side_spinner;
    LinearLayout ll_logged, ll_not_logged;
    ImageView iv_avatar;
    TextView tv_name, tv_email;
    Button btn_info, btn_login, btn_search, btn_register;
    ImageButton ib_search, ib_find, ib_logout;
//   User lưu giá trị của user, nếu bằng null tức là chưa đăng nhập
//   nếu != null thì có nghĩa là đã đăng nhập
//   user sẽ được lấy từ UserAccount. Lớp này được viết dựa trên Singleton Pattern
//   và có synchronized giúp cho việc gọi trong nhiều luồng không làm sai singleton
    User user;
//   address lưu giá trị của địa chỉ được trả veeff khi sử dụng chức năng tìm kiếm địa điểm
    Address address;
//   icon dùng để làm marker, có 2 loại icon là big và small
//   Khi màn hình có độ phóng lớn (>14) thì ta sẽ sử dụng big_icon
//   Ngược lại thì sử dụng small_icon
    Bitmap big_icon, small_icon;
//   map sẽ lưu lại google map được trả về trong hàm onMapReady
    private GoogleMap map;
//   2 list small markers và big_markers chứa 2 loại markers khi màn hình có độ zoom lớn và nhỏ
//   Đã thử dùng một list sau đó khi độ zoom thay đổi thì thay đổi icon big thành small và ngược lại
//   Nhưng hiệu xuất rất kém tại vì số station rất lớn nếu cứ load đi load lại thì rất phí tài nguyên
//   Thay vào đó sử dụng 2 list và chỉ sử dụng setVisible
    List<Marker> small_markers, big_markers;
//   dest là marker tại vị trí mà ta search
    Marker dest;
//   Đây là latLng default làm trung tâm của bản đồ (ĐH SPKT)
    LatLng df = new LatLng(10.85075361772994, 106.77124465290879);
//   2 biến zoom và pre_zoom giúp cho ta có thể biết được độ zoom trước và sau của bản đồ
//   Được dùng để kiểm tra xem khi nào zoom vượt ngưỡng thì sẽ bật tắt các markers
    float zoom = 15, pre_zoom = 15;

//   Đây là một launcher được dùng để lấy address, launcher này được sử dụng thay thế cho
//   onActivityResult
    ActivityResultLauncher<Intent> getAddress = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        address = (Address) result.getData().getSerializableExtra("address");
                        showAddress();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Đầu tiên ta cần load user để xem người dùng đã đăng nhập hay chưa
//        để hiển thị navigation cho đúng và giới hạn các chức năng cần đăng nhập
        user = UserAccount.getUser();
//        Với máy ảo android 12 api 31 thì không cần loaddb trước
//        Nhưng có vấn đề với android 12 api 32 thì nó không tìm thấy table nên ta cần load trước
        initLoadDb();
//        Khởi tạo bản đồ
        initMap();
//        Ánh xạ các thành phần view
        initUI();
//        Bắt các sự kiện trên các view
        initListener();
    }

//    Load database đơn giản chỉ cần gọi hàm onCreate của lớp DatabaseHelper
    private void initLoadDb() {
        DatabaseHelper helper = new DatabaseHelper(this);
        helper.onCreate(helper.getWritableDatabase());
    }

//    Khởi tạo map
    private void initMap() {
//        tạo 2 icon big và small cho để làm icon marker
        Drawable ic = getDrawable(R.drawable.ic_station_small);
        int width = ic.getIntrinsicWidth();
        int height = ic.getIntrinsicHeight();
        small_icon = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(small_icon));

        ic = getDrawable(R.drawable.ic_station_big);
        width = ic.getIntrinsicWidth();
        height = ic.getIntrinsicHeight();
        big_icon = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(big_icon));

//        Khởi tạo trước 2 list markers
        big_markers = new ArrayList<>();
        small_markers = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fm_map);
        mapFragment.getMapAsync(this);
    }

//    Ánh xạ các view trong layout
    private void initUI() {
//        Ánh xạ drawable_layout và navigation
        drawable_layout = findViewById(R.id.drawable_layout);
        nv_group = findViewById(R.id.nv_group);

//        hide title của toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        Tạo toggle lên icon đóng mở navigation
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawable_layout,
                        toolbar, R.string.open_nav, R.string.close_nav);
        drawable_layout.addDrawerListener(toggle);
        toggle.syncState();

//        Đổ data vào spinner chọn các vùng miền trong app
        side_spinner = findViewById(R.id.side_spinner);
        SideAdapter adapter = new SideAdapter(this, getSides());
        side_spinner.setAdapter(adapter);

        btn_search = findViewById(R.id.btn_search);
        ib_search = findViewById(R.id.ib_search);
        ib_find = findViewById(R.id.ib_find);

//        Ánh xạ các view của header navigation
        View header = nv_group.getHeaderView(0);
        ll_logged = header.findViewById(R.id.ll_logged);
        iv_avatar = header.findViewById(R.id.iv_avatar);
        tv_name = header.findViewById(R.id.tv_name);
        tv_email = header.findViewById(R.id.tv_email);
        btn_info = header.findViewById(R.id.btn_info);
        ll_not_logged = header.findViewById(R.id.ll_not_logged);
        btn_login = header.findViewById(R.id.btn_login);
        ib_logout = header.findViewById(R.id.ib_logout);
        btn_register = header.findViewById(R.id.btn_register);

//        Khi người dùng đã login thì sẽ hiển thị header navigation là thông tin của người dùng
        if (user == null) {
            ll_not_logged.setVisibility(View.VISIBLE);
            ll_logged.setVisibility(View.GONE);
            btn_login.setVisibility(View.VISIBLE);
        } else {
            ll_logged.setVisibility(View.VISIBLE);
            if (user.getImage() == null) {
                iv_avatar.setImageResource(R.drawable.avatar_default);
            } else {
                byte[] image = user.getImage();
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                iv_avatar.setImageBitmap(bitmap);
            }
            tv_name.setText(user.getName());
            tv_email.setText(user.getEmail());

            ll_logged.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.GONE);
        }
    }

//    Bắt sự kiện
    private void initListener() {
//        Xử lý sự kiện khi click các item bên trong navigation
//        Khi click vào 1 item thì đòng thời thu gọn navigation và thực hiện chức năng
        nv_group.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nv_find_address:
                        onSearchAddressSelected();
                        break;
                    case R.id.nv_notification:
                        onNotificationSelected();
                        break;
                    case R.id.nv_find_bus:
                        onFindBusSelected();
                        break;
                    case R.id.nv_search:
                        onSearchSelected();
                        break;
                    case R.id.nv_update:
                        onUpdateSelected();
                        break;
                    case R.id.nv_feedback:
                        onFeedbackSelected();
                        break;
                    case R.id.nv_rate:
                        onRateSelected();
                        break;
                    case R.id.nv_information:
                        onInformationSelected();
                        break;
                }
                drawable_layout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

//        Mở infomation activity của người dùng
        btn_info.setOnClickListener(v -> {
            Intent intent = new Intent(this, InformationActivity.class);
            startActivity(intent);
        });

//        Gọi hàm khi click vào button search, hàm đã được đinh nghĩa bên dưới
        btn_search.setOnClickListener(v -> {
            onSearchAddressSelected();
        });

//        Gọi hàm khi click vào button find road, hàm đã được đinh nghĩa bên dưới
        ib_find.setOnClickListener(v -> {
            onFindBusSelected();
        });

//        Gọi hàm khi click vào button search, hàm đã được đinh nghĩa bên dưới
        ib_search.setOnClickListener(v -> {
            onSearchSelected();
        });

//        chuyển đến activity đăng nhập
        btn_login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

//        đăng xuất và gọi lại đến main activity nhưng sẽ xóa hết các task activity cũ
        ib_logout.setOnClickListener(v -> {
            UserAccount.logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

//    Mở activity RegisterActivity khi click vào button đăng ký
        btn_register.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

//        launcher activity AddressSearchActivity để nhận kết quả trả về là một address
    public void onSearchAddressSelected() {
        Intent intent = new Intent(this, AddressSearchActivity.class);
        getAddress.launch(intent);
    }

    //    Mở activity RouteActivity
    public void onSearchSelected() {
        Intent intent = new Intent(this, RouteActivity.class);
        startActivity(intent);
    }

    //    Mở activity FindRoadActivity
    public void onFindBusSelected() {
        Intent intent = new Intent(this, FindRoadActivity.class);
        startActivity(intent);
    }

    //    Mở activity SavedActivity
    public void onSavedSelected() {
        Intent intent = new Intent(this, SavedActivity.class);
        startActivity(intent);
    }

    //    Mở activity NotificationActivity
    private void onNotificationSelected() {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    //    Mở activity UpdateActivity
    private void onUpdateSelected() {
        Intent intent = new Intent(this, UpdateActivity.class);
        startActivity(intent);
    }

    //    Mở activity FeedbackActivity
    private void onFeedbackSelected() {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    //    Mở activity RateActivity
    private void onRateSelected() {
        Intent intent = new Intent(this, RateActivity.class);
        startActivity(intent);
    }

//    Mở activity InformationGroupActivity
    private void onInformationSelected() {
        Intent intent = new Intent(this, InformationGroupActivity.class);
        startActivity(intent);
    }

//    Menu chỉ gồm 1 item là saved
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_saved, menu);
        return true;
    }

//    Nếu item_saved được click thì sẽ gọi hàm onSavesSelected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_saved:
                onSavedSelected();
                break;
        }
        return true;
    }

//    Show địa chỉ trả về lên thanh tìm kiếm và move camera tới địa chỉ vừa trả về
    private void showAddress() {
        btn_search.setText(address.getAddress());
        df = new LatLng(address.getLat(), address.getLng());
        moveCamera();
    }

//    Tạo các side mặc định
    public List<Side> getSides() {
        List<Side> sides = new ArrayList<>();
        sides.add(new Side("TP Hồ Chí Minh", R.drawable.vn));
        sides.add(new Side("Đà Nẵng", R.drawable.vn));
        sides.add(new Side("Thừa Thiên Huế", R.drawable.vn));
        sides.add(new Side("Hà Nội", R.drawable.vn));
        return sides;
    }

//    di chuyển camera đến vị trí của df với độ zoom được thiết lập lại là 15
//    marker dest sẽ được hiển thị lên trên cùng
    public void moveCamera() {
        pre_zoom = 15;
        zoom = 15;
        if (dest != null) {
            dest.setPosition(df);
        } else {
            dest = map.addMarker(new MarkerOptions().position(df).zIndex(1f));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(df, zoom));
    }

//    Vẽ mặc định các markers
    public void drawDefault() {
//        Lấy tất cả latlng của station
        List<LatLng> latLngs = StationDAO.getLatLngOfStations(this);
        int len = latLngs.size();
//        Với các latlng của các stations, mỗi latlng sẽ vẽ 2 marker và ẩn marker của icon small đi
        for (int i = 0; i < len; i++) {
            big_markers.add(map.addMarker(new MarkerOptions().position(latLngs.get(i)).icon(BitmapDescriptorFactory.fromBitmap(big_icon))));
            small_markers.add(map.addMarker(new MarkerOptions().position(latLngs.get(i)).icon(BitmapDescriptorFactory.fromBitmap(small_icon))));
            small_markers.get(i).setVisible(false);
        }
//        Bắt sự kiện move camera: nếu camera từ độ zoom từ < 14 lên > 14 thì sẽ show big marker
//        Nếu camera từ độ zoom > 14 xuống < 14 thì sẽ show small marker
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                zoom = map.getCameraPosition().zoom;
                if (zoom >= 14 && pre_zoom < 14) {
                    setBigIcon();
                }
                if (zoom < 14 && pre_zoom >= 14) {
                    setSmallIcon();
                }
                pre_zoom = zoom;
            }
        });
        moveCamera();
    }

//    Hàm này ẩn tất cả các small_markers và show tất cả các big_markers
    private void setBigIcon() {
        int len = big_markers.size();
        for (int i = 0; i < len; i++) {
//            big_markers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(big_icon));
            big_markers.get(i).setVisible(true);
            small_markers.get(i).setVisible(false);
        }
    }

//    Hàm này ẩn tất cả các big_markers và show tất cả các small_markers
    private void setSmallIcon() {
//        int len = big_markers.size();
        int len = small_markers.size();
        for (int i = 0; i < len; i++) {
//            big_markers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(big_icon));
            big_markers.get(i).setVisible(false);
            small_markers.get(i).setVisible(true);
        }
    }

//    khi map đã load xong thì gán googleMap và map sau đó vẽ các marker
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        drawDefault();
    }

//    Khi bấm nút back, nếu drawable_layout đang mở thì cần đóng nó lại trước
    @Override
    public void onBackPressed() {
        if (drawable_layout.isDrawerOpen(GravityCompat.START)) {
            drawable_layout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}