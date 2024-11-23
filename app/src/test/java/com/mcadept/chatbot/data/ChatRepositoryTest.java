package com.mcadept.chatbot.data;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.mcadept.chatbot.api.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ChatRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Context mockContext;
    @Mock
    private ChatDatabase mockDatabase;
    @Mock
    private ApiClient mockApiClient;
    @Mock
    private ChatMessageDao mockMessageDao;
    @Mock
    private ChatResponseDao mockResponseDao;
    @Mock
    private ConnectivityManager mockConnectivityManager;
    @Mock
    private NetworkInfo mockNetworkInfo;

    private ChatRepository chatRepository;
    private MutableLiveData<List<ChatMessage>> messagesLiveData;

    @Before
    public void setup() {
        // Mock system services
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo())
                .thenReturn(mockNetworkInfo);

        // Mock database and DAOs
        when(mockDatabase.chatMessageDao()).thenReturn(mockMessageDao);
        when(mockDatabase.chatResponseDao()).thenReturn(mockResponseDao);

        // Setup LiveData
        messagesLiveData = new MutableLiveData<>();
        List<ChatMessage> messages = new ArrayList<>();
        messagesLiveData.setValue(messages);
        when(mockMessageDao.getAllMessages()).thenReturn(messagesLiveData);

        // Initialize repository
        chatRepository = new ChatRepository(mockContext);
    }

    @Test
    public void sendMessage_whenOnline_shouldCallApi() throws JSONException {
        // Arrange
        String message = "test message";
        String token = "test_token";
        when(mockNetworkInfo.isConnected()).thenReturn(true);

        JSONObject response = new JSONObject();
        response.put("response", "bot response");

        ChatRepository.ChatCallback callback = mock(ChatRepository.ChatCallback.class);

        // Act
        chatRepository.sendMessage(message, token, callback);

        // Assert
        verify(mockMessageDao).insert(any(ChatMessage.class));
        verify(mockApiClient).sendMessage(anyString(), anyString(), any(), any());
    }

    @Test
    public void sendMessage_whenOffline_shouldUseCachedResponse() {
        // Arrange
        String message = "test message";
        String token = "test_token";
        when(mockNetworkInfo.isConnected()).thenReturn(false);

        ChatResponse cachedResponse = new ChatResponse("test message", "cached response");
        when(mockResponseDao.findResponseForQuery(message)).thenReturn(cachedResponse);

        ChatRepository.ChatCallback callback = mock(ChatRepository.ChatCallback.class);

        // Act
        chatRepository.sendMessage(message, token, callback);

        // Assert
        verify(mockMessageDao).insert(any(ChatMessage.class));
        verify(mockResponseDao).findResponseForQuery(message);
        verify(callback).onResponse("cached response");
    }

    @Test
    public void sendMessage_whenOfflineNoCache_shouldReturnError() {
        // Arrange
        String message = "test message";
        String token = "test_token";
        when(mockNetworkInfo.isConnected()).thenReturn(false);
        when(mockResponseDao.findResponseForQuery(message)).thenReturn(null);
        when(mockResponseDao.getMostUsedResponses(5))
                .thenReturn(new ArrayList<>());

        ChatRepository.ChatCallback callback = mock(ChatRepository.ChatCallback.class);

        // Act
        chatRepository.sendMessage(message, token, callback);

        // Assert
        verify(mockMessageDao).insert(any(ChatMessage.class));
        verify(callback).onError("No internet connection and no cached responses");
    }

    @Test
    public void cleanupOldData_shouldDeleteOldMessages() {
        // Act
        chatRepository.cleanupOldData();

        // Assert
        verify(mockResponseDao).deleteOldResponses(any(Long.class));
        verify(mockMessageDao).deleteOldMessages(any(Long.class));
    }

    @Test
    public void getAllMessages_shouldReturnLiveData() {
        // Act
        chatRepository.getAllMessages();

        // Assert
        verify(mockMessageDao).getAllMessages();
    }
}
