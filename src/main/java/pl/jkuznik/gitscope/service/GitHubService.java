package pl.jkuznik.gitscope.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import pl.jkuznik.gitscope.exception.GitHubUserNotFoundException;
import pl.jkuznik.gitscope.infrasctructure.github.GitHubClient;
import pl.jkuznik.gitscope.model.github.GitHubBranch;
import pl.jkuznik.gitscope.model.github.GitHubRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubService {

    private final GitHubClient gitHubClient;

    public List<GitHubRepository> getReposByUsername(String username) {
        try {
            List<GitHubRepository> publicRepos = gitHubClient.getPublicRepos(username);
            return parseRepositories(publicRepos.stream()
                    .filter(repo -> repo.owner().login().equalsIgnoreCase(username))
                    .toList());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new GitHubUserNotFoundException(username);
            }
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch public repositories for '{}'", username, e);
            throw new RuntimeException("Unable to fetch data from GitHub");
        }
    }

    public List<GitHubRepository> getAllRepos(String token) {
        try {
            List<GitHubRepository> privateRepos = gitHubClient.getPrivateRepos(token);
            return parseRepositories(privateRepos);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Unauthorized GitHub token");
            }
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch repositories using token", e);
            throw new RuntimeException("Unable to fetch data from GitHub");
        }
    }

    private List<GitHubRepository> parseRepositories(List<GitHubRepository> repos) {
        List<GitHubRepository> result = new ArrayList<>();

        repos.stream()
                .filter(repo -> !repo.fork())
                .forEach(repo -> {
                    List<GitHubBranch> branches = fetchBranches(repo.owner().login(), repo.name(), repo.isPrivate());
                    result.add(new GitHubRepository(repo.name(), repo.owner(), repo.fork(), repo.isPrivate(), branches));
                });

        return result;
    }

    private List<GitHubBranch> fetchBranches(String ownerLogin, String repoName, boolean isPrivate) {
        List<GitHubBranch> branches = new ArrayList<>();

        try {
            if (isPrivate) {
                branches.add(new GitHubBranch(
                        "N/A (private repository)",
                        new GitHubBranch.Commit("Branch details not accessible")
                ));
            } else {
                List<GitHubBranch> retrievedBranches = gitHubClient.getBranches(ownerLogin, repoName);

                retrievedBranches.forEach(branch -> {
                    branches.add(new GitHubBranch(branch.name(), branch.commit()));
                });
            }
        } catch (Exception e) {
            log.warn("Failed to fetch branches for repo {}/{}: {}", ownerLogin, repoName, e.getMessage());
            branches.add(new GitHubBranch("Error", new GitHubBranch.Commit("Failed to fetch branch details")));
        }

        return branches;
    }
}