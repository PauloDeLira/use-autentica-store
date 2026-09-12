package br.com.useautentica.backend.controller.admin;

import br.com.useautentica.backend.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardAdminControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${admin.seed.email}")
    private String adminEmail;

    @Value("${admin.seed.password}")
    private String adminPassword;

    private String adminToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", adminEmail,
                                "password", adminPassword
                        ))))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private String createEntity(String token, String path, Map<String, Object> body) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void rejectsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/whatsapp-clicks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void countsClicksAndRanksTopProducts() throws Exception {
        String token = adminToken();
        String categoryId = createEntity(token, "/api/admin/categories", Map.of("name", "Categoria " + UUID.randomUUID()));
        String productId = createEntity(token, "/api/admin/products", Map.of(
                "name", "Camiseta Oversized", "price", 89.90, "categoryId", categoryId));

        mockMvc.perform(post("/api/products/{id}/whatsapp-click", productId)).andExpect(status().isNoContent());
        mockMvc.perform(post("/api/products/{id}/whatsapp-click", productId)).andExpect(status().isNoContent());

        mockMvc.perform(get("/api/admin/dashboard/whatsapp-clicks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clicksToday").value(2))
                .andExpect(jsonPath("$.clicksThisWeek").value(2))
                .andExpect(jsonPath("$.topProducts[0].productId").value(productId))
                .andExpect(jsonPath("$.topProducts[0].clicks").value(2));
    }
}
