package tdp.bikum.newtube;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tdp.bikum.newtube.adapters.VideoAdapter;
import tdp.bikum.newtube.models.Video;
import tdp.bikum.newtube.services.ApiService;

public class MainActivity extends AppCompatActivity {

    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private List<Video> videoList;
    private ApiService apiService;
    private static final String BASE_URL = "http://192.168.1.109:3000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(this, videoList);
        videoRecyclerView.setAdapter(videoAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        loadVideos();
    }

    private void loadVideos() {
        Call<List<Video>> call = apiService.getVideos(); // Đã sửa thành getVideos()
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful()) {
                    videoList = response.body();
                    if (videoList != null) {
                        Log.d("MainActivity", "API Response Success, videoList size: " + videoList.size());
                        videoAdapter.setVideoList(videoList);
                        videoAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("MainActivity", "API Response body is null (videoList is null)");
                    }
                } else {
                    Log.e("API Error", "Lỗi response API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                Log.e("API Failure", "Lỗi gọi API: " + t.getMessage());
            }
        });
    }
}