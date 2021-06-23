package de.developkt.junit5demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTestBestPractices {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostDatabase postDatabase;
    @Mock
    private UserDatabase userDatabase;
    @Mock
    private AuthDatabase authDatabase;
    @Mock
    private ErrorLogger errorLogger;

    @Test
    public void should_create_non_null_instances_of_service_and_mocks() {
        assertNotNull(postService);
        assertNotNull(postDatabase);
        assertNotNull(userDatabase);
        assertNotNull(authDatabase);
        assertNotNull(errorLogger);
    }

    @Test
    public void should_allow_a_user_with_role_writer_to_create_posts() {
        String userId = "111";
        User writer = new User(userId, UserRole.WRITER, "John", "john@example.com");
        Post post = new Post("Hello", "Hello World Post", null, null);

        when(userDatabase.loadUserById(userId)).thenReturn(writer);
        when(authDatabase.isAllowedToWritePosts(writer)).thenReturn(true);

        boolean result = postService.createPost(userId, post);

        assertTrue(result);
        assertEquals(writer, post.getCreatedBy());
        verify(userDatabase).loadUserById(userId);
        verify(authDatabase).isAllowedToWritePosts(writer);
        verify(postDatabase).savePost(post);
    }

    @Test
    public void should_prohibit_a_user_with_role_reader_to_create_posts() {
        User reader = new User(UUID.randomUUID().toString(), UserRole.WRITER, "John", "john@example.com");
        Post post = new Post("Hello", "Hello", null, null);

        when(userDatabase.loadUserById(anyString())).thenReturn(reader);
        when(authDatabase.isAllowedToWritePosts(any())).thenReturn(false);

        boolean result = postService.createPost("", post);

        assertFalse(result);
        assertNull(post.getCreatedBy());
        assertNull(post.getCreatedAt());
        verify(userDatabase).loadUserById(anyString());
        verify(authDatabase).isAllowedToWritePosts(any());
        verifyNoMoreInteractions(userDatabase, authDatabase);
        verifyNoInteractions(postDatabase); // as replacement for verifyZeroInteractions(postDatabase) because it is deprecated
    }

    @Test
    public void should_log_an_error_when_user_not_found() {
        Post post = new Post("Hello", "World", null, null);
        doThrow(new UserNotFoundException("user not found")).when(userDatabase).loadUserById(any());
        ArgumentCaptor<Error> captor = ArgumentCaptor.forClass(Error.class);
        String userId = UUID.randomUUID().toString();

        boolean result = postService.createPost(userId, post);

        assertFalse(result);
        verify(errorLogger).logError(captor.capture());
        assertEquals(ErrorType.DATABASE_ERROR, captor.getValue().getType());
        assertEquals("Could not load user " + userId, captor.getValue().getMessage());
    }
}
