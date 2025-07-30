package pl.jkuznik.gitscope.exception;

public class GitHubUserNotFoundException extends RuntimeException {
    public GitHubUserNotFoundException(String username) {
        super("Username '" + username + "' not found");
    }
}
