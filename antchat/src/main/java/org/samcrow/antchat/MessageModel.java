package org.samcrow.antchat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Stores messages and provides access for a ListView
 */
public class MessageModel extends BaseAdapter {

	private final List<Message> messages = new ArrayList<>();

	public MessageModel() {
		this(null);
	}

	public MessageModel(Collection<? extends Message> initial) {
		if(initial != null) {
			messages.addAll(initial);
			notifyDataSetChanged();
		}
	}

	public void add(Message message) {
		messages.add(message);
		notifyDataSetChanged();
	}

	public void clear() {
		messages.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView instanceof MessageView) {
			((MessageView) convertView).setMessage(messages.get(position));
			return convertView;
		}
		else {
			final MessageView view = new MessageView(parent.getContext());
			view.setMessage(messages.get(position));
			return view;
		}
	}
}
