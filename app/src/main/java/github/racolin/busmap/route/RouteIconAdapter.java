package github.racolin.busmap.route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.component.ResultRoute;

//Route Icon để hiển thị một list các icon ở giữa là tên tuyến
//Đầu vào là một list các result_route, với mỗi result_route sẽ là một icon
public class RouteIconAdapter extends RecyclerView.Adapter<RouteIconAdapter.RouteIconHolder> {
    List<ResultRoute> result_routes;
    Context context;

    public RouteIconAdapter(Context context, List<ResultRoute> result_routes) {
        this.result_routes = result_routes;
        this.context = context;
    }

    @NonNull
    @Override
    public RouteIconHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RouteIconHolder(LayoutInflater.from(context).inflate(R.layout.result_route, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RouteIconHolder holder, int position) {
//        Khi mà position = 0 thì sẽ ẩn đi dấu chấm ngăn cách phía trước route icon
        if (position == 0) {
            holder.v_circle.setVisibility(View.GONE);
        }
        holder.tv_id.setText(result_routes.get(position).getRoute().getId());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return result_routes.size();
    }
//  route icon holder
    public class RouteIconHolder extends RecyclerView.ViewHolder {
        TextView tv_id;
        View v_circle;
        public RouteIconHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.tv_id);
            v_circle = itemView.findViewById(R.id.v_circle);
        }
    }
}
