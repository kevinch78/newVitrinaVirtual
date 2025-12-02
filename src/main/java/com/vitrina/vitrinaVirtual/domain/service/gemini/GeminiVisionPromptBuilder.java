package com.vitrina.vitrinaVirtual.domain.service.gemini;

import org.springframework.stereotype.Component;

/**
 * Constructor de prompts optimizado para Gemini Vision API.
 * 
 * OPTIMIZACIONES IMPLEMENTADAS:
 * - Opciones expandidas de atributos (pattern: 15, fit: 11)
 * - Guía detallada de clasificación de garmentType
 * - Contexto visual específico para análisis de imagen
 * - Instrucciones mejoradas para technicalDescription
 * - Reglas de validación de coherencia
 * - Few-shot learning con ejemplos contextuales
 * 
 * @author Vitrina Virtual Team
 * @version 2.0 - Optimized
 */
@Component
public class GeminiVisionPromptBuilder {

  /**
   * Construye un prompt robusto y optimizado para el análisis de una imagen de
   * producto.
   * Este prompt está diseñado para maximizar la precisión del análisis y forzar
   * salida JSON estricta.
   *
   * @param productName El nombre del producto para dar contexto.
   * @return El prompt completo como un String.
   */
  public String buildProductAnalysisPrompt(String productName) {
    return """
        TASK: Analyze the clothing item in the image and provide its attributes in a strict JSON format.
        PRODUCT NAME: "%s"

        ═══════════════════════════════════════════════════════════════════════════════
        CRITICAL INSTRUCTION:
        If the image shows a model, mannequin, or person wearing the garment, analyze ONLY
        the main product being sold. Completely ignore the model's body, face, background,
        and any other clothing items that are not part of this specific product.
        ═══════════════════════════════════════════════════════════════════════════════

        ┌─────────────────────────────────────────────────────────────────────────────┐
        │ VISUAL ANALYSIS GUIDE                                                       │
        │ Focus ONLY on the main garment. Examine these aspects:                     │
        └─────────────────────────────────────────────────────────────────────────────┘

        1. SILHOUETTE & FIT:
           - How does it fit? (tight, loose, structured, flowing, form-fitting)
           - Overall shape (A-line, straight, fitted, boxy, tapered, flared)

        2. KEY STRUCTURAL DETAILS:
           - NECKLINE (if applicable): Crew, V-neck, scoop, turtleneck, boat neck,
             off-shoulder, halter, square, sweetheart
           - SLEEVES: Sleeveless, cap, short, elbow, 3/4, long, bell, puff, raglan,
             dolman, bishop
           - LENGTH: Cropped, regular, long, maxi, mini, midi, knee-length, ankle-length
           - CLOSURES: Buttons (count and placement), zipper (location), elastic,
             drawstring, snap, hook-and-eye, lace-up, pullover
           - POCKETS: Patch, welt, slash, kangaroo, hidden, flap, cargo

        3. CONSTRUCTION & TEXTURE:
           - Stitching: Topstitching, contrast stitching, decorative stitching
           - Seams: Visible seams, princess seams, side panels
           - Fabric texture: Smooth, ribbed, quilted, pleated, ruffled, gathered,
             knit, woven, textured
           - Embellishments: Embroidery, appliqué, studs, sequins, beading, lace,
             fringe, tassels

        4. SPECIFIC FEATURES:
           - Collars (if applicable): Pointed, rounded, mandarin, shawl, notched,
             Peter Pan, spread
           - Waistband: Elastic, button, drawstring, fitted, belted, high-waisted,
             mid-rise, low-rise
           - Hem style: Straight, curved, asymmetric, raw edge, rolled, lettuce edge
           - Lining: Fully lined, partially lined, unlined

        ┌─────────────────────────────────────────────────────────────────────────────┐
        │ GARMENT TYPE CLASSIFICATION GUIDE                                          │
        │ Choose the category that BEST describes the main function of the garment   │
        └─────────────────────────────────────────────────────────────────────────────┘

        'TOP':
        Covers upper body (chest, shoulders, arms). Worn directly on torso.
        Examples: Camisetas, camisas, blusas, polos, suéteres, hoodies, crop tops,
        tank tops, halter tops, tube tops, bodysuits (if upper-body focused)

        'BOTTOM':
        Covers lower body (waist to legs/feet). Worn on hips/waist.
        Examples: Pantalones, jeans, shorts, faldas, leggings, joggers, bermudas,
        capris, palazzo pants, culottes

        'DRESS':
        Single garment covering BOTH upper and lower body in one piece.
        Examples: Vestidos, enterizos, jumpsuits, monos, rompers, overalls (if dress-like)

        'OUTERWEAR':
        Worn OVER other clothing for warmth, protection, or style layering.
        Examples: Abrigos, chaquetas, blazers, sacos, cardigans, parkas, chaquetas de
        cuero, bombers, trench coats, ponchos, kimonos, vests (worn over shirts)

        'FOOTWEAR':
        Worn on feet for protection and style.
        Examples: Zapatos, botas, sandalias, tenis, tacones, mocasines, zapatillas,
        alpargatas, chanclas, botines

        'ACCESSORY':
        Complements outfit but is not primary clothing. Adds style or function.
        Examples: Bolsos, cinturones, bufandas, gorras, sombreros, gafas, collares,
        relojes, guantes, pañuelos, corbatas, pajaritas

        DISAMBIGUATION RULES (when uncertain):
        - VEST/CHALECO with sleeves → TOP
        - VEST/CHALECO without sleeves worn OVER shirt → OUTERWEAR
        - CARDIGAN (opens in front, buttons/zipper) → OUTERWEAR
        - SWEATER/SUÉTER (pullover, no opening) → TOP
        - BODYSUIT (one-piece, snaps at crotch) → TOP
        - If unsure between TOP and OUTERWEAR: "Is it typically worn over other clothes?"
          → YES = OUTERWEAR, NO = TOP

        ┌─────────────────────────────────────────────────────────────────────────────┐
        │ ANALYSIS INSTRUCTIONS                                                       │
        └─────────────────────────────────────────────────────────────────────────────┘

        1. Examine the image carefully following the Visual Analysis Guide above

        2. Classify 'garmentType' using the Classification Guide

        3. For each attribute, choose ONLY ONE option from the provided list

        4. 'formality' must be an integer from 1-5:
           1 = Very informal (gym wear, loungewear)
           2 = Casual (everyday, relaxed)
           3 = Smart casual (polished but not formal)
           4 = Semi-formal (dressy, elegant)
           5 = Very formal (business, ceremonies, galas)

        5. Generate TWO descriptions:

           A) 'detailedDescription' (SPANISH, 80-120 words):
              Marketing-oriented text to persuade customers. Should:
              - Highlight the garment's BEST features
              - Describe how it LOOKS and FEELS
              - Suggest OCCASIONS to wear it
              - Use persuasive, elegant language
              - Focus on BENEFITS, not just features

              Example tone: "Esta elegante blusa de seda combina sofisticación y
              comodidad. Su corte fluido favorece cualquier silueta, mientras que el
              cuello en V alarga visualmente. Perfecta para la oficina o una cena especial."

           B) 'technicalDescription' (ENGLISH, 60-100 words):
              Purely visual, objective description for AI-to-AI communication.

              CRITICAL: If image shows a model, describe ONLY the main product.

              List specific visual details:
              - CONSTRUCTION: Seams, stitching type, panels, lining
              - CLOSURES: Type and exact placement (e.g., "center-front zipper",
                "5-button placket")
              - DETAILS: Pockets (type and location), collar/neckline, cuffs, hem style
              - EMBELLISHMENTS: Prints, embroidery, hardware (buttons, zippers, grommets)
              - TEXTURE: Fabric weave, surface treatment, finish

              Example: "Single-breasted blazer with notched lapels. Two-button front
              closure. Welt pockets at chest and hips. Fully lined interior. Structured
              shoulders with light padding. Contrast stitching along lapels and pockets."

        6. Validate your response using the rules below BEFORE responding

        7. Your response MUST be ONLY the raw JSON object, with NO surrounding text,
           NO markdown code fences, NO explanations

        ┌─────────────────────────────────────────────────────────────────────────────┐
        │ VALIDATION RULES (Auto-check before responding)                            │
        └─────────────────────────────────────────────────────────────────────────────┘

        1. If garmentType is 'FOOTWEAR' or 'ACCESSORY', fit MUST be 'N/A'
        2. If style is 'Deportivo', formality MUST be 1-2
        3. If style is 'Formal', formality MUST be 4-5
        4. If style is 'Elegante', formality MUST be 3-5
        5. If style is 'Casual', formality MUST be 2-4
        6. If style is 'Urbano', formality MUST be 1-3
        7. If pattern is NOT 'Liso', it MUST be mentioned in technicalDescription
        8. If garmentType is 'DRESS', it MUST cover both upper and lower body
        9. detailedDescription MUST be in SPANISH
        10. technicalDescription MUST be in ENGLISH

        ┌─────────────────────────────────────────────────────────────────────────────┐
        │ JSON STRUCTURE (Your response must match this EXACTLY)                     │
        └─────────────────────────────────────────────────────────────────────────────┘

        {
          "attributes": {
            "style": "One of: 'Formal', 'Elegante', 'Casual', 'Deportivo', 'Urbano', 'Tradicional'",
            "formality": <integer 1-5>,
            "garmentType": "One of: 'TOP', 'BOTTOM', 'DRESS', 'OUTERWEAR', 'FOOTWEAR', 'ACCESSORY'",
            "colorFamily": "One of: 'Cálido', 'Frío', 'Neutro'",
            "pattern": "One of: 'Liso', 'Rayas Verticales', 'Rayas Horizontales', 'Cuadros', 'Cuadros Escoceses', 'Floral Grande', 'Floral Pequeño', 'Animal Print (Leopardo)', 'Animal Print (Cebra)', 'Animal Print (Serpiente)', 'Geométrico', 'Abstracto', 'Lunares', 'Camuflaje', 'Tie-Dye'",
            "fit": "One of: 'Skinny', 'Slim', 'Ajustado', 'Regular', 'Relaxed', 'Holgado', 'Oversize', 'Boyfriend', 'Mom Fit', 'Crop', 'N/A'"
          },
          "detailedDescription": "<Your marketing description in SPANISH here>",
          "technicalDescription": "<Your visual, objective description in ENGLISH here>"
        }

        ┌─────────────────────────────────────────────────────────────────────────────┐
        │ FEW-SHOT EXAMPLES (Learn from these successful analyses)                   │
        └─────────────────────────────────────────────────────────────────────────────┘

        EXAMPLE 1 - Blazer Formal:
        {
          "attributes": {
            "style": "Formal",
            "formality": 5,
            "garmentType": "OUTERWEAR",
            "colorFamily": "Neutro",
            "pattern": "Liso",
            "fit": "Slim"
          },
          "detailedDescription": "Blazer impecable que define elegancia profesional. Confeccionado con atención al detalle, presenta un corte slim que estiliza la figura sin comprometer la comodidad. Sus solapas clásicas y botones de calidad aportan sofisticación atemporal. Ideal para reuniones importantes, presentaciones ejecutivas o eventos formales donde la primera impresión es crucial. Una inversión en tu imagen profesional que nunca pasa de moda.",
          "technicalDescription": "Single-breasted blazer with notched lapels. Two-button front closure positioned at natural waist. Welt pockets at chest (left side) and hips. Fully lined interior with internal pocket. Structured shoulders with light padding. Four-button cuffs. Center back vent. Contrast stitching along lapel edges and pocket welts."
        }

        EXAMPLE 2 - Vestido Casual Floral:
        {
          "attributes": {
            "style": "Casual",
            "formality": 2,
            "garmentType": "DRESS",
            "colorFamily": "Cálido",
            "pattern": "Floral Pequeño",
            "fit": "Regular"
          },
          "detailedDescription": "Vestido fresco y femenino perfecto para el día a día. Su estampado floral delicado aporta un toque romántico sin ser excesivo. El corte regular favorece múltiples siluetas, mientras que la tela ligera garantiza comodidad durante todo el día. Ideal para paseos casuales, brunch con amigas o tardes de verano. Combínalo con sandalias para un look relajado o con botines para darle un giro más urbano.",
          "technicalDescription": "A-line dress with round neckline. Sleeveless design with thin shoulder straps. Small floral print pattern distributed evenly across fabric. Fitted bodice with elastic back panel. Gathered skirt portion starting at natural waist. Knee-length hem with slight flare. Side seam pockets. Unlined construction in lightweight woven fabric."
        }

        ═══════════════════════════════════════════════════════════════════════════════
        NOW ANALYZE THE IMAGE AND RESPOND WITH ONLY THE JSON OBJECT.
        ═══════════════════════════════════════════════════════════════════════════════
        """
        .formatted(productName);
  }
}