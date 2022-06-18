package github.racolin.busmap.result;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.address.AddressSearchActivity;
import github.racolin.busmap.config.AddressResultType;
import github.racolin.busmap.entities.Address;

public class FindRoadActivity extends AppCompatActivity implements OnMapReadyCallback {
//    Các biến ánh xạ các view trong layout
    Spinner spinner_route_amount;
    LinearLayout ll_to, ll_from;
    ImageButton ib_swap;
    Button btn_find_road;
    TextView tv_from, tv_to;

//    Các biến hỗ trợ trong map
    Address from, to;
    GoogleMap map;
    Marker start, end;
    Polyline polyline;
    Bitmap ic_walk, ic_dest;

//   Đây là một launcher được dùng để lấy address từ điểm đi,
//   launcher này được sử dụng thay thế cho onActivityResult
//   hàm xử lý nhập address với type from sẽ được gọi
    ActivityResultLauncher<Intent> launcherFrom = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        from = (Address) result.getData()
                                .getSerializableExtra("address");
                        handleAddress(AddressResultType.FROM);
                    }
                }
            }
    );

    //   Đây là một launcher được dùng để lấy address từ điểm đến,
//   launcher này được sử dụng thay thế cho onActivityResult
//   hàm xử lý nhập address với type to sẽ được gọi
    ActivityResultLauncher<Intent> launcherTo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        to = (Address) result.getData()
                                .getSerializableExtra("address");
                        handleAddress(AddressResultType.TO);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_road);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(R.string.find_road);

        initMap();
        initUI();
        initListener();
    }

    private void initMap() {
//      2 icon là ic_walk và ic_dest lần lượt biểu diễn cho vị trí người dùng và vị trí đến
        Drawable ic = getDrawable(R.drawable.ic_walk_map_focus);
        int width = ic.getIntrinsicWidth();
        int height = ic.getIntrinsicHeight();
        ic_walk = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_walk));

        ic = getDrawable(R.drawable.ic_destination_focus);
        width = ic.getIntrinsicWidth();
        height = ic.getIntrinsicHeight();
        ic_dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_dest));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fm_map);
        mapFragment.getMapAsync(this);
    }

//    Ánh xạ các view của layoute
    private void initUI() {
        btn_find_road = findViewById(R.id.btn_find_road);
        ib_swap = findViewById(R.id.ib_swap);
        ll_from = findViewById(R.id.ll_from);
        ll_to = findViewById(R.id.ll_to);
        tv_from = findViewById(R.id.tv_from);
        tv_to = findViewById(R.id.tv_to);

//        spinner route amount sẽ lấy từ người dùng số lượng tuyến tối đa mà người dùng muốn đi
        spinner_route_amount = findViewById(R.id.spinner_route_amount);
        List<String> amount_route = Arrays.asList(getResources().getStringArray(R.array.amount_route));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.route_amount_item, R.id.tv_item, amount_route);
        spinner_route_amount.setAdapter(adapter);
    }

//    Bắt các sự kiện
    private void initListener() {
//        Khi bấm vào button swap địa chỉ thì sẽ gọi hàm đổi địa chỉ là swapAddress
        ib_swap.setOnClickListener(v -> {
            swapAddress();
        });
//        Khi bấm vào vị trí điểm đi thì sẽ gọi hàm lấy địa chỉ với type là from
        ll_from.setOnClickListener(v -> {
            getAddress(AddressResultType.FROM);
        });
//        Khi bấm vào vị trí điểm đến thì sẽ gọi hàm lấy địa chỉ với type là to
        ll_to.setOnClickListener(v -> {
            getAddress(AddressResultType.TO);
        });
//        Khi bấm tìm đường thì sẽ chuyển sang activity ResultFindRoadActivity
//        Kèm với địa điểm của điểm bắt đầu đi, điểm kết thúc và số lượng tuyến tối đa muốn
        btn_find_road.setOnClickListener(v -> {
            if (null != from && null != to) {
                Intent intent = new Intent(this, ResultFindRoadActivity.class);
                intent.putExtra("from", from);
                intent.putExtra("to", to);
                intent.putExtra("route_amount", spinner_route_amount.getSelectedItemPosition());
                startActivity(intent);
            }
        });
    }

