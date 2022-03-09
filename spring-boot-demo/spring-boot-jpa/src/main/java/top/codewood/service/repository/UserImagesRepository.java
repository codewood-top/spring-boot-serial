package top.codewood.service.repository;

import com.mysql.cj.xdevapi.JsonArray;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.codewood.entity.UserImages;

import java.util.List;

public interface UserImagesRepository extends BaseRepository<UserImages> {

    @Modifying
    @Query("update UserImages ui set ui.images=:images where ui.id=:id")
    int setImages(@Param("id") Long id, @Param("images") String images);


}
