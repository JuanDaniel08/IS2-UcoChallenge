# Análisis: Lógica de Negocio Faltante

## 🔴 Problemas Críticos

### 1. **Validación Incorrecta en `UsuarioConMismoId`**

**Problema:** La lógica está invertida. Para un registro nuevo, el ID siempre será nuevo (UUID.randomUUID()), por lo que `existsById()` siempre retornará `false`.

```java
// ❌ ACTUAL (INCORRECTO)
if (!userRepository.existsById(data)){  // Esto siempre será true para un ID nuevo
    resultadoValidacion.agregarMensaje("No existe un usuario en el id: " + data);
}
```

**Solución:** La validación debería ser que SI existe el ID, entonces hay error. Pero para registro nuevo, esta validación no tiene sentido. Probablemente debería eliminarse o cambiar la lógica.

**Opciones:**
- Eliminar esta validación para registro nuevo (siempre generamos ID nuevo)
- Cambiar la validación para que verifique otra cosa (ej: si el ID ya existe en la BD, no debería pasar)

### 2. **Falta Notificación al Actor (Ejecutor)**

**Problema:** En `UserRegistrationService` se recibe y notifica al `ejecutor`, pero en `RegisterUserUseCaseImpl` no se recibe ni se pasa.

**Falta:**
- Recibir el `actor` o `ejecutor` en el UseCase
- Notificar al actor cuando hay errores
- Notificar al actor cuando el registro es exitoso

**Ejemplo:**
```java
// ❌ ACTUAL: No se recibe el actor
public Void execute(final RegisterUserDomain domain)

// ✅ DEBERÍA SER:
public Void execute(final RegisterUserDomain domain, final String actor)
```

### 3. **Mensajes Hardcodeados (Catálogo de Mensajes)**

**Problema:** Hay un TODO explícito que indica que los mensajes deben venir de un catálogo.

**Falta:**
- Implementar `CatalogUseCase` o usar el `ParameterCatalog` existente
- Crear un catálogo de mensajes de validación
- Reemplazar todos los mensajes hardcodeados por referencias al catálogo

**Ejemplo:**
```java
// ❌ ACTUAL
resultadoFinal.agregarMensaje("Ya existe un usuario con el mismo email");

// ✅ DEBERÍA SER
String mensaje = catalogUseCase.getMensaje("error.usuario.email.duplicado");
resultadoFinal.agregarMensaje(mensaje);
```

## 🟡 Validaciones Faltantes

### 4. **Validaciones de Formato de Datos**

**Faltan validaciones para:**

#### Email
- Formato de email válido (regex)
- Email no puede estar vacío (si es requerido)
- Longitud máxima de email

#### Teléfono Móvil
- Formato de teléfono válido
- Longitud del número
- Solo números (y caracteres permitidos)

#### Número de Identificación
- Formato según tipo de ID
- Longitud válida
- Solo caracteres permitidos

#### Nombres y Apellidos
- Campos requeridos (primer nombre, primer apellido)
- Longitud mínima/máxima
- Solo caracteres alfabéticos (y espacios, acentos)

#### IDs (UUIDs)
- Verificar que idType existe en la BD
- Verificar que homeCity existe en la BD

### 5. **Validaciones de Campos Requeridos**

**Falta validar:**
- `idType` es requerido
- `idNumber` es requerido
- `firstName` es requerido
- `firstSurname` es requerido
- `email` es requerido
- `homeCity` es requerido
- `mobileNumber` puede ser opcional

**Ejemplo de validador faltante:**
```java
public class CamposRequeridosValidator implements Validator<RegisterUserDomain, ValidationResultVO> {
    public ValidationResultVO validate(RegisterUserDomain domain) {
        var resultado = new ValidationResultVO();
        
        if (domain.getIdType() == null) {
            resultado.agregarMensaje("El tipo de identificación es requerido");
        }
        if (domain.getIdNumber() == null || domain.getIdNumber().isBlank()) {
            resultado.agregarMensaje("El número de identificación es requerido");
        }
        // ... más validaciones
        return resultado;
    }
}
```

### 6. **Validaciones de Integridad Referencial**

**Falta validar:**
- Que `idType` existe en la tabla `IdType`
- Que `homeCity` existe en la tabla `City`
- Que estos registros están activos (si aplica)

## 🟢 Mejoras de Lógica de Negocio

### 7. **Inicialización de Estados de Confirmación**

**Problema:** Los flags `emailConfirmed` y `mobileNumberConfirmed` no se están inicializando correctamente en el UseCase.

**Falta:**
```java
// En el builder de UserEntity, debería inicializarse:
.emailConfirmed(false)
.mobileNumberConfirmed(false)
```

### 8. **Normalización de Datos**

**Falta normalizar antes de validar:**
- Trim de strings
- Convertir a mayúsculas/minúsculas según reglas
- Eliminar espacios extras
- Normalizar formato de teléfono (solo números)

**Nota:** Ya se hace en `RegisterUserInputDTO.normalize()`, pero debería verificarse que se aplique correctamente.

### 9. **Manejo de Errores Mejorado**

**Problema:** Los errores se capturan de forma muy genérica.

**Falta:**
- Tipos de excepciones específicas
- Logging apropiado
- Clasificación de errores (validación, persistencia, negocio)

