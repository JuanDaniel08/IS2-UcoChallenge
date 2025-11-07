package co.edu.uco.ucochallenge.secondary.adapters.repository.entity;

import java.util.UUID;

import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.UUIDHelper;
import jakarta.persistence.*;

@Entity
@Table(name = "Ciudad")
public class CityEntity {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento", nullable = false)
    private StateEntity state;

    @Column(name = "nombre")
    private String name;

    /**
     * ✅ Constructor vacío obligatorio para JPA/Hibernate.
     * ❗️Debe permanecer vacío (sin lógica ni helpers).
     */
    protected CityEntity() {
        // No inicializar nada aquí
    }

    /**
     * Constructor privado para el patrón Builder.
     */
    private CityEntity(final Builder builder) {
        setId(builder.id);
        setState(builder.state);
        setName(builder.name);
    }

    // --- Builder Pattern ---
    public static class Builder {
        private UUID id;
        private StateEntity state;
        private String name;

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder state(final StateEntity state) {
            this.state = state;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public CityEntity build() {
            return new CityEntity(this);
        }
    }

    // --- Getters ---
    public UUID getId() {
        return id;
    }

    public StateEntity getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    // --- Setters ---
    private void setId(final UUID id) {
        this.id = UUIDHelper.getDefault(id);
    }

    private void setState(final StateEntity state) {
        this.state = ObjectHelper.getDefault(state, null);
    }

    private void setName(final String name) {
        this.name = TextHelper.getDefaultWithTrim(name);
    }
}
