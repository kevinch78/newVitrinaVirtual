# 📚 Documentación Completa de la API - Vitrina Virtual

> **Vitrina Virtual** es una plataforma inteligente de moda que utiliza IA para generar outfits personalizados, conectando clientes con tiendas de ropa y facilitando la reserva de productos.

---

## 📋 Tabla de Contenidos

1. [Acceso a la Documentación](#-acceso-a-la-documentación)
2. [Arquitectura del Sistema](#-arquitectura-del-sistema)
3. [Flujo de Inteligencia Artificial](#-flujo-de-inteligencia-artificial)
4. [Autenticación](#-autenticación)
5. [Endpoints de la API](#-endpoints-de-la-api)
6. [Modelos de Datos](#-modelos-de-datos)
7. [Ejemplos de Uso](#-ejemplos-de-uso)
8. [Códigos de Estado](#-códigos-de-estado)

---

## 🌐 Acceso a la Documentación

Una vez que hayas iniciado la aplicación Spring Boot, podrás acceder a la documentación interactiva:

### 🔗 URLs de Documentación

| Recurso | URL | Descripción |
|---------|-----|-------------|
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` | Interfaz interactiva para probar endpoints |
| **ReDoc** | `http://localhost:8080/redoc.html` | Documentación alternativa elegante |
| **OpenAPI JSON** | `http://localhost:8080/v3/api-docs` | Especificación en formato JSON |
| **OpenAPI YAML** | `http://localhost:8080/v3/api-docs.yaml` | Especificación en formato YAML |

> ⚠️ **Nota**: Los endpoints de documentación **NO requieren autenticación** y están disponibles públicamente.

---

## 🏗️ Arquitectura del Sistema

### Stack Tecnológico

```
┌─────────────────────────────────────────────────────────────┐
│                      VITRINA VIRTUAL                         │
├─────────────────────────────────────────────────────────────┤
│  Backend:     Spring Boot 3.5.3 + Java 17                   │
│  Seguridad:   JWT + Spring Security                          │
│  Base de Datos: MySQL 8                                      │
│  IA Curadora:  Google Gemini 2.0 Flash                       │
│  IA Imágenes:  Replicate API (Flux Pro)                      │
│  Storage:      Cloudinary                                    │
│  Email:        Spring Mail (Gmail SMTP)                      │
│  Docs:         SpringDoc OpenAPI 3                           │
└─────────────────────────────────────────────────────────────┘
```

### Roles del Sistema

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **ADMIN** | Administrador del sistema | Gestión completa de productos, tiendas y usuarios |
| **CLIENT** | Cliente/Usuario final | Generar outfits, reservar productos, guardar outfits |
| **VENDOR** | Vendedor/Dueño de tienda | Gestionar reservas de su tienda, ver notificaciones |

---

## 🤖 Flujo de Inteligencia Artificial

Vitrina Virtual utiliza **tres sistemas de IA** para ofrecer una experiencia completa:

### 1️⃣ IA Curadora de Productos (Gemini Vision)

**Propósito**: Analizar imágenes de productos y extraer automáticamente sus atributos de moda.

```
┌─────────────────────────────────────────────────────────────┐
│                  FLUJO DE ANÁLISIS DE PRODUCTO               │
└─────────────────────────────────────────────────────────────┘

1. ADMIN sube producto con imagen
        ↓
2. Imagen se convierte a Base64
        ↓
3. Se envía a Gemini Vision API (gemini-2.0-flash-lite)
        ↓
4. IA analiza la imagen y extrae:
   • Tipo de prenda (TOP, BOTTOM, OUTERWEAR, etc.)
   • Estilo (Formal, Casual, Deportivo, etc.)
   • Colores (primario, secundarios, familia de color)
   • Patrón (liso, rayas, floral, etc.)
   • Formalidad (escala 1-5)
   • Ajuste (slim, regular, oversized)
   • Clima recomendado (Frío, Templado, Cálido)
        ↓
5. IA genera dos descripciones:
   • Descripción de Marketing (atractiva para clientes)
   • Descripción Técnica (detalles visuales precisos)
        ↓
6. Atributos se guardan automáticamente en la BD
        ↓
7. Si la IA falla, se aplica enriquecimiento manual como fallback
```

**Modelo**: `gemini-2.0-flash-lite`  
**Endpoint**: Automático al crear producto con imagen  
**Fallback**: Análisis basado en reglas si la IA no está disponible

---

### 2️⃣ IA Estilista de Outfits (Gemini Flash)

**Propósito**: Generar combinaciones de ropa coherentes y estilizadas según preferencias del usuario.

```
┌─────────────────────────────────────────────────────────────┐
│              FLUJO DE GENERACIÓN DE OUTFIT                   │
└─────────────────────────────────────────────────────────────┘

MODO 1: GENERACIÓN POR FILTROS
───────────────────────────────
1. Cliente especifica filtros:
   • Género (Masculino/Femenino)
   • Clima (Frío/Templado/Cálido)
   • Estilo (Formal/Casual/Deportivo/Tradicional)
   • Material (opcional: algodón, lana, denim, etc.)
        ↓
2. Sistema filtra productos de tiendas activas
        ↓
3. Optimización inteligente:
   • Prioriza prendas esenciales (TOP, BOTTOM, FOOTWEAR)
   • Agrega OUTERWEAR si clima es Frío/Templado
   • Limita a 60 productos máximo para la IA
   • Balancea productos entre tiendas
        ↓
4. Se envía a Gemini con prompt especializado
        ↓
5. IA selecciona 4-6 productos que combinan bien
        ↓
6. IA genera:
   • Lista de productos seleccionados
   • Descripción del outfit
   • Consejos de estilismo
   • Accesorios recomendados
        ↓
7. (Opcional) Se genera imagen fotorrealista


MODO 2: GENERACIÓN POR CHAT
────────────────────────────
1. Cliente escribe mensaje en lenguaje natural:
   Ejemplo: "Quiero un outfit elegante para una boda en invierno"
        ↓
2. Sistema parsea el mensaje y extrae:
   • Clima: "invierno" → Frío
   • Estilo: "elegante" → Formal
   • Ocasión: "boda" → Formal
   • Material: (si se menciona)
        ↓
3. Se aplica el mismo flujo que Modo 1
        ↓
4. IA genera outfit contextualizado al mensaje
```

**Modelo**: `gemini-2.0-flash`  
**Endpoints**: 
- `GET /api/products/outfit` (Modo Filtros)
- `POST /api/products/chat` (Modo Chat)

**Estrategia de Fallback**:
- Si no hay suficientes productos con filtros estrictos → flexibiliza estilo
- Si aún no hay suficientes → usa estilos compatibles
- Siempre garantiza variedad de prendas esenciales

---

### 3️⃣ IA Generadora de Imágenes (Replicate - Flux Pro)

**Propósito**: Crear visualizaciones fotorrealistas de los outfits generados.

```
┌─────────────────────────────────────────────────────────────┐
│           FLUJO DE GENERACIÓN DE IMAGEN DE OUTFIT            │
└─────────────────────────────────────────────────────────────┘

1. Cliente solicita outfit con generateImage=true
        ↓
2. IA Estilista genera el outfit (productos + descripción)
        ↓
3. Sistema construye prompt detallado:
   • Modelo humano (masculino/femenino)
   • Descripción técnica de cada prenda
   • Contexto de clima y estilo
   • Instrucciones de calidad fotográfica
        ↓
4. Se envía a Replicate API (Flux Pro 1.1)
        ↓
5. IA genera imagen fotorrealista en ~10-30 segundos
        ↓
6. Imagen se sube a Cloudinary
        ↓
7. URL de imagen se adjunta al outfit
        ↓
8. Cliente recibe outfit con visualización
```

**Modelo**: `black-forest-labs/flux-1.1-pro`  
**Parámetro**: `generateImage=true` en endpoints de outfit  
**Tiempo estimado**: 10-30 segundos  
**Resolución**: 1024x1024px (configurable)

**Ejemplo de Prompt Generado**:
```
A professional fashion photograph of a male model wearing:
- A navy blue slim-fit blazer made of wool
- A white cotton dress shirt with a regular fit
- Dark gray formal trousers with a straight fit
- Black leather oxford shoes

The model is standing in a studio setting with neutral background.
Professional lighting, high-quality fashion photography, 8k resolution.
```

---

## 🔐 Autenticación

### Flujo de Autenticación

```
1. Registro
   POST /api/auth/register
   ↓
2. Login (obtener token JWT)
   POST /api/auth/login
   ↓
3. Usar token en headers
   Authorization: Bearer <tu_token_jwt>
```

### Endpoints Públicos (sin autenticación)

- ✅ `POST /api/auth/register`
- ✅ `POST /api/auth/login`
- ✅ `GET /api/products/**` (listar y ver productos)
- ✅ `GET /api/stores/**` (listar y ver tiendas)
- ✅ Documentación Swagger

### Endpoints Protegidos

Todos los demás endpoints requieren token JWT válido.

---

## 📡 Endpoints de la API

### 🔑 Autenticación (`/api/auth`)

#### Registrar Usuario

```http
POST /api/auth/register
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "contrasena": "password123",
  "rol": "CLIENT"  // CLIENT, VENDOR, o ADMIN
}
```

**Respuesta**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "rol": "CLIENT"
}
```

#### Iniciar Sesión

```http
POST /api/auth/login
Content-Type: application/json

{
  "correo": "juan@example.com",
  "contrasena": "password123"
}
```

**Respuesta**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "rol": "CLIENT"
}
```

---

### 👕 Productos (`/api/products`)

#### Crear Producto (con IA Curadora)

```http
POST /api/products
Authorization: Bearer <token_admin>
Content-Type: multipart/form-data

productDto: {
  "name": "Blazer Azul Marino",
  "price": 150000,
  "existencia": 10,
  "descripcion": "Blazer elegante para ocasiones formales",
  "storeId": 1,
  "gender": "Masculino",
  "subcategory": "Chaquetas"
}
image: <archivo_imagen>
```

**Proceso Automático**:
1. ✅ Imagen se sube a Cloudinary
2. ✅ IA Curadora analiza la imagen
3. ✅ Se extraen atributos automáticamente
4. ✅ Se generan descripciones de marketing y técnicas
5. ✅ Producto se guarda enriquecido

**Respuesta**:
```json
{
  "idProduct": 123,
  "name": "Blazer Azul Marino",
  "price": 150000,
  "imagenUrl": "https://res.cloudinary.com/...",
  "garmentType": "OUTERWEAR",
  "style": "Formal",
  "climate": "Templado",
  "formality": 4,
  "primaryColor": "Azul Marino",
  "colorFamily": "Frío",
  "pattern": "Liso",
  "fit": "Slim",
  "iaDescription": "Blazer elegante de corte slim en azul marino, perfecto para eventos formales...",
  "technicalDescription": "Prenda de vestir superior tipo blazer, color azul marino sólido..."
}
```

#### Listar Productos

```http
GET /api/products
```

**Respuesta**: Array de productos

#### Obtener Producto por ID

```http
GET /api/products/{productId}
```

#### Productos por Estilo

```http
GET /api/products/style/{style}
```

Estilos disponibles: `Formal`, `Casual`, `Deportivo`, `Tradicional`, `Elegante`, `Urbano`

#### Productos por Tienda

```http
GET /api/products/store/{storeId}
```

---

### 🎨 Generación de Outfits (IA Estilista)

#### Generar Outfit por Filtros

```http
GET /api/products/outfit?gender=Masculino&climate=Frío&style=Formal&generateImage=true
Authorization: Bearer <token_client>
```

**Parámetros**:
- `gender` (requerido): `Masculino` o `Femenino`
- `climate` (requerido): `Frío`, `Templado`, `Cálido`
- `style` (requerido): `Formal`, `Casual`, `Deportivo`, `Tradicional`
- `material` (opcional): `algodón`, `lana`, `denim`, `cuero`, `seda`, `lino`
- `storeIds` (opcional): Array de IDs de tiendas específicas
- `generateImage` (opcional, default: false): `true` para generar imagen fotorrealista

**Respuesta**:
```json
{
  "selectedProducts": [
    {
      "product": {
        "idProduct": 45,
        "name": "Abrigo de Lana Gris",
        "price": 250000,
        "imagenUrl": "https://...",
        "garmentType": "OUTERWEAR"
      },
      "store": {
        "storeId": 2,
        "name": "Elegancia Urbana",
        "city": "Bogotá"
      }
    },
    {
      "product": {
        "idProduct": 78,
        "name": "Camisa Blanca Formal",
        "price": 80000,
        "garmentType": "TOP"
      },
      "store": {...}
    },
    {
      "product": {
        "idProduct": 92,
        "name": "Pantalón de Vestir Negro",
        "price": 120000,
        "garmentType": "BOTTOM"
      },
      "store": {...}
    },
    {
      "product": {
        "idProduct": 103,
        "name": "Zapatos Oxford Negros",
        "price": 180000,
        "garmentType": "FOOTWEAR"
      },
      "store": {...}
    }
  ],
  "description": "Outfit formal elegante perfecto para clima frío. El abrigo de lana gris aporta sofisticación y calidez, combinado con una camisa blanca clásica y pantalón de vestir negro. Los zapatos oxford completan el look con un toque de distinción.",
  "accessory": "Considera agregar un reloj de acero y una bufanda de lana en tonos neutros para completar el conjunto.",
  "imageUrl": "https://res.cloudinary.com/.../outfit_12345.png"
}
```

#### Generar Outfit desde Chat (Lenguaje Natural)

```http
POST /api/products/chat?gender=Masculino&generateImage=false
Authorization: Bearer <token_client>
Content-Type: application/json

{
  "message": "Necesito un outfit casual para ir a la playa en verano"
}
```

**El sistema extrae automáticamente**:
- Clima: "verano" → `Cálido`
- Estilo: "casual" → `Casual`
- Ocasión: "playa" → Contexto para la IA

**Respuesta**: Mismo formato que el endpoint anterior

**Ejemplos de mensajes**:
- ✅ "Outfit elegante para una boda en invierno"
- ✅ "Ropa deportiva cómoda para el gimnasio"
- ✅ "Look casual para salir con amigos en primavera"
- ✅ "Traje formal para entrevista de trabajo"

---

### 🏪 Tiendas (`/api/stores`)

#### Crear Tienda

```http
POST /api/stores
Authorization: Bearer <token_admin>
Content-Type: multipart/form-data

storeDto: {
  "name": "Moda Urbana",
  "description": "Tienda de ropa casual y urbana",
  "city": "Medellín",
  "address": "Calle 50 #45-30",
  "contact": "+57 300 1234567",
  "activeAdvertising": true
}
imagen: <archivo_imagen>
```

#### Listar Tiendas

```http
GET /api/stores
```

#### Obtener Tienda por ID

```http
GET /api/stores/{storeId}
```

#### Tiendas con Publicidad Activa

```http
GET /api/stores/pay-advertising
Authorization: Bearer <token_admin>
```

#### Buscar por Nombre

```http
GET /api/stores/name/{name}
Authorization: Bearer <token_admin>
```

#### Buscar por Dirección

```http
GET /api/stores/address/{address}
Authorization: Bearer <token_admin>
```

---

### 📦 Reservas (`/api/reservations`)

#### Crear Reserva

```http
POST /api/reservations
Authorization: Bearer <token_client>
Content-Type: application/json

{
  "clientId": 5,
  "storeId": 2,
  "status": "PENDING",
  "totalPrice": 450000,
  "items": [
    {
      "productId": 45,
      "quantity": 1,
      "price": 250000
    },
    {
      "productId": 78,
      "quantity": 2,
      "price": 100000
    }
  ]
}
```

**Proceso Automático**:
1. ✅ Reserva se crea con estado `PENDING`
2. ✅ Se envía notificación automática a la tienda
3. ✅ Cliente recibe confirmación

**Estados de Reserva**:
- `PENDING`: Esperando aprobación del vendedor
- `ACCEPTED`: Vendedor aceptó la reserva
- `PARTIALLY_ACCEPTED`: Algunos items aceptados, otros no
- `READY`: Productos listos para recoger
- `DELIVERED`: Entregado al cliente
- `CANCELLED`: Cancelado por el cliente
- `REJECTED`: Rechazado por el vendedor

#### Obtener Reservas del Cliente

```http
GET /api/reservations/client/{clientId}
Authorization: Bearer <token_client>
```

#### Obtener Reservas de la Tienda

```http
GET /api/reservations/store/{storeId}
Authorization: Bearer <token_vendor>
```

#### Actualizar Estado de Reserva

```http
PUT /api/reservations/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "ACCEPTED"
}
```

**Notificaciones Automáticas**:
- `ACCEPTED` → Cliente recibe: "Tu reserva ha sido aceptada"
- `REJECTED` → Cliente recibe: "Tu reserva ha sido rechazada"
- `READY` → Cliente recibe: "Tu reserva está lista para recoger"
- `CANCELLED` → Tienda recibe: "El cliente canceló la reserva"

#### Cancelar Reserva

```http
DELETE /api/reservations/{id}
Authorization: Bearer <token_client>
```

---

### 🔔 Notificaciones (`/api/notifications`)

#### Obtener Notificaciones del Cliente

```http
GET /api/notifications/client/all
Authorization: Bearer <token_client>
```

**Respuesta**:
```json
[
  {
    "id": 123,
    "title": "Reserva Aceptada",
    "message": "Tu reserva #45 en Moda Urbana ha sido aceptada",
    "type": "RESERVATION_ACCEPTED",
    "recipientType": "CLIENT",
    "isRead": false,
    "priority": 7,
    "createdAt": "2025-12-01T18:30:00",
    "relatedEntityId": 45,
    "relatedEntityType": "RESERVATION",
    "actionUrl": "/reservations/45"
  }
]
```

#### Notificaciones No Leídas

```http
GET /api/notifications/client/unread
Authorization: Bearer <token_client>
```

#### Contador de No Leídas

```http
GET /api/notifications/client/unread/count
Authorization: Bearer <token_client>
```

**Respuesta**:
```json
{
  "count": 3
}
```

#### Marcar Todas como Leídas

```http
POST /api/notifications/client/mark-all-read
Authorization: Bearer <token_client>
```

#### Marcar Una como Leída

```http
PUT /api/notifications/{id}/read
Authorization: Bearer <token>
```

#### Eliminar Notificación

```http
DELETE /api/notifications/{id}
Authorization: Bearer <token>
```

#### Notificaciones de Tienda

```http
GET /api/notifications/store/all
Authorization: Bearer <token_vendor>
```

```http
GET /api/notifications/store/unread
Authorization: Bearer <token_vendor>
```

```http
POST /api/notifications/store/mark-all-read
Authorization: Bearer <token_vendor>
```

---

### 💾 Outfits Guardados (`/api/outfits`)

#### Guardar Outfit

```http
POST /api/outfits
Authorization: Bearer <token_client>
Content-Type: application/json

{
  "outfitName": "Look Formal Invierno",
  "description": "Outfit elegante para clima frío",
  "accessory": "Reloj de acero, bufanda gris",
  "imageUrl": "https://res.cloudinary.com/.../outfit.png",
  "gender": "Masculino",
  "climate": "Frío",
  "style": "Formal",
  "products": [
    {
      "productId": 45,
      "productName": "Abrigo de Lana Gris",
      "productImageUrl": "https://...",
      "productPrice": 250000
    },
    {
      "productId": 78,
      "productName": "Camisa Blanca Formal",
      "productImageUrl": "https://...",
      "productPrice": 80000
    }
  ]
}
```

#### Obtener Outfits del Usuario

```http
GET /api/outfits
Authorization: Bearer <token_client>
```

#### Obtener Outfit por ID

```http
GET /api/outfits/{id}
Authorization: Bearer <token_client>
```

#### Actualizar Outfit

```http
PUT /api/outfits/{id}
Authorization: Bearer <token_client>
Content-Type: application/json

{
  "outfitName": "Look Formal Invierno Actualizado",
  "description": "Nueva descripción"
}
```

#### Eliminar Outfit

```http
DELETE /api/outfits/{id}
Authorization: Bearer <token_client>
```

---

## 📊 Modelos de Datos

### ProductDto

```json
{
  "idProduct": 123,
  "name": "Blazer Azul Marino",
  "price": 150000,
  "existencia": 10,
  "descripcion": "Descripción del producto",
  "imagenUrl": "https://...",
  "storeId": 2,
  
  // Atributos básicos
  "gender": "Masculino",
  "style": "Formal",
  "climate": "Templado",
  "occasion": "Formal",
  "material": "Lana",
  
  // Atributos extraídos por IA
  "garmentType": "OUTERWEAR",
  "subcategory": "Blazer",
  "primaryColor": "Azul Marino",
  "secondaryColors": "Gris",
  "colorFamily": "Frío",
  "pattern": "Liso",
  "fit": "Slim",
  "formality": 4,
  
  // Descripciones generadas por IA
  "iaDescription": "Blazer elegante de corte slim...",
  "technicalDescription": "Prenda de vestir superior tipo blazer..."
}
```

### OutfitRecommendation

```json
{
  "selectedProducts": [
    {
      "product": { /* ProductDto */ },
      "store": { /* StoreDto */ }
    }
  ],
  "description": "Descripción del outfit generada por IA",
  "accessory": "Accesorios recomendados",
  "imageUrl": "https://... (si generateImage=true)"
}
```

### ReservationDto

```json
{
  "id": 45,
  "clientId": 5,
  "storeId": 2,
  "status": "PENDING",
  "totalPrice": 450000,
  "createdAt": "2025-12-01T18:00:00",
  "items": [
    {
      "id": 101,
      "productId": 45,
      "productName": "Abrigo de Lana",
      "quantity": 1,
      "price": 250000,
      "status": "PENDING"
    }
  ]
}
```

### NotificationDto

```json
{
  "id": 123,
  "recipientId": 5,
  "recipientType": "CLIENT",
  "title": "Reserva Aceptada",
  "message": "Tu reserva #45 ha sido aceptada",
  "type": "RESERVATION_ACCEPTED",
  "relatedEntityId": 45,
  "relatedEntityType": "RESERVATION",
  "actionUrl": "/reservations/45",
  "isRead": false,
  "priority": 7,
  "createdAt": "2025-12-01T18:30:00",
  "readAt": null,
  "expiresAt": null
}
```

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Flujo Completo de Cliente

```bash
# 1. Registrarse
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María García",
    "correo": "maria@example.com",
    "contrasena": "password123",
    "rol": "CLIENT"
  }'

# 2. Iniciar sesión
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "maria@example.com",
    "contrasena": "password123"
  }'

# Guardar el token recibido
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 3. Generar outfit con IA
curl -X GET "http://localhost:8080/api/products/outfit?gender=Femenino&climate=Templado&style=Casual&generateImage=true" \
  -H "Authorization: Bearer $TOKEN"

# 4. Guardar outfit favorito
curl -X POST http://localhost:8080/api/outfits \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "outfitName": "Look Casual Primavera",
    "description": "Outfit cómodo para el día a día",
    "gender": "Femenino",
    "climate": "Templado",
    "style": "Casual",
    "products": [...]
  }'

# 5. Crear reserva
curl -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 5,
    "storeId": 2,
    "status": "PENDING",
    "totalPrice": 350000,
    "items": [
      {"productId": 12, "quantity": 1, "price": 150000},
      {"productId": 34, "quantity": 1, "price": 200000}
    ]
  }'

# 6. Ver notificaciones
curl -X GET http://localhost:8080/api/notifications/client/unread \
  -H "Authorization: Bearer $TOKEN"
```

### Ejemplo 2: Generar Outfit con Chat

```bash
curl -X POST "http://localhost:8080/api/products/chat?gender=Masculino&generateImage=true" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Necesito un outfit elegante para una cena de negocios en invierno, prefiero colores oscuros"
  }'
```

La IA extraerá:
- Clima: `Frío` (invierno)
- Estilo: `Formal` (elegante, negocios)
- Preferencia de color: Colores oscuros

### Ejemplo 3: Vendedor Gestionando Reservas

```bash
# 1. Ver reservas de mi tienda
curl -X GET http://localhost:8080/api/reservations/store/2 \
  -H "Authorization: Bearer $TOKEN_VENDOR"

# 2. Aceptar reserva
curl -X PUT http://localhost:8080/api/reservations/45 \
  -H "Authorization: Bearer $TOKEN_VENDOR" \
  -H "Content-Type: application/json" \
  -d '{"status": "ACCEPTED"}'

# 3. Marcar como lista para recoger
curl -X PUT http://localhost:8080/api/reservations/45 \
  -H "Authorization: Bearer $TOKEN_VENDOR" \
  -H "Content-Type: application/json" \
  -d '{"status": "READY"}'

# 4. Ver notificaciones de la tienda
curl -X GET http://localhost:8080/api/notifications/store/all \
  -H "Authorization: Bearer $TOKEN_VENDOR"
```

---

## 📋 Códigos de Estado

| Código | Significado | Cuándo se usa |
|--------|-------------|---------------|
| **200** | OK | Operación exitosa |
| **201** | Created | Recurso creado exitosamente |
| **204** | No Content | Eliminación exitosa |
| **400** | Bad Request | Datos inválidos o faltantes |
| **401** | Unauthorized | Token JWT inválido o faltante |
| **403** | Forbidden | Sin permisos para esta operación |
| **404** | Not Found | Recurso no encontrado |
| **500** | Internal Server Error | Error del servidor |

---

## 🚀 Cómo Iniciar

### 1. Instalar Dependencias

```bash
mvn clean install
```

### 2. Configurar Variables de Entorno

Edita el archivo `.env` con tus credenciales:

```env
GEMINI_API_KEY=tu_api_key
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
DB_URL=jdbc:mysql://localhost:3306/vitrina_virtual
DB_USERNAME=root
DB_PASSWORD=root
REPLICATE_API_KEY=tu_replicate_key
JWT_SECRET_KEY=tu_clave_secreta
```

### 3. Iniciar la Aplicación

```bash
mvn spring-boot:run
```

### 4. Acceder a Swagger

Abre tu navegador en: `http://localhost:8080/swagger-ui.html`

---

## 🎯 Características Destacadas

### ✨ IA Curadora Automática
- Analiza imágenes de productos automáticamente
- Extrae 15+ atributos de moda
- Genera descripciones profesionales
- Fallback manual si la IA falla

### 🎨 IA Estilista Inteligente
- Genera outfits coherentes y estilizados
- Entiende lenguaje natural
- Optimiza selección de productos
- Sistema de fallback en cascada

### 🖼️ Generación de Imágenes Fotorrealistas
- Visualiza outfits antes de comprar
- Calidad profesional (Flux Pro 1.1)
- Integración transparente con Cloudinary

### 🔔 Sistema de Notificaciones Automático
- Notificaciones en tiempo real
- Para clientes y vendedores
- Integrado con eventos de reservas

### 🔐 Seguridad Robusta
- JWT con expiración
- Roles y permisos granulares
- CORS configurado
- Endpoints protegidos

---

## 📞 Soporte

Para más información, consulta:
- **Swagger UI**: Documentación interactiva completa
- **Código fuente**: Comentarios detallados en cada servicio
- **Logs**: Nivel DEBUG habilitado para troubleshooting

---

## 🎉 ¡Disfruta de Vitrina Virtual!

Una plataforma de moda inteligente que combina lo mejor de la IA con una experiencia de usuario excepcional.

**Desarrollado con ❤️ usando Spring Boot + Google Gemini + Replicate**
