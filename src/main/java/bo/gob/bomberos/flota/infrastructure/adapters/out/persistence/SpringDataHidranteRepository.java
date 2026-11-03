package bo.gob.bomberos.flota.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataHidranteRepository extends JpaRepository<HidranteEntity, String> {

    @Query(value = """
        SELECT * FROM hidrantes_red
        WHERE operativo = true
          AND ST_DWithin(
            ST_SetSRID(ST_MakePoint(longitud, latitud), 4326)::geography,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
            :radioMetros
        )
        ORDER BY ST_Distance(
            ST_SetSRID(ST_MakePoint(longitud, latitud), 4326)::geography,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
        ) ASC
        """, nativeQuery = true)
    List<HidranteEntity> findHidrantesCercanos(@Param("lat") double lat,
                                              @Param("lon") double lon,
                                              @Param("radioMetros") double radioMetros);
}
