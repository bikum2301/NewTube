package tdp.bikum.newtube.utils;

import android.util.Log;

import com.google.api.client.http.javanet.NetHttpTransport; // ⚠️ Import NetHttpTransport instead of GoogleNetHttpTransport for direct use
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoLiveStreamingDetails;

import java.util.ArrayList;
import java.util.List;

public class YouTubeStreamFetcher {

    private static final String TAG = "YouTubeStreamFetcher";
    private static final String YOUTUBE_API_KEY = "AIzaSyA469Dluw3YF0yA2iixnPz__eDtdFsfZCQ"; // 🔴 Thay bằng API Key thật (ĐÃ XÁC NHẬN LÀ API KEY THẬT CỦA BẠN)

    public interface StreamUrlCallback {
        void onStreamUrlsReceived(List<String> streamUrls);
        void onStreamUrlFetchFailed(String errorMessage);
    }

    public static void getYouTubeStreamUrlsAsync(String videoId, StreamUrlCallback callback) {
        new Thread(() -> {
            List<String> streamUrls = getYouTubeStreamUrls(videoId);
            if (streamUrls != null && !streamUrls.isEmpty()) {
                callback.onStreamUrlsReceived(streamUrls);
            } else {
                callback.onStreamUrlFetchFailed("⚠️ Không thể lấy link stream từ YouTube API");
            }
        }).start();
    }

    private static List<String> getYouTubeStreamUrls(String videoId) {
        try {
            // ⚠️ SỬA ĐỔI QUAN TRỌNG: Sử dụng NetHttpTransport() thay vì GoogleNetHttpTransport.newTrustedTransport()
            NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

            YouTube youtubeService = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
                // Không cần init gì đặc biệt cho API key
            }).setApplicationName("NewTube-App").build();

            // 🔹 Chỉ cần lấy "liveStreamingDetails"
            YouTube.Videos.List request = youtubeService.videos().list("liveStreamingDetails");
            request.setId(videoId);
            request.setKey(YOUTUBE_API_KEY);

            // ⚠️ THÊM LOG DEBUG NGAY TRƯỚC GỌI API REQUEST
            Log.d(TAG, "➡️ Gọi YouTube API request...");

            VideoListResponse response = request.execute();

            // ⚠️ THÊM LOG DEBUG NGAY SAU GỌI API RESPONSE
            Log.d(TAG, "⬅️ Đã nhận YouTube API response.");

            // ⚠️ THÊM LOG RESPONSE OBJECTS ĐỂ DEBUG NỘI DUNG RESPONSE
            Log.d(TAG, "Response Object: " + response); // Log toàn bộ response object
            List<Video> videoList = response.getItems();
            Log.d(TAG, "Video List: " + videoList); // Log videoList
            if (videoList != null && !videoList.isEmpty()) {
                Video video = videoList.get(0);
                Log.d(TAG, "Video Item: " + video); // Log video item
                VideoLiveStreamingDetails liveDetails = video.getLiveStreamingDetails();
                Log.d(TAG, "Live Streaming Details: " + liveDetails); // Log liveDetails

                if (liveDetails != null) {
                    List<String> streamUrls = new ArrayList<>();
                    // ⚠️ SỬA ĐỔI: Kiểm tra containsKey thay vì get() != null
                    // Kiểm tra và thêm HLS URL nếu có
                    if (liveDetails.containsKey("hlsManifestUrl")) {
                        streamUrls.add((String) liveDetails.get("hlsManifestUrl")); // 🔹 URL HLS stream
                    }
                    // Kiểm tra và thêm DASH URL nếu có
                    if (liveDetails.containsKey("dashManifestUrl")) {
                        streamUrls.add((String) liveDetails.get("dashManifestUrl")); // 🔹 URL DASH stream
                    }

                    if (!streamUrls.isEmpty()) { // ✅ Kiểm tra xem có stream URLs nào được trích xuất không
                        return streamUrls; // ✅ TRẢ VỀ streamUrls NẾU CÓ
                    } else {
                        Log.w(TAG, "⚠️ Không tìm thấy HLS hoặc DASH stream URLs trong liveStreamingDetails."); // Log cảnh báo
                        return null; // Trả về null nếu không có stream URLs
                    }

                }
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Lỗi khi lấy link stream từ YouTube API", e);
            Log.e(TAG, "YouTube API Response (Debug):", e);

            // ⚠️ THÊM LOG TOÀN BỘ RESPONSE (DẠNG JSON STRING) - CHỈ LOG KHI CÓ EXCEPTION
            if (e instanceof com.google.api.client.googleapis.json.GoogleJsonResponseException) {
                com.google.api.client.googleapis.json.GoogleJsonResponseException jsonException = (com.google.api.client.googleapis.json.GoogleJsonResponseException) e;
                try {
                    Log.e(TAG, "YouTube API Full Response (JSON): " + jsonException.getDetails().toPrettyString()); // Log JSON response
                } catch (Exception ex) {
                    Log.e(TAG, "❌ Lỗi khi log JSON response", ex); // Handle exception during JSON logging
                }
            }
            return null;
        }
        Log.w(TAG, "⚠️ Không có liveStreamingDetails trong response từ YouTube API."); // Log cảnh báo nếu không có liveStreamingDetails
        return null; // Trả về null nếu không có liveStreamingDetails
    }
}