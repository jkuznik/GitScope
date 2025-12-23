package pl.jkuznik.gitscope.gitHub;

class GitHubException extends RuntimeException {
    GitHubException(String message) {
        super(message);
    }

    static class UsernameNotFound extends GitHubException {
        public UsernameNotFound(String username) {
            super("Username '" + username + "' not found");
        }
    }

    static class Unauthorized extends GitHubException {
        public Unauthorized(String message) {
            super(message);
        }
    }
}
