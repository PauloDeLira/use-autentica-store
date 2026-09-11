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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductVariantAdminControllerTest extends AbstractIntegrationTest {

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

    private String createProduct(String token) throws Exception {
        String categoryId = createEntity(token, "/api/admin/categories", Map.of("name", "Categoria " + UUID.randomUUID()));
        return createEntity(token, "/api/admin/products", Map.of(
                "name", "Camiseta Oversized",
                "price", 89.90,
                "categoryId", categoryId
        ));
    }

    private String createSize(String token) throws Exception {
        return createEntity(token, "/api/admin/sizes", Map.of("name", "T" + UUID.randomUUID().toString().substring(0, 8), "displayOrder", 1));
    }

    private String createColor(String token) throws Exception {
        return createEntity(token, "/api/admin/colors", Map.of("name", "C" + UUID.randomUUID().toString().substring(0, 8), "hexCode", "#FF0000"));
    }

    @Test
    void createsVariantForProduct() throws Exception {
        String token = adminToken();
        String productId = createProduct(token);
        String sizeId = createSize(token);
        String colorId = createColor(token);

        mockMvc.perform(post("/api/admin/products/{productId}/variants", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "sizeId", sizeId,
                                "colorId", colorId,
                                "stockQuantity", 10
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.stockQuantity").value(10))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void rejectsDuplicateSizeColorCombination() throws Exception {
        String token = adminToken();
        String productId = createProduct(token);
        String sizeId = createSize(token);
        String colorId = createColor(token);
        Map<String, Object> body = Map.of("sizeId", sizeId, "colorId", colorId, "stockQuantity", 10);

        mockMvc.perform(post("/api/admin/products/{productId}/variants", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/admin/products/{productId}/variants", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("VARIANT_ALREADY_EXISTS"));
    }

    @Test
    void softDeletesVariant() throws Exception {
        String token = adminToken();
        String productId = createProduct(token);
        String sizeId = createSize(token);
        String colorId = createColor(token);

        MvcResult createResult = mockMvc.perform(post("/api/admin/products/{productId}/variants", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "sizeId", sizeId, "colorId", colorId, "stockQuantity", 10))))
                .andReturn();
        String variantId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(delete("/api/admin/products/{productId}/variants/{variantId}", productId, variantId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        String anotherColorId = createColor(token);
        mockMvc.perform(put("/api/admin/products/{productId}/variants/{variantId}", productId, variantId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "sizeId", sizeId, "colorId", anotherColorId, "stockQuantity", 3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(3));
    }

    @Test
    void rejectsUpdateWithDuplicateSizeColorCombination() throws Exception {
        String token = adminToken();
        String productId = createProduct(token);
        String sizeId = createSize(token);
        String colorA = createColor(token);
        String colorB = createColor(token);

        // variação já existente com size+colorA
        mockMvc.perform(post("/api/admin/products/{productId}/variants", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "sizeId", sizeId, "colorId", colorA, "stockQuantity", 5))))
                .andExpect(status().isCreated());

        // variação a ser editada, ainda com size+colorB
        MvcResult toEditResult = mockMvc.perform(post("/api/admin/products/{productId}/variants", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "sizeId", sizeId, "colorId", colorB, "stockQuantity", 5))))
                .andReturn();
        String variantId = objectMapper.readTree(toEditResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/api/admin/products/{productId}/variants/{variantId}", productId, variantId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "sizeId", sizeId, "colorId", colorA, "stockQuantity", 5))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("VARIANT_ALREADY_EXISTS"));
    }

    @Test
    void rejectsCreateWithoutToken() throws Exception {
        mockMvc.perform(post("/api/admin/products/{productId}/variants", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "sizeId", UUID.randomUUID().toString(),
                                "colorId", UUID.randomUUID().toString(),
                                "stockQuantity", 1
                        ))))
                .andExpect(status().isUnauthorized());
    }
}
