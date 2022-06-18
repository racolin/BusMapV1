package github.racolin.busmap.result;

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
import github.racolin.busmap.component.BusStopGuide;
import github.racolin.busmap.listener.OnBusStopListener;

// Fragment có recyclerview là một list các bus stops guide
public class ResultBusStopsGuideFragment extends Fragment {
    List<BusStopGuide> bus_stop_guides;
    OnBusStopListener listener;

    public ResultBusStopsGuideFragment() {

    }

    public ResultBusStopsGuideFragment(List<BusStopGuide> bus_stop_guides, OnBusStopListener listener) {
        this.bus_stop_guides = bus_stop_guides;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear, container, false);
        if (bus_stop_guides != null) {
            RecyclerView rv = view.findViewById(R.id.rv_linear);
            ResultBusStopGuideAdapter adapter = new ResultBusStopGuideAdapter(getContext(), bus_stop_guides, listener);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        return view;
    }
}
