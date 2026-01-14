package com.kt.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.admin-cli-secret}") // Admin 권한을 가진 클라이언트 시크릿
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();



    public Map<String, Object> login(String username, String password) {
        String url = String.format("%s/realms/%s/protocol/openid-connect/token", authServerUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return restTemplate.postForObject(url, request, Map.class);
    }

    public void register(String username, String email, String password) {
        String adminToken = getAdminToken(); // 사용자 생성을 위한 관리자 토큰 획득
        String url = String.format("%s/admin/realms/%s/users", authServerUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("enabled", true);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);

        user.put("credentials", List.of(credentials));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
        URI uri = restTemplate.postForLocation(url, request);

        String location = uri.getPath();
        String userId = location.substring(location.lastIndexOf("/") + 1);
        assignUserRole(headers, userId);
    }

    public void logout(String refreshToken) {
        String url = String.format("%s/realms/%s/protocol/openid-connect/logout", authServerUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        restTemplate.postForObject(url, request, String.class);
    }

    public void updateUserPassword(String userName, String newPassword) {
        String adminToken = getAdminToken();
        String userId = getUserIdByUsername(userName, adminToken);
        String url = String.format("%s/admin/realms/%s/users/%s/reset-password", authServerUrl, realm, userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", newPassword);
        credentials.put("temporary", false);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(credentials, headers);
        restTemplate.put(url, request);
    }

    private String getAdminToken() {
        // 1. 토큰 요청 엔드포인트 설정
        String url = String.format("%s/realms/%s/protocol/openid-connect/token", authServerUrl, realm);

        // 2. 헤더 설정 (Form 데이터 전송 형식)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 3. 파라미터 설정 (관리자 권한을 가진 클라이언트 정보)
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", clientId); // 혹은 직접 생성한 관리자용 Client ID
        map.add("client_secret", clientSecret); // Client Secret 값

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // 4. Keycloak 서버에 요청
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        // 5. 응답 본문에서 access_token만 추출하여 반환
        if (response.getBody() != null && response.getBody().containsKey("access_token")) {
            return (String) response.getBody().get("access_token");
        }

        throw new RuntimeException("Keycloak 관리자 토큰을 가져오는 데 실패했습니다.");
    }

    private void assignUserRole(HttpHeaders headers, String userId) {
        assingRole(headers, userId, "kt-user");
    }

    private void assignAdminRole(HttpHeaders headers, String userId) {
        assingRole(headers, userId, "kt-admin");
    }

    private void assingRole(HttpHeaders headers, String userId, String roleName) {
        // Role 정보 조회
        String getRoleUrl = String.format("%s/admin/realms/%s/roles/%s", authServerUrl, realm, roleName);
        ResponseEntity<Map> roleResponse = restTemplate.exchange(getRoleUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        // 사용자에게 Role 부여
        Map<String, Object> roleInfo = roleResponse.getBody();
        String roleMappingUrl = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm", authServerUrl, realm, userId);
        List<Map<String, Object>> rolesToAssign = List.of(Map.of(
                "id", roleInfo.get("id"),     // 조회한 Role의 UUID
                "name", roleInfo.get("name")  // Role의 이름 (user)
        ));

        HttpEntity<List<Map<String, Object>>> roleRequest = new HttpEntity<>(rolesToAssign, headers);
        restTemplate.postForEntity(roleMappingUrl, roleRequest, Void.class);
    }

    private String getUserIdByUsername(String username, String adminToken) {
        String url = String.format("%s/admin/realms/%s/users?username=%s&exact=true", authServerUrl, realm, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
        List<Map<String, Object>> users = response.getBody();

        if (users != null && !users.isEmpty()) {
            return (String) users.get(0).get("id");
        }

        throw new RuntimeException("사용자를 찾을 수 없습니다.");
    }
}
