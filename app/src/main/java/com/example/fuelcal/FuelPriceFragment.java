package com.example.fuelcal;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FuelPriceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FuelPriceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";







    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DownloadManager downloadManager;
    long downloadReference;

    private final static String BUS_STOP_JSON_URL = "https://storelocator.asda.com/fuel_prices_data.json";
    private final static String BUS_STOP_JSON_FILE_NAME = "asda/price_data";
    private final static String BUS_STOP_JSON_FILE_TMP_NAME = "asda/price_data_tmp";
    private final static String JSON_SUFFIX = ".json";


    ProgressBar progressBar;
    TextView progressText;
    TextView JSONTextView;



    public FuelPriceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FuelPriceFragment newInstance(String param1, String param2) {
        FuelPriceFragment fragment = new FuelPriceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fuelprice_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressText = (TextView) view.findViewById(R.id.progressText);
        JSONTextView = (TextView) view.findViewById(R.id.JSONTextView);


        File filePath = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(filePath, "asda/price_data");

        if (file.exists()) {
            file.delete();
        }

        downloadJSON(BUS_STOP_JSON_URL, "Bus Stop Data", "Bus Stop Data", BUS_STOP_JSON_FILE_NAME);
        checkDownloadStatusFunction(progressText, progressBar, BUS_STOP_JSON_FILE_TMP_NAME, BUS_STOP_JSON_FILE_NAME);


        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadReference == reference) {

                    ArrayList<Object> listData = new ArrayList<Object>();

                    try {

                        convertJsonToArrayList(listData, BUS_STOP_JSON_FILE_NAME, JSONTextView);
                        for (int i = 0; i < listData.size(); i++) {
                            /*System.out.println(listData.get(i).toString());
                            for (int j = 0; j < listData.get(i).size(); i++) {

                            }*/
                        }
                    } catch (FileNotFoundException e) {
                        //busStop1TextView.setText("Bus stop json not found");
                        System.out.println("Bus stop json not found");
                    } catch (Throwable e) {
                        //busStop1TextView.setText(e.toString());
                        System.out.println(e.toString());
                    }


                }
            }
        };
        requireActivity().registerReceiver(receiver, filter);


    }




    private void downloadJSON(String url, String title, String description, String subPath) {

        downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(title);

        //Setting description of request
        request.setDescription(description);


        request.setDestinationInExternalFilesDir(requireContext(),
                Environment.DIRECTORY_DOWNLOADS, subPath);


        downloadReference = downloadManager.enqueue(request); // enqueue a new download


    }

    private void checkDownloadStatusFunction(final TextView progressText, final ProgressBar progressBar, final String tmpFileName, final String realFileName) {

        // update progressbar
        final long downloadReferenceInUse = downloadReference;

        final Timer progressTimer = new Timer();
        progressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DownloadManager.Query downloadQuery = new DownloadManager.Query();
                downloadQuery.setFilterById(downloadReferenceInUse);

                final Cursor cursor = downloadManager.query(downloadQuery);

                if (cursor.moveToFirst()) { // this "if" is crucial to prevent a kind of error
                    @SuppressLint("Range") final int downloadedBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    @SuppressLint("Range") final int totalBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)); // integer is enough for files under 2GB
                    @SuppressLint("Range") final String downloadPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));

                    int downloadStatus = DownloadStatus(cursor);
                    removeTmpFile(tmpFileName, realFileName);


                    cursor.close();

                    final float downloadProgress = downloadedBytes * 100f / totalBytes;
                    //if (downloadProgress > 99.9) // stop repeating timer (it's also useful for error prevention)
                    if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) { // stop repeating timer (it's also useful for error prevention)
                        progressTimer.cancel();
                    }


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressText.setText(downloadPath + "\n" + downloadedBytes + " bytes\n" + totalBytes + " bytes\n" + downloadProgress + "%");
                            progressBar.setProgress((int) downloadProgress);
                        }
                    });
                }


            }
        }, 0, 10);
    }

    private int DownloadStatus(Cursor cursor) {

        //https://www.codeproject.com/articles/1112730/android-download-manager-tutorial-how-to-download

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        //int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        //String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                break;
        }

        return status;


    }

    private void removeTmpFile(String tmpFileName, String realFileName) {

        File filePath = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(filePath, tmpFileName);
        File file2 = new File(filePath, realFileName);

        if (file.exists()) {
            file2.delete();
            file.renameTo(file2);
            file.delete();
        }


    }


    private void convertJsonToArrayList(ArrayList<Object> listData, String filename, TextView showTextView) throws Exception {
        //https://stackoverflow.com/questions/31670076/android-download-and-store-json-so-app-can-work-offline


        File filePath = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(filePath, filename);


        FileInputStream fileStream = new FileInputStream(file);


        String JSONString = convertStreamToString(fileStream);
        //Make sure you close all streams.
        fileStream.close();

        //System.out.print(JSONString);

        JSONObject result = new JSONObject(JSONString);


        JSONArray jsonArray = result.getJSONArray("stations");
        String timeStamp = result.getString("last_updated");

        //String dateTimeString = DateUtil.returnDatetimeString(timeStamp);
        //showTextView.setText(dateTimeString);
        showTextView.setText(timeStamp);
        showTextView.append("\n");


        //Checking whether the JSON array has some value or not
        if (jsonArray != null) {

            //Iterating JSON array
            for (int i = 0; i < jsonArray.length(); i++) {

                //Adding each element of JSON array into ArrayList
               // listData.add(jsonArray.get(i));
                //System.out.println(jsonArray.get(i).toString());

                JSONObject result2 = new JSONObject(jsonArray.get(i).toString());



                System.out.println("site_id: " + result2.getString("site_id"));
                System.out.println("brand: " + result2.getString("brand"));
                System.out.println("address: " + result2.getString("address"));
                System.out.println("postcode: " + result2.getString("postcode"));
                System.out.println("location: " + result2.getString("location"));
                System.out.println("prices: " + result2.getString("prices"));

                showTextView.append("\n");
                showTextView.append("site_id: " + result2.getString("site_id") + "\n");
                showTextView.append("brand: " + result2.getString("brand") + "\n");
                showTextView.append("address: " + result2.getString("address") + "\n");
                showTextView.append("postcode: " + result2.getString("postcode") + "\n");
                showTextView.append("location: " + result2.getString("location") + "\n");
                showTextView.append("prices: " + result2.getString("prices") + "\n");





            }
        }
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


}