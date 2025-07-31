package pl.jkuznik.gitscope.model.github;

import java.util.Objects;

public record GitHubBranch(
        String name,
        String lastCommitSha
) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GitHubBranch that)) return false;
        return Objects.equals(name(), that.name()) && Objects.equals(lastCommitSha(), that.lastCommitSha());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name(), lastCommitSha());
    }
}
