/**
 * -----------------------------------------------------------------------
 *     Copyright  2010 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package jcsoluciones.com.socialfootball;

/**
 * The Class Constants.
 */
/*
 * This class contains all constants variables that require in application.
 */
public class Constants {
	
	/** The Constant App42ApiKey. */
	/*
	 * For getting API_KEY & SECRET_KEY, just login or register to AppHQ Console (http://apphq.shephertz.com/). 
	 */
	 static final String App42ApiKey = "02ca49b5fe497cb629f4334066fcc9cf97a6ef5a282601daa729ea75f4836ab4";  // API_KEY received From AppHQ console, When You create your first app in AppHQ Console.
	 
 	/** The Constant App42ApiSecret. */
 	static final String App42ApiSecret = "d1111d1770adf36609a9f137542065639c4b5c0f1ddec406218440bf6da280d2"; // SECRET_KEY received From AppHQ console, When You create your first app in AppHQ Console.
	 
	 /** The Constant App42DBName. */
 	/*
	  * For creating Database from AppHQ console, just go to (Technical Service Manager -> Storage Service -> click "Add DB".)
	  */
	 static final String App42DBName = "SocialFootball";  // Change it as your requirement. (Note that, only one DataBase can be created through same API_KEY & SECRET_KEY);
	
	 /** The Constant CollectionName. */
 	static final String CollectionName = ""; // Change it as your requirement.
	
	 /** The Constant App42GameName. */
 	/*
	  * For Creating Game, just go to (Business Service Manager -> Game Service -> select Game -> click "Add Game".)
	  */
	 static final String App42GameName = "GameName"; // Change it as your requirement. (You have to create game through AppHQ console.);
	 
	 /** The Constant IntentUserName. */
 	static final String IntentUserName = "intentUserName";
 	/** The Constant App42AndroidPref. */
 	static final String App42AndroidPref="App42AndroidPreferences";
 	/** The Constant UserName. */
 	static final String UserName = "TestUser";
	/** Then Google Project No */
	static  final String GoogleProjectNo="1071669197043";
	/**  OpenShift */
	static  final String HostServer="http://147.120.0.123:3000/";//"http://socialsoccer-solucionesjc.rhcloud.com/";//;
	/** Then Google Client Id*/
	static  final String IdClientGoogle="1071669197043-87276jki2t3ot7bbr0cggdk1ulh7vdlv.apps.googleusercontent.com";
}