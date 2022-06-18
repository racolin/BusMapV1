package github.racolin.busmap.route;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.Support;
import github.racolin.busmap.data.BusStopDAO;
import github.racolin.busmap.entities.BusStop;
import github.racolin.busmap.entities.Route;
import github.racolin.busmap.listener.OnBusStopListener;

//Ở Route detail, có 3 thứ chính là map, bus stops, route info
public class RouteDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback, OnBusStopListener {
//    Các view dùng để ánh xạ
    ViewPager2 vp2_detail_route;
    Route route;
    TextView tv_id, tv_name;
    List<BusStop> busStops;
    List<LocalTime> timeLines;
//    biến Google map và các bitmap và tạo độ để vẽ bản đồ và các marker
    GoogleMap map;
    Bitmap ic_big, ic_small, ic_focus;
    List<Marker> markers;
    LatLng[] latLngs;
//    pre_position cho biết điểm click trước đó là gì để hủy focus
    int pre_position = 0;
//    drag và ll xử dụng cho việc lướt phần chi tiết route lên và xuống
    LinearLayout drag, ll;
//    height là chiều cao của screen, down là vị trí tương đối của y khi vừa drag vào linear layout drag
    int height, down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        Intent intent = getIntent();
        route = (Route) intent.getSerializableExtra("route");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        String title = getString(R.string.route_number) + " " + route.getId();
        getSupportActionBar().setTitle(title);

//        Khởi tại map
        initMap();
//        ánh xạ view
        initUI();
//        bắt các sự kiện
        initListener();
    }

    private void initMap() {
//      tạo ra các icon cho marker gồm ic_small, ic_big và ic_focus
        busStops = getAllBusStopFromRoute();
        timeLines = getAllBusStopTimeLinesFromRoute();
        Drawable ic = getDrawable(R.drawable.ic_station_small);
        int width = ic.getIntrinsicWidth();
        int height = ic.getIntrinsicHeight();
        ic_small = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_small));

        ic = getDrawable(R.drawable.ic_station_big);
        width = ic.getIntrinsicWidth();
        height = ic.getIntrinsicHeight();
        ic_big = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_big));

        ic = getDrawable(R.drawable.ic_station_focus);
        width = ic.getIntrinsicWidth();
        height = ic.getIntrinsicHeight();
        ic_focus = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_focus));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fm_map);
        mapFragment.getMapAsync(this);
    }

    private void initUI() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        height = displayMetrics.heightPixels;
        drag = findViewById(R.id.drag);
        ll = findViewById(R.id.ll);
        tv_id = findViewById(R.id.tv_id);
        tv_id.setText(String.valueOf(route.getId()));
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(String.valueOf(route.getName()));

//        2 tab là 2 fragment bus tops và route info
        vp2_detail_route = findViewById(R.id.vp2_detail_route);
        RouteDetailStateAdapter adapter = new RouteDetailStateAdapter(this,
                route, busStops, timeLines, this);
        vp2_detail_route.setAdapter(adapter);

        new TabLayoutMediator(findViewById(R.id.tab_layout), vp2_detail_route, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.route_tab_bus_stop);
                        break;
                    case 1:
                        tab.setText(R.string.information_text);
                        break;
                }
            }
        }).attach();
//        middleRouteDetail();
    }

    private void initListener() {
//        Xử lý sự kiện click hold và kéo view lên xuống
//        Action down để lấy giá trị tương đối khi vừa click
//        down có mục đích là để biết được người dùng click vào đâu và thể hiện cho đúng
//        chứ không phải luôn luôn lấy điểm đầu tiên của drag làm vị trí trung tâm

//        action move xử lý sử kiện kéo view, khi đó cập nhật view
        drag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int y = (int) motionEvent.getRawY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        down = (int) motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setHeightRouteDetail(height - y + down);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (y - drag.getHeight() / 2 > height * 3/ 4) {
                            collapseRouteDetail();
                        } else {
                            if (y - drag.getHeight() / 2 < height / 4) {
                                expandRouteDetail();
                            }
                            else {
                                middleRouteDetail();
                            }
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

//    Đặt chiều cao của ll theo height
    private void setHeightRouteDetail(int height) {
        height = Math.max(height, drag.getHeight());
        height = Math.min(height, this.height - getSupportActionBar().getHeight());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ll.setLayoutParams(new RelativeLayout.LayoutParams(layoutParams));
    }

//    Đặt chiều cao của ll là lớn nhất
    private void expandRouteDetail() {
        setHeightRouteDetail(height - getSupportActionBar().getHeight());
    }

//    Đặt chiều cao của ll là bé nhất
    private void collapseRouteDetail() {
        setHeightRouteDetail(drag.getHeight());
    }

//    Đặt chiều cao của ll là mức ở giữa
    private void middleRouteDetail() {
        setHeightRouteDetail(height / 2);

    }
    
//    Hàm này được gọi từ bên trong adapter của bus stop fragment
//    khi click vào item trong adapter, vì nó có giữ listener nên có thể gọi ngược ra lại
//    Khi được gọi, vị trí bus stop sẽ được đổi màu thể hiện được focus
    @Override
    public void setOnBusStopClickListener(int position) {
//        Cập nhật icon marker
        markers.get(pre_position).setIcon(BitmapDescriptorFactory.fromBitmap(ic_big));
        markers.get(position).setIcon(BitmapDescriptorFactory.fromBitmap(ic_focus));
        pre_position = position;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs[position], 15));
    }

    //    Lấy tất cả bus stop từ route id
    private List<BusStop> getAllBusStopFromRoute() {
        return BusStopDAO.getBusStopsByRouteId(this, route.getId());
    }

//    Lấy tất cả time line từ route
    private List<LocalTime> getAllBusStopTimeLinesFromRoute() {
        List<LocalTime> time_lines = new ArrayList<>();
        String s = route.getOperation_time().split(" -")[0];
        time_lines.add(Support.stringToLocalTime(s, "HH:mm"));
        for (int i = 1; i < route.getPer_day(); i++) {
            time_lines.add(time_lines.get(i - 1).plusMinutes(route.getRepeat_time()));
        }
        return time_lines;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        markers = new ArrayList<>();
        latLngs = new LatLng[busStops.size()];
        int i = 0;

//        Khi bản đồ được load, ta vẽ các markers ứng với vị trí của các bus stop
        for (BusStop busStop : busStops) {
            latLngs[i++] = new LatLng(busStop.getStation().getAddress().getLat(),
                    busStop.getStation().getAddress().getLng());
            markers.add(map.addMarker(new MarkerOptions()
                    .position(new LatLng(latLngs[i - 1].latitude,
                            latLngs[i - 1].longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(ic_big))));
        }

        markers.get(pre_position).setIcon(BitmapDescriptorFactory.fromBitmap(ic_focus));
//      Vẽ line đi qua các bus stop này
        googleMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .add(latLngs)
                .color(getColor(R.color.orange)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs[0], 15));
    }
}