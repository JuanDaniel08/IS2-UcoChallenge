package co.edu.uco.ucochallenge.secondary.ports.repository;

import java.util.Optional;
import java.util.UUID;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;

@Repository

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByMobileNumber(String mobileNumber);
    Optional<UserEntity> findByIdTypeAndIdNumber(IdTypeEntity idType, String idNumber);
    @Query("SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(u.secondName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(u.firstSurname) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(u.secondSurname) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<UserEntity> findByNameContaining(@Param("name") String name, Pageable pageable);}

