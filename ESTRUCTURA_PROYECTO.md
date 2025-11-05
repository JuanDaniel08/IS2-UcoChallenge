# Guía de Estructura del Proyecto - Arquitectura Hexagonal

## 📋 Arquitectura Base

El proyecto sigue una **Arquitectura Hexagonal (Ports & Adapters)** con principios de **Clean Architecture**. La estructura está organizada en capas claramente definidas.

## 🏗️ Estructura de Capas

```
co.edu.uco.ucochallenge
├── application/                    # Capa de aplicación (interfaces base)
│   ├── interactor/
│   │   ├── Interactor<T, R>      # Interface base para interactores
│   │   └── usecase/
│   │       └── UseCase<D, R>      # Interface base para casos de uso
│   ├── Response<T>                # Clase base para respuestas
│   └── Void                        # Clase para operaciones sin retorno
│
├── primary/                        # ⚠️ PUERTOS DE ENTRADA (Driving Adapters)
│   ├── controller/                # Controladores REST (Spring @RestController)
│   └── handler/                   # Manejadores de excepciones
│
├── secondary/                     # ⚠️ PUERTOS DE SALIDA (Driven Adapters)
│   ├── adapters/                  # Implementaciones concretas
│   │   ├── repository/
│   │   │   └── entity/            # Entidades JPA (Entity)
│   │   └── cache/                 # Adaptadores de caché (Redis)
│   └── ports/                     # ⚠️ INTERFACES (Puertos)
│       └── repository/            # Interfaces de repositorio (Ports)
│
├── user/                          # Módulo de negocio (Feature)
│   └── registeruser/
│       └── application/
│           ├── interactor/        # Orquestadores de casos de uso
│           │   ├── dto/           # DTOs de entrada
│           │   ├── impl/          # Implementaciones de interactores
│           │   └── usecase/       # Casos de uso específicos
│           │       └── impl/      # Implementaciones de casos de uso
│           ├── service/           # Servicios de aplicación
│           └── usecase/
│               ├── domain/        # Objetos de dominio
│               └── validator/     # Validadores
│
└── crosscuting/                   # Utilidades transversales
    └── helper/                    # Helpers (TextHelper, UUIDHelper, etc.)
```

## 🎯 Áreas Clave en las que Enfocarse

### 1. **Separación de Responsabilidades**

#### ✅ **PRIMARY (Puertos de Entrada)**
- **Solo** controladores REST, handlers, mappers DTO → Domain
- **NO** debe contener lógica de negocio
- **NO** debe acceder directamente a `secondary.adapters`

**Ejemplo Correcto:**
```java
@RestController
public class UserController {
    private final RegisterUserInteractor interactor; // ✅ Usa Interactor
    
    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterUserInputDTO dto) {
        registerUserInteractor.execute(dto); // ✅ Delega al Interactor
        return ResponseEntity.ok(...);
    }
}
```

