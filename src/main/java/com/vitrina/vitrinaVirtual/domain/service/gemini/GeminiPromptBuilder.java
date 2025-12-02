package com.vitrina.vitrinaVirtual.domain.service.gemini;

import com.vitrina.vitrinaVirtual.domain.dto.ProductDto;
import com.vitrina.vitrinaVirtual.domain.dto.ProductWithStoreDto;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Constructor de prompts optimizado para Gemini AI.
 * 
 * OPTIMIZACIONES IMPLEMENTADAS:
 * - Reglas condicionales (solo lo necesario según contexto)
 * - Número de prendas dinámico por clima
 * - Información de productos simplificada (sin tienda ni precio)
 * - Mini-guías de outfit por estilo y género
 * - Few-shot learning con ejemplos contextuales
 * - Instrucciones de accesorios específicas
 * 
 * AHORRO ESTIMADO: 52% de tokens vs versión anterior
 * 
 * @author Vitrina Virtual Team
 * @version 2.0 - Optimized
 */
@Component
public class GeminiPromptBuilder {

    // ============ CONFIGURACIÓN DE PRENDAS POR CLIMA ============

    private static final Map<String, OutfitSizeConfig> OUTFIT_SIZE_BY_CLIMATE = Map.of(
            "Cálido", new OutfitSizeConfig(3, 4),
            "Templado", new OutfitSizeConfig(4, 5),
            "Frío", new OutfitSizeConfig(5, 6));

    private static record OutfitSizeConfig(int min, int max) {
    }

    // ============ MÉTODO PRINCIPAL ============

    /**
     * Construye un prompt optimizado para generación de outfits.
     * El prompt se construye en capas condicionales para minimizar tokens.
     */
    public String buildCoherentOutfitPrompt(
            String gender,
            String climate,
            String style,
            String material,
            List<ProductWithStoreDto> products) {

        StringBuilder prompt = new StringBuilder();

        // 1. Introducción breve
        prompt.append(buildIntro());

        // 2. Perfil del cliente
        prompt.append(buildClientProfile(gender, climate, style, material));

        // 3. Reglas universales (siempre aplican)
        prompt.append(buildUniversalRules());

        // 4. Reglas condicionales (solo las relevantes)
        prompt.append(buildConditionalRules(gender, climate, style));

        // 5. Mini-guía de outfit (template específico)
        prompt.append(buildOutfitTemplate(gender, climate, style));

        // 6. Productos disponibles (simplificados)
        prompt.append(buildProductList(products));

        // 7. Instrucciones de tarea
        prompt.append(buildTaskInstructions(climate, style));

        // 8. Ejemplo few-shot (si aplica)
        prompt.append(buildFewShotExample(gender, climate, style));

        // 9. Formato de respuesta
        prompt.append(buildResponseFormat());

        return prompt.toString();
    }

    // ============ CONSTRUCCIÓN DE SECCIONES ============

    private String buildIntro() {
        return """
                Eres un estilista de moda profesional experto. Tu misión es crear outfits excepcionales
                y coherentes que combinen estilo, funcionalidad y armonía visual.

                """;
    }

    private String buildClientProfile(String gender, String climate, String style, String material) {
        StringBuilder profile = new StringBuilder("### PERFIL DEL CLIENTE\n");

        if (hasValue(gender))
            profile.append("- Género: ").append(gender).append("\n");
        if (hasValue(climate))
            profile.append("- Clima: ").append(climate).append("\n");
        if (hasValue(style))
            profile.append("- Estilo: ").append(style).append("\n");
        if (hasValue(material))
            profile.append("- Material preferido: ").append(material).append("\n");

        profile.append("\n");
        return profile.toString();
    }

    private String buildUniversalRules() {
        return """
                ### REGLAS DE ESTILISMO UNIVERSALES
                1. **Armonía de Color**: Combina colores de la misma familia o usa neutros como base con un acento de color.
                2. **Máximo 1 Patrón Llamativo**: Solo una prenda puede tener patrón llamativo (Floral, Cuadros). El resto debe ser Liso.
                3. **Balance de Silueta**: Combina prendas ajustadas con holgadas, o mantén un fit regular en todo el conjunto.

                """;
    }

