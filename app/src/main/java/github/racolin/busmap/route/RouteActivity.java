package github.racolin.busmap.route;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import github.racolin.busmap.R;
import github.racolin.busmap.data.RouteDAO;

//Route activity gồm một list các tuyến xe buýt và có thanh tìm kiếm
public class RouteActivity extends AppCompatActivity {
    EditText edt_route;
    RouteListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(R.string.pick_route);

        initUI();
        initListener();
    }

    private void initListener() {
//        Bắt sự kiện change text và với mỗi text thay đổi thì sẽ gọi hàm filterRoute
        edt_route.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterRoute(editable.toString());
            }
        });
    }

//    Hàm này lấy key được nhận vào ở thanh tìm kiếm và gọi -> fragment -> adapter để lọc các route phù hợp
    private void filterRoute(String key) {
        fragment.filterRoute(key);
    }

    private void initUI() {
        edt_route = findViewById(R.id.edt_route);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = new RouteListFragment(RouteDAO.getAllRoutes(this), false);
        transaction.add(R.id.fm_routes, fragment);
        transaction.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}