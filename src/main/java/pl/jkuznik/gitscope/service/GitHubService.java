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

    public List<GitHubRepository> getUserRepositories(String username, String token) {
        List<GitHubRepository> result = new ArrayList<>();

        try {
            String reposAsString = gitHubClient.getReposByUsername(username, token);
            JsonNode parsedRepos = objectMapper.readTree(reposAsString);

            for (JsonNode repo : parsedRepos) {
                if (repo.get("fork").asBoolean()) continue;

                String ownerLogin = repo.get("owner").get("login").asText();
                if (!ownerLogin.equalsIgnoreCase(username)) continue;

                String repoName = repo.get("name").asText();
                boolean isPrivate = repo.get("private").asBoolean();

                List<GitHubBranch> branches = new ArrayList<>();
                if (!isPrivate) {
                    String branchesAsString = gitHubClient.getBranches(ownerLogin, repoName, token);
                    JsonNode parsedBranches = objectMapper.readTree(branchesAsString);

                    for (JsonNode branch : parsedBranches) {
                        String branchName = branch.get("name").asText();
                        String lastSha = branch.get("commit").get("sha").asText();

                        branches.add(new GitHubBranch(branchName, lastSha));
                    }
                } else {
                    branches.add(new GitHubBranch(
                            "N/A (private repository)",
                            "Branch details not accessible"
                    ));
                }

                result.add(new GitHubRepository(repoName, ownerLogin, branches));
            }

            log.info("Retrieved {} repositories", result.size());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new GitHubUserNotFoundException(username);
            } else {
                throw e;
            }
        } catch (Exception e) {
            log.error("Failed to fetch repositories or branches for user '{}'", username, e);
            throw new RuntimeException("Unable to fetch data from GitHub caused unexpected error");
        }

        return result;
    }
}