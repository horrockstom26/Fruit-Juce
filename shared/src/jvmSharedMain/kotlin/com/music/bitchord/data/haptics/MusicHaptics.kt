package com.music.bitchord.data.haptics

/**
 * What the motor follows: the whole mix, or only the vocal band.
 *
 * Lives in `shared` because three modules need the same two names — the
 * playback engine that reads the audio, the settings that store the choice,
 * and the player UI that shows it — and two copies of an enum are two
 * vocabularies waiting to drift apart.
 */
enum class MusicHapticsMode(val label: String) {
    FULL_MIX("Full Mix"),
    VOCALS_ONLY("Vocals Only"),
    ;

    companion object {
        fun fromName(value: String?): MusicHapticsMode =
            entries.firstOrNull { it.name == value } ?: FULL_MIX
    }
}

/** How hard the motor answers, as a multiplier on the burst amplitude. */
enum class MusicHapticsIntensity(val label: String, val scale: Float) {
    LIGHT("Light", 0.5f),
    MEDIUM("Medium", 1.0f),
    STRONG("Strong", 1.5f),
    ;

    companion object {
        fun fromName(value: String?): MusicHapticsIntensity =
            entries.firstOrNull { it.name == value } ?: MEDIUM
    }
}
