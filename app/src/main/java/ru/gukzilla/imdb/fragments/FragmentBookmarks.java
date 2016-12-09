package ru.gukzilla.imdb.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.gukzilla.imdb.R;
import ru.gukzilla.imdb.activities.VideoActivity;
import ru.gukzilla.imdb.api.Api;
import ru.gukzilla.imdb.api.RestClient;
import ru.gukzilla.imdb.custom_views.ImgView;
import ru.gukzilla.imdb.models.FullVideo;
import ru.gukzilla.imdb.models.Video;
import ru.gukzilla.imdb.store.BookMarksStorage;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class FragmentBookmarks extends Fragment {

    private final int REQUEST_CODE = 10;
    private Api api;
    private ListAdapter listAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        api = new Api();

        View parent = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_bookmarks, container, false);

        final ListView bookmarksListViewId = (ListView) parent.findViewById(R.id.bookmarksListViewId);
        listAdapter = new ListAdapter();
        bookmarksListViewId.setAdapter(listAdapter);

        bookmarksListViewId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListAdapter listAdapter = (ListAdapter) adapterView.getAdapter();
                Video video = listAdapter.getItem(i);
                Intent intent = VideoActivity.getIntentForResult(getActivity(), video.getImdbID());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return parent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE:
                listAdapter.update();
                break;
        }
    }

    private static class ViewHolder {
        TextView titleId;
        TextView yearId;
        ImgView imgId;
        TextView typeId;

    }

    private class ListAdapter extends BaseAdapter {

        LayoutInflater inf;
        List<FullVideo> videoLists = new ArrayList<>();
        BookMarksStorage bookMarksStorage;
        ListAdapter() {
            inf = getActivity().getLayoutInflater();
            bookMarksStorage = new BookMarksStorage(getActivity());
            update();
        }

        public void update() {
            videoLists = bookMarksStorage.getAllBookmarks();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return videoLists.size();
        }

        @Override
        public Video getItem(int i) {
            return videoLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final ViewHolder holder;
            if(view == null) {
                holder = new ViewHolder();
                view = inf.inflate(R.layout.item_search_film, viewGroup, false);
                holder.titleId = (TextView) view.findViewById(R.id.titleId);
                holder.yearId = (TextView) view.findViewById(R.id.yearId);
                holder.typeId = (TextView) view.findViewById(R.id.typeId);
                holder.imgId = (ImgView) view.findViewById(R.id.imgId);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Video videoList = getItem(i);
            holder.titleId.setText(videoList.getTitle());
            holder.yearId.setText(videoList.getYear());
            holder.typeId.setText(videoList.getType());

            holder.imgId.setImageBitmap(null);
            if(videoList.getPoster() == null) {
                holder.imgId.setVisibility(View.GONE);
            } else {
                api.downloadBitmap(videoList.getPoster(), new RestClient.BitmapListener() {
                    @Override
                    public void onResult(Bitmap bitmap) {
                        if(bitmap != null) {
                            holder.imgId.setImageBitmap(bitmap);
                        } else {
                            holder.imgId.setVisibility(View.GONE);
                        }
                    }
                });
            }

            return view;
        }
    }
}