#### ✅ **SECONDARY (Puertos de Salida)**
- **ports/**: Solo interfaces (contractos)
- **adapters/**: Implementaciones concretas (Entity, Repository JPA, etc.)

**Ejemplo:**
```java
// secondary/ports/repository/UserRepository.java (INTERFACE)
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
}

// secondary/adapters/repository/entity/UserEntity.java (IMPLEMENTACIÓN)
@Entity
public class UserEntity { ... }
```

### 2. **Flujo de Datos Correcto**

```
Controller (Primary)
    ↓
Interactor (Application)
    ↓
UseCase (Application)
    ↓
Domain (Application)
    ↓
Port (Secondary - Interface)
    ↓
Adapter (Secondary - Implementation)
```

**Ejemplo de flujo:**
1. `UserController` recibe `RegisterUserInputDTO`
2. `RegisterUserInteractor` convierte DTO → `RegisterUserDomain`
3. `RegisterUserUseCase` ejecuta lógica de negocio
4. `RegisterUserUseCase` usa `UserRepository` (port) para persistir
5. Spring inyecta `UserRepository` (JPA) que implementa el port

### 3. **Dependencias y Reglas**

#### ✅ **Reglas de Dependencia:**
- **Primary** → **Application** (puede usar)
- **Application** → **Secondary Ports** (puede usar interfaces)
- **Secondary Adapters** → **Secondary Ports** (implementa)
- **Application** → **Application** (puede usar)
- **NUNCA**: Primary → Secondary Adapters (directamente)
- **NUNCA**: Application → Secondary Adapters (directamente, solo ports)

#### ✅ **Inyección de Dependencias:**
```java
// ✅ CORRECTO: UseCase usa el Port (interfaz)
@Service
public class RegisterUserUseCaseImpl {
    private UserRepository repository; // Port interface
    
    public RegisterUserUseCaseImpl(UserRepository repository) {
        this.repository = repository; // Spring inyecta la implementación
    }
}

// ❌ INCORRECTO: Usar directamente la Entity
@Service
public class RegisterUserUseCaseImpl {
    private UserEntity entity; // ❌ NO usar directamente
}
```

### 4. **Estructura de Módulos por Feature**

Cada feature (como `registeruser`) debe seguir esta estructura:

```
user/registeruser/application/
├── interactor/
│   ├── RegisterUserInteractor          # Interface (extiende Interactor)
│   ├── RegisterUserInteractorImpl     # Implementación
│   ├── dto/
│   │   └── RegisterUserInputDTO        # DTO de entrada
│   └── usecase/
│       ├── RegisterUserUseCase         # Interface (extiende UseCase)
│       └── impl/
│           └── RegisterUserUseCaseImpl # Implementación
├── service/                            # Servicios auxiliares
│   └── NotificationService
└── usecase/
    ├── domain/
    │   └── RegisterUserDomain          # Objeto de dominio
    └── validator/
        ├── ValidationResultVO
        └── Validator<T, R>
```

### 5. **Mapeo de Datos**

#### ✅ **DTO → Domain → Entity**
- **DTO**: Estructura de datos de entrada (desde el controller)
- **Domain**: Objeto de negocio (puro, sin anotaciones JPA)
- **Entity**: Entidad JPA para persistencia

```java
// 1. Controller recibe DTO
@PostMapping
public ResponseEntity<?> register(@RequestBody RegisterUserInputDTO dto)

// 2. Interactor convierte DTO → Domain
RegisterUserDomain domain = new RegisterUserDomain(
    dto.idType(), dto.idNumber(), ...
);

// 3. UseCase trabaja con Domain
useCase.execute(domain);

// 4. UseCase convierte Domain → Entity (solo cuando persiste)
UserEntity entity = new UserEntity.Builder()
    .id(domain.getIdType())
    .build();
```

### 6. **Validadores y Reglas de Negocio**

#### ✅ **Estructura de Validadores:**
```java
// usecase/validator/Validator.java (Interface genérica)
public interface Validator<T, R> {
    R validate(T data);
}

// usecase/validator/ValidationResultVO.java (Value Object)
public class ValidationResultVO {
    private List<String> mensajes;
    public boolean isValidacionCorrecta();
}

// usecase/impl/UsuarioConMismoId.java (Implementación)
public class UsuarioConMismoId implements Validator<UUID, ValidationResultVO> {
    public ValidationResultVO validate(UUID data) { ... }
}
```

### 7. **Servicios de Aplicación**

Los servicios en `application/service/` deben:
- Ser agnósticos de frameworks (no depender directamente de Spring Data)
- Usar Ports (interfaces) en lugar de Adapters
- Ser inyectables vía constructor

### 8. **Mensajería y Eventos (Redis)**

#### ✅ **Estructura Correcta:**
```java
// service/dto/NotificationMessage.java (DTO para mensajes)
public class NotificationMessage { ... }

// service/NotificationService.java (Publica mensajes)
@Service
public class NotificationService {
    private StringRedisTemplate redisTemplate; // ✅ Usa Redis
    public void notifyAdmin(String message) {
        publishMessage(CHANNEL_ADMIN, "admin", message);
    }
}

// service/listener/NotificationListener.java (Consume mensajes)
@Component
public class NotificationListener implements MessageListener {
    public void onMessage(Message message, byte[] pattern) { ... }
}
```

## ⚠️ Errores Comunes a Evitar

### ❌ **1. Acceso Directo a Adapters desde Primary**
```java
// ❌ INCORRECTO
@RestController
public class UserController {
    @Autowired
    private UserEntity entity; // ❌ NO hacer esto
}
```

### ❌ **2. Lógica de Negocio en Controller**
```java
// ❌ INCORRECTO
@RestController
public class UserController {
    @PostMapping
    public ResponseEntity<?> register(@RequestBody DTO dto) {
        if (dto.email() == null) { // ❌ Validación en controller
            return ResponseEntity.badRequest();
        }
        // Lógica de negocio aquí... ❌
    }
}
```

### ❌ **3. Entity en Domain o UseCase**
```java
// ❌ INCORRECTO
public class RegisterUserDomain {
    private UserEntity entity; // ❌ NO mezclar Entity con Domain
}
```

### ❌ **4. Anotaciones JPA en Domain**
```java
// ❌ INCORRECTO
@Entity // ❌ Domain no debe tener anotaciones JPA
public class RegisterUserDomain { ... }
```

## ✅ Checklist para Nuevas Features

Al agregar una nueva feature, verifica:

- [ ] ¿Tienes un `Interactor` que orquesta?
- [ ] ¿Tienes un `UseCase` con la lógica de negocio?
- [ ] ¿Tienes un `Domain` object (sin anotaciones JPA)?
- [ ] ¿Tienes DTOs en `interactor/dto/`?
- [ ] ¿Tus repositorios están en `secondary/ports/repository/`?
- [ ] ¿Tus entities están en `secondary/adapters/repository/entity/`?
- [ ] ¿El Controller solo delega al Interactor?
- [ ] ¿El UseCase solo usa interfaces (ports), no adapters?
- [ ] ¿Las validaciones están en `usecase/validator/`?
- [ ] ¿Los servicios están en `application/service/`?

## 📚 Convenciones de Nombres

- **Interfaces**: Sin sufijo (ej: `RegisterUserInteractor`)
- **Implementaciones**: Sufijo `Impl` (ej: `RegisterUserInteractorImpl`)
- **DTOs**: Sufijo `DTO` (ej: `RegisterUserInputDTO`)
- **Domain**: Sin sufijo (ej: `RegisterUserDomain`)
- **Entities**: Sufijo `Entity` (ej: `UserEntity`)
- **Ports**: Solo interfaces (ej: `UserRepository`)
- **Adapters**: Implementaciones concretas (ej: JPA Repository)

## 🔄 Flujo Completo Ejemplo

```
1. Cliente HTTP → UserController.register(dto)
2. UserController → RegisterUserInteractor.execute(dto)
3. RegisterUserInteractor → Convierte DTO a Domain
4. RegisterUserInteractor → RegisterUserUseCase.execute(domain)
5. RegisterUserUseCase → Ejecuta validaciones y lógica
6. RegisterUserUseCase → UserRepository.save(entity) (port)
7. Spring inyecta → JPA Repository (adapter) que implementa el port
8. JPA Repository → Persiste en base de datos
```

## 🎯 Resumen: En qué Enfocarse

1. **Separación clara**: Primary → Application → Secondary Ports
2. **No romper dependencias**: Nunca Primary → Secondary Adapters
3. **Domain puro**: Sin anotaciones JPA, sin dependencias de frameworks
4. **Interfaces primero**: Usa Ports (interfaces), no Adapters (implementaciones)
5. **Un módulo por feature**: Cada funcionalidad en su propio paquete
6. **DTOs para entrada/salida**: No exponer Domain directamente
7. **Servicios reutilizables**: En `application/service/` para lógica compartida

