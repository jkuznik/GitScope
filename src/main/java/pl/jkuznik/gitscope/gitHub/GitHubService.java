package pl.jkuznik.gitscope.gitHub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import pl.jkuznik.gitscope.gitHub.GitHubRepositoryModel.GitHubBranch;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class GitHubService {

    private final GitHubClient gitHubClient;

    public List<GitHubRepositoryModel> getPublicReposByUsername(String username) {
        try {
            List<GitHubRepositoryModel> publicRepos = gitHubClient.getPublicRepos(username);
            return filterForkRepositories(publicRepos.stream()
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

    public List<GitHubRepositoryModel> getAllReposByToken(String token) {
        try {
            List<GitHubRepositoryModel> privateRepos = gitHubClient.getPrivateRepos(token);
            return filterForkRepositories(privateRepos);
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

    private List<GitHubRepositoryModel> filterForkRepositories(List<GitHubRepositoryModel> repos) {
        List<GitHubRepositoryModel> result = new ArrayList<>();

        repos.stream()
                .filter(repo -> !repo.fork())
                .forEach(repo -> {
                    List<GitHubBranch> branches = fetchBranches(repo.owner().login(), repo.name(), repo.isPrivate());
                    result.add(new GitHubRepositoryModel(repo.name(), repo.owner(), false, repo.isPrivate(), branches));
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