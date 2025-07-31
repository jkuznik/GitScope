package pl.jkuznik.gitscope.model.github;

import java.util.List;
import java.util.Objects;

public record GitHubRepository(
        String repositoryName,
        String ownerLogin,
        List<GitHubBranch> branches
) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GitHubRepository that)) return false;
        return Objects.equals(ownerLogin(), that.ownerLogin()) && Objects.equals(repositoryName(), that.repositoryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(repositoryName(), ownerLogin());
    }
}