package network;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public interface NotificationService {
    @Headers("Content-Type: application/json")
    @POST("/api/notifications/send")
    Call<Void> sendNotification(@Body NotificationRequest notificationRequest);
}