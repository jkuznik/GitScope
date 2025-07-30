package pl.jkuznik.gitscope.infrasctructure.github;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GitHubClientTest {

    @Autowired
    private GitHubClient gitHubClient;

    @Test
    void getReposByUsername_shouldReturnExpectedRepo() {
        // given
        var username = "jkuznik";
        var expectedRepo = "GitScope";

        // when
        String reposByUsername = gitHubClient.getReposByUsername(username, null);

        // then
        assertThat(reposByUsername.contains(expectedRepo)).isTrue();
    }

    @Test
    void getBranches_shouldReturnExpectedBranches() {

    }
}