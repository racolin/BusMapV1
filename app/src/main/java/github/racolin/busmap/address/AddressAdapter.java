package github.racolin.busmap.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.entities.Address;
import github.racolin.busmap.listener.OnAddressListener;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {
    Context context;
    List<Address> addresses;
    OnAddressListener listener;
// adapter nhận đầu vào là context, một list addresses và một interface để giao tiếp với activity
    public AddressAdapter(Context context, List<Address> addresses, OnAddressListener listener) {
        this.context = context;
        this.addresses = addresses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        inflate address layout
        return new AddressHolder(LayoutInflater.from(context).inflate(R.layout.address, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder holder, int position) {
//        set các text address, lat và lng
        holder.tv_address.setText(addresses.get(position).getAddress());
        holder.tv_lat.setText(String.valueOf(addresses.get(position).getLat()));
        holder.tv_lng.setText(String.valueOf(addresses.get(position).getLng()));
//        khi item được click thì sẽ gọi hàm onAddressClickListener phía activity
        holder.itemView.setOnClickListener(v -> {
            listener.onAddressClickListener(position);
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

//    holder cho address
    public class AddressHolder extends RecyclerView.ViewHolder {
        TextView tv_address, tv_lat, tv_lng;
        public AddressHolder(@NonNull View itemView) {
            super(itemView);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_lat = itemView.findViewById(R.id.tv_lat);
            tv_lng = itemView.findViewById(R.id.tv_lng);
        }
    }
}
