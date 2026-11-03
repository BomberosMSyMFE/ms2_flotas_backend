package bo.gob.bomberos.flota.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataCamionRepository extends JpaRepository<CamionEntity, String> {

    @Query(value = """
        SELECT * FROM unidades_flota
        WHERE ST_DWithin(
            ST_SetSRID(ST_MakePoint(longitud, latitud), 4326)::geography,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
            :radioKms * 1000
        )
        ORDER BY ST_Distance(
            ST_SetSRID(ST_MakePoint(longitud, latitud), 4326)::geography,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
        ) ASC
        """, nativeQuery = true)
    List<CamionEntity> findCamionesCercanos(@Param("lat") double lat,
                                           @Param("lon") double lon,
                                           @Param("radioKms") double radioKms);
}
