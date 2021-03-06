package org.godotengine.godot;

import com.google.android.gms.ads.*;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.widget.FrameLayout;
import android.view.ViewGroup.LayoutParams;
import android.provider.Settings;
import android.graphics.Color;
import android.util.Log;
import java.util.Locale;
import android.view.Gravity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class GodotAdMob extends Godot.SingletonBase
{
    private final String TAG = "godo";
    private final String MODULE = "AdMob";
    
	private Activity activity = null; // The main activity of the game
	private int instance_id = 0;

	private InterstitialAd interstitialAd = null; // Interstitial object
	private AdView adView = null; // Banner view

	private boolean isReal = false; // Store if is real or not

	private FrameLayout layout = null; // Store the layout
	private FrameLayout.LayoutParams adParams = null; // Store the layout params

	private RewardedVideoAd rewardedVideoAd = null; // Rewarded Video object

	/* Init
	 * ********************************************************************** */

	/**
	 * Prepare for work with AdMob
	 * @param boolean is_real Tell if the enviroment is for real or test
	 */
	public void init(boolean is_real, int instance_id)
	{
		this.isReal = is_real;
		this.instance_id = instance_id;
		Log.d(TAG, MODULE + ": init");
	}


	/* Rewarded Video
	 * ********************************************************************** */
	private void initRewardedVideo()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				MobileAds.initialize(activity);
				rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
				rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener()
				{
					@Override
					public void onRewardedVideoAdLeftApplication() {
						Log.w(TAG, MODULE + ": onRewardedVideoAdLeftApplication");
						GodotLib.calldeferred(instance_id, "_on_rewarded_video_ad_left_application", new Object[] { });
					}

					@Override
					public void onRewardedVideoAdClosed() {
						Log.w(TAG, MODULE + ": onRewardedVideoAdClosed");
						GodotLib.calldeferred(instance_id, "_on_rewarded_video_ad_closed", new Object[] { });
					}

					@Override
					public void onRewardedVideoAdFailedToLoad(int errorCode) {
						Log.w(TAG, MODULE + ": onRewardedVideoAdFailedToLoad. errorCode: " + errorCode);
						GodotLib.calldeferred(instance_id, "_on_rewarded_video_ad_failed_to_load", new Object[] { errorCode });
					}

					@Override
					public void onRewardedVideoAdLoaded() {
						Log.w(TAG, MODULE + ": onRewardedVideoAdLoaded");
						GodotLib.calldeferred(instance_id, "_on_rewarded_video_ad_loaded", new Object[] { });
					}

					@Override
					public void onRewardedVideoAdOpened() {
						Log.w(TAG, MODULE + ": onRewardedVideoAdOpened");
						GodotLib.calldeferred(instance_id, "_on_rewarded_video_ad_opened", new Object[] { });
					}

					@Override
					public void onRewarded(RewardItem reward) {
						Log.w(TAG, MODULE + ": " + String.format(" onRewarded! currency: %s amount: %d", reward.getType(),
								reward.getAmount()));
						GodotLib.calldeferred(instance_id, "_on_rewarded", new Object[] { reward.getType(), reward.getAmount() });
					}

					@Override
					public void onRewardedVideoStarted() {
						Log.w(TAG, MODULE + ": onRewardedVideoStarted");
						GodotLib.calldeferred(instance_id, "_on_rewarded_video_started", new Object[] { });
					}
				});

			}
		});

	}

	/**
	 * Load a Rewarded Video
	 * @param String id AdMod Rewarded video ID
	 */
	public void load_rewarded_video(final String id) {
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (rewardedVideoAd == null) {
					initRewardedVideo();
				}

				if (!rewardedVideoAd.isLoaded()) {
					rewardedVideoAd.loadAd(id, new AdRequest.Builder().build());
				}
			}
		});
	}

	/**
	 * Show a Rewarded Video
	 */
	public void show_rewarded_video() {
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (rewardedVideoAd.isLoaded()) {
					rewardedVideoAd.show();
				}
			}
		});
	}


	/* Banner
	 * ********************************************************************** */

	/**
	 * Load a banner
	 * @param String id AdMod Banner ID
	 * @param boolean is_on_top To made the banner top or bottom
	 */
	public void load_banner(final String id, final boolean is_on_top)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				layout = ((Godot) activity).layout;
				adParams = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT
				);
				if(is_on_top) adParams.gravity = Gravity.TOP;
				else adParams.gravity = Gravity.BOTTOM;

				adView = new AdView(activity);
				adView.setAdUnitId(id);

				adView.setBackgroundColor(Color.TRANSPARENT);

				adView.setAdSize(AdSize.SMART_BANNER);
				adView.setAdListener(new AdListener()
				{
					@Override
					public void onAdLoaded() {
						Log.w(TAG, MODULE + ": onAdLoaded");
						GodotLib.calldeferred(instance_id, "_on_admob_ad_loaded", new Object[]{ });
					}

					@Override
					public void onAdFailedToLoad(int errorCode)
					{
						String	errorString;
                        boolean networkError = false;
                        
						switch(errorCode) {
							case AdRequest.ERROR_CODE_INTERNAL_ERROR:
								errorString	= "ERROR_CODE_INTERNAL_ERROR";
								break;
							case AdRequest.ERROR_CODE_INVALID_REQUEST:
								errorString	= "ERROR_CODE_INVALID_REQUEST";
								break;
							case AdRequest.ERROR_CODE_NETWORK_ERROR:
								errorString	= "ERROR_CODE_NETWORK_ERROR";
                                networkError = true;
								GodotLib.calldeferred(instance_id, "_on_admob_network_error", new Object[]{ });
								break;
							case AdRequest.ERROR_CODE_NO_FILL:
								errorString	= "ERROR_CODE_NO_FILL";
								break;
							default:
								errorString	= "Code: " + errorCode;
								break;

                            if (networkError) return;
                            
                            GodotLib.calldeferred(instance_id, "_on_admob_load_banner_error", new Object[]{errorString});
						}

                        
                        
						Log.w(TAG, MODULE + ": onAdFailedToLoad -> " + str);
					}
				});
				layout.addView(adView, adParams);

				// Request
				AdRequest.Builder adBuilder = new AdRequest.Builder();
				adBuilder.tagForChildDirectedTreatment(true);
				if (!isReal) {
					adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
					adBuilder.addTestDevice(getAdmobDeviceId());
				}
				adView.loadAd(adBuilder.build());
			}
		});
	}

	/**
	 * Show the banner
	 */
	public void show_banner()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (adView.getVisibility() == View.VISIBLE) return;
				adView.setVisibility(View.VISIBLE);
				adView.resume();
				Log.d(TAG, MODULE + ": Show Banner");
			}
		});
	}

	/**
	 * Resize the banner
	 *
	 */
	public void resize()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				layout.removeView(adView); // Remove the old view

				// Extract params

				int gravity = adParams.gravity;
				FrameLayout	layout = ((Godot)activity).layout;
				adParams = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT
				);
				adParams.gravity = gravity;
				AdListener adListener = adView.getAdListener();
				String id = adView.getAdUnitId();

				// Create new view & set old params
				adView = new AdView(activity);
				adView.setAdUnitId(id);
				adView.setBackgroundColor(Color.TRANSPARENT);
				adView.setAdSize(AdSize.SMART_BANNER);
				adView.setAdListener(adListener);

				// Add to layout and load ad
				layout.addView(adView, adParams);

				// Request
				AdRequest.Builder adBuilder = new AdRequest.Builder();
				adBuilder.tagForChildDirectedTreatment(true);
				if (!isReal) {
					adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
					adBuilder.addTestDevice(getAdmobDeviceId());
				}
				adView.loadAd(adBuilder.build());

				Log.d(TAG, MODULE + ": Banner Resized");
			}
		});
	}

	/**
	 * Hide the banner
	 */
	public void hide_banner()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (adView.getVisibility() == View.GONE) return;
				adView.setVisibility(View.GONE);
				adView.pause();
				Log.d(TAG, MODULE + ": Hide Banner");
			}
		});
	}

	/**
	 * Get the banner width
	 * @return int Banner width
	 */
	public int get_banner_width()
	{
		return AdSize.SMART_BANNER.getWidthInPixels(activity);
	}

	/**
	 * Get the banner height
	 * @return int Banner height
	 */
	public int get_banner_height()
	{
		return AdSize.SMART_BANNER.getHeightInPixels(activity);
	}

	/* Interstitial
	 * ********************************************************************** */

	/**
	 * Load a interstitial
	 * @param String id AdMod Interstitial ID
	 */
	public void load_interstitial(final String id)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				interstitialAd = new InterstitialAd(activity);
				interstitialAd.setAdUnitId(id);
		        interstitialAd.setAdListener(new AdListener()
				{
					@Override
					public void onAdLoaded() {
						Log.w(TAG, MODULE + ": onAdLoaded");
						GodotLib.calldeferred(instance_id, "_on_interstitial_loaded", new Object[] { });
					}

					@Override
					public void onAdClosed() {
						GodotLib.calldeferred(instance_id, "_on_interstitial_close", new Object[] { });

						AdRequest.Builder adBuilder = new AdRequest.Builder();
						adBuilder.tagForChildDirectedTreatment(true);
						if (!isReal) {
							adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
							adBuilder.addTestDevice(getAdmobDeviceId());
						}
						interstitialAd.loadAd(adBuilder.build());

						Log.w(TAG, MODULE + ": onAdClosed");
					}
				});

				AdRequest.Builder adBuilder = new AdRequest.Builder();
				adBuilder.tagForChildDirectedTreatment(true);
				if (!isReal) {
					adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
					adBuilder.addTestDevice(getAdmobDeviceId());
				}

				interstitialAd.loadAd(adBuilder.build());
			}
		});
	}

	/**
	 * Show the interstitial
	 */
	public void show_interstitial()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (interstitialAd.isLoaded()) {
					interstitialAd.show();
				} else {
					Log.w(TAG, MODULE + ": _on_interstitial_not_loaded");
					GodotLib.calldeferred(instance_id, "_on_interstitial_not_loaded", new Object[] { });
				}
			}
		});
	}

	/* Utils
	 * ********************************************************************** */

	/**
	 * Generate MD5 for the deviceID
	 * @param String s The string to generate de MD5
	 * @return String The MD5 generated
	 */
	private String md5(final String s)
	{
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i=0; i<messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2) h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();
		} catch(NoSuchAlgorithmException e) {
			//Logger.logStackTrace(TAG,e);
		}
		return "";
	}

	/**
	 * Get the Device ID for AdMob
	 * @return String Device ID
	 */
	private String getAdmobDeviceId()
	{
		String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = md5(android_id).toUpperCase(Locale.US);
		return deviceId;
	}

	/* Definitions
	 * ********************************************************************** */

	/**
	 * Initilization Singleton
	 * @param Activity The main activity
	 */
 	static public Godot.SingletonBase initialize(Activity activity)
 	{
 		return new GodotAdMob(activity);
 	}

	/**
	 * Constructor
	 * @param Activity Main activity
	 */
	public GodotAdMob(Activity p_activity) {
		registerClass("GodotAdMob", new String[] {
			"init",
			"load_banner", "show_banner", "hide_banner", "get_banner_width", "get_banner_height", "resize",
			"load_interstitial", "show_interstitial", "load_rewarded_video", "show_rewarded_video"
		});
		activity = p_activity;
	}
}
