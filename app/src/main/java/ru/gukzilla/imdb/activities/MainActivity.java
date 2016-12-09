package ru.gukzilla.imdb.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ru.gukzilla.imdb.R;
import ru.gukzilla.imdb.fragments.FragmentBookmarks;
import ru.gukzilla.imdb.fragments.FragmentSearch;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinnerNav = (Spinner) findViewById(R.id.spinnerNav);
        spinnerNav.setAdapter(new BaseAdapter() {
            String[] strings = new String[]{getString(R.string.search), getString(R.string.bookmarks)};

            @Override
            public int getCount() {
                return strings.length;
            }

            @Override
            public String getItem(int i) {
                return strings[i];
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                TextView item = (TextView) getLayoutInflater()
                        .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                item.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                item.setText(getItem(i));
                return item;
            }
        });

        final FragmentSearch fragmentSearch = new FragmentSearch();
        final FragmentBookmarks fragmentBookmarks = new FragmentBookmarks();

        spinnerNav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                switch (position) {
                    case 0:
                        fragmentTransaction.replace(R.id.contentLayout, fragmentSearch);
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.contentLayout, fragmentBookmarks);
                        break;
                }

                fragmentTransaction.commitNow();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
