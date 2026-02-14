package vn.nghlong3004.boom.online.server.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nghlong3004.boom.online.server.model.ExchangeToken;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/24/2025
 */
@Repository
public interface ExchangeTokenRepository extends JpaRepository<ExchangeToken, Long> {
  Optional<ExchangeToken> findByToken(String token);
}
