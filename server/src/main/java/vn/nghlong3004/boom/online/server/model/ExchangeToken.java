package vn.nghlong3004.boom.online.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(name = "exchange_token")
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 255)
  @NotNull
  @Column(name = "token", nullable = false)
  private String token;

  @Size(max = 255)
  @NotNull
  @Column(name = "email", nullable = false)
  private String email;

  @NotNull
  @Column(name = "expiry_time", nullable = false)
  private Instant expiryTime;
}
