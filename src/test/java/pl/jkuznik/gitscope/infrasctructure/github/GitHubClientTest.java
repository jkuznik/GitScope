package pl.jkuznik.gitscope.infrasctructure.github;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GitHubClientTest {

    @Autowired
    private GitHubClient gitHubClient;

    private final String username = "jkuznik";
    private final String expectedRepo = "GitScope";
    private final String token = null;

    @Test
    void getReposByUsername_shouldReturnExpectedRepo() {
        // when
        String result = gitHubClient.getReposByUsername(username, token);

        // then
        assertThat(result.contains(expectedRepo)).isTrue();
    }

    @Test
    void getBranches_shouldReturnExpectedBranches() {
        // given
        var expectedBranch = "main";

        // when
        String result = gitHubClient.getBranches(username, expectedRepo, token);

        // then
        assertThat(result.contains(expectedBranch)).isTrue();
    }
}