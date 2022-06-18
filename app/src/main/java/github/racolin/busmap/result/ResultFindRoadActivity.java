package github.racolin.busmap.result;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.Support;
import github.racolin.busmap.component.Result;
import github.racolin.busmap.entities.Address;

//  Activity sẽ show danh sách các kết quả tìm được
public class ResultFindRoadActivity extends AppCompatActivity {
//    Các view để ảnh xạ
    private RecyclerView rv_results;
    private Address from, to;
    private int route_amount;
    private TextView tv_from, tv_to;
    private ImageButton ib_swap;
    private ResultAdapter adapter;
    private List<Result> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_road_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(R.string.find_road_result);
        getData();
//      Ánh xạ view
        initUI();
//        Bắt các sự kiện
        initListener();
    }
//      Ánh xạ các view đến các view trong layout
    private void initUI() {
        tv_from = findViewById(R.id.tv_from);
        tv_to = findViewById(R.id.tv_to);
        setAddressText();

        ib_swap = findViewById(R.id.ib_swap);

//        Khởi tạo rv_results với result được tính từ hàm trong lớp support
        rv_results = findViewById(R.id.rv_results);
        results = getResults();
        adapter = new ResultAdapter(this, results, from, to);
        rv_results.setAdapter(adapter);
        rv_results.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initListener() {
//        Bắt sự kiện bấm vào swap
        ib_swap.setOnClickListener(v -> {
            swapAddress();
        });
    }

//    Lấy dữ liệu từ Activity trước đó truyền cho mà cụ thể là FinRoadActivity
    private void getData() {
        Intent intent = getIntent();
        route_amount = intent.getIntExtra("route_amount", -1);
        from = (Address) intent.getSerializableExtra("from");
        to = (Address) intent.getSerializableExtra("to");
    }

//    Swap 2 địa điểm
    private void swapAddress() {
        Address adr = to;
        to = from;
        from = adr;
        setAddressText();
        loadResult();
    }

//    Đặt lại 2 giá trị
    private void setAddressText() {
        tv_from.setText(from.getAddress());
        tv_to.setText(to.getAddress());
    }

//    Load result khi người dùng bấm swap điểm đến và điểm đi
    private void loadResult() {
        results = getResults();
        adapter.setResults(results, from, to);
    }

//    Gọi thuật toán tính ra Result
    public List<Result> getResults() {
        return Support.calculateResults(this, from, to, route_amount);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}