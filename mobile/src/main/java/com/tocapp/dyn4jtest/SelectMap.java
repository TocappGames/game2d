package com.tocapp.dyn4jtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SelectMap extends AppCompatActivity implements DataClient.OnDataChangedListener, MessageClient.OnMessageReceivedListener, CapabilityClient.OnCapabilityChangedListener {
    private static final Object TAG = "Debug";
    int selection = 0;
    int numMaps = 5;

    int ballColor;
    int sticksColor;
    int boxColor;
    int goalsColor;
    private ArrayList<Integer> images;
    private ImageButton image;


    private static final String VIDEO_CONFIRMATION_PATH = "/confirmation";
    private static final String VIDEO_CONFIRMATION_TIME = "time";
    private static final String UNLOCKED_MAP_ID = "id";
    private static final String START_ACTIVITY_PATH = "/start-activity";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Button buttonSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        ImageButton buttonLeft = findViewById(R.id.buttonLeft);
        ImageButton buttonRight = findViewById(R.id.buttonRight);
        buttonSelect = findViewById(R.id.selectBtn);

        image = findViewById(R.id.image);

        images = new ArrayList<>();
        images.add(R.drawable.mapa1);
        images.add(R.drawable.mapa2);
        images.add(R.drawable.mapa3);
        images.add(R.drawable.mapa4);
        images.add(R.drawable.mapa5);

        image.setImageResource(images.get(selection));

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selection < numMaps -1) {
                    selection++;
                    mapChanged();
                    image.setImageResource(images.get(selection));
                }

            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selection > 0) {
                    selection--;
                    mapChanged();
                    image.setImageResource(images.get(selection));

                }
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapSelected();


            }
        });

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapSelected();
            }
        });
    }

    private void onMapSelected() {
        switch (selection) {
            case 0:
                MainActivity.ballColor = Color.WHITE;
                MainActivity.sticksColor = Color.BLUE;
                MainActivity.boxColor = Color.WHITE;
                MainActivity.goalsColor = Color.RED;
                break;
        case 1:
            viewVideo(1);

            // get if video is viewed
            boolean mapa1Viewed = sharedPref.getBoolean("1", false);
            if (mapa1Viewed) {
            MainActivity.ballColor = Color.YELLOW;
            MainActivity.sticksColor = Color.GREEN;
            MainActivity.boxColor = Color.MAGENTA;
            MainActivity.goalsColor = Color.CYAN;
            MainActivity.backgroundImage = R.drawable.fondo_2;
        }
            break;
            case 2:
                // When the video is viewed
                viewVideo(2);
                // get if video is viewed
                boolean mapa2Viewed = sharedPref.getBoolean("2", false);
                if (mapa2Viewed) {
                    MainActivity.ballColor = Color.CYAN;
                    MainActivity.sticksColor = Color.YELLOW;
                    MainActivity.boxColor = Color.MAGENTA;
                    MainActivity.goalsColor = Color.GREEN;
                    MainActivity.backgroundImage = R.drawable.fondo_3;
                }
                break;
            case 3:
                // When the video is viewed
                viewVideo(3);

                // get if video is viewed
                boolean mapa3Viewed = sharedPref.getBoolean("3", false);
                if (mapa3Viewed) {
                    MainActivity.ballColor = Color.GREEN;
                    MainActivity.sticksColor = Color.MAGENTA;
                    MainActivity.boxColor = Color.WHITE;
                    MainActivity.goalsColor = Color.RED;
                    MainActivity.backgroundImage = R.drawable.fondo_4;
                }
                break;
            case 4:
                // When the video is viewed
                viewVideo(4);

                // get if video is viewed
                boolean mapa4Viewed = sharedPref.getBoolean("4", false);
                if (mapa4Viewed) {
                    MainActivity.ballColor = Color.BLACK;
                    MainActivity.sticksColor = Color.GREEN;
                    MainActivity.boxColor = Color.YELLOW;
                    MainActivity.goalsColor = Color.MAGENTA;
                    MainActivity.backgroundImage = R.drawable.fondo_5;
                }
                break;
        }
        Intent i = new Intent(SelectMap.this, NewActivity.class);
        startActivity(i);
    }

    private void viewVideo(int numMap) {
        // When the video is viewed
        editor.putBoolean(Integer.toString(numMap), true);
        editor.commit();
        sendVideoViewedConfirmation(numMap);
    }

    private void mapChanged() {
        boolean mapIsUnlocked = sharedPref.getBoolean(Integer.toString(selection), false);
        if (selection == 0) mapIsUnlocked = true;
        if (mapIsUnlocked) {
            buttonSelect.setText("Select map");
        } else {
            buttonSelect.setText("You need to see a video");
        }
    }

    @Override
    public void onResume() {
    super.onResume();
        // Instantiates clients without member variables, as clients are inexpensive to create and
        // won't lose their listeners. (They are cached and shared between GoogleApi instances.)
        Wearable.getDataClient(this).addListener(this);
        Wearable.getMessageClient(this).addListener(this);
        Wearable.getCapabilityClient(this)
                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        System.out.println("onDataChanged: " + dataEvents);

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                System.out.println(event.getDataItem().toString());
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                System.out.println(event.getDataItem().toString());

            }
        }
    }

    /** Sends an RPC to start a fullscreen Activity on the wearable. */
    public void onStartWearableActivityClick(View view) {
        System.out.println("Generating RPC");

        // Trigger an AsyncTask that will query for a list of connected nodes and send a
        // "start-activity" message to each connected node.
        new StartWearableActivityTask().execute();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
      System.out.println("Message recived " + messageEvent);
    }

    @Override
    public void onCapabilityChanged(final CapabilityInfo capabilityInfo) {
        System.out.println("Capability changed: " + capabilityInfo.toString());
    }

    @WorkerThread
    private void sendStartActivityMessage(String node) {
        System.out.println("Sending message to start activity");
        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this).sendMessage(node, START_ACTIVITY_PATH, new byte[0]);

        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            Integer result = Tasks.await(sendMessageTask);
            System.out.println("Message start activity sent: " + result);

        } catch (ExecutionException exception) {
            System.out.println("Task failed: " + exception);

        } catch (InterruptedException exception) {
            System.out.println("Interrupt occurred: " + exception);
        }
    }


    @WorkerThread
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();

        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();

        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            List<Node> nodes = Tasks.await(nodeListTask);

            for (Node node : nodes) {
                results.add(node.getId());
            }

        } catch (ExecutionException exception) {
            System.out.println("Task failed: " + exception);

        } catch (InterruptedException exception) {
            System.out.println("Interrupt occurred: " + exception);
        }

        return results;
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                System.out.println("Nodes: " + node);
                sendStartActivityMessage(node);
            }
            return null;
        }
    }


    private void sendVideoViewedConfirmation(int mapId) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(VIDEO_CONFIRMATION_PATH);
        System.out.println(new Date().getTime());
        dataMap.getDataMap().putLong(VIDEO_CONFIRMATION_TIME, new Date().getTime());
        dataMap.getDataMap().putInt(UNLOCKED_MAP_ID, mapId );
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();

        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);

        dataItemTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                       System.out.println("Send video confirmation was successful: " + dataItem);
                    }
                });
    }


}