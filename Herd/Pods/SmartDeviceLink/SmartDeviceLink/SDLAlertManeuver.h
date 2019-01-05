//  SDLAlertManeuver.h
//


#import "SDLRPCRequest.h"

@class SDLSoftButton;
@class SDLTTSChunk;


/**
 *  Shows a SDLShowConstantTBT message with an optional voice command. This message is shown as an overlay over the display's base screen.
 *
 *  @since SmartDeviceLink 1.0
 */

NS_ASSUME_NONNULL_BEGIN

@interface SDLAlertManeuver : SDLRPCRequest

- (instancetype)initWithTTS:(nullable NSString *)ttsText softButtons:(nullable NSArray<SDLSoftButton *> *)softButtons;
- (instancetype)initWithTTSChunks:(nullable NSArray<SDLTTSChunk *> *)ttsChunks softButtons:(nullable NSArray<SDLSoftButton *> *)softButtons;

/**
 *  An array of text chunks.
 *
 *  Optional, Array of SDLTTSChunk, Array length 1 - 100
 *
 *  @see SDLTTSChunk
 */
@property (nullable, strong, nonatomic) NSArray<SDLTTSChunk *> *ttsChunks;

/**
 *  An arry of soft buttons. If omitted on supported displays, only the system defined "Close" SoftButton shall be displayed.
 *
 *  Optional, Array of SDLSoftButton, Array length 0 - 3
 *
 *  @see SDLSoftButton
 */
@property (nullable, strong, nonatomic) NSArray<SDLSoftButton *> *softButtons;

@end

NS_ASSUME_NONNULL_END
