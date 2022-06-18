package github.racolin.busmap.route;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.Support;
import github.racolin.busmap.entities.Route;

//Route list hiển thị danh sách các route
//được sử dụng khi dùng chức năng tra cứu tuyến đường hoặc chi tiết các route đi qua station
public class RouteListFragment extends Fragment {
    List<Route> routes;
    RouteAdapter adapter;
    boolean saved;

    public RouteListFragment() {

    }

    public RouteListFragment(List<Route> routes, boolean saved) {
        this.routes = routes;
        this.saved = saved;
    }

//    dựa vào key thì chuyển về dạng in thường và không dấu
//    Sau đó so sánh với route id và name của route cũng in thường và không dấu
    public void filterRoute(String key) {
        key = Support.covertToString(key).toLowerCase();
        List<Route> list = new ArrayList<>();
        for (Route route : routes) {
            if (Support.covertToString(route.getId() + route.getName()).toLowerCase().contains(key)) {
                list.add(route);
            }
        }
//        SetRoutes để cập nhật routes mới
        adapter.setRoutes(list);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear, container, false);
        if (routes != null && routes.size() > 0) {
//            Recycler view dùng để show list route
            RecyclerView rv_linear = view.findViewById(R.id.rv_linear);
            adapter = new RouteAdapter(getContext(), routes, saved);
            rv_linear.setAdapter(adapter);
            rv_linear.setLayoutManager(new LinearLayoutManager(getContext()));
        };
        return view;
    }
}
