package pl.jkuznik.gitscope.infrasctructure.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import pl.jkuznik.gitscope.model.github.GitHubBranch;
import pl.jkuznik.gitscope.model.github.GitHubRepository;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class GitHubClient {

    private final RestClient restClient;

    // info source: https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
    public List<GitHubRepository> getPublicRepos(String username) {
        try {
                return restClient.get()
                        .uri("/users/{username}/repos", username)
                        .headers(buildHeaders(null))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
                throw e;
        } catch (Exception e) {
            log.error("Error fetching repos for user '{}': {}", username, e.getMessage());
            throw new RuntimeException("Failed to fetch repositories from GitHub", e);
        }
    }

    public List<GitHubRepository> getPrivateRepos(String token) {
        try {
            return restClient.get()
                    .uri("/user/repos")
                    .headers(buildHeaders(token))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
        throw e;
    } catch (Exception e) {
        log.error("Error fetching repos with token '{}': {}", token, e.getMessage());
        throw new RuntimeException("Failed to fetch repositories from GitHub", e);
    }
    }

    public List<GitHubBranch> getBranches(String owner, String repo) {
        try {
            return restClient.get()
                    .uri("/repos/{owner}/{repo}/branches", owner, repo)
                    .headers(buildHeaders(null))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching branches for {}/{}: {}", owner, repo, e.getMessage());
            throw new RuntimeException("Failed to fetch branches from GitHub", e);
        }
    }

    private Consumer<HttpHeaders> buildHeaders(String token) {
        return headers -> {
            headers.set(HttpHeaders.ACCEPT, "application/vnd.github+json");
            headers.set("X-GitHub-Api-Version", "2022-11-28");
            if (withToken(token)) {
                String cleanToken = token.replaceAll("Bearer", "").trim();
                headers.setBearerAuth(cleanToken);
                log.info("Using cleaned token: {}", cleanToken);
            }
        };
    }

    private boolean withToken(String token) {
        return token != null && !token.isBlank();
    }
}
