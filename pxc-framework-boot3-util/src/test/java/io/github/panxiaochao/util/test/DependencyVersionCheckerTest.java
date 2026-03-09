package io.github.panxiaochao.util.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * </p>
 *
 * @author lypxc
 * @since 2026-03-09
 * @version 1.0
 */
public class DependencyVersionCheckerTest {

    private static final String MAVEN_CENTRAL_API = "https://search.maven.org/solrsearch/select";

    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        try {
            ResponseEntity<String> response = restTemplate
                .getForEntity("https://repo1.maven.org/maven2/org/springframework/spring-core/", String.class);

            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
        }
        catch (RestClientException e) {
            System.err.println("请求失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

}
