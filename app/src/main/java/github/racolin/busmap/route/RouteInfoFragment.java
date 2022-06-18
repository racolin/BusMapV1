package github.racolin.busmap.route;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import github.racolin.busmap.R;
import github.racolin.busmap.entities.Route;

//Fragment Route info được sử dụng bởi viewpager2 ở phần detail route
//Nếu mà route == null thì sẽ inflate layout not found
//Nếu ngược lại thì sẽ inflate layout route info và đặt các thông tin route vào
public class RouteInfoFragment extends Fragment {
    Route route;

    public RouteInfoFragment() {

    }

    public RouteInfoFragment(Route route) {
        this.route = route;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (route == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.not_found_layout, container, false);
        } else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.info_route, container, false);
            ((TextView) view.findViewById(R.id.tv_id)).setText(route.getId());
            ((TextView) view.findViewById(R.id.tv_name)).setText(route.getName());
            ((TextView) view.findViewById(R.id.tv_operating)).setText(route.getOperation_time());
            ((TextView) view.findViewById(R.id.tv_price)).setText(route.getMoney());
            ((TextView) view.findViewById(R.id.tv_type)).setText(route.getType());
            ((TextView) view.findViewById(R.id.tv_time_run)).setText(route.getCycle_time());
            ((TextView) view.findViewById(R.id.tv_repeat)).setText(route.getRepeatTime());
            ((TextView) view.findViewById(R.id.tv_amount)).setText(route.getPerDay());
            ((TextView) view.findViewById(R.id.tv_unit)).setText(route.getUnit());
        }
        return view;
    }
}
