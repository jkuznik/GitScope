package pl.jkuznik.gitscope.infrasctructure.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class GitHubClient {

    private final RestClient restClient;

    // info source: https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
    public String getReposByUsername(String username, String token) {
        try {
            if (withToken(token)) {
                return restClient.get()
                        .uri("/user/repos")
                        .headers(buildHeaders(token))
                        .retrieve()
                        .body(String.class);
            } else {
                return restClient.get()
                        .uri("/users/{username}/repos", username)
                        .headers(buildHeaders(token))
                        .retrieve()
                        .body(String.class);
            }
        } catch (HttpClientErrorException e) {
                throw e;
        } catch (Exception e) {
            log.error("Error fetching repos for user '{}': {}", username, e.getMessage());
            throw new RuntimeException("Failed to fetch repositories from GitHub", e);
        }
    }

    public String getBranches(String owner, String repo, String token) {
        try {
            return restClient.get()
                    .uri("/repos/{owner}/{repo}/branches", owner, repo)
                    .headers(buildHeaders(token))
                    .retrieve()
                    .body(String.class);
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
                log.debug("Using cleaned token: {}", cleanToken);
            }
        };
    }

    private boolean withToken(String token) {
        return token != null && !token.isBlank();
    }
}
