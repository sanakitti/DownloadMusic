package sn.free.music.downloadmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import sn.free.music.downloadmusic.R;
import sn.free.music.downloadmusic.model.Song;

public class AdapterMusicSearch extends RecyclerView.Adapter<AdapterMusicSearch.ViewHolder> {
    private ArrayList<Song> arrayList;
    private LayoutInflater inflater;
    private Context context;
    private ClickCallBack callBack;

    public void setCallBack(ClickCallBack callBack) {
        this.callBack = callBack;
    }

    public AdapterMusicSearch(ArrayList<Song> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_music, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Song itemMusicSearch = arrayList.get(i);
        viewHolder.binData(itemMusicSearch);
        if (callBack != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.clickItem(i);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView tvName;
        private TextView tvAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAuthor = itemView.findViewById(R.id.tv_auth);
        }

        public void binData(Song itemMusicSearch) {
            Glide.with(context).load(itemMusicSearch.getImage()).into(imgAvatar);
            tvName.setText(itemMusicSearch.getTitle().replace("&#39;",""));
            tvAuthor.setText(itemMusicSearch.getAuTh());
        }
    }

    public interface ClickCallBack {
        void clickItem(int position);
    }
}
