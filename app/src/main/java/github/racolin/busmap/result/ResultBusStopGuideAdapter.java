package github.racolin.busmap.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.component.BusStopGuide;
import github.racolin.busmap.config.MoveType;
import github.racolin.busmap.listener.OnBusStopListener;

//ResultBusStopGuideAdapter là adapter để hiện thị list các bus stops guide
public class ResultBusStopGuideAdapter extends RecyclerView.Adapter<ResultBusStopGuideAdapter.BusStopGuideHolder> {
    Context context;
    List<BusStopGuide> bus_stop_guides;
    BusStopGuideHolder preHolder = null;
    OnBusStopListener listener;

    public ResultBusStopGuideAdapter(Context context, List<BusStopGuide> bus_stop_guides, OnBusStopListener listener) {
        this.context = context;
        this.bus_stop_guides = bus_stop_guides;
        this.listener = listener;
    }
    @NonNull
    @Override
    public BusStopGuideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BusStopGuideHolder(LayoutInflater.from(context).inflate(R.layout.bus_stop_result, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull BusStopGuideHolder holder, int position) {
        if (bus_stop_guides.get(position).getType() == MoveType.BUS) {
//            Nếu bus stop guide có type là BUS và có bus stop guide liền trước nó là WALK hoặc là route id phía trước nó khác nó
//            Thì sẽ ẩn đi đường phía trên
            if (position > 0 && (bus_stop_guides.get(position - 1).getType() == MoveType.WALK
                    || !bus_stop_guides.get(position - 1).getRoute_id().equals(bus_stop_guides.get(position).getRoute_id()))) {
                holder.line_vertical_end.setVisibility(View.INVISIBLE);
                holder.order_first.setVisibility(View.VISIBLE);
            }
//            Nếu bus stop guide có type là BUS và có bus stop guide sau nó là WALK hoặc là route id của phía sau nó khác nó
//            Thì sẽ ẩn đi đường phía dưới
            if (position < bus_stop_guides.size() - 1 && (bus_stop_guides.get(position + 1).getType() == MoveType.WALK
                    || !bus_stop_guides.get(position + 1).getRoute_id().equals(bus_stop_guides.get(position).getRoute_id()))) {
                holder.line_vertical_first.setVisibility(View.INVISIBLE);
                holder.order_first.setVisibility(View.VISIBLE);
            }
        } else {
//            Nếu là WALK thì mặc định ẩn 2 đường line đi và hiển thị order black
            holder.order_black.setVisibility(View.VISIBLE);
            holder.line_vertical_first.setVisibility(View.INVISIBLE);
            holder.line_vertical_end.setVisibility(View.INVISIBLE);
        }

        holder.tv_route.setText(bus_stop_guides.get(position).getRoute_id());
        holder.tv_name.setText(bus_stop_guides.get(position).getName(33));

        holder.itemView.setOnClickListener(v -> {
//            Khi click vào item thì sẽ gọi hàm setOnBusStopClickListener để thực hiện focus trên bản đồ
            listener.setOnBusStopClickListener(position);

//            Khi click vào item thì  thực hiện focus item đó
            holder.order.setVisibility(View.VISIBLE);
            if (preHolder != null && preHolder != holder) {
                preHolder.order.setVisibility(View.INVISIBLE);
            }
            preHolder = holder;
        });
    }
    
    @Override
    public int getItemCount() {
        return bus_stop_guides == null ? 0 : bus_stop_guides.size();
    }

    public class BusStopGuideHolder extends RecyclerView.ViewHolder {
        View line_vertical_first, line_vertical_end, order_first, order_black, order;
        TextView tv_route, tv_name;
        public BusStopGuideHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_route = itemView.findViewById(R.id.tv_route);
            order = itemView.findViewById(R.id.order);
            order_black = itemView.findViewById(R.id.order_black);
            order_first = itemView.findViewById(R.id.order_first);
            line_vertical_end = itemView.findViewById(R.id.line_vertical_end);
            line_vertical_first = itemView.findViewById(R.id.line_vertical_first);
        }
    }
}
