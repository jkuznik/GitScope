package pl.jkuznik.gitscope.gitHub;

class GitHubUserNotFoundException extends RuntimeException {
    public GitHubUserNotFoundException(String username) {
        super("Username '" + username + "' not found");
    }
}
