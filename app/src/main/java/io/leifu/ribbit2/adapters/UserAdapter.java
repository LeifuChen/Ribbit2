package io.leifu.ribbit2.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.leifu.ribbit2.R;
import io.leifu.ribbit2.utils.MD5Util;

public class UserAdapter extends ArrayAdapter<ParseUser> {
    private static final String TAG = UserAdapter.class.getSimpleName();
    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.user_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();
        if (email.equals("")) {
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        } else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "https://www.gravatar.com/avatar/" + hash + 
                    "?s=204&d=404";
            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty)
                    .into(holder.userImageView);
        }
        holder.nameLabel.setText(user.getUsername());
        holder.nameLabel.setTextColor(ContextCompat.getColor(mContext, R.color.purple));
        return convertView;
    }

    private static class ViewHolder {
        ImageView userImageView;
        TextView nameLabel;
    }


    public void refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        this.notifyDataSetChanged();
    }
}
