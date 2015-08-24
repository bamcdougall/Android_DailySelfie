---
title       : Daily Selfie - Android Devices
subtitle    : Implementing Alarms & Threads
author      : B.A. McDougall
job         : NSCI Consulting
date        : Sunday, 23 August 2015
output      : html_document
---

## Abstract

This GitHub Repository contains a project for **Programming Mobile Applications for Android Handheld Systems: Part 2** built within *Android Studio*.  This Repository contains 1,270 files in 588 folders.  This CodeBook only delineates the primary java classes for the Android project.

## Critcal Java Classes
There are 8 primary classes in the directory *DailySelfie/app/src/main/java/com/nsci_consulting/www/dailyselfie*

* **MainActivity.java** is the primary file for the Android project that uses two fragments.  This class also initates the menu, the intent for taking pictures, and passes the picture to the MainActivityFragment.java class.
* **MainActivityFragment.java** is the fragment class that handles the picture thumbnail data for the ListAdapter
* **BigPictureFragment.java** is the fragment class that handles display requests for full-sized picture data.
* **AlarmReceiver.java** receives the alarm broadcast receiver that notifies the user to take new self-portraits.
* **CustomAdapter.java** is the class that displays the picture / thumbnail data to the user interface
* **PictureHelper.java** is the class that manages the files and thumbnail generation.
* **PictureRecord.java** is the class for managing file information of the pictures/thumbnails
* **PictureDatabase.java** is a SQLite database for managing the cumulative self-portraits generated withing this app.

The remaining 4 java classes may be extraneous.  Continuous improvement of this project requires elimination of extraneous code and classes.

## Useful External Resource

* [Google Material Icons](https://www.google.com/design/icons/) provides icons for use as drawable resources.
