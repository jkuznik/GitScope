package pl.jkuznik.gitscope.gitHub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

record GitHubRepositoryModel(
        String name,
        Owner owner,
        boolean fork,
        @JsonProperty("private")
        boolean isPrivate,
        List<GitHubBranch> branches


) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GitHubRepositoryModel that)) return false;
        return Objects.equals(name(), that.name()) && Objects.equals(owner(), that.owner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name(), owner());
    }

    record Owner(String login) {

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Owner owner)) return false;
            return Objects.equals(login(), owner.login());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(login());
        }
    }

    record GitHubBranch(
            String name,
            Commit commit
    ) {
        record Commit(String sha) {
        }
    }
}