package cnic.sdc.videorecorder;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cnic.sdc.videorecorder.VideoItemFragment.OnListFragmentInteractionListener;

public class VideoItemRecyclerViewAdapter extends RecyclerView.Adapter<VideoItemRecyclerViewAdapter.ViewHolder> {

    private final List<VideoItem> mVideos;
    private final OnListFragmentInteractionListener mListener;

    public VideoItemRecyclerViewAdapter(List<VideoItem> items, OnListFragmentInteractionListener listener) {
        mVideos = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_videoitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mVideos.get(position);

        switch (holder.mItem.item_type){

            case VIDEO:
                String video_path = holder.mItem.path;
                final Bitmap thumbnail = createThumbnail(video_path);
                String filename = video_path.substring(video_path.lastIndexOf("/")+1);
                if(thumbnail == null){
                    holder.mThumbnailView.setImageResource(R.drawable.thumbnail_placeholder);
                }else{
                    holder.mThumbnailView.setImageBitmap(thumbnail);
                }
                holder.mNameView.setText(filename);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            mListener.onFragmentClick(holder.mItem);
                        }
                    }
                });

                holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (null != mListener) {
                            mListener.onFragmentLongClick(holder.mItem);
                        }
                        return true;
                    }
                });

                break;

            case BUTTON:
                holder.mThumbnailView.setImageResource(R.drawable.video_recorder);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            mListener.onFragmentClick(holder.mItem);
                        }
                    }
                });
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    private Bitmap createThumbnail(String video_path){

        if(video_path == null){
            return null;
        }

        File f = new File(video_path);
        if(!f.exists()){
            return null;
        }

        return ThumbnailUtils.createVideoThumbnail(video_path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumbnailView;
        public final TextView mNameView;
        public VideoItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumbnailView = (ImageView) view.findViewById(R.id.iv_thumbnail);
            mNameView = (TextView) view.findViewById(R.id.iv_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

}
