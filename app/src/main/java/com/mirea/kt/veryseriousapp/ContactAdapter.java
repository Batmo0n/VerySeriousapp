package com.mirea.kt.veryseriousapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class ContactAdapter extends ArrayAdapter<Contact>{

    private final LayoutInflater inflater;
    private final MainActivity mainActivity;

    public ContactAdapter(MainActivity activity,Context context, List<Contact> contacts) {
        super(context, 0, contacts);
        this.mainActivity = activity;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_item, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.contact_name);
            holder.phoneTextView = convertView.findViewById(R.id.contact_phone);
            holder.avatarView = convertView.findViewById(R.id.contact_avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = getItem(position);

        if (contact.hasAvatar) {
            Picasso.get().load(contact.getAvatar()).into(holder.avatarView);
        } else {
            holder.avatarView.setImageResource(R.drawable.default_avatar);
        }

        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhone());

        convertView.setOnClickListener(v -> {
            mainActivity.openEditContactScreen(contact, getContext());
            Log.d("Myapppp", "onClick: Click on item");
        });

        convertView.setOnLongClickListener(v -> {
            Log.d("Myapppp", "onClick: Long click on item");
            // Perform the action when long-clicked
            if (!Objects.equals(contact.getPhone(), "")){
                String phoneNumber = contact.getPhone();

                if (phoneNumber.equals("89001123344") || phoneNumber.equals("89987654321") || phoneNumber.equals("89123456789")){
                    Toast.makeText(mainActivity, "Сюда не звонить", Toast.LENGTH_SHORT).show();

                }
                else{
                    Log.d("Myapppp", "onLongClick: REAL CALL ALERT");
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    v.getContext().startActivity(intent);
                }
            }
            return true;
        });

        return convertView;
    }


    private static class ViewHolder {
        ImageView avatarView;
        TextView nameTextView;
        TextView phoneTextView;
    }
}


