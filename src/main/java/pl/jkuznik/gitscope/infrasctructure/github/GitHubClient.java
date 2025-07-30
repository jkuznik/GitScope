package pl.jkuznik.gitscope.infrasctructure.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
public class GitHubClient {

    private final RestClient restClient;

    // info source: https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
    public String getReposByUsername(String username, String token) {
        try {
            return restClient.get()
                    .uri("/users/{username}/repos", username)
                    .headers(headers -> {
                        headers.set(HttpHeaders.ACCEPT, "application/vnd.github+json");
                        if (token != null && !token.isBlank()) {
                            headers.setBearerAuth(token);
                        }
                    })
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.error("Error fetching repos for user '{}': {}", username, e.getMessage());
            throw new RuntimeException("Failed to fetch repositories from GitHub", e);
        }
    }
}