//    Chọn launcher cho đúng với type
    private void getAddress(AddressResultType type) {
        Intent intent = new Intent(this, AddressSearchActivity.class);
        if (AddressResultType.FROM == type) {
            launcherFrom.launch(intent);
        } else {
            launcherTo.launch(intent);
        }
    }

//    Hàm được gọi sau khi address được trả về
//    Sau đó hàm update sẽ được gọi để show marker
    private void handleAddress(AddressResultType type) {
        if (type == AddressResultType.FROM) {
            tv_from.setText(from.getAddress());
        } else {
            tv_to.setText(to.getAddress());
            update();
        }
        update();
    }

//    hàm sẽ tiến hàn đổi chỗ address và đổi marker và text
    private void swapAddress() {
        Address adr = to;
        to = from;
        from = adr;
        update();
        setAddress();
    }

//    Hàm update sẽ update marker và gọi hàm drawline để vẽ đường nối giữa 2 điểm
    private void update() {
        boolean moveStart = false;
//        Nếu điểm đi mà khác null thì sẽ kiểm tra xem marker start đã được khởi tạo chưa
        if (from != null) {
//        Nếu rồi thì chỉ cần set lại vị trí của nó cho đúng với from
//        Còn nếu chưa thì tạo mới một marker với icon là ic_walk
            if (start != null) {
                start.setPosition(new LatLng(from.getLat(), from.getLng()));
                start.setVisible(true);
            } else {
                start = map.addMarker(new MarkerOptions().
                        position(new LatLng(from.getLat(), from.getLng()))
                        .icon(BitmapDescriptorFactory.fromBitmap(ic_walk)));
            }
//            Khi đó thì đặt giá trj move start thành true và di chuyển camera về phía điểm bắt đầu
            moveStart = true;
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(start.getPosition(), 15));
        } else {
//            Ngược lại và nếu start lại khác null: xảy ra khi người dùng đã pick điểm đến mà chưa
//            pick điểm đi. Và sau đó người dùng swap điểm đến và điểm đi
//            Lúc này ta chiir cần ẩn marker start đi
            if (start != null) {
                start.setVisible(false);
            }
        }
//        tương tự với điểm đi thì điểm đến cũng tương tự, có điều là nếu move start bằng false
//        nghĩa là marker start không có thì mới quay camera về phía điểm cuối.
        if (to != null) {
            if (end != null) {
                end.setPosition(new LatLng(to.getLat(), to.getLng()));
                end.setVisible(true);
            } else {
                end = map.addMarker(new MarkerOptions().
                        position(new LatLng(to.getLat(), to.getLng()))
                        .icon(BitmapDescriptorFactory.fromBitmap(ic_dest)));
            }
            if (!moveStart) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(end.getPosition(), 15));
            }
        } else {
            if (end != null) {
                end.setVisible(false);
            }
        }
        drawLine();
    }

//    Hàm này sẽ có chức năng cập nhật giá trị của biến from và to lên textview
    private void setAddress() {
        if (from != null) {
            tv_from.setText(from.getAddress());
        } else {
            tv_from.setText("");
        }
        if (to != null) {
            tv_to.setText(to.getAddress());
        } else {
            tv_to.setText("");
        }
    }

//    Hàm này vẽ 1 đường thẳng trực tiếp từ điểm bắt đầu đến điểm kết đến
    private void drawLine() {
        if (from != null && to != null) {
            LatLng x = new LatLng(from.getLat(), from.getLng());
            LatLng y = new LatLng(to.getLat(), to.getLng());
            if (polyline == null) {
                polyline = map.addPolyline(new PolylineOptions()
                        .add(x, y)
                        .color(getColor(R.color.orange)));
            } else {
                List<LatLng> latLngs = new ArrayList<>();
                latLngs.add(x);
                latLngs.add(y);
                polyline.setPoints(latLngs);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.85075361772994, 106.77124465290879), 15));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}