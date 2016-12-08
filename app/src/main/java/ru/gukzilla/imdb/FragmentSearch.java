package ru.gukzilla.imdb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.gukzilla.imdb.api.Api;
import ru.gukzilla.imdb.models.Film;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class FragmentSearch extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View parent = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_search, container, false);

        final Api api = new Api();

        ListView searchListViewId = (ListView) parent.findViewById(R.id.searchListViewId);
        final ListAdapter listAdapter = new ListAdapter();
        searchListViewId.setAdapter(listAdapter);

        final LazySearch lazySearch = new LazySearch();
        final LazySearch.SearchCallBack searchCallBack = new LazySearch.SearchCallBack() {
            @Override
            public void onFinished(String lastText) {
                api.searchAsync(lastText, new Api.SearchListener() {
                    @Override
                    public void onResult(List<Film> films) {
                        listAdapter.update(films);
                    }
                });
            }
        };

        EditText searchId = (EditText) parent.findViewById(R.id.searchId);
        searchId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lazySearch.search(charSequence.toString(), searchCallBack);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return parent;
    }

    private static class ViewHolder {
        View parent;
        TextView titleId;
    }

    private class ListAdapter extends BaseAdapter {

        LayoutInflater inf;
        List<Film> films = new ArrayList<>();
        ListAdapter() {
            inf = getActivity().getLayoutInflater();
        }

        void update(List<Film> films) {
            this.films = films;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return films.size();
        }

        @Override
        public Object getItem(int i) {
            return films.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder;
            if(view == null) {
                holder = new ViewHolder();
                holder.parent = view = inf.inflate(R.layout.item_search_film, viewGroup, false);
                holder.titleId = (TextView) view.findViewById(R.id.titleId);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Film film = films.get(i);
            holder.titleId.setText(film.getTitle());

            return view;
        }
    }
}
