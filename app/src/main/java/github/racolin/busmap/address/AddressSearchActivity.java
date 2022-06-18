package github.racolin.busmap.address;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.data.AddressDAO;
import github.racolin.busmap.entities.Address;
import github.racolin.busmap.entities.User;
import github.racolin.busmap.entities.UserAccount;
import github.racolin.busmap.listener.OnAddressListener;

// Sử dụng Places API của google để tư động gợi ý địa điểm khi nười dùng nhập
public class AddressSearchActivity extends AppCompatActivity implements OnAddressListener {
//    Recyclerview là một list các address mà người dùng đã tìm kiếm trước đây
    RecyclerView rv_addresses;
    AddressAdapter adapter;
    List<Address> addresses;
//    user để kiểm tra xem người dùng đã đăng nhập hay chưa
    User user;
//    Đây chính là API cần dùng
    PlacesClient placesClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        initPlaces();
        initUI();
    }

    private void initPlaces() {
        Places.initialize(getApplicationContext(), getString(R.string.apiKey));
        placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                returnResult(place.getName(), place.getLatLng());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("AddressSearchActivity", "An error occurred: " + status);
            }
        });
    }

    private void initUI() {
//        Lấy user
        user = UserAccount.getUser();
        if (user != null) {
//            Nếu đã đăng nhập thì load list addresses người dùng đã tìm kiếm
            addresses = AddressDAO.getAddressesByUserEmail(this, user.getEmail());
        } else {
//            Nếu chưa đăng nhập thì list này rỗng
            addresses = new ArrayList<>();
        }
//        Tạo một list các address đã nhập trước đây
        rv_addresses = findViewById(R.id.rv_addresses);
        adapter = new AddressAdapter(this, addresses, this);
        rv_addresses.setAdapter(adapter);
        rv_addresses.setLayoutManager(new LinearLayoutManager(this));
    }

//  đảm nhận việc gửi kết quả về khi người dùng search và chọn địa chỉ
    public void returnResult(String name, LatLng latLng) {
        Address address = new Address(name, latLng.latitude, latLng.longitude);
        if (user != null && !addresses.contains(address)) {
            AddressDAO.insert(this, address, user.getEmail());
        }
        Intent intent = new Intent();
        intent.putExtra("address", address);
        setResult(RESULT_OK, intent);
        finish();
    }

//    Đảm nhận việc gửi kết quả về khi người dùng click vào một item trong recycler view
    public void returnResult(Address address) {
        Intent intent = new Intent();
        intent.putExtra("address", address);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onAddressClickListener(int position) {
        returnResult(addresses.get(position));
    }
}