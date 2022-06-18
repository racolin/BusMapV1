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
import github.racolin.busmap.component.RouteGuide;
import github.racolin.busmap.listener.OnRouteListener;

// Fragment có recyclerview là một list các routes guide
public class ResultRouteGuideFragment extends Fragment {
    List<RouteGuide> route_guides;
    OnRouteListener listener;

    public ResultRouteGuideFragment() {

    }

    public ResultRouteGuideFragment( List<RouteGuide> route_guides, OnRouteListener listener) {
        this.route_guides = route_guides;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear, container, false);
        if (route_guides != null) {
            RecyclerView rv = view.findViewById(R.id.rv_linear);
            ResultRouteGuideAdapter adapter = new ResultRouteGuideAdapter(getContext(), route_guides, listener);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        return view;
    }
}
