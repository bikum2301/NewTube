package tdp.bikum.newtube;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";
    private WebView youtubeWebView;
    private String videoUrl;
    private String videoId;

    @SuppressLint("SetJavaScriptEnabled") // Cần thiết để YouTube Embed hoạt động
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        youtubeWebView = findViewById(R.id.youtubeWebView);

        videoUrl = getIntent().getStringExtra("VIDEO_URL");
        if (videoUrl == null || videoUrl.isEmpty()) {
            Log.e(TAG, "No video URL received!");
            finish();
            return;
        }
        Log.d(TAG, "Video URL received: " + videoUrl);

        videoId = extractVideoId(videoUrl);
        if (videoId == null) {
            Log.e(TAG, "Could not extract video ID from URL: " + videoUrl);
            Toast.makeText(this, "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "Extracted Video ID: " + videoId);

        // **KHÔNG CẦN YouTubeStreamFetcher NỮA, CHÚNG TA DÙNG WebView**

        // **Cấu hình WebView**
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Bật JavaScript (cần thiết cho YouTube Embed)
        youtubeWebView.setWebViewClient(new WebViewClient()); // Để xử lý các sự kiện trong WebView (ví dụ, link clicks)

        // **Tạo YouTube Embed URL**
        String embedUrl = "https://www.youtube.com/embed/" + videoId;
        Log.d(TAG, "Loading Embed URL: " + embedUrl);
        youtubeWebView.loadUrl(embedUrl); // Tải Embed URL vào WebView
    }

    private String extractVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return null;
        }

        String videoId = null;
        String patternYouTube = "(?:(?:https?:\\/\\/)?(?:www\\.)?youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=)|youtu\\.be\\/)([^#\\&\\?\\n]+)";
        Pattern compiledPattern = Pattern.compile(patternYouTube);
        Matcher matcher = compiledPattern.matcher(videoUrl);

        if (matcher.find()) {
            videoId = matcher.group(1);
        } else {
            try {
                Uri uri = Uri.parse(videoUrl);
                String host = uri.getHost();
                String path = uri.getPath();
                String query = uri.getQuery();

                if (host != null && (host.contains("youtube.com") || host.contains("youtu.be"))) {
                    if (path != null && path.startsWith("/embed/")) {
                        videoId = path.substring("/embed/".length());
                        if (videoId.contains("/")) {
                            videoId = videoId.substring(0, videoId.indexOf("/"));
                        }
                    } else if (host.contains("youtube.com") && path != null && path.startsWith("/watch")) {
                        if (query != null) {
                            videoId = uri.getQueryParameter("v");
                        }
                    } else if (host.contains("youtu.be") && path != null && path.startsWith("/")) {
                        videoId = path.substring(1);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing URL as URI: " + e.getMessage());
            }
        }

        return videoId;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Không cần ExoPlayer nữa
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (youtubeWebView != null) {
            youtubeWebView.destroy();
            youtubeWebView = null;
        }
    }
}