    private String buildConditionalRules(String gender, String climate, String style) {
        StringBuilder rules = new StringBuilder("### REGLAS ESPECÍFICAS\n");
        int ruleNumber = 4;

        // Reglas de clima
        if (hasValue(climate)) {
            rules.append(buildClimateRules(climate, ruleNumber));
            ruleNumber++;
        }

        // Reglas de género
        if (hasValue(gender) && "Femenino".equalsIgnoreCase(gender)) {
            rules.append(ruleNumber)
                    .append(". **Opción Vestido**: Puedes usar un DRESS como alternativa a TOP + BOTTOM.\n");
            ruleNumber++;
        }

        // Reglas de estilo
        if (hasValue(style)) {
            rules.append(buildStyleRules(style, gender, ruleNumber));
        }

        rules.append("\n");
        return rules.toString();
    }

    private String buildClimateRules(String climate, int ruleNumber) {
        return switch (climate.toLowerCase()) {
            case "cálido", "calido" -> String.format(
                    "%d. **Clima Cálido**: PROHIBIDO Outerwear pesado. Usa materiales ligeros (Lino, Algodón).\n",
                    ruleNumber);
            case "templado" -> String.format(
                    "%d. **Clima Templado**: Outerwear ligero RECOMENDADO (chaqueta, cardigan).\n",
                    ruleNumber);
            case "frío", "frio" -> String.format(
                    "%d. **Clima Frío**: Outerwear OBLIGATORIO. Prioriza materiales cálidos (Lana). Considera bufanda.\n",
                    ruleNumber);
            default -> "";
        };
    }

    private String buildStyleRules(String style, String gender, int ruleNumber) {
        boolean isFeminine = "Femenino".equalsIgnoreCase(gender);

        return switch (style.toLowerCase()) {
            case "formal", "elegante" -> String.format(
                    "%d. **Estilo %s**: Formalidad 3-5. Calzado: %s. NO mezclar con deportivo.\n",
                    ruleNumber,
                    style,
                    isFeminine ? "zapatillas(tacones)/botines de vestir" : "zapatos formales/mocasines");
            case "deportivo" -> String.format(
                    "%d. **Estilo Deportivo**: Formalidad 1-2. Calzado: tenis deportivos. Materiales sintéticos.\n",
                    ruleNumber);
            case "casual" -> String.format(
                    "%d. **Estilo Casual**: Formalidad 2-4 (Smart Casual). Calzado: tenis/botas/mocasines.\n",
                    ruleNumber);
            case "urbano" -> String.format(
                    "%d. **Estilo Urbano**: Street style moderno. Calzado: tenis. Considera gorra/mochila.\n",
                    ruleNumber);
            default -> "";
        };
    }

    private String buildOutfitTemplate(String gender, String climate, String style) {
        if (!hasValue(style))
            return "";

        StringBuilder template = new StringBuilder("### PLANTILLA DE OUTFIT RECOMENDADA\n");
        boolean isFeminine = "Femenino".equalsIgnoreCase(gender);

        String baseTemplate = switch (style.toLowerCase()) {
            case "formal", "elegante" -> isFeminine
                    ? "OPCIÓN A: Vestido elegante + Zapatillas(tacones)\nOPCIÓN B: Blusa + Falda/Pantalón formal + Zapatillas(tacones) + Blazer(opcional)"
                    : "Camisa formal + Pantalón de vestir + Zapatos formales + Blazer(si clima ≠ Cálido) + Corbata(opcional)";

            case "casual" -> isFeminine
                    ? "Blusa/Camiseta + Jeans/Falda + Tenis/Sandalias/Botas\nOPCIÓN B: Vestido casual + Tenis"
                    : "Camiseta/Polo/Camisa casual + Jeans/Chinos + Tenis/Mocasines";

            case "urbano" -> isFeminine
                    ? "Top/Crop top + Jeans/Leggings + Tenis/Botas + Chaqueta denim/bomber(opcional)"
                    : "Hoodie/Sudadera/Camiseta + Jeans/Joggers + Tenis + Chaqueta bomber(opcional)";

            case "deportivo" -> isFeminine
                    ? "Top deportivo + Leggings/Short deportivo + Tenis deportivos + Chaqueta deportiva(si clima ≠ Cálido)"
                    : "Camiseta deportiva + Pantalón/Short deportivo + Tenis deportivos + Chaqueta deportiva(si clima ≠ Cálido)";

            default -> "Combina TOP + BOTTOM + FOOTWEAR de forma coherente";
        };

        template.append(baseTemplate).append("\n\n");

        // Nota sobre prendas compartidas
        if ("formal".equalsIgnoreCase(style) || "elegante".equalsIgnoreCase(style)) {
            template.append("NOTA: Formal y Elegante comparten prendas. Una camisa formal sirve para ambos.\n\n");
        }

        return template.toString();
    }

