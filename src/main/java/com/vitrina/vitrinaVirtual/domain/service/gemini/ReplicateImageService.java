package com.vitrina.vitrinaVirtual.domain.service.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitrina.vitrinaVirtual.domain.dto.OutfitRecommendation;
import com.vitrina.vitrinaVirtual.domain.dto.ProductWithStoreDto;
import com.vitrina.vitrinaVirtual.domain.service.CloudinaryService;
import com.vitrina.vitrinaVirtual.domain.dto.ProductDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ReplicateImageService {

    private static final Log logger = LogFactory.getLog(ReplicateImageService.class);

    private final RestTemplate restTemplate;
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper;

    @Value("${replicate.api.key}")
    private String replicateApiKey;

    @Value("${replicate.api.base-url:https://api.replicate.com/v1}")
    private String replicateApiBaseUrl;

    // FLUX.1 [dev] - Modelo gratuito y de alta calidad
    private static final String FLUX_MODEL_VERSION = "black-forest-labs/flux-dev";
    private static final int MAX_POLLING_ATTEMPTS = 60; // 60 segundos máximo
    private static final int POLLING_INTERVAL_MS = 1000; // Cada 1 segundo

    public ReplicateImageService(RestTemplate restTemplate, CloudinaryService cloudinaryService,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.cloudinaryService = cloudinaryService;
        this.objectMapper = objectMapper;
    }

    /**
     * Genera una imagen fotorealista del outfit usando FLUX via Replicate.
     */
    public String generateOutfitImage(OutfitRecommendation outfit, String gender, String climate, String style) {
        try {
            logger.info("=== INICIANDO GENERACIÓN DE IMAGEN CON REPLICATE (FLUX) ===");
            logger.info("Productos en outfit: " + outfit.getSelectedProducts().size());

            // 1. Construir prompt (reutilizar lógica de StabilityAI)
            String prompt = buildPhotorealisticPrompt(outfit, gender, climate, style);
            logger.info("Prompt generado (longitud: " + prompt.length() + " chars)");

            // 2. Iniciar predicción en Replicate
            String predictionId = startPrediction(prompt);
            logger.info("Predicción iniciada con ID: " + predictionId);

            // 3. Esperar a que se complete (polling)
            String imageUrl = pollForCompletion(predictionId);
            logger.info("Imagen generada: " + imageUrl);

            // 4. Descargar y convertir a Base64
            String base64Image = downloadAndConvertToBase64(imageUrl);

            // 5. Subir a Cloudinary
            String publicId = "outfit_flux_" + System.currentTimeMillis();
            String cloudinaryUrl = cloudinaryService.uploadBase64Image(base64Image, "vitrina_virtual/outfits",
                    publicId);

            logger.info("=== IMAGEN SUBIDA EXITOSAMENTE ===");
            logger.info("URL: " + cloudinaryUrl);

            return cloudinaryUrl;

        } catch (Exception e) {
            logger.error("Error generando imagen con Replicate", e);
            return null;
        }
    }

    /**
     * Inicia una predicción en Replicate y devuelve el ID.
     */
    private String startPrediction(String prompt) throws Exception {
        String apiUrl = replicateApiBaseUrl + "/predictions";

        Map<String, Object> input = Map.of(
                "prompt", prompt,
                // "aspect_ratio", "9:16",
                "width", 480,
                "height", 854,
                "output_format", "png",
                "output_quality", 80,
                "num_inference_steps", 10, // Balance calidad/velocidad
                "guidance_scale", 3.5);

        Map<String, Object> requestBody = Map.of(
                "version", FLUX_MODEL_VERSION,
                "input", input);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + replicateApiKey);
        headers.set("Prefer", "wait"); // Intenta esperar respuesta inmediata

        HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(requestBody),
                headers);

        logger.debug("POST " + apiUrl);
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.POST, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Replicate API error: " + response.getStatusCode());
        }

        JsonNode responseJson = objectMapper.readTree(response.getBody());
        return responseJson.path("id").asText();
    }

    /**
     * Hace polling hasta que la imagen esté lista.
     */
    private String pollForCompletion(String predictionId) throws Exception {
        String statusUrl = replicateApiBaseUrl + "/predictions/" + predictionId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + replicateApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        for (int attempt = 0; attempt < MAX_POLLING_ATTEMPTS; attempt++) {
            logger.debug("Polling intento " + (attempt + 1) + "/" + MAX_POLLING_ATTEMPTS);

            ResponseEntity<String> response = restTemplate.exchange(
                    statusUrl, HttpMethod.GET, entity, String.class);

            JsonNode statusJson = objectMapper.readTree(response.getBody());
            String status = statusJson.path("status").asText();

            logger.debug("Status: " + status);

            if ("succeeded".equals(status)) {
                // La salida puede ser un string o un array
                JsonNode output = statusJson.path("output");
                if (output.isArray() && output.size() > 0) {
                    return output.get(0).asText();
                } else if (output.isTextual()) {
                    return output.asText();
                }
                throw new RuntimeException("Output format desconocido");
            } else if ("failed".equals(status) || "canceled".equals(status)) {
                String error = statusJson.path("error").asText("Unknown error");
                throw new RuntimeException("Generación falló: " + error);
            }

            // Esperar antes del siguiente intento
            Thread.sleep(POLLING_INTERVAL_MS);
        }

        throw new RuntimeException("Timeout esperando generación de imagen (60s)");
    }

    /**
     * Descarga la imagen desde URL y convierte a Base64.
     */
    private String downloadAndConvertToBase64(String imageUrl) throws Exception {
        logger.info("Descargando imagen desde: " + imageUrl);

        ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error descargando imagen desde Replicate");
        }

        return Base64.getEncoder().encodeToString(response.getBody());
    }

    // ============ MÉTODOS REUTILIZADOS DE STABILITY AI ============

    /**
     * Construye un prompt fotorrealista optimizado para generación de imágenes de
     * outfit.
     * Incluye: modelo, pose, iluminación, fondo, descripción de prendas y concepto
     * del outfit.
     */
    private String buildPhotorealisticPrompt(OutfitRecommendation outfit, String gender, String climate, String style) {
        StringBuilder prompt = new StringBuilder();

        // 1. Tipo de fotografía base
        prompt.append("professional fashion editorial photography, full body portrait, ");

        // 2. Modelo y pose específica
        prompt.append(getModelAndPose(gender, style)).append(", ");

        // 3. Estilo fotográfico e iluminación
        prompt.append(getPhotoStyle(style)).append(", ");

        // 4. Fondo contextual
        prompt.append(getBackgroundByStyle(style)).append(", ");

        // 5. Ambiente climático
        prompt.append(getClimateAmbiance(climate)).append(", ");

        // 6. Concepto del outfit (si existe)
        if (hasValue(outfit.getReason())) {
            prompt.append("outfit concept: ").append(outfit.getReason()).append(", ");
        }

        // 7. Descripción detallada de las prendas
        prompt.append("wearing: ");
        List<ProductWithStoreDto> products = outfit.getSelectedProducts();
        for (int i = 0; i < products.size(); i++) {
            ProductDto product = products.get(i).getProduct();

            if (hasValue(product.getTechnicalDescription())) {
                prompt.append(product.getTechnicalDescription());
            } else {
                prompt.append(buildManualDescription(product));
            }

            if (i < products.size() - 1) {
                prompt.append(", ");
            }
        }

        // 8. Calidad y detalles técnicos
        prompt.append(". ");
        prompt.append("sharp focus, high resolution, detailed fabric texture, ");
        prompt.append("realistic materials and colors, professional color grading, ");
        prompt.append("fashion magazine quality, photorealistic");

        return prompt.toString();
    }

    /**
     * Construye una descripción manual detallada del producto cuando no hay
     * technicalDescription.
     * Usa detección inteligente del tipo de prenda basada en el nombre.
     */
    private String buildManualDescription(ProductDto product) {
        StringBuilder desc = new StringBuilder();

        // Usar detección inteligente de tipo de prenda
        String garmentType = translateGarmentType(product.getGarmentType(), product.getName());
        desc.append(garmentType);

        if (hasValue(product.getPrimaryColor())) {
            desc.append(" in ").append(product.getPrimaryColor().toLowerCase());
        }

        if (hasValue(product.getMaterial())) {
            desc.append(", ").append(product.getMaterial().toLowerCase()).append(" material");
        }

        if (hasValue(product.getPattern()) && !"Liso".equalsIgnoreCase(product.getPattern())) {
            desc.append(" with ").append(product.getPattern().toLowerCase()).append(" pattern");
        }

        if (hasValue(product.getFit())) {
            desc.append(", ").append(translateFit(product.getFit())).append(" fit");
        }

        return desc.toString();
    }

    /**
     * Retorna el modelo y la pose específica según género y estilo.
     */
    private String getModelAndPose(String gender, String style) {
        boolean isFemale = "Femenino".equalsIgnoreCase(gender);
        String styleKey = style != null ? style.toLowerCase() : "";

        if (isFemale) {
            return switch (styleKey) {
                case "formal", "elegante" ->
                    "elegant female model in her late 20s, sophisticated pose with hand on hip, graceful stance, confident expression";
                case "deportivo" ->
                    "athletic female model, dynamic sporty pose, energetic stance, fit physique";
                case "casual" ->
                    "natural female model, relaxed casual pose, friendly expression, approachable demeanor";
                case "urbano" ->
                    "trendy female model, edgy urban pose, modern attitude, street style confidence";
                default ->
                    "elegant female model, confident pose, natural stance";
            };
        } else {
            return switch (styleKey) {
                case "formal", "elegante" ->
                    "handsome male model in his early 30s, standing confidently with hands in pockets, professional business posture, authoritative presence";
                case "deportivo" ->
                    "athletic male model, dynamic sporty stance, strong physique, energetic pose";
                case "casual" ->
                    "natural male model, relaxed casual stance, hands in pockets or crossed arms, approachable look";
                case "urbano" ->
                    "trendy male model, urban street pose, modern edge, confident attitude";
                default ->
                    "handsome male model, confident stance, natural posture";
            };
        }
    }

    /**
     * Retorna el estilo fotográfico e iluminación específica según el estilo del
     * outfit.
     */
    private String getPhotoStyle(String style) {
        if (style == null)
            return "professional studio photography with balanced lighting";

        return switch (style.toLowerCase()) {
            case "formal", "elegante" ->
                "elegant high-fashion photography, dramatic three-point studio lighting with rim light, luxury aesthetic, professional setup";
            case "deportivo" ->
                "dynamic athletic photography, bright energetic lighting, clean modern aesthetic";
            case "casual" ->
                "lifestyle photography, soft natural lighting, warm golden hour ambiance, relaxed atmosphere";
            case "urbano" ->
                "urban street style photography, edgy directional lighting, contemporary trendy look";
            default ->
                "professional editorial photography, balanced studio lighting, clean aesthetic";
        };
    }

    /**
     * Retorna el fondo contextual apropiado según el estilo del outfit.
     */
    private String getBackgroundByStyle(String style) {
        if (style == null)
            return "clean neutral background";

        return switch (style.toLowerCase()) {
            case "formal", "elegante" ->
                "luxury interior setting, marble floor, elegant minimalist backdrop, sophisticated environment";
            case "deportivo" ->
                "modern gym environment, clean athletic setting, minimalist sports background";
            case "casual" ->
                "natural outdoor setting, soft daylight, park or urban cafe background, relaxed environment";
            case "urbano" ->
                "urban street background, concrete wall, city atmosphere, contemporary setting";
            default ->
                "clean white studio background, professional setting";
        };
    }

    private String getClimateAmbiance(String climate) {
        if (climate == null)
            return "neutral temperature atmosphere";

        return switch (climate.toLowerCase()) {
            case "frío", "frio" -> "winter season atmosphere, cool color tones";
            case "cálido", "calido" -> "summer season atmosphere, warm color tones";
            case "templado" -> "spring/autumn atmosphere, balanced color tones";
            default -> "neutral season, balanced lighting";
        };
    }

    /**
     * Traduce el tipo de prenda de forma inteligente, detectando el tipo específico
     * a partir del nombre del producto para mayor precisión en la generación de
     * imágenes.
     */
    private String translateGarmentType(String garmentType, String productName) {
        if (garmentType == null)
            return "clothing item";

        String name = productName != null ? productName.toLowerCase() : "";

        return switch (garmentType.toUpperCase()) {
            case "TOP" -> {
                if (name.contains("camisa"))
                    yield "dress shirt";
                if (name.contains("blusa"))
                    yield "blouse";
                if (name.contains("camiseta"))
                    yield "t-shirt";
                if (name.contains("polo"))
                    yield "polo shirt";
                if (name.contains("suéter") || name.contains("sueter"))
                    yield "sweater";
                if (name.contains("hoodie"))
                    yield "hoodie";
                if (name.contains("crop"))
                    yield "crop top";
                if (name.contains("tank"))
                    yield "tank top";
                yield "top";
            }
            case "BOTTOM" -> {
                if (name.contains("pantalón") || name.contains("pantalon")) {
                    if (name.contains("vestir"))
                        yield "dress trousers";
                    if (name.contains("jean"))
                        yield "denim jeans";
                    yield "trousers";
                }
                if (name.contains("falda"))
                    yield "skirt";
                if (name.contains("short"))
                    yield "shorts";
                if (name.contains("legging"))
                    yield "leggings";
                if (name.contains("jogger"))
                    yield "joggers";
                yield "bottom";
            }
            case "DRESS" -> "dress";
            case "OUTERWEAR" -> {
                if (name.contains("blazer"))
                    yield "blazer";
                if (name.contains("chaqueta"))
                    yield "jacket";
                if (name.contains("abrigo"))
                    yield "coat";
                if (name.contains("saco"))
                    yield "suit jacket";
                if (name.contains("cardigan"))
                    yield "cardigan";
                if (name.contains("bomber"))
                    yield "bomber jacket";
                yield "outerwear";
            }
            case "FOOTWEAR" -> {
                if (name.contains("zapato"))
                    yield "dress shoes";
                if (name.contains("tenis"))
                    yield "sneakers";
                if (name.contains("bota"))
                    yield "boots";
                if (name.contains("sandalia"))
                    yield "sandals";
                if (name.contains("tacón") || name.contains("tacon"))
                    yield "high heels";
                if (name.contains("mocasín") || name.contains("mocasin"))
                    yield "loafers";
                yield "shoes";
            }
            case "ACCESSORY" -> "accessory";
            default -> "garment";
        };
    }

    private String translateFit(String fit) {
        if (fit == null)
            return "regular";

        return switch (fit.toLowerCase()) {
            case "ajustado" -> "fitted";
            case "holgado" -> "loose";
            case "oversize" -> "oversized";
            default -> "regular";
        };
    }

    private static boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
