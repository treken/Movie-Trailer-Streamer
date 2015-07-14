package com.example.anthony.moviestreamer;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    public ArrayAdapter<String> movieImageAdapter;
    public ArrayAdapter<String> movieTitleAdapter;
    public List<JSONArray> resultHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        movieImageAdapter =
                new ArrayAdapter<String>(
                        this, // The current context (this activity)
                        R.layout.grid_item_layout, // The name of the layout ID.
                        R.id.image, // The ID of the textview to populate.
                        new ArrayList<String>());
        movieTitleAdapter =
                new ArrayAdapter<String>(
                        this, // The current context (this activity)
                        R.layout.grid_item_layout, // The name of the layout ID.
                        R.id.text, // The ID of the textview to populate.
                        new ArrayList<String>());
    }

    @Override
    public void updateSort(){

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

    public class FetchMovieData extends AsyncTask<String, Void, String[]>{
        public String[] resultStrs;
        private final String LOG_TAG = FetchMovieData.class.getSimpleName();


        private String[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException{
            //Json objects needed from results
            final String QUERY_RESULTS = "results";
            final String BACKGROUND_IMAGE = "backdrop_path";
            final String TITLE = "title";
            final String DESCRIPTION = "overview";
            final String RELEASE_DATE = "release_date";
            final String IMAGE = "poster_path";
            final String VOTE = "vote_average";

            JSONObject holder = new JSONObject(movieJsonStr);
            JSONArray movieArray = holder.getJSONArray(QUERY_RESULTS);

            resultStrs = new String[movieArray.length()];
            resultHolder = new ArrayList<JSONArray>();
            for (int i = 0;i < movieArray.length(); i++){
                String title;
                JSONObject movieResult = movieArray.getJSONObject(i);
                title = movieResult.getJSONObject(TITLE).toString();
                resultHolder.add(movieArray.getJSONArray(i));
                resultStrs[i] = title;
            }



        }

        protected String[] doInBackground(String... params){
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            try{
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/discover/movie?api_key=0ad0c3a316a84d6a2604e366f8a7b940&";
                final String SORT_PARAM = "sort_by";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendQueryParameter(SORT_PARAM, params[0]).build();
                URL url = new URL(builtUri.toString());

                // Create the request to MovieDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            //this will happen only if there was an error getting or parsing the output
            return null;

        }
        protected void onPostExecute(String[] strings){
            movieTitleAdapter.clear();
            movieImageAdapter.clear();
            for(int i= 0; i < resultStrs.length;i++){
                movieTitleAdapter.add(resultStrs[i]);

            }
        }
    }
}
