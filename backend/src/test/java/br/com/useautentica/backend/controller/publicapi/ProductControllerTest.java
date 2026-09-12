package br.com.useautentica.backend.controller.publicapi;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest extends AbstractIntegrationTest {

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

    @Test
    void hidesInactiveProductFromPublicDetails() throws Exception {
        String token = adminToken();

        MvcResult categoryResult = mockMvc.perform(post("/api/admin/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Categoria " + UUID.randomUUID()))))
                .andReturn();
        String categoryId = objectMapper.readTree(categoryResult.getResponse().getContentAsString()).get("id").asText();

        MvcResult productResult = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Camiseta Oversized",
                                "price", 89.90,
                                "categoryId", categoryId
                        ))))
                .andReturn();
        String productId = objectMapper.readTree(productResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(patch("/api/admin/products/{id}/active", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("active", false))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void listIsPubliclyAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
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
    void exposesOnlyActiveVariantsForProductDetail() throws Exception {
        String token = adminToken();
        String categoryId = createEntity(token, "/api/admin/categories", Map.of("name", "Categoria " + UUID.randomUUID()));
        String productId = createEntity(token, "/api/admin/products", Map.of(
                "name", "Camiseta Oversized", "price", 89.90, "categoryId", categoryId));
        String sizeId = createEntity(token, "/api/admin/sizes", Map.of("name", "T" + UUID.randomUUID().toString().substring(0, 8), "displayOrder", 1));
        String colorId = createEntity(token, "/api/admin/colors", Map.of("name", "C" + UUID.randomUUID().toString().substring(0, 8), "hexCode", "#FF0000"));
        String activeVariantId = createEntity(token, "/api/admin/products/" + productId + "/variants", Map.of(
                "sizeId", sizeId, "colorId", colorId, "stockQuantity", 5));

        String otherColorId = createEntity(token, "/api/admin/colors", Map.of("name", "C" + UUID.randomUUID().toString().substring(0, 8), "hexCode", "#00FF00"));
        String inactiveVariantId = createEntity(token, "/api/admin/products/" + productId + "/variants", Map.of(
                "sizeId", sizeId, "colorId", otherColorId, "stockQuantity", 5));
        mockMvc.perform(delete("/api/admin/products/{productId}/variants/{variantId}", productId, inactiveVariantId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}/variants", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(activeVariantId))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void registersWhatsAppClickWithoutToken() throws Exception {
        String token = adminToken();
        String categoryId = createEntity(token, "/api/admin/categories", Map.of("name", "Categoria " + UUID.randomUUID()));
        String productId = createEntity(token, "/api/admin/products", Map.of(
                "name", "Camiseta Oversized", "price", 89.90, "categoryId", categoryId));

        mockMvc.perform(post("/api/products/{id}/whatsapp-click", productId))
                .andExpect(status().isNoContent());
    }

    @Test
    void rejectsWhatsAppClickForNonexistentProduct() throws Exception {
        mockMvc.perform(post("/api/products/{id}/whatsapp-click", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
