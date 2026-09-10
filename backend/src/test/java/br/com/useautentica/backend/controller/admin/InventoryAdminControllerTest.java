package br.com.useautentica.backend.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InventoryAdminControllerTest {

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

    private String createVariant(String token) throws Exception {
        String categoryId = createEntity(token, "/api/admin/categories", Map.of("name", "Categoria " + UUID.randomUUID()));
        String productId = createEntity(token, "/api/admin/products", Map.of(
                "name", "Camiseta Oversized", "price", 89.90, "categoryId", categoryId));
        String sizeId = createEntity(token, "/api/admin/sizes", Map.of("name", "T" + UUID.randomUUID().toString().substring(0, 8), "displayOrder", 1));
        String colorId = createEntity(token, "/api/admin/colors", Map.of("name", "C" + UUID.randomUUID().toString().substring(0, 8), "hexCode", "#FF0000"));
        return createEntity(token, "/api/admin/products/" + productId + "/variants", Map.of(
                "sizeId", sizeId, "colorId", colorId, "stockQuantity", 5));
    }

    @Test
    void updatesStockToAbsoluteValue() throws Exception {
        String token = adminToken();
        String variantId = createVariant(token);

        mockMvc.perform(patch("/api/admin/inventory/{variantId}", variantId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stockQuantity", 42))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(42));
    }

    @Test
    void rejectsNegativeStock() throws Exception {
        String token = adminToken();
        String variantId = createVariant(token);

        mockMvc.perform(patch("/api/admin/inventory/{variantId}", variantId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stockQuantity", -1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listIsProtectedByAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin/inventory"))
                .andExpect(status().isUnauthorized());
    }
}
