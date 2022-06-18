package github.racolin.busmap.bus_stop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.entities.BusStop;
import github.racolin.busmap.listener.OnBusStopListener;

public class BusStopListFragment extends Fragment {
    private boolean order;
    private List<BusStop> bus_stops;
    private List<LocalTime> time_lines;
    private OnBusStopListener listener;

    public BusStopListFragment() {
        order = false;
    }

    public BusStopListFragment(List<BusStop> bus_stops, boolean order) {
        this.bus_stops = bus_stops;
        this.order = order;
    }

//    fragment được khởi tạo bởi list bus_stops, time_lines, order (cho biết là có sắp xếp các bus_stops theo thứ tự hay không)
//    và một listener là OnBusStopListener để khi click vào một item thì ta sẽ gọi đến activity để thay đổi map
    public BusStopListFragment(List<BusStop> bus_stops, List<LocalTime> time_lines,
                               boolean order, OnBusStopListener listener) {
        this.bus_stops = bus_stops;
        this.time_lines = time_lines;
        this.order = order;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2_linear, container, false);
//        Tiến hành nạp data vào 2 adapter cho 2 recycler view
        if (bus_stops != null && bus_stops.size() > 0) {
            RecyclerView rv_linear_2 = view.findViewById(R.id.rv_linear_2);
//            Ta truyền listener vào adapter để khi click vào item thì nó sẽ cập nhật map ở activity
            BusStopAdapter adapter_2 = new BusStopAdapter(getContext(), bus_stops, order, listener);
            rv_linear_2.setAdapter(adapter_2);
            rv_linear_2.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        if (time_lines != null && time_lines.size() > 0) {
            RecyclerView rv_linear_1 = view.findViewById(R.id.rv_linear_1);
            BusStopTimeLineAdapter adapter_1 = new BusStopTimeLineAdapter(getContext(), time_lines);
            rv_linear_1.setAdapter((RecyclerView.Adapter) adapter_1);
            rv_linear_1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        }
        return view;
    }
}
