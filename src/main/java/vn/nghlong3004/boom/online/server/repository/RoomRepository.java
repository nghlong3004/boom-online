package vn.nghlong3004.boom.online.server.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nghlong3004.boom.online.server.model.Room;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/25/2025
 */
public interface RoomRepository extends JpaRepository<Room, String> {
  Optional<Room> findFirstBySlotsUsernameAndSlotsOccupiedTrue(String username);
}
