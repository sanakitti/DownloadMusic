package sn.free.music.downloadmusic.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import sn.free.music.downloadmusic.R;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder> {
    private ArrayList<String> arrayList;
    private LayoutInflater inflater;
    private Context context;
    private CallBackClickEvent callBack;
    public void setCallBack(CallBackClickEvent callBack) {
        this.callBack = callBack;
    }
    public AdapterSearch(ArrayList<String> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_suggestion_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String string = arrayList.get(position);
        holder.binData(string);
        if (callBack != null) {
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.clickImageSearch(position);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.clickItemSearch(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView tvSearch;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSearch = itemView.findViewById(R.id.suggest);
            img = itemView.findViewById(R.id.put_in_search_box);
        }

        public void binData(String string) {
            tvSearch.setText(string);

        }
    }

    public interface CallBackClickEvent {
        void clickItemSearch(int postion);

        void clickImageSearch(int position);
    }
}
