package pl.jkuznik.gitscope.model.github;

public record GitHubBranch(
        String name,
        String lastCommitSha
) {}
