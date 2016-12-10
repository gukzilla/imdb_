package ru.gukzilla.imdb.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.gukzilla.imdb.LazySearch;
import ru.gukzilla.imdb.R;
import ru.gukzilla.imdb.activities.VideoActivity;
import ru.gukzilla.imdb.api.Api;
import ru.gukzilla.imdb.api.RestClient;
import ru.gukzilla.imdb.custom_views.ImgView;
import ru.gukzilla.imdb.models.Const;
import ru.gukzilla.imdb.models.Video;
import ru.gukzilla.imdb.store.CacheStorage;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class FragmentSearch extends Fragment {

    private Api api;
    private ListAdapter listAdapter;
    private CacheStorage cacheStorage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View parent = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_search, container, false);

        api = new Api();
        listAdapter = new ListAdapter();
        cacheStorage = new CacheStorage(getActivity());

        final ListView searchListViewId = (ListView) parent.findViewById(R.id.searchListViewId);
        searchListViewId.setEmptyView(parent.findViewById(R.id.emptyLayout));
        searchListViewId.setAdapter(listAdapter);
        listAdapter.update(cacheStorage.getSavedVideoList());

        final View progressId = parent.findViewById(R.id.progressId);

        searchListViewId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListAdapter listAdapter = (ListAdapter) adapterView.getAdapter();
                Video video = listAdapter.getItem(i);
                VideoActivity.open(getActivity(), video.getImdbID());
            }
        });

        final LazySearch lazySearch = new LazySearch();
        final LazySearch.SearchCallBack searchCallBack = new LazySearch.SearchCallBack() {
            @Override
            public void onFinished(final String lastText) {
                api.searchAsync(lastText, new Api.SearchListener() {
                    @Override
                    public void onResult(List<Video> films) {
                        listAdapter.update(films);
                        cacheStorage.save(Const.searchText, lastText);
                        cacheStorage.saveVideoList(films);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressId.setVisibility(View.INVISIBLE);
                        searchListViewId.setVisibility(View.VISIBLE);
                        toastNothingFound();
                    }

                    @Override
                    public void onComplete() {
                        progressId.setVisibility(View.INVISIBLE);
                        searchListViewId.setVisibility(View.VISIBLE);
                    }
                });
            }
        };

        EditText searchId = (EditText) parent.findViewById(R.id.searchId);
        searchId.setText(cacheStorage.getString(Const.searchText));
        searchId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                progressId.setVisibility(View.VISIBLE);
                searchListViewId.setVisibility(View.INVISIBLE);

                lazySearch.search(charSequence.toString(), searchCallBack);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return parent;
    }

    private void toastNothingFound() {
        Toast.makeText(getActivity(), R.string.nothingFound, Toast.LENGTH_SHORT).show();
    }

    private static class ViewHolder {
        TextView titleId;
        TextView yearId;
        ImgView imgId;
        TextView typeId;

    }

    private class ListAdapter extends BaseAdapter {

        LayoutInflater inf;
        List<Video> videoLists = new ArrayList<>();
        ListAdapter() {
            inf = getActivity().getLayoutInflater();
        }

        void update(List<Video> videoLists) {
            this.videoLists = videoLists;
            notifyDataSetChanged();
        }

        ArrayList<Video> getVideoList() {
            return new ArrayList<>(videoLists);
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
