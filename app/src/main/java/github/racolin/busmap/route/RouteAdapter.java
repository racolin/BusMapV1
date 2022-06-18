package github.racolin.busmap.route;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.data.SavedRouteDAO;
import github.racolin.busmap.entities.Route;
import github.racolin.busmap.entities.User;
import github.racolin.busmap.entities.UserAccount;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteHolder> {
    private Context context;
    private List<Route> routes;
    private boolean saved;

//    Adapter nhận vào một list route sau đó show lên
    public RouteAdapter(Context context, List<Route> routes, boolean saved) {
        this.context = context;
        this.routes = routes;
        this.saved = saved;
    }

//    Adapter nhận vào một list route sau đó show lên
    public RouteAdapter(Context context, List<Route> routes) {
        this.context = context;
        this.routes = routes;
        this.saved = false;
    }

//    Cập nhật routes mới và gọi hàm notifyDataSetChanged() để thay đổi adapter
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RouteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RouteHolder(LayoutInflater.from(context).inflate(R.layout.route, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RouteHolder holder, int position) {
        holder.tv_id.setText(routes.get(position).getId());
        holder.tv_name.setText(routes.get(position).getName());
        holder.tv_time.setText(routes.get(position).getOperation_time());
        holder.tv_money.setText(routes.get(position).getMoney());
        if (saved) {
            holder.ib_saved.setBackgroundResource(R.drawable.ic_delete);
            holder.ib_saved.setOnClickListener(v -> {
                deleteRoute(position);
            });
        } else {
            holder.ib_saved.setOnClickListener(v -> {
                saveRoute(position);
            });
        }
        holder.itemView.setOnClickListener(v -> {
            toRouteDetail(position);
        });
    }

    private void toRouteDetail(int position) {
        Intent intent = new Intent(context, RouteDetailActivity.class);
        intent.putExtra("route", routes.get(position));
        context.startActivity(intent);
    }

    private void deleteRoute(int position) {
        User user = UserAccount.getUser();
        if (user != null) {
            String email = user.getEmail();
            SavedRouteDAO.deleteSavedRoute(context, email, routes.get(position).getId());
            routes.remove(position);
            notifyDataSetChanged();
        }
        Toast.makeText(context, R.string.deleted_toast, Toast.LENGTH_SHORT).show();
    }

    private void saveRoute(int position) {
        User user = UserAccount.getUser();
        if (user != null) {
            String email = user.getEmail();
            SavedRouteDAO.insertSavedRoute(context, email, routes.get(position).getId());
        }
        Toast.makeText(context, R.string.saved_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return routes == null ? 0 : routes.size();
    }

    public class RouteHolder extends RecyclerView.ViewHolder {
        TextView tv_id, tv_name, tv_time, tv_money;
        ImageButton ib_saved;
        public RouteHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_money = itemView.findViewById(R.id.tv_money);
            tv_name = itemView.findViewById(R.id.tv_name);
            ib_saved = itemView.findViewById(R.id.ib_saved);
        }
    }
}
