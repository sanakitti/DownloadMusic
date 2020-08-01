package sn.free.music.downloadmusic.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sn.free.music.downloadmusic.R;
import sn.free.music.downloadmusic.model.ItemMusicDownloaded;

public class AdapterMusicDownloaded extends RecyclerView.Adapter<AdapterMusicDownloaded.ViewHolder> {
    private ArrayList<ItemMusicDownloaded> arrayList;
    private LayoutInflater inflater;
    private Context context;
    private ClickCallBack callBack;

    public void setCallBack(ClickCallBack callBack) {
        this.callBack = callBack;
    }

    public AdapterMusicDownloaded(ArrayList<ItemMusicDownloaded> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_music_downloaded, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        ItemMusicDownloaded itemMusicDownloaded = arrayList.get(i);
        viewHolder.binData(itemMusicDownloaded);
        if (callBack != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.clickItemMusic(i);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callBack.clickOptionItemMusic(i, viewHolder.imgOption);
                    return false;
                }
            });
            viewHolder.imgOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.clickOptionItemMusic(i, viewHolder.imgOption);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDate, tvDuration;
        private ImageView imgOption;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            imgOption = itemView.findViewById(R.id.img_option);

        }

        public void binData(ItemMusicDownloaded itemMusicDownloaded) {
            tvName.setText(itemMusicDownloaded.getTitle());
            tvDate.setText(getLastModifiedString(itemMusicDownloaded.getPubDate()));

            int seconds = (int) (itemMusicDownloaded.getDuration() / 1000) % 60;
            int minutes = (int) ((itemMusicDownloaded.getDuration() / (1000 * 60)) % 60);
            String time = minutes + ":" + seconds;
            tvDuration.setText(time);
        }
    }

    public interface ClickCallBack {
         void clickItemMusic(int postion);
         void clickOptionItemMusic(int postion, View view);
    }

    public String getLastModifiedString(Date lastModified) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(lastModified);
    }
}
