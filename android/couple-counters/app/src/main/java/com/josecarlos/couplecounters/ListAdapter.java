package com.josecarlos.couplecounters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.josecarlos.couplecounters.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose Carlos on 30/10/2015.
 */
public class ListAdapter extends BaseAdapter {

	private final List<CounterItem> items = new ArrayList<CounterItem>();
	//private final List<String> items = new ArrayList<String>();
    private final Context context;

    public ListAdapter(Context context)
    {
        this.context = context;
    }

    public void add(CounterItem item)
    {
        this.items.add(item);
        notifyDataSetChanged();
    }

    public void remove(CounterItem item)
    {
        this.items.remove(item);
        notifyDataSetChanged();
    }

    public void clear()
    {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void set(int pos, CounterItem item)
    {
        this.items.set(pos, item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
	public CounterItem getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

		final CounterItem item  = getItem(position);
		LinearLayout itemLayout = (LinearLayout)convertView;
		if (itemLayout==null)
		{
			LayoutInflater layoutInflater = LayoutInflater.from(this.context);
			itemLayout = (LinearLayout)layoutInflater.inflate(R.layout.item_view, parent, false);
		}

        final ImageView imageView = (ImageView) itemLayout.findViewById(R.id.counter_pic);
        Utils.setInitial(imageView, item.getCounterName(), 1);

		final TextView titleView = (TextView) itemLayout.findViewById(R.id.counter_text);
        //titleView.setTextColor(item.isCommon()?Color.parseColor("#FFBFDF"):Color.parseColor("#000000"));
        titleView.setText(item.getCounterName());

		return itemLayout;
    }
}