package github.racolin.busmap.station;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.entities.Station;

//fragment là list các stations
public class StationListFragment extends Fragment {
    private List<Station> stations;

    public StationListFragment() {

    }

    public StationListFragment(List<Station> stations) {
        this.stations = stations;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2_linear, container, false);
        if (stations != null && stations.size() > 0) {
//            Data của các station sẽ được đổ vào Recycler view
            RecyclerView rv_linear_1 = view.findViewById(R.id.rv_linear_1);
            rv_linear_1.setVisibility(View.GONE);
            RecyclerView rv_linear_2 = view.findViewById(R.id.rv_linear_2);
            StationAdapter adapter_2 = new StationAdapter(getContext(), stations);
            rv_linear_2.setAdapter(adapter_2);
            rv_linear_2.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        return view;
    }
}
