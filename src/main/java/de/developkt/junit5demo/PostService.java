package de.developkt.junit5demo;

import java.time.OffsetDateTime;

public class PostService {

    private final PostDatabase postDatabase;
    private final UserDatabase userDatabase;
    private final AuthDatabase authDatabase;
    private final ErrorLogger errorLogger;

    public PostService(PostDatabase postDatabase, UserDatabase userDatabase, AuthDatabase authDatabase, ErrorLogger errorLogger) {
        this.postDatabase = postDatabase;
        this.userDatabase = userDatabase;
        this.authDatabase = authDatabase;
        this.errorLogger = errorLogger;
    }

    public boolean createPost(String userId, Post post) {
        try {
            User user = userDatabase.loadUserById(userId);
            if (authDatabase.isAllowedToWritePosts(user)) {
                post.setCreatedAt(OffsetDateTime.now());
                post.setCreatedBy(user);
                postDatabase.savePost(post);
                return true;
            }
        } catch (UserNotFoundException ex) {
            errorLogger.logError(new Error(
                    "Could not load user " + userId,
                    ErrorType.DATABASE_ERROR
            ));
        }
        return false;
    }
}

class PostDatabase {
    public void savePost(Post post) {
        // do nothing
    }
}

class UserDatabase {
    public User loadUserById(String userId) throws UserNotFoundException {
        return new User("111", UserRole.READER, "John", "john@example.com");
    }
}

class AuthDatabase {
    public boolean isAllowedToWritePosts(User user) {
        return false;
    }
}

class ErrorLogger {
    public void logError(Error error) {
        // something happens here
    }
}

class Post {
    private String subject;
    private String message;
    private OffsetDateTime createdAt;
    private User createdBy;

    public Post(String subject, String message, OffsetDateTime createdAt, User createdBy) {
        this.subject = subject;
        this.message = message;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}

class User {
    private String userId;
    private UserRole role;
    private String name;
    private String email;

    public User(String userId, UserRole role, String name, String email) {
        this.userId = userId;
        this.role = role;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

enum UserRole {
    READER, WRITER, ADMIN
}

class Error {
    private final String message;
    private final ErrorType type;

    public Error(String message, ErrorType type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public ErrorType getType() {
        return type;
    }
}

enum ErrorType {
    DATABASE_ERROR
}

class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}