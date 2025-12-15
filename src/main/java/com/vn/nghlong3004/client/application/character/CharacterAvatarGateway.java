package com.vn.nghlong3004.client.application.character;

import com.vn.nghlong3004.client.domain.character.CharacterId;
import javax.swing.ImageIcon;

/**
 * Project: boom-online-client
 *
 * Application boundary for fetching character avatar assets.
 * UI depends on this interface, not on resource loading.
 *
 * @author nghlong3004
 * @since 12/15/2025
 */
public interface CharacterAvatarGateway {
  ImageIcon getAvatar(CharacterId characterId, int size);
}
