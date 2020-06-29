package com.uzair.smarttravelmanagement.Interfaces;

import com.uzair.smarttravelmanagement.Notifications.MyResponse;
import com.uzair.smarttravelmanagement.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAApp-FwUQ:APA91bGC35C4GotOOqlVimLe7QfpMKqgjKv-o3p8pKLdQtToSkqoV7Gq00kohU_Ir_1RAscqfK9U4npJIhNeF5AX-ugCtFgGEc1d-C7SWr21XG1By5fSiM8fH7H9zPCDSAhRzcwz4DBQ"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