    private String buildProductList(List<ProductWithStoreDto> products) {
        StringBuilder list = new StringBuilder("### PRODUCTOS DISPONIBLES\n");

        Map<String, List<ProductWithStoreDto>> categorized = products.stream()
                .filter(p -> p.getProduct() != null && hasValue(p.getProduct().getGarmentType()))
                .collect(Collectors.groupingBy(p -> p.getProduct().getGarmentType()));

        appendCategory(list, "TOP", categorized.get("TOP"));
        appendCategory(list, "BOTTOM", categorized.get("BOTTOM"));
        appendCategory(list, "DRESS", categorized.get("DRESS"));
        appendCategory(list, "OUTERWEAR", categorized.get("OUTERWEAR"));
        appendCategory(list, "FOOTWEAR", categorized.get("FOOTWEAR"));
        appendCategory(list, "ACCESSORY", categorized.get("ACCESSORY"));

        list.append("\n");
        return list.toString();
    }

    private void appendCategory(StringBuilder list, String category, List<ProductWithStoreDto> products) {
        list.append("\n**").append(category).append(":**\n");

        if (products == null || products.isEmpty()) {
            list.append("  • [No disponible]\n");
            return;
        }

        products.forEach(p -> list.append(formatProductSimplified(p)));
    }

    /**
     * Formato simplificado de producto (solo campos esenciales para estilismo).
     * ELIMINADOS: tienda, precio (no afectan la decisión de estilismo)
     * MANTENIDOS: nombre, color/familia, patrón, fit, formalidad
     */
    private String formatProductSimplified(ProductWithStoreDto p) {
        ProductDto product = p.getProduct();
        if (product == null)
            return "";

        return String.format("  • %s | %s/%s | %s | %s | F:%d\n",
                product.getName(),
                nullSafe(product.getPrimaryColor()),
                nullSafe(product.getColorFamily()),
                nullSafe(product.getPattern()),
                nullSafe(product.getFit()),
                product.getFormality() != null ? product.getFormality() : 2);
    }

    private String buildTaskInstructions(String climate, String style) {
        OutfitSizeConfig sizeConfig = OUTFIT_SIZE_BY_CLIMATE.getOrDefault(
                climate,
                new OutfitSizeConfig(3, 5));

        StringBuilder task = new StringBuilder("### TU TAREA\n");

        task.append(String.format(
                "1. Crea un outfit de %d a %d prendas siguiendo TODAS las reglas.\n",
                sizeConfig.min, sizeConfig.max));

        task.append("2. El outfit DEBE incluir: FOOTWEAR + (TOP y BOTTOM) O un DRESS.\n");
        task.append("   ⚠️ CRÍTICO: FOOTWEAR es OBLIGATORIO. Si NO hay FOOTWEAR disponible:\n");
        task.append("   - Sugiere zapatos como 'accesorio externo'\n");
        task.append("   - NO incluyas 2 TOPs (el outfit sería incompleto)\n");
        task.append("   - Limítate a: 1 TOP + 1 BOTTOM + OUTERWEAR (si hay)\n\n");

        // Instrucciones de layering (capas)
        task.append("3. **Layering (Capas)**: Puedes combinar TOP + OUTERWEAR cuando sea apropiado.\n");
        task.append("   El OUTERWEAR se usa SOBRE el TOP.\n");
        task.append("   Ejemplos válidos: camisa + blazer, camiseta + chaqueta, blusa + cardigan.\n");
        task.append("   ⚠️ IMPORTANTE: Solo usa 2 TOPs (ej: camisa + jersey) si HAY FOOTWEAR disponible.\n");
        task.append("   Si falta FOOTWEAR, prioriza completar el outfit con zapatos sugeridos.\n\n");

        // Reglas específicas por clima
        if ("Frío".equalsIgnoreCase(climate) || "frio".equalsIgnoreCase(climate)) {
            task.append("4. **Clima Frío**: OBLIGATORIO incluir OUTERWEAR (se usa sobre el TOP). ");
            task.append("El layering es ESENCIAL para este clima.\n");
        } else if ("Templado".equalsIgnoreCase(climate)) {
            task.append("4. **Clima Templado**: RECOMENDADO incluir OUTERWEAR ligero (chaqueta, cardigan, blazer). ");
            task.append("El layering aporta estilo y funcionalidad.\n");
        } else {
            task.append("4. **Clima Cálido**: OUTERWEAR opcional y solo si es ligero (cardigan fino, kimono). ");
            task.append("Evita capas pesadas.\n");
        }

        task.append("5. **Accesorios**: ");
        task.append(buildAccessoryInstructions(style));
        task.append("\n");

        task.append("6. USA SOLO los nombres EXACTOS de los productos de la lista.\n\n");

        return task.toString();
    }

