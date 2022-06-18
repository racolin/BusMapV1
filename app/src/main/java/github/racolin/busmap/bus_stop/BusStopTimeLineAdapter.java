package github.racolin.busmap.bus_stop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.Support;

//Adapter nhận vào một context và một list các localtime
//sau đó inflate R.layout.time_line và set text cho time line
public class BusStopTimeLineAdapter extends RecyclerView.Adapter<BusStopTimeLineAdapter.TimeLineHolder> {
    List<LocalTime> time_lines;
    Context context;
//constructor của adapter
    public BusStopTimeLineAdapter(Context context, List<LocalTime> time_lines) {
        this.time_lines = time_lines;
        this.context = context;
    }
    @NonNull
    @Override
    public TimeLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        inflate time_line layout
        return new TimeLineHolder(LayoutInflater.from(context).inflate(R.layout.time_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineHolder holder, int position) {
//        set text cho btn_time
        holder.btn_time.setText(Support.timeToString(time_lines.get(position)));
        holder.btn_time.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return time_lines == null ? 0 : time_lines.size();
    }

    public class TimeLineHolder extends RecyclerView.ViewHolder {
        Button btn_time;
        public TimeLineHolder(@NonNull View itemView) {
            super(itemView);
            btn_time = itemView.findViewById(R.id.btn_time);
        }
    }

}
