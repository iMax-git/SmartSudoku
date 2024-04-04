package com.backtracking.smartsudoku.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WallpaperAdapter<T> extends ArrayAdapter<T> {

    public WallpaperAdapter(android.content.Context context, int resource, T[] objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);

    }


}
