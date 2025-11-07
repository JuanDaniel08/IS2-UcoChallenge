package co.edu.uco.ucochallenge.secondary.adapters.repository.entity;

import java.util.UUID;

import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.UUIDHelper;
import jakarta.persistence.*;

@Entity
@Table(name = "TipoIdentificacion")
public class IdTypeEntity {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(name = "nombre")
    private String name;

    /**
     * ✅ Constructor vacío requerido por JPA/Hibernate.
     * ❗️Debe permanecer vacío — sin helpers ni inicialización.
     */
    protected IdTypeEntity() {
        // Dejar completamente vacío.
    }

    /**
     * Constructor privado para el patrón Builder.
     */
    private IdTypeEntity(final Builder builder) {
        setId(builder.id);
        setName(builder.name);
    }

    // --- Builder Pattern ---
    public static final class Builder {
        private UUID id;
        private String name;

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public IdTypeEntity build() {
            return new IdTypeEntity(this);
        }
    }

    // --- Getters ---
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // --- Setters ---
    private void setId(final UUID id) {
        this.id = UUIDHelper.getDefault(id);
    }

    private void setName(final String name) {
        this.name = TextHelper.getDefaultWithTrim(name);
    }
}
