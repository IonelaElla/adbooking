package com.generatik.adbooking.adspace.repositories;

import com.generatik.adbooking.adspace.dto.enums.AdSpaceAvailabilityStatus;
import com.generatik.adbooking.adspace.dto.enums.AdSpaceType;
import com.generatik.adbooking.adspace.entities.AdSpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdSpaceRepository extends JpaRepository<AdSpaceEntity, Integer> {

    Optional<AdSpaceEntity> findByUuid(UUID uuid);

    @Query("""
            SELECT adSpaceEntity FROM AdSpaceEntity adSpaceEntity
                WHERE adSpaceEntity.availabilityStatus = :availabilityStatus
                AND (:city IS NULL OR adSpaceEntity.city = :city)
                AND (:type IS NULL OR adSpaceEntity.type = :type)
            """)
    List<AdSpaceEntity> getAdSpacesBy(@Param("availabilityStatus") AdSpaceAvailabilityStatus availabilityStatus, @Param("type") AdSpaceType type, @Param("city") String city);


}