**Mejora sugerida:**
```java
try {
    // validaciones
} catch (ValidationException e) {
    resultadoFinal.agregarMensaje(e.getMessage());
} catch (PersistenceException e) {
    resultadoFinal.agregarMensaje("Error al acceder a la base de datos");
    logger.error("Error de persistencia", e);
} catch (Exception e) {
    resultadoFinal.agregarMensaje("Error inesperado durante la validación");
    logger.error("Error inesperado", e);
}
```

### 10. **Retorno de Errores de Validación**

**Problema:** Cuando hay errores, solo retorna `Void.returnVoid()` sin indicar qué errores hubo.

**Falta:**
- Retornar los mensajes de validación al cliente
- Cambiar el tipo de retorno para incluir errores
- O lanzar una excepción con los mensajes

**Opciones:**
1. Cambiar `Void` por `Response<ValidationResultVO>`
2. Lanzar excepción con mensajes: `throw new ValidationException(resultadoFinal)`
3. Usar un `Response` que incluya errores

### 11. **Validaciones de Reglas de Negocio Adicionales**

**Pueden faltar (dependiendo de requisitos):**
- Validar edad mínima/máxima
- Validar que el email no esté en lista negra
- Validar que el teléfono no esté en lista negra
- Validar límite de intentos de registro
- Validar que no haya múltiples registros desde la misma IP en tiempo corto

### 12. **Manejo de Transacciones**

**Falta:**
- `@Transactional` en el UseCase para garantizar atomicidad
- Rollback apropiado en caso de error

**Mejora:**
```java
@Override
@Transactional
public Void execute(final RegisterUserDomain domain) {
    // ... lógica
}
```

## 📋 Checklist de Implementación

### Prioridad Alta (Crítico)
- [ ] Corregir validación de `UsuarioConMismoId`
- [ ] Implementar catálogo de mensajes
- [ ] Agregar notificación al actor
- [ ] Validar campos requeridos
- [ ] Validar formato de email

### Prioridad Media (Importante)
- [ ] Validar formato de teléfono
- [ ] Validar formato de número de identificación
- [ ] Validar integridad referencial (idType, homeCity)
- [ ] Mejorar manejo de errores
- [ ] Retornar errores de validación al cliente

### Prioridad Baja (Mejora)
- [ ] Agregar logging apropiado
- [ ] Normalización de datos más robusta
- [ ] Validaciones adicionales de reglas de negocio
- [ ] Documentación de validaciones

## 🔍 Ejemplo de Implementación Completa

### Validador de Campos Requeridos
```java
public class CamposRequeridosValidator implements Validator<RegisterUserDomain, ValidationResultVO> {
    private CatalogUseCase catalogUseCase;
    
    public ValidationResultVO validate(RegisterUserDomain domain) {
        var resultado = new ValidationResultVO();
        
        if (domain.getIdType() == null) {
            resultado.agregarMensaje(catalogUseCase.getMensaje("error.idType.requerido"));
        }
        // ... más validaciones
        return resultado;
    }
}
```

### Validador de Formato de Email
```java
public class EmailFormatoValidator implements Validator<String, ValidationResultVO> {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    
    public ValidationResultVO validate(String email) {
        var resultado = new ValidationResultVO();
        if (email != null && !email.matches(EMAIL_PATTERN)) {
            resultado.agregarMensaje("El formato del email no es válido");
        }
        return resultado;
    }
}
```

### UseCase Mejorado
```java
@Override
@Transactional
public Void execute(final RegisterUserDomain domain, final String actor) {
    var resultadoFinal = new ValidationResultVO();
    
    // 1. Validar campos requeridos
    resultadoFinal.agregarMensajes(camposRequeridosValidator.validate(domain).getMensajes());
    
    // 2. Validar formatos
    resultadoFinal.agregarMensajes(emailFormatoValidator.validate(domain.getEmail()).getMensajes());
    resultadoFinal.agregarMensajes(telefonoFormatoValidator.validate(domain.getMobileNumber()).getMensajes());
    
    // 3. Validar integridad referencial
    resultadoFinal.agregarMensajes(validarIdTypeExists(domain.getIdType()).getMensajes());
    resultadoFinal.agregarMensajes(validarCityExists(domain.getHomeCity()).getMensajes());
    
    // 4. Validar duplicados
    resultadoFinal.agregarMensajes(validarEmailDuplicado(domain.getEmail()).getMensajes());
    resultadoFinal.agregarMensajes(validarTelefonoDuplicado(domain.getMobileNumber()).getMensajes());
    resultadoFinal.agregarMensajes(validarIdDuplicado(domain.getIdType(), domain.getIdNumber()).getMensajes());
    
    // 5. Si hay errores, lanzar excepción
    if (!resultadoFinal.isValidacionCorrecta()) {
        notificationService.notifyActor(actor, "Error en validación de usuario");
        throw new ValidationException(resultadoFinal);
    }
    
    // 6. Persistir
    UserEntity savedUser = repository.save(userEntity);
    
    // 7. Notificar éxito
    notificationService.notifyActor(actor, "Usuario registrado exitosamente");
    
    return Void.returnVoid();
}
```

