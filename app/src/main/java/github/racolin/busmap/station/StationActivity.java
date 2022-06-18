package github.racolin.busmap.station;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.data.RouteDAO;
import github.racolin.busmap.data.SavedStationDAO;
import github.racolin.busmap.entities.Route;
import github.racolin.busmap.entities.Station;
import github.racolin.busmap.entities.User;
import github.racolin.busmap.entities.UserAccount;
import github.racolin.busmap.route.RouteAdapter;

public class StationActivity extends AppCompatActivity {
    RecyclerView rv_routes;
    Station station;
    TextView tv_name, tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getData();

        initUI();
        initListener();
    }

    public void getData() {
        Intent intent = getIntent();
        station = (Station) intent.getSerializableExtra("station");
    }

    private void initListener() {
    }

    private void initUI() {
        tv_address = findViewById(R.id.tv_address);
        tv_address.setText(station.getAddress().getAddress());
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(station.getName());

        rv_routes = findViewById(R.id.rv_routes);
        RouteAdapter adapter = new RouteAdapter(this, getRoutesFromBusStop());
        rv_routes.setAdapter(adapter);
        rv_routes.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<Route> getRoutesFromBusStop() {
        return RouteDAO.getRoutesFromStationId(this, station.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_saved, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_saved:
                onSavedStation();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void onSavedStation() {
        User user = UserAccount.getUser();
        if (user != null) {
            String email = user.getEmail();
            SavedStationDAO.insertSavedStation(this, email, station.getId());
        }
        Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
    }
}