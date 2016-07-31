package io.leifu.ribbit2.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.leifu.ribbit2.R;
import io.leifu.ribbit2.adapters.UserAdapter;
import io.leifu.ribbit2.utils.AlertDialogHelper;
import io.leifu.ribbit2.utils.FileHelper;
import io.leifu.ribbit2.utils.ParseConstants;

public class RecipientsActivity extends AppCompatActivity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;

    private GridView mGridView;
    private TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_menu_overflow));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mEmptyView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(mEmptyView);
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }
                    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            if (mGridView.getCheckedItemCount() > 0) {
                                mSendMenuItem.setVisible(true);
                            } else {
                                mSendMenuItem.setVisible(false);
                            }

                            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
                            if (mGridView.isItemChecked(position)) {
                                checkImageView.setVisibility(View.VISIBLE);
                            } else {
                                checkImageView.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialogHelper.builder(RecipientsActivity.this,
                            e.getMessage(),
                            getString(R.string.error_title));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_send:
                ParseObject message = createMessage();
                if (message == null) {
                    // error
                    AlertDialogHelper.builder(this,
                            getString(R.string.error_selecting_file_message),
                            getString(R.string.error_title));
                } else {
                    send(message);
                    finish();
                }
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // success!
                    Toast.makeText(RecipientsActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
//                    sendPushNotifications();
                    callCloudCode();
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialogHelper.builder(RecipientsActivity.this,
                            getString(R.string.error_sending_message),
                            getString(R.string.error_title));
                }
            }
        });
    }

    private void sendPushNotifications() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientIds());

        // send push notification
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message,
                ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();

    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // add LoginActivity as a new class
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear all the old activities
        startActivity(intent);
    }


    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECEIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if (fileBytes == null) {
            return null;
        } else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);

            return message;
        }

    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<>();
        for (int i = 0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    private void callCloudCode() {
        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> where = new HashMap<>();
        HashMap<String, Object> data = new HashMap<>();
        for (int i=0; i < getRecipientIds().size(); i++) {
            where.put("userId", getRecipientIds().get(i));
            data.put("alert", getString(R.string.push_message,
                    ParseUser.getCurrentUser().getUsername()));
            params.put("where", where);
            params.put("data", data);
            params.put("useMasterKey", true);
            ParseCloud.callFunctionInBackground("push", params, new FunctionCallback<String>() {
                @Override
                public void done(String object, ParseException e) {
                    if (e == null) {
                        // ratings is 4.5
                        Log.i(TAG, "Successfully called cloud code: " + object);
                    } else {
                        Log.e(TAG, "Could not call cloud code: " + e.getMessage());
                    }
                }
            });
        }
    }
}