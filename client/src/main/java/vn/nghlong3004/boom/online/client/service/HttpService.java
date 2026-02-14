package vn.nghlong3004.boom.online.client.service;

import java.util.concurrent.CompletableFuture;
import vn.nghlong3004.boom.online.client.model.request.*;
import vn.nghlong3004.boom.online.client.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.client.model.room.Room;

public interface HttpService {

  CompletableFuture<RoomPageResponse> getRooms(int page, int size, String token);

  CompletableFuture<Room> createRoom(CreateRoomRequest request, String token);

  CompletableFuture<Room> joinRoom(String roomId, String token);

  CompletableFuture<String> sendRegisterRequest(RegisterRequest registerRequest);

  CompletableFuture<String> sendLoginRequest(LoginRequest loginRequest);

  CompletableFuture<String> sendForgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  CompletableFuture<String> sendVerifyOTP(OTPRequest otpRequest);

  CompletableFuture<String> sendResetPassword(ResetPasswordRequest resetPasswordRequest);
}
