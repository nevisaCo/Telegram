-keep public class com.google.android.gms.* { public *; }
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keep class org.webrtc.* { *; }
-keep class org.webrtc.audio.* { *; }
-keep class org.webrtc.voiceengine.* { *; }
-keep class org.telegram.messenger.* { *; }
-keep class org.telegram.messenger.camera.* { *; }
-keep class org.telegram.messenger.secretmedia.* { *; }
-keep class org.telegram.messenger.support.* { *; }
-keep class org.telegram.messenger.support.* { *; }
-keep class org.telegram.messenger.time.* { *; }
-keep class org.telegram.messenger.video.* { *; }
-keep class org.telegram.messenger.voip.* { *; }
-keep class org.telegram.SQLite.** { *; }
-keep class org.telegram.tgnet.ConnectionsManager { *; }
-keep class org.telegram.tgnet.NativeByteBuffer { *; }
-keep class org.telegram.tgnet.RequestDelegateInternal { *; }
-keep class org.telegram.tgnet.RequestTimeDelegate { *; }
-keep class org.telegram.tgnet.RequestDelegate { *; }
-keep class org.telegram.tgnet.QuickAckDelegate { *; }
-keep class org.telegram.tgnet.WriteToSocketDelegate { *; }
-keep class com.google.android.exoplayer2.ext.** { *; }
-keep class com.google.android.exoplayer2.util.FlacStreamMetadata { *; }
-keep class com.google.android.exoplayer2.metadata.flac.PictureFrame { *; }
-keep class com.google.android.exoplayer2.decoder.SimpleOutputBuffer { *; }

# https://developers.google.com/ml-kit/known-issues#android_issues
-keep class com.google.mlkit.nl.languageid.internal.LanguageIdentificationJni { *; }

# Constant folding for resource integers may mean that a resource passed to this method appears to be unused. Keep the method to prevent this from happening.
-keep class com.google.android.exoplayer2.upstream.RawResourceDataSource {
  public static android.net.Uri buildRawResourceUri(int);
}

# Methods accessed via reflection in DefaultExtractorsFactory
-dontnote com.google.android.exoplayer2.ext.flac.FlacLibrary
-keepclassmembers class com.google.android.exoplayer2.ext.flac.FlacLibrary {

}

# Some members of this class are being accessed from native methods. Keep them unobfuscated.
-keep class com.google.android.exoplayer2.video.VideoDecoderOutputBuffer {
  *;
}

-dontnote com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioProcessor[]);
}
-dontnote com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioProcessor[]);
}
-dontnote com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioProcessor[]);
}

# Constructors accessed via reflection in DefaultExtractorsFactory
-dontnote com.google.android.exoplayer2.ext.flac.FlacExtractor
-keepclassmembers class com.google.android.exoplayer2.ext.flac.FlacExtractor {
  <init>();
}

# Constructors accessed via reflection in DefaultDownloaderFactory
-dontnote com.google.android.exoplayer2.source.dash.offline.DashDownloader
-keepclassmembers class com.google.android.exoplayer2.source.dash.offline.DashDownloader {
  <init>(android.net.Uri, java.util.List, com.google.android.exoplayer2.offline.DownloaderConstructorHelper);
}
-dontnote com.google.android.exoplayer2.source.hls.offline.HlsDownloader
-keepclassmembers class com.google.android.exoplayer2.source.hls.offline.HlsDownloader {
  <init>(android.net.Uri, java.util.List, com.google.android.exoplayer2.offline.DownloaderConstructorHelper);
}
-dontnote com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader
-keepclassmembers class com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader {
  <init>(android.net.Uri, java.util.List, com.google.android.exoplayer2.offline.DownloaderConstructorHelper);
}

# Constructors accessed via reflection in DownloadHelper
-dontnote com.google.android.exoplayer2.source.dash.DashMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.dash.DashMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}

# Don't warn about checkerframework and Kotlin annotations
-dontwarn org.checkerframework.**
-dontwarn javax.annotation.**

# Use -keep to explicitly keep any other classes shrinking would remove
#-dontoptimize
#-dontobfuscate
-keepclasseswithmembernames class * { native <methods>; }
-keepattributes SourceFile

-keep class org.apache.http.** { *; }
-keep interface org.apache.http.** { *; }
-dontwarn org.apache.http.**

-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

-keep class com.google.** { *; }
-keep interface com.google.** { *; }
-dontwarn com.google.**

-keep class org.checkerframework.** { *; }
-keep class net.hockeyapp.android.** { *; }
-keep class com.googlecode.** { *; }
-keep class com.stripe.** { *; }

-keep class org.telegram.messenger.** { *; }
-keep class org.telegram.PhoneFormat.** { *; }
-keep class org.telegram.SQLite.** { *; }
-keep class org.telegram.tgnet.** { *; }
-dontwarn org.telegram.messenger.**
-dontwarn org.telegram.PhoneFormat.**
-dontwarn org.telegram.SQLite.**
-dontwarn org.telegram.tgnet.**
-keep class org.telegram.ui.ActionBar.** { *; }
-keep class org.telegram.ui.Components.** { *; }
-keep class org.telegram.ui.ProfileActivity.** { *; }
-keep class org.telegram.ui.PhotoViewer.** { *; }
-keepclassmembers class org.telegram.ui.NotificationsSettingsActivity { <fields>; }
-keepclassmembers class org.telegram.ui.PrivacySettingsActivity { <fields>; }
-keepclassmembers class org.telegram.ui.DataSettingsActivity { <fields>; }
-keepclassmembers class org.telegram.ui.ThemeActivity { <fields>; }
-keepclassmembers class org.telegram.ui.CacheControlActivity { <fields>; }
-keepclassmembers class org.telegram.ui.StickersActivity { <fields>; }

#-keepclassmembers class turbogram.SettingsGeneralActivity { <fields>; }
#-keepclassmembers class turbogram.SettingsDialogsActivity { <fields>; }
#-keepclassmembers class turbogram.SettingsMessagesActivity { <fields>; }
#-keepclassmembers class turbogram.SettingsProfileActivity { <fields>; }
#-keepclassmembers class turbogram.SettingsContactsActivity { <fields>; }
#-keepclassmembers class turbogram.StorageSettingsActivity { <fields>; }
#-keepclassmembers class turbogram.ToastSettingsActivity { <fields>; }
#-keepclassmembers class turbogram.BackupRestoreActivity { <fields>; }
#-keepclassmembers class turbogram.ToolbarSettingsActivity { <fields>; }
#-keepclassmembers class turbogram.ChatbarSettingsActivity { <fields>; }
#-keepclassmembers class turbogram.EmojiSettingsActivity { <fields>; }
#-keepclassmembers class turbogram.ForwardSettingsActivity { <fields>; }
#-keep class turbogram.Components.Fam.** { *; }
#-dontwarn turbogram.Components.Fam.**

-keep class com.airbnb.** { *; }
-keep class com.magnetadservices.sdk.** { *; }
-keep class !com.google.android.gms.ads.** { *; }

-keep class de.jurihock.** { *; }
-dontwarn de.jurihock.**
