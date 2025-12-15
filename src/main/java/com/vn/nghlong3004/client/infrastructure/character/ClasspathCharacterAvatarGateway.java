package com.vn.nghlong3004.client.infrastructure.character;

import com.vn.nghlong3004.client.application.character.CharacterAvatarGateway;
import com.vn.nghlong3004.client.domain.character.CharacterId;
import com.vn.nghlong3004.client.util.ImageUtil;
import java.awt.Image;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;

/**
 * Project: boom-online-client
 *
 * Loads character avatars from classpath resources.
 *
 * @author nghlong3004
 * @since 12/15/2025
 */
public class ClasspathCharacterAvatarGateway implements CharacterAvatarGateway {

  private final Map<String, ImageIcon> cache = new ConcurrentHashMap<>();

  public static ClasspathCharacterAvatarGateway getInstance() {
    return Holder.INSTANCE;
  }

  private ClasspathCharacterAvatarGateway() {}

  @Override
  public ImageIcon getAvatar(CharacterId characterId, int size) {
    String key = characterId.name() + "@" + size;
    ImageIcon cached = cache.get(key);
    if (cached != null) {
      return cached;
    }

    String path = switch (characterId) {
      case BOZ -> "/images/player/boz_avatar.png";
      case EVIE -> "/images/player/evie_avatar.png";
      case IKE -> "/images/player/ike_avatar.png";
      case PLUNK -> "/images/player/plunk_avatar.png";
    };

    Image scaled = ImageUtil.loadImage(path).getScaledInstance(size, size, Image.SCALE_SMOOTH);
    ImageIcon icon = new ImageIcon(scaled);
    cache.put(key, icon);
    return icon;
  }

  private static final class Holder {
    private static final ClasspathCharacterAvatarGateway INSTANCE = new ClasspathCharacterAvatarGateway();
  }
}
