#import "RefreshAlbumPlugin.h"
#if __has_include(<refresh_album/refresh_album-Swift.h>)
#import <refresh_album/refresh_album-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "refresh_album-Swift.h"
#endif

@implementation RefreshAlbumPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftRefreshAlbumPlugin registerWithRegistrar:registrar];
}
@end
