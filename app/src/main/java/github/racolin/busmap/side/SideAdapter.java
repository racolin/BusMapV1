package github.racolin.busmap.side;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.component.Side;

// side adapter kế thừ recyclerview và vừa implement spinner adapter
// vì spinner không cho phép set adapter là recyclerview nên phải implement và override các method
public class SideAdapter extends RecyclerView.Adapter<SideAdapter.SideHolder> implements SpinnerAdapter {
    private List<Side> sides;
    private Context context;
    public SideAdapter(Context context, List<Side> sides) {
        this.sides = sides;
        this.context = context;
    }

    @NonNull
    @Override
    public SideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.side_spinner, parent, false);
        return new SideHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SideHolder holder, int position) {
        holder.tv_city_name.setText(sides.get(position).getName());
        holder.iv_country.setImageResource(sides.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return sides.size();
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.side_spinner, viewGroup, false);
        TextView tv_city_name = view.findViewById(R.id.tv_city_name);
        ImageView iv_country = view.findViewById(R.id.iv_country);
        tv_city_name.setText(sides.get(i).getName());
        iv_country.setImageResource(sides.get(i).getImage());
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return sides.size();
    }

    @Override
    public Object getItem(int i) {
        return sides.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.side_spinner, viewGroup, false);
        TextView tv_city_name = view.findViewById(R.id.tv_city_name);
        ImageView iv_country = view.findViewById(R.id.iv_country);
        tv_city_name.setText(sides.get(i).getName());
        iv_country.setImageResource(sides.get(i).getImage());
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return sides.isEmpty();
    }

    public class SideHolder extends RecyclerView.ViewHolder {
        private TextView tv_city_name;
        private ImageView iv_country;

        public SideHolder(@NonNull View itemView) {
            super(itemView);
            tv_city_name = itemView.findViewById(R.id.tv_city_name);
            iv_country = itemView.findViewById(R.id.iv_country);
        }
    }
}
