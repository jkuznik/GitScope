package pl.jkuznik.gitscope.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public List<GitHubRepository> getReposByUsername(String username) {
        try {
            String reposAsString = gitHubClient.getPublicRepos(username);
            JsonNode parsedRepos = objectMapper.readTree(reposAsString);
            return parseRepositories(parsedRepos)
                    .stream()
                    .filter(repo -> repo.ownerLogin().equalsIgnoreCase(username))
                    .toList();
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
            String reposAsString = gitHubClient.getPrivateRepos(token);
            JsonNode parsedRepos = objectMapper.readTree(reposAsString);
            return parseRepositories(parsedRepos);
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

    private List<GitHubRepository> parseRepositories(JsonNode repos) {
        List<GitHubRepository> result = new ArrayList<>();

        for (JsonNode repo : repos) {
            if (repo.get("fork").asBoolean()) continue;

            String ownerLogin = repo.get("owner").get("login").asText();
            String repoName = repo.get("name").asText();
            boolean isPrivate = repo.get("private").asBoolean();

            List<GitHubBranch> branches = fetchBranches(ownerLogin, repoName, isPrivate);
            result.add(new GitHubRepository(repoName, ownerLogin, branches));
        }

        return result;
    }

    private List<GitHubBranch> fetchBranches(String ownerLogin, String repoName, boolean isPrivate) {
        List<GitHubBranch> branches = new ArrayList<>();

        try {
            if (isPrivate) {
                branches.add(new GitHubBranch(
                        "N/A (private repository)",
                        "Branch details not accessible"
                ));
            } else {
                String branchesAsString = gitHubClient.getBranches(ownerLogin, repoName);

                JsonNode parsedBranches = objectMapper.readTree(branchesAsString);
                for (JsonNode branch : parsedBranches) {
                    String branchName = branch.get("name").asText();
                    String lastSha = branch.get("commit").get("sha").asText();
                    branches.add(new GitHubBranch(branchName, lastSha));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch branches for repo {}/{}: {}", ownerLogin, repoName, e.getMessage());
            branches.add(new GitHubBranch("Error", "Failed to fetch branch details"));
        }

        return branches;
    }
}