package tdp.bikum.newtube.models;

public class Video {
    private int id;
    private String title;
    private String description;
    private String video_url;
    private String thumbnail_url;
    private String upload_date;

    public Video() {
        // Constructor mặc định (cần thiết cho Gson)
    }

    // Constructor đầy đủ (nếu cần)
    public Video(int id, String title, String description, String video_url, String thumbnail_url, String upload_date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.video_url = video_url;
        this.thumbnail_url = thumbnail_url;
        this.upload_date = upload_date;
    }

    // Getters và Setters cho tất cả các trường
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }
}