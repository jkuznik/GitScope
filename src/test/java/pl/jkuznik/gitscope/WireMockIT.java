package pl.jkuznik.gitscope;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public abstract class WireMockIT {

    public static WireMockServer wireMockGitHubServer;

    @BeforeAll
    static void startWireMock() {
        wireMockGitHubServer = new WireMockServer(options().dynamicPort());
        wireMockGitHubServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockGitHubServer.stop();
    }

    @BeforeEach
    void resetWireMock() {
        wireMockGitHubServer.resetAll();
    }

    @DynamicPropertySource
    static void registerBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("properties.github.base-url", wireMockGitHubServer::baseUrl);
    }
}
