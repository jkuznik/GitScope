package pl.jkuznik.gitscope.infrasctructure.github;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(GitHubProperties.class)
@RequiredArgsConstructor
class GitHubConfig {

    private final GitHubProperties gitHubProperties;

    @Bean
    public GitHubClient gitHubClient() {
        RestClient restClient =
                RestClient.builder()
                        .baseUrl(gitHubProperties.getBaseUrl())
                        .build();

        return new GitHubClient(restClient);
    }
}
