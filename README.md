AdMob
=====
This is the AdMob module for Godot Engine (https://github.com/okamstudio/godot)
- Android only
- Banner
- Interstitial
- Rewarded Video

How to use
----------
Drop the "admob" directory inside the "modules" directory on the Godot source.

~~Move file GodotAdMob.java from "admob/android/" to "platform/android/java/src/org/godotengine/godot/".~~

Recompile android export template (For documentation: http://docs.godotengine.org/en/latest/reference/compiling_for_android.html#compiling-export-templates).


In Example project goto Export > Target > Android:

	Options:
		Custom Package:
			- point to the location of your custom-built template apks
		Permissions on:
			- Access Network State
			- Internet

Configuring your game
---------------------

To enable the module on Android, add the path to the module to the "modules" property on the [android] section of your engine.cfg file. It should look like this:

	[android]
	modules="org/godotengine/godot/GodotAdMob"

If you have more separate by comma.

API Reference
-------------

The following methods are available:
```python

# Init AdMob
# @param bool is_real Show real ad or test ad
# @param int instance_id The instance id from Godot (get_instance_ID())
init(is_real, instance_id)

# Banner Methods
# --------------

# Load Banner Ads (and show inmediatly)
# @param String id The banner unit id
# @param boolean is_top Show the banner on top or bottom
load_banner(id, is_top)

# Show the banner
show_banner()

# Hide the banner
hide_banner()

# Resize the banner (when orientation change for example)
resize()

# Get the Banner width
# @return int Banner width
get_banner_width()

# Get the Banner height
# @return int Banner height
get_banner_height()

# Callback on ad loaded (Banner)
_on_admob_ad_loaded()

# Callback on ad network error (Banner)
_on_admob_network_error()

# Callback on any error while loading banner
_on_admob_load_banner_error(String reason)
#
# reason can be one of the values:
#  - ERROR_CODE_INTERNAL_ERROR   
#  - ERROR_CODE_INVALID_REQUEST
#  - ERROR_CODE_NO_FILL
#  - Code: X (where X is an error code)

# Interstitial Methods
# --------------------

# Load Interstitial Ads
# @param String id The interstitial unit id
load_interstitial(id)

# Show the interstitial ad
show_interstitial()

# Callback for interstitial ad fail on load
_on_interstitial_not_loaded()

# Callback for interstitial loaded
_on_interstitial_loaded

# Callback for insterstitial ad close action
_on_interstitial_close()

# Rewarded Videos Methods
# -----------------------

# Load rewarded videos ads
# @param String id The rewarded video unit id
load_rewarded_video(id)

# Show the rewarded video ad
show_rewarded_video()

# Callback for rewarded video ad left application
_on_rewarded_video_ad_left_application()

# Callback for rewarded video ad closed 
_on_rewarded_video_ad_closed()

# Callback for rewarded video ad failed to load
# @param int error_code the code of error
_on_rewarded_video_ad_failed_to_load(error_code)

# Callback for rewarded video ad loaded
_on_rewarded_video_ad_loaded()

# Callback for rewarded video ad opened
_on_rewarded_video_ad_opened()

# Callback for rewarded video ad reward user
# @param String currency The reward item description, ex: coin
# @param int amount The reward item amount
_on_rewarded(currency, amount)

# Callback for rewarded video ad started do play
_on_rewarded_video_started()
```

References
-------------
Based on the work of:
* https://github.com/Shin-NiL/godot-admob
* https://github.com/Mavhod/GodotAdmob

License
-------------
MIT license
