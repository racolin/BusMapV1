package github.racolin.busmap.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.component.RouteGuide;
import github.racolin.busmap.config.MoveType;
import github.racolin.busmap.listener.OnRouteListener;

//ResultBusStopGuideAdapter là adapter để hiện thị list các routes guide
public class ResultRouteGuideAdapter extends RecyclerView.Adapter<ResultRouteGuideAdapter.RouteGuideHolder> {
    Context context;
    List<RouteGuide> route_guides;
    OnRouteListener listener;

    public ResultRouteGuideAdapter(Context context, List<RouteGuide> route_guides, OnRouteListener listener) {
        this.context = context;
        this.route_guides = route_guides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RouteGuideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RouteGuideHolder(LayoutInflater.from(context).inflate(R.layout.route_result_guide, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RouteGuideHolder holder, int position) {
        if (route_guides.get(position).getType() == MoveType.BUS) {
//            Đặt icon bus
//            Nếu type là BUS thì sẽ hiển thị ll_price lên để hiển thị giá của chuyến
            holder.iv_icon.setImageResource(R.drawable.ic_bus);
            holder.tv_money.setText(route_guides.get(position).getPrice());
            holder.ll_price.setVisibility(View.VISIBLE);
        } else {
//            Đặt icon walk
//            ẩn giá tiền đi
            holder.ll_price.setVisibility(View.GONE);
            holder.iv_icon.setImageResource(R.drawable.ic_walk);
        }
        holder.tv_title.setText(route_guides.get(position).getTitle());
        holder.tv_description.setText(route_guides.get(position).getDescription());
        holder.tv_time_pass.setText(route_guides.get(position).getTimePass());
        holder.tv_distance.setText(route_guides.get(position).getDistanceMeter());
        holder.itemView.setOnClickListener(v -> {
//            Khi click vào item thì sẽ gọi hàm setOnRouteClickListener để thực hiện focus trên bản đồ
            listener.setOnRouteClickListener(position);
        });
    }

    @Override
    public int getItemCount() {
        return route_guides == null ? 0 : route_guides.size();
    }

    public class RouteGuideHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_price;
        TextView tv_description, tv_title, tv_money, tv_time_pass, tv_distance;
        ImageView iv_icon;

        public RouteGuideHolder(@NonNull View itemView) {
            super(itemView);
            ll_price = itemView.findViewById(R.id.ll_price);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_money = itemView.findViewById(R.id.tv_money);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_time_pass = itemView.findViewById(R.id.tv_time_pass);
            tv_distance = itemView.findViewById(R.id.tv_distance);
        }
    }
}
