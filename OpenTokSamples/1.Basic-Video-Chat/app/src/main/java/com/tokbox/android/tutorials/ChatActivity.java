package com.tokbox.android.tutorials;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;


public class ChatActivity   extends ActionBarActivity
                            implements  Session.SessionListener,
                                        PublisherKit.PublisherListener,
                                        SubscriberKit.SubscriberListener {

    private static final String LOG_TAG = ChatActivity.class.getSimpleName();


    // Replace with your OpenTok API key
    private static final String API_KEY = "";
    // Replace with a generated Session ID
    private static final String SESSION_ID = "";
    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    private static final String TOKEN = "";
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
        mSession.setSessionListener(this);
        mSession.connect(TOKEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Activity lifecycle methods */

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");

        if (mSession != null) {
            mSession.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

        if (mSession != null) {
            mSession.onResume();
        }
    }

    /* SessionListener methods */
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");

        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        LayoutParams layoutParams = new LayoutParams(200, 150);
        this.getWindow().addContentView(mPublisher.getView(), layoutParams);

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSubscriber.setSubscriberListener(this);
            mSession.subscribe(mSubscriber);
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            // mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
    }

    /* Publisher Listener methods */

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher Stream Created");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher Stream Destroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());
    }

    /* Subscriber Listener methods */

    @Override
    public void onConnected(SubscriberKit subscriber) {
        Log.i(LOG_TAG, "Subscriber Connected");

        LayoutParams layoutParams = new LayoutParams(400, 300);
        this.getWindow().addContentView(subscriber.getView(), layoutParams);
    }

    @Override
    public void onDisconnected(SubscriberKit subscriber) {
        Log.i(LOG_TAG, "Subscriber Disconnected");
    }

    @Override
    public void onError(SubscriberKit subscriber, OpentokError opentokError) {
        Log.e(LOG_TAG, "Subscriber error: " + opentokError.getMessage());
    }
}
