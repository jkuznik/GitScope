package pl.jkuznik.gitscope.model.github;

import java.util.List;

public record GitHubRepository(
        String repositoryName,
        String ownerLogin,
        List<GitHubBranch> branches
) {}