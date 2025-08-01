package pl.jkuznik.gitscope.model.github;

public record GitHubBranch(
        String name,
        Commit commit
) {
    public record Commit(String sha) {
    }
}
