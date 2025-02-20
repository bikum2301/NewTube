package tdp.bikum.newtube.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import tdp.bikum.newtube.models.Video;

public interface ApiService {
    @GET("/videos")
    Call<List<Video>> getVideos(); //
}