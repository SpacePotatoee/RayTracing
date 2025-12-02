package sp.sponge.util;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.logging.Logger;

import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.VK_FALSE;

public class VulkanUtils {
    private static OSType osType;

    public static final int MESSAGE_SEVERITY_BITMASK = VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT;
    public static final int MESSAGE_TYPE_BITMASK = VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT |
            VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;
    private static final String DBG_CALL_BACK_PREF = "VkDebugUtilsCallback, ";

    public static OSType getOS() {
        if (osType == null) {
            String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            osType = OSType.getTypeFromName(os);
        }

        return osType;
    }

    public static VkDebugUtilsMessengerCreateInfoEXT createDebugCallBack(Logger logger) {
        return VkDebugUtilsMessengerCreateInfoEXT.calloc()
                .sType$Default()
                .messageSeverity(MESSAGE_SEVERITY_BITMASK)
                .messageType(MESSAGE_TYPE_BITMASK)
                .pfnUserCallback((messageSeverity, messageTypes, pCallbackData, pUserData) -> {
                    VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);

//                    logger.warning("VkDebugUtilsCallback, " + callbackData.pMessageString());

                    if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT) != 0) {
                        logger.info(DBG_CALL_BACK_PREF + callbackData.pMessageString());
                    } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT) != 0) {
                        logger.warning(DBG_CALL_BACK_PREF + callbackData.pMessageString());
                    } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0) {
                        logger.severe(DBG_CALL_BACK_PREF + callbackData.pMessageString());
                    } else {
                        logger.info(DBG_CALL_BACK_PREF + callbackData.pMessageString());
                    }
                    return VK_FALSE;
                });
    }

    public static void check(int errorCode, String errorString) {
        if (errorCode != VK14.VK_SUCCESS) {
            String type = switch (errorCode) {
                case VK10.VK_NOT_READY                   -> "VK_NOT_READY";
                case VK10.VK_TIMEOUT                     -> "VK_TIMEOUT";
                case VK10.VK_EVENT_SET                   -> "VK_EVENT_SET";
                case VK10.VK_EVENT_RESET                 -> "VK_EVENT_RESET";
                case VK10.VK_INCOMPLETE                  -> "VK_INCOMPLETE";
                case VK10.VK_ERROR_OUT_OF_HOST_MEMORY    -> "VK_ERROR_OUT_OF_HOST_MEMORY";
                case VK10.VK_ERROR_OUT_OF_DEVICE_MEMORY  -> "VK_ERROR_OUT_OF_DEVICE_MEMORY";
                case VK10.VK_ERROR_INITIALIZATION_FAILED -> "VK_ERROR_INITIALIZATION_FAILED";
                case VK10.VK_ERROR_DEVICE_LOST           -> "VK_ERROR_DEVICE_LOST";
                case VK10.VK_ERROR_MEMORY_MAP_FAILED     -> "VK_ERROR_MEMORY_MAP_FAILED";
                case VK10.VK_ERROR_LAYER_NOT_PRESENT     -> "VK_ERROR_LAYER_NOT_PRESENT";
                case VK10.VK_ERROR_EXTENSION_NOT_PRESENT -> "VK_ERROR_EXTENSION_NOT_PRESENT";
                case VK10.VK_ERROR_FEATURE_NOT_PRESENT   -> "VK_ERROR_FEATURE_NOT_PRESENT";
                case VK10.VK_ERROR_INCOMPATIBLE_DRIVER   -> "VK_ERROR_INCOMPATIBLE_DRIVER";
                case VK10.VK_ERROR_TOO_MANY_OBJECTS      -> "VK_ERROR_TOO_MANY_OBJECTS";
                case VK10.VK_ERROR_FORMAT_NOT_SUPPORTED  -> "VK_ERROR_FORMAT_NOT_SUPPORTED";
                case VK10.VK_ERROR_FRAGMENTED_POOL       -> "VK_ERROR_FRAGMENTED_POOL";
                case VK10.VK_ERROR_UNKNOWN               -> "VK_ERROR_UNKNOWN ";
                default -> "Unknown error";
            };

            throw new RuntimeException(errorString + type);
        }
    }

    public enum OSType {
        MACOS("mac", "darwin"),
        WINDOWS("win"),
        LINUX("nux"),
        OTHER;

        final String[] names;

        OSType(String... names) {
            this.names = names;
        }

        public static OSType getTypeFromName(String name) {
            for (OSType type : values()) {
                for (String osName : type.names) {
                    if (name.equals(osName)) {
                        return type;
                    }
                }
            }

            return OTHER;
        }
    }
}
