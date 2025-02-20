package tdp.bikum.newtube.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tdp.bikum.newtube.R;
import tdp.bikum.newtube.VideoPlayerActivity;
import tdp.bikum.newtube.models.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private List<Video> videoList;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(itemView, videoList);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.videoTitleTextView.setText(video.getTitle());
        holder.videoDescriptionTextView.setText(video.getDescription());

        Picasso.get()
                .load(video.getThumbnail_url())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.videoThumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void setVideoList(List<Video> newList) {
        this.videoList = newList;
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnailImageView;
        TextView videoTitleTextView;
        TextView videoDescriptionTextView;
        List<Video> videoList;

        public VideoViewHolder(@NonNull View itemView, List<Video> videoList) {
            super(itemView);
            this.videoList = videoList;
            videoThumbnailImageView = itemView.findViewById(R.id.videoThumbnailImageView);
            videoTitleTextView = itemView.findViewById(R.id.videoTitleTextView);
            videoDescriptionTextView = itemView.findViewById(R.id.videoDescriptionTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Video video = videoList.get(position);
                        Context context = itemView.getContext();
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra("VIDEO_URL", video.getVideo_url()); // Still passing video_url from backend
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}