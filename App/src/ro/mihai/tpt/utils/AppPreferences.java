/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011  Mihai Balint

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/
package ro.mihai.tpt.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ro.mihai.tpt.R;
import ro.mihai.tpt.analytics.AnalyticsService;
import ro.mihai.tpt.analytics.Collector;
import ro.mihai.tpt.analytics.IAnalyticsService;
import ro.mihai.tpt.analytics.NoAnalyticsService;
import ro.mihai.util.IPrefs;

public class AppPreferences implements IPrefs {

	private Context ctx;
	private Collector collector;
	
	private String pref_device_id;
	private String pref_base_download_url; 
	private String pref_analytics_enabled;
	private String pref_analytics_cache;
	private String pref_current_version;
	
	public AppPreferences(Context ctx) {
		this.ctx = ctx;
		pref_device_id = ctx.getString(R.string.pref_device_id);
		pref_base_download_url = ctx.getString(R.string.pref_base_download_url);
		pref_analytics_enabled = ctx.getString(R.string.pref_analytics_enabled);
		pref_analytics_cache = ctx.getString(R.string.pref_analytics_cache);
		pref_current_version = ctx.getString(R.string.pref_current_version);
		this.collector = new Collector(this, getDeviceId());
	}
	
    private String baseUrl = null;
	public void refreshBaseUrl() {
    	baseUrl = readBaseDownloadUrl(ctx);
	}
	
    public String getBaseUrl() {
    	if(baseUrl==null) 
    		baseUrl = readBaseDownloadUrl(ctx);
		return baseUrl;
	}

	public Collector getAnalyticsCollector() {
		return collector;
	}
	
	private String readBaseDownloadUrl(Context ctx) {
		// Read a sample value they have set
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String defaultUrl = ctx.getString(R.string.pref_base_download_url_default);
		
		return sharedPref.getString(pref_base_download_url, defaultUrl);
	}
	
	public String getDeviceId() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sharedPref.getString(pref_device_id, IPrefs.DEFAULT_DEVICE_ID);
	}
	
	public void setDeviceId(String deviceId) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		sharedPref.edit()
			.putString(pref_device_id, deviceId)
			.commit();
	}
	
	public void setAnalyticsEnabled(boolean enabled) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		sharedPref.edit()
			.putBoolean(pref_analytics_enabled, enabled)
			.commit();
	}
	
	private IAnalyticsService service = null;
	public synchronized IAnalyticsService getAnalyticsService() {
		if (service == null) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
			if (sharedPref.getBoolean(pref_analytics_enabled, true)) {
				String host = ctx.getString(R.string.pref_analytics_host);
				String port = ctx.getString(R.string.pref_analytics_port);
				service = new AnalyticsService(host, Integer.parseInt(port));
			} else {
				service = new NoAnalyticsService();
			}
		}
		return service;
	}
	
	public int getCurrentVersion(int missingVersionCode) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sharedPref.getInt(pref_current_version, missingVersionCode);
	}
	
	public void setCurrentVersion(int versionCode) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		sharedPref.edit()
			.putInt(pref_current_version, versionCode)
			.commit();
	}
	

	public void setCachedAnalytics(String analytics) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		sharedPref.edit()
			.putString(pref_analytics_cache, analytics)
			.commit();
	}

	public String addCachedAnalytics(String analytics) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String cachedAnalytics = sharedPref.getString(pref_analytics_cache, "") + analytics;
		sharedPref.edit()
			.putString(pref_analytics_cache, cachedAnalytics)
			.commit();
		return cachedAnalytics;
	}

}
