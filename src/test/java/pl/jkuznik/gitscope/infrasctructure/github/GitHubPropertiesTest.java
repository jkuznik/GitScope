package pl.jkuznik.gitscope.infrasctructure.github;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GitHubPropertiesTest {

    @Autowired
    private GitHubProperties gitHubProperties;

    @Test
    void getBaseUrl() {
        // given
        String baseUrl = "https://api.github.com";

        // when
        String result = gitHubProperties.getBaseUrl();

        // then
        Assertions.assertThat(baseUrl).isEqualTo(result);
    }
}