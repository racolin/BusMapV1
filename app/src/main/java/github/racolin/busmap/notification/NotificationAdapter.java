package github.racolin.busmap.notification;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.Support;
import github.racolin.busmap.component.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    List<Notification> notifications;
    Context context;

//    adapter nhận đầu vào là context và list notification
    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationHolder(LayoutInflater.from(context).inflate(R.layout.notification, parent, false));
    }
//  Nhét dữ liệu vào cho các item
    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        holder.tv_title.setText(notifications.get(position).getTitle());
        holder.tv_description.setText(notifications.get(position).getDescription());
        holder.iv_icon.setImageResource(notifications.get(position).getImage());
        holder.tv_time.setText(Support.dateToString(notifications.get(position).getTime(), "dd/MM/yyyy HH:mm"));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NotificationDetailActivity.class);
            intent.putExtra("content", notifications.get(position).getContent());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

//    holder cho notification
    public class NotificationHolder extends RecyclerView.ViewHolder {
        public ImageView iv_icon;
        public TextView tv_title, tv_description, tv_time;
        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