    private String buildAccessoryInstructions(String style) {
        return switch (style != null ? style.toLowerCase() : "") {
            case "formal", "elegante" ->
                "Selecciona 1-2 de: Corbata/Pajarita, Reloj elegante, Cinturón, Collar/Aretes discretos, Bolso elegante. "
                        +
                        "Si no hay en la lista, sugiere con formato 'Sugerencia externa: [nombre]'.";

            case "urbano" ->
                "Selecciona 1-2 de: Gorra, Mochila, Riñonera, Gafas de sol, Cadena. " +
                        "Si no hay, sugiere uno apropiado.";

            case "deportivo" ->
                "OPCIONAL (0-1): Gorra deportiva, Reloj deportivo, Mochila deportiva. " +
                        "Si no hay, puedes omitir o sugerir.";

            default ->
                "OPCIONAL (0-1): Gorra, Gafas de sol, Reloj, Bolso. " +
                        "Si no hay apropiados, puedes omitir.";
        };
    }

    private String buildFewShotExample(String gender, String climate, String style) {
        // Solo mostrar ejemplo si el contexto coincide exactamente
        if ("Masculino".equalsIgnoreCase(gender) &&
                "Templado".equalsIgnoreCase(climate) &&
                ("Formal".equalsIgnoreCase(style) || "Elegante".equalsIgnoreCase(style))) {

            return """
                    ### EJEMPLO DE OUTFIT EXITOSO
                    ```json
                    {
                      "prendas": ["Camisa Blanca Oxford", "Pantalón Gris de Vestir", "Blazer Azul Marino", "Zapatos Oxford Negros"],
                      "accesorio": "Corbata Azul Marino, Reloj Plateado",
                      "razon": "Outfit clásico de negocios. El blazer azul marino aporta autoridad profesional, la camisa blanca es atemporal y versátil. Los zapatos oxford completan el look formal con elegancia."
                    }
                    ```

                    """;
        }

        if ("Femenino".equalsIgnoreCase(gender) &&
                "Cálido".equalsIgnoreCase(climate) &&
                "Casual".equalsIgnoreCase(style)) {

            return """
                    ### EJEMPLO DE OUTFIT EXITOSO
                    ```json
                    {
                      "prendas": ["Blusa Blanca de Lino", "Jeans Azul Claro", "Sandalias Beige"],
                      "accesorio": "Bolso de Mimbre, Gafas de Sol",
                      "razon": "Look fresco y relajado perfecto para verano. El lino es ideal para clima cálido, los jeans claros reflejan luz y calor. Las sandalias completan el estilo casual-chic sin sacrificar comodidad."
                    }
                    ```

                    """;
        }

        // No mostrar ejemplo si no coincide el contexto
        return "";
    }

    private String buildResponseFormat() {
        return """
                ### FORMATO DE RESPUESTA (JSON PURO, SIN MARKDOWN)
                {
                  "prendas": ["<NOMBRE_EXACTO_1>", "<NOMBRE_EXACTO_2>", ...],
                  "accesorio": "<NOMBRE_ACCESORIO_O_SUGERENCIA>",
                  "razon": "<Explicación breve y convincente del outfit>"
                }
                """;
    }

    // ============ UTILIDADES ============

    private static boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String nullSafe(String value) {
        return value == null ? "N/A" : value;
    }
}
