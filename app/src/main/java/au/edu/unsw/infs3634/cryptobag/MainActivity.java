package au.edu.unsw.infs3634.cryptobag;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.unsw.infs3634.cryptobag.Entities.Coin;
import au.edu.unsw.infs3634.cryptobag.Entities.CoinLoreResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_ACTIVITY";
    private boolean mTwoPane;
    private CoinAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoinDatabase coinData = Room.databaseBuilder(getApplicationContext(), CoinDatabase.class, "coinDatabase").build();

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;




     /**
        coinsCall.enqueue(new Callback<CoinLoreResponse>() {
            @Override
            public void onResponse(Call<CoinLoreResponse> call, Response<CoinLoreResponse> response) {
                List<Coin> coins = response.body().getData();
                mAdapter.setCoins(coins);
            }

            @Override
            public void onFailure(Call<CoinLoreResponse> call, Throwable t) {

            }
        });
      **/
    }
        RecyclerView mRecyclerView = findViewById(R.id.rvList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CoinAdapter(this, new ArrayList<Coin>(), mTwoPane);
        mRecyclerView.setAdapter(mAdapter);

        //Executing the AsyncTask
        new GetCoinTask().execute();
        new GetCoinDBTask().execute();
    }

        private class GetCoinTask extends AsyncTask<Void, Void, List<Coin>>{

        @Override
        protected List<Coin> doInBackground(Void... voids) {
            //This is what the thread will be doing in the background. Getting the data from the API

            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.coinlore.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                CoinService service = retrofit.create(CoinService.class);
                Call<CoinLoreResponse> coinsCall = service.getCoins();
                Response<CoinLoreResponse> coinLoreResponseResponse = coinsCall.execute();

                List<Coin> coins = coinLoreResponseResponse.body().getData();
                CoinDatabase.coinDao().deleteAll(CoinDatabase.coinDao().toArray(new Coin[CoinDatabase.coinDao().getCoins().size()]));
                CoinDatabase.coinDao().insertAll(coins.toArray(new Coin[coins.size()]));

                return coins;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        //Incoking stuff for the UI because the background thread cannot make changes to the UI
        protected void onPostExecute(List<Coin> coins){
            mAdapter.setCoins(coins);
        }


    }
    private class GetCoinDBTask extends AsyncTask<Void, Void, List<Coin>>{

        @Override
        protected List<Coin> doInBackground(Void... voids) {
            return CoinDatabase.coinDao().getCoins();
        }

        @Override
        protected void onPostExecute(List<Coin> coins){
            mAdapter.setCoins(coins);
        }
    }
}
