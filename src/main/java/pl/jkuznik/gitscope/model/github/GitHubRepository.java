package pl.jkuznik.gitscope.model.github;

import java.util.List;

public record GitHubRepository(
        String name,
        Owner owner,
        boolean fork,
        boolean isPrivate,
        List<GitHubBranch> branches
) {
    public record Owner(String login) {
    }
}