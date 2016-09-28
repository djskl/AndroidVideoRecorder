package cnic.sdc.videorecorder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class VideoItemFragment extends Fragment {

    public static final List<VideoItem> ITEMS = new ArrayList<>();

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static int mColumnCount = 3;
    private OnListFragmentInteractionListener mListener;
    private VideoItemRecyclerViewAdapter mVideoAdapter;

    public VideoItemFragment() {
        if(ITEMS.size()==0){
            ITEMS.add(new VideoItem(ItemType.BUTTON, null));
        }else{
            ITEMS.set(0, new VideoItem(ItemType.BUTTON, null));
        }
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static VideoItemFragment newInstance() {
        VideoItemFragment fragment = new VideoItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, mColumnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videoitem_grid, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(mVideoAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
            mVideoAdapter = new VideoItemRecyclerViewAdapter(this.ITEMS, mListener);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addItem(VideoItem item){
        this.ITEMS.add(item);
        this.mVideoAdapter.notifyDataSetChanged();
    }

    public interface OnListFragmentInteractionListener {
        void onFragmentClick(VideoItem item);
        void onFragmentLongClick(VideoItem item);
    }
}
