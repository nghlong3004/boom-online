package vn.nghlong3004.boom.online.server.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.nghlong3004.boom.online.server.model.Room;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/25/2025
 */
public interface RoomRepository extends JpaRepository<Room, String> {
  @Query("SELECT r FROM Room r JOIN r.slots s WHERE s.userId = :userId AND s.occupied = true")
  Optional<Room> findRoomByUserId(Long userId);
}